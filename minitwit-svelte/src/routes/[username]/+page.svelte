<script lang="ts">
    import { page } from '$app/state'; 
    import { user } from '$lib/stores';
    import Timeline from '$lib/components/Timeline.svelte';
    import PageWrapper from '$lib/components/PageWrapper.svelte';
    import * as Card from "$lib/components/ui/card/index.js";
    import * as Item from "$lib/components/ui/item/index.js";
    import { Button } from "$lib/components/ui/button/index.js";
    import InfoIcon from "@lucide/svelte/icons/info";
    import UserX from "@lucide/svelte/icons/user-x";
    import UserCheck from "@lucide/svelte/icons/user-check";
    import { toast } from 'svelte-sonner';

    let { data } = $props();
    const username = $derived(page.params.username);

    let isFollowed = $state<boolean | null>(null);

    async function toggleFollow() {
        const action = isFollowed ? 'unfollow' : 'follow';
        const response = await fetch(`/api/${action}/${username}`, { method: 'POST' });
        if (response.ok) {
            isFollowed = !isFollowed;
            toast.success(`You are ${isFollowed ? 'now following' : 'no longer following'} "${username}"`);
        }
    }
</script>

<svelte:head>
    <title>{username}'s Timeline | MiniTwit</title>
</svelte:head>

<PageWrapper>
    <Card.Root class="overflow-hidden p-0">
        <Card.Content class="p-0">
            <div class="flex flex-col gap-4 p-6 md:p-8">
                <h1 class="text-2xl font-bold">{username}'s timeline</h1>

                {#await data.profile}
                    <div class="h-10 w-full animate-pulse bg-muted rounded-md"></div>
                {:then profileData}
                    {#if profileData}
                        {#snippet followButton()}
                             {@const followed = isFollowed ?? profileData.followed}
                             <Button 
                                variant={followed ? 'outline' : 'default'}
                                size="sm"
                                onclick={toggleFollow} >
                                {followed ? 'Unfollow' : 'Follow'} user
                            </Button>
                        {/snippet}

                        {#if $user}
                            <div class="followstatus">
                                {#if $user.username === username}
                                   <Item.Root variant="outline" size="sm">
                                        <Item.Media><InfoIcon class="size-5 text-[#1DA1F2]" /></Item.Media>
                                        <Item.Content><Item.Title class="text-[#1DA1F2]">This is you!</Item.Title></Item.Content>
                                    </Item.Root>
                                {:else}
                                    <Item.Root variant="outline" size="sm">
                                       <Item.Media>
                                            {#if isFollowed ?? profileData.followed}
                                                <UserCheck class="size-5 text-[#1DA1F2]" />
                                            {:else}
                                                <UserX class="size-5 text-[#1DA1F2]" />
                                            {/if}
                                        </Item.Media>       
                                        <Item.Content class="flex flex-row justify-between items-center">
                                            <Item.Title>
                                                You are {isFollowed ?? profileData.followed ? 'currently' : 'not yet'} following this user.
                                            </Item.Title>
                                            {@render followButton()}
                                        </Item.Content>
                                    </Item.Root>
                                {/if}
                            </div>
                        {/if}
                    {/if}
                {/await}

                <Timeline messages={data.profile.then(p => p?.messages ?? [])} />
            </div>
        </Card.Content>
    </Card.Root>
</PageWrapper>