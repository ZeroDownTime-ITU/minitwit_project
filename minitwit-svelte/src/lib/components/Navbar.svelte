<script lang="ts">
    import { user } from '$lib/stores';
    import * as NavigationMenu from "$lib/components/ui/navigation-menu/index.js";
    import { navigationMenuTriggerStyle } from '$lib/components/ui/navigation-menu/navigation-menu-trigger.svelte';
    import { Separator } from "$lib/components/ui/separator/index.js";
    import DarkModeToggle from '$lib/components/DarkMode.svelte';
    import BirdIcon from "@lucide/svelte/icons/bird";
</script>

<header class="sticky top-0 z-50 w-full border-b bg-background/80 backdrop-blur supports-backdrop-filter:bg-background/80">
    <div class="flex justify-center">
        <NavigationMenu.Root class="w-full max-w-sm md:max-w-3xl justify-between py-2">
            <NavigationMenu.List>
                <NavigationMenu.Item>
                    <a href="/" class="flex items-center gap-2 text-xl font-bold px-2">
                        <BirdIcon class="h-5 w-5 text-[#1DA1F2]" />
                        MiniTwit.
                    </a>
                </NavigationMenu.Item>
            </NavigationMenu.List>

            <NavigationMenu.List>
                {#if $user}
                    <NavigationMenu.Item>
                        <NavigationMenu.Link>
                            {#snippet child()}
                            <a href="/" class={navigationMenuTriggerStyle()}>My timeline</a>
                            {/snippet}
                        </NavigationMenu.Link>
                    </NavigationMenu.Item>
                    <NavigationMenu.Item>
                        <NavigationMenu.Link>
                            {#snippet child()}
                            <a href="/public" class={navigationMenuTriggerStyle()}>Public timeline</a>
                            {/snippet}
                        </NavigationMenu.Link>
                    </NavigationMenu.Item>
                    <NavigationMenu.Item>
                        <NavigationMenu.Link>
                            {#snippet child()}
                            <a href="/logout" class={navigationMenuTriggerStyle()}>Sign out [{$user.username}]</a>
                            {/snippet}
                        </NavigationMenu.Link>
                    </NavigationMenu.Item>
                {:else}
                    <NavigationMenu.Item>
                        <NavigationMenu.Link>
                            {#snippet child()}
                            <a href="/public" class={navigationMenuTriggerStyle()}>Public timeline</a>
                            {/snippet}
                        </NavigationMenu.Link>
                    </NavigationMenu.Item>
                    <NavigationMenu.Item>
                        <NavigationMenu.Link>
                            {#snippet child()}
                            <a href="/register" class={navigationMenuTriggerStyle()}>Sign up</a>
                            {/snippet}
                        </NavigationMenu.Link>
                    </NavigationMenu.Item>
                    <NavigationMenu.Item>
                        <NavigationMenu.Link>
                            {#snippet child()}
                            <a href="/login" class={navigationMenuTriggerStyle()}>Sign in</a>
                            {/snippet}
                        </NavigationMenu.Link>
                    </NavigationMenu.Item>
                {/if}
                <Separator orientation="vertical" class="h-6 mx-1" />
                <NavigationMenu.Item>
                    <DarkModeToggle />
                </NavigationMenu.Item>
            </NavigationMenu.List>
        </NavigationMenu.Root>
    </div>
</header>