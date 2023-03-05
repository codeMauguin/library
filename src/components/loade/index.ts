/** @format */

import x from "@/components/loade/index";
import load from "@/components/loade/view.vue";
import { createVNode, render } from "vue";
import type { VNode } from "vue";
const instance: {
	node?: VNode<InstanceType<typeof load>>;
	target?: string;
	position?: string;
	html?: HTMLElement;
	cancel?: () => void;
	timer?: number;
} & Instance = {};
type Instance = { target?: string; cancel?: () => void; autoClose?: boolean };

export function closed() {
	if (instance.target && instance.html) {
		const element: HTMLElement | null = document.querySelector(instance.target);
		if (!element) {
			return;
		}
		element.removeChild(instance.html);
		element.style.position = instance.position as string;
	}
	instance.position = undefined;
	instance.target = undefined;
	instance.cancel = undefined;
	instance.node?.component?.exposed?.closed?.();
	instance.autoClose = true;
}

function start(option: Instance = {}) {
	x(option);
}

function progress(number?: number) {
	instance?.node?.component?.exposed?.progress(Math.min(100, number ?? 0));
}

function create(): void {
	//@ts-ignore
	instance.node = createVNode(load, {
		onCancel() {
			instance?.cancel?.();
			closed();
		},
		onClosed() {
			if (instance.autoClose) {
				setTimeout(() => {
					closed();
				}, 3000);
			}
		}
	});
	instance.html = document.createElement("div");
	//@ts-ignore
	render(instance.node, instance.html);
}

export const LoadProgress = {
	start,
	closed,
	progress
};

export default (option: Instance = {}) => {
	if (instance.node && instance.html) {
		A: {
			if (instance.target) {
				//说明上一个没有结束
				const element: HTMLElement | null = document.querySelector(instance.target);
				if (!element) break A;
				element.removeChild(instance.html); //移除
				element.style.position = instance.position as string;
			}
		}
		instance.target = option.target ?? "body";
		const element: HTMLElement | null = document.querySelector(instance.target);
		if (!element) {
			return closed();
		}
		instance.position = element.style.position;
		element.style.position = "relative";
		element.appendChild(instance.html);
		instance.cancel = option.cancel;
		instance.autoClose = option.autoClose ?? true;
	} else {
		create();
		x(option);
	}
};
