<script lang="ts">
    import type { Message } from '$lib/types';
    import * as Avatar from "$lib/components/ui/avatar/index.js";
    import { Separator } from "$lib/components/ui/separator/index.js";

    let { msg, isLast = false }: { msg: Message; isLast?: boolean } = $props();
</script>

<div class="flex items-center gap-4 py-2" >
    <Avatar.Root class="size-12 ring-1 ring-[#1DA1F2]">
        <Avatar.Image src={msg.gravatarUrl} alt={msg.username} />
        <Avatar.Fallback>{msg.username[0].toUpperCase()}</Avatar.Fallback>
    </Avatar.Root>
    
    <div class="flex flex-col gap-1 min-w-0">
        <div class="flex items-center gap-2 flex-wrap">
            <a href="/{msg.username}" class="text-sm font-semibold hover:text-[#1DA1F2]"> 
                {msg.username}
            </a>
            <span class="text-muted-foreground text-xs">{msg.pubDate}</span>
        </div>
        <p class="text-sm wrap-break-word text-muted-foreground">{msg.text}</p>
    </div>
</div>
{#if !isLast}
    <Separator />
{/if}

