/** @format */

import instance               from "@/axios";
import type ResponseApi       from "@/axios/ResponseApi";
import type { SignalDriver }  from "@/utils/Signal";
import type { AxiosResponse } from "axios";

type commodity = string | string[];
const error = console.error;

/**
 * 创建订单
 * 获取订单token
 */
async function createOrder(commodity: commodity): Promise<{
	token: string | null;
	error: string | null;
}> {
	try {
		const {
				  data: {data: orderId}
			  } = await instance.post(HttpURL.createOrderId, commodity);
		return {
			token: orderId,
			error: "创建成功"
		};
	} catch (data: unknown) {
		const error: string = (<AxiosResponse<ResponseApi<null>>>data)?.data.error;
		return {
			token: null,
			error
		};
	}
}

/**
 * 提交订单
 */
/**
 * > 该函数用于提交订单
 * @param {commodity} commodity - 待借商品，对象数组。
 * @param {SignalDriver} signalDriver - 信号驱动器
 */
async function commitOrder(commodity: commodity, signalDriver: SignalDriver): Promise<boolean> {
	commodity = Array.isArray(commodity) ? commodity : [commodity];
	signalDriver.emit(1);
	const {token: orderId, error: e} = await createOrder(commodity);
	if (Assert.isNull(orderId)) {
		signalDriver.emit(3);
		return false;
	}
	signalDriver.emit(1);
	try {
		const {
				  data: {data}
			  } = await instance.post(HttpURL.commitOrder, {
			orderId
		});
		signalDriver.emit(2, data);
		return true;
	} catch (e) {
		signalDriver.emit(3, e);
	}
	return false;
}

export { commitOrder };