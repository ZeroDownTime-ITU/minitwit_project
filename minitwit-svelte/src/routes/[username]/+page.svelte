<script lang="ts">
    import { page } from '$app/state'; 
    import { flashes, user } from '$lib/stores';
    import MessageList from '$lib/components/MessageList.svelte';
    import type { UserProfileData } from '$lib/types';

    let profileData = $state<UserProfileData | null>(null);
    let loading = $state(true);
    const username = $derived(page.params.username);

    async function loadData() {
        const response = await fetch(`/api/user/${username}`);
        if (response.ok) {
            profileData = await response.json();
        }
    }

    // Reload data if the username in the URL changes
    $effect(() => {
        username;
        loadData();
    });

    async function toggleFollow() {
        if (!profileData) return;
        const action = profileData.followed ? 'unfollow' : 'follow';
        const response = await fetch(`/api/${action}/${username}`, { method: 'POST' });
        if (response.ok) {
            profileData.followed = !profileData.followed;
            flashes.set([`You are ${profileData.followed ? 'now following' : 'no longer following'} "${username}"`]);
        }
    }
</script>

<svelte:head>
    <title>{username}'s Timeline | MiniTwit</title>
</svelte:head>

{#if profileData}
    <h2>{username}'s Timeline</h2>

    {#if $user}
        <div class="followstatus">
            {#if $user.username === username}
                This is you!
            {:else}
                You are {profileData.followed ? 'currently' : 'not yet'} following this user.
                <button 
                    class={profileData.followed ? 'unfollow' : 'follow'} 
                    onclick={toggleFollow}
                >
                    {profileData.followed ? 'Unfollow' : 'Follow'} user
                </button>
            {/if}
        </div>
    {/if}

    <MessageList messages={profileData.messages} />
{/if}