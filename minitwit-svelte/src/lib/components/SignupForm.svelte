<script lang="ts">
	import { cn } from "$lib/utils.js";
	import { Button } from "$lib/components/ui/button/index.js";
	import * as Card from "$lib/components/ui/card/index.js";
	import * as Field from "$lib/components/ui/field/index.js";
	import { Input } from "$lib/components/ui/input/index.js";
	import type { HTMLAttributes } from "svelte/elements";

	let {
		class: className,
		onregister,
		error,
		loading,
		...restProps
	}: HTMLAttributes<HTMLDivElement> & {
		onregister: (username: string, email: string, password: string, passwordConfirm: string) => void;
		error: string | null;
		loading: boolean;
	} = $props();

	const id = $props.id();

	let username = $state('');
	let email = $state('');
	let password = $state('');
	let passwordConfirm = $state('');

	function handleSubmit(e: Event) {
		e.preventDefault();
		onregister(username, email, password, passwordConfirm);
	}

	const bgStyle = `
		background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='none' stroke='white' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M16 7h.01'/%3E%3Cpath d='M3.4 18H12a8 8 0 0 0 8-8V7a4 4 0 0 0-7.28-2.3L2 20'/%3E%3Cpath d='m20 7 2 .5-2 .5'/%3E%3Cpath d='M10 18v3'/%3E%3Cpath d='M14 17.75V21'/%3E%3Cpath d='M7 18a6 6 0 0 0 3.84-10.61'/%3E%3C/svg%3E");
		background-size: 128px 64px;
		background-repeat: repeat-x;
		background-position: 0px 400px;
	`;
</script>

<div class={cn("flex flex-col gap-6", className)} {...restProps}>
	<Card.Root class="overflow-hidden p-0">
		<Card.Content class="grid p-0 md:grid-cols-2">
			<form class="p-6 md:p-8" onsubmit={handleSubmit}>
				<Field.Group>
					<div class="flex flex-col gap-2">
						<h1 class="text-2xl font-bold">Create an account</h1>
						<p class="text-sm text-muted-foreground text-balance">
							Enter your information to create your account
						</p>
					</div>
					{#if error}
						<p class="text-destructive text-sm text-center">{error}</p>
					{/if}
					<Field.Field>
						<Field.Label for="username">Username</Field.Label>
						<Input id="username" type="username" placeholder="John Doe" bind:value={username} required disabled={loading} />
					</Field.Field>
					<Field.Field>
						<Field.Label for="email">Email</Field.Label>
						<Input id="email" type="email" placeholder="john.doe@example.com" bind:value={email} required disabled={loading} />
					</Field.Field>
					<Field.Field>
						<Field.Field class="grid grid-cols-2 gap-4">
							<Field.Field>
								<Field.Label for="password">Password</Field.Label>
								<Input id="password" type="password" bind:value={password} required disabled={loading} />
							</Field.Field>
							<Field.Field>
								<Field.Label for="passwordConfirm">Confirm Password</Field.Label>
								<Input id="passwordConfirm" type="password" bind:value={passwordConfirm} required disabled={loading} />
							</Field.Field>
						</Field.Field>
					</Field.Field>
					<Field.Field>
						<Button type="submit" disabled={loading}>
							{loading ? "Signing up..." : "Create Account"}
						</Button>
					</Field.Field>
					<Field.Description class="text-center">
						Already have an account? <a href="/login">Sign in</a>
					</Field.Description>
				</Field.Group>
			</form>
			<div class="relative hidden md:flex bg-[#1DA1F2]" style={bgStyle}></div>
		</Card.Content>
	</Card.Root>
</div>
