<!-- @format -->

<template>
    <el-drawer
            :model-value="show"
            direction="rtl"
            size="450"
            title="购物车"
            @closed="closed">
        <template #default>
            <el-table
                    ref="multipleTableRef"
                    :cell-style="{ 'text-align': 'center' }"
                    :data="datas"
                    :header-cell-style="{ 'text-align': 'center' }"
                    size="small"
                    style="width: 100%"
                    @selection-change="handleSelectionChange">
                <el-table-column
                        type="selection"
                        width="55"/>
                <el-table-column
                        label="书名"
                        prop="name"
                        width="100"></el-table-column>
                <el-table-column
                        label="数量"
                        width="80">
                    <template #default>
                        <el-tag
                                size="large"
                                type="info"
                        >1
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="删除">
                    <template #default="{ row }">
                        <el-button
                                circle
                                class="iconfont icon-shanchu"
                                type="danger"
                                @click="emits('delete', row)"/>
                    </template>
                </el-table-column>
            </el-table>
        </template>

        <template #footer>
            <div style="text-align: left">
                <el-steps
                        v-if="stepShow"
                        :active="active"
                        :space="200"
                        align-center
                        finish-status="success">
                    <el-step
                            :status="status[0]"
                            title="创建订单"/>
                    <el-step
                            :status="status[1]"
                            title="订单创建成功"/>
                    <el-step
                            :status="status[2]"
                            title="借阅成功"/>
                </el-steps>
            </div>
            已选择：{{ total }}
            <div style="flex: auto; margin-top: 20px">
                <el-button-group>
                    <el-button @click="closed">cancel</el-button>
                    <el-button
                            type="warning"
                            @click="shoppingCart.emptyTheCart">
                        清空购物车
                    </el-button>
                    <el-button
                            type="primary"
                            @click="commit"
                    >借阅
                    </el-button>
                </el-button-group>
            </div>
        </template>
    </el-drawer>
</template>

<script lang="ts" setup>
import type ResponseApi                   from "@/axios/ResponseApi";
import {
    useReaderStore
}                                         from "@/stores/readerStore";
import {
    useShopStore
}                                         from "@/stores/ShopStore";
import type Book                          from "@/types/Book";
import {
    SignalDriver
}                                         from "@/utils/Signal";
import {
    commitOrder
}                                         from "@/views/order/order";
import type { AxiosResponse }             from "axios";
import {
    ElButton, ElButtonGroup, ElDrawer, ElMessage, ElStep, ElSteps, ElTable, ElTableColumn, ElTag
}                                         from "element-plus";
import { nextTick, reactive, ref, watch } from "vue";

const multipleTableRef = ref<InstanceType<typeof ElTable>>();
const emits = defineEmits<{ (e: "delete", row: Book): void }>();
const total = computed(() => changeBooks.length);
const changeBooks = reactive<Book[]>([]);
const datas = computed(() => Object.values(defaults.books));
const shoppingCart = useShopStore();

const active = ref<number>(0);
const status = ref<string[]>(["wait", "wait", "wait"]);

const stepShow = ref<boolean>(false);
const signal: SignalDriver = new SignalDriver();
signal.on(1, () => {
	status.value[active.value] = "success";
	++active.value;
});
signal.on(2, (data: boolean): void => {
	if (data) {
		status.value[2] = "success";
		ElMessage.success({
			grouping: true,
			message : "借阅成功"
		});
		const reader = useReaderStore();
		reader.borrowALibraryCard.borrowedBook.push(...changeBooks.map(map));
		shoppingCart.removeAProduct(changeBooks);
		changeBooks.splice(0, changeBooks.length);
	}
});
signal.on(3, (e: AxiosResponse<ResponseApi<any>>): void => {
	status.value[active.value] = "error";
	status.value[2] = "error";
	active.value = 2;
	ElMessage.warning({
		grouping: true,
		message : e?.data?.error ?? "借阅失败"
	});
});
signal.on(4, () => {
	setTimeout(() => {
		stepShow.value = false;
	}, 3000);
});

const defaults = withDefaults(
	defineProps<{
		show: boolean;
		books: Record<string, Book>;
		closed: () => void;
	}>(),
	{
		show: false
	}
);
watch(defaults, () => {
	if (defaults.show) {
		nextTick(() => {
			multipleTableRef.value?.clearSelection();
			multipleTableRef.value?.toggleAllSelection();
		});
	}
});

function map(target: Book): string {
	return target.id;
}

function commit(): void {
	stepShow.value = true;
	Promise.resolve(commitOrder(changeBooks.map(map), signal)).finally(() => {
		signal.emit(4);
	});
}

function handleSelectionChange(change: Book[]) {
	for (let i = 0; i < change.length; ++i) {
		changeBooks[i] = change[i];
	}
	changeBooks.length = change.length;
}
</script>

<style scoped></style>
