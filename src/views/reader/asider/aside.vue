<template>
    <div class="bs-d">
        <el-drawer>
            <template #default>
                <component :is="views"/>
            </template>
        </el-drawer>
    </div>
</template>

<script lang="ts" setup>
import type { Component } from "vue";
import type { Ref }       from "vue";

const views: Ref<string | Component> = shallowRef("");
defineExpose<{}>();
</script>

<style scoped>
.bs-d {
    height: 0;
}
</style>