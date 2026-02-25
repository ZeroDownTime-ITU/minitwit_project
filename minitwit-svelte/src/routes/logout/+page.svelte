<script lang="ts">
    import { onMount } from 'svelte';
    import { goto } from '$app/navigation';
    import { user } from '$lib/stores';
    import { toast } from 'svelte-sonner';

    onMount(async () => {
        const response = await fetch('/api/auth/logout', { method: 'POST' });

        if (response.ok) {
            user.set(null);
            toast.success("You were logged out");

            goto('/public');
        }
    });
</script>