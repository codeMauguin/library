/**
 * 页面管理插件
 * 页面大小可变
 * 预先不可知页面的大小
 *
 * @format
 
 */

import type { VirtualPage }                 from "@/components/Pages/VirtualPage";
import { closeLoading, start as startLoad } from "@/utils/load";
import type { UnwrapNestedRefs }            from "@vue/reactivity";
import { reactive, watch }                  from "vue";
import type { ComputedRef }                 from "vue";

export interface PageInstance<T> {
	view: UnwrapNestedRefs<T[]>;
	pageInfo: UnwrapNestedRefs<PageInfo>;
	
	[x: string]: any;
	
	/**
	 * 下一页
	 * @param index 跳转的页数
	 */
	next(index?: number): Promise<void>;
	
	/**
	 * 前一页
	 */
	prev(): Promise<void>;
	
	/**
	 * 初始化系统参数
	 * 使用创建后刷新
	 * @param options 选项
	 * @deprecated
	 */
	init(options: PageOption): void;
	
	updateData(callback: (value: T[], totalSize: number) => number): void;
	
	/**
	 * “将调用给定函数的结果与当前页面信息合并到当前页面信息中。”
	 *
	 * 该函数是通过一个函数调用的，该函数接受一个页面信息对象并返回一个页面信息对象。使用当前页面信息对象调用该函数。调用函数的结果合并到当前页面信息对象中
	 * @param callable - （页面：PageInfo）=> PageInfo
	 */
	pageUpdate(callable: (page: PageOption) => PageOption): void;
	
	/**
	 * 它刷新当前页面。
	 */
	refresh(...args: any[] | undefined[]): Promise<void>;
	
	virtual(virtualPage?: VirtualPage<T>): void;
}

export type PageInfo = {
	totalSize: number;
	
	pageSize: number;
	
	currentIndex: number;
	
	readonly pages: ComputedRef<number>;
	
	circulate?: boolean;
};

export type PageData<T> = {
	readonly size: number;
	value: T[];
};

export interface LoadPage<T> {
	/* A function that loads data from the server. */
	load(offset: number, pageSize: number, size: number, last?: T): Promise<PageData<T>>; //加载 从offset 开始 pageSize 数据
}

export type PageOption = Partial<Pick<PageInfo, "circulate" | "pageSize" | "totalSize">>;

abstract class AbstractPage<T> implements PageInstance<T> {
	view: UnwrapNestedRefs<T[]> = reactive<T[]>([]);
	/**
	 * 暴露的页面信息
	 */
	pageInfo: UnwrapNestedRefs<PageInfo> = reactive<PageInfo>({
																  currentIndex: 1,
																  pageSize    : 10,
																  totalSize   : -1,
																  circulate   : false,
																  pages       : computed(() => Math.ceil(
																	  this.pageInfo.totalSize / this.pageInfo.pageSize))
															  });
	protected realPageInfo: Required<PageInfo> = {
		currentIndex: 1,
		pageSize    : 10,
		totalSize   : 0,
		circulate   : false,
		pages       : computed(() => Math.ceil(this.realPageInfo.totalSize / this.realPageInfo.pageSize))
	};
	protected data: T[] = [];
	protected pageData: LoadPage<T>;
	protected page: VirtualPage<T> | undefined | null;
	protected status: number = 0;
	
	/**
	 * > The constructor of the `Page` class, which is used to initialize the `pageData` property and the `pageInfo`
	 * property, and then call the `refresh` method to refresh the page
	 * @param pageData - LoadPage<T>
	 * @param {PageOption} [options] - PageOption
	 * @param {boolean} [lazy=false] - Whether to load data immediately after initialization.
	 */
	protected constructor(pageData: LoadPage<T>, options?: PageOption, lazy: boolean = false) {
		this.pageData = pageData;
		if (Assert.notNull(options)) {
			this.pageUpdate(() => <PageOption>options);
		}
		!lazy && this.refresh();
		watch(
			() => this.pageInfo.pageSize,
			(value: number) => {
				if (this.page) {
					this.realPageInfo.pageSize = value;
					this.page.pageUpdate(page => ((page.pageSize = value), page));
				} else {
					this.pageUpdate(page => ((page.pageSize = value), page));
				}
				return this.refresh();
			}
		);
	}
	
	get values(): T[] {
		return this.data;
	}
	
	abstract virtual(virtualPage: PageInstance<T>): void;
	
	/**
	 * 用于导航到下一页的函数。
	 * @param {number} index - 要显示的页面的索引。
	 */
	async next(index: number): Promise<void> {
		const offset: number = (index - 1) * this.pageInfo.pageSize;
		const pageSize: number = this.min(offset);
		await this.preCheck(offset, offset + pageSize)
				  .then((): void => {
					  this.updateView(offset, pageSize);
					  this.pageInfo.currentIndex = index;
					  this.realPageInfo.currentIndex = index;
				  });
	}
	
	/**
	 * 如果当前索引大于0，则将当前索引减1。如果当前索引小于0，则将当前索引设置为总页数
	 * @returns {void}
	 */
	async prev(): Promise<void> {
		let index = this.pageInfo.currentIndex - 1;
		if (index - 1 < 0) {
			if (this.pageInfo.circulate) {
				index = Math.floor(this.pageInfo.totalSize / this.pageInfo.pageSize) + 1;
			} else {
				return;
			}
		}
		return this.next(index);
	}
	
