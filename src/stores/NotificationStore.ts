/** @format */

import { defineStore } from "pinia";
import type Notification from "@/types/Notification";
import type { Ref } from "vue";
/** @format */
export const useNotification = defineStore(
	"Notification",
	() => {
		const _: Ref<Notification[]> = ref<Notification[]>([]);
		function init(value: Record<keyof Notification, any>[]) {
			const user = useUserInfoStore();
			_.value.length = 0;
			for (const val of value) {
				val.sendTime = new Date(val.sendTime);
				val.acceptTime = val.acceptTime ? new Date(val.acceptTime) : null;
				val.own = val.send.id === user.user.id;
				val.accept = val.own ? user.user : val.accept;
				val.send.username = val.send.name;
				_.value.push(val);
			}
		}
		function filter(
			predicate: (val: Notification, index?: number, array?: Notification[]) => boolean
		): Notification[] {
			return _.value.filter(predicate);
		}

		function anyMatch(
			predicate: (val: Notification, index?: number, array?: Notification[]) => boolean
		): boolean {
			return _.value.some(predicate);
		}

		return { _, init, filter, anyMatch };
	},
	{}
);
