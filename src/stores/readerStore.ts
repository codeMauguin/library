/** @format */

import instance                  from "@/axios";
import HttpApi                   from "@/axios/HttpURL";
import type ResponseApi          from "@/axios/ResponseApi";
import type ReaderCard           from "@/types/ReaderCard";
import type { UnwrapNestedRefs } from "@vue/reactivity";
import type { AxiosResponse }    from "axios";
import { defineStore }           from "pinia";
import { computed, reactive }    from "vue";

const messages: (() => void)[] = [];

export const useReaderStore = defineStore(
	"reader",
	() => {
		const _: UnwrapNestedRefs<ReaderCard> = reactive<ReaderCard>({
			id          : "",
			borrowed    : 0,
			timeout     : 0,
			total       : 0,
			borrowedBook: []
		});
		
		function message(m: () => void): void {
			messages.push(m);
		}
		
		function patch(reader: ReaderCard): void {
			_.id = reader.id ?? _.id;
			_.borrowed = reader.borrowed ?? _.borrowed;
			_.total = reader.total ?? _.total;
			_.timeout = reader.timeout ?? _.timeout;
			while (messages.length > 0) {
				messages.shift()?.();
			}
		}
		
		function loadBorrowed(): void {
			instance
				.get(`${HttpApi.GET_HAS_BORROWED}`)
				.then(
					({
						 data: {data, code, error}
					 }: AxiosResponse<ResponseApi<string[]>>):void => {
							_.borrowedBook.push(...data);
					}
				);
		}
		
		return {
			_,
			patch,
			loadBorrowed,
			message,
			borrowALibraryCard: computed(() => _)
		};
	},
	{
		persist: {
			enabled   : true,
			strategies: [
				{
					storage: sessionStorage
				}
			]
		}
	}
);
