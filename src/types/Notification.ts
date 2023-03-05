/** @format */

import type User from "@/types/User";

type Notification = {
	readonly id: string;
	readonly type: number;
	readonly title: string;
	message: string;
	sendTime: Date;
	acceptTime: Date | null;
	status: number;
	/* `user` 是一种用户。 */
	readonly send: User;
	readonly accept: User;
	/* `own` 是一个布尔值，指示通知是否由当前用户发送。 */
	own: boolean;
};
export default Notification;
