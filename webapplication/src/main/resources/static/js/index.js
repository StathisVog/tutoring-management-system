/**
 * Script for the Landing Page (index.html).
 * Handles dynamic UI changes based on authentication status.
 */
document.addEventListener("DOMContentLoaded", () => {

    // --- DOM Elements Caching ---

    /** @type {HTMLAnchorElement} */
    const heroCtaBtn = document.getElementById("heroCtaBtn");

    // Safety check: If the button was removed from the HTML, stop execution
    if (!heroCtaBtn) return;


    // --- Initialization ---

    updateHeroButton();


    // --- Core Functions ---

    /**
     * Checks if the user is authenticated (via UI state) and redirects the Call-To-Action
     * button to their specific dashboard instead of the Registration page.
     */
    function updateHeroButton() {
        // Read the role from login.js
        const role = localStorage.getItem('tms_user_role');

        // If there is no role, the user is a guest. The button remains as "Get Started"
        if (!role) return;

        // The user is logged in. Change the button text.
        heroCtaBtn.innerText = "Go to Dashboard";

        if (role === 'ROLE_ADMIN') {
            heroCtaBtn.href = "/admin/users";
        } else if (role === 'ROLE_TEACHER') {
            heroCtaBtn.href = "/teacher/schedule";
        } else if (role === 'ROLE_STUDENT') {
            heroCtaBtn.href = "/student/schedule";
        } else {
            // Failsafe fallback
            heroCtaBtn.href = "/";
        }
    }

});
