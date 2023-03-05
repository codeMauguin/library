<!-- @format -->

<template>
	<div>
		<h1>Login</h1>
		<el-form
				ref="ruleFormRef"
				:model="user"
				:rules="rules"
				@keyup.enter.native="submitLogin(ruleFormRef)">
			<el-form-item
					class="form-group"
					prop="username">
				<label for="username">ID-Name</label>
				<el-input
						id="username"
						v-model="user.username"
						:autocomplete="'off'"
						size="large"
						type="text"
						@focus="() => ruleFormRef?.clearValidate()">
					<template #prefix>
						<i class="iconfont icon-yonghuming"></i>
					</template>
				</el-input>
			</el-form-item>
			<el-form-item
					class="form-group"
					prop="password">
				<label for="password">Password</label>
				<el-input
						id="password"
						v-model="user.password"
						:show-password="true"
						size="large"
						@focus="() => ruleFormRef?.clearValidate()">
					<template #prefix>
						<i class="iconfont icon-mima"></i>
					</template>
				</el-input>
			</el-form-item>
		</el-form>
		<el-button
				class="button-name"
				size="large"
				@click="submitLogin(ruleFormRef)">
			<template #icon>
				<i class="iconfont icon-submit"/>
			</template>
			<span>Submit</span>
		</el-button>
	</div>
</template>
<script lang="ts" setup>
import instance                              from "@/axios";
import HttpApi                               from "@/axios/HttpURL";
import type ResponseApi                      from "@/axios/ResponseApi";
import { user, valid }                       from "@/components/login/type";
import { useUserInfoStore }                  from "@/stores/counter";
import { closeLoading }                      from "@/utils";
import type { AxiosResponse }                from "axios";
import { type FormInstance, type FormRules } from "element-plus";
import { reactive }                          from "vue";
import { ref }                               from "vue";
import type { Ref }                          from "vue";
import { useRouter }                         from "vue-router";

const ruleFormRef: Ref<FormInstance | null> = ref<FormInstance | null>(null);
const rules = reactive<FormRules>({
	username: [
		{
			validator: valid,
			trigger  : "valid"
		}
	],
	password: [
		{
			validator: valid,
			trigger  : "valid"
		}
	]
});
const router = useRouter();

function submitLogin(formEl: FormInstance | null): void {
	formEl?.validate((isValid: boolean) => {
		
		IfStream.of(isValid).then(async () => {
			const isNum: boolean = Assert.isNumber(user.username);
			start();
			return {
				state   : true,
				value   : await instance
					.post(HttpApi.login, {
						id      : isNum ? user.username : undefined,
						name    : !isNum ? user.username : undefined,
						password: user.password
					})
					.then(
						({
							 data: {
								 data
							 }
						 }: AxiosResponse<ResponseApi<any>>) => data
					),
				__SELF__: FLAG
			};
		}).catch(() => {
			error("请输入用户名或密码");
		}).next()
				.then((value) => {
					const {id, name, permissions} = value;
					const counter = useUserInfoStore();
					counter.login(id, name, permissions);
					router
						.push({
							name: "main"
						})
						.finally(() => {
							closeLoading();
							success("登陆成功");
						});
					return true;
				}).catch((value) => {
			user.password = "";
			closeLoading();
			warning(value?.data?.error);
			return false;
		});
	});
}


</script>
<style scoped>
@import "@/assets/css/input.css";
@import url("@/components/login/btn.css");

h1 {
    font-size: 24px;
    margin-bottom: 20px;
    text-align: center;
}

label {
    display: block;
    margin-bottom: 5px;
    font-weight: bold;
    user-select: none;
}

.el-input {
    font-family: 'Courier New', Courier, monospace;
    transition: all .5s;
    letter-spacing: 10px;
}


</style>