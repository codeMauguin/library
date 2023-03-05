import { isSymbol }      from "@/utils/Assert";
import { isPromiseLike } from "@/utils/Assert";

export interface NoParamCallback {
	(arg?: any): callbackReturn | resultCallback | Promise<resultCallback>;
}

type callbackReturn = boolean | void | Promise<boolean> | Symbol;
export const FLAG = Symbol.for("resultCallback");

type resultCallback = {
	readonly state: boolean; value: any; __SELF__: symbol;
};

function isObjectReturn(obj: any): obj is resultCallback {
	return Assert.notNull(obj) && obj.__SELF__ === FLAG;
}

type IFStreamConstructorType = (() => boolean) | boolean | Promise<boolean | resultCallback> | resultCallback;

interface PromiseLike {
	__self__: PromiseLike | null;
	
	then(callback: NoParamCallback): PromiseLike;
	
	catch(callback: NoParamCallback): PromiseLike;
}

function exe(self: WaitStream | null): void {
	if (Assert.isNull(self)) return;
	self!.execute()
		 .then(() => exe(self!.__self__));
}

export class IfStream implements PromiseLike {
	public static STOP: Symbol = Symbol("STOP");
	protected static READY: Symbol = Symbol("READY");
	protected static FINISH: Symbol = Symbol("FINISH");
	protected static INITIALIZE: Symbol = Symbol("INITIALIZE");
	__self__: WaitStream | null = null;
	public state: Symbol = IfStream.INITIALIZE;
	public finalResult: ReturnType<NoParamCallback> | null = null;
	protected successCallback: NoParamCallback | null = null;
	protected errorCallback: NoParamCallback | null = null;
	private result: boolean = false;
	
	constructor(predicate: IFStreamConstructorType | null) {
		if (predicate === null) return;
		Promise.resolve((Assert.isBoolean(predicate) || isPromiseLike(predicate) || isObjectReturn(
				   predicate)) ? (predicate as boolean) : (predicate as () => boolean)())
			   .then((r: boolean | resultCallback) => {
				   if (isObjectReturn(predicate)) {
					   const {state, value} = <resultCallback>r;
					   this.result = state;
					   this.finalResult = value;
					   this.state = IfStream.READY;//更新当前状态
					   if (state) {
						   Assert.notNull(this.successCallback) && this.resolve(this.successCallback as NoParamCallback,
																				value);
					   } else {
						   Assert.notNull(this.errorCallback) && this.resolve(this.errorCallback as NoParamCallback,
																			  value);
					   }
				   } else {
					   this.result = <boolean>r;
					   this.state = IfStream.READY;//更新当前状态
					   if (r) {
						   Assert.notNull(this.successCallback) && this.resolve(
							   this.successCallback as NoParamCallback);
					   } else {
						   Assert.notNull(this.errorCallback) && this.resolve(this.errorCallback as NoParamCallback);
					   }
				   }
			   })
			   .catch((e) => {
				   this.finalResult = {
					   state                           : false, value: e, __SELF__: FLAG
				   };
				   this.result = false;
				   this.state = IfStream.READY;//更新当前状态
				   Assert.notNull(this.errorCallback) && this.resolve(this.errorCallback as NoParamCallback, e);
			   });
	}
	
	public static of(predicate: IFStreamConstructorType): IfStream {
		return new IfStream(predicate);
	}
	
	then(callback: NoParamCallback): IfStream {
		if (this.state === IfStream.READY && this.result) {
			//从js模型上这个分支进不来//但是用户没有立即传递then 还是有可能达到这个状态
			this.resolve(callback, this.finalResult);
		} else {
			this.successCallback = callback;
		}
		return this;
	}
	
	catch(callback: NoParamCallback): IfStream {
		if (this.state === IfStream.READY && !this.result) {
			this.resolve(callback, this.finalResult);
		} else {
			this.errorCallback = callback;
		}
		return this;
	}
	
	next(): IfStream {
		if (this.state === IfStream.FINISH) {
			return (isSymbol(this.finalResult) ? new StopStream() : IfStream.of(
				<boolean | resultCallback>this.finalResult));
		}
		return this.__self__ = new WaitStream(this);
	}
	
	private resolve(callback: NoParamCallback, next: any = this.result) {
		Promise.resolve(callback(next))
			   .then(res => {
				   this.finalResult = res;
				   this.state = IfStream.FINISH;
				   exe(this.__self__);
			   })
			   .catch((e) => {
				   this.finalResult = {
					   state                           : false, value             : e, __SELF__: FLAG
				   };
				   this.state = IfStream.FINISH;
				   exe(this.__self__);
			   });
	}
}

class WaitStream extends IfStream implements PromiseLike {
	__self__: WaitStream | null = null;
	
	__prev__: IfStream;
	
	constructor(prev: IfStream) {
		super(null);
		this.__prev__ = prev;
	}
	
	public execute(): Promise<void> {
		if (this.__prev__.state === IfStream.FINISH && !isSymbol(this.__prev__.finalResult)) {
			if (isObjectReturn(this.__prev__.finalResult)) {
				const {value, state} = this.__prev__.finalResult;
				this.__prev__.finalResult = value;
				if (state && Assert.notNull(this.successCallback)) {
					return this.resolves(this.successCallback as NoParamCallback);
				} else if (!state && Assert.notNull(this.errorCallback)) {
					return this.resolves(this.errorCallback as NoParamCallback);
				}
			} else if (Assert.isBoolean(this.__prev__.finalResult)) {
				if (this.__prev__.finalResult && Assert.notNull(this.successCallback)) {
					return this.resolves(this.successCallback as NoParamCallback);
				} else if (!this.__prev__.finalResult && Assert.notNull(this.errorCallback)) {
					return this.resolves(this.errorCallback as NoParamCallback);
				}
			}
		}
		return Promise.resolve();
	}
	
	public then(callback: NoParamCallback): IfStream {
		this.successCallback = callback;
		this.execute()
			.then();
		return this;
	}
	
	public catch(callback: NoParamCallback): IfStream {
		this.errorCallback = callback;
		this.execute()
			.then();
		return this;
	}
	
	public next(): IfStream {
		return this.__self__ = new WaitStream(this);
	}
	
	private resolves(callback: NoParamCallback): Promise<void> {
		return Promise.resolve(callback(this.__prev__.finalResult))
					  .then((r) => {
						  this.finalResult = r;
						  this.state = IfStream.FINISH;
					  })
					  .catch((e) => {
						  this.finalResult = {
							  state                           : false, value             : e, __SELF__: FLAG
						  };
						  this.state = IfStream.FINISH;
					  });
	}
}

class StopStream extends IfStream {
	
	constructor() {
		super(null);
	}
	
	public then(callback: NoParamCallback): IfStream {
		return this;
	}
	
	public catch(callback: NoParamCallback): IfStream {
		return this;
	}
	
	public next(): IfStream {
		return new StopStream();
	}
	
}

export class Stream {
	
	static if(predicate: IFStreamConstructorType): IfStream {
		return IfStream.of(predicate);
	}
	
}