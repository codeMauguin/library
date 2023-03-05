<!-- @format -->

<script lang="ts" setup>
import router         from "@/router";
import { RouterView } from "vue-router";

const getKey = () => {
	if (router.currentRoute.value.path.localeCompare("/login") === 0) {
		return 1;
	}
	return 2;
};
</script>

<template>
    <RouterView
            :key="getKey()"
            style="min-width: 1000px"/>
</template>

<style>
@import "@/assets/css/loading.css";
</style>