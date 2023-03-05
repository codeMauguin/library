/** @format */

type Order = {
	readonly id: string;
	status: number;
	create_time: Date;
	end_time: Date | null;
	reason: string | null;
};
export default Order;
