<template>
	<div class="_body">
		<div class="header">
			<el-input placeholder="查询编号" size="large"/>
			<el-checkbox label="正常用户"/>
			<el-checkbox label="已拉黑"/>
		</div>
		<el-table :data="pageInstance.view" border>
			<el-table-column type="selection"/>
			<el-table-column label="用户编号" prop="id"/>
			<el-table-column label="用户姓名" prop="name"/>
			<el-table-column
							 label="状态">
				<template #default="{row}">
					<el-tag :type="row.status===1?'danger':''" hit size="large">{{
						row.status === 1 ? "已拉黑" : "正常"
						}}
					</el-tag>
				</template>
			</el-table-column>
			<el-table-column label="操作">
				<template #default="{row}">
					<el-button-group size="large">
						<el-button v-if="row.status===0" class="btn_back" @click="pull(row)"><i
								class="iconfont icon-lahei"
								style="margin-right: 10px"/> 拉黑
						</el-button>
						
						<el-button v-else class="button-name" type="primary" @click="restoreInto(row)">
							<i class="iconfont icon-yichu"
							   style="margin-right: 10px"/>恢复
						</el-button>
					</el-button-group>
				</template>
			</el-table-column>
		</el-table>
		<el-pagination
				v-model:current-page="pageInstance.pageInfo.currentIndex"
				v-model:page-size="pageInstance.pageInfo.pageSize"
				v-model:total="pageInstance.pageInfo.totalSize"
				:page-sizes="[5, 10]"
				background
				class="el-pagination"
				layout="total,sizes,prev, pager, next,jumper"
				@current-change="index => pageInstance.next(index)"/>
	</div>
</template>

<script lang="ts" setup>
import instance              from "@/axios";
import type { PageData }     from "@/components/Pages/Page2";
import type { LoadPage }     from "@/components/Pages/Page2";
import type { PageInstance } from "@/components/Pages/Page2";
import user                  from "@/types/User";

type PrivateUser = Omit<user, "permissions">
const pageInstance: PageInstance<PrivateUser> = new Page(new class implements LoadPage<PrivateUser> {
	public async load(offset: number, pageSize: number, size: number, last?: PrivateUser):
		Promise<PageData<PrivateUser>> {
		return instance.get(AdminAPI.ALREADY_A, {params: {offset, pageSize, size}})
					   .then(({data: {data}}) => data);
	}
}());

function search() {
	console.log(arguments);
	return true;
}

function restoreInto(row: PrivateUser): void {
	IfStream.of(instance.put(AdminAPI.restore, row.id)
						.then(({data: {data}}) => data))
			.then(() => {
				row.status = 0;
				success("已恢复");
			})
			.catch((e) => {
				console.log(e);
				error("恢复失败");
			});
}

function pull(row: PrivateUser): void {
	IfStream.of(instance.put(AdminAPI.PULL, row.id)
						.then(({data: {data}}) => data))
			.then(() => {
				row.status = 1;
				success("已拉黑");
			})
			.catch((e) => {
				console.log(e);
				warning("拉黑失败");
			});
}
</script>

<style scoped>
@import "@/views/css/btn_back.css";
@import url("@/components/login/btn.css");

._body {
    display: flex;
    flex-direction: column;
    height: 100%;
}
.header{
	flex: 0 0 auto;
	padding:10px;
	display: flex;
	gap: 10px;
}

.el-table {
    height: 85%;
    flex: 1;
}

.el-pagination {
    flex: 0 0 auto;
	align-self: center;
}
</style>