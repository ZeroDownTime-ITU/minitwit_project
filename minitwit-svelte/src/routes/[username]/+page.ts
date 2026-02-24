import type { PageLoad } from './$types';
import type { UserProfileData } from '$lib/types';

export const load: PageLoad = async ({ fetch, params }) => {
    const profilePromise = fetch(`/api/user/${params.username}`)
        .then(async (r) => {
            return r.ok ? (await r.json() as UserProfileData) : null;
        })
        .catch(() => null);

    return {
        profile: profilePromise
    };
};