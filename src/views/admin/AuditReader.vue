<!-- @format -->

<template>
	<div style="height: 100%">
		<el-skeleton
				:loading="pageInstance.pageInfo.totalSize === -1"
				:rows="10"
				animated>
			<template #default>
				<div
						ref="tableRef"
						class="__container">
					<el-table
							:border="true"
							:cell-style="{ 'text-align': 'center' }"
							:data="pageInstance.view"
							:header-cell-style="{ 'text-align': 'center' }"
							:highlight-current-row="true"
							class="___table"
							table-layout="fixed">
						<template #empty>
							<el-empty description="description"/>
						</template>
						<el-table-column
								label="用户编号"
								prop="id"/>
						<el-table-column
								label="用户名称"
								prop="username"/>
						<el-table-column
								label="书籍名称"
								prop="bookName"/>
						<el-table-column
								label="状态"
								sortable
								width="100">
							<template #default="{ row: { status } }">
								<el-tag
										:type="tagType(status)"
										hit
										size="large"
								>{{ tagValue(status) }}
								</el-tag
								>
							</template>
						</el-table-column>
						<el-table-column
								fixed="right"
								label="Operations">
							<template #default="{ row, $index }">
								<el-button-group
										v-if="row.status !== 1"
										size="large">
									<el-tooltip
											v-if="row.status !== 1"
											content="提醒还书">
										<el-button
												class="iconfont icon-tixingtianchong"
												@click.prevent="$index"/>
									</el-tooltip>
									
									<el-tooltip
											v-if="row.status !== 1"
											content="标记归还">
                      <div style="display: inline-block">
												<el-popconfirm
														cancel-button-text="取消"
														cancel-button-type="danger"
														confirm-button-text="归还"
														confirm-button-type="primary"
														title="是否归还当前用户图书?"
														@confirm="flag(row, 1)">
													<template #reference>
														<el-button
																class="iconfont icon-bianji"
														/>
													</template>
												</el-popconfirm>
											</div>
									
									</el-tooltip>
									
									<el-tooltip
											v-if="row.status !== -1"
											content="标记超时" >
										<div style="display: inline-block">
                      <el-popconfirm
                              cancel-button-text="取消"
                              cancel-button-type="danger"
                              confirm-button-text="超时"
                              confirm-button-type="primary"
                              title="是否归标记超时当前图书?"
                              @confirm="flag(row, -1)">
                          <template #reference>
                              <el-button
                                      class="iconfont icon-iconzhengli-"
                                      style="font-size: 20px"
                              />
                          </template>
                      </el-popconfirm>
										</div>
									</el-tooltip>
								</el-button-group>
								<span v-else>无</span>
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
		</el-skeleton>
	</div>
</template>

<script lang="ts" setup>
import instance               from "@/axios";
import type ResponseApi       from "@/axios/ResponseApi";
import type { PageData }      from "@/components/Pages/Page2";
import { LoadPage, Page }     from "@/components/Pages/Page2";
import { tagType, tagValue }  from "@/utils/Gloab";
import type { AxiosResponse } from "axios";

type Instance = {
	readonly id: number;
	readonly username: string;
	readonly bookName: string;
	readonly bookId: string;
	status: number; //借阅状态
	comments: string;
};

const pageInstance = new Page<Instance>(
	new (class implements LoadPage<Instance> {
		load(
			offset: number,
			pageSize: number,
			size: number,
			last?: Instance
		): Promise<PageData<Instance>> {
			// 因为需要优先展示正在借阅的数据，无法按照id排序
			return instance
				.get(AdminAPI.ALL_READER_BORROWED, {
					params: {
						offset,
						pageSize,
						size,
						id: last?.id
					}
				})
				.then(({data: {data}}: AxiosResponse<ResponseApi<PageData<Instance>>>) => {
					data.value.sort((a, b) => a.status - b.status);
					return data;
				});
		}
	})()
);

async function flag(id: Instance, state: number): Promise<void> {
	try {
		const {} = await instance.patch(AdminAPI.FLAG_BORROWED, {id: id.id, state});
		id.status = state;
		success("标记成功");
	} catch (e) {
		error("标记失败");
	}
}
</script>

<style scoped>
.__container {
    flex-direction: column;
    display: flex;
    align-items: center;
    gap: 15px;
    height: 100%;
}

.___table {
    width: 100%;
    height: 80%;
    flex: 1;
}

.el-pagination {
    flex: 0 0 auto;
}
</style>