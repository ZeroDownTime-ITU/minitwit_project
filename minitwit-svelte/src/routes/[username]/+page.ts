import type { Message } from '$lib/types';
import type { PageLoad } from './$types';

export const load: PageLoad = async ({ fetch, params, url }) => {
    const page = Number(url.searchParams.get('page') ?? 0);

    const result = await fetch(`/web/user/${params.username}?page=${page}`)
        .then(r => r.ok ? r.json() : { messages: [], following: false, total: 0})
        .catch(() => ({ messages: [], following: false, total: 0}));

    return {
        messages: result.messages as Message[],
        following: result.following as boolean,
        total: result.total as number,
        page
    };
};
