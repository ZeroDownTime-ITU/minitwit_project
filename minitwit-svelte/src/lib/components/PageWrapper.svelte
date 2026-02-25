<script lang="ts">
    import type { Snippet } from 'svelte';
    let { children }: { children: Snippet } = $props();
</script>

<div class="flex min-h-svh flex-col items-center py-8 px-10 bg-linear-to-br from-[#f0f2f5] via-[#e8f4fd] to-[#ddeeff] dark:from-[#16202a] dark:via-[#1a2535] dark:to-[#0f1c2e]">      <div class="w-full max-w-sm md:max-w-3xl">
        {@render children()}
    </div>
</div>