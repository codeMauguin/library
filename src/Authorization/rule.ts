/** @format */

import config         from "@/config";
import AntPathMatcher from "@howiefh/ant-path-matcher/src";

const pathMatch = new AntPathMatcher();

interface VERIFY_READ {
	/**
	 * 校验路径权限 对请求后的路径进行权限校验
	 * Checking if the user has the right to access the url.
	 * @param url 待请求的路径
	 * @param rule 已有权限
	 * @param isLogin 是否登陆
	 * @throws NO_LOGIN、NO_AUTH
	 */
	verify(url: string, rule: number, isLogin: boolean): boolean;
	
	/**
	 * It returns true if the url is in the white list
	 * @param {string} url - The URL of the request
	 * @returns A boolean value.
	 */
	isWhite(url: string): boolean;
	
	/**
	 * It returns true if the url is in the login array
	 * @param {string} url - The URL to be checked.
	 * @returns A boolean value.
	 */
	isLogin(url: string): boolean;
}

function verify(state: number, verify: number): boolean {
	return (state & verify) === verify;
}

/**
 *  权限验证
 */
class Ruler implements VERIFY_READ {
	public isLogin(url: string): boolean {
		return this.login.some(cur => pathMatch.match(cur, url));
	}
	
	/**
	 * "If the key object has a property with the name of the rule argument, return that property's value,
	 * otherwise return 0."
	 *
	 * The ?? operator is called the nullish coalescing operator. It's a new operator in JavaScript that's
	 * similar to the || operator, but it only returns the right side if the left side is null or
	 * undefined
	 * @param {string} rule - The rule to check for.
	 * @returns The value of the key in the object.
	 */
	public auth(rule: string): number {
		return this.key[rule] ?? 0;
	}
	
	private readonly rules: (string | number)[][] = config.Authorization.rules;
	private readonly login: string[] = config.Authorization.login;
	private readonly key: Record<string, number> = config.Authorization.authKey;
	
	public verify(url: string, rule: number, isLogin: boolean): boolean {
		return this.rules.some(
			(value: (string | number)[]): boolean =>
				pathMatch.match(<string>value[0], url) && verify(<number>value[1], rule)
		);
	}
	
	isWhite(url: string): boolean {
		return config.Authorization.white.some(value => pathMatch.match(value, url));
	}
}

const role = new Ruler();
export default role;
