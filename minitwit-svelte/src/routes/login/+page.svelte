<script lang="ts">
    import { goto } from '$app/navigation';
    import { user, flashes } from '$lib/stores';

    let username = $state('');
    let password = $state('');
    let error = $state<string | null>(null);
    let loading = $state(false);

    async function handleLogin(event: Event) {
        event.preventDefault(); // Stop the browser from reloading the page
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
                flashes.set(["You were logged in"]);

                goto('/'); 
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
    <title>Sign In | MiniTwit</title>
</svelte:head>

<h2>Sign In</h2>

{#if error}
    <div class="error"><strong>Error:</strong> {error}</div>
{/if}

<form onsubmit={handleLogin}>
    <dl>
        <dt>Username:</dt>
        <dd><input type="text" bind:value={username} size="30" disabled={loading}></dd>
        
        <dt>Password:</dt>
        <dd><input type="password" bind:value={password} size="30" disabled={loading}></dd>
    </dl>
    <div class="actions">
        <input type="submit" value={loading ? "Signing in..." : "Sign In"} disabled={loading}>
    </div>
</form>
<!-- {% extends "layout.html" %}
{% block title %}Sign In{% endblock %}

{% block body %}
  <h2>Sign In</h2>
  
  {% if error is not empty %}
    <div class="error"><strong>Error:</strong> {{ error }}</div>
  {% endif %}
  
  <form action="/login" method="post">
    <dl>
      <dt>Username:
      <dd><input type="text" name="username" size="30" value="{{ username }}">
      
      <dt>Password:
      <dd><input type="password" name="password" size="30">
    </dl>
    <div class="actions">
      <input type="submit" value="Sign In">
    </div>
  </form>
{% endblock %} -->