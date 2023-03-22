/** @format */

export class Assert {
	/**
	 * 如果目标为 null 或未定义，则返回 true，否则返回 false。
	 * @param {any} target - 任何
	 * @returns 如果目标为空或未定义，则返回目标。
	 */
	public static isNull(target: any): boolean {
		return target === null || target === (void 0);
	}
	
	public static isEmpty(target: any): boolean {
		return Assert.isNull(target) || ("length" in target && target.length === 0);
	}
	
	public static clear(target: any): any {
		if (!isObject(target)) {
			if (Assert.isString(target)) return "";
			if (Assert.isNumber(target)) return 0;
			return target === true ? false : target;
		}
		if (Array.isArray(target)) {
			target.splice(0, target.length);
			return target;
		}
		for (const key of Object.keys(target)) {
			const val = Reflect.get(target, key);
			Reflect.set(target, key, Assert.clear(val));
		}
		return target;
	}
	
	/**
	 * 如果目标为 null，则返回 false，否则返回 true。
	 * @param {any} target - unknown - 检查它是否为 null 的目标。
	 * @returns 一个布尔值。
	 */
	public static notNull(target: any): boolean {
		return !Assert.isNull(target);
	}
	
	/**
	 * > Returns true if the target string is not null and contains at least one non-whitespace character
	 * @param {string} target - The target string to check.
	 * @returns A boolean value.
	 */
	public static hasText(target?: string | null): boolean {
		return Assert.notNull(target) && /\S/.test(target as string);
	}
	
	public static isBoolean(target: any): boolean {
		return typeof target === "boolean";
	}
	
	public static isFunction(target: any): boolean {
		return (Assert.notNull(target) && (typeof target === "function" || target instanceof Function));
	}
	
	/**
	 * 如果目标字符串是正整数，则返回 true，否则返回 false
	 * @param {string} target - 要测试的字符串。
	 * @returns 一个布尔值。
	 */
	public static isNumber(target: string | number | any): boolean {
		return ((Assert.isString(target) && /^([1-9])[0-9]*$/.test(target as string)) || Assert.notNull(
			target) && typeof target === "number");
	}
	
	/**
	 * 如果目标不为 null 且长度大于零，则返回 true。
	 * @param {string} target - 要检查的字符串。
	 * @returns 一个布尔值。
	 */
	public static hasLength(target: string | null): boolean {
		return Assert.isString(target) && (target as string).length > 0;
	}
	
	public static isString(target: unknown): boolean {
		return Assert.notNull(target) && typeof target === "string";
	}
	
}

/**
 * “throttle() 返回一个函数，该函数仅每 `delay` 毫秒执行一次。”
 *
 * `throttle()` 函数有两个参数：
 * 在执行期间有多次调用则会继续尾调用一次，保证节流的函数在delay只运行1-2次 在滚动条滚动中保证停止后调用
 *
 * 1. `fn`：节流的函数。
 * 2. `delay`：两次执行之间等待的毫秒数
 *
 * //如果多次点击我保证最后一次点击会继续更新一次，防止之前一次中确实有一次实际更新没有被加载
 * @param {Function} fn - 要节流的函数。
 * @param {number} [delay=1000] - 执行函数之前等待的时间（以毫秒为单位）。
 * @param max 超过这么多次后会继续执行
 */
export function throttle<R = any | null>(fn: (...args: any[]) => any, delay: number = 1000,
										 max: number                                = 10): (...args: unknown[]) => R {
	let timer: unknown | null = null;
	let count = 0;
	max = Math.max(max, 1);//防止max被设置负数导致死循环
	
	function clear(this: any, ...args: any[]): void {
		
		if (count > max) {
			Promise.resolve()
				   .then(() => {
					   count = 0;
					   return fn.apply(this, args);
				   })
				   .then(() => {
					   clear.apply(this, args);
				   });
		} else {
			timer = null;
			count = 0;
		}
	}
	
	
	return function (this: any, ...args: any[]): R {
		if (timer != null || count > 0) {
			++count;//记录当前阻塞中有多少次请求进来
			return <R>(<unknown>null);
		}
		
		timer = setTimeout(() => {
			clear.apply(this, args);
		}, delay);
		return fn.apply(this, args);//进来优先执行
	};
}

/**
 * 如果定时器不为null，则返回null，否则延时后将定时器设置为null，并返回函数的结果。
 * @param {(...args: any[]) => never} fn - 节流的功能。
 * @param {number} [delay=1000] - 调用函数之前等待的时间。
 * @returns 将在延迟后调用传入函数的函数。
 */
export function antiShake(fn: (...args: any[]) => any, delay: number = 1000): (...args: unknown[]) => void {
	let timer: string | number | null = null;
	return function (this: any, ...args: any[]): void {
		if (Assert.notNull(timer)) {
			clearTimeout((timer as number | undefined));
		}
		timer = setTimeout(() => {
			fn.apply(this, args);
			timer = null;
		}, delay);
	};
}

export function isPromiseLike<T>(obj: any): obj is PromiseLike<T> {
	return obj && (obj instanceof Promise || typeof obj.then === "function");
}

/**
 * 如果值是符号，则返回 true，否则返回 false。
 * @param {any} finalResult - 任何
 */
export function isSymbol(finalResult: any): finalResult is symbol {
	return typeof finalResult === "symbol";
}

export const isObject = (target: unknown): boolean => Assert.notNull(
	target) && (typeof target === "object" || target instanceof Object);