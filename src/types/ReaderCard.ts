/** @format */

export type ReaderCard = {
	id: string;
	borrowed: number;
	total: number;
	timeout: number;
	
	borrowedBook: string[]
};
export default ReaderCard;
