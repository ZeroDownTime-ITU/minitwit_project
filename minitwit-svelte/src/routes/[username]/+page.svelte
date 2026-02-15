<script lang="ts">
    import { onMount } from 'svelte';
    import { page } from '$app/state'; 
    import { user } from '$lib/stores';
    import MessageComponent from '$lib/components/Message.svelte';
    import type { Message, UserProfileData } from '$lib/types';

    // State using Svelte 5 Runes
    let profileData = $state<UserProfileData | null>(null);
    let loading = $state(true);

    // Reactive variable that updates if the URL changes (e.g., clicking a different user)
    const username = $derived(page.params.username);

    async function loadData() {
        loading = true;
        const res = await fetch(`/api/user/${username}`);
        if (res.ok) {
            profileData = await res.json();
        }
        loading = false;
    }

    // Reload data if the username in the URL changes
    $effect(() => {
        username; // dependency
        loadData();
    });

    async function toggleFollow() {
        if (!profileData) return;
        const action = profileData.followed ? 'unfollow' : 'follow';
        const res = await fetch(`/api/user/${username}/${action}`, { method: 'POST' });
        if (res.ok) {
            profileData.followed = !profileData.followed;
        }
    }
</script>

<svelte:head>
    <title>{username}'s Timeline | MiniTwit</title>
</svelte:head>

{#if loading}
    <p>Loading profile...</p>
{:else if profileData}
    <h2>{username}'s Timeline</h2>

    {#if $user}
        <div class="followstatus">
            {#if $user.username === username}
                This is you!
            {:else}
                You are {profileData.followed ? '' : 'not yet'} following this user.
                <button 
                    class={profileData.followed ? 'unfollow' : 'follow'} 
                    onclick={toggleFollow}
                >
                    {profileData.followed ? 'Unfollow' : 'Follow'} user
                </button>
            {/if}
        </div>
    {/if}

    <ul class="messages">
        {#each profileData.messages as msg}
            <MessageComponent {msg} />
        {:else}
            <li><em>There's no message so far.</em></li>
        {/each}
    </ul>
{/if}