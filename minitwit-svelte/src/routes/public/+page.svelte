<script lang="ts">
	import MessageComponent from '$lib/components/Message.svelte';
	import type { Message } from '$lib/types';

	let { data } = $props();
</script>

<svelte:head>
	<title>Public Timeline | MiniTwit</title>
</svelte:head>

<h2>Public Timeline</h2>

{#if data.error}
    <p class="error">{data.error}</p>
{:else}
    <ul class="messages">
        {#each data.messages as msg}
            <MessageComponent {msg} />
        {:else}
            <li><em>There's no message so far.</em></li>
        {/each}
    </ul>
{/if}