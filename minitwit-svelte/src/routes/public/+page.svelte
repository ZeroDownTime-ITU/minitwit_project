<script lang="ts">
	import { onMount } from 'svelte';
	import MessageComponent from '$lib/components/Message.svelte';
	import type { Message } from '$lib/types';

	// State for our messages
	let messages = $state<Message[]>([]);
	let loading = $state(true);
	let error = $state<string | null>(null);

	async function fetchPublicTimeline() {
		try {
			const res = await fetch('/api/public');
			if (res.ok) {
				messages = await res.json();
			} else {
				error = "Failed to load the public timeline.";
			}
		} catch (e) {
			error = "Could not connect to the server.";
		} finally {
			loading = false;
		}
	}

	onMount(() => {
		fetchPublicTimeline();
	});
</script>

<svelte:head>
	<title>Public Timeline | MiniTwit</title>
</svelte:head>

<h2>Public Timeline</h2>

{#if loading}
	<p>Loading messages...</p>
{:else if error}
	<p class="error">{error}</p>
{:else}
	<ul class="messages">
		{#each messages as msg}
			<MessageComponent {msg} />
		{:else}
			<li><em>There's no message so far.</em></li>
		{/each}
	</ul>
{/if}