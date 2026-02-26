<script lang="ts">
    import PageWrapper from '$lib/components/PageWrapper.svelte';
    import Timeline from '$lib/components/Timeline.svelte';
	import * as Card from "$lib/components/ui/card/index.js";

	let { data } = $props();
</script>

<svelte:head>
	<title>Public Timeline | MiniTwit</title>
</svelte:head>

<PageWrapper>
    <div class="flex flex-col gap-6">
        <Card.Root class="overflow-hidden p-0">
            <Card.Content class="p-0">
                <div class="flex flex-col gap-4 p-6 md:p-8">
                    <div class="flex flex-col gap-2">
                        <h1 class="text-2xl font-bold">Public timeline</h1>
                    </div>
                
                   <Timeline messages={data.messages} />
                </div>
            </Card.Content>
        </Card.Root>
    </div>
</PageWrapper>