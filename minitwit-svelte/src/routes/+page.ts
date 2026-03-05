import type { PageLoad } from './$types';
import { get } from 'svelte/store';
import { user } from '$lib/stores';
import { redirect } from '@sveltejs/kit';
import type { Message } from '$lib/types';

export const ssr = false;

export const load: PageLoad = async ({ fetch, url }) => {
    const currentUser = get(user);
    if (!currentUser) {
        throw redirect(302, '/public');
    }

    const page = Number(url.searchParams.get('page') ?? 0);

    const result = await fetch(`/web/user-timeline?page=${page}`)
        .then(r => r.ok ? r.json() : { messages: [], total: 0 })
        .catch(() => ({ messages: [], total: 0 }));

    return { 
        messages: result.messages as Message[],
        total: result.total as number,
        page
    };
};