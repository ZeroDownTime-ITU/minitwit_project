<script lang="ts">
    import { goto } from '$app/navigation';
    import { flashes } from '$lib/stores';

    let username = $state('');
    let email = $state('');
    let password = $state('');
    let passwordConfirm = $state('');
    let error = $state<string | null>(null);
    let loading = $state(false);

    async function handleRegister(event: Event) {
        event.preventDefault(); // Stop the browser from reloading the page
        loading = true;
        error = null;

        try {
            const response = await fetch('/api/auth/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, email, password, passwordConfirm })
            });

            if (response.ok) {
                flashes.set(["You were successfully registered"]);
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

<h2>Sign Up</h2>

{#if error}
    <div class="error"><strong>Error:</strong> {error}</div>
{/if}

<form onsubmit={handleRegister}>
    <dl>
        <dt>Username:</dt>
        <dd><input type="text" bind:value={username} size="30" disabled={loading}></dd>

        <dt>E-Mail:</dt>
        <dd><input type="text" bind:value={email} size="30" disabled={loading}></dd>
        
        <dt>Password:</dt>
        <dd><input type="password" bind:value={password} size="30" disabled={loading}></dd>

        <dt>Password <small>(repeat)</small>:</dt>
        <dd><input type="password" bind:value={passwordConfirm} size="30" disabled={loading}></dd>
    </dl>
    <div class="actions">
        <input type="submit" value={loading ? "Signing up..." : "Sign Up"} disabled={loading}>
    </div>
</form>