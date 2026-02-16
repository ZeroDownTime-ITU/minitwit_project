<script lang="ts">
    import { onMount } from 'svelte';
    import { user } from '$lib/stores';
    import { goto } from '$app/navigation';
    import MessageList from '$lib/components/MessageList.svelte';
    import type { Message } from '$lib/types';

    let messages = $state<Message[]>([]);
    let newMessageText = $state("");

    onMount(async () => {
        if (!$user) {
            goto('/public');
            return;
        }

        const response = await fetch('/api/user-timeline');
        if (response.ok) {
            messages = await response.json();
        }
    });

    async function postMessage(event: Event) {
        event.preventDefault();
        
        const response = await fetch('/api/add-message', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ text: newMessageText })
        });

        if (response.ok) {
            const newMessage = await response.json();
            messages = [newMessage, ...messages];
            newMessageText = ""; // Clear the input field after posting
        }
    }
</script>

<h2>My Timeline</h2>

{#if $user}
    <div class="twitbox">
        <h3>What's on your mind {$user.username}?</h3>
        <form onsubmit={postMessage}>
            <p>
                <input type="text" bind:value={newMessageText} size="60"><!--
                --><input type="submit" value="Share">
            </p>
        </form>
    </div>
{/if}

<MessageList {messages} />