/** @format */

import type { PageData }         from "@/components/Pages/Page2";
import type { LoadPage }         from "@/components/Pages/Page2";
import type { PageOption }       from "@/components/Pages/Page2";
import type { PageInfo }         from "@/components/Pages/Page2";
import type { PageInstance }     from "@/components/Pages/Page2";
import type { VirtualPage }      from "@/components/Pages/VirtualPage";
import { reactive }              from "vue";
import type { UnwrapNestedRefs } from "vue";

export default class LongPage<T> implements PageInstance<T> {
	public pageInfo: UnwrapNestedRefs<PageInfo> = reactive<PageInfo>({
																		 currentIndex: 1,
																		 pageSize    : 5,
																		 totalSize   : -1,
																		 circulate   : false,
																		 pages       : computed(() => Math.ceil(
																			 this.pageInfo.totalSize / this.pageInfo.pageSize))
																	 });
	public view: UnwrapNestedRefs<T[]>;
	private page?: VirtualPage<T> | null = null;
	private pageData: LoadPage<T>;
	
	constructor(pageData: LoadPage<T>, options?: PageOption) {
		this.pageData = pageData;
		this.view = reactive([]);
		if (options) {
			this.pageUpdate(() => options);
		}
	}
	
	public init(options: PageOption): void {
		this.pageUpdate(() => options);
		this.refresh()
			.then();
	}
	
	public async next(index?: number): Promise<void> {
		if (Assert.notNull(this.page)) {
			return this.page?.next(index)
					   .then((): void => {
						   merge(this.view, this.page!.view);
					   });
		}
		const offset: number = (this.pageInfo.currentIndex - 1) * this.pageInfo.pageSize;
		if (this.pageInfo.totalSize >= 0 && offset >= this.pageInfo.totalSize) return;
		const page: number =
				  this.pageInfo.totalSize >= 0
					  ? Math.min(this.pageInfo.totalSize - offset, this.pageInfo.pageSize)
					  : this.pageInfo.pageSize;
		try {
			start();
			const {size, value}: PageData<T> = await this.pageData.load(
				offset,
				page,
				this.pageInfo.totalSize,
				<T>this.view[offset - 1]
			);
			if (Assert.notNull(size)) {
				this.pageUpdate((page: PageOption) => ((page.totalSize = size), page));
			}
			(this.view as T[]).push(...value);
			++this.pageInfo.currentIndex;
		} catch (e) {
		} finally {
			closeLoading();
		}
	}
	
	public pageUpdate(callable: (page: PageOption) => PageOption): void {
		merge(this.pageInfo, callable(this.pageInfo));
	}
	
	public async prev(): Promise<void> {
	}
	
	public async refresh(): Promise<void> {
		if (Assert.notNull(this.page)) {
			await this.page!.refresh();
			merge(this.view, this.page!.view);
			return;
		}
		await this.next();
	}
	
	public updateData(callback: (value: T[], totalSize: number) => number): void {
		this.pageInfo.totalSize = callback(this.view as T[], this.pageInfo.totalSize);
	}
	
	public virtual(virtualPage?: VirtualPage<T>): void {
		this.page = virtualPage;
		Promise.resolve(this.refresh())
			   .then();
	}
}