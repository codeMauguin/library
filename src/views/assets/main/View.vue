<!-- @format -->

<template>
    <public-menu
            :balance="readerStore.borrowALibraryCard.total - readerStore.borrowALibraryCard.borrowed"
            :data="{ isCollapse, ElMenus: store.isAdmin ? adminAside : readerAside }"
            :home="store.state?'/main':'/reader/allBooks'"
            @closed="closed">
        <template #header>
            <tags-pilot style="height: 100%"/>
        </template>
    </public-menu>
</template>
<script lang="ts" setup>
import TagsPilot                   from "@/components/TagsPilot/index.vue";
import { adminAside, readerAside } from "@/config/AsideData";
import { useUserInfoStore }        from "@/stores/counter";
import { useReaderStore }          from "@/stores/readerStore";
import PublicMenu                  from "@/views/assets/main/PublicMenu.vue";
import { getBorrowedCard }         from "@/views/reader/init/init";
import { onBeforeMount, ref }      from "vue";

const readerStore = useReaderStore();
const store = useUserInfoStore();
const isCollapse = ref<boolean>(true);
const closed = () => (isCollapse.value = !isCollapse.value);
onBeforeMount(() => {
	if (store.state) {
		if (!store.isAdmin) {
			getBorrowedCard(store.user.id);
		} else {
		
		}
	}
});
</script>

<style scoped>
.el-header {
    padding: 0;
    background: #313743;
}

.el-header,
.el-main {
    height: 15vh;
}

.el-main {
    height: 85vh;
}

.el-aside {
    height: 85vh;
    -webkit-user-select: none;
    user-select: none;
    background: #313743;
}

.header-user {
    width: 200px;
    height: 100%;
    border: 1px solid red;
    margin-left: auto;
    display: flex;
    align-items: center;
}

.el-sub-menu {
    background: #a5a5b2;
}

.el-menu {
    background: #313743;
    border-right: none;
}

.container {
    display: flex;
    align-items: center;
    width: 100%;
    height: 100%;
}

.header-image {
    height: 15vh;
    width: 200px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.refresh {
    animation: rotate 0.7s linear 1;
}

@keyframes rotate {
    0% {
        transform: rotate(0deg);
    }
    25% {
        transform: rotate(90deg);
    }
    50% {
        transform: rotate(180deg);
    }
    75% {
        transform: rotate(270deg);
    }
    100% {
        transform: rotate(360deg);
    }
}
</style>