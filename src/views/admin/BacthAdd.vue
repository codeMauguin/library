<template>
    <el-descriptions
            border
            title="excel 表头映射字段">
        <el-descriptions-item label="书籍封面">
            <el-tag
                    size="large"
                    type="warning"
            >无
            </el-tag
            >
        </el-descriptions-item
        >
        <el-descriptions-item label="书籍名称"
        >
            <el-tag size="large">name</el-tag>
        </el-descriptions-item
        >
        <el-descriptions-item label="作者"
        >
            <el-tag size="large">author</el-tag>
        </el-descriptions-item
        >
        <el-descriptions-item label="类别"
        >
            <el-tag size="large">category</el-tag>
        </el-descriptions-item
        >
        <el-descriptions-item label="出版社"
        >
            <el-tag size="large">press</el-tag>
        </el-descriptions-item
        >
        <el-descriptions-item label="简介"
        >
            <el-tag size="large">introduction</el-tag>
        </el-descriptions-item
        >
        <el-descriptions-item label="库存"
        >
            <el-tag size="large">stock</el-tag>
        </el-descriptions-item
        >
    </el-descriptions>
    <el-alert
            description="库存字段不存在时,设置初始库存为0"
            show-icon
            title="提示"
            type="info"/>
    <el-switch
            v-model="errorStop"
            active-text="异常停止上传,并回退"
            inactive-text="异常忽略继续上传"/>
    <el-upload
            ref="uploadRef"
            :beforeUpload="before"
            :data="{errorStop}"
            :headers="{'lb':userStore.token}"
            :limit="1"
            :onError="error"
            :onProgress="progress"
            :onSuccess="success"
            accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.ms-excel"
            action="http://localhost:8088/admin/book/excel"
            class="upload-demo"
            drag>
        <div class="el-upload__text">
            Drop file here or <em>click to upload</em>
        </div>
    </el-upload>
</template>

<script lang="ts" setup>
import type ResponseApi from "@/axios/ResponseApi";
import {
    LoadProgress
}                       from "@/components/loade";
import {
    ElMessage, ElNotification, ElUpload, UploadFile, UploadFiles, type UploadInstance, UploadProgressEvent
}                       from "element-plus";
import type {
    Ref
}                       from "vue";

const uploadRef = ref<UploadInstance>();
const userStore = useUserInfoStore();

function success(response: ResponseApi<number>, uploadFile: UploadFile, uploadFiles: UploadFiles) {
	uploadRef.value?.clearFiles();
	if (response.code === 200 && response.data === 0) {
		ElMessage.success({
			grouping: true,
			message : response.error
		});
	} else {
		ElNotification.error({
			message: errorStop.value ? "所有数据已回退" : `失败了${response.data}本数据`
		});
	}
	LoadProgress.closed();
}

function progress(evt: UploadProgressEvent) {
	LoadProgress.progress((evt.loaded / evt.total) * 100);
}

function error() {
	LoadProgress.closed();
}

function before() {
	LoadProgress.start();
}

const errorStop: Ref<boolean> = ref<boolean>(true);
</script>
<style scoped>
</style>