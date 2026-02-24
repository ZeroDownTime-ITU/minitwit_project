<script lang="ts">
	import '../app.css';
	import { ModeWatcher } from "mode-watcher";
	import { Toaster } from "$lib/components/ui/sonner/index.js";
	import * as Tooltip from "$lib/components/ui/tooltip/index.js";
    import Navbar from '$lib/components/Navbar.svelte';
    import Footer from '$lib/components/Footer.svelte';
		
	let { children } = $props();
</script>
<ModeWatcher />

<Tooltip.Provider>
	<div class="page flex flex-col min-h-screen">
		<Navbar />
		<main class="body w-full bg-[#f0f2f5] dark:bg-[#16202a]">
			{@render children()} 
		</main>
		<Footer />
		<Toaster />
	</div>
</Tooltip.Provider>