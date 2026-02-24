<script lang="ts">
    import { goto } from '$app/navigation';
    import PageWrapper from '$lib/components/PageWrapper.svelte';
    import SignupForm from '$lib/components/SignupForm.svelte';
    import { toast } from 'svelte-sonner';

    let error = $state<string | null>(null);
    let loading = $state(false);

    async function handleRegister(username: string, email: string, password: string, passwordConfirm: string) {
        loading = true;
        error = null;

        try {
            const response = await fetch('/api/auth/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, email, password, passwordConfirm })
            });

            if (response.ok) {
                toast.success("You were successfully registered");
                goto('/login'); 
            } else {
                const data = await response.json();
                error = data.message || "Invalid username or password";
            }
        } catch (e) {
            error = "Could not connect to the server.";
        } finally {
            loading = false;
        }
    }
</script>

<svelte:head>
    <title>Sign Up | MiniTwit</title>
</svelte:head>

{#if error}
    <div class="error"><strong>Error:</strong> {error}</div>
{/if}

<PageWrapper>
    <SignupForm onregister={handleRegister} {error} {loading} />
</PageWrapper>