/** @format */

import { useUserInfoStore } from "@/stores/counter";
import { useReaderStore }   from "@/stores/readerStore";
import type book            from "@/types/Book";
import { defineStore }      from "pinia";

export const useShopStore = defineStore(
	"shop",
	() => {
		/* 创建一个响应式的书籍数组。 */
		const shoppingCart: Record<string, book> = reactive<Record<string, book>>({});
		
		/**
		 * 函数 emptyTheCart() {
		 * 对于（Object.keys（shoppingCart）的常量键）{
		 * Reflect.deleteProperty(shoppingCart, key);
		 * }
		 * }
		 *
		 * // 概括：
		 * // 对于 shoppingCart 对象中的每个键，删除该属性。
		 */
		
		function emptyTheCart() {
			for (const key of Object.keys(shoppingCart)) {
				Reflect.deleteProperty(shoppingCart, key);
			}
		}
		
		/**
		 * 如果书是数组，则对数组中的每个项目调用 removeAProduct，否则从 shoppingCart 中删除书
		 * @param {book | book[]} book - 书 |书[]
		 * @returns 购物车对象。
		 */
		function removeAProduct(book: book | book[]): void {
			if (Array.isArray(book)) {
				book.forEach(removeAProduct);
				return;
			}
			Reflect.deleteProperty(shoppingCart, book.id);
		}
		
		function addToCart(book: book[], fail: (fail: book) => void): void;
		function addToCart(book: book): void;
		/**
		 * 它将一本书添加到购物车，但前提是用户已登录，并且用户尚未将书添加到购物车，并且用户尚未将太多书添加到购物车。
		 * @param {book[] | book} book - 书[] |书
		 * @param [fail] - （失败：书）=>无效
		 */
		function addToCart(book: book[] | book, fail?: (fail: book) => void): void {
			const user = useUserInfoStore();
			if (!user.state) {
				reject(book, "请先登录", fail);
				return;
			}
			const newAdd = [];
			const readerStore = useReaderStore();
			const isArray = Array.isArray(book);
			if (isArray) {
				for (let i: number = 0; i < book.length; ++i) {
					if (shoppingCart[book[i].id]) continue;
					newAdd.push(book[i]);
				}
			}
			const size = Object.keys(shoppingCart).length + (isArray ? (<book[]>newAdd).length : 1);
			if (
				readerStore.borrowALibraryCard.timeout > 0 ||
				size >
				readerStore.borrowALibraryCard.total - readerStore.borrowALibraryCard.borrowed
			) {
				reject(book, "可借阅额度不足", fail);
				return;
			}
			if (Array.isArray(book)) {
				for (let bo of newAdd) {
					shoppingCart[bo.id] = bo;
				}
			} else {
				shoppingCart[book.id] = book;
			}
		}
		
		/**
		 * `reject` 是一个接受 `book` 或 `book` 数组、`message` 和可选的 `fail` 函数的函数。它使用 ElMessage.warning 显示消息，如果提供了 fail
		 * 函数，它会为数组中的每本书调用它
		 * @param {book[] | book} book - 书[] |书
		 * @param {string} message - string - 要显示给用户的消息。
		 * @param [fail] - （失败：书）=>无效
		 */
		function reject(book: book[] | book, message: string, fail?: (fail: book) => void): void {
			messageLinkTo(message, "/login");
			if (fail) {
				const reject: book[] = [];
				Array.isArray(book) ? reject.unshift(...book) : reject.unshift(book);
				reject.forEach(fail);
			}
		}
		
		return {
			_   : shoppingCart,
			size: computed(() => Object.keys(shoppingCart).length),
			emptyTheCart,
			removeAProduct,
			addToCart
		};
	},
	{
		persist: {
			enabled   : true,
			strategies: [{storage: sessionStorage}]
		}
	}
);