<!-- @format -->

<template>
	<div style="display: flex; flex-direction: column; height: 100%;
	align-items:
	center">
		<div class="contain">
			<div class="container">
				<div>
					<el-input
							v-model="searchText"
							clearable
							size="large"
							style="border-bottom-left-radius: 0; width: 300px"
							@clear="() => pageInstance.virtual()">
						<template #append>
							<el-button @click.prevent="search">
								<template #icon>
									<i class="iconfont icon-sousuo"></i>
								</template>
								<template #default>搜索</template>
							</el-button>
						</template>
					</el-input>
				</div>
				<div style="margin-left: auto; min-width: 350px; display: flex">
					<el-button
							size="large"
							style="margin-left: auto"
							type="success"
							@click.prevent="() => open(1)">
						<template #icon>
							<i class="iconfont icon-tianjiayonghu"></i>
						</template>
						<template #default><span>添加管理员账户</span></template>
					</el-button>
					<el-button
							size="large"
							style="margin-left: auto"
							type="primary"
							@click.prevent="() => open(0)">
						<template #icon>
							<i class="iconfont icon-tianjiayonghu"></i>
						</template>
						<template #default><span>添加读者账户</span></template>
					</el-button>
				</div>
			</div>
			<el-table
					:cell-style="{ 'text-align': 'center' }"
					:data="pageInstance.view"
					:header-cell-style="{ 'text-align': 'center' }"
					:v-loading="pageInstance.pageInfo.totalSize===-1"
					border>
				<el-table-column type="selection"/>
				<el-table-column
						label="用户名"
						prop="name"/>
				<el-table-column
						label="总借阅额度"
						prop="borrowed">
					<template #default="{ row }">
						<el-slider
								v-if="row.permissions.localeCompare('reader') === 0"
								v-model="row.total"
								:max="100"
								show-input/>
						<span v-else>0</span>
					</template>
				</el-table-column>
				<el-table-column
						:sort-by="'permissions'"
						label="角色"
						sortable>
					<template #default="{ row }">
						<el-tag :type="row.status=== -1?'danger':row.permissions === 'reader' ? 'info' :'success'" hit
										size="large"
						>{{ row.status=== -1?"已删除":row.permissions === "reader" ? "读者" : "管理员"  }}
						</el-tag>
					</template>
				</el-table-column>
				<el-table-column
						fixed="right"
						label="操作">
					<template #default="{ row }">
						<el-button-group size="large">
							<el-button
									:disabled="row.permissions === 'admin'"
									type="primary"
									@click="() => addReaderTotal(row)">
								<template #icon>
									<i class="iconfont icon-bianji"/>
								</template>
								修改
							</el-button>
							<template v-if="row.permissions !=='admin'">
								<template v-if="row.status !== -1">
									<el-button type="danger" @click="delUser(row)">
										<template #icon>
											<i class="iconfont icon-shanchu"/>
										</template>
										删除用户
									</el-button>
								</template>
								<template v-else>
									<el-button type="success" @click="restoreInto(row)">
										恢复账户
									</el-button>
								</template>
							</template>
						
						</el-button-group>
					</template>
				</el-table-column>
			</el-table>
		</div>
		<el-pagination
				v-model:current-page="pageInstance.pageInfo.currentIndex"
				v-model:page-size="pageInstance.pageInfo.pageSize"
				v-model:total="pageInstance.pageInfo.totalSize"
				:page-sizes="[5, 10]"
				background
				layout="total,sizes,prev, pager, next,jumper"
				@current-change="index => pageInstance.next(index)"/>
		
		<el-dialog
				v-model="show"
				align-center
				width="500">
			<template #header>
				<el-page-header @back="cancel">
					<template #content
					><span>{{ title }}</span></template
					>
				</el-page-header>
			</template>
			<template #default>
				<el-form
						ref="formInstance"
						:model="data"
						:rules="rules"
						label-suffix=" :"
						label-width="100"
						require-asterisk-position="right"
						status-icon>
					<el-form-item
							label="userId"
							prop="id">
						<el-input
								v-model="data.id"
								autocomplete="false"></el-input>
					</el-form-item>
					<el-form-item
							label="username"
							prop="username">
						<el-input
								v-model="data.username"
								autocomplete="false"/>
					</el-form-item>
					<el-form-item
							label="password"
							prop="password">
						<el-input
								v-model="data.password"
								:show-password="true"
								autocomplete="new-password"
								type="password"/>
					</el-form-item>
				</el-form>
			</template>
			<template #footer>
				<span class="dialog-footer">
					<el-button @click="cancel">Cancel</el-button>
					<el-button
							type="primary"
							@click="addUser">
						添加
					</el-button>
				</span>
			</template>
		</el-dialog>
	</div>
