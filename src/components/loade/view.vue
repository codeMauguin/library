<template>
    <div class="process">
        <el-progress :percentage="percentage" :status="status" type="circle"/>
        <el-button v-if="percentage!==100" type="warning" @click="cancel">取消</el-button>
        <el-button v-else type="success" @click="emits('closed')">确定</el-button>
    </div>
</template>

<script lang="ts" setup>
import { ElButton, ElProgress } from "element-plus";
import type { Ref }             from "vue";

const cancel = (): void => {
	emits("cancel");
};
const percentage: Ref<number> = ref<number>(0);
const emits: { (e: "cancel"): void, (e: "closed"): void } = defineEmits<{ (e: "cancel"): void, (e: "closed"): void }>();
watch(percentage, () => {
	if (percentage.value === 100) {
		emits("closed");
	}
});
defineExpose({
	             progress(val: number) {
		             percentage.value = Number(val.toFixed(2));
	             },
	             closed(): void {
		             percentage.value = 0;
	             }
             });

const status = computed(() => percentage.value === 100 ? "success" : "");
</script>

<style scoped>
.process {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, .5);
    display: flex;
    align-items: center;
    justify-content: center;
    user-select: none;
    flex-direction: column;

    z-index: 100;
}

.el-progress {
    background: white;
    border-radius: 50%;
}

.el-button {
    margin-top: 20px;
}
</style>