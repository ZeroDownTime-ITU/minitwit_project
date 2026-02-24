<script lang="ts">
    import SunIcon from "@lucide/svelte/icons/sun";
    import MoonIcon from "@lucide/svelte/icons/moon";
    import { toggleMode } from "mode-watcher";
    import * as Tooltip from "$lib/components/ui/tooltip/index.js";
</script>

<Tooltip.Root>
    <Tooltip.Trigger onclick={toggleMode} class="inline-flex items-center justify-center rounded-md p-2 hover:bg-accent">
        <SunIcon class="h-[1.2rem] w-[1.2rem] scale-100 rotate-0 transition-all! dark:scale-0 dark:-rotate-90" />
        <MoonIcon class="absolute h-[1.2rem] w-[1.2rem] scale-0 rotate-90 transition-all! dark:scale-100 dark:rotate-0" />
        <span class="sr-only">Toggle theme</span>
    </Tooltip.Trigger>
    <Tooltip.Content>
        <p>Toggle theme</p>
    </Tooltip.Content>
</Tooltip.Root>