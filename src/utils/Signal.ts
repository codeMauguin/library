/** @format */
type signal = {
	[key: string | number]: task[];
};
/* 定义一个称为任务的类型。 */
type task = {
	(...args: any[]): void | ((...args: any[]) => any);
};
class SignalDriver {
	signal: signal = {};
	on(signal: string | number, next: task): void {
		if (this.signal[signal]) {
			this.signal[signal].push(next);
		} else {
			this.signal[signal] = [next];
		}
	}
	emit(signal: string | number, ...args: any[]): void {
		for (const task of this.signal[signal] ?? []) {
			Promise.resolve().then(() => task(args));
		}
	}
}
export type { signal, task };
export { SignalDriver };
