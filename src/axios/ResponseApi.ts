/** @format */

type ResponseApi<T> = { code: number; error: string; data: T };
export default ResponseApi;
