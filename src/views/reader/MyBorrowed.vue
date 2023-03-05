<!-- @format -->

<template>
	<div class="_body">
		<blockquote class="blockquote" style="--block: #73767a;width: 100%">
			<i class="iconfont icon-tishi"/> 图书续签只有一次机会
		</blockquote>
		<div style="flex: 1 1 auto;width: 100%;height: 90%;overflow:auto">
			<el-table
					:cell-style="{ 'text-align': 'center' }"
					:data="pageInstance.view"
					:header-cell-style="{ 'text-align': 'center' }"
					:highlight-current-row="true"
					:row-class-name="queryState"
					border
					table-layout="auto"
					@filter-change="filterChange">
				<template #empty>
					<el-empty
							:description="useUserInfo.state ? '没有借阅信息' : '没有登录'"></el-empty>
				</template>
				<el-table-column
						label="订单编号"
						prop="orderId"
						sortable/>
				<el-table-column
						label="书籍编号"
						prop="bookId"
						sortable/>
				<el-table-column
						label="借阅时间"
						prop="createTime"
						sortable></el-table-column>
				<el-table-column
						label="归还时间"
						prop="returnTime"
						sortable></el-table-column>
				<el-table-column
						:filter-method="filterState"
						:filters="[
						{ text: '借阅中', value: '0' },
						{ text: '已归还', value: '1' },
						{ text: '超时', value: '-1' }
					]"
						:sort-by="sort"
						column-key="status"
						label="状态"
						sortable>
					<template #default="{ row }">
						<el-tag :type="tagType(row.status)" hit size="large">{{ tagValue(row.status) }}
						</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="操作">
					<template #default="{ row }">
						<div style=" display: flex; align-items: center;justify-content: center; gap: 10px">
							<button class="cssbuttons-io-button" @click="() => returnBook(row)"> 还书
								<div class="icon">
									<svg height="24" viewBox="0 0 24 24" width="24" xmlns="http://www.w3.org/2000/svg">
										<path d="M0 0h24v24H0z" fill="none"></path>
										<path d="M16.172 11l-5.364-5.364 1.414-1.414L20 12l-7.778 7.778-1.414-1.414L16.172 13H4v-2z"
											  fill="currentColor"></path>
									</svg>
								</div>
							</button>
							<el-button :disabled="row.renewal>0||row.status===1" class="xq_btn"
									   @click="renewal(row)"> 续期
							</el-button>
						</div>
					</template>
				</el-table-column>
			</el-table>
		</div>
		<div class="footer">
			<el-pagination
					v-model:current-page="pageInstance.pageInfo.currentIndex"
					v-model:page-size="pageInstance.pageInfo.pageSize"
					v-model:total="pageInstance.pageInfo.totalSize"
					:page-sizes="[5, 10,15]"
					background
					layout="total,sizes,prev, pager, next,jumper"
					@current-change="index => pageInstance.next(index)"/>
		</div>
	</div>
</template>

<script lang="ts" setup>
import instance                                                                          from "@/axios";
import HttpApi                                                                           from "@/axios/HttpURL";
import type ResponseApi                                                                  from "@/axios/ResponseApi";
import type {
	LoadPage,
	PageData,
	PageInstance
}                                                                                        from "@/components/Pages/Page2";
import {
	Page
}                                                                                        from "@/components/Pages/Page2";
import { useUserInfoStore }                                                              from "@/stores/counter";
import type { BookBorrowCard, default as Book }                                          from "@/types/Book";
import { tagType, tagValue }                                                             from "@/utils/Gloab";
import {
	getBorrowedBooks
}                                                                                        from "@/views/reader/init/init";
import type { AxiosResponse }                                                            from "axios";
import { ElEmpty, ElMessage, ElMessageBox, ElPagination, ElTable, ElTableColumn, ElTag } from "element-plus";
//@ts-ignore
import zhCn
																						 from "element-plus/dist/locale/zh-cn.mjs";

//Todo 对催促的数据标红
/**
 * 1、查询通知
 * 2、对页面渲染
 */
/**
 * > 对异常的数据标红
 *   - timeout error
 *   - 催促 warning
 * > 该函数接受一个具有两个属性的对象，`row` 和 `rowIndex`，并返回一个字符串
 * @param  - row - 行数据
 * @returns 字符串“警告行”
 */
