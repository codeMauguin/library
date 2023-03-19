/** @format */

type ResponseApi<T> = Readonly<{ code: number; error: string; data: T }>;
export default ResponseApi;