	abstract init(options: PageOption): void;
	
	/**
	 * 函数以回调函数为参数，回调函数以数组为参数，返回一个数字
	 * @param callback - （值：T[]）=> 数字
	 */
	updateData(callback: (value: T[], totalSize: number) => number): void {
		//更新视图
		this.pageInfo.totalSize = callback(this.data, this.pageInfo.totalSize);
		this.refresh()
			.then();
	}
	
	/**
	 * “将调用给定函数的结果与当前页面信息合并到当前页面信息中。”
	 *
	 *
	 * 该函数是通过一个函数调用的，该函数接受一个页面信息对象并返回一个页面信息对象。使用当前页面信息对象调用该函数。调用函数的结果合并到当前页面信息对象中
	 * @param callable - （页面：PageInfo）=> PageInfo
	 */
	pageUpdate(callable: (page: PageOption) => PageOption): void {
		merge(this.realPageInfo, callable(this.realPageInfo));
		merge(this.pageInfo, callable(this.pageInfo));
	}
	
	/**
	 * 它刷新当前页面。
	 */
	async refresh(): Promise<void> {
		return this.next(this.pageInfo.currentIndex);
	}
	
	/**
	 * 从服务器加载数据的函数。
	 * @param {number} start - 待加载数据的起始索引
	 * @param {number} end - 待加载数据的结束索引
	 * @returns 返回值是函数中计算的最后一个表达式的值。
	 */
	protected async preCheck(start: number, end: number): Promise<void> {
		P: {
			for (let i: number = start; i < end; ++i) {
				if (!this.data[i]) {
					break P;
				}
			}
			//更新视图
			return;
		}
		startLoad();
		const pageSize: number =
				  this.pageInfo.totalSize >= 0
					  ? Math.min(this.pageInfo.totalSize, end - start)
					  : end - start;
		try {
			const response: PageData<T> = await this.pageData.load(
				start,
				pageSize,
				this.pageInfo.totalSize,
				start > 0 ? this.data[start - 1] : undefined
			);
			const {size, value} = response;
			if (Assert.notNull(size)) {
				this.pageUpdate((page: PageOption) => ((page.totalSize = size), page));
			}
			if (Assert.isNull(value) || value.length > pageSize) {
				throw new Error(`响应数据不正确:[${JSON.stringify(response)}]`);
			}
			for (let i: number = 0; i < value.length; ++i) {
				if (Assert.isNull(this.data[i + start])) {
					this.data[i + start] = value[i];
					this.status++;
				}
			}
		} catch (e: any) {
			throw e;
		} finally {
			closeLoading();
		}
	}
	
	/**
	 * 计算合适的页面大小
	 * @param {number} offset 当前偏移量
	 * @return {number} 页面的大小
	 * @protected
	 */
	protected min(offset: number): number {
		if (this.pageInfo.totalSize < 0) return this.pageInfo.pageSize;
		return Math.min(this.pageInfo.pageSize, this.pageInfo.totalSize - offset);
	}
	
	/**
	 * > 它获取数据数组并将元素从偏移量复制到偏移量 + pageSize 到视图数组中
	 * @param {number} offset - 要在视图中显示的数据数组中第一项的索引。
	 * @param {number} pageSize - 页面上显示的项目数。
	 */
	protected updateView(offset: number, pageSize: number): void {
		this.view.splice(0, this.view.length, ...(this.data.slice(offset, offset + pageSize) as UnwrapNestedRefs<T[]>));
	}
}

export class Page<T> extends AbstractPage<T> {
	/**
	 * 暴露的页面信息
	 */
	constructor(pageData: LoadPage<T>, options?: PageOption, lazy?: boolean) {
		super(pageData, options, lazy);
	}
	
	//查询是否所有数据加载完毕
	get ready(): boolean {
		return this.status === this.realPageInfo.totalSize;
	}
	
	async next(index?: number | undefined): Promise<void> {
		if (this.page) {
			await this.page.next(index);
			merge(this.view, this.page!.view);
			return;
		}
		let next: number = index ?? this.pageInfo.currentIndex + 1;
		if (
			this.pageInfo.totalSize >= 0 &&
			(next - 1) * this.pageInfo.pageSize >= this.pageInfo.totalSize
		) {
			if (this.pageInfo.circulate || this.pageInfo.totalSize === 0) {
				next = 1;
			} else {
				return;
			}
		}
		
		return super.next(next);
	}
	
	async prev(): Promise<void> {
		if (this.page) {
			await this.page.prev();
			merge(this.view, this.page.view);
			return;
		}
		return super.prev();
	}
	
	init(options: PageOption): void {
		this.status = 0;
		this.pageUpdate(() => options);
		this.refresh()
			.then();
	}
	
	/**
	 * 会加载所有数据
	 * @param page 虚拟页面
	 */
	virtual(page?: PageInstance<T>): void {
		if (Assert.isNull(page)) {
			merge(this.pageInfo, this.realPageInfo); //将页面信息更新
		} else if (Assert.isNull(this.page)) {
			//防止将之前虚拟的页面更新到真实页面
			merge(this.realPageInfo, this.pageInfo); //缓存当前页面信息
			//更新虚拟页面的页面
		}
		page?.pageUpdate(() => ({
			pageSize : this.pageInfo.pageSize,
			circulate: this.pageInfo.circulate
		}));
		this.page = page;
		Promise.resolve(this.refresh())
			   .then();
	}
}