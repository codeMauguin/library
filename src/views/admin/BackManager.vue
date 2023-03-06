<template>
    <div class="_body">
        <div class="header">
            <el-input v-model="searchId" placeholder="查询编号" size="large" style="width: 200px"
                      @input="searchId=searchId.replace(/^(0+)|[^\d]+/g,'')"/>
            <el-button color="#FEC195" @click.stop="search">
                <template #icon>
                    <i class="iconfont icon-sousuo"/>
                </template>
                搜索
            </el-button>
            <el-space alignment="center" size="large"><span> 正常用户:</span>
                <label class="switch">
                    <input v-model="normal" class="chk" type="checkbox">
                    <span class="slider"></span>
                </label>
                <span>已拉黑:</span>
                <label class="switch">
                    <input v-model="blacklist" class="chk" type="checkbox">
                    <span class="slider"></span>
                </label></el-space>
            <el-button type="primary" @click.stop="filter">
                <template #icon>
                    <i class="iconfont icon-shaixuan"/>
                </template>
                筛选
            </el-button>
            <el-button type="primary" @click.stop="clear">
                <template #icon>
                    <i class="iconfont icon-qingchu"/>
                </template>
                清除
            </el-button>
        </div>
        <el-table :data="pageInstance.view" border>
            <template #empty>
                <el-empty
                        :description="`没有${normal&&blacklist?'':!normal&&!blacklist?'请选择一项条件':normal?'正常':'黑名单'}用户`"/>
            </template>
            <el-table-column label="用户编号" prop="id"/>
            <el-table-column label="用户姓名" prop="name"/>
            <el-table-column
                    label="状态">
                <template #default="{row}">
                    <el-tag :type="row.status===1?'danger':''" hit size="large">{{
												row.status === 1 ? "已拉黑" : "正常"
                        }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作">
                <template #default="{row}">
                    <el-button-group size="large">
                        <el-button v-if="row.status===0" class="btn_back" @click="pull(row)"><i
                                class="iconfont icon-lahei"
                                style="margin-right: 10px"/> 拉黑
                        </el-button>

                        <el-button v-else class="button-name" type="primary" @click="restoreInto(row)">
                            <i class="iconfont icon-yichu"
                               style="margin-right: 10px"/>恢复
                        </el-button>
                    </el-button-group>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination
                v-model:current-page="pageInstance.pageInfo.currentIndex"
                v-model:page-size="pageInstance.pageInfo.pageSize"
                v-model:total="pageInstance.pageInfo.totalSize"
                :page-sizes="[5, 10]"
                background
                class="el-pagination"
                layout="total,sizes,prev, pager, next,jumper"
                @current-change="index => pageInstance.next(index)"/>
    </div>
</template>

<script lang="ts" setup>
import instance                                  from "@/axios";
import type { LoadPage, PageData, PageInstance } from "@/components/Pages/Page2";
import user                                      from "@/types/User";
import type { Ref }                              from "vue";

const searchId: Ref<string> = ref<string>("");
const normal: Ref<boolean> = ref<boolean>(true);
const blacklist: Ref<boolean> = ref<boolean>(true);


type PrivateUser = Omit<user, "permissions">
const pageInstance: PageInstance<PrivateUser> = new Page(new class implements LoadPage<PrivateUser> {
	public async load(offset: number, pageSize: number, size: number, last?: PrivateUser):
		Promise<PageData<PrivateUser>> {
		return instance.get(AdminAPI.ALREADY_A, {params: { offset, pageSize,
                           size, isNormal: normal.value, isBlack: blacklist.value, searchId: searchId.value}})
					   .then(({data: {data}}) => data);
	}
}());

const EMPTY_PAGE = new LocalVirtualPage([], pageInstance.pageInfo);
const error=throttle(() => warning('输入搜索用户编号'), 500);
function search() {
	IfStream.of(Assert.hasText(searchId.value)).then(filter).catch(error);
}

function filter(): void {
	
	IfStream.of(!normal.value && !blacklist.value).then(() => {
		pageInstance.virtual(EMPTY_PAGE);
	}).catch(() => {
		const searchPage: PageInstance<PrivateUser> = new Page(new class implements LoadPage<PrivateUser> {
			public async load(offset: number, pageSize: number, size: number, last?: PrivateUser):
				Promise<PageData<PrivateUser>> {
				return instance.get(AdminAPI.ALREADY_A, {
								   params: {
									   offset, pageSize,
									   size, isNormal: normal.value, isBlack: blacklist.value, searchId: searchId.value
								   }
							   })
							   .then(({data: {data}}) => data);
			}
		}());
		pageInstance.virtual(searchPage);
	});
}

function clear(): void {
	pageInstance.virtual();
	normal.value = true;
	blacklist.value = true;
	searchId.value = "";
}

function restoreInto(row: PrivateUser): void {
	IfStream.of(instance.put(AdminAPI.restore, row.id)
						.then(({data: {data}}) => data))
			.then(() => {
				row.status = 0;
				success("已恢复");
			})
			.catch((e) => {
				error("恢复失败");
			});
}

function pull(row: PrivateUser): void {
	IfStream.of(instance.put(AdminAPI.PULL, row.id)
						.then(({data: {data}}) => data))
			.then(() => {
				row.status = 1;
				success("已拉黑");
			})
			.catch((e) => {
				warning("拉黑失败");
			});
}
</script>

<style scoped>
@import "@/views/css/btn_back.css";
@import url("@/components/login/btn.css");
@import url("@/views/css/tuigan.css");

._body {
    display: flex;
    flex-direction: column;
    height: 100%;
}

.header {
    flex: 0 0 auto;
    padding: 10px;
    display: flex;
    gap: 10px;
    align-items: center;
}

.el-table {
    height: 85%;
    flex: 1;
}

.el-pagination {
    flex: 0 0 auto;
    align-self: center;
}
</style>