/** @format */

import { ElLink, ElMessage } from "element-plus";
import type { MessageParamsWithType } from "element-plus";

export function success(options: MessageParamsWithType | string) {
	if (Assert.isString(options)) {
		options = {
			message: <string>options
		};
	}
	ElMessage.success(copyWith(options, { grouping: true }));
}

export function warning(options: MessageParamsWithType | string) {
	if (Assert.isString(options)) {
		options = {
			message: <string>options
		};
	}
	ElMessage.warning(copyWith(options, { grouping: true }));
}

export function error(options: MessageParamsWithType | string) {
	if (Assert.isString(options)) {
		options = {
			message: <string>options
		};
	}
	ElMessage.error(copyWith(<object>options, { grouping: true }));
}

export function info(options: MessageParamsWithType | string) {
	if (Assert.isString(options)) {
		options = {
			message: <string>options
		};
	}
	ElMessage.info(copyWith(options, { grouping: true }));
}

export function messageLinkTo(message: string, uri: string): void {
	info({
		message: () =>
			h(
				ElLink,
				{
					type: "warning",
					href: uri
				},
				() => message
			)
	});
}
