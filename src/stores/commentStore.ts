/** @format */
import instance                    from "@/axios";
import type ResponseApi            from "@/axios/ResponseApi";
import type { LoadPage, PageData } from "@/components/Pages/Page2";
import { Page }                    from "@/components/Pages/Page2";
import type CommentType            from "@/types/CommentType";
import type { returnType }         from "@/types/CommentType";
import type { AxiosResponse }      from "axios";
import { defineStore }             from "pinia";

export const useCommentStore = defineStore("comment", () => {
	//todo 数据更新加载等问题
	const _: Record<string, Page<CommentType>> = reactive<Record<string, Page<CommentType>>>({});
	
	/**
	 * `getPage` 返回一个 `Page` 对象，该对象从服务器加载 `CommentType` 对象
	 * @param {string} bookId - 字符串，用户 ID：字符串 |不明确的
	 * @param {string | undefined} userId - 字符串 |不明确的
	 * @returns 以 PageData 对象作为参数的 Page 对象。
	 */
	function getPage(bookId: string, userId: string | undefined | null): Page<CommentType> {
		return new Page<CommentType>(
			new (class implements LoadPage<CommentType> {
				public async load(
					offset: number,
					pageSize: number,
					size: number,
					last?: CommentType
				): Promise<PageData<CommentType>> {
					return instance
						.post(`${HttpURL.getComments}${bookId}${userId ? "/" + userId : ""}`, {
							offset,
							pageSize,
							size,
							id: last?.id
						})
						.then(
							({
								 data: {data}
							 }: AxiosResponse<
								ResponseApi<PageData<CommentType>>
							>): PageData<CommentType> => {
								data.value = data.value.map((v: unknown) => map(<returnType>v));
								return data;
							})
						.catch(reason => {
							throw reason;
						});
				}
			})(), {}, true
		);
	}
	
	/**
	 * 加载书籍评论的函数。
	 * @param {string} bookId - 细绳，
	 * @param {string | null | undefined} userId - 字符串 |空 |不明确的
	 * @returns 解析为 Page 对象的承诺。
	 */
	function getBookComment(bookId: string, userId?: string | null | undefined): Page<CommentType> {
		/**
		 * 在_中查找数据
		 */
		if (_[bookId]) return _[bookId];
		/**
		 * 在网络中加载数据
		 */
		return (_[bookId] = getPage(bookId, userId));
	}
	
	function updateComment(bookId: string, comments: CommentType[], rootId: string): void;
	function updateComment(bookId: string, comment: CommentType): void;
	/**
	 * `updataComment` 是一个带有两个参数的函数，`bookId` 和 `comment`，并返回 `void`
	 * @param {string} bookId - 字符串，评论：评论类型
	 * @param {CommentType} comment - 评论类型
	 * @param rootId
	 * @returns 返回值是书中的评论数。
	 */
	function updateComment(
		bookId: string,
		comment: CommentType | CommentType[],
		rootId?: string
	): void {
		if (!bookId || bookId.length === 0) return;
		debugger
		const target: Page<CommentType> = _[bookId];
		if (Assert.isNull(target)) {
			console.warn(`未存在${bookId}书籍评论`);
			return;
		}
		if (Array.isArray(comment)) {
			target.updateData((value, size) => {
				const parent: CommentType | undefined = value.find(
					v => v.id.localeCompare(rootId as string) === 0
				);
				if (Assert.isNull(parent)) {
					console.warn(`未找到${bookId}书籍评论`);
				} else {
					parent?.children?.unshift(...comment);
				}
				return size;
			});
		} else {
			if (Assert.isNull(comment.parent) || Assert.isNull(comment.root)) {
				target.updateData((value, size) => {
					value.unshift(comment);
					return size + 1;
				});
			} else {
				target.updateData((value, size) => {
					const parent: CommentType | undefined = value.find(
						v => v.id.localeCompare(comment.root as string) === 0
					);
					if (Assert.isNull(parent)) {
						console.warn(`未找到${bookId}书籍评论`);
					} else {
						parent?.children?.push(comment);
					}
					return size;
				});
			}
		}
	}
	
	/**
	 * 这个函数接受一个 bookId 和一个 id 并且什么都不返回。
	 * @param {string} bookId - 评论所属图书的 ID。
	 * @param {string} id - 要删除的评论的 ID。
	 */
	function deleteComment(bookId: string, id: string): void {
		if (!_[bookId]) return;
		const book = _[bookId];
		
		book.updateData((data: CommentType[], size: number): number => {
			/* 在数组 data 中查找 id 为 id 的评论的索引。 */
			const find: number = data.findIndex(c => c.id.localeCompare(id) === 0);
			if (find === -1) {
				for (const comment of data) {
					const findIndex: number =
							  comment.children?.findIndex(c => c.id.localeCompare(id) === 0) ?? -1;
					if (findIndex > -1) {
						//说明找到了该评论
						comment.children?.splice(findIndex, 1);
						--comment.child;
					}
				}
			} else {
				--size;
				data.splice(find, 1);
				data.length = size;
			}
			return size;
		});
	}
	
	return {
		comments     : computed(() => _),
		updateComment: updateComment,
		deleteComment,
		getBookComment,
		_
	};
});