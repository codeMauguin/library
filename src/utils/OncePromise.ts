/**
 * It returns a function that returns a promise that resolves to the result of the first call to the function it was
 * passed, and then returns the same promise for all subsequent calls
 * @param {Function} requestFunc - The function that will be called.
 * @returns A function that returns a promise.
 */
export default function oncePromise(requestFunc: Function): (...args: unknown[]) => Promise<any> {
	let p: Promise<unknown> | null = null;
	return function (this: unknown, ...args: unknown[]): Promise<unknown> {
		return p !== null ? p : (p = Promise.resolve().then(() => requestFunc.apply(this, args)).finally(
			(): null => p = null));
	};
}