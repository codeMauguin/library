import { Assert } from "@/utils/Assert";

function buildContext(keys: (string | symbol)[], context: any, el: string) {
	const ast: string[] = [];
	ast.push("return function(){");
	for (let i: number = 0; i < keys.length; ++i) {
		ast.push(`const ${keys[i] as string} = Reflect.get(this, '${keys[i] as string}');`);
	}
	ast.push(`return eval(${el});}`);
	return ast.join("");
}


/**
 * 它接受一个字符串和一个对象，并返回一个函数，该函数使用对象的属性作为变量来评估字符串
 * @param {string} el - string - 要评估的字符串。
 * @param {any} context - 将用于计算表达式的上下文对象。
 * @returns 从模板字符串创建的函数。
 */
export function eva(el: string, context: any): any | null {
	try {
		const template: string = buildContext(Reflect.ownKeys(context), context, el);
		return new Function(template);
	} catch (e) {
		return null;
	}
}

/**
 * "执行一个函数并返回结果，或者如果结果是一个函数，则执行它并返回结果，或者如果结果是一个函数，则执行它并返回结果，或者如果结果是一个函数，则执行它并返回结果返回结果，或者如果结果是一个函数，则执行它并返回结果，或者如果结果是一个函数，则执行它并返回结果，或者如果结果是一个函数，则执行它并返回结果，或者如果结果是一个函数，则执行它并返回结果，或者如果结果是一个函数，则执行它并返回结果，或者如果结果是一个函数，则执行它并返回结果，或者如果结果是一个函数，执行它并返回结果，或者如果结果是一个函数，则执行它并返回结果，或者如果结果是一个函数，则执行它并返回结果，或者如果结果是一个函数，则执行它
 * @param {Function} fn - 要执行的函数。
 * @param {any} context - 执行函数的上下文。
 * @param {any[]} [args] - 传递给函数的参数。
 */
export function executor(fn: Function, context: any, args?: any[]): any | null {
	try {
		return Assert.isFunction(fn) ? executor(fn.apply(context, args), context, args) : fn;
	} catch (e: any) {
		return null;
	}
}

const funReg: RegExp = /^(?<fn1>\((?<arg1>[\w,]*)\)=>.*)|(?<fn2>function([\s\w]*)\((?<arg2>[\w,]*)\)\s*\{.*})|((?<fn3>\w+)=>)/i;

/**
 * 它接受一个字符串并返回一个具有两个属性 args 和 fn 的对象。 args 属性是一个字符串数组，fn 属性是一个函数
 * @param {string} el - 要解析的字符串
 * @param {any} context - 函数的上下文，也就是函数的上下文。
 * @returns 一个接受字符串并返回具有两个属性的对象的函数：args 和 fn。
 */
export function parseFun(el: string,context:any): { args: string[], fn: Function } {
	const regExpExecArray: RegExpExecArray | null = funReg.exec(el);
	if (Assert.isNull(regExpExecArray) || Assert.isNull(regExpExecArray?.groups)) return {
		args: [],
		fn  : eva(el,context)
	};
	if (Assert.notNull(regExpExecArray?.groups?.fn1)) {
		return {
			args: regExpExecArray!.groups!.arg1.split(","),
			fn  : eva(el,context)
		};
	}
	if (Assert.notNull(regExpExecArray?.groups?.fn2))
		return {
			args: regExpExecArray!.groups!.arg2.split(","),
			fn  :eva(el,context)
		};
	return {
		args: [regExpExecArray!.groups!.arg3],
		fn  : eva(el,context)
	};
}

/**
 * 它接受一个字符串数组和一个上下文对象，并从上下文对象返回一个值数组
 * @param {string[]} args - 要解析的参数。
 * @param {any} context - 函数的上下文。
 * @returns 参数数组。
 */
export function parseArgs(args: string[], context: any): any[] {
	if (Assert.isEmpty(args)) return [];
	const argument = [];
	for (let i = 0; i < args.length; ++i) {
		argument[i] = Reflect.get(context, args[i]);
	}
	return argument;
}


/**
 * > 它接受一个字符串和一个上下文，将字符串解析为函数和参数，然后使用参数执行函数
 * @param {string} el - string - 要解析的字符串
 * @param {unknown} context - 函数的上下文。
 * @returns 函数调用的结果。
 */
export function runner(el: string, context: unknown): any {
	const parse: { args: string[]; fn: Function } = parseFun(el,context);
	const args: any[] = parseArgs(parse.args, context);
	return executor(parse.fn,  context,args);
}