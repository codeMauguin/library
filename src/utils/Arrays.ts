/** @format */
import { isObject } from "@/utils/Assert";

/**
 * 它需要一个目标数组和一个源数组，并将所有值从源数组复制到目标数组
 * @param target - 目标数组
 * @param source - 源数组
 * @param conversion - 将源值转换为目标值的函数。
 */
function merge<T, U = T | unknown>(
	target: Array<T>,
	source: Array<U>,
	conversion: (e: U) => T
): Array<T>;
function merge<T extends object, U extends object>(target: T, source: U): T;
function merge<T, U>(target: T, source: U, conversion: (e: U) => T): T;

function merge<T, U>(target: T, source: U, deep: boolean): T;

/**
 * "Merge the source object into the target object, converting the source object's properties to the target
 * object's
 * properties if a conversion function is provided."
 *
 * The function is generic, so it can be used with any type of object. The first generic type parameter, T, is the
 * type of the target object. The second generic type parameter, U, is the type of the source object. The function
 * returns the target object
 * @param {T} target - The object to merge into.
 * @param {U} source - The source object to merge into the target object.
 * @param [conversion] - A function that converts the source object to the target object.
 * @readonly 两个待合并的对象应该保持类型一致
 * @returns The target object.
 */
function merge<T, U>(target: T, source: U, conversion?: ((e: U) => T) | boolean): T {
	const isObject_1 = isObject(target);
	const isObject_2 = isObject(source);
	if (isObject_1 && isObject_2) {
		const isFunction: boolean = Assert.isFunction(conversion);
		const isDeep: boolean = !isFunction && Assert.isBoolean(conversion) && (conversion as boolean);
		if (Array.isArray(target) && Array.isArray(source)) {
			target.length = 0;
			for (let i = 0; i < source.length; ++i) {
				if (isFunction) {
					target.push(
						(conversion as (...args: any[]) => T)(source[i]) ?? <T>(<unknown>source[i])
					);
				} else {
					target.push(<T>(<unknown>source[i]));
				}
			}
			return target;
		}
		const keys: (string | symbol)[] = Reflect.ownKeys(<object>(<unknown>source));
		for (const key of keys) {
			const value: U = <U>Reflect.get(<object>(<unknown>source), key);
			if (isFunction) {
				Reflect.set(
					<object>(<unknown>target),
					key,
					(conversion as (...args: any[]) => T)(value) ??
					value ??
					Reflect.get(<object>(<unknown>target), key)
				);
			} else if (isDeep) {
				Reflect.set(
					<object>(<unknown>target),
					key,
					merge(Reflect.get(<object>(<unknown>target), key), value, isDeep)
				);
			} else {
				Reflect.set(
					<object>(<unknown>target),
					key,
					value ?? Reflect.get(<object>(<unknown>target), key)
				);
			}
		}
		return target;
	} else {
		if (!isObject_1 && !isObject_2) {
			return Assert.isFunction(conversion)
				? (conversion as (...args: any[]) => T)(source)
				: ((<unknown>source) as T);
		} else {
			//对象转换有歧义
			return Assert.isFunction(conversion)
				? (conversion as (...args: any[]) => T)(source)
				: target;
		}
	}
}

function copyWith<T, R>(target: T, ...source: R[]): T;
function copyWith<T, R>(target: T, source: R): T;
/**
 * 它将源对象的所有属性复制到目标对象，并返回目标对象
 * @param {T} target - 要将属性复制到的目标对象。
 * @param {R} source - 要从中复制的源对象。
 * @returns 带有两个参数（目标和源）并返回目标的函数。
 */
function copyWith<T, R>(target: T, source: R | R[]): T {
	if (!isObject(target) || !isObject(source)) return target;
	if (Array.isArray(source)) {
		return source.reduce(
			(previousValue: T, currentValue: R) => copyWith(previousValue, currentValue),
			target
		);
	}
	const keys: (string | symbol)[] = Reflect.ownKeys((<unknown>source) as object);
	
	for (const propertyKey of keys) {
		Reflect.set(<object>target, propertyKey, Reflect.get(<object>source, propertyKey));
	}
	return target;
}

export { merge, copyWith };
