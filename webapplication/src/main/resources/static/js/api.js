/**
 * Central class for communication with Backend.
 * Uses 'credentials: same-origin' to automatically send the HttpOnly JWT Cookie
 * and manages 401 Unauthorized errors (failure to login).
 */
const ApiService = {

    /**
     * The central method that will be called instead of the simple fetch()
     * @param {string} url - The API endpoint to call
     * @param {RequestInit} [options={}] - Fetch options (method, body, etc.)
     * @returns {Promise<Response>}
     */
    request: async (url, options = {}) => {

        // setting up basic headers
        const headers = {
            'Content-Type': 'application/json',
            ...(options.headers || {})
        };

        // tells the browser to set the HttpOnly cookie on every request automatically
        const config = {
            ...options,
            headers,
            credentials: 'same-origin'
        };

        let response;
        try {
            response = await fetch(url, config);
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }

        // 401 Unauthorized Handling
        if (response.status === 401 && window.location.pathname !== '/login') {
            window.location.href = '/login';
            return Promise.reject(new Error("Unauthorized access. Redirecting to login..."));
        }

        return response;
    }
};
