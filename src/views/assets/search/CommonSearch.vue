<!-- @format -->

<template>
	<div class="__body___">
		<el-select
				v-model="searchMode"
				placeholder="Select"
				size="large"
				style="width: 120px"
				@change="() => (searchValue = '')">
			<el-option
					v-for="o in searchOption"
					:key="o.value"
					:label="o.label"
					:value="o.value"/>
		</el-select>
		<el-select
				v-model:model-value="searchValue"
				:filter-method="filter"
				:loading="search_load"
				clearable
				filterable
				placeholder="Please enter a keyword"
				size="large"
				@clear="emits('refresh')"
				@keyup.enter.native="search">
			<el-option
					v-for="item in searchRemote"
					:key="item.value"
					:label="item.label"
					:value="item.value"/>
		</el-select>
		
		<el-button-group>
			<el-button
					:dark="true"
					class="iconfont icon-sousuo"
					color="#626aef"
					size="large"
					type="primary"
					@click="search">
				&nbsp;&nbsp;搜索
			</el-button>
			<el-button
					:dark="true"
					class="iconfont icon-qingchu"
					size="large"
					@click="clean">
			</el-button>
		</el-button-group>
	</div>
</template>
<script lang="ts" setup>
import instance     from "@/axios";
import type Book    from "@/types/Book";
import type { Ref } from "vue";

/**
 *  fixme 使用虚拟列表
 *  2、页面搜索
 *
 *
 */
const searchValue: Ref<string> = ref<string>("");
const searchMode: Ref<string> = ref<string>("bookName");
const searchOption: {
	label: string;
	value: string;
}[] = [
	{
		label: "id",
		value: "bookId"
	},
	{label: "书籍名称", value: "bookName"},
	{
		label: "作者",
		value: "author"
	}
];
const searchRemote = ref<{ label: string; value: string }[]>([]);
const search_load = ref<boolean>(true);
const load_keyword = antiShake(async (keyword: string) => {
	if (!Assert.hasLength(keyword)) {
		searchRemote.value.splice(0, searchRemote.value.length);
		return;
	}
	search_load.value = true;
	const {
			  data: {data}
		  } = await instance.get(HttpURL.SEARCH_KEYWORD, {
		params: {condition: searchMode.value, keyword}
	});
	merge(
		searchRemote.value,
		data.map((d: string) => ({label: d, value: d}))
	);
	search_load.value = false;
}, 500);

function filter(val: string) {
	if (Assert.hasText(val)) searchValue.value = val;
	load_keyword(val);
}

function clean() {
	searchValue.value = "";
	emits("refresh");
}

const emits = defineEmits<{
	(e: "refresh"): never;
	(e: "search", data: Book[]): never;
}>();

const search = throttle(async () => {
	if (searchValue.value.length === 0) {
		emits("refresh");
		return;
	}
	start();
	try {
		const {
				  data: {data}
			  } = await instance.get(HttpURL.SEARCH_BOOK, {
			params: {
				mode  : searchMode.value,
				search: searchValue.value
			}
		});
		emits("search", data);
	} catch (e) {
	} finally {
		closeLoading();
	}
}, 400);
</script>
<style scoped>
.__body___ {
    display: flex;
    flex-direction: row !important;
    flex-wrap: nowrap;
    gap: 10px;
    width: 1000px;
}
</style>