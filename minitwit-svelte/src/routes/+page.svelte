<script lang="ts">
    import { onMount } from 'svelte';
    import type { Message } from '$lib/types';

    let messages: Message[] = $state([]);
    let loading: boolean = $state(true);
    let errorMessage: string | null = $state(null);

    onMount(async () => {
        try {
            const response = await fetch('http://localhost:7070/login');
            
            if (!response.ok) {
                throw new Error(`Server responded with ${response.status}`);
            }

            const data = await response.json();
            messages = data; 
        } catch (err) {
            errorMessage = err instanceof Error ? err.message : 'Unknown error';
            console.error("Connection failed:", err);
        } finally {
            loading = false;
        }
    });
</script>

<h1>Minitwit Timeline</h1>

{#if loading}
    <p>Loading tweets...</p>
{:else if errorMessage}
    <p style="color: red;">Error: {errorMessage}</p>
{:else if messages.length > 0}
    <ul>
        {#each messages as msg}
            <li>
                <strong>{msg.username}</strong>: {msg.content}
            </li>
        {/each}
    </ul>
{:else}
    <p>No messages yet!</p>
{/if}