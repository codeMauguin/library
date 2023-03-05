<template>
	<div>
		<el-skeleton :loading="ref===err||ref.value===err" :rows="10" animated>
			<template #default>
				<suspense>
					<component :is="ref"></component>
				</suspense>
			</template>
		</el-skeleton>
	</div>
</template>
<script lang="ts" setup>
import err from "@/views/error/404.vue";

const ref = shallowRef(err);
const store = useUserInfoStore();
if (store.auth.localeCompare("admin") === 0) {
	import("@/views/assets/main/adminPanel/index.vue").then(module => {
		ref.value = module.default;
	});
} else {
	import("@/views/assets/main/readerPanel/index.vue").then(module => {
		ref.value = module.default;
	});
}
</script>