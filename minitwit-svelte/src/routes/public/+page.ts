import type { Message } from '$lib/types';
import type { PageLoad } from './$types';

export const load: PageLoad = async ({ fetch }) => {
    const messagesPromise = fetch('/api/public-timeline')
        .then(async r => {
            if (!r.ok) return [];
            return await r.json() as Message[];
        })
        .catch(() => []);

    return { 
        messages: messagesPromise 
    };
};