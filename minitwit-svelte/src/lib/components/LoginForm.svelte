<script lang="ts">
	import * as Card from "$lib/components/ui/card/index.js";
	import {
		FieldGroup,
		Field,
		FieldLabel,
		FieldDescription,
	} from "$lib/components/ui/field/index.js";
	import { Input } from "$lib/components/ui/input/index.js";
	import { Button } from "$lib/components/ui/button/index.js";
	import { cn } from "$lib/utils.js";
	import type { HTMLAttributes } from "svelte/elements";
	import BirdIcon from "@lucide/svelte/icons/bird";

	let {
		class: className,
		onlogin,
		error,
		loading,
		...restProps
	}: HTMLAttributes<HTMLDivElement> & {
		onlogin: (username: string, password: string) => void;
		error: string | null;
		loading: boolean;
	} = $props();

	const id = $props.id();

	let username = $state('');
	let password = $state('');

	function handleSubmit(e: Event) {
		e.preventDefault();
		onlogin(username, password);
	}
</script>

<div class={cn("flex flex-col gap-6", className)} {...restProps}>
	<Card.Root class="overflow-hidden p-0">
		<Card.Content class="grid p-0 md:grid-cols-2">
			<form class="p-6 md:p-8" onsubmit={handleSubmit}>
				<FieldGroup>
					<div class="flex flex-col gap-2">
						<h1 class="text-2xl font-bold">Login</h1>
						<p class="text-sm text-muted-foreground text-balance">
							Enter your credentials to login to your account
						</p>
					</div>
					{#if error}
						<p class="text-destructive text-sm text-center">{error}</p>
					{/if}
					<Field>
						<FieldLabel for="username-{id}">Username</FieldLabel>
						<Input id="username-{id}" type="text" bind:value={username} required disabled={loading} />
					</Field>
					<Field>
						<div class="flex items-center">
							<FieldLabel for="password-{id}">Password</FieldLabel>
						</div>
						<Input id="password-{id}" type="password" bind:value={password} required disabled={loading} />
					</Field>
					<Field>
						<Button type="submit" disabled={loading}>
							{loading ? "Signing in..." : "Login"}
						</Button>
					</Field>
					<FieldDescription class="text-center">
						Don't have an account? <a href="/register">Sign up</a>
					</FieldDescription>
				</FieldGroup>
			</form>
			<div class="bg-[#1DA1F2] relative hidden md:flex items-center justify-center">
				<BirdIcon class="size-32 text-white opacity-80" />
			</div>
		</Card.Content>
	</Card.Root>
</div>