<script lang="ts">
    import { onMount } from 'svelte';
    import { goto } from '$app/navigation';
    import { user, flashes } from '$lib/stores';

    onMount(async () => {
        const res = await fetch('/api/auth/logout', { method: 'POST' });

        if (res.ok) {
            user.set(null);
            flashes.set(["You were logged out"]);

            goto('/public');
        }
    });
</script>

<p>Logging you out...</p>