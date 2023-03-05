<!-- @format -->

<!--
    增加历史记录的路由
    记录历史访问
-->
<template>
	<div class="mx-1">
		<template
			v-for="(item, index) in tagData"
			:key="item.path">
			<el-tag
				:hit="true"
				:style="{
					cursor:
						router.currentRoute.value.path === item.path
							? 'no-drop !important'
							: 'pointer'
				}"
				:type="router.currentRoute.value.path === item.path ? defaultType : tagsType[index]"
				class="mx-1"
				closable
				color=""
				effect="plain"
				size="large"
				@click="linkTo(item.path)"
				@close="handleClose(index)"
				@mouseenter="hover(index, item.path)"
				@mouseleave="leave(index)">
				{{ item.name }}
			</el-tag>
			<el-divider
				v-if="index !== tagData.length - 1"
				direction="vertical" />
		</template>
	</div>
</template>

<script lang="ts" setup>
	import { ElDivider, ElTag } from "element-plus";
	import type { EpPropMergeType } from "element-plus/es/utils";
	import type { Ref } from "vue";
	import type { RouteRecordName } from "vue-router";
	import { useRouter } from "vue-router";

	const tagData: Ref<{ path: string; name: RouteRecordName | null | undefined }[]> = ref<
		{
			path: string;
			name: RouteRecordName | null | undefined;
		}[]
	>([]);
	const defaultType: EpPropMergeType<
		StringConstructor,
		"" | "success" | "warning" | "info" | "danger",
		unknown
	> = "success";

	const tagsType: EpPropMergeType<
		StringConstructor,
		"" | "success" | "warning" | "info" | "danger",
		unknown
	>[] = [];
	const router = useRouter();
	router.beforeResolve((to, from) => {
		if (
			from.name === "book" ||
			tagData.value.some(value => value.path.localeCompare(from.path) === 0)
		)
			return;
		tagData.value.push({
			path: from.path,
			name: from.name
		});
		tagsType.push("info");
	});

	function handleClose(index: number) {
		tagData.value.splice(index, 1);
		tagsType.splice(index, 1);
	}

	function leave(index: number) {
		tagsType[index] = "info";
	}

	function linkTo(path: string) {
		if (router.currentRoute.value.path !== path) {
			router.push(path);
		}
	}

	function hover(index: number, path: string) {
		if (router.currentRoute.value.path !== path) {
			tagsType[index] = "";
		}
	}
</script>

<style scoped>
	.mx-1 {
		cursor: pointer;
		user-select: none;
		display: flex;
		align-items: center;
		flex-wrap: nowrap;
		height: 100%;
		border-radius: 0%;
	}
</style>
