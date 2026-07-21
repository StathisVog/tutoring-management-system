/**
 * @typedef {Object} CourseResource
 * @property {number} id
 * @property {string} title
 * @property {boolean} active
 */

document.addEventListener("DOMContentLoaded", () => {

    // --- DOM ELEMENTS CACHING ---
    /** @type {HTMLFormElement} */ const registerForm = document.getElementById("registerForm");
    /** @type {HTMLSelectElement} */ const roleSelection = document.getElementById("roleSelection");
    /** @type {HTMLButtonElement} */ const submitBtn = document.getElementById("submitBtn");

    /** @type {HTMLDivElement} */ const studentSection = document.getElementById("studentSection");
    /** @type {HTMLDivElement} */ const teacherSection = document.getElementById("teacherSection");
    /** @type {HTMLDivElement} */ const successMessage = document.getElementById("successMessage");
    /** @type {HTMLDivElement} */ const errorMessage = document.getElementById("errorMessage");

    /** @type {HTMLInputElement} */ const usernameInput = document.getElementById("username");
    /** @type {HTMLInputElement} */ const emailInput = document.getElementById("email");
    /** @type {HTMLInputElement} */ const passwordInput = document.getElementById("password");
    /** @type {HTMLDivElement} */ const passwordStrengthContainer = document.getElementById("passwordStrengthContainer");
    /** @type {HTMLSpanElement} */ const passwordStrengthBadge = document.getElementById("passwordStrengthBadge");
    /** @type {HTMLInputElement} */ const fullNameInput = document.getElementById("fullName");
    /** @type {HTMLInputElement} */ const addressInput = document.getElementById("address");

    /** @type {HTMLInputElement} */ const ageInput = document.getElementById("age");
    /** @type {HTMLInputElement} */ const schoolClassInput = document.getElementById("schoolClass");
    /** @type {HTMLInputElement} */ const parentFullNameInput = document.getElementById("parentFullName");
    /** @type {HTMLInputElement} */ const parentTaxIdInput = document.getElementById("parentTaxId");

    /** @type {HTMLInputElement} */ const specialtyInput = document.getElementById("specialty");
    /** @type {HTMLTextAreaElement} */ const bioInput = document.getElementById("bio");
    /** @type {HTMLDivElement} */ const coursesCheckboxContainer = document.getElementById("coursesCheckboxContainer");

    // --- INITIALIZATION ---
    // Initialize: Fetch courses for the Teacher section immediately
    loadActiveCourses().catch(e => console.error("Error initializing courses:", e));

    // Initialize the UI state (disable required fields and the submit button)
    initFormState();

    // --- EVENT LISTENERS ---
    roleSelection.addEventListener("change", handleRoleChange);
    registerForm.addEventListener("submit", handleRegistration);
    passwordInput.addEventListener("input", handlePasswordStrength);

    // --- FUNCTIONS ---

    /**
     * Sets the initial state of the form on page load.
     */
    function initFormState() {
        applyRoleLogic(roleSelection.value);
    }

    /**
     * Event listener callback for role selection.
     * @param {Event} e
     */
    function handleRoleChange(e) {
        applyRoleLogic(e.target.value);
    }

    /**
     * Core business logic to toggle form fields based on the selected role.
     * @param {string} role
     */
    function applyRoleLogic(role) {
        // Hide both sections initially
        studentSection.classList.remove("active");
        teacherSection.classList.remove("active");

        // Disable required attributes for both sections temporarily
        ageInput.required = false;
        schoolClassInput.required = false;
        parentFullNameInput.required = false;
        parentTaxIdInput.required = false;
        specialtyInput.required = false;

        // Enable the submit button ONLY if a valid role is selected
        submitBtn.disabled = !(role === "STUDENT" || role === "TEACHER");

        // Show the relevant section and enable its required fields
        if (role === "STUDENT") {
            studentSection.classList.add("active");
            ageInput.required = true;
            schoolClassInput.required = true;
            parentFullNameInput.required = true;
            parentTaxIdInput.required = true;
        } else if (role === "TEACHER") {
            teacherSection.classList.add("active");
            specialtyInput.required = true;
        }
    }

    /**
     * Dynamically calculates and visualizes password strength
     */
    function handlePasswordStrength() {
        const val = passwordInput.value;
        const len = val.length;

        // If it is empty, hide the entire line
        if (len === 0) {
            passwordStrengthContainer.style.display = "none";
            return;
        }

        // Display the line and reset the Badge class
        passwordStrengthContainer.style.display = "flex";
        passwordStrengthBadge.className = "strength-badge";

        if (len >= 1 && len <= 3) {
            passwordStrengthBadge.innerText = "Very Weak";
            passwordStrengthBadge.classList.add("bg-very-weak");
        } else if (len >= 4 && len <= 5) {
            passwordStrengthBadge.innerText = "Weak";
            passwordStrengthBadge.classList.add("bg-weak");
        } else if (len >= 6 && len <= 8) {
            passwordStrengthBadge.innerText = "Normal";
            passwordStrengthBadge.classList.add("bg-normal");
        } else if (len >= 9 && len <= 12) {
            passwordStrengthBadge.innerText = "Strong";
            passwordStrengthBadge.classList.add("bg-strong");
        } else if (len >= 13) {
            passwordStrengthBadge.innerText = "Very Strong";
            passwordStrengthBadge.classList.add("bg-very-strong");
        }
    }

    /**
     * Fetches courses from the public API and populates the scrollable checkbox list.
     */
    async function loadActiveCourses() {
        try {
            const response = await ApiService.request('/api/courses', { method: 'GET' });
            if (response.ok) {
                /** @type {CourseResource[]} */
                const courses = await response.json();

                // filter only ACTIVE courses for new teachers
                const activeCourses = courses.filter(c => c.active === true);

                coursesCheckboxContainer.innerHTML = "";

                if (activeCourses.length === 0) {
                    coursesCheckboxContainer.innerHTML = '<span class="status-text-sm">No active courses found.</span>';
                    return;
                }

                activeCourses.forEach(course => {
                    const label = document.createElement("label");
                    label.className = "checkbox-label";
                    label.innerHTML = `
                        <input type="checkbox" name="eligibleCourses" value="${course.id}">
                        ${course.title}
                    `;
                    coursesCheckboxContainer.appendChild(label);
                });

            } else {
                coursesCheckboxContainer.innerHTML = '<span class="status-error-sm">Failed to load courses.</span>';
            }
        } catch (error) {
            coursesCheckboxContainer.innerHTML = '<span class="status-error-sm">Network error.</span>';
        }
    }

    /**
     * @param {SubmitEvent} event
     */
    async function handleRegistration(event) {
        // Prevent page refresh
        event.preventDefault();

        // Reset alerts
        successMessage.style.display = "none";
        errorMessage.style.display = "none";

        const selectedRole = roleSelection.value;

        // Base payload (Common fields for both)
        let payload = {
            username: usernameInput.value,
            email: emailInput.value,
            password: passwordInput.value,
            fullName: fullNameInput.value,
            address: addressInput.value.trim()
        };

        let endpoint = "";

        if (selectedRole === "STUDENT") {
            endpoint = "/api/auth/register/student";
            payload.age = parseInt(ageInput.value, 10);
            payload.schoolClass = schoolClassInput.value;
            payload.parentFullName = parentFullNameInput.value;
            payload.parentTaxId = parentTaxIdInput.value;

        } else if (selectedRole === "TEACHER") {
            endpoint = "/api/auth/register/teacher";
            payload.specialty = specialtyInput.value.trim();
            payload.bio = bioInput.value.trim();

            // collect all checked checkboxes
            const checkedBoxes = document.querySelectorAll('input[name="eligibleCourses"]:checked');
            const selectedCourseIds = Array.from(checkedBoxes).map(cb => parseInt(cb.value, 10));

            // Validation check
            if (selectedCourseIds.length === 0) {
                errorMessage.innerText = "Please select at least one course you are eligible to teach.";
                errorMessage.style.display = "block";
                return; // stop Submit
            }

            payload.eligibleCourseIds = selectedCourseIds;
        }

        try {
            // Disable button during network request to prevent double submissions
            submitBtn.disabled = true;
            submitBtn.innerText = "Processing...";

            const response = await ApiService.request(endpoint, {
                method: 'POST',
                body: JSON.stringify(payload)
            });

            if (response.ok) {
                registerForm.reset();
                studentSection.classList.remove("active");
                teacherSection.classList.remove("active");

                // Clear the password strength meter too
                passwordStrengthContainer.style.display = "none";
                passwordStrengthBadge.className = "strength-badge";

                successMessage.innerText = "Registration successful! Your account has been created but is currently INACTIVE. " +
                    "Please wait for an Administrator to approve it before logging in.";
                successMessage.style.display = "block";

                // Redirect to login after 10 seconds
                setTimeout(() => {
                    window.location.href = "/login";
                }, 10000);

            } else {
                const errorData = await response.json();
                errorMessage.innerText = errorData.message || "Registration failed. " +
                    "Username or email might already be in use.";
                errorMessage.style.display = "block";

                submitBtn.disabled = false;
                submitBtn.innerText = "Register";
            }
        } catch (error) {
            console.error("Registration failed:", error);
            errorMessage.innerText = "Network error. Please try again later.";
            errorMessage.style.display = "block";

            submitBtn.disabled = false;
            submitBtn.innerText = "Register";
        }
    }
});
