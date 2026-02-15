<script lang="ts">
	import '../app.css'; // Import your CSS here
	import { user, flashes } from '$lib/stores'; // We'll define these in a second
	
	// Svelte 5 uses 'children' as a snippet for page content
	let { children } = $props();
</script>

<div class="page">
	<h1>MiniTwit</h1>
	
	<div class="navigation">
		{#if $user}
			<a href="/">my timeline</a> |
			<a href="/public">public timeline</a> |
			<a href="/logout">sign out [{$user.username}]</a>
		{:else}
			<a href="/public">public timeline</a> |
			<a href="/register">sign up</a> |
			<a href="/login">sign in</a>
		{/if}
	</div>

	{#if $flashes.length > 0}
		<ul class="flashes">
			{#each $flashes as message}
				<li>{message}</li>
			{/each}
		</ul>
	{/if}

	<div class="body">
		{@render children()} 
	</div>

	<div class="footer">
		MiniTwit &mdash; A Svelte & Javalin Application
	</div>
</div>

<style>
    /* You can paste your existing style.css content here 
       or keep it in app.css */
</style>