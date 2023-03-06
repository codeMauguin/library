/** @format */

type PublicMenuInstance = {
	isCollapse: boolean;
	ElMenus: Array<ElMenu>;
};

export type ElMenu = {
	icon?: string;
	text: string;
	index: string;
	children?: Array<ElMenu>;
	
	el?: string;
};

export default PublicMenuInstance;