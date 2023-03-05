/** @format */

import type { UnwrapNestedRefs } from "vue";
import type { PageInfo } from "./Page2";
import type { PageOption } from "./Page2";
import type { PageInstance } from "./Page2";

export type VirtualPage<T> = PageInstance<T>

/**
 * 本地化页面，数据不需要加载
 */
export class LocalVirtualPage<T> implements VirtualPage<T> {
	[x: string]: any;

	public pageInfo: UnwrapNestedRefs<PageInfo>;
	public view: UnwrapNestedRefs<T[]> = [];
	current = 1;
	data: T[];

	constructor(data: T[], pageInfo: UnwrapNestedRefs<PageInfo>) {
		this.data = data;
		this.pageInfo = pageInfo;
	}

	public init(options: PageOption): void {}

	public next(index?: number): Promise<void> {
		let next: number = Assert.isNull(index) ? this.current + 1 : <number>index;
		if ((next - 1) * this.pageInfo.pageSize >= this.data.length) {
			if (this.pageInfo.circulate) {
				next = 1;
			} else {
				return Promise.resolve();
			}
		}
		merge(
			this.view,
			this.data.slice(
				(next - 1) * this.pageInfo.pageSize,
				Math.min(next * this.pageInfo.pageSize, this.data.length)
			)
		);
		this.pageInfo.currentIndex = this.current = next;
		return Promise.resolve();
	}

	public pageUpdate(callable: (page: PageOption) => PageOption): void {
		merge(this.pageInfo, callable(this.pageInfo));
		this.pageInfo.currentIndex = this.current;
		this.pageInfo.totalSize = this.data.length;
	}

	public prev(): Promise<void> {
		let prev = this.current - 1;
		if (prev < 0) {
			if (this.pageInfo.circulate) {
				prev = Math.floor(this.data.length / this.pageInfo.pageSize);
			} else {
				return Promise.resolve();
			}
		}
		merge(
			this.view,
			this.data.slice(
				(prev - 1) * this.pageInfo.pageSize,
				Math.min(prev * this.pageInfo.pageSize, this.data.length)
			)
		);
		this.pageInfo.currentIndex = this.current = prev;
		return Promise.resolve();
	}

	public refresh(): Promise<void> {
		return this.next(this.current);
	}

	public updateData(callback: (value: T[], totalSize: number) => number): void {}

	public virtual(virtualPage?: PageInstance<T>): void {}
}
