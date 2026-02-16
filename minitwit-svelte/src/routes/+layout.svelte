<script lang="ts">
	import '../app.css';
	import { user, flashes } from '$lib/stores';
    import { beforeNavigate } from '$app/navigation';
	
	let { children } = $props();

	beforeNavigate((navigation) => {
        // Only clear flashes if the user clicked a link ('link') or used the back/forward buttons.
        if (navigation.type === 'link' || navigation.type === 'popstate') {
            flashes.set([]);
        }
    });
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