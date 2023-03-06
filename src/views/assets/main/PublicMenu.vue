<!-- @format -->

<template>
	<el-container>
		<el-header height="8vh">
			<div class="container">
				<div class="header-image">
					<el-icon>
						<i class="iconfont icon-tushu"></i>
					</el-icon>
					<span style="font-size: 19px">图书借阅系统</span>
				</div>
				<div class="header-center">

            <label class="menuButton"  for="check">
                <input id="check"   type="checkbox"  @click="emits('closed')">
                <span class="top"></span>
                <span class="mid"></span>
                <span class="bot"></span>
            </label>
					<slot name="header"></slot>
				</div>
				<div class="header-user">
					<el-row
							:gutter="40"
							align="middle">
						<el-col :span="6">
							<el-tooltip
									class="box-item"
									content="刷新"
									effect="dark"
									placement="bottom">
								<el-icon size="20">
									<i
											:class="refreshClass"
											class="iconfont icon-shuaxin"
											@click="refresh"></i>
								</el-icon>
							</el-tooltip>
						</el-col>
						<el-col :span="6">
							<el-dropdown @command="handleCommand">
								<el-avatar
										:size="30"
										alt="头像"
										src="https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png">
									<img
											alt="头像"
											src="https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png"/>
								</el-avatar>
								<template #dropdown>
									<el-dropdown-menu v-if="!userInfoStore.state">
										<el-dropdown-item>
											<router-link to="/login"> 登录</router-link>
										</el-dropdown-item>
									</el-dropdown-menu>
									<el-dropdown-menu v-else>
										<el-dropdown-item command="user">
											我的:{{ userInfoStore.user.name }}
										</el-dropdown-item>
										<el-dropdown-item
												v-if="userInfoStore.auth === 'reader'"
												divided
										>可借阅:{{ balance }}本
										</el-dropdown-item>
										<el-dropdown-item divided>通知</el-dropdown-item>
										<el-dropdown-item
												divided
												@click="logout"
										>退出
										</el-dropdown-item>
									</el-dropdown-menu>
								</template>
							</el-dropdown>
						</el-col>
					</el-row>
				</div>
			</div>
		</el-header>
		<el-container direction="horizontal">
			<el-aside width="auto">
				<el-menu
						:collapse="props.data.isCollapse"
						:collapse-transition="true"
						:default-active="router.currentRoute.value.path"
						:router="true"
						:unique-opened="true"
						active-text-color="#409Eff"
						background-color="#545c64"
						class="el-menu-vertical-demo">
					<menu-tree :data="props.data.ElMenus"></menu-tree>
				</el-menu>
			</el-aside>
			<el-main>
				<div class="main_header">
					<el-breadcrumb separator="/">
						<el-breadcrumb-item :to="{ path: home }">主页</el-breadcrumb-item>
						<el-breadcrumb-item v-if="router.currentRoute.value.path !== home"
						>{{ router.currentRoute.value.name }}
						</el-breadcrumb-item>
					</el-breadcrumb>
					<el-divider/>
				</div>
				<div
						id="view-body"
						style="flex: 1; width: 100%; overflow: auto">
					<router-view
							v-slot="{ Component }">
						<transition
								mode="out-in"
								name="el-zoom-in-top">
							<keep-alive>
								<component :is="Component" :key="router.currentRoute.value.name ?? router.currentRoute.value.path"/>
							</keep-alive>
						</transition>
					</router-view>
				</div>
			</el-main>
		</el-container>
	</el-container>
</template>
<script lang="ts" setup>
import instance, { cacheInstance } from "@/axios/index";
import { useBookStore }            from "@/stores/BooksStore";
import { useUserInfoStore }        from "@/stores/counter";
import { useNotification }         from "@/stores/NotificationStore";
import { useReaderStore }          from "@/stores/readerStore";
import { useShopStore }            from "@/stores/ShopStore";
import MenuTree                    from "@/views/assets/main/MenuTree.vue";
import type publicMenuInstance     from "@/views/assets/main/PublicMenuInstance";
import { ElMessage }               from "element-plus";
import { ref }                     from "vue";
import { useRouter }               from "vue-router";

const refreshClass = ref<string>("");

const props = defineProps<{ data: publicMenuInstance; home: string; balance?: number }>();
const emits = defineEmits<{ (e: "closed"): void }>();
const router = useRouter();
const userInfoStore = useUserInfoStore();

function handleCommand(command: string | number | object) {
	if (command === "user") {
		router.push("/user").finally();
	}
}

function refresh() {
	cacheInstance.clear();
	location.reload();
	refreshClass.value = "refresh";
}

// 当用户单击后退按钮时调用的函数。
function back(): void {
	router.back();
}

// 当用户单击注销按钮时调用的函数。
function logout(): void {
	instance
		.patch(HttpURL.logout, userInfoStore.user.id, {
			headers: {
				"Content-Type": "application/json"
			}
		})
		.then(() => {
			success("退出成功");
		})
		.catch(e => {
			ElMessage.warning({
				grouping: true,
				message : e
			});
		})
		.finally(() => {
			userInfoStore.$reset();
			useReaderStore().$reset();
			useBookStore().$reset();
			useNotification().$reset();
			useShopStore().$reset();
			router.push("/").finally(() => {
				cacheInstance.clear();
			});
		});
}
</script>

<style scoped>
@import "@/views/css/check.css";

.common-layout {
    height: 100vh;
}

.el-header {
    padding: 0;
    background: #313743;
}

.el-dropdown-item {
    justify-content: center;
}

.el-main {
    height: 92vh;
    display: flex;
    flex-direction: column;
    overflow: scroll;
}

.el-aside {
    height: 92vh;
    -webkit-user-select: none;
    user-select: none;
    background: #313743;
}

.header-user {
    width: 200px;
    height: 100%;
    margin-left: auto;
    display: flex;
    align-items: center;
}

.el-sub-menu {
    background: #a5a5b2;
}

.el-menu {
    background: #313743;
    border-right: none;
}

.container {
    display: flex;
    align-items: center;
    width: 100%;
    height: 100%;
}

.header-image {
    height: 8vh;
    width: 200px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.main_header {
    position: sticky;
    top: calc(0px - var(--el-main-padding));
    background: white;
    z-index: 100;
    padding-top: calc(var(--el-main-padding));
    margin-top: calc(0px - var(--el-main-padding));
}

.refresh {
    animation: rotate 0.7s linear 1;
}

.el-card {
    height: 87%;
}

@keyframes rotate {
    0% {
        transform: rotate(0deg);
    }
    25% {
        transform: rotate(90deg);
    }
    50% {
        transform: rotate(180deg);
    }
    75% {
        transform: rotate(270deg);
    }
    100% {
        transform: rotate(360deg);
    }
}

.header-center {
    height: 100%;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    flex-wrap: nowrap;
    gap: 20px;
    box-sizing: border-box;
}

.header-aside {
    display: block;
}
</style>