<!-- @format -->

<template>
    <div>
        <div
                v-if="data.id.length > 0"
                style="width: 100%; height: auto">
            <div class="comment_title">
                <h1> 图书信息</h1>
            </div>
            <div class="___body">
                <div class="book">
                    <div>
                        <el-skeleton :loading="loading" animated>
                            <template #template>
                                <el-skeleton-item style="width: 200px;height: 300px" variant="image"/>
                            </template>
                            <template #default>
                                <img :src="image" alt="" style="width: 200px;height: 300px" @load="load"/>
                            </template>
                        </el-skeleton>

                    </div>
                    <div class="book_info">
                        <h1 class="book_name">《{{ data?.name }}》</h1>
                        <el-space
                                alignment="normal"
                                direction="vertical">
                            <div class="book_small"
                            ><span>库存：</span>
                                <span
                                >{{
																		(data?.inventory.total ?? 0) -
																		(data?.inventory.borrowed ?? 0)
                                    }}/{{ data?.inventory.total }}
								</span></div
                            >
                            <div class="book_small"> 作者：{{ data?.author }}</div>
                            <div class="book_small"> 分类: {{ data?.category }}</div>
                            <div class="book_small"> 出版社: {{ data?.press }}</div>
                            <div class="book_small"> 上架时间：{{ data?.time }}</div>
                            <div class="book_small">
                                <pre>简介：{{ data?.info }}</pre>
                            </div>
                        </el-space>
                    </div>
                    <div class="option">
                        <el-button-group v-if="!userStore.isAdmin">
                            <el-button
                                    :dark="true"
                                    type="primary"
                                    @click="addShop(data)">
                                <template #icon>
                                    <i class="iconfont icon-gouwuchekong"/>
                                </template>
                                {{ !!shoppingCart._[data.id] ? "打开购物车" : "加入购物车" }}
                            </el-button>
                            <el-button
                                    :dark="true"
                                    type="success"
                                    @click="onsubmit(data)">
                                <template #icon>
                                    <i class="iconfont icon-wodejieyue"/>
                                </template>
                                借阅
                            </el-button>
                        </el-button-group>
                    </div>
                </div>
                <el-divider/>
                <div class="comment_title">
                    <h1 style="margin: 20px 0 20px 0">图书评论</h1>
                </div>
                <div class="comment">
                    <el-card>
                        <template
                                v-for="(mock, index) in pageInstance!.view"
                                v-if="pageInstance && pageInstance.view.length > 0"
                                :key="mock.id">
                            <my-comment
                                    :data="mock"
                                    @update-view="updateView"
                                    @delete-comment="deleteComment"/>
                            <el-divider v-if="index !== pageInstance?.view.length - 1"/>
                        </template>
                        <el-empty
                                v-else
                                description="暂无评论"
                                style="height: 100%"/>
                    </el-card>
                </div>
            </div>
            <div
                    v-if="userStore.state"
                    class="comment_footer">
                <el-input
                        v-model:model-value="commentContent"
                        :rows="3"
                        maxlength="100"
                        placeholder="请友善交流"
                        show-word-limit
                        size="large"
                        type="textarea"/>
                <div style="flex: auto; margin-top: 20px">
                    <el-button @click="() => (commentContent = '')">cancel</el-button>
                    <el-button
                            type="primary"
                            @click="commit">
                        <template #icon>
                            <i class="icon-pinglunxiao iconfont"/>
                        </template>
                        评论
                    </el-button>
                </div>
            </div>
            <Drawer
                    v-model:show="openShop"
                    :books="shoppingCart._"
                    :closed="closed"
                    class="drawer"
                    @delete="(row:Book)=>shoppingCart.removeAProduct(row)"/>
        </div>
        <el-empty
                v-else
                description="书籍不存在"/>
    </div>
