<script lang="ts">
    import { user } from '$lib/stores';
    import Timeline from '$lib/components/Timeline.svelte';
    import * as Card from "$lib/components/ui/card/index.js";
    import { Textarea } from "$lib/components/ui/textarea/index.js";
    import { Button } from "$lib/components/ui/button/index.js";
    import Separator from '$lib/components/ui/separator/separator.svelte';
    import PageWrapper from '$lib/components/PageWrapper.svelte';
    import { invalidateAll } from '$app/navigation';

    let { data } = $props();
    
    let newMessageText = $state("");

    async function postMessage(event: Event) {
        event.preventDefault();
        const response = await fetch('/api/add-message', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ text: newMessageText })
        });

       if (response.ok) {
            newMessageText = "";
            await invalidateAll();
        }
    }
</script>

<PageWrapper>
    <Card.Root class="overflow-hidden p-0">
        <Card.Content class="p-0">
            <div class="flex flex-col gap-4 p-6 md:p-8">
                <h1 class="text-2xl font-bold">My timeline</h1>
                
                {#if $user}
                    <div class="flex flex-col gap-3">
                        <h2 class="text-sm font-medium text-muted-foreground">
                            What's on your mind, {$user.username}?
                        </h2>
                        <form onsubmit={postMessage} class="flex flex-col gap-3">
                            <Textarea 
                                bind:value={newMessageText} 
                                placeholder="Share something..." 
                                rows={3}
                                onkeydown={(e) => {
                                    if (e.key === 'Enter' && !e.shiftKey) {
                                        e.preventDefault();
                                        if (newMessageText.trim()) postMessage(e);
                                    }
                                }}
                            />
                            <div class="flex justify-end">
                                <Button type="submit" disabled={!newMessageText.trim()}>Share</Button>
                            </div>
                        </form>
                    </div>
                    <Separator/>
                {/if}

                <Timeline messages={data.messages} />
            </div>
        </Card.Content>
    </Card.Root>
</PageWrapper>