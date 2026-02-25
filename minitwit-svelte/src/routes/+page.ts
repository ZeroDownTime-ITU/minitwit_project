import type { PageLoad } from './$types';
import { get } from 'svelte/store';
import { user } from '$lib/stores';
import { redirect } from '@sveltejs/kit';

export const load: PageLoad = async ({ fetch }) => {
    const currentUser = get(user);
    if (!currentUser) {
        throw redirect(302, '/public');
    }

    const messagesPromise = fetch('/api/user-timeline')
        .then(async r => {
            return r.ok ? await r.json() : [];
        })
        .catch(() => []);

    return {
        messages: messagesPromise
    };
};