import { ElLoading }            from "element-plus";
import type { Ref, ShallowRef } from "vue";


const el: Ref<string> = shallowRef<string>("document.body");
const cur: ShallowRef<any> = shallowRef([]);

export function loadEl(els: string): void {
	el.value = els;
}

export function closeLoading(): void {
	while (!Assert.isEmpty(cur.value)) {
		cur.value.shift().close();
	}
}

export function start(option?: {
	fullscreen?: boolean,
	target?: string | HTMLElement,
	lock?: boolean
	customClass?: string,
	autoClose?: boolean | number
}): void {
	IfStream.of(Assert.notNull(option?.autoClose)).then(() => {
		if (typeof option?.autoClose !== "number") {
			option!.autoClose = 3000;
		}
		cur.value.push(ElLoading.service({
											 fullscreen : option?.fullscreen ?? true,
											 text       : "",
											 target     : option?.target ?? el.value,
											 lock       : option?.lock,
											 body       : true,
											 background : "rgba(0, 0, 0, 0.7)",
											 customClass: option?.customClass
										 }));
		setTimeout(() => {
			closeLoading();
		}, option?.autoClose);
	}).catch(() => {
		cur.value.push(ElLoading.service({
											 fullscreen : option?.fullscreen ?? true,
											 text       : "",
											 target     : option?.target ?? el.value,
											 lock       : option?.lock,
											 body       : true,
											 background : "rgba(0, 0, 0, 0.7)",
											 customClass: option?.customClass
										 }));
	});
	
	
}