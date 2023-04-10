<template>
    <div class="body-parent">
        <div class="reader-body">
            <div class="reader-header">
                <div style="display: flex; align-items: center;  color: #999;
    font-size: 14px;">
                    <h3>用户：{{ checkName(data.user.username) }}</h3>
                    <div
                            v-if="data.parent && data.parent.id !== data.root"
                            style="display: flex; align-items: center">
                        <span style="font-size: 12px; user-select: none">&nbsp;回复&nbsp;</span>
                        <h3>{{ checkName(data.parent.user.username) }}</h3>
                    </div>
                </div>
                <div class="operator">
                    <el-space alignment="normal" size="large">
                        <el-badge :value="likeCount">
                            <label class="container">
                                <input v-model="isLike" type="checkbox"
                                       @change="like">
                                <svg id="Glyph" version="1.1" viewBox="0 0 32 32" xml:space="preserve"
                                     xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink"><path id="XMLID_254_" d="M29.845,17.099l-2.489,8.725C26.989,27.105,25.804,28,24.473,28H11c-0.553,0-1-0.448-1-1V13  c0-0.215,0.069-0.425,0.198-0.597l5.392-7.24C16.188,4.414,17.05,4,17.974,4C19.643,4,21,5.357,21,7.026V12h5.002  c1.265,0,2.427,0.579,3.188,1.589C29.954,14.601,30.192,15.88,29.845,17.099z"></path>
                                    <path id="XMLID_256_"
                                          d="M7,12H3c-0.553,0-1,0.448-1,1v14c0,0.552,0.447,1,1,1h4c0.553,0,1-0.448,1-1V13C8,12.448,7.553,12,7,12z   M5,25.5c-0.828,0-1.5-0.672-1.5-1.5c0-0.828,0.672-1.5,1.5-1.5c0.828,0,1.5,0.672,1.5,1.5C6.5,24.828,5.828,25.5,5,25.5z"></path></svg>
                            </label>
                        </el-badge>
                        <el-badge :value="unLikeCount">
                            <label class="container un_like" style="transform: rotate(180deg) scale(.8);">
                                <input v-model="isNotLike" type="checkbox"
                                       @change="unLike">
                                <svg id="Glyph" version="1.1" viewBox="0 0 32 32" xml:space="preserve"
                                     xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink"><path id="XMLID_254_" d="M29.845,17.099l-2.489,8.725C26.989,27.105,25.804,28,24.473,28H11c-0.553,0-1-0.448-1-1V13  c0-0.215,0.069-0.425,0.198-0.597l5.392-7.24C16.188,4.414,17.05,4,17.974,4C19.643,4,21,5.357,21,7.026V12h5.002  c1.265,0,2.427,0.579,3.188,1.589C29.954,14.601,30.192,15.88,29.845,17.099z"></path>
                                    <path id="XMLID_256_"
                                          d="M7,12H3c-0.553,0-1,0.448-1,1v14c0,0.552,0.447,1,1,1h4c0.553,0,1-0.448,1-1V13C8,12.448,7.553,12,7,12z   M5,25.5c-0.828,0-1.5-0.672-1.5-1.5c0-0.828,0.672-1.5,1.5-1.5c0.828,0,1.5,0.672,1.5,1.5C6.5,24.828,5.828,25.5,5,25.5z"></path></svg>
                            </label>
                        </el-badge>
                        <button v-if="userInfoStore.user.id.localeCompare(data.user.id) === 0"
                                class="del-button " @click="deleteComments">
                            <i class="iconfont icon-shanchu1"></i>
                        </button>
                    </el-space>

                </div>
            </div>
            <span class="time">评论时间：
				{{
								formatTime(data.timestamp)
                }}
			</span>
            <el-space
                    alignment="normal"
                    direction="vertical">
				<pre

                style="cursor: pointer;font-size: 18px"
                @click="commentShow = !commentShow"
                v-html="data.content"></pre>
                <!--        回复评论-->
                <el-collapse-transition>
                    <div
                            v-if="commentShow"
                            class="replyToComments transition-box">
                        <el-space
                                style="width: 100%">
                            <el-input v-model="comments"
                                      :placeholder="'回复 ' + data.user.username + ': '"
                                      class="replyToComments transition-box"
                                      type="textarea"/>
                            <el-button
                                    size="large"
                                    type="primary"
                                    @click="commit">
                                <template #icon>
                                    <i class="icon-pinglunxiao iconfont"/>
                                </template>
                                评论
                            </el-button>
                        </el-space>
                    </div>
                </el-collapse-transition>
            </el-space>
            <MyCollapse
                    v-if="data.child > 0"
                    ref="collapseRef"
                    :cur="data.children?.length ?? 0"
                    :total="Math.max(data.children?.length ?? 0, data.child)"
                    @change="handle">
                <template #body>
                    <TransitionGroup name="list">
                        <template
                                v-for="(item, index) in data!.children"
                                :key="item.id">
                            <comment
                                    :data="item"
                            />
                        </template>
                    </TransitionGroup>
                </template>
            </MyCollapse>
        </div>
    </div>
