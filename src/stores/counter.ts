import type User         from "@/types/User";
import { defineStore }   from "pinia";
import { computed, ref } from "vue";

export const useUserInfoStore = defineStore(
	"rules",
	() => {
		const $token = ref<string>("");
		const $states = ref<boolean>(false);
		const $user = ref<User>({id: "", name: "", permissions: ""});
		
		function login(id: string, name: string, permission: string): void {
			$user.value.id = id;
			$user.value.name = name;
			$states.value = true;
			$user.value.permissions = permission;
			cacheInstance.clear();
		}
		
		function refreshToken(token: string | undefined): void {
			if (Assert.isNull(token)) return;
			$token.value = <string>token;
		}
		
		/**
		 * 它是一个接受三个参数并返回解析为 void 的 promise 的函数
		 * @param {string} newToken - 将用于替换旧令牌的新令牌。
		 * @param {string} id - 用户的id
		 */
		return {
			$token,
			$states,
			$user,
			isAdmin: computed(() => $states.value && $user.value.permissions === "admin"),
			auth   : computed(() => $user.value.permissions),
			token  : computed(() => $token.value),
			state  : computed(() => $states.value),
			user   : computed(() => $user.value),
			login,
			refreshToken
		};
	},
	{
		persist: {
			enabled   : true,
			strategies: [{storage: sessionStorage}]
		}
	}
);
