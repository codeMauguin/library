/** @format */

import instance from "@/axios/index";
import type ResponseApi from "@/axios/ResponseApi";
import oncePromise from "@/utils/OncePromise";
import type { AxiosResponse } from "axios";

const promise = oncePromise(instance.post);

export function commitComment(
	userId: string,
	bookId: string,
	content: string,
	parentId?: string
): Promise<AxiosResponse<ResponseApi<string>>> {
	return promise(HttpURL.commentCommit, {
		userId,
		bookId,
		content,
		parentId
	});
}
