<!-- @format -->

<script lang="ts" setup>
import { computed, type ComputedRef, defineProps, type Ref, ref } from "vue";

const show: Ref<boolean> = ref<boolean>(false);
	const loading: Ref<boolean> = ref<boolean>(false);

	const emits = defineEmits<{ (e: "change", cur: number): void }>();
	const props = withDefaults(defineProps<{ total: number; cur?: number }>(), {
		cur: 0
	}); //cur 当前数据展开量
	let timer: string | number | NodeJS.Timeout | null | undefined = null;
	defineExpose({
		closeLoading: () => {
			loading.value = false;
			show.value = true;
			if (timer) clearTimeout(timer);
		}
	});
	const titleText: ComputedRef<string> = computed(() =>
		show.value && props.cur == props.total ? "收起信息" : `展开 ${props.total} 条信息`
	);
	const iconClass: ComputedRef<string> = computed(() =>
		show.value && props.cur == props.total ? "icon-xuanzeqishouqi" : `icon-xuanzeqizhankai`
	);

	function change(): void {
		if (props.cur < props.total) {
			loading.value = true;
			timer = setTimeout(() => {
				loading.value = false;
				show.value = true;
			}, 3000);
			emits("change", props.cur);
		} else {
			//表示数据已经完全加载
			show.value = !show.value;
		}
	}
</script>

<template>
	<div class="ix">
		<el-collapse-transition>
			<div v-show="show" class="parent">
				<slot
					class="transition-box"
					name="body"></slot>
			</div>
		</el-collapse-transition>
	</div>
	<div
		v-loading="loading"
		:style="{ height: loading ? '100px' : '0px' }"
		element-loading-text="正在加载"></div>

	<div
		v-if="!loading"
		class="title"
		@click="change">
		<slot name="title">
			<el-button
				link
				type="info">
				{{ titleText }}</el-button
			>

			<i
				:class="iconClass"
				class="iconfont"></i>
		</slot>
	</div>
</template>
<style scoped>
	.title {
		font-size: 15px;
		user-select: none;
		display: flex;
		align-items: center;
	}
	.parent{
			display: flex;
			flex-direction: column;
			gap: 10px;
	}
</style>