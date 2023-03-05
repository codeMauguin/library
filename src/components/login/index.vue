<!-- @format -->

<template>
	<div class="body">
		<div class="container">
			<div class="login">
				<transition
					mode="out-in"
					name="el-zoom-in-top">
					<component :is="view"></component>
				</transition>
			</div>
			
			<div class="registry">
				<button  @click.stop="change"> {{ text }}
					<span></span>
				</button>
			</div>
		</div>
		
	</div>
</template>

<script lang="ts" setup>
import login                from "@/components/login/login.vue";
import registry             from "@/components/login/registry.vue";
import type { ComputedRef } from "vue";
import type { Ref }         from "vue";

const view: Ref<InstanceType<typeof login | typeof registry>> = shallowRef<
		InstanceType<typeof login | typeof registry>
	>(<any>(<unknown>login));
	const change = (): void =>
		(view.value = <any>(<unknown>(Object.is(view.value, login) ? registry : login)));
	const text: ComputedRef<" SIGN IN " | " SIGN UP " | string> = computed(() =>
		Object.is(view.value, login) ? "SIGN IN" : "SIGN UP"
	);
</script>

<style scoped>
	.body {
		background-image: linear-gradient(120deg, #3498db, #8e44ad);
		z-index: -1;
		font-family: Arial, sans-serif;
		display: flex;
		align-items: center;
		justify-content: center;
		height: 100vh;
		width: 100%;
	}

	.container {
		background-color: #fff;
		border-radius: 5px;
		width: auto;
		height: auto;
		display: flex;
		z-index: 1;
		flex-direction: row;
		gap: 30px;
	}

	.login {
		padding: 30px;
		min-width: 400px;
		flex: 1 50%;
	}

	.registry {
		flex: .5;
		min-width: 200px;
		height: 100%;
		min-height: 400px;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.submit-button span {
		display: block;
		padding: 10px;
	}
    button {
        border: none;
        display: block;
        position: relative;
        padding: 0.7em 2.4em;
        font-size: 18px;
        background: transparent;
        cursor: pointer;
        -webkit-user-select: none;
        -moz-user-select: none;
        -ms-user-select: none;
        user-select: none;
        overflow: hidden;
        color: royalblue;
        z-index: 1;
        font-family: inherit;
        font-weight: 500;
    }

    button span {
        position: absolute;
        left: 0;
        top: 0;
        width: 100%;
        height: 100%;
        background: transparent;
        z-index: -1;
        border: 4px solid royalblue;
    }

    button span::before {
        content: "";
        display: block;
        position: absolute;
        width: 8%;
        height: 500%;
        background: var(--lightgray);
        top: 50%;
        left: 50%;
        -webkit-transform: translate(-50%, -50%) rotate(-60deg);
        -ms-transform: translate(-50%, -50%) rotate(-60deg);
        transform: translate(-50%, -50%) rotate(-60deg);
        -webkit-transition: all 0.3s;
        transition: all 0.3s;
    }

    button:hover span::before {
        -webkit-transform: translate(-50%, -50%) rotate(-90deg);
        -ms-transform: translate(-50%, -50%) rotate(-90deg);
        transform: translate(-50%, -50%) rotate(-90deg);
        width: 100%;
        background: royalblue;
    }

    button:hover {
        color: white;
    }

    button:active span::before {
        background: #2751cd;
    }
	
</style>
