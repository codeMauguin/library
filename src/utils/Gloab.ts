/** @format */

import { useReaderStore }       from "@/stores/readerStore";
import type Book                from "@/types/Book";
import { IfStream }             from "@/utils/IFStream";
import { commitOrder }          from "@/views/order/order";
import { ElMessageBox }         from "element-plus";
import type { EpPropMergeType } from "element-plus/es/utils";
import type { Ref }             from "vue";

export const openShop: Ref<boolean> = ref<boolean>(false);
export const closed = () => (openShop.value = false);
/**
 * 如果用户没有选择任何图书，或者用户没有登录，则什么都不做，否则，向服务器发送借阅所选图书的请求，如果请求成功，则将所选图书从列表中删除购物车，否则显示错误信息。
 * @param {Book[]} books - 书[]
 * @returns 正在返回函数 onsubmit。
 */
export const onsubmit = (books: Book): void => {
	if (Assert.isNull(books)) return;
	const user = useUserInfoStore();
	const reader = useReaderStore();
	IfStream.of(user.state)
			.then(() => {
				return reader.borrowALibraryCard.borrowedBook.some(value => value.localeCompare(books.id) === 0);
			}).catch(() => {
				messageLinkTo("点击登陆", "/login");
				return IfStream.STOP;
			})
			.next()
			.then(() => {
				return ElMessageBox.confirm("已经借阅过，是否继续借阅", "INFO").then(() => {
					return true;
				}).catch(() => {
					return false;
				});
			})
			.catch(() => {
				commitOrder(books.id, new SignalDriver()).then(r => success("借阅成功"));
				return IfStream.STOP;
			})
			.next()
			.then(() => {
				return commitOrder(books.id, new SignalDriver());
			}).next()
			.then(() => {
				success("借阅成功");
			})
			.catch(() => {
				warning("借阅失败");
			});
};

export function tagType(
	key: number
):
	| EpPropMergeType<StringConstructor, "" | "success" | "warning" | "info" | "danger", unknown>
	| undefined {
	switch (key) {
		case 0:
			return "";
		case 1:
			return "success";
		case -1:
			return "danger";
	}
	return "danger";
}

export function tagValue(key: number): string {
	switch (key) {
		case 0:
			return "借阅中";
		case 1:
			return "已归还";
		case -1:
			return "已逾期";
	}
	return "danger";
}