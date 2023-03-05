/** @format */

import type Book       from "@/types/Book";
import { defineStore } from "pinia";

export const useBookStore
				 = defineStore("books", () => {
	const book: { _: Book | null } = shallowReactive({
		_: null
	});
	
	function addBook(b: Book): void {
		book._ = b;
	}
	
	return {_: book, book: computed(() => book), addBook};
}, {
	persist: {
		enabled   : true,
		strategies: [{storage: sessionStorage}]
	}
});