</template>

<script lang="ts" setup>
import { AdminAPI }                 from "@/axios/HttpURL";
import instance                     from "@/axios/index";
import type ResponseApi             from "@/axios/ResponseApi";
import { LoadPage, Page, PageData } from "@/components/Pages/Page2";
import type { AxiosResponse }       from "axios";
import { ElMessage }                from "element-plus";
import {
	ElButton, ElDialog, ElForm, ElFormItem, ElInput, ElPageHeader, ElPagination, ElTable, ElTableColumn, ElTag,
	FormInstance, FormRules
}                                   from "element-plus";
import { reactive, ref }            from "vue";

const searchText = ref<string>("");
type User = {
	id: string;
	name: string;
	
	cardId: string;
	permissions: string;
	borrowed: number;
	total: number;
};
const pageInstance = new Page<User>(
		new (class implements LoadPage<User> {
			public load(
					offset: number,
					pageSize: number,
					size: number,
					last?: User
			): Promise<PageData<User>> {
				return instance
						.get(AdminAPI.ALREADY_ACCOUNT, {
							params: {
								offset,
								pageSize,
								size,
								id: last
							}
						})
						.then(({data: {data}}) => data);
			}
		})()
);

function search() {
}

function addReaderTotal(row: User): void {
	instance
			.patch(AdminAPI.UPDATE_READER_BORROWED, {
				id   : row.id,
				total: row.total
			})
			.then(({data: {data}}: AxiosResponse<ResponseApi<boolean>>): any => {
				if (data)
					success({
										message: "修改成功"
									});
				else {
					return Promise.reject({data: {error: "失败"}});
				}
			})
			.catch((reason: any) => {
				error(reason.data.error);
			});
}

function restoreInto(row: any): void {
	IfStream.of(instance.put(AdminAPI.restore, row.id)
											.then(({data: {data}}) => data))
					.then(() => {
						row.status = 0;
						success("已恢复");
					})
					.catch((e) => {
						error("恢复失败");
					});
}

const formInstance = ref<FormInstance | null>(null);
const show = ref<boolean>(false);
const open = (index: number = 0): void => {
	if (index === 0) {
		title.value = "添加读者";
		status.value = 0;
	} else {
		status.value = 1;
		title.value = "添加管理员";
	}
	show.value = true;
};
const status = ref<number>(0);
const cancel = () => {
	formInstance.value?.resetFields();
	show.value = false;
};

const title = ref<string>("增加读者");

type instance = { id?: string; username: string; password: string };
const data = reactive<instance>({
																	username: "",
																	password: ""
																});

function addUser(): void {
	formInstance.value?.validate(async (isValid: boolean): Promise<void> => {
		try {
			let id: AxiosResponse<ResponseApi<string>>;
			isValid &&
			(id = await instance.post(AdminAPI.REGISTRY, {
				name      : data.username,
				password  : data.password,
				Permission: status.value === 0 ? "reader" : "admin"
			}));
			if (!isValid) {
				return;
			}
			pageInstance.updateData((datas: User[], sizes: number) => {
				datas.push({
										 id         : id.data.data,
										 name       : data.username,
										 cardId     : "",
										 borrowed   : 0,
										 permissions: status.value === 0 ? "reader" : "admin",
										 total      : 0
									 });
				return sizes + 1;
			});
		} catch (e) {
		}
	});
}

async function delUser(row: any): Promise<void> {
	try {
		const {
						data: {
							data,
							error
						}
					} = await instance.put(AdminAPI.DELETE_USER, row.id);
		ElMessage.success(data);
		row.status = -1;
	} catch ({
		data: {
			error
		}
	}) {
		ElMessage.error(<any>error);
	}
}

const rules: FormRules = {
	id      : {
		required: true,
		min     : 1,
		message : "id不能为空",
		trigger : "blur"
	},
	username: {
		required: true,
		min     : 1,
		message : "名称为空",
		trigger : "blur"
	},
	password: {
		required: true,
		min     : 1,
		message : "密码为空",
		trigger : "blur"
	}
};
</script>

<style scoped>
.contain {
	width: 100%;
	flex-grow: 1;
	align-self: flex-start;
}

.container {
	display: flex;
	justify-content: space-between;
	height: 50px !important;
}

.el-pagination {
	flex-shrink: 0;
}

.el-table {
	align-self: center;
}
</style>