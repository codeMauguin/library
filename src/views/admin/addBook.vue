<script lang="ts" setup>
import instance                            from "@/axios";
import { AdminAPI }                        from "@/axios/HttpURL";
import type Book                           from "@/types/Book";
import { error }                           from "@/utils/Alert";
import BatchAdd                            from "@/views/admin/BacthAdd.vue";
import type { Ref }                        from "vue";
import { reactive, ref, UnwrapNestedRefs } from "vue";

type UploadBook = Omit<Book, "id" | "comments" | "time" | "state" | "inventory" | "image"> & {
	inventory: { total: number };
};
const createBookTemplate = (): UploadBook => ({
	name     : "",
	author   : "",
	category : "",
	press    : "",
	info     : "",
	inventory: {total: 0}
});

function valid(row: UploadBook): string | symbol | null {
	for (const ownKey of Reflect.ownKeys(row)) {
		if (!Assert.hasText(Reflect.get(row, ownKey))) return ownKey;
	}
	return null;
}

function autoAppend() {
	if (!autoAdd.value) return;
	if (Books.length <= 1 && valid(Books[0]) === null) {
		Books.push(createBookTemplate());
	} else {
		addRow();
	}
}

function deleteRow(index: number) {
	Books.splice(index, 1);
	status.splice(index, 1);
	if (Books.length === 0) {
		status.push(false);
		Books.unshift(createBookTemplate());
	} else {
		status[status.length - 1] = false;
	}
}

const task: HTMLElement[] = [];
const status: UnwrapNestedRefs<boolean[]> = reactive<boolean[]>([false]);

function clear() {
	while (!Assert.isEmpty(task)) {
		const el: HTMLElement = <HTMLElement>task.shift();
		el.style.setProperty("border", "none");
	}
}

function editor(index: number) {
	status[index] = false;
}

function addRow(index?: number) {
	const key: string | symbol | null = valid(Books[index ?? Books.length - 1]);
	if (Assert.isNull(key)) {
		status[index ?? status.length - 1] = true;
		if (index === undefined || index === Books.length - 1) {
			status.push(false);
			Books.push(createBookTemplate());
		}
	} else {
		const els: NodeListOf<HTMLElement> = document.querySelectorAll(`.${<string>key}`);
		if (Assert.isEmpty(els)) return;
		const el: HTMLElement = els[index ?? els.length - 1];
		el.style.setProperty("border", "1px solid red");
		task.unshift(el);
		return;
	}
	if (Assert.notNull(index)) {
		status[index as number] = true;
	}
}

const activeName = ref<string>("one");
const autoAdd: Ref<boolean> = ref<boolean>(true);
const Books: UnwrapNestedRefs<UploadBook[]> = reactive<UploadBook[]>([createBookTemplate()]);

