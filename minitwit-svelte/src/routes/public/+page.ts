import type { Message } from '$lib/types';
import type { PageLoad } from './$types';

export const load: PageLoad = async ({ fetch, url }) => {
    const page = Number(url.searchParams.get('page') ?? 0);

    const result = await fetch(`/web/public-timeline?page=${page}`)
        .then(r => r.ok ? r.json() : { messages: [], total: 0 })
        .catch(() => ({ messages: [], total: 0 }));

    return { 
        messages: result.messages as Message[],
        total: result.total as number,
        page
    };
};