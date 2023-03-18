<!-- @format -->

<template>
    <div
            ref="body"
            class="my-body">
        <div class="el-form">
            <el-space>
                <Transition
                        mode="out-in"
                        name="el-zoom-in-center">
                    <div
                            v-if="view === LongList"
                            class="iconfont icon-yunpanlogo- icon-style"
                            @click="view = MyTable">
                    </div>
                    <div
                            v-else
                            class="iconfont icon-liebiaomoshi icon-style"
                            @click="view = LongList">
                    </div>
                </Transition>
                <CommonSearch
                        @refresh="viewRef?.refresh"
                        @search="viewRef?.search"/>
            </el-space>
        </div>
        <transition
                mode="out-in"
                name="el-zoom-in-center">
            <KeepAlive>
                <component
                        :is="view"
                        ref="viewRef"/>
            </KeepAlive>
        </transition>
        <el-affix
                v-if="!infoStore.isAdmin"
                :offset="150"
                class="affix"
                position="bottom"
                target=".content"
                @click="open">
            <div class="item">
                <el-badge
                        :value="shoppingCart.size"
                        type="primary">
                    <i class="iconfont icon-gouwuchekong affix-icon"></i>
                </el-badge>
            </div>
        </el-affix>
        <Drawer
                v-model:show="openShop"
                :books="shoppingCart._"
                :closed="closed"
                class="drawer"
                @delete="(row:Book)=>shoppingCart.removeAProduct(row)"/>
    </div>
</template>

<script lang="ts" setup>
import { useUserInfoStore }   from "@/stores/counter";
import { useReaderStore }     from "@/stores/readerStore";
import { useShopStore }       from "@/stores/ShopStore";
import type Book              from "@/types/Book";
import { closed, openShop }   from "@/utils/Gloab";
import CommonSearch           from "@/views/assets/search/CommonSearch.vue";
import LongList               from "@/views/reader/AllBookChild/LongList.vue";
import { default as MyTable } from "@/views/reader/AllBookChild/table.vue";
import Drawer                 from "@/views/reader/Drawer.vue";
import { ElSpace }            from "element-plus";
import { Ref, Transition }    from "vue";

const view: Ref<InstanceType<typeof LongList> | InstanceType<typeof MyTable>> = shallowRef<
	InstanceType<typeof LongList> | InstanceType<typeof MyTable>
>(LongList);
const viewRef: Ref<InstanceType<typeof LongList | typeof MyTable> | null> = ref<InstanceType<
	typeof LongList | typeof MyTable
> | null>(null);

const body = ref<HTMLElement>();
const open = () => (openShop.value = true);
useReaderStore();
const infoStore = useUserInfoStore();
const shoppingCart = useShopStore();

const router = useRouter();
onBeforeMount(() => {
	const readerStore = useReaderStore();
	if (infoStore.state && !infoStore.isAdmin) {
		if (Assert.hasLength(readerStore.borrowALibraryCard.id)) {
			readerStore.loadBorrowed();
		} else {
			readerStore.message(() => {
				readerStore.loadBorrowed();
			});
		}
	}
});
onMounted(() => {
});
</script>

<style scoped>
.my-body {
    height: 100%;
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: 10px;
}


.el-form {
    position: sticky;
    flex: 0 0 auto;
    margin-bottom: 10px;
}

.affix {
    position: absolute;
    bottom: 0;
    left: calc(100vw - 65px - 60px);
    z-index: 50;
}

.affix-icon {
    font-size: 30px !important;
}

.item:hover {
    background: rgba(209, 93, 93, 0.5);
}

.input-with-select .el-input-group__prepend {
    background-color: var(--el-fill-color-blank);
}

.item {
    cursor: pointer;
    width: 65px;
    position: fixed;
    height: 65px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(0, 0, 0, 0.5);
    border-radius: 50%;
}

.icon-style {
    display: inline-block;
    font-size: 20px;
    color: aqua;
}

</style>