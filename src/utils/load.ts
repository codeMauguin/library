import { ElLoading } from "element-plus";
import type { Ref }  from "vue";


const el: Ref<string> = shallowRef<string>("document.body");

export function loadEl(els: string): void {
	el.value = els;
}

export function closeLoading() {
	ElLoading.service().close();
}

export function start(option?: {
	fullscreen?: boolean,
	target?: string | HTMLElement,
	lock?: boolean
	customClass?: string,
	autoClose?: boolean | number
}): void {
	if (option?.autoClose) {
		if (typeof option.autoClose !== "number") {
			option.autoClose = 3000;
		}
		setTimeout(() => {
			ElLoading.service({
								  fullscreen : option?.fullscreen ?? true,
								  text       : "",
								  target     : option?.target ?? el.value,
								  lock       : option?.lock,
								  body       : true,
								  customClass: option?.customClass,
								  background : "rgba(0, 0, 0, 0.7)"
							  });
		}, option.autoClose);
		
	} else {
		ElLoading.service({
							  fullscreen : option?.fullscreen ?? true,
							  text       : "",
							  target     : option?.target ?? el.value,
							  lock       : option?.lock,
							  body       : true,
							  background : "rgba(0, 0, 0, 0.7)",
							  customClass: option?.customClass
						  });
	}
}