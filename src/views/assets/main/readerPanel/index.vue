<template>
	<div class="top">
		<div v-status="'reader'" class="body">
			<div class="card">
				<el-statistic :value="borrowed" suffix="本" title="借阅中"/>
			</div>
			<div class="card">
				<el-countdown :value="borrowedTime.returnTime" format="DD [天] HH:mm:ss" >
					<template #title>
						最近待返还书籍 <el-link class="iconfont icon-wodejieyue" href="/reader/borrowed" type="primary">跳转</el-link>
					</template>
				</el-countdown>
			</div>
			<div class="card">
				<el-statistic :value="timer" suffix="本" title="超时">
				</el-statistic>
			</div>
			<div class="card">
				<el-statistic :value="getDay(timeout.returnTime)" suffix="天" >
					<template #title>
						最近逾期借阅已过去  <el-link class="iconfont icon-wodejieyue"  href="/reader/borrowed" type="primary">跳转</el-link>
					</template>
				</el-statistic>
			</div>
		</div>
		<div class="card">
			<el-statistic :value="history" suffix="本" title="历史借阅"/>
		</div>
	</div>
</template>

<script async lang="ts" setup>

import instance from "@/axios";

function getDay(time: string): number {
	return Math.abs((new Date().getTime() - new Date(time).getTime()) / 1000 / 3600 / 24);
}

const {
		  data: {
			  data: {
				  borrowed, returnTime, timer, timeout,history
			  }
		  }
	  } = await instance.get(HttpURL.COUNT);
const borrowedTime = returnTime;
borrowedTime.returnTime = new Date(borrowedTime.returnTime);
const time = timeout;
</script>

<style scoped>
.top {
    display: grid;
    grid-column-end: auto;
    grid-row-gap: 20px;
    grid-template-columns: repeat(auto-fill, 430px);
    grid-auto-rows: 200px;
}

.body {
    display: flex;
    gap: 20px;
    flex-wrap: wrap;
}

.card {
    width: 190px;
    height: 190px;
    border-radius: 30px;
    background: #e0e0e0;
    box-shadow: 15px 10px 30px #bebebe,
    -15px -15px 30px #ffffff;
    display: flex;
    align-items: center;
    justify-content: center;
}

.card:hover {
    border-radius: 30px;
    animation: sale .2s;
}

@keyframes sale {
    0% {

    }
    50% {
        transform: scale(0.9);
    }
}

</style>