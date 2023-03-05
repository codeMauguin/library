<!-- @format -->

<template>
	<div class="body">
		<div class="header">
			<CommonSearch
					@refresh="() => pageInstance.virtual()"
					@search="renderSearch"/>
		</div>
		<el-table
				v-model:data="pageInstance.view"
				:cell-style="{textAlign:'center',justifyContent: 'center',alignItems: 'center'}"
				border
				max-height="auto"
				size="large"
				style="width: 100%"
				table-layout="auto">
			<el-table-column type="selection"/>
			<el-table-column
					label="书名"
					prop="name"/>
			<el-table-column
					label="作者"
					prop="author"/>
			<el-table-column
					label="出版社"
					prop="category"/>
			<el-table-column
					label="类别"
					prop="press"/>
			<el-table-column
					label="简介"
					prop="info"/>
			<el-table-column label="当前库存">
				<template #default="{ row }">
					{{ row.inventory.total - row.inventory.borrowed }}
				</template>
			</el-table-column>
			<el-table-column label="总库存">
				<template #default="{ row }">
					{{ row.inventory.total }}
				</template>
			</el-table-column>
			<el-table-column label="状态" width="110">
				<template #default="{ row }">
					<el-tag :type="row.state === 0 ? 'success' : 'danger'" size="large"
					>{{ row.state === 0 ? "在货架中" : "已下架" }}
					</el-tag>
				</template>
			</el-table-column>
			<el-table-column
					label="操作"
					min-width="212px"
					width="212">
				<template #default="{ row }">
					<el-button-group size="large">
						<el-button @click="modify(row)">
							<template #icon>
								<i class="iconfont icon-bianji"/>
							</template>
							编辑
						</el-button>
						<el-popconfirm
								v-if="row.state === 0"
								cancel-button-type="success"
								confirm-button-type="danger"
								title="确定下架这本书么?"
								@confirm="takedown(row)">
							<template #reference>
								<el-button type="warning">
									<template #icon>
										<i class="iconfont icon-xiajia"/>
									</template>
									下架
								</el-button>
							</template>
						</el-popconfirm>
						<el-popconfirm
								v-else
								cancel-button-type="success"
								confirm-button-type="danger"
								title="确定上架这本书么?"
								@confirm="shelves(row)">
							<template #reference>
								<el-button color="#626aef">
									<template #icon>
										<i class="icon-shangjia iconfont"/>
									</template>
									上架
								</el-button>
							</template>
						</el-popconfirm>
					</el-button-group>
				</template>
			</el-table-column>
		</el-table>
		<el-pagination
				v-model:current-page="pageInstance.pageInfo.currentIndex"
				v-model:page-size="pageInstance.pageInfo.pageSize"
				v-model:total="pageInstance.pageInfo.totalSize"
				:page-sizes="[5, 10, 15]"
				layout="sizes,prev,pager,next"
				@current-change="index => pageInstance.next(index)"/>
		<el-dialog
				v-model="modifyView"
				align-center
				destroy-on-close
				draggable
				title="修改"
				width="400">
			<el-form
					ref="modify_ref"
					v-model:model="modifyData"
					label-position="right"
					label-suffix=":"
					label-width="67px">
				<el-form-item
						label="书名"
						prop="name"
						required>
					<el-input v-model:model-value="modifyData.name"/>
				</el-form-item>
				<el-form-item
						label="作者"
						prop="author"
						required>
					<el-input v-model:model-value="modifyData.author"/>
				</el-form-item>
				<el-form-item
						label="出版社"
						prop="press"
						required>
					<el-input v-model:model-value="modifyData.press"/>
				</el-form-item>
				<el-form-item
						label="类别"
						prop="category"
						required>
					<el-input v-model:model-value="modifyData.category"/>
				</el-form-item>
				<el-form-item
						label="库存"
						prop="inventory.total"
						required>
					<el-input-number
							v-model:model-value="modifyData.inventory.total"
							:min="modifyData.inventory.borrowed"
							controls-position="right"/>
				</el-form-item>
				<el-form-item
						label="简介"
						prop="info"
						required>
					<el-input
							v-model:model-value="modifyData.info"
							type="textarea"/>
				</el-form-item>
			</el-form>
			<template #footer>
				<el-button-group>
					<el-button @click="cancel">取消</el-button>
					<el-button
							color="blue"
							@click="commit"
					>确定
					</el-button
					>
				</el-button-group>
			</template>
		</el-dialog>
	</div>
