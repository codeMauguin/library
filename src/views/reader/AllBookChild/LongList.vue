<!-- @format -->

<template>
    <div ref="el" class="content" @scroll="scroll">
        <div
                class=" _____boxed "
        >
            <TransitionGroup name="list" tag="ul">
                <template
                        v-for="item in BooksData.view"
                        :key="item.index"
                >

                    <book-view
                            :cancel="() => shoppingCart.removeAProduct(item.value)"
                            :check-has-borrowed="
					() => readerStore.borrowALibraryCard.borrowedBook.indexOf(item.value.id) > -1
				"
                            :check-has-checked="() => !!shoppingCart._[item.value.id]"
                            :prop="item.value"
                            :style="position(item.index)"
                            class="child"
                            @addAShoppingCart="() => shoppingCart.addToCart(item.value)"
                            @click.prevent="
					(e:Event) => {
            e.preventDefault();
						linkTo(item.value);
					}
				"/>
                </template>
            </TransitionGroup>
        </div>
        <el-backtop
                :bottom="180"
                :right="60"
                style="width: 60px"
                target=".content">
            <button class="btn_top">
                <div class="arrow-up"/>
            </button>
        </el-backtop>
    </div>
</template>
<script lang="ts" setup>
import type ResponseApi            from "@/axios/ResponseApi";

/**
 * > 优化了长列表性能，解决上一个版本的每次点击清除都会去请求下一页问题，完全根据需要可视进行加载
 * todo 设置图片缓存
 */
import LongPage                    from "@/components/Pages/LongPage";
import type { LoadPage, PageData } from "@/components/Pages/Page2";
import router                      from "@/router";
import { useReaderStore }          from "@/stores/readerStore";
import { useShopStore }            from "@/stores/ShopStore";
import type Book                   from "@/types/Book";
import { Assert }                  from "@/utils";
import { default as BookView }     from "@/views/reader/Book.vue";
import type { AxiosResponse }      from "axios";
import { watchEffect }             from "vue";
import type { Ref }                from "vue";
import { ref }                     from "vue";

const shoppingCart = useShopStore();
const readerStore = useReaderStore();
const store = useBookStore();
const el: Ref<HTMLElement | null> = ref(null);
const width: number = 200;
const height: number = 300;

const clientHeight: Ref<number> = ref(document.body.clientHeight);
const column: Ref<number> = ref<number>(1);
const gap: Ref<number> = ref<number>(10);
const gap_x = 10;//按照当前的计算保证间距不会很小
const gap_y = 10;//设置上下间距
const clientTop: Ref<number> = ref<number>(0);//客户端的距离顶部的高度
const clientScrollHeight: Ref<string> = ref("0px");
const BooksData: LongPage<Book> = new LongPage<Book>(
	new (class implements LoadPage<Book> {
		public load(offset: number, pageSize: number, size: number, last?: Book): Promise<PageData<Book>> {
			return cacheInstance
				.post(
					HttpURL.getAllBooks,
					{
						pageInfo: {
							offset,
							pageSize,
							size,
							id: last?.id
						}
					},
					{
						group: "BOOK"
					}
				)
				.then(({data: {data}}: AxiosResponse<ResponseApi<PageData<Book>>>) => data);
		}
	})()
);


function linkTo(book: Book): void {
	store.addBook(book);
	router.push({
					name  : `book`,
					params: {id: book.id}
				});
}

/**
 * @fixme 03-18 宽度变化和总高度不一致导致可视区域无渲染元素
 * @param {number} index
 * @return {{[p: string]: string}}
 */
function position(index: number): { [key: string]: string } {
	const top: number = Math.floor(index / column.value);
	const curCol: number = index - (top * column.value);
	gap.value = (el.value!.clientWidth - column.value * width) / (column.value + 1);
	return {
		transform: `translate(${curCol * width + curCol * gap.value}px,${
			top * height + top * gap_y
		}px)`
	};
}

const throttle_mounted = throttle(mounted, 350);
const throttle_resize = throttle(() => {
	column.value = Math.floor((el.value!.clientWidth + gap_x) / (gap_x + width));
	mounted();
}, 300, 0);
const resizeObserver: ResizeObserver = new ResizeObserver(entries => {
	throttle_resize();
});


const scroll = throttle_mounted;

defineExpose<{ refresh: () => void; search: (virtual: Book[]) => void }>({
																			 refresh() {
																				 const [start, end] = getRange();
																				 BooksData.virtualTo(null, start, end);
																			 },
																			 search(virtual: Book[]) {
																				 BooksData.virtualTo(
																					 new LocalVirtualPage<Readonly<{
																						 value: Book,
																						 index: number
																					 }>>(virtual.map((value,
																									  index) =>
																										 ({
																											 value,
																											 index
																										 })),
																						 BooksData.pageInfo),
																					 0, virtual.length);
																			 }
																			 
																		 });


/**
 * 加载页面容纳的列和行数
 */

function mounted(): Promise<void> | void {
	if (Assert.isNull(el.value)) return;
	clientTop.value = el.value!.scrollTop;
	clientHeight.value = el.value!.offsetHeight === 0 ? document.body.clientHeight : el.value!.offsetHeight;
	updateToView();
}

onActivated(() => {
	throttle_resize();
});

onMounted(throttle_resize);
onMounted(() => {
	resizeObserver.observe(el.value as HTMLElement);
});


//收集元素变化计算页面长度变化
watchEffect(() => {
	const row = Math.ceil(Math.max(0, BooksData.pageInfo.totalSize) / column.value);
	clientScrollHeight.value = `${(row * (gap_y + height) - gap_y)}px`;
});


function getRange(): number[] {
	const col = column.value;
	const topRow = Math.floor((clientTop.value + gap_y) / (gap_y + height));//在顶部的行数
	const start: number = Math.max(0, topRow) * col;
	const end: number = (Math.ceil(
		(clientTop.value + gap_y + clientHeight.value) / (gap_y + height))) * col;//计算当前区域底部行
	return [start, BooksData.pageInfo.totalSize !== -1 ? Math.min(end, BooksData.pageInfo.totalSize) : end];
	
}

function updateToView(): void {
	const [start, end] = getRange();
	BooksData.updateToView(start, end).then(() => {
	});
}

</script>

<style scoped>
@import "@/views/reader/AllBookChild/btn_top.css";

.content {
    overflow-y: auto;
    height: v-bind(clientScrollHeight);
    position: relative;
}

._____boxed {
    height: v-bind(clientScrollHeight);
    width: 100%;
}


.child {
    position: absolute;
    top: 0;
    left: 0;
    transition: all 0.3s;
}

.list-move, /* 对移动中的元素应用的过渡 */
.list-enter-active,
.list-leave-active {
    transition: all 0.5s ease;
}

.list-enter-from,
.list-leave-to {
    opacity: 0;
}

/* 确保将离开的元素从布局流中删除
  以便能够正确地计算移动的动画。 */
.list-leave-active {
    position: absolute;
}


</style>