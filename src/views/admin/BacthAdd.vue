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
    <blockquote class="blockquote" style="--block: #909399">库存字段不存在时,设置初始库存为0</blockquote>

    <label for="temperature" style="display: flex;align-items: center;height: 40px;margin-bottom:10px;gap: 10px">
        <span>配置:</span>
     
        <el-tooltip :content="errorStop?'异常中断':'异常继续上传'">
            <input id="temperature" v-model="errorStop" class="ch"  name="temperature" type="checkbox"
                   value="is_hot"
            >
        </el-tooltip>
       <el-tooltip content="上传错误是否中断">
           <el-button class="iconfont icon-tishi" text></el-button>
       </el-tooltip>
    </label>
    <el-upload
            ref="uploadRef"
            :beforeUpload="before"
            :http-request="upload"
            :limit="1"
            :onError="errors"
            :onSuccess="suc"
            class="upload-demo"
            drag>
        <div class="el-upload__text">
            Drop file here or <em>click to upload</em>
        </div>
    </el-upload>
</template>

<script lang="ts" setup>
import instance               from "@/axios";
import type ResponseApi       from "@/axios/ResponseApi";
import { LoadProgress }       from "@/components/loade";
import { AxiosProgressEvent } from "axios";
import {
    ElNotification, ElUpload, UploadFile, UploadFiles, type UploadInstance, UploadProgressEvent, UploadRequestOptions
}                             from "element-plus";
import type { Ref }           from "vue";

const uploadRef = ref<UploadInstance>();
const userStore = useUserInfoStore();

function suc(response: ResponseApi<number>, uploadFile: UploadFile, uploadFiles: UploadFiles) {
	uploadRef.value?.clearFiles();
	LoadProgress.closed();
}

function progress(evt: UploadProgressEvent) {
	LoadProgress.progress((evt.loaded / evt.total) * 100);
}

function errors() {
	uploadRef.value?.clearFiles();
	ElNotification.error({
							 message: "所有数据已回退"
						 });
	LoadProgress.closed();
}

function upload(options: UploadRequestOptions) {
	const data = new FormData();
	data.set("file", options.file, options.file.name);
	data.set("errorStop", String(errorStop.value));
	return instance.post(AdminAPI.BATCH_URL, data, {
		lb: false, headers: {lb_1: true}, onUploadProgress(progressEvent:
															   AxiosProgressEvent): void {
			LoadProgress.progress((progressEvent.progress as number) * 100);
		}
	}).then(({data: {data: response, error}}) => {
		if (response > 0) {
			ElNotification.info({
									title  : "添加图书",
									message: `共失败${response}条数据`
								});
			return;
		}
		ElNotification.success({
								   title  : "添加图书",
								   message: error
							   });
	}).catch((e) => {
		ElNotification.error({
								 title  : "添加图书",
								 message: "添加失败"
							 });
	});
}

function before() {
	LoadProgress.start();
}

const errorStop: Ref<boolean> = ref<boolean>(true);
</script>
<style scoped>
@import "@/views/css/check_1.css";
</style>