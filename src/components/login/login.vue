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
            <el-form-item prop="code">
                <div style="display: flex; gap: 10px;align-items: center;width: 100%">
                    <span>验证码：</span>
                    <el-input v-model="user.code" maxlength="4" size="large" style="width: 150px;"/>
                    <div class="code" @click="code">
                        <img ref="codeRef" alt="" src="#"/>
                    </div>
                </div>
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
import instance                                              from "@/axios";
import HttpApi                                               from "@/axios/HttpURL";
import type ResponseApi                                      from "@/axios/ResponseApi";
import { user, valid }                                       from "@/components/login/type";
import { useUserInfoStore }                                  from "@/stores/counter";
import { closeLoading }                                      from "@/utils";
import type { AxiosResponse }                                from "axios";
import { ElNotification, type FormInstance, type FormRules } from "element-plus";
import type { Ref }                                          from "vue";
import { reactive, ref }                                     from "vue";
import { useRouter }                                         from "vue-router";

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
									  ],
									  code    : [{
										  validator: valid,
										  trigger  : "valid",
										  message  : "验证码为空！！"
									  }]
								  });
const router = useRouter();

function submitLogin(formEl: FormInstance | null): void {
	formEl?.validate((isValid: boolean) => {
		IfStream.of(isValid).then(async () => {
					return {
						state   : true,
						value   : await instance.get(HttpApi.CODE_VALID,
													 {params: {username: sessionStorage.getItem("code_key"), code: user.code}}),
						__SELF__: FLAG
					};
				})
				.catch(() => {
					if (!Assert.hasText(user.code)) {
						ElNotification.error({
												 title  : "登录",
												 message: "请输入验证码"
											 });
					} else {
						ElNotification.error({
												 title  : "登录",
												 message: "请输入用户名或密码"
											 });
					}
				}).next()
				.then(async () => {
					const isNum: boolean = Assert.isNumber(user.username);
					start();
					return {
						state   : true,
						value   : await instance
							.post(HttpApi.login, {
								id      : isNum ? user.username : undefined,
								name    : !isNum ? user.username : undefined,
								card    : sessionStorage.getItem("code_key"),
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
			ElNotification.warning({
									   title  : "登录",
									   message: "验证码错误"
								   });
			getCode();
			user.code = "";
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
							ElNotification.success({
													   title  : "登录",
													   message: "登陆成功"
												   });
						});
					return true;
				}).catch((value) => {
			getCode();
			user.password = "";
			closeLoading();
			ElNotification.error({
									 title  : "登录",
									 message: value?.data?.error
								 });
			return false;
		});
	});
}

const codeRef: Ref<HTMLImageElement | null> = ref<HTMLImageElement | null>(null);


function getCode(): void {
	instance.get(HttpApi.CODE, {
		lb             : false, params: {
			username: sessionStorage.getItem("code_key")
		}, responseType: "blob"
	}).then((data) => {
		const reader = new FileReader();
		reader.readAsDataURL(data.data);
		reader.onload = function (e) {
			const url: string | ArrayBuffer | null = e!.target!.result;
			codeRef.value!.src = (<string>url);
		};
	});
}

const code: (...args: unknown[]) => any = throttle(getCode, 500);

onMounted(() => {
	getCode();
});
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

.code {
    width: 100px;
    height: 40px;
    border: #1b7b91 1px solid;
}
</style>