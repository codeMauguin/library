import type JSEncrypt from "jsencrypt";

let b64map = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
let b64pad = "=";

export function decode(this: JSEncrypt, string: string) {
	const k=this.getKey();
	const MAX_DECRYPT_BLOCK = ((k['n'].bitLength()+7)>>3);
	try {
		let ct = "";
		let t1;
		let bufTmp;
		let hexTmp;
		let str = b64tohex(string);
		let buf = hexToBytes(str);
		let inputLen = buf.length;
		//开始长度
		let offSet = 0;
		//结束长度
		let endOffSet = MAX_DECRYPT_BLOCK;
		
		//分段解密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
				bufTmp = buf.slice(offSet, endOffSet);
				hexTmp = bytesToHex(bufTmp);
				t1 = k.decrypt(hexTmp);
				ct += t1;
				
			} else {
				bufTmp = buf.slice(offSet, inputLen);
				hexTmp = bytesToHex(bufTmp);
				t1 = k.decrypt(hexTmp);
				ct += t1;
				
			}
			offSet += MAX_DECRYPT_BLOCK;
			endOffSet += MAX_DECRYPT_BLOCK;
		}
		return ct;
	} catch (ex) {
		return false;
	}
}


function hex2b64(h: string) {
	let i;
	let c;
	let ret = "";
	for (i = 0; i + 3 <= h.length; i += 3) {
		c = parseInt(h.substring(i, i + 3), 16);
		ret += b64map.charAt(c >> 6) + b64map.charAt(c & 63);
	}
	if (i + 1 == h.length) {
		c = parseInt(h.substring(i, i + 1), 16);
		ret += b64map.charAt(c << 2);
	} else if (i + 2 == h.length) {
		c = parseInt(h.substring(i, i + 2), 16);
		ret += b64map.charAt(c >> 2) + b64map.charAt((c & 3) << 4);
	}
	while ((ret.length & 3) > 0) ret += b64pad;
	return ret;
}

function hexToBytes(hex: string) {
	let bytes = [];
	for (let c = 0; c < hex.length; c += 2) bytes.push(parseInt(hex.slice(c, c + 2), 16));
	return bytes;
}

function bytesToHex(bytes: number[]) {
	let hex = [];
	for (let i = 0; i < bytes.length; i++) {
		hex.push((bytes[i] >>> 4).toString(16));
		hex.push((bytes[i] & 0xF).toString(16));
	}
	return hex.join("");
}

function b64tohex(str: string) {
	let hex = [];
	for (let i = 0, bin = atob(str.replace(/[ \r\n]+$/, "")); i < bin.length; ++i) {
		let tmp = bin.charCodeAt(i)
					 .toString(16);
		if (tmp.length === 1) tmp = "0" + tmp;
		hex[hex.length] = tmp;
	}
	return hex.join("");
}

function addPreZero(num: number, length: number) {
	let t = (num + "").length, s = "";
	for (let i = 0; i < length - t; i++) {
		s += "0";
	}
	
	return s + num;
}