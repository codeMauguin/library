<!-- @format -->

<template>
    <template
            v-for="item in props.data"
            :key="item.index">
        <el-sub-menu
                v-if="item.children&&check(item.el)"
                :index="item.index">
            <template #title>
                <el-icon v-if="item.icon">
                    <i
                            :class="item.icon"
                            class="iconfont"></i>
                </el-icon>
                <span>{{ item.text }}</span>
            </template>
            <menu-tree :data="item.children"/>
        </el-sub-menu>
        <el-menu-item
                v-else-if="check(item.el)"

                :index="item.index">
            <template #title>
                <el-icon v-if="item.icon">
                    <i
                            :class="item.icon"
                            class="iconfont"></i>
                </el-icon>
                <span>{{ item.text }}</span>
            </template>
        </el-menu-item>
    </template>
</template>

<script lang="ts" setup>
import { runner }      from "@/utils/FunctionWrapper";
import type { ElMenu } from "@/views/assets/main/PublicMenuInstance";

const props = defineProps<{ data: ElMenu[] }>();
const state = useUserInfoStore();

function check(el?: string): boolean {
	return Assert.isNull(el) || runner(el as string, {state});
}


</script>

<style scoped></style>