<script lang="ts">
    import MessageComponent from './Message.svelte';
    import type { Message } from '$lib/types';

    let { messages }: { messages: Message[] } = $props();
</script>

<ul class="messages">
    {#each messages as msg}
        <MessageComponent {msg} />
    {:else}
        <li><em>There's no message so far.</em></li>
    {/each}
</ul>