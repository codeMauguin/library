<!-- @format -->

<script lang="ts" setup>

const target = ref<HTMLInputElement>();
const preview = ref<HTMLImageElement>();
const isOpen = ref<boolean>(false);
const emits = defineEmits<{
	(e: "upload", file: File | null, name: string | null): void;
}>();
const files: File[] = reactive<File[]>([]);
const urls: string[] = reactive<string[]>([]);
const props = withDefaults(defineProps<{ disable?: boolean }>(), {
	disable: false
});

function imageLoad(a: HTMLImageElement | Event) {
	URL.revokeObjectURL((a as HTMLImageElement).src);
}

function handle() {
	if (props.disable || files.length > 0) return;
	target.value?.click(); //点击
}

defineExpose<{ clear(): void }>({
	                                clear: function (): void {
		                                urls.splice(0, urls.length);
		                                files.splice(0, files.length);
		                                isOpen.value = false;
		                                hover.value = "";
	                                }
                                });

function change(e: Event) {
	if ((<HTMLInputElement>e.target).files?.length === 0) return;
	const file = (<HTMLInputElement>e.target).files?.item(0);
	if (!file) return;
	omit(file);
	files.push(file);
	urls.push(URL.createObjectURL(file as Blob));
	hover.value = "preview";
}

const hover = ref<string>("");
const omit = (uploadFile: File): void => {
	emits("upload", <File>uploadFile, uploadFile.name);
	isOpen.value = true;
};
</script>
<template>
    <div>
        <transition-group
                class="container-view"
                name="slide-fade"
                tag="div">
            <div
                    v-for="item in urls"
                    :key="item"
                    :class="hover">
                <el-space
                        style="position: absolute"
                        wrap>
                    <i
                            class="iconfont icon-shanchu del"
                            @click="
							() => {
								urls.length = 0;
								files.length = 0;
								isOpen = false;
							}
						"></i>
                </el-space>
                <img
                        ref="preview"
                        :src="item"
                        alt=""
                        @load="(e: Event | HTMLImageElement) => imageLoad(e)"/>
            </div>
        </transition-group>
        <div
                v-if="!isOpen"
                class="image-upload"
                @click.once="handle">
            <slot name="default">
                <i class="iconfont icon-tianjiatupian input-center"> </i>
            </slot>
            <input
                    ref="target"
                    accept="image/*"
                    class="upload-input"
                    type="file"
                    @change="change"/>
        </div>
    </div>
</template>

<style scoped>
.upload-input {
    opacity: 0;
    display: none;
}

.image-upload:hover {
    border: var(--el-color-primary) 1px dashed;
}

.image-upload {
    --el-upload-picture-card-size: 148px;
    background-color: var(--el-fill-color-lighter);
    border: 1px dashed var(--el-border-color-darker);
    border-radius: 6px;
    box-sizing: border-box;
    width: var(--el-upload-picture-card-size);
    height: var(--el-upload-picture-card-size);
    cursor: pointer;
    vertical-align: top;
    display: inline-flex;
    justify-content: center;
    align-items: center;
}

.preview {
    position: relative;
    user-select: none;
    --el-upload-picture-card-size: 148px;
    border: 1px solid var(--el-border-color-darker);
    border-radius: 6px;
    box-sizing: border-box;
    width: var(--el-upload-picture-card-size);
    height: var(--el-upload-picture-card-size);
    cursor: pointer;
    display: inline-flex;
    align-items: center;
    justify-content: center;
}

.preview:hover {
    background-color: rgba(0, 0, 0, .5);
}

.preview:hover img {
    z-index: -1;
}

.del:hover {
    color: var(--el-color-primary);
}

.preview img {
    display: inline-block;
    width: 100%;
    height: 100%;
    object-fit: contain;
    z-index: 0;
}

.input-center {
    font-size: 28px;
    color: var(--el-text-color-secondary);
}

/*
Enter and leave animations can use different
durations and timing functions.
*/
.slide-fade-enter-active {
    transition: all 0.3s ease-out;
}

.slide-fade-leave-active {
    transition: all 0.1s ease-in;
}

.slide-fade-leave-active {
    position: absolute;
}

.slide-fade-leave-from,
.slide-fade-enter-to {
    transform: translateY(0);
}

.slide-fade-leave-to,
.slide-fade-enter-from {
    transform: translateY(-90px);
}
</style>
