/** @format */

import type { ElMenu } from "@/views/assets/main/PublicMenuInstance";

const loginEl: string = "state.state";
export const readerAside: ElMenu[] = [{
	index: "1", text: "图书", icon: "icon-wenjianjia", children: [{
		index: "/reader/allBooks", text: "图书馆", icon: "icon-nav_tushuguan", children: undefined
	}]
}, {
	index: "2", text: "我的", el: loginEl, icon: "icon-fenlei", children: [{
		index: "/reader/borrowed", text: "借阅情况", icon: "icon-wodedingdan"
	}, {
		index: "/reader/order", text: "订单中心", icon: "icon-dingdanzhongxin"
	}, {
		index: "#", text: "挂失或报损",
		icon : "icon-UIsheji_menjinxitong-28"
	}]
}, {
	index: "3", el: loginEl, text: "统计分析", icon: "icon-tongjifenxi-xiangmubiaogetongji", children: [{
		index: "/main", text: "报表"
	}]
}];
export const adminAside: ElMenu[] = [{
	index: "1", text: "功能管理", icon: "icon-gongnengguanli", children: [{
		index: "/admin/vi/books/add", text: "添加图书", icon: "icon-tianjia", children: undefined
	}, {
		index: "/admin/vi/books/update/inventory", icon: "icon-guanli1", text: "图书管理", children: undefined
	}, {
		index: "/reader/allBooks", text: "图书馆", icon: "icon-nav_tushuguan", children: undefined
	}]
}, {
	index: "2", text: "用户管理", icon: "icon-bangzhu", children: [{
		index: "/admin/audit/user/book", text: "用户借阅审核"
	}, {
		text: "读者账户管理", index: "/admin/readers"
	}, {
		text: "黑名单管理", index: "/admin/vi/back"
	}]
}, {
	index: "3", text: "统计分析", icon: "icon-tongjifenxi-xiangmubiaogetongji", children: [{
		index: "/main", text: "报表"
	}]
}];