function queryState({row, rowIndex}: { row: BookBorrowCard; rowIndex: number }): string {
	if (row.status === -1) return "error-row";
	if (row.status === 1) return "success-row";
	const notification = useNotification();
	if (
		notification.anyMatch(
			v =>
				v.title.localeCompare("还书提醒") === 0 &&
				v.message.indexOf(row.id) > -1 &&
				v.status === 0
		)
	)
		return "warning-row";
	return "";
}

const useUserInfo = useUserInfoStore();
const pageInstance: PageInstance<BookBorrowCard> = new Page<BookBorrowCard>(
	new (class implements LoadPage<BookBorrowCard> {
		load(
			offset: number,
			pageSize: number,
			size: number,
			last?: BookBorrowCard
		): Promise<PageData<BookBorrowCard>> {
			// 因为需要优先展示正在借阅的数据，无法按照id排序
			return getBorrowedBooks(null, {
				offset,
				pageSize,
				size,
				id: last?.id
			});
		}
	})()
);
const dataFormatter = (date: Date) => {
	return `${date.getUTCFullYear()}-${
		date.getUTCMonth() + 1
	}-${date.getDate()} ${date.getHours()}:${date.getUTCMinutes()}:${date.getUTCSeconds()}`;
};

function returnBook(book: BookBorrowCard): void {
	ElMessageBox.confirm("还书? Continue?", "返回书籍", {
					confirmButtonText: "OK",
					cancelButtonText : "Cancel",
					type             : "info"
				})
				.then(() => {
					IfStream.of(instance
									.patch(HttpApi.RETURN_BOOK, {
										id: book.id
									})
									.then(({data: {data}}: AxiosResponse<ResponseApi<boolean>>) => data))
							.then(() => {
								pageInstance.updateData((data, size) => {
									ElMessage.success("还书成功");
									const el = data.find(
										predicate => predicate.id.localeCompare(book.id) === 0
									);
									book.status = 1;
									if (Assert.notNull(el)) {
										el!.returnTime = dataFormatter(new Date());
										el!.status = 1;
									}
									return size;
								});
							})
							.catch(() => {
								error("还书失败");
							});
				})
				.catch(() => {
				});
}

function sort(row: Book, index: number): string {
	return String(row.state);
}

function filterState(value: string, row: BookBorrowCard): boolean {
	return row.status.toString() === value;
}

/**
 * 加载所有数据
 * @param column 筛选结果变化
 */
function filterChange(column: Record<string, string[]>) {
	if (column.status.length === 0) {
		pageInstance.virtual();
	} else
		pageInstance.virtual(
			new Page<BookBorrowCard>(
				new (class implements LoadPage<BookBorrowCard> {
					load(
						offset: number,
						pageSize: number,
						size: number,
						last?: BookBorrowCard
					): Promise<PageData<BookBorrowCard>> {
						// 因为需要优先展示正在借阅的数据，无法按照id排序
						return getBorrowedBooks(column.status, {
							offset,
							pageSize,
							size,
							id: last?.id
						});
					}
				})(),
				{},
				true
			)
		);
}

function renewal(row: BookBorrowCard): void {
	ElMessageBox.confirm(`是否对借阅单号${row.id}->续期?`, "图书续期")
				.then(() => {
					IfStream.of(instance.put(HttpApi.RENEWAL_BOOK, row.id)
										.then(({data: {data}}) => ({state: true, value: data, __SELF__: FLAG})))
							.then(({value: time}) => {
								++row.renewal;
								success("续签成功");
								row.returnTime = time;
							})
							.catch((e) => {
								warning(e.data.error);
								++row.renewal;
							});
				});
	
}
</script>

<style scoped>
@import "@/views/css/btn_rtn.css";

@import "@/views/css/btn_xq.css";

._body {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 20px;
    height: 100%;
    width: 100%;
}

.footer {
    flex: 0 0 auto;;
}
</style>
<style>
.warning-row {
    --el-table-tr-bg-color: var(--el-color-warning-light-9);
}

.error-row {
    --el-table-tr-bg-color: rgba(231, 101, 101, 0.2);
}

.success-row {
    --el-table-tr-bg-color: var(--el-color-success-light-9);
}
</style>