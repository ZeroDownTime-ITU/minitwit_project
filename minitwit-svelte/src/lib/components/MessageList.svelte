<script lang="ts">
    import MessageComponent from './Message.svelte';
    import type { Message } from '$lib/types';

    let { messages }: { messages: Message[] } = $props();
</script>

<ul class="flex flex-col gap-4">
    {#each messages as msg, i}
        <MessageComponent {msg} isLast={i === messages.length - 1} />
    {:else}
        <li><em>There's no message so far.</em></li>
    {/each}
</ul>