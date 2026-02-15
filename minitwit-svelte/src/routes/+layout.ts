import { user } from '$lib/stores';

export async function load({ fetch }) {
    const response = await fetch('/api/auth/session', {
        credentials: 'include'
    });
    
    if (response.ok) {
        const userData = await response.json();
        user.set(userData);
        return { user: userData };
    }

    user.set(null);
    return { user: null };
}