</template>

<script lang="ts" setup>
import instance                     from "@/axios";
import type { PageInstance }        from "@/components/Pages/Page2";
import { LoadPage, Page, PageData } from "@/components/Pages/Page2";
import type Book                    from "@/types/Book";
import CommonSearch                 from "@/views/assets/search/CommonSearch.vue";
import type { FormInstance }        from "element-plus";
import type { Ref }                 from "vue";
import type { UnwrapNestedRefs }    from "vue";

const pageInstance: PageInstance<Book> = new Page(
	new (class implements LoadPage<Book> {
		async load(
			offset: number,
			pageSize: number,
			size: number,
			last?: Book
		): Promise<PageData<Book>> {
			const {
					  data: {data: data_1}
				  } = await instance.post(HttpURL.getAllBooks, {
				pageInfo: {
					offset,
					pageSize,
					size,
					id: last?.id
				}
			});
			return data_1;
		}
	})()
);

function renderSearch(data: Book[]): void {
	pageInstance.virtual(new LocalVirtualPage(data, pageInstance.pageInfo));
}

const modify_row: Ref<ModifyType | null> = shallowRef<ModifyType | null>(null);

function modify(row: ModifyType): void {
	modifyView.value = true;
	modify_row.value = row;
	merge(modifyData, row, true);
}

const modify_ref: Ref<FormInstance | null> = ref<FormInstance | null>(null);

async function commit(): Promise<void> {
	modify_ref.value?.validate(async (valid: boolean) => {
		if (valid) {
			try {
				const {
						  data: {data}
					  } = await instance.patch(AdminAPI.updateBook, modifyData);
				if (data) {
					merge(modify_row.value, modifyData, true);
					cancel();
					success("更新成功！！！");
				} else {
					warning("更新失败");
				}
			} catch (e) {
				warning("更新异常！");
			}
		} else {
			warning("完善数据！");
		}
	});
}

function cancel() {
	modifyView.value = false;
	Assert.clear(modifyData);
	modify_row.value = null;
}

type ModifyType = Omit<Book, "comments" | "state" | "time" | "image">;

const modifyData: UnwrapNestedRefs<ModifyType> = reactive<ModifyType>({
	id       : "",
	name     : "",
	author   : "",
	category : "",
	info     : "",
	inventory: {total: 0, borrowed: 0},
	press    : ""
});

/**
 * 上架书籍
 */
async function shelves(row: Book) {
	//网络请求
	try {
		const {
				  data: {data}
			  } = await instance.patch(AdminAPI.shelves, row.id, {
			headers: {
				"Content-Type": "application/json"
			}
		});
		if (data) {
			row.state = 0;
			success("书籍成功上架");
		}
	} catch (e) {
	}
}

/**
 * 下架书籍
 */
async function takedown(row: Book) {
	//网络请求
	try {
		const {
				  data: {data}
			  } = await instance.patch(AdminAPI.takedown, row.id, {
			headers: {
				"Content-Type": "application/json"
			}
		});
		if (data) {
			row.state = 1;
			success("下架书籍成功");
		}
	} catch (e) {
	}
}

//修改部分
const modifyView: Ref<boolean> = ref<boolean>(false);
</script>
<style scoped>
.el-table {
    flex: 1;
}

.el-pagination {
    flex: 0 0 auto;
}

.body {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    gap: 20px;
    height: 100%;
}

.header {
    align-self: flex-start;
}
</style>