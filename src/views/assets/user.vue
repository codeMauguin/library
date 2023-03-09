<!-- @format -->

<template>
	<el-card shadow="hover">
		<el-tabs
			class="demo-tabs"
			tab-position="left"
			type="border-card">
			<el-tab-pane label="User">
				<el-descriptions
					:column="1"
					border>
					<el-descriptions-item
						align="left"
						label="用户名:"
						>{{ store.user.name }}
					</el-descriptions-item>
					<el-descriptions-item
						align="left"
						label="身份:">
						{{ store.auth === "reader" ? "读者" : "管理员" }}
					</el-descriptions-item>
					<el-descriptions-item label="邮箱"> 无 </el-descriptions-item>
				</el-descriptions>
			</el-tab-pane>
			<el-tab-pane label="借阅信息">
				<el-descriptions
					:column="1"
					extra="单位(本)"
					title="借阅信息">
					<el-descriptions-item
						align="right"
						label="总可借阅图书数:">
						{{ reader.borrowALibraryCard.total }}
					</el-descriptions-item>
					<el-descriptions-item
						align="right"
						label="借阅中:">
						{{ reader.borrowALibraryCard.borrowed }}
					</el-descriptions-item>
					<el-descriptions-item
						align="right"
						label="超时借阅:">
						{{ reader.borrowALibraryCard.timeout }}
					</el-descriptions-item>
					<el-descriptions-item
						align="right"
						label="已经归还:">
						{{ 0 }}
					</el-descriptions-item>
				</el-descriptions>
			</el-tab-pane>
			<el-tab-pane label="修改个人信息">
				<div style="display: flex; flex-direction: column; gap: 20px">
					<div
						style="
							flex: 1;
							align-self: flex-start;
							display: grid;
							grid-template-columns: 200px 30px;
							align-items: center;
							gap: 10px;
						">
						<input
							v-model="name"
							autocomplete="new-password"
							class="input"
							name="firstName"
							placeholder="First name"
							type="text" />
						<el-tooltip content="用户名不可以全部数字">
							<i class="iconfont icon-tishi" />
						</el-tooltip>
						<input
							v-model="password"
							autocomplete="new-password"
							class="input"
							name="password"
							placeholder="password"
							type="password" />
					</div>
					<div class="edit_user">
						<button
							class="btn_commit"
							@click="revise">
							<svg
								class="css-i6dzq1"
								fill="none"
								height="24"
								stroke="#FFFFFF"
								stroke-linecap="round"
								stroke-linejoin="round"
								stroke-width="2"
								viewBox="0 0 24 24"
								width="24">
								<path
									d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"></path>
							</svg>
							Edit
						</button>
					</div>
				</div>
			</el-tab-pane>
		</el-tabs>
	</el-card>
</template>

<script lang="ts" setup>
	import { cacheInstance } from "@/axios";
	import instance from "@/axios";
	import router from "@/router";
	import { useBookStore } from "@/stores/BooksStore";
	import { useUserInfoStore } from "@/stores/counter";
	import { useNotification } from "@/stores/NotificationStore";
	import { useReaderStore } from "@/stores/readerStore";
	import { useShopStore } from "@/stores/ShopStore";

	const store = useUserInfoStore();
	const reader = useReaderStore();
	const name = ref<string>("");
	const password = ref<string>("");
	//Todo 查询借阅信息
	//timeout 2023-03-01
	async function revise() {
		if (
			(!Assert.hasText(name.value) && !Assert.hasText(password.value)) ||
			(Assert.hasText(name.value) && Assert.isNumber(name.value))
		) {
			warning("请输入正确信息");
			name.value = "";
			password.value = "";
			return;
		}
		IfStream.of(
			instance
				.post(HttpURL.REVISE_USER, { name: name.value, password: password.value })
				.then(({ data: { data } }) => data)
		)
			.then(() => {
				success("修改成功");
				store.$patch({ $user: { name: name.value } });
				name.value = "";
				if (Assert.hasText(password.value)) {
					//下线当前用户
					store.$reset();
					reader.$reset();
					useBookStore().$reset();
					useNotification().$reset();
					useShopStore().$reset();
					router.push("/login").finally(() => {
						cacheInstance.clear();
						router.go(0);
					});
				}
			})
			.catch(() => {
				warning("修改失败");
			});
	}
</script>

<style scoped>
	@import "@/views/css/input.css";
	@import "@/views/css/btn_commit.css";

	.el-descriptions {
		float: left;
	}

	.edit_user {
		display: flex;
		flex-direction: column;

		gap: 15px;
	}
</style>
