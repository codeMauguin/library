/** @format */

const HttpApi = {
	login               : "/user/v2/login",
	logout              : "/user/v2/logout",
	BASEURL             : "http://localhost:7000",
	userInfo            : "/user/v2/userInfo",
	getAllBooks         : "/reader/v2/books",
	getBook             : "/reader/v2/book/",
	getComments         : "/comment/getRoots/",
	getCommentSize      : "/comment/size/root/",
	commentCommit       : "/comment/comment/commit",
	deleteComment       : "/comment/delete",
	getCommentChildren  : "/comment/comment/children",
	getCard             : "/user/v2/card",
	getBorrowed         : "/order/book/borrowed",
	createOrderId       : "/order/create",
	commitOrder         : "order/commit",
	SEARCH_KEYWORD      : "/reader/v2/book/search/key_word",
	SEARCH_BOOK         : "/reader/v2/book/search",
	getOrders           : "/order/getOrders",
	getOrdersByCondition: "/order/getOrders/condition",
	RETURN_BOOK         : "/order/returnBook",//还书,
	GET_HAS_BORROWED    : "/order/reader/borrowed",//获取已经借阅书籍id
	COUNT               : "/order/reader/statistics",
	REGISTRY            : "/user/registry",
	COMMENT_LIKE        : "/comment/like",
	COMMENT_UNLIKE      : "/comment/unlike",
	COMMENT_CANCEL      : "comment/cancelLike",
	REVISE_USER         : "user/v2/user/revise",
	RENEWAL_BOOK        : "order/book/renewal"
};

export default HttpApi;
export const AdminAPI = {
	uploadBooks           : "/reader/api/book/task",
	updateBook            : "/reader/api/book/update",
	takedown              : "/reader/api/book/takeDown",
	shelves               : "/reader/api/book/shelves",
	ALL_READER_BORROWED   : "/order/borrowed/all",
	ALREADY_ACCOUNT       : "/user/account",
	ALREADY_A             : "/user/account/all",
	FLAG_BORROWED         : "/order/api/book/borrowed",
	UPDATE_READER_BORROWED: "/user/api/user/total",
	REGISTRY              : "user/v2/registry",
	STATISTIC             : "/order/api/statistic",
	STATISTIC_YEAR        : "/order/api/statistic/year",
	PULL                  : "/user/api/pullIntoBackList",
	restore               : "/user/api/restoreInto"
};