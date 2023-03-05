<!-- @format -->

<template>
  <div >
	<el-tooltip
		:content="prop.name"
		:hide-after="0"
		transition="el-fade-in-linear">
	  <div class="book-view">
		<el-image
			:initial-index="0"
			:src="prop.image"
			:z-index="999"
			fit="cover"
			preview-teleported
			style="width: 200px; height: 300px"/>
		<div class="zhe">
		  <div class="gou">
			<el-tooltip
				v-if="checkHasBorrowed(prop)"
				content="已借阅"
				effect="dark">
			  <i
				  class="iconfont icon-tishi"
				  style="color: white; font-size: 30px !important"></i>
			</el-tooltip>
			<el-tooltip
				v-else-if="checkHasChecked(prop)"
				content="取消选中"
				effect="dark">
			  <i
				  class="iconfont icon-xuanze"
				  style="color: white; font-size: 30px !important"
				  @click.stop="() => cancel(prop)"></i>
			</el-tooltip>
			<el-tooltip
				v-else
				content="➕购物车"
				effect="dark">
			  <i
				  class="iconfont icon-tianjiagouwuche add"
				  style="color: white; font-size: 30px !important"
				  @click.stop="
									() => {
										emits('addAShoppingCart', prop);
									}
								"></i>
			</el-tooltip>
		  </div>
		  
		  <div class="foot">
			<div class="foot-left">
				<!--              评论区操作-->
				<i
					class="iconfont add icon-tubiaopinglunshu"
					style="color: white; font-size: 30px !important"
					></i>
			</div>
		  </div>
		</div>
	  </div>
	</el-tooltip>
  </div>
</template>
<script lang="ts" setup>
import { useUserInfoStore }   from "@/stores/counter";
import type Book              from "@/types/Book";
import { ElImage, ElTooltip } from "element-plus";

const props = defineProps<{
  prop: Book;
  cancel: (book: Book) => void;
  checkHasBorrowed: (book: Book) => boolean;
  checkHasChecked: (book: Book) => boolean;
}>();
const store = useUserInfoStore();
const commentStore = useCommentStore();
const data=commentStore
	.getBookComment(props.prop.id, store.user.id)

const emits = defineEmits<{
  (e: "addAShoppingCart", book: Book): void;
}>();
</script>
<style scoped>
.book-view {
  width: 200px;
  height: 300px;
  position: relative;
  z-index: 1;
}

.book-view:hover .zhe {
  cursor: pointer;
  height: 100%;
  display: flex;
  box-shadow: var(--el-box-shadow-dark);
  transform: translateY(-2px);
}


.gou {
  width: 100%;
  height: 80%;
  flex-grow: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.zhe {
  position: absolute;
  left: 0;
  overflow: hidden;
  top: 0;
  width: 100%;
  height: 0%;
  background: rgba(13, 10, 49, 0.9);
  display: flex;
  flex-direction: column;
  z-index: 100;
}

.foot {
  width: 100%;
  height: 20%;
  display: flex;
}

.add:hover {
  color: #3967ff !important;
}

.foot-left {
  height: 100%;
  display: flex;
  flex-grow: 1;
  justify-content: center;
  align-items: center;
}
</style>