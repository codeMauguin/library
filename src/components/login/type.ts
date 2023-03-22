export function valid(rule: any, value: any, callback: (args?: any) => void): void {
	if (!Assert.hasText(value)) {
		callback(new Error(`${rule.field} is null`));
	} else {
		callback();
	}
}

export const user:Record<string, any> = reactive({
								 username: "", password: "", code: ""
							 });