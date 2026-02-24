<script lang="ts">
    import type { Message } from '$lib/types';
    import MessageList from './MessageList.svelte';
    import MessageSkeleton from './MessageSkeleton.svelte';

   let { messages }: { messages: Promise<Message[]> | Message[] } = $props();
</script>

{#await messages}
    {#each Array(5) as _, i}
        <MessageSkeleton isLast={i === 4} />
    {/each}
{:then resolved}
    {#if resolved.length > 0}
        <MessageList messages={resolved} />
    {:else}
        <p class="text-muted-foreground text-sm">No messages to show.</p>
    {/if}
{:catch error}
    <p class="text-destructive text-sm">Failed to load: {error.message}</p>
{/await}