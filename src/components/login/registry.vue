<!-- @format -->

<template>
    <div>
        <h1> Registry </h1>
        <el-form
                ref="ruleFormRef"
                :model="user"
                :rules="rules">
            <el-form-item
                    class="form-group"
                    prop="username">
                <label for="username">username</label>
                <el-input
                        id="username"
                        v-model="user.username"
                        autocomplete="off"
                        size="large"
                        type="text">
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
                        autocomplete="new-password"
                        size="large">
                    <template #prefix>
                        <i class="iconfont icon-mima"></i>
                    </template>
                </el-input>
            </el-form-item>
        </el-form>
        <button class="learn-more iconfont icon-submit" style="float: right" @click="registry">
            注册
        </button>
    </div>
</template>
<script lang="ts" setup>
import instance                         from "@/axios";
import type ResponseApi                 from "@/axios/ResponseApi";
import { user, valid }                  from "@/components/login/type";
import router                           from "@/router";
import { useUserInfoStore }             from "@/stores/counter";
import oncePromise                      from "@/utils/OncePromise";
import type { AxiosResponse }           from "axios";
import type { FormInstance, FormRules } from "element-plus";
import type { Ref }                     from "vue";

const ruleFormRef: Ref<FormInstance | null> = ref<FormInstance | null>(null);
const rules: FormRules = reactive<FormRules>({
												 username: [
													 {
														 validator: checkName,
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

function checkName(rule: any, value: any, callback: (args?: any) => void): void {
	if (Assert.isNumber(value)) {
		callback(new Error(`${rule.field} is not set Number`));
	} else if (!Assert.hasText(value)) {
		callback(new Error(`${rule.field} is null`));
	} else {
		callback();
	}
}

const user: Record<string, any> = reactive({
											   username: "",
											   password: ""
										   });
const promise: (...args: unknown[]) => Promise<any> = oncePromise(instance.post);

function registry(): void {
	ruleFormRef.value?.validate(async (isValid: boolean) => {
		IfStream.of(isValid && !Assert.isNumber(user.username))
				.then(async () =>
						  ({
							  state   : true,
							  __SELF__: FLAG,
							  value   : await promise(HttpURL.REGISTRY, {
								  name    : user.username,
								  password: user.password
							  })
						  })
				)
				.catch(() => {
					warning("用户名或密码为空");
					return IfStream.STOP;
				})
				.next()
				.then((value: AxiosResponse<ResponseApi<string>>) => {
					success("注册成功");
					const counter = useUserInfoStore();
					counter.login(value.data.data, user.username, "reader");
					router
						.push({
								  name: "main"
							  });
					ruleFormRef.value!.clearValidate();
					ruleFormRef.value!.resetFields();
				})
				.catch((e) => {
					warning("用户名已存在！");
					ruleFormRef.value!.resetFields();
				});
	});
}
</script>
<style scoped>
h1 {
    font-size: 24px;
    margin-bottom: 20px;
    text-align: center;
}

.el-input {
    font-family: 'Courier New', Courier, monospace;
    transition: all .5s;
}

button {
    position: relative;
    display: inline-block;
    cursor: pointer;
    outline: none;
    border: 0;
    vertical-align: middle;
    text-decoration: none;
    font-family: inherit;
    font-size: 15px;
}

button.learn-more {
    font-weight: 600;
    color: #382b22;
    text-transform: uppercase;
    padding: 1.25em 2em;
    background: #fff0f0;
    border: 2px solid #b18597;
    border-radius: 0.75em;
    -webkit-transform-style: preserve-3d;
    transform-style: preserve-3d;
    -webkit-transition: background 150ms cubic-bezier(0, 0, 0.58, 1), -webkit-transform 150ms cubic-bezier(0, 0, 0.58, 1);
    transition: transform 150ms cubic-bezier(0, 0, 0.58, 1), background 150ms cubic-bezier(0, 0, 0.58, 1), -webkit-transform 150ms cubic-bezier(0, 0, 0.58, 1);
}

button.learn-more::before {
    position: absolute;
    content: '';
    width: 100%;
    height: 100%;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: #f9c4d2;
    border-radius: inherit;
    -webkit-box-shadow: 0 0 0 2px #b18597, 0 0.625em 0 0 #ffe3e2;
    box-shadow: 0 0 0 2px #b18597, 0 0.625em 0 0 #ffe3e2;
    -webkit-transform: translate3d(0, 0.75em, -1em);
    transform: translate3d(0, 0.75em, -1em);
    transition: transform 150ms cubic-bezier(0, 0, 0.58, 1), box-shadow 150ms cubic-bezier(0, 0, 0.58, 1), -webkit-transform 150ms cubic-bezier(0, 0, 0.58, 1), -webkit-box-shadow 150ms cubic-bezier(0, 0, 0.58, 1);
}

button.learn-more:hover {
    background: #ffe9e9;
    -webkit-transform: translate(0, 0.25em);
    transform: translate(0, 0.25em);
}

button.learn-more:hover::before {
    -webkit-box-shadow: 0 0 0 2px #b18597, 0 0.5em 0 0 #ffe3e2;
    box-shadow: 0 0 0 2px #b18597, 0 0.5em 0 0 #ffe3e2;
    -webkit-transform: translate3d(0, 0.5em, -1em);
    transform: translate3d(0, 0.5em, -1em);
}

button.learn-more:active {
    background: #ffe9e9;
    -webkit-transform: translate(0em, 0.75em);
    transform: translate(0em, 0.75em);
}

button.learn-more:active::before {
    -webkit-box-shadow: 0 0 0 2px #b18597, 0 0 #ffe3e2;
    box-shadow: 0 0 0 2px #b18597, 0 0 #ffe3e2;
    -webkit-transform: translate3d(0, 0, -1em);
    transform: translate3d(0, 0, -1em);
}
</style>