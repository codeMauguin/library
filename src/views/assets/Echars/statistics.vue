<!-- @format -->

<template>
    <div class="statistic-content">
        <el-card shadow="hover">
            <v-chart
                    :option="option"
                    autoresize
                    style="width: 100%; height: 250px"></v-chart>
        </el-card>
        <el-card shadow="hover">
            <el-select
                    v-model="value"
                    clearable
                    placeholder="年份"
                    @change="changeYear">
                <el-option
                        v-for="item in options"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"/>
            </el-select
            >
            <v-chart
                    :option="borrowedOption"
                    autoresize
                    style="width: 100%; height: 250px"></v-chart>
        </el-card>
    </div>
</template>

<script lang="ts" setup>
import instance                    from "@/axios";
import type ResponseApi            from "@/axios/ResponseApi";
import type { AxiosResponse }      from "axios";
import { ElCard, ElMessage }       from "element-plus";
import { computed, onBeforeMount } from "vue";

const value = ref<number>(new Date().getFullYear());
const options: { value: number; label: string }[] = [];
for (let i = 4; i >= 0; --i) {
	options.push({
		value: value.value - i,
		label: `${value.value - i}年`
	});
}
type BorrowedInfo = {
	borrowed: number;
	timeout: number;
};
const ck: BorrowedInfo = reactive<BorrowedInfo>({
	borrowed: 0,
	timeout : 0
});
const time = reactive<{
	value: number[];
	count: number[];
}>({value: [], count: []});

function changeYear() {
	if (typeof value.value === "string") {
		return;
	}
	instance
		.get("/admin/statistic/time", {params: {year: value.value}})
		.then(({data: {data}}: AxiosResponse<ResponseApi<number[]>>): void => {
			merge(time.value, data);
			time.count[0] = data[0];
			for (let index = 1; index < data.length; ++index) {
				time.count[index] = data[index] + time.count[index - 1];
			}
		});
}

onBeforeMount(() => {
	instance
		.get("/admin/statistics/borrowed")
		.then(({data: {data}}: AxiosResponse<ResponseApi<BorrowedInfo>>): void => {
			ck.borrowed = data.borrowed;
			ck.timeout = data.timeout;
		})
		.catch(() => {
			ElMessage.warning({
				grouping: true,
				message : "查询失败"
			});
		});
	changeYear();
});
const borrowedOption = computed(() => {
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
				name       : "借出",
				min        : 0,
				max        : Math.max(...time.value),
				minInterval: 1,
				position   : "left",
				axisLabel  : {
					formatter: "{value} 本"
				}
			},
			{
				type       : "value",
				name       : "总借出",
				min        : 0,
				minInterval: 1,
				max        : time.count[time.count.length - 1],
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
				smooth    : true,
				yAxisIndex: 0,
				data      : time.value
			},
			{
				name      : "累计总借出",
				type      : "line",
				smooth    : true,
				yAxisIndex: 1,
				data      : time.count
			}
		]
	};
});
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
							借阅中  : "#a5a5b2",
							剩余库存: "#177faa",
							超时    : "#ff0000"
						};
						return colorList[colors.name];
					}
				},
				data     : [
					{
						value: ck.borrowed,
						name : "借阅中"
					},
					{
						value: ck.timeout,
						name : "超时"
					}
				]
			}
		]
	};
});
</script>

<style scoped>
.statistic-content {
    display: flex;
    gap: 10px;
    flex-direction: column;
}
</style>
