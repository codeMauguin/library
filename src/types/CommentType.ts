/** @format */

type returnType = {
	id: string;
	root_id?: string;
	parent_id?: string;
	bookId: string;
	user: {
		id: string; name: string; image: string;
	};
	child_size: number;
	content: string;
	timestamp: string;
	liked: number;
	unLike: number;
	isLiked: boolean;
	isUnLiked: boolean;
};

type CommentType = {
	readonly id: string; child: number; root?: string; parent?: CommentType; children?: CommentType[];
	
	user: {
		readonly username: string; readonly id: string; readonly headerImage: string;
	}; book: {
		readonly bookId: string;
	}; content: string;
	
	timestamp: Date; likeCount: number; unLikeCount: number;
	
	isLike: boolean; isNotLike: boolean;
};

function map(element: returnType): CommentType {
	return {
		root       : element.root_id,
		parent     : undefined,
		book       : {bookId: element.bookId},
		timestamp  : new Date(element.timestamp),
		likeCount  : element.liked,
		children   : [],
		unLikeCount: element.unLike,
		id         : element.id,
		content    : element.content,
		user       : {
			id: element?.user?.id, username: element?.user?.name, headerImage: element?.user?.image
		},
		isLike     : element.isLiked,
		isNotLike  : element.isUnLiked,
		child      : element.child_size
	};
}

export type { returnType };
export { map };
export default CommentType;