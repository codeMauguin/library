/** @format */

type Inventory = {
	total: number; borrowed: number;
};
export type BookBorrowCard = {
	readonly id: string;
	readonly bookId: string;
	readonly orderId: string;
	status: number;
	readonly createTime: string;
	returnTime: string;
	
	renewal: number;
};

type Book = {
	readonly id: string;
	name: string;
	author: string;
	info: string;
	category: string;
	press: string;
	image: string | ArrayBuffer ;
	inventory: Inventory;
	state: number;
	readonly comments: number;
	time: Date;
};
export default Book;