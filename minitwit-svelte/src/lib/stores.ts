import { writable } from 'svelte/store';

interface User {
    username: string;
    email: string;
}

export const user = writable<User | null>(null);

export const flashes = writable<string[]>([]);