<script lang="ts">
    import { onMount } from 'svelte';
    import { goto } from '$app/navigation';
    import { user, flashes } from '$lib/stores';

    onMount(async () => {
        const response = await fetch('/api/auth/logout', { method: 'POST' });

        if (response.ok) {
            user.set(null);
            flashes.set(["You were logged out"]);

            goto('/public');
        }
    });
</script>