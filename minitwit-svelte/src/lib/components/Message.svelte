<script lang="ts">
    import type { Message } from '$lib/types';

    let { msg }: { msg: Message } = $props();
</script>

<li>
    <img src={msg.gravatarUrl} alt="avatar" />
    <p>
        <strong><a href="/{msg.username}">{msg.username}</a></strong>
        {msg.text}
        <small>&mdash; {msg.pubDate}</small>
    </p>
</li>

<style>
    /* These styles only apply to this component */
    img { float: left; margin-right: 10px; border-radius: 4px; }
    li { list-style: none; padding: 10px 0; border-bottom: 1px solid #eee; }
</style>