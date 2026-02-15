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