</template>

<script lang="ts" setup>
//TODO 评论后更新评论列表 后台使用消息队列给用户发送消息 删除消息
//TODO 将评论的子节点在点击展开后开始加载

import instance                    from "@/axios";
import type ResponseApi            from "@/axios/ResponseApi";
import { default as MyCollapse }   from "@/components/collapse/index.vue";
import CommentType, { returnType } from "@/types/CommentType";
import { commit as c }             from "@/utils/Gloab";
import oncePromise                 from "@/utils/OncePromise";
import type { AxiosResponse }      from "axios";
import { ElMessage, ElMessageBox } from "element-plus";

const isLike = ref<boolean>(false);
const isNotLike = ref<boolean>(false);

const likeCount = ref<number>(0);
const unLikeCount = ref<number>(0);
const props = defineProps<{ data: CommentType }>();

const collapseRef = ref<InstanceType<typeof MyCollapse> | null>(null);

const userInfoStore = useUserInfoStore();
onBeforeMount(() => {
	isLike.value = props.data.isLike;
	isNotLike.value = props.data.isNotLike;
	likeCount.value = props.data.likeCount;
	unLikeCount.value = props.data.unLikeCount;
});


// 返回用户名称的函数。
function checkName(name: string): string {
	return name === store.user.name ? "我" : name;
}

const patch = oncePromise(instance.patch);
const promise = oncePromise(instance.post);
const getPromise = oncePromise(instance.get);

function like() {
	if (!userInfoStore.state) {
		isLike.value = false;
		messageLinkTo("尚未登陆,点击跳转", "/login");
		return;
	}
	const commentId = props.data.id;
	//发送请求
	const url = !isLike.value
		? HttpURL.COMMENT_CANCEL : HttpURL.COMMENT_LIKE;
	patch(url, commentId, {
		headers: {
			"content-type": "application/json"
		}
	}).then(({data: {data}}) => {
		if (!isLike.value) {
			likeCount.value--;
			props.data.likeCount--;
		} else {
			props.data.likeCount++;
			likeCount.value++;
		}
		if (isLike.value && isNotLike.value) {
			unLikeCount.value--;
			props.data.unLikeCount--;
			isNotLike.value = false;
		}
	});
}

function formatTime(time: Date): string {
	const now = new Date();
	const diff: number = (now.getTime() - time.getTime()) / 1000; // 计算时间差，单位为秒
	if (diff < 60) {
		return "刚刚";
	} else if (diff < 60 * 60) {
		return `${Math.floor(diff / 60)}分钟前`;
	} else if (diff < 60 * 60 * 24) {
		return `${Math.floor(diff / 60 / 60)}小时前`;
	} else {
		return `${Math.floor(diff / 60 / 60 / 24)}天前`;
	}
}

function unLike() {
	if (!userInfoStore.state) {
		isNotLike.value = false;
		messageLinkTo("尚未登陆,点击跳转", "/login");
		return;
	}
	const commentId = props.data.id;
	const id = userInfoStore.user.id;
	//发送请求
	const url = !isNotLike.value
		? HttpURL.COMMENT_CANCEL
		: HttpURL.COMMENT_UNLIKE;
	patch(url, commentId, {
		headers: {
			"content-type": "application/json"
		}
	}).then(({data: {data}}) => {
		if (!isNotLike.value) {
			unLikeCount.value--;
			props.data.unLikeCount--;
		} else {
			props.data.unLikeCount++;
			unLikeCount.value++;
		}
		if (isNotLike.value && isLike.value) {
			likeCount.value--;
        props.data.likeCount--;
			isLike.value = false;
		}
	});
}

