<!-- @format -->

<template>
	<div
		ref="el"
		class="infinite-list content"
		infinite-scroll-delay="100"
		infinite-scroll-distance="1000"
		@scroll="scroll">
		<template
			v-for="(book, i) in BooksData.view"
			:key="book.id">
			<book-view
				v-show="isEnd(i) || show(i)"
				:cancel="() => shoppingCart.removeAProduct(book)"
				:check-has-borrowed="
					() => readerStore.borrowALibraryCard.borrowedBook.indexOf(book.id) > -1
				"
				:check-has-checked="() => !!shoppingCart._[book.id]"
				:prop="book"
				:style="position(i)"
				class="child"
				@addAShoppingCart="() => shoppingCart.addToCart(book)"
				@click.prevent="
					() => {
						linkTo(book);
					}
				" />
		</template>
		<el-backtop
			:bottom="180"
			:right="60"
			style="width: 60px"
			target=".infinite-list">
			<button class="btn_top">
				<div class="arrow-up" />
			</button>
		</el-backtop>
	</div>
</template>
<script lang="ts" setup>
	import LongPage from "@/components/Pages/LongPage";
	import type { PageInstance } from "@/components/Pages/Page2";
	import type { LoadPage, PageData } from "@/components/Pages/Page2";
	import router from "@/router";
	import { useReaderStore } from "@/stores/readerStore";
	import { useShopStore } from "@/stores/ShopStore";
	import type Book from "@/types/Book";
	import { default as BookView } from "@/views/reader/Book.vue";
	import type { Ref } from "vue";
	import { ref } from "vue";

	const shoppingCart = useShopStore();
	const readerStore = useReaderStore();
	const store = useBookStore();
	const el: Ref<HTMLElement> = <Ref<HTMLElement>>ref<HTMLElement>();

	function linkTo(book: Book): void {
		store.addBook(book);
		router.push({
			name: `book`,
			params: { id: book.id }
		});
	}

	function position(index: number): { [key: string]: string } {
		const curRow: number = Math.floor(index / column.value);
		const curColumn: number = index - curRow * column.value;
		return {
			transform: `translate(${space.value * (curColumn + 1) + 200 * curColumn}px,${
				space.value * (1 + curRow) + 300 * curRow
			}px)`
		};
	}

	onActivated(() => {
		flush();
	});

	const BooksData: PageInstance<Book> = new LongPage<Book>(
		new (class implements LoadPage<Book> {
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
					.then(({ data: { data } }) => data);
			}
		})()
	);

	const up: Ref<number> = ref<number>(0);
	const down: Ref<number> = ref<number>(0);
	const isEnd = (index: number): boolean => {
		return index >= BooksData.view.length - column.value;
	};
	const scroll = throttle((e: Event) => {
		const target: HTMLElement = e.target as HTMLElement;
		up.value = Math.floor(target.scrollTop / (300 + space.value));
		row.value = Math.ceil(el.value.clientHeight / 300);
		down.value = up.value + row.value;
		if (target.scrollHeight - target.scrollTop <= row.value * 300) {
			load();
		}
	}, 300);

	defineExpose<{ refresh: () => void; search: (virtual: Book[]) => void }>({
		refresh() {
			BooksData.virtual();
		},
		search(virtual: Book[]) {
			BooksData.virtual(new LocalVirtualPage(virtual, BooksData.pageInfo));
		}
	});

	const show = (index: number): boolean => {
		const cur: number = Math.floor(index / column.value);
		return cur >= up.value - 2 && cur <= down.value + 2;
	};

	function load(): void {
		const size =
			(Math.ceil((el.value.clientHeight ?? 0) / 300) *
				Math.ceil((el.value.clientWidth ?? 0) / 200)) <<
			1;
		BooksData.pageUpdate(page => ((page.pageSize = size), page));
		BooksData.refresh();
	}

	function flush() {
		if (Assert.isNull(el.value)) return;
		column.value = Math.floor(el.value.clientWidth / 200);
		row.value = Math.ceil(el.value.clientHeight / 300);
		space.value = (el.value.clientWidth - column.value * 200) / (column.value + 1);
		up.value = Math.floor(el.value.offsetTop / (300 + space.value));
		down.value = up.value + row.value;
	}

	const row: Ref<number> = ref<number>(0);
	const column: Ref<number> = ref<number>(0);
	const space: Ref<number> = ref<number>(0);
	const fl: (...args: unknown[]) => any = throttle(flush, 200);
	onMounted(() => {
		fl();
		load();
		const resizeObserver: ResizeObserver = new ResizeObserver(entries => {
			fl();
		});
		resizeObserver.observe(el.value);
	});
</script>

<style scoped>
	@import "@/views/reader/AllBookChild/btn_top.css";

	.content {
		position: relative;
	}

	.child {
		position: absolute;
	}

	.content,
	.child {
		height: 100%;
		scroll-behavior: smooth;
		overflow: auto;
		transition: all 0.5ms linear;
	}
</style>
