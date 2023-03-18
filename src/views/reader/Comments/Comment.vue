<template>
    <div class="body-parnet">
        <!--        <el-avatar-->
        <!--                :size="60"-->
        <!--                :src="data.user.headerImage">-->
        <!--            <img-->
        <!--                    :alt="data.user.username"-->
        <!--                    src="https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png"/>-->
        <!--        </el-avatar>-->
        <div class="reader-body">
            <div class="reader-header">
                <div style="display: flex; align-items: center">
                    <h3>{{ checkName(data.user.username) }}</h3>
                    <div
                            v-if="data.parent && data.parent.id !== data.root"
                            style="display: flex; align-items: center">
                        <span style="font-size: 12px; user-select: none">&nbsp;回复&nbsp;</span>
                        <h3>{{ checkName(data.parent.user.username) }}</h3>
                    </div>
                </div>
                <div class="operator">
                    <el-space>
                        <el-badge :value="likeCount">
                            <label class="container">
                                <input v-model="isLike" :disabled="!userInfoStore.state" type="checkbox" @change="like">
                                <div class="checkmark">
                                    <svg fill="none" viewBox="0 0 24 24">
                                        <path d="M8 10V20M8 10L4 9.99998V20L8 20M8 10L13.1956 3.93847C13.6886 3.3633 14.4642 3.11604 15.1992 3.29977L15.2467 3.31166C16.5885 3.64711 17.1929 5.21057 16.4258 6.36135L14 9.99998H18.5604C19.8225 9.99998 20.7691 11.1546 20.5216 12.3922L19.3216 18.3922C19.1346 19.3271 18.3138 20 17.3604 20L8 20"
                                              stroke="#FFFFFF" stroke-linecap="round" stroke-linejoin="round"
                                              stroke-width="1.3"></path>
                                    </svg>
                                </div>
                            </label>
                        </el-badge>
                    </el-space>
                    <el-space>
                        <el-badge :value="unLikeCount">
                            <label class="container" style="transform: rotate(180deg) translateY(40%);">
                                <input v-model="isNotLike" :disabled="!userInfoStore.state" type="checkbox"
                                       @change="unLike">
                                <div class="checkmark">
                                    <svg fill="none" viewBox="0 0 24 24">
                                        <path d="M8 10V20M8 10L4 9.99998V20L8 20M8 10L13.1956 3.93847C13.6886 3.3633 14.4642 3.11604 15.1992 3.29977L15.2467 3.31166C16.5885 3.64711 17.1929 5.21057 16.4258 6.36135L14 9.99998H18.5604C19.8225 9.99998 20.7691 11.1546 20.5216 12.3922L19.3216 18.3922C19.1346 19.3271 18.3138 20 17.3604 20L8 20"
                                              stroke="#FFFFFF" stroke-linecap="round" stroke-linejoin="round"
                                              stroke-width="1.3"></path>
                                    </svg>
                                </div>
                            </label>
                        </el-badge>
                    </el-space>

                    <button v-if="userInfoStore.user.id.localeCompare(data.user.id) === 0" class="btn"
                            @click="deleteComment">
                        <svg class="icon" height="17.5" viewBox="0 0 15 17.5" width="15"
                             xmlns="http://www.w3.org/2000/svg">
                            <path id="Fill"
                                  d="M15,18.75H5A1.251,1.251,0,0,1,3.75,17.5V5H2.5V3.75h15V5H16.25V17.5A1.251,1.251,0,0,1,15,18.75ZM5,5V17.5H15V5Zm7.5,10H11.25V7.5H12.5V15ZM8.75,15H7.5V7.5H8.75V15ZM12.5,2.5h-5V1.25h5V2.5Z"
                                  transform="translate(-2.5 -1.25)"></path>
                        </svg>
                    </button>
                </div>
            </div>
            <span class="time">
				{{
								formatTime(data.timestamp)
                }}
			</span>
            <el-space
                    alignment="normal"
                    direction="vertical">
				<pre
                style="cursor: pointer"
                @click="commentShow = !commentShow"
                v-html="data.content"></pre>
                <!--        回复评论-->
                <el-collapse-transition>
                    <div
                            v-if="commentShow"
                            class="replyToComments transition-box">
                        <el-row
                                :gutter="10"
                                style="width: 100%">
                            <el-col :span="20">
                                <el-input
                                        v-model="comments"
                                        :placeholder="'回复 ' + data.user.username + ': '"/>
                            </el-col>
                            <el-col :span="4">
                                <el-button
                                        type="primary"
                                        @click="commit">
                                    <template #icon>
                                        <i class="icon-pinglunxiao iconfont"/>
                                    </template>
                                    评论
                                </el-button>
                            </el-col>
                        </el-row>
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
                    <template
                            v-for="(item, index) in data.children"
                            :key="item.id">
                        <comment
                                :data="item"
                                @updateView="d => emits('updateView', d)"
                                @delete-comment="id => emits('deleteComment', id)"/>
                    </template>
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