function deleteComments() {
	//判断当前评论是否是自己的
	if (userInfoStore.user.id.localeCompare(props.data.user.id) === 0) {
		ElMessageBox({
						 title            : "删除评论",
						 boxType          : "confirm",
						 message          : h("pre", null, [
							 "是否删除",
							 h("span", {style: {color: "red", fontWeight: "bold"}}, props.data.content)
						 ]),
						 showCancelButton : true,
						 cancelButtonText : "取消",
						 confirmButtonText: "删除"
					 })
			.then(() => {
				patch(`${HttpURL.deleteComment}/${props.data.id}/${userInfoStore.user.id}`)
					.then(value => {
						ElMessage.success("删除成功");
						deleteComment(props.data.id);
					})
					.catch(error => {
						ElMessage.warning("删除失败:" + error.data.error);
					});
			})
			.catch(reason => {
			});
	}
}

const {updateView, deleteComment} = inject<{
	updateView: (...arg: any[]) => void, deleteComment: (...arg: any[]) =>
		void
}>("comment") as { updateView: (...arg: any[]) => void, deleteComment: (...arg: any[]) => void };
const comments = ref<string>("");
const commentShow = ref<boolean>(false);
const store = useUserInfoStore();
const commit = () => {
	c(comments.value, props.data.book.bookId, (book) => {
		updateView(book);
	}, props.data, props.data.root ?? props.data.id);
	comments.value = "";
	commentShow.value = false;
	
};


/**
 * 用于加载当前评论的子评论的函数。
 */
async function handle(): Promise<void> {
	try {
		const {
				  data: {data}
			  }: AxiosResponse<ResponseApi<returnType[]>> = await getPromise(
			`${HttpURL.getCommentChildren}`,
			{
				params: {
					id    : props.data.id,
					userId: userInfoStore.user.id
				}
			}
		);
		const commentStore = useCommentStore();
		const record: WeakMap<CommentType, string | undefined> = new WeakMap<
			CommentType,
			string | undefined
		>();
		const cache: Record<string, CommentType> = {};
		const children = data.map((e: returnType) => {
			const comment: CommentType = map(e);
			record.set(comment, e.parentId);
			cache[comment.id] = comment;
			return comment;
		});
		children.forEach((c: CommentType): void => {
			const parentId: string | undefined = record.get(c);
			c.parent = parentId ? cache[parentId] : undefined;
		});
		commentStore.updateComment(props.data.book.bookId, children, props.data.id);
		collapseRef.value?.closeLoading();
	} catch (e: any) {
		ElMessage.warning(e.data.error);
		return;
	}
}
</script>

<style scoped>
@import url("@/views/reader/Comments/btn_del.css");
@import url("@/views/css/check_like.css");

.body-parent {
    background-color: #f3f3f3;
    color: #333;
    border: 1px dashed #ccc;
    padding: 10px;
    display: -webkit-flex;
    display: flex;
    flex-wrap: nowrap;
    align-items: flex-start;
}

.el-divider {
    margin-top: 0;
}

.el-avatar {
    width: 40px;
    height: 40px;
    user-select: none;
}

.reader-body {
    flex: 1;
    padding: 10px;
    display: flex;
    flex-direction: column;
}

:deep(.el-space) {
    vertical-align: baseline;
}

.reader-header {
    display: flex;
    flex-wrap: nowrap;
    flex-direction: row;
    justify-content: space-between;
    height: 25px;
}

.operator {
    cursor: pointer;
}

h3 {

    cursor: default;
    user-select: none;
}

.icon {
    font-size: 18px;
}

.replyToComments {
    display: flex;
    display: -webkit-flex;
}

.time {
    color: #767575;
    user-select: none;
    font-size: 8px;
    height: 30px;
    align-items: center;
    display: flex;
}

.list-move, /* 对移动中的元素应用的过渡 */
.list-enter-active,
.list-leave-active {
    transition: all 0.5s ease;
}

.list-enter-from,
.list-leave-to {
    opacity: 0;
    transform: translateX(30px);
}

/* 确保将离开的元素从布局流中删除
  以便能够正确地计算移动的动画。 */
.list-leave-active {
    position: absolute;
}


</style>