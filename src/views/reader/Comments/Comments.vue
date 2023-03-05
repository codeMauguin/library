<!-- @format -->

<template>
	<el-drawer
		:append-to-body="true"
		:model-value="defaults.show"
		:title="title"
		direction="rtl"
		size="450"
		@close="() => emits('beClosed')">
		<template #default>
			<div class="comment-content">
				<template v-for="(view, index) in data.view">
					<comment
						:data="view"
						@updateView="updateView"
						@delete-comment="deleteComment" />
					<el-divider v-if="index !== data.view.length - 1" />
				</template>
				<el-empty
					v-if="!data || data.view.length === 0"
					description="暂无评论" />
			</div>
		</template>
		<template #footer>
			<el-input
				v-model="commentContent"
				:rows="5"
				maxlength="100"
				placeholder="评论"
				resize="none"
				show-word-limit
				size="large"
				style="z-index: 100"
				type="textarea" />
			<div style="flex: auto; margin-top: 20px">
				<el-button @click="cancel">cancel</el-button>
				<el-button
					type="primary"
					@click="commit"
					>评论
				</el-button>
			</div>
		</template>
	</el-drawer>
</template>

<script lang="ts" setup>
	import type ResponseApi from "@/axios/ResponseApi";
	import type { Page } from "@/components/Pages/Page2";
	import { useUserInfoStore } from "@/stores/counter";
	import type CommentType from "@/types/CommentType";
	import Comment from "@/views/reader/Comments/Comment.vue";
	import { commitComment } from "@/views/reader/Comments/index";
	import type { AxiosResponse } from "axios";
	import {
		ElButton,
		ElDivider,
		ElDrawer,
		ElEmpty,
		ElInput,
		ElMessage,
		ElMessageBox
	} from "element-plus";
	import { ref, UnwrapNestedRefs } from "vue";

	type CommentInstance = {
		show: boolean;
		title: string;
		bookId: string;
		data: UnwrapNestedRefs<Page<CommentType>>;
	};
	const commentContent = ref<string>("");

	function cancel() {
		emits("beClosed");
		commentContent.value = "";
	}
	onBeforeMount(() => {
		defaults.data.refresh();
	});

	// 用于提交评论的函数。
	function commit() {
		if (commentContent.value.length === 0) return;
		const store = useUserInfoStore();
		if (!store.state) {
			ElMessageBox.alert("登录后在评论");
			return;
		}
		commitComment(store.user.id, defaults.bookId, commentContent.value).then(
			({ data: { code, data, error } }: AxiosResponse<ResponseApi<string>>) => {
				ElMessage({
					grouping: true,
					type: code === 200 ? "success" : "error",
					message: error
				});
				// 加入评论界面
				updateView({
					parent: undefined,
					child: 0,
					root: undefined,
					user: {
						username: store.user.name,
						id: store.user.id,
						headerImage: ""
					},
					id: data,
					children: undefined,
					content: commentContent.value,
					unLikeCount: 0,
					likeCount: 0,
					timestamp: new Date(),
					book: {
						bookId: defaults.bookId
					},
					isLike: false,
					isNotLike: false
				});
				commentContent.value = "";
			}
		);
	}

	function deleteComment(id: string): void {
		useCommentStore().deleteComment(defaults.bookId, id);
	}

	function updateView(data: CommentType): void {
		useCommentStore().updateComment(defaults.bookId, data);
	}

	const defaults = withDefaults(defineProps<CommentInstance>(), {
		show: false
	});
	const emits = defineEmits<(e: "beClosed") => void>();
</script>

<style scoped>
	.comment-content {
		height: 80%;
	}

	.el-divider {
		margin-top: 0;
	}
</style>
