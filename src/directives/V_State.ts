import type { Directive }        from "@vue/runtime-core";
import type { VNode }            from "vue";
import type { DirectiveBinding } from "vue";


/**
 * If the element has a parent node, remove the element from its parent node.
 * @param {Element} el - Element - The element to be destroyed.
 */
function destroy(el: Element): void {
	el.parentNode && el.parentNode.removeChild(el);
}

function bind(el: Element, binding: string, thisApp: () => { permission: string, status: boolean } ): void {
	const app: { permission: string; status: boolean } = thisApp();
	if (Assert.isNull(binding)) {
		// 	验证是否登陆
		if (!app.status) {
			destroy(el);
		}
		return;
	}
	//权限不对
	if (binding!.localeCompare(app.permission) !== 0) {
		destroy(el);
	}
}

export default function (context: () => { permission: string, status: boolean }): Directive {
	return {
		updated(el: any, binding: DirectiveBinding<string>, vnode: VNode<any, any>,
				prevVNode: VNode<any, any>): void {
			bind(el, binding.value, context);
		},
		mounted(el: any, binder: DirectiveBinding<string>) {
			bind(el, binder.value,context);
		}
	};
}