const emits = defineEmits<{
	(e: "updateView", p: CommentType): void;
	(e: "deleteComment", p: string): void;
}>();

// 返回用户名称的函数。
function checkName(name: string): string {
	return name === store.user.name ? "我" : name;
}

const patch = oncePromise(instance.patch);
const promise = oncePromise(instance.post);
const getPromise = oncePromise(instance.get);

function like() {
	if (!userInfoStore.state) {
		messageLinkTo("尚未登陆", "/login");
		return;
	}
	const commentId = props.data.id;
	//发送请求
	const url = isLike.value
		? HttpURL.COMMENT_CANCEL : HttpURL.COMMENT_LIKE;
	patch(url, commentId, {
		headers: {
			"content-type": "application/json"
		}
	}).then(({data: {data}}) => {
		if (!isLike.value) {
			likeCount.value--;
		} else {
			likeCount.value++;
		}
		if (isLike.value && isNotLike.value) {
			unLikeCount.value--;
			isNotLike.value = false;
		}
	});
}

function formatTime(time: Date): string {
	console.log(time);
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
		messageLinkTo("尚未登陆", "/login");
		return;
	}
	const commentId = props.data.id;
	const id = userInfoStore.user.id;
	//发送请求
	const url = isNotLike.value
		? HttpURL.COMMENT_CANCEL
		: HttpURL.COMMENT_UNLIKE;
	patch(url, commentId, {
		headers: {
			"content-type": "application/json"
		}
	}).then(({data: {data}}) => {
		if (!isNotLike.value) {
			unLikeCount.value--;
		} else {
			unLikeCount.value++;
		}
		if (isNotLike.value && isLike.value) {
			likeCount.value--;
			isLike.value = false;
		}
	});
}

function deleteComment() {
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
						emits("deleteComment", props.data.id);
					})
					.catch(error => {
						console.log(error);
						ElMessage.warning("删除失败:" + error.data.error);
					});
			})
			.catch(reason => {
			});
	}
}

const comments = ref<string>("");
const commentShow = ref<boolean>(false);
const store = useUserInfoStore();

function commit() {
	if (!userInfoStore.state) {
		messageLinkTo("尚未登陆", "/login");
		return;
	}
	//评论为空
	if (comments.value.length === 0) return;
	commitComment(store.user.id, props.data.book.bookId, comments.value, props.data.id)
		.then(({data: {data}}: AxiosResponse<ResponseApi<string>>) => {
			emits("updateView", {
				parent     : props.data,
				root       : props.data.root ?? props.data.id,
				user       : {
					username   : store.user.name,
					id         : store.user.id,
					headerImage: ""
				},
				id         : data,
				children   : undefined,
				content    : comments.value,
				unLikeCount: 0,
				likeCount  : 0,
				timestamp  : new Date(),
				book       : {
					bookId: props.data.book.bookId
				},
				isLike     : false,
				isNotLike  : false,
				child      : 0
			});
		})
		.catch(({data: {error}}) => {
			ElMessage.warning(error);
		})
		.finally(() => {
			comments.value = "";
			commentShow.value = false;
		});
}

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
			record.set(comment, e.parent_id);
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

.body-parnet {
    border: 1px solid #ccc;
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
    justify-content: space-between;
}

.time {
    color: #767575;
    user-select: none;
    font-size: 12px;
    height: 30px;
    align-items: center;
    display: flex;
}
</style>