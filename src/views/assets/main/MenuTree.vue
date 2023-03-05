<!-- @format -->

<template>
	<template
		v-for="item in props.data"
		:key="item.index">
		<el-sub-menu
			v-if="item.children"
			:index="item.index">
			<template #title>
				<el-icon v-if="item.icon">
					<i
						class="iconfont"
						:class="item.icon"></i>
				</el-icon>
				<span>{{ item.text }}</span>
			</template>
			<menu-tree :data="item.children"></menu-tree>
		</el-sub-menu>
		<el-menu-item
			v-else
			:index="item.index">
			<template #title>
				<el-icon v-if="item.icon">
					<i
						class="iconfont"
						:class="item.icon"></i>
				</el-icon>
				<span>{{ item.text }}</span>
			</template>
		</el-menu-item>
	</template>
</template>

<script setup lang="ts">
	import type { ElMenu } from "@/views/assets/main/PublicMenuInstance";

	const props = defineProps<{ data: ElMenu[] }>();
</script>

<style scoped></style>