async function commit(): Promise<void> {
	task: {
		const tasks: UploadBook[] = Books.filter(value => Assert.isNull(valid(value)));
		if (Assert.isEmpty(tasks)) break task;
		//获取上传任务id 如果id获取失败或者重复
		try {
			await instance.post(AdminAPI.uploadBooks, tasks);
			success("上传成功");
		} catch (e) {
			error("上传失败");
			return;
		}
	}
	Books.splice(0, Books.length);
	status.splice(0, status.length);
	Books.unshift(createBookTemplate());
	status.unshift(false);
}
</script>
<template>
	<div class="body">
		<el-tabs
				v-model="activeName"
				class="card">
			<el-tab-pane
					key="1"
					label="单个插入"
					name="one">
				<el-table
						:border="true"
						:cell-style="{ 'text-align': 'center' }"
						:data="Books"
						:header-cell-style="{ 'text-align': 'center' }">
					<el-table-column label="书籍名称">
						<template #default="{ row, $index }">
							<el-input
									v-if="!status[$index]"
									v-model:model-value="row.name"
									class="name"
									@blur="autoAppend"
									@focus="clear"/>
							<span v-else>{{ row.name }}</span>
						</template>
					</el-table-column>
					<el-table-column label="作者">
						<template #default="{ row, $index }">
							<el-input
									v-if="!status[$index]"
									v-model:model-value="row.author"
									class="author"
									@blur="autoAppend"
									@focus="clear"/>
							<span v-else>{{ row.author }}</span>
						</template>
					</el-table-column>
					<el-table-column label="类别">
						<template #default="{ row, $index }">
							<el-input
									v-if="!status[$index]"
									v-model:model-value="row.category"
									class="category"
									@blur="autoAppend"
									@focus="clear"/>
							<span v-else>{{ row.category }}</span>
						</template>
					</el-table-column>
					<el-table-column label="出版社">
						<template #default="{ row, $index }">
							<el-input
									v-if="!status[$index]"
									v-model:model-value="row.press"
									class="press"
									@blur="autoAppend"
									@focus="clear"/>
							<span v-else>{{ row.press }}</span>
						</template>
					</el-table-column>
					<el-table-column label="简介">
						<template #default="{ row, $index }">
							<el-input
									v-if="!status[$index]"
									v-model:model-value="row.info"
									:autosize="{ maxRows: 2 }"
									class="info"
									type="textarea"
									@blur="autoAppend"
									@focus="clear"/>
							<span v-else>{{ row.info }}</span>
						</template>
					</el-table-column>
					<el-table-column label="库存">
						<template #default="{ row, $index }">
							<el-input
									v-if="!status[$index]"
									v-model:model-value="row.inventory.total"
							/>
							<span v-else>{{ row.inventory.total }}</span>
						</template>
					</el-table-column>
					<el-table-column
							label="操作"
							min-width="120">
						<template #header>
							<div
									style="
									display: flex;
									gap: 8px;
									align-items: center;
									justify-content: center;
								">
								<el-tooltip :content="autoAdd?'自动确定':'手动添加'">
									<label class="switch">
										<input checked type="checkbox" @change.stop="autoAdd=!autoAdd"/>
										<span class="slider"></span>
									</label>
								</el-tooltip>
							</div>
						</template>
						<template #default="{ row, $index }">
							<el-button-group>
								<el-button
										v-if="!status[$index]"
										color="#626aef"
										type="primary"
										@click.stop="addRow($index)">
									<template #icon>
										<i class="iconfont icon-tubiao_tijiao"/>
									</template>
									确定
								</el-button>
								<el-button
										v-else
										@click.stop="editor($index)">
									<template #icon>
										<i class="iconfont icon-bianji"/>
									</template>
									编辑
								</el-button>
								<el-button
										type="danger"
										@click.stop="deleteRow($index)">
									<template #icon>
										<i class="iconfont icon-shanchu1"/>
									</template>
									删除
								</el-button
								>
							</el-button-group>
						</template>
					</el-table-column>
				</el-table>
				<button class="sub_btn" style="margin-top: 20px" @click="commit">
					<div class=" svg-wrapper-1">
						<div class="svg-wrapper">
							<svg height="24" viewBox="0 0 24 24" width="24" xmlns="http://www.w3.org/2000/svg">
								<path d="M0 0h24v24H0z" fill="none"></path>
								<path d="M1.946 9.315c-.522-.174-.527-.455.01-.634l19.087-6.362c.529-.176.832.12.684.638l-5.454 19.086c-.15.529-.455.547-.679.045L12 14l6-8-8 6-8.054-2.685z"
									  fill="currentColor"></path>
							</svg>
						</div>
					</div>
					<span>提交</span>
				</button>
			</el-tab-pane>
			<el-tab-pane
					key="2"
					label="批量插入"
					name="second">
				<batch-add/>
			</el-tab-pane>
		</el-tabs>
	</div>
</template>
<style scoped>
@import url("@/views/css/button_submit.css");
.body {
    display: flex;
    flex-direction: row;
    width: 100%;
}

.card {
    width: 100%;
}

.name,
.author,
.category,
.press,
.info {
    border-radius: 4px;
}

/* The switch - the box around the slider */
.switch {
    font-size: 1rem;
    position: relative;
    display: inline-block;
    width: 4em;
    height: 2em;
}

/* Hide default HTML checkbox */
.switch input {
    opacity: 0;
    width: 0;
    height: 0;
}

/* The slider */
.slider {
    position: absolute;
    cursor: pointer;
    inset: 0;
    background-color: #eee;
    -webkit-transition: 0.4s;
    transition: 0.4s;
    border-radius: 0.5em;
    -webkit-box-shadow: 0 0.2em #dfd9d9;
    box-shadow: 0 0.2em #dfd9d9;
}

.slider:before {
    position: absolute;
    content: "";
    height: 1.5em;
    width: 1.4em;
    border-radius: 0.3em;
    left: 0.3em;
    bottom: 0.7em;
    background-color: lightsalmon;
    -webkit-transition: 0.4s;
    transition: 0.4s;
    -webkit-box-shadow: 0 0.4em #bcb4b4;
    box-shadow: 0 0.4em #bcb4b4;
}

.slider:hover::before {
    -webkit-box-shadow: 0 0.2em #bcb4b4;
    box-shadow: 0 0.2em #bcb4b4;
    bottom: 0.5em;
}

input:checked + .slider:before {
    -webkit-transform: translateX(2em);
    -ms-transform: translateX(2em);
    transform: translateX(2em);
    background: lightgreen;
}
</style>