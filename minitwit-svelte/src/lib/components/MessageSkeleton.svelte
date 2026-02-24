<script lang="ts">
    import { Skeleton } from "$lib/components/ui/skeleton/index.js";
    import { Separator } from "$lib/components/ui/separator/index.js";

    let { isLast = false }: { isLast?: boolean } = $props();
</script>

<div class="flex items-center gap-4 py-2">
    <Skeleton class="size-12 rounded-full shrink-0" />
    <div class="flex flex-col gap-2 flex-1">
        <div class="flex items-center gap-2">
            <Skeleton class="h-3 w-24" />
            <Skeleton class="h-3 w-16" />
        </div>
        <Skeleton class="h-3 w-full" />
    </div>
</div>
{#if !isLast}
    <Separator />
{/if}