/** @format */

import type { LoadPage, PageInfo, PageInstance, PageOption } from "@/components/Pages/Page2";
import type { VirtualPage }                                  from "@/components/Pages/VirtualPage";
import { closeLoading }                                      from "@/utils";
import { start as loader }                                   from "@/utils";
import type { UnwrapNestedRefs }                             from "vue";
import { reactive }                                          from "vue";

type Item<T> = {
	readonly value: T,
	index: number;
}
export default class LongPage<T> implements PageInstance<Item<T>> {
	public pageInfo: UnwrapNestedRefs<PageInfo> = reactive<PageInfo>({
																		 currentIndex: 1,
																		 pageSize    : 5,
																		 totalSize   : -1,
																		 circulate   : false,
																		 pages       : computed(() => Math.ceil(
																			 this.pageInfo.totalSize / this.pageInfo.pageSize))
																	 });
	public view: UnwrapNestedRefs<Item<T>[]>;
	private data: Item<T>[] = [];
	private page?: VirtualPage<T> | null = null;
	private pageData: LoadPage<T>;
	private realPage: Partial<PageInfo> = {};
	
	constructor(pageData: LoadPage<T>, options?: PageOption) {
		this.pageData = pageData;
		this.view = reactive([]);
		merge(this.realPage, this.pageInfo);
		if (options) {
			this.pageUpdate(() => options);
		}
	}
	
	public async updateToView(start: number, end: number): Promise<void> {
		if (Assert.notNull(this.page)) {
			const update: UnwrapNestedRefs<Item<T>[]> = this.page!.data.slice(start,
																			  end) as UnwrapNestedRefs<Item<T>[]>;
			this.updateIndex(<Item<T>[]>update, start);
			merge(this.view, update);
			return;
		}
		check:{
			for (let i = start; i < end; ++i) {
				if (Assert.isNull(this.data[i])) {
					break check;
				}
			}
			const update: UnwrapNestedRefs<Item<T>[]> = this.data.slice(start, end) as UnwrapNestedRefs<Item<T>[]>;
			this.updateIndex(<Item<T>[]>update, start);
			merge(this.view, update);
			return;
		}
		loader({
				   target: ".content"
			   });
		return this.pageData.load(start, end - start, this.pageInfo.totalSize,
								  <T>this.view[start - 1]).then((response) => {
			if (Assert.notNull(response.size)) {
				this.pageUpdate((page: PageOption) => ((page.totalSize = response.size), page));
			}
			const update: Item<T>[] = (response.value.map((value, index) => ({
				value: value, index: index + start
			})) as Item<T>[]);
			this.data.splice(start, end - start, ...update);
			this.data.forEach((views: Item<T>, index): void => {
				if (Assert.notNull(views))
					views.index = index;
			});
			this.updateIndex(<Item<T>[]>update, start);
			merge(this.view, update);
		}).finally(closeLoading);
		
	}
	
	public async next(index?: number): Promise<void> {
	}
	
	public async prev(): Promise<void> {
	}
	
	public init(options: PageOption): void {
	}
	
	public updateData(callback: (value: Item<T>[], totalSize: number) => number): void {
		this.pageInfo.totalSize = callback(this.view as Item<T>[], this.pageInfo.totalSize);
	}
	
	public pageUpdate(callable: (page: PageOption) => PageOption): void {
		merge(this.pageInfo, callable(this.pageInfo));
		merge(this.realPage, this.pageInfo);
	}
	
	public async refresh(start: number, end: number): Promise<void> {
		if (Assert.notNull(this.page)) {
			await this.page!.refresh();
			merge(this.view, this.page!.view);
			return;
		}
		return await this.updateToView(start, end);
	}
	
	/**
	 * @deprecated
	 * @param {VirtualPage<T>} virtualPage
	 */
	public virtual(virtualPage?: VirtualPage<Item<T>>): void {
		this.page = null;
	}
	
	public virtualTo(virtualPage: VirtualPage<Item<T>>, start: number, end: number): void;
	
	public virtualTo(virtual: null, start: number, end: number): void;
	
	/**
	 * @deprecated
	 * @param {VirtualPage<T>} virtualPage
	 * @param start 更新从
	 * @param end 更新结束
	 */
	public virtualTo(virtualPage: VirtualPage<Item<T>> | null, start?: number, end?: number): void {
		if (Assert.isNull(virtualPage)) {
			this.page = null;
			merge(this.pageInfo, this.realPage);
			this.updateToView(start as number, end as number).then();
		} else {
			merge(this.realPage, this.pageInfo);
			this.page = virtualPage as VirtualPage<T>;
			this.pageInfo.totalSize = this.page!.data.length;
			this.updateToView(start as number, end as number).then();
		}
	}
	
	private updateIndex(target: Item<T>[], start: number): void {
		for (let i = 0; i < target.length; ++i) {
			target[i].index = i + start;
		}
	}
	
}