<template>
    <div ref="client" class="box content" @scroll="scroll">
        <template v-for="item in views" :key="item.value">
            <book-view
                    :cancel="() => shoppingCart.removeAProduct(item.value)"
                    :check-has-borrowed="
					() => readerStore.borrowALibraryCard.borrowedBook.indexOf(item.value.id) > -1
				"
                    :check-has-checked="() => !!shoppingCart._[item.value.id]"
                    :prop="item.value"
                    :style="position(item.top,item.left)"
                    class="item"
                    @addAShoppingCart="() => shoppingCart.addToCart(item.value)"
                    @click.prevent="
					() => {
						linkTo(item.value);
					}
				"/>
        </template>
    </div>
</template>

<script lang="ts" setup>
import { LoadPage }            from "@/components/Pages/Page2";
import type { PageData }       from "@/components/Pages/Page2";
import router                  from "@/router";
import { useReaderStore }      from "@/stores/readerStore";
import { useShopStore }        from "@/stores/ShopStore";
import Book                    from "@/types/Book";
import { default as BookView } from "@/views/reader/Book.vue";
import { ref }                 from "vue";
import type { Ref }            from "vue";
import { reactive }            from "vue";

const size: Ref<number> = ref<number>(-1);
//存放页面总数据
const data: Item[] = reactive([]);
//视图数据存放区
const views: Item[] = reactive<Item[]>([]);
//下列数据可以使用props传递，这里直接我定义了
//定义容器宽高和间隔
const props = {
	width : 200,
	height: 300,
	space : 10
};
const loader: LoadPage<Book> = new (class implements LoadPage<Book> {
	public load(
		offset: number,
		pageSize: number,
		size: number,
		last?: Book
	): Promise<PageData<Book>> {
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
			.then(({data: {data}}) => data);
	}
})();

const clientTop: Ref<number> = ref<number>(0);//客户端的距离顶部的高度

interface Item {
	top: number;
	left: number;
	value: any;
}

const client: Ref<HTMLElement | null> = ref<HTMLElement | null>(null);
const clientWidth: Ref<number> = ref<number>(0);//客户端的宽度
const clientHeight: Ref<number> = ref<number>(0);//客户端高度
onMounted(() => {
	clientWidth.value = client.value!.clientWidth;//将客户端的宽度加载
	loader.load(0, 1, size.value).then(value => {
		size.value = value.size;
		const col = Math.floor((clientWidth.value + props.space) / (props.width + props.space));//计算出当前有多少列
		for (let i = 0; i < 1; ++i) {
			const top: number = Math.floor(i / col);
			const curCol: number = i - (top * col);
			data[i] = {
				top  : top * props.height + top * props.space,
				left : curCol * props.width + curCol * props.space,
				value: value.value[i]
			};
		}
		views.splice(0, views.length, ...data.slice(0, 1));
	});
});
//更新容器高度的副作用
watchEffect(() => {
	//数据总长度 除以一行列数
	const row: number = Math.ceil(
		size.value / Math.floor((clientWidth.value + props.space) / (props.width + props.space)));
	clientHeight.value = Math.max(row * props.height + props.space * (row - 1), 0);
});
//顶部变动副作用
watchEffect(() => {
	updateToView();
});

function position(top: number, left: number): { [key: string]: string } {
	return {
		transform: `translate(${left}px,${
			top
		}px)`
	};
}

const scroll = throttle((e: Event): void => { clientTop.value = (e.target as HTMLElement).scrollTop;}, 1000);

function updateToView(): void {
	const [start, end] = getRange();
	const length: number = Math.min(size.value, end);
	//检查当前data是否加载
	A:  {
		for (let i = start; i < length; ++i) {
			if (Assert.isNull(data[i])) {
				//需要加载
				break A;
			}
		}
		views.splice(0, views.length, ...data.slice(start, end));
		return;
	}
	//更新视角
	loader.load(start, end - start, size.value === 0 ? -1 : size.value).then(value => {
		size.value = value.size ?? size.value;
		const col = Math.floor((clientWidth.value + props.space) / (props.width + props.space));//计算出当前有多少列
		for (let i = start; i < length; ++i) {
			const top: number = Math.floor(i / col);
			const curCol: number = i - (top * col);
			data[i] = {
				top  : top * props.height + top * props.space,
				left : curCol * props.width + curCol * props.space,
				value: value.value[i - start]
			};
		}
		views.splice(0, views.length, ...data.slice(start, end));
	});
	
}


/**
 *
 * 计算可视区域
 * @return {number[]}
 */
function getRange(): number[] {
	//计算可视区域切割长度
	const col = Math.floor((clientWidth.value + props.space) / (props.width + props.space));//计算出当前有多少列
	const topRow = Math.max(0, Math.floor((clientTop.value + props.space) / (props.space + props.height)));//计算当前区域开始行
	const start: number = topRow * col;
	const viewRow: number = Math.floor((clientHeight.value + props.space) / (props.space + props.height)) + 1;
	const end: number = (topRow + viewRow) * col;
	return [start, end];
}


const shoppingCart = useShopStore();
const readerStore = useReaderStore();
const store = useBookStore();

function linkTo(book: Book): void {
	store.addBook(book);
	router.push({
					name  : `book`,
					params: {id: book.id}
				});
}

</script>

<style scoped>
.box {
    height: v-bind(clientHeight+ 'px');
    position: relative;
    overflow: auto;
}

.item {
    position: absolute;
    top: 0;
    left: 0;
}
</style>