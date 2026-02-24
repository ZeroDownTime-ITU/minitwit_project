<script lang="ts">
    import { goto } from '$app/navigation';
    import { user } from '$lib/stores';
    import LoginForm from "$lib/components/LoginForm.svelte";
    import PageWrapper from '$lib/components/PageWrapper.svelte';
    import { toast } from 'svelte-sonner';

    let error = $state<string | null>(null);
    let loading = $state(false);

    async function handleLogin(username: string, password: string) {
        loading = true;
        error = null;

        try {
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });

            if (response.ok) {
                const loggedInUser = await response.json();
                user.set(loggedInUser);
                toast.success("You were logged in");
                goto('/');
            } else {
                const data = await response.json();
                error = data.error || "Invalid username or password";
            }
        } catch (e) {
            error = "Could not connect to the server.";
        } finally {
            loading = false;
        }
    }
</script>

<svelte:head>
	<title>Login | MiniTwit</title>
</svelte:head>

<PageWrapper>
    <LoginForm onlogin={handleLogin} {error} {loading} />
</PageWrapper>