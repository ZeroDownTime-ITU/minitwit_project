import type { Message } from '$lib/types';
import type { PageLoad } from './$types';

export const load: PageLoad = async ({ fetch }) => {
    try {
        const response = await fetch('/api/public-timeline');
        if (!response.ok) {
            return { messages: [], error: 'Failed to load the public timeline.' };
        }
        const messages: Message[] = await response.json();
        return { messages, error: null };
    } catch {
        return { messages: [], error: 'Could not connect to the server.' };
    }
};