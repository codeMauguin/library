<template>
	<div v-status="'admin'" class="content">
		<div class="statistic-card">
			<div class="el-statistics">
				<el-statistic :value="borrowed.count" title="日借阅数">
				</el-statistic>
				<div class="statistic-footer">
					<div class="footer-item">
						<span>than yesterday </span>
						<span class="green">
              {{ borrowed.count - (borrowed as statistic).yesterday }}
            </span>
					</div>
				</div>
			</div>
			<div class="el-statistics">
				<el-statistic :value="returnData.count" title="日归还">
				</el-statistic>
				<div class="statistic-footer">
					<div class="footer-item">
						<span>than yesterday </span>
						<span class="green">
             {{ returnData.count - (returnData as statistic).yesterday }}
            </span>
					</div>
				</div>
			</div>
			<div>
				<v-chart
						:option="option"
						autoresize
						style="min-width: 340px;height: 100%"
				/>
			</div>
		</div>
		<el-select
				v-model="curYear"
				clearable
				placeholder="年份"
				style="width: 25%"
				@change="change"
		>
			<el-option
					v-for="(item,index) in 10"
					:key="item"
					:label="new Date().getFullYear()-index"
					:value="new Date().getFullYear()-index"/>
		</el-select
		>
		<v-chart
				:option="TotalStatistic"
				autoresize
				style="width: 100%; min-height:280px "></v-chart>
	</div>
</template>
<script async lang="ts" setup>
import instance               from "@/axios";
import type ResponseApi       from "@/axios/ResponseApi";
import type { AxiosResponse } from "axios";

type statistic = {
	count: number;
	yesterday: number
}
let response: AxiosResponse<ResponseApi<{
	borrowed: statistic;
	returnData: statistic,
	totalBorrow: { count: number, timeout: number },
	statistic: { borrowed: number[], returnTotal: number[] }
}>>;
try {
	response = await
		instance.get(AdminAPI.STATISTIC);
} catch (e) {
	response = <AxiosResponse><unknown>{
		data: {
			code: 1000, error: "",
			data: {
				borrowed   : {count: 0, yesterday: 0},
				returnData : {count: 0, yesterday: 0},
				totalBorrow: {count: 0, timeout: 0},
				statistic  : {borrowed: [], returnTotal: []}
			}
		}
	};
}


const {data: {data: {borrowed, returnData, totalBorrow, statistic}}} = response;
const option = computed(() => {
	return {
		title  : {},
		tooltip: {},
		legend : {
			orient: "vertical",
			left  : "auto",
			data  : ["借阅中", "超时"]
		},
		series : [
			{
				name     : "库存信息",
				type     : "pie",
				itemStyle: {
					color: function (colors: { name: string }): string {
						const colorList: Record<string, string> = {
							"借阅中": "#3030c8",
							"超时"  : "#ff0000"
						};
						return colorList[colors.name];
					}
				},
				data     : [
					{
						value: totalBorrow.count,
						name : "借阅中"
					},
					{
						value: totalBorrow.timeout,
						name : "超时"
					}
				]
			}
		]
	};
});
const statistics = reactive(statistic);
const statisticsBorrowed: number[] = reactive([]);
const statisticsReturn: number[] = reactive([]);

async function change(): Promise<void> {
	if (Object.is(curYear.value, "")) {
		curYear.value = new Date().getFullYear();
	}
	const {data: {data: {borrowed, returnTotal}}} = await
		instance.get(`${AdminAPI.STATISTIC_YEAR}/${curYear.value}`);
	merge(statistics.borrowed, borrowed);
	merge(statistics.returnTotal, returnTotal);
	getStatistic();
}

function getStatistic(): void {
	merge(statisticsBorrowed, statistics.borrowed);
	merge(statisticsReturn, statistics.returnTotal);
	for (let i = 1; i < statisticsBorrowed.length; ++i) {
		statisticsBorrowed[i] += statisticsBorrowed[i - 1];
		statisticsReturn[i] += statisticsReturn[i - 1];
	}
}

getStatistic();
const TotalStatistic = computed(() => {
	return {
		tooltip: {
			trigger    : "axis",
			axisPointer: {type: "cross"}
		},
		legend : {},
		xAxis  : [
			{
				type    : "category",
				axisTick: {
					alignWithLabel: true
				},
				data    : [
					"1月",
					"2月",
					"3月",
					"4月",
					"5月",
					"6月",
					"7月",
					"8月",
					"9月",
					"10月",
					"11月",
					"12月"
				]
			}
		],
		yAxis  : [
			{
				type       : "value",
				name       : "数量",
				min        : 0,
				max        : Math.max(Math.max(...statistics.borrowed), Math.max(...statistics.returnTotal)),
				minInterval: 1,
				position   : "left",
				axisLabel  : {
					formatter: "{value} 本",
					rotate   : 20
				}
			},
			{
				type       : "value",
				name       : "总数",
				min        : 0,
				minInterval: 5,
				max        : Math.max(statisticsBorrowed[statisticsBorrowed.length - 1],
					statisticsReturn[statisticsReturn.length - 1]),
				position   : "right",
				axisLabel  : {
					formatter: "{value} 本"
				}
			}
		],
		series : [
			{
				name      : "借出",
				type      : "bar",
				yAxisIndex: 0,
				data      : statistics.borrowed
			},
			{
				name      : "还书",
				type      : "bar",
				yAxisIndex: 0,
				data      : statistics.returnTotal
			},
			{
				name      : "累计归还",
				type      : "line",
				smooth    : true,
				yAxisIndex: 1,
				data      : statisticsReturn
			},
			{
				name      : "累计总借出",
				type      : "line",
				smooth    : true,
				yAxisIndex: 1,
				data      : statisticsBorrowed
			}
		]
	};
});
const curYear = ref<number>(new Date().getFullYear());
</script>

<style scoped>
.content {
    display: flex;
    flex-direction: column;
    gap: 10px;
    width: 50%;
}

.statistic-card {
    display: flex;
    flex-direction: row;
    gap: 20px;
}

.el-statistics {
    height: auto;
    display: flex;
    flex-direction: column;
    gap: 20px;
    border-radius: 10%;
    padding: 30px;
    background: rgb(243, 236, 236);
}

.footer-item {
    font-size: 12px;
}
</style>