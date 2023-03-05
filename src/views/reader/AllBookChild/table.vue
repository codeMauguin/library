<!-- @format -->
<!--todo 页面统一存储数据-->
<script lang="ts" setup>
import type { PageData }                from "@/components/Pages/Page2";
import { LoadPage, Page, PageInstance } from "@/components/Pages/Page2";
import router                           from "@/router";
import { useShopStore }                 from "@/stores/ShopStore";
import type Book                        from "@/types/Book";
import { ElTable }                      from "element-plus";
import type { Ref }                     from "vue";

const shoppingCart = useShopStore();
const el: Ref<InstanceType<typeof ElTable> | null> = ref<InstanceType<typeof ElTable> | null>(
	null
);
const pageInstance: PageInstance<Book> = new Page<Book>(
	new (class implements LoadPage<Book> {
		public async load(
			offset: number,
			pageSize: number,
			size: number,
			last?: Book
		): Promise<PageData<Book>> {
			const {
					  data: {data: data_1}
				  } = await cacheInstance.post(HttpURL.getAllBooks, {
				pageInfo: {
					offset,
					pageSize,
					size,
					id: last?.id
				}
			});
			return data_1;
		}
	})(),
	{
		pageSize: 10
	}
);
const formatter = (row: Book): string => {
	const time = new Date(row.time);
	return `${time.getFullYear()}-${time.getMonth() + 1}-${time.getDate()}`;
};
watch(pageInstance.view, () => {
	pageInstance.view.forEach((book: Book): void => {
		if (!!shoppingCart._[book.id]) {
			el.value?.toggleRowSelection(book, true);
		}
	});
});

/**
 * 它删除当前页面并添加当前页面。
 * @param {Book[]} cur - 当前页面数据
 */
function currentChange(cur: Book[]): void {
	// 先删除当前页面的所有选项在添加
	const deleteShop = [];
	for (let i = 0; i < pageInstance.view.length; ++i) {
		if (cur.indexOf(pageInstance.view[i]) > -1) continue;
		deleteShop.push(pageInstance.view[i]);
	}
	shoppingCart.removeAProduct(deleteShop);
	shoppingCart.addToCart(cur, (fail: Book): void => {
		el.value?.toggleRowSelection(fail, false);
	});
}

const store = useBookStore();
defineExpose<{ refresh: () => void; search: (virtual: Book[]) => void }>({
	refresh() {
		pageInstance.virtual();
	},
	search(virtual: Book[]) {
		pageInstance.virtual(new LocalVirtualPage(virtual, pageInstance.pageInfo));
	}
});
const userInfo = useUserInfoStore();

function linkTo(row: Book): void {
	store.addBook(row);
	router.push({
		name  : `book`,
		params: {id: row.id}
	});
}
</script>
<template>
	<div v-loading="pageInstance.pageInfo.totalSize===-1" class="_body">
		<el-table
				ref="el"
				v-model:data="pageInstance.view"
				class="_content"
				max-height="auto"
				scrollbar-always-on
				@select="currentChange"
				@row-click="linkTo"
				@select-all="currentChange">
			<el-table-column
					v-if="!userInfo.isAdmin"
					type="selection"/>
			<el-table-column
					label="id"
					property="id"/>
			<el-table-column
					label="书名"
					property="name"/>
			<el-table-column
					label="作者"
					property="author"/>
			<el-table-column
					label="类别"
					property="category"/>
			<el-table-column
					label="出版社"
					property="press"/>
			<el-table-column label="库存">
				<template #default="{ row }">
					{{ row.inventory.total - row.inventory.borrowed }}
				</template>
			</el-table-column>
			<el-table-column
					:formatter="formatter"
					label="上架时间"
					property="time"/>
			<el-table-column label="状态">
				<template #default="{ row }">
					<el-tag
							v-if="row.state === 1"
							hit
							size="large"
							type="info"
					>未上架
					</el-tag>
					<el-tag
							v-else-if="row.inventory.total === row.inventory.borrowed"
							hit
							size="large"
							type="warning"
					>库存不足
					</el-tag>
					<el-tag
							v-else
							hit
							size="large"
							type="success"
					>已上架
					</el-tag>
				</template>
			</el-table-column>
		</el-table>
		<div class="footer">
			<el-pagination
					v-model:current-page="pageInstance.pageInfo.currentIndex"
					v-model:page-size="pageInstance.pageInfo.pageSize"
					v-model:total="pageInstance.pageInfo.totalSize"
					background
					layout="total,sizes,prev, pager, next,jumper"
					@current-change="index => pageInstance.next(index)"/>
		</div>
	</div>
</template>
<style scoped>
._body {
    display: flex;
    width: 100%;
    height: 100%;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    gap: 20px;
}

._content {
    flex: 1 1px;
}

.footer {
    flex: 0 0 auto;
}
</style>