</template>
<script lang="ts" setup>
import type { Page }                                 from "@/components/Pages/Page2";
import { useReaderStore }                            from "@/stores/readerStore";
import { useShopStore }                              from "@/stores/ShopStore";
import type Book                                     from "@/types/Book";
import type CommentType                              from "@/types/CommentType";
import { closed, onsubmit, openShop }                from "@/utils/Gloab";
import { commit as c }                               from "@/utils/Gloab";
import LRUCache                                      from "@/utils/LRUCache";
import { default as MyComment }                      from "@/views/reader/Comments/Comment.vue";
import Drawer                                        from "@/views/reader/Drawer.vue";
import { ElDivider, ElEmpty, ElMessageBox, ElSpace } from "element-plus";
import type { Ref, UnwrapNestedRefs }                from "vue";

const image: Ref<string | ArrayBuffer | null> = ref("");
const loading: Ref<boolean> = ref(true);
const load = () => {
	loading.value = false;
};
const data: UnwrapNestedRefs<Book> = reactive<Book>({
														id       : "",
														name     : "",
														author   : "",
														info     : "",
														category : "",
														press    : "",
														inventory: {
															total   : 0,
															borrowed: 0
														},
														state    : 0,
														comments : 0,
														time     : new Date(),
														image    : ""
													});
const route = useRoute();
const shoppingCart = useShopStore();
const userStore = useUserInfoStore();
const store = useCommentStore();
const bookStore = useBookStore();

/**
 * 1、有数据使用缓存
 * 2、无数据请求网络
 * 如果找不到这本书，请返回上一页
 * @param {string} id - 要加载的图书的 ID。
 */
async function loader(id: string): Promise<void> {
	const book: Book | null = bookStore.book._;
	if (book === null || book.id.localeCompare(id) !== 0) {
		const {
				  data: {data: cur}
			  } = await cacheInstance.get(`${HttpURL.getBook}${id}`, {group: "BOOKS"});
		merge(data, cur);
	} else {
		merge(data, book);
	}
	pageInstance.value = store.getBookComment(id, userStore.user.id);
	pageInstance.value?.refresh().then(() => {
		pageInstance.value?.view.sort((b, a) => a.timestamp.getTime() - b.timestamp.getTime());
		pageInstance.value!.pageUpdate((page) => (page.pageSize = pageInstance.value?.pageInfo.totalSize, page));
	});
	const cache: string | undefined = LRUCache.get(data.image as string);
	if (cache === undefined) {
		fetch(data.image as string).then(value => value.blob()).then(value => {
			const reader: FileReader = new FileReader();
			reader.readAsDataURL(value);
			reader.onloadend = function () {
				image.value = reader.result;
				LRUCache.put(data.image as string, image.value as string);
				loading.value = false;
			};
		});
	} else {
		loading.value = false;
		image.value = cache;
	}
}

const readerStore = useReaderStore();

function addShop(book: Book) {
	if (readerStore.borrowALibraryCard.borrowedBook.indexOf(book.id) > -1) {
		ElMessageBox.alert("已经借阅过");
		return;
	}
	if (!!shoppingCart._[book.id]) {
		openShop.value = true;
	} else shoppingCart.addToCart(book);
}

function deleteComment(id: string): void {
	store.deleteComment(route.params.id as string, id);
}

function updateView(comment: CommentType): void {
	store.updateComment(route.params.id as string, comment);
}

// 在安装组件之前调用的生命周期挂钩。
onBeforeMount(() => {
	loader(route.params.id as string);
});
onActivated(() => {
	loader(route.params.id as string);
});
const pageInstance: Ref<Page<CommentType> | null> = shallowRef(null);
const commentContent = ref<string>("");
const commit = () => {
	c(commentContent.value, route.params.id as string, updateView);
	commentContent.value = "";
};

</script>

<style scoped>
.___body {
    width: 100%;
    display: flex;
    height: auto;
    flex-direction: column;
}

.book {
    display: flex;
    flex-direction: row;
}

.book_small {
    font-size: 12px;
}

.book_name {
    margin: 20px 0 20px 0;
}

.book_info {
    margin-left: 10px;
    text-align: left;
}

.comment_title {
    background: white;
    z-index: 10;
    position: sticky;
    top: 0;
}

.comment {
    height: 10%;
    width: 100%;
    overflow: auto;
}

.comment_footer {
    position: sticky;
    width: 30%;
    bottom: 0;
    z-index: 100;
    background-color: white;
}

.option {
    align-self: flex-end;
}
</style>