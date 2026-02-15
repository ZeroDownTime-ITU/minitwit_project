<script lang="ts">
    import { goto } from '$app/navigation';
    import { flashes, user } from '$lib/stores';

    let username = $state('');
    let email = $state('');
    let password = $state('');
    let password2 = $state('');
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
                body: JSON.stringify({ username, email, password, password2 })
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
        <dd><input type="password" bind:value={password2} size="30" disabled={loading}></dd>
    </dl>
    <div class="actions">
        <input type="submit" value={loading ? "Signing up..." : "Sign Up"} disabled={loading}>
    </div>
</form>
<!-- {% extends "layout.html" %}
{% block title %}Sign Up{% endblock %}

{% block body %}
  <h2>Sign Up</h2>
  
  {% if error is not empty %}
    <div class="error"><strong>Error:</strong> {{ error }}</div>
  {% endif %}
  
  <form action="/register" method="post">
    <dl>
      <dt>Username:
      <dd><input type="text" name="username" size="30" value="{{ username }}">
      
      <dt>E-Mail:
      <dd><input type="text" name="email" size="30" value="{{ email }}">
      
      <dt>Password:
      <dd><input type="password" name="password" size="30">
      
      <dt>Password <small>(repeat)</small>:
      <dd><input type="password" name="password2" size="30">
    </dl>
    <div class="actions">
        <input type="submit" value="Sign Up">
    </div>
  </form>
{% endblock %} -->