/**
 * @typedef {Object} UserProfileResource
 * @property {number} id
 * @property {string} username
 * @property {string} fullName
 * @property {string} email
 * @property {string} [address]
 * @property {"ADMIN" | "TEACHER" | "STUDENT"} role
 * @property {string} themeColor - e.g., 'theme-default', 'theme-blue'
 * @property {string} avatarName - e.g., 'avatar-default.svg'
 * @property {boolean} enabled
 */

document.addEventListener("DOMContentLoaded", () => {

    // --- DOM ELEMENTS CACHING ---

    /** @type {NodeListOf<HTMLElement>} */ const guestOnlyElements = document.querySelectorAll('.guest-only');
    /** @type {NodeListOf<HTMLElement>} */ const userOnlyElements = document.querySelectorAll('.user-only');
    /** @type {HTMLAnchorElement} */ const logoutBtn = document.getElementById('logoutBtn');
    /** @type {HTMLAnchorElement} */ const navDashboardLink = document.getElementById('navDashboardLink');
    /** @type {HTMLImageElement} */ const navAvatarImage = document.getElementById('navAvatarImage');
    /** @type {HTMLAnchorElement} */ const navProfileLink = document.getElementById('navProfileLink');
    /** @type {HTMLButtonElement} */ const sidebarToggleBtn = document.getElementById('sidebarToggleBtn');

    // --- INITIALIZATION ---

    updateNavigation().catch(console.error);
    attachEventListeners();

    // --- FUNCTIONS ---

    /**
     * Asks the backend for the complete user profile via the HttpOnly cookie.
     * Handles role-based routing, theme synchronization, and sidebar state.
     * @returns {Promise<void>}
     */
    async function updateNavigation() {

        try {
            const response = await fetch('/api/users/me', {
                method: 'GET',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'same-origin'
            });

            if (response.ok) {
                // The user is logged in. Get the entire UserResource
                /** @type {UserProfileResource} */
                const data = await response.json();
                const role = data.role; // "ADMIN", "TEACHER", or "STUDENT"
                const username = data.username;

                // Store which user is currently active (and their role for UI routing)
                localStorage.setItem('tms_current_user', username);
                localStorage.setItem('tms_user_role', role);

                // Toggle visibility for authenticated users
                guestOnlyElements.forEach(el => el.style.display = 'none');
                userOnlyElements.forEach(el => el.style.display = 'inline-block');

                // Setting the Avatar Image
                if (navAvatarImage && data.avatarName) {
                    navAvatarImage.src = `/images/avatars/${data.avatarName}`;
                }

                // --- THEME & SIDEBAR SYNC LOGIC ---

                // Find the color. If it is null in the database, set the default
                const activeTheme = data.themeColor ? data.themeColor : 'theme-default';

                // Read the sidebar state exclusively for this user
                const isCollapsed = localStorage.getItem(`tms_sidebar_collapsed_${username}`) === 'true';

                // Apply both to the body
                document.body.className = activeTheme + (isCollapsed ? ' sidebar-collapsed' : '');

                // Update localStorage - for the Anti-FOUC script to work properly
                localStorage.setItem('tms_theme', activeTheme);

                // Role-based routing for Dashboard & Profile Link
                if (role === 'ADMIN') {
                    navDashboardLink.href = '/admin/users';

                    if (navProfileLink) {
                        navProfileLink.removeAttribute('href');
                        navProfileLink.style.cursor = 'default';
                    }
                } else if (role === 'TEACHER') {
                    navDashboardLink.href = '/teacher/schedule';
                    if (navProfileLink) navProfileLink.href = '/teacher/profile';
                } else if (role === 'STUDENT') {
                    navDashboardLink.href = '/student/schedule';
                    if (navProfileLink) navProfileLink.href = '/student/profile';
                }
            } else {
                // User is a guest (Response was 401 Unauthorized)
                handleGuestState();
            }
        } catch (error) {
            // Network error (Assume Guest)
            handleGuestState();
        }
    }

    /**
     * Resets the UI to the default guest state.
     * Clears user-specific local storage preferences to prevent state bleeding.
     */
    function handleGuestState() {
        userOnlyElements.forEach(el => el.style.display = 'none');
        guestOnlyElements.forEach(el => el.style.display = 'inline-block');

        document.body.className = 'theme-default';

        localStorage.removeItem('tms_theme');
        localStorage.removeItem('tms_current_user');
        localStorage.removeItem('tms_user_role');
    }

    // --- EVENT LISTENERS ---

    /**
     * Attaches all global navigation event listeners (Logout, Sidebar Toggle).
     */
    function attachEventListeners() {

        // Handle Logout Event
        if (logoutBtn) {
            logoutBtn.addEventListener("click", async (e) => {
                e.preventDefault();

                try {
                    await fetch('/api/auth/logout', {
                        method: 'POST',
                        credentials: 'same-origin'
                    });

                    // Delete ALL the user's state from the browser
                    localStorage.removeItem('tms_theme');
                    localStorage.removeItem('tms_current_user');
                    localStorage.removeItem('tms_user_role');

                } catch (err) {
                    console.error("Logout error", err);
                } finally {
                    window.location.href = '/';
                }
            });
        }

        // Handle Sidebar Toggle Logic
        if (sidebarToggleBtn) {
            sidebarToggleBtn.addEventListener('click', () => {
                // Toggle the CSS class
                document.body.classList.toggle('sidebar-collapsed');

                // Check the current state after the toggle
                const isCollapsed = document.body.classList.contains('sidebar-collapsed');

                // If the system temporarily loses the username, set it to 'guest' (arbitrary value)
                const username = localStorage.getItem('tms_current_user') || 'guest';

                // Storage with dynamic key per user
                localStorage.setItem(`tms_sidebar_collapsed_${username}`, isCollapsed ? 'true' : 'false');
            });
        }
    }

});
