const originalFetch = window.fetch;

// Redirect to login on 401 responses (e.g. expired token mid-session)
window.fetch = async (input, init = {}) => {
    const response = await originalFetch(input, init);

    const url = typeof input === 'string' ? input : input instanceof Request ? input.url : '';
    if (response.status === 401 && !url.includes('/web/auth/session')) {
        // eslint-disable-next-line security/detect-possible-timing-attacks
        window.location.href = '/login';
    }

    return response;
};