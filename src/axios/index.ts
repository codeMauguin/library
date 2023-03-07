/** @format */

import HttpApi                                                                               from "@/axios/HttpURL";
import type ResponseApi                                                                      from "@/axios/ResponseApi";
import { publicKey }                                                                         from "@/config";
import { useBookStore }                                                                      from "@/stores/BooksStore";
import { useUserInfoStore }                                                                  from "@/stores/counter";
import {
	useReaderStore
}                                                                                            from "@/stores/readerStore";
import { error }                                                                             from "@/utils/Alert";
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from "axios";
import axios                                                                                 from "axios";
import CacheAxios                                                                            from "axios-cache-data";
import CryptoJS                                                                              from "crypto-js";
import JSEncrypt                                                                             from "jsencrypt";
import {
	hex2b64
}                                                                                            from "jsencrypt/lib/lib/jsbn/base64";

function encryptUnicodeLong(val: string, encrypt: JSEncrypt) {
	let k = encrypt.getKey();
	//根据key所能编码的最大长度来定分段长度。key size - 11：11字节随机padding使每次加密结果都不同。
	let maxLength = ((k["n"].bitLength() + 7) >> 3) - 11;
	try {
		let subStr = "", encryptedString = "";
		let subStart = 0, subEnd = 0;
		let bitLen = 0, tmpPoint = 0;
		const len = val.length;
		for (let i = 0; i < len; i++) {
			//js 是使用 Unicode 编码的，每个字符所占用的字节数不同
			let charCode = val.charCodeAt(i);
			if (charCode <= 0x007f) {
				bitLen += 1;
			} else if (charCode <= 0x07ff) {
				bitLen += 2;
			} else if (charCode <= 0xffff) {
				bitLen += 3;
			} else {
				bitLen += 4;
			}
			//字节数到达上限，获取子字符串加密并追加到总字符串后。更新下一个字符串起始位置及字节计算。
			if (bitLen > maxLength) {
				subStr = val.substring(subStart, subEnd);
				encryptedString += k.encrypt(subStr);
				subStart = subEnd;
				bitLen = bitLen - tmpPoint;
			} else {
				subEnd = i;
				tmpPoint = bitLen;
			}
		}
		subStr = val.substring(subStart, len);
		encryptedString += k.encrypt(subStr);
		return hex2b64(encryptedString);
	} catch (ex) {
		return false;
	}
}

const crypto = new JSEncrypt({default_key_size: "2048"});
crypto.setPublicKey(publicKey);
const config: AxiosRequestConfig = {
	baseURL: HttpApi.BASEURL,
	timeout: 10000
};

const instance: AxiosInstance = axios.create(config);
declare module "axios" {
	interface InternalAxiosRequestConfig {
		key: string;
		lb: boolean;
	}
}

const cacheInstance: CacheAxios = CacheAxios.create({
														adapter    : con => instance.request(con),
														enableCache: true,
														maxAge     : (1000 * 60) << 1,
														valid(response: AxiosResponse): boolean {
															return response.status === 200 && isResponseOk(
																response.data.code);
														}
													});
instance.interceptors.request.use(value => {
	value.lb = value.lb ?? true;
	return Assert.isNull(value.data) || !value.lb ? setKey(value) : encrypt(value);
	
});

function setKey(value: InternalAxiosRequestConfig): InternalAxiosRequestConfig {
	value.key = CryptoJS.enc.Base64.stringify(CryptoJS.lib.WordArray.random(32)).slice(0, 16);
	value.headers.RESPONSE_KEY = encryptUnicodeLong(value.key, crypto);
	return value;
}

function encrypt(value: InternalAxiosRequestConfig) {
	value.key = CryptoJS.enc.Base64.stringify(CryptoJS.lib.WordArray.random(32)).slice(0, 16);
	const stringify: string = JSON.stringify({
												 payload: JSON.stringify(value.data),
												 value  : value.key
											 });
	value.headers.setContentType("application/json");
	value.data = encryptUnicodeLong(stringify, crypto);
	return value;
}

function decrypt(key: string, value: AxiosResponse<any, any>): any {
	try {
		const k = CryptoJS.enc.Utf8.parse(key);
		console.log(key);
		value.data = JSON.parse(
			CryptoJS.AES.decrypt(value.data, k, {mode: CryptoJS.mode.ECB, padding: CryptoJS.pad.Pkcs7}).toString(
				CryptoJS.enc.Utf8));
	} catch (e) {
		console.log(e);
	}
	return value;
}

instance.interceptors.response.use(value => {
	const {key, lb} = value.config;
	return lb ? decrypt(key, value) : value;
});


/**
 * 身份验签
 */
instance.interceptors.request.use(
	(target: InternalAxiosRequestConfig): InternalAxiosRequestConfig => {
		if ((target.url as string).localeCompare("login") === 0) return target;
		const store = useUserInfoStore();
		target.headers.lb_2 = store.token;
		return target;
	}
);
instance.interceptors.response.use(
	(
		value: AxiosResponse<ResponseApi<unknown>>
	): AxiosResponse<unknown> | Promise<AxiosResponse<unknown>> => {
		const store = useUserInfoStore();
		//网络请求正常，且符合系统正常响应
		if (isResponseOk(value.data.code)) return value;
		if (value.data.code === 2003) {
			store.$reset();
			useReaderStore()
				.$reset();
			useBookStore()
				.$reset();
			error("没有权限");
		}
		return Promise.reject(value);
	},
	err => {
		const store = useUserInfoStore();
		if (!store.state) {
			messageLinkTo("请先登陆", "/login");
			return Promise.reject(err);
		}
		if (err?.data?.error)
			error({
					  message: err.data.error
				  });
		return Promise.reject(err);
	}
);
instance.interceptors.response.use((value: AxiosResponse<ResponseApi<unknown>>) => {
	const token: string | undefined = value.headers.bl;
	const store = useUserInfoStore();
	store.refreshToken(token);
	return value;
});


/**
 * 此函数接受一个数字并返回一个布尔值。
 * @param {number} code - 响应代码。
 * @returns 一个接受数字并返回布尔值的函数。
 */
function isResponseOk(code: number): boolean {
	return code === 1000;
}

export default instance;
export { cacheInstance, isResponseOk };