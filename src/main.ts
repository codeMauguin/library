/** @format */

import "@/assets/css/base.css";
import "@/assets/css/BlockQuote.css";
import "@/assets/icon/iconfont.css";
import plugin                                                               from "@/directives/V_State";
import { useUserInfoStore }                                                 from "@/stores/counter";
import { BarChart, LineChart, PieChart }                                    from "echarts/charts";
import { GridComponent, LegendComponent, TitleComponent, TooltipComponent } from "echarts/components";


import { use }            from "echarts/core";
import { CanvasRenderer } from "echarts/renderers";

import { createPinia }     from "pinia";
import piniaPersist        from "pinia-plugin-persist";
import { createApp }       from "vue";
import ECharts             from "vue-echarts";
import App                 from "./App.vue";
import router              from "./router";
import { useNotification } from "./stores/NotificationStore";

function context(): {
	permission: string;
	status: boolean;
} {
	const userInfoStore = useUserInfoStore();
	return {permission: userInfoStore.auth, status: userInfoStore.state};
}

function getUuid() {
	return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, function (c) {
		const r = Math.random() * 16 | 0, v = c == "x" ? r : (r & 0x3 | 0x8);
		return v.toString(16);
	});
}


use([CanvasRenderer,
		PieChart,
		TitleComponent,
		TooltipComponent,
		GridComponent,
		LegendComponent, BarChart, LineChart]);

const pinia = createPinia();
pinia.use(({store}) => {
	const initialState = JSON.parse(JSON.stringify(store.$state));
	store.$reset = () => {
		store.$state = JSON.parse(JSON.stringify(initialState));
	};
});
pinia.use(piniaPersist);
const app = createApp(App);
app.directive("status", plugin(context))
   .component("v-chart", ECharts)
   .use(pinia)
   .use(router);
router.isReady()
	  .then(() => {
		  app.mount("#app");
		  useUserInfoStore();
		  useNotification();
		  //生成唯一机器码
		  sessionStorage.setItem("code_key", getUuid());
	  });