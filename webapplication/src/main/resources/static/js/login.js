/**
 * @typedef {Object} LoginResponseResource
 * @property {"ROLE_ADMIN" | "ROLE_TEACHER" | "ROLE_STUDENT"} role
 */

document.addEventListener("DOMContentLoaded", () => {

    // Grab the data from the HTML with strict Type Casting for IDE support
    /** @type {HTMLFormElement} */ const loginForm = document.getElementById("loginForm");

    /** @type {HTMLDivElement} */ const errorMessageDiv = document.getElementById("errorMessage");

    /** @type {HTMLInputElement} */ const usernameInput = document.getElementById("username");

    /** @type {HTMLInputElement} */ const passwordInput = document.getElementById("password");

    /** @type {HTMLButtonElement} */ const submitBtn = loginForm.querySelector("button[type='submit']");

    // What will happen when the user clicks "Login"
    loginForm.addEventListener("submit", async (event) => {

        // Stop the default page refresh
        event.preventDefault();

        // Hide the error message in case it was visible from a previous attempt
        errorMessageDiv.style.display = "none";

        // Read what user wrote
        const payload = {
            username: usernameInput.value,
            password: passwordInput.value
        };

        try {
            // Disable button to prevent double-clicks on slow networks
            submitBtn.disabled = true;
            submitBtn.innerText = "Authenticating...";

            // Call /api/auth/login via ApiService
            const response = await ApiService.request('/api/auth/login', {
                method: 'POST',
                body: JSON.stringify(payload)
            });

            if (response.ok) {
                // Backend sent us the LoginResponseResource
                /** @type {LoginResponseResource} */
                const data = await response.json();

                // read the role directly from the answer
                const userRole = data.role;

                localStorage.setItem('tms_user_role', userRole);

                // Role-based routing
                if (userRole === 'ROLE_ADMIN') {
                    window.location.href = '/admin/users';
                } else if (userRole === 'ROLE_TEACHER') {
                    window.location.href = '/teacher/schedule';
                } else if (userRole === 'ROLE_STUDENT') {
                    window.location.href = '/student/schedule';
                } else {
                    window.location.href = '/';
                }

            } else if (response.status === 401) {
                // 401 Unauthorized
                errorMessageDiv.innerText = "Incorrect username or password. Or your account has not been activated.";
                errorMessageDiv.style.display = "block";
            } else {
                // Server Error
                errorMessageDiv.innerText = "There was a problem communicating with the server.";
                errorMessageDiv.style.display = "block";
            }
        } catch (error) {
            console.error("Login failed:", error);
            errorMessageDiv.innerText = "Network problem. Please try again.";
            errorMessageDiv.style.display = "block";
        } finally {
            // Regardless of success or error (unless redirected), re-enable the button
            submitBtn.disabled = false;
            submitBtn.innerText = "Login";
        }
    });
});
