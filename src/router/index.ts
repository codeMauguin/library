/** @format */

import { rule }                           from "@/Authorization";
import { useUserInfoStore }               from "@/stores/counter";
import { Assert }                         from "@/utils";
import { ElMessage }                      from "element-plus";
import type { Router }                    from "vue-router";
import { createRouter, createWebHistory } from "vue-router";

const router: Router = createRouter({
	history: createWebHistory(import.meta.env.BASE_URL),
	routes : [
		{
			path     : "/login",
			name     : "login",
			component: () => import("@/components/login/index.vue")
		},
		{
			path    : "/",
			name    : "home",
			redirect: "/reader/allBooks"
		},
		{
			path     : "/main",
			name     : "main",
			component: () => import("@/views/assets/main/View.vue"),
			redirect:"/main/statistics",
			meta     : {
				title: "图书借阅系统"
			},
			children : [
				{
					path     : "statistics",
					name     : "statistics",
					component: () => import("@/views/assets/main/main.vue")
				},
				{
					path     : "/admin/vi/books/add",
					name     : "添加图书",
					component: () => import("@/views/admin/addBook.vue")
				},
				{
					path     : "/admin/vi/books/update/inventory",
					name     : "图书管理",
					component: () => import("@/views/BooksManager/BookInventory.vue")
				},
				{
					path     : "/admin/audit/user/book",
					name     : "用户借阅审核",
					component: () => import("@/views/admin/AuditReader.vue")
				},
				{
					path     : "/admin/readers",
					name     : "读者账户管理",
					component: () => import("@/views/admin/AddReader.vue")
				},
				{
					path     : "/reader/allBooks",
					name     : "图书馆",
					component: () => import("@/views/reader/AllBooks.vue")
				},
				{
					path     : "/reader/borrowed",
					name     : "借阅表",
					component: () => import("@/views/reader/MyBorrowed.vue")
				},
				{
					path     : "/user",
					name     : "user",
					component: () => import("@/views/assets/user.vue")
				},
				{
					path     : "/book/:id?",
					name     : "book",
					props    : true,
					component: () => import("@/views/assets/main/BookView.vue")
				},
				{
					path:"/admin/vi/back",
					name:"back",
					component: () => import("@/views/admin/BackManager.vue")
				},
				{
					path     : "/reader/order",
					name     : "订单中心",
					component: () => import("@/views/reader/order/order.vue")
				}
			]
		}
	]
});


/**
 *权限路由控制  - 先检查是否在白名单内
 *            - 检查页面是否有权限
 */
/* A router guard. */
router.beforeEach((to, from, next) => {
	if (Assert.isNull(to.name)) {
		//提示
		ElMessage.error({
			grouping: true,
			message : "页面不存在"
		});
		return next(from);
	}
	/**
	 * 1.检查是是否需要登录
	 * 2.检查权限
	 */
	//在白名单内
	if (rule.isWhite(to.path)) {
		return next();
	}
	//获取页面信息
	const userInfoStore = useUserInfoStore();
	if (rule.isLogin(to.path) && !userInfoStore.state) {
		messageLinkTo("没有登陆", "/login");
		return next(from);
	} else if (rule.verify(to.path, rule.auth(userInfoStore.auth), userInfoStore.state)) {
		return next();
	} else {
		ElMessage({
			grouping: true,
			message : "没有权限"
		});
		return next(from);
	}
});
export default router;