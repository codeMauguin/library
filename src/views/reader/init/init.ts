/** @format */

/** @format */
/** @format */
import instance, { cacheInstance } from "@/axios";
import type ResponseApi            from "@/axios/ResponseApi";
import type { PageData }           from "@/components/Pages/Page2";
import { useNotification }         from "@/stores/NotificationStore";
import { useReaderStore }          from "@/stores/readerStore";
import type { BookBorrowCard }     from "@/types/Book";
import type Notification           from "@/types/Notification";
import type { Page }               from "@/types/page";
import type ReaderCard             from "@/types/ReaderCard";
import type { AxiosResponse }      from "axios";
import { ElMessage }               from "element-plus";

/**
 * 它返回具有给定 ID 的卡片。
 * @param {string} id - 你要获取的卡的id
 */
export function getBorrowedCard(id: string) {
	instance
		.get(HttpURL.getCard, {params: {id}})
		.then(({data: {data}}: AxiosResponse<ResponseApi<ReaderCard>>) => {
			const readerStore = useReaderStore();
			readerStore.patch(data);
			readerStore.loadBorrowed();
		})
		.catch(data =>
			ElMessage.warning({
				grouping: true,
				message : data
			})
		);
}

/**
 * It returns a promise that resolves to a page of book borrow cards
 * @param status
 * @param {Page} page - Page
 * @returns A promise that resolves to a PageData<BookBorrowCard>
 */
export async function getBorrowedBooks(
	status: string[] | null,
	page: Page
): Promise<PageData<BookBorrowCard>> {
	return cacheInstance
		.post(
			HttpURL.getBorrowed,
			{
				pageInfo: page,
				status
			},
			{
				hit   : true,
				expire: 1000 * 10
			}
		)
		.then(
			({
				 data: {data}
			 }: AxiosResponse<ResponseApi<PageData<BookBorrowCard>>>): PageData<BookBorrowCard> =>
				data
		);
}

//获取用户所有通知
export async function getNotification(id: string): Promise<void> {
	try {
		const {
				  data: {data}
			  }: AxiosResponse<ResponseApi<Notification[]>> = await instance.get("/notification", {
			params: {id: id}
		});
		const notification = useNotification();
		notification.init(data);
	} catch (e) {
		console.log(e);
	}
}
