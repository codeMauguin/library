/** @format */

import vue                     from "@vitejs/plugin-vue";
import { fileURLToPath, URL }  from "node:url";
import { terser }              from "rollup-plugin-terser";
import AutoImport              from "unplugin-auto-import/vite";
import ElementPlus             from "unplugin-element-plus/vite";
import { ElementPlusResolver } from "unplugin-vue-components/resolvers";
import Components              from "unplugin-vue-components/vite";
import { defineConfig }        from "vite";
import PluginImportToCDN       from "vite-plugin-cdn-import";

/** @type {import("vite").UserConfig} */
export default defineConfig({
								plugins: [
									vue(),
									// visualizer({open: true}),
									PluginImportToCDN({
														  modules: []
													  }),
									AutoImport({
												   imports                : ["vue", "vue-router"],
												   dirs                   : ["./src/**"],
												   defaultExportByFilename: true,
												   resolvers              : [ElementPlusResolver()],
												   sourceMap              : false,
												   dts                    : "./src/auto-import.d.ts"
											   }),
									Components({
												   resolvers: [ElementPlusResolver()]
											   }),
									ElementPlus({})
								],
								resolve: {
									alias: {
										"@": fileURLToPath(new URL("./src", import.meta.url))
									}
								},
								build  : {
									sourcemap      : false
									, rollupOptions: {
										output   : {
											sourcemap         : false,
											esModule          : true,
											dynamicImportInCjs: true
										}
										, plugins: [terser]
									}
								}
							});