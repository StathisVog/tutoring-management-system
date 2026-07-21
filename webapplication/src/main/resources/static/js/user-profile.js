/**
 * Unified logic for Teacher and Student Profiles.
 */

document.addEventListener("DOMContentLoaded", () => {

    // --- DOM Elements Caching ---

    // Tabs
    /** @type {NodeListOf<HTMLButtonElement>} */ const tabBtns = document.querySelectorAll(".tab-btn");
    /** @type {NodeListOf<HTMLDivElement>} */ const tabPanels = document.querySelectorAll(".tab-panel");

    // Form & Common Inputs
    /** @type {HTMLFormElement} */ const personalInfoForm = document.getElementById("personalInfoForm");
    /** @type {HTMLInputElement} */ const usernameInput = document.getElementById("username");
    /** @type {HTMLInputElement} */ const emailInput = document.getElementById("email");
    /** @type {HTMLInputElement} */ const fullNameInput = document.getElementById("fullName");
    /** @type {HTMLInputElement} */ const addressInput = document.getElementById("address");

    // Role Specific Containers
    /** @type {HTMLDivElement} */ const teacherFields = document.getElementById("teacherFields");
    /** @type {HTMLDivElement} */ const studentFields = document.getElementById("studentFields");

    // Teacher Specific Inputs
    /** @type {HTMLInputElement} */ const specialtyInput = document.getElementById("specialty");
    /** @type {HTMLTextAreaElement} */ const bioInput = document.getElementById("bio");

    // Student Specific Inputs
    /** @type {HTMLInputElement} */ const ageInput = document.getElementById("age");
    /** @type {HTMLInputElement} */ const schoolClassInput = document.getElementById("schoolClass");
    /** @type {HTMLInputElement} */ const parentFullNameInput = document.getElementById("parentFullName");
    /** @type {HTMLInputElement} */ const parentTaxIdInput = document.getElementById("parentTaxId");

    // Tab 2 (Preferences)
    /** @type {HTMLFormElement} */ const preferencesForm = document.getElementById("preferencesForm");
    /** @type {NodeListOf<HTMLInputElement>} */ const themeRadios = document.querySelectorAll('input[name="themeColor"]');

    // Tab 3 (Security - Password)
    /** @type {HTMLFormElement} */ const securityForm = document.getElementById("securityForm");
    /** @type {HTMLInputElement} */ const oldPasswordInput = document.getElementById("oldPassword");
    /** @type {HTMLInputElement} */ const newPasswordInput = document.getElementById("newPassword");
    /** @type {HTMLInputElement} */ const confirmNewPasswordInput = document.getElementById("confirmNewPassword");
    /** @type {NodeListOf<HTMLButtonElement>} */ const togglePasswordBtns = document.querySelectorAll(".toggle-password");
    /** @type {HTMLDivElement} */ const passwordStrengthContainer = document.getElementById("passwordStrengthContainer");
    /** @type {HTMLSpanElement} */ const passwordStrengthBadge = document.getElementById("passwordStrengthBadge");

    // Tab 4 (Avatar)
    /** @type {HTMLFormElement} */ const avatarForm = document.getElementById("avatarForm");
    /** @type {NodeListOf<HTMLInputElement>} */ const avatarRadios = document.querySelectorAll('input[name="avatarName"]');
    /** @type {HTMLImageElement} */ const navAvatarImage = document.getElementById("navAvatarImage");

    // State
    let currentUserData = {};
    let currentUserRole = null; // 'TEACHER' or 'STUDENT'

    // --- Initialization ---
    initTabs();
    loadUserProfile().catch(console.error);

    // --- Event Listeners ---
    if (personalInfoForm) {
        personalInfoForm.addEventListener("submit", handlePersonalInfoSubmit);
    }

    if (preferencesForm) {
        preferencesForm.addEventListener("submit", handlePreferencesSubmit);
    }

    if (securityForm) {
        securityForm.addEventListener("submit", handleSecuritySubmit);
    }

    if (avatarForm) {
        avatarForm.addEventListener("submit", handleAvatarSubmit);
    }

    if (newPasswordInput) {
        newPasswordInput.addEventListener("input", handlePasswordStrength);
    }

    // Toggle Password Visibility Logic
    togglePasswordBtns.forEach(btn => {
        btn.addEventListener("click", function() {

            // input field is right next to the button
            const input = this.previousElementSibling;
            const icon = this.querySelector("i");

            if (input.type === "password") {
                input.type = "text";
                icon.classList.replace("fa-eye", "fa-eye-slash");
            } else {
                input.type = "password";
                icon.classList.replace("fa-eye-slash", "fa-eye");
            }
        });
    });

    // Live Preview for Theme
    themeRadios.forEach(radio => {
        radio.addEventListener("change", (e) => {

            // Check if the sidebar is currently closed
            const isCollapsed = document.body.classList.contains('sidebar-collapsed');

            // Apply the new color, but also "stick" the sidebar state if necessary
            document.body.className = e.target.value + (isCollapsed ? ' sidebar-collapsed' : '');
        });
    });

    // Live Preview for Avatar
    avatarRadios.forEach(radio => {
        radio.addEventListener("change", (e) => {
            if (navAvatarImage) {
                navAvatarImage.src = `/images/avatars/${e.target.value}`;
            }
        });
    });

    // --- Functions ---

    /**
     * Dynamically calculates and visualizes the strength of the new password
     */
    function handlePasswordStrength() {
        const val = newPasswordInput.value;
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
            passwordStrengthBadge.innerText = "Very Weak!";
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
            passwordStrengthBadge.innerText = "Very Strong!";
            passwordStrengthBadge.classList.add("bg-very-strong");
        }
    }

    /**
     * Logic to handle Tab Switching dynamically
     */
    function initTabs() {
        tabBtns.forEach(btn => {
            btn.addEventListener("click", () => {

                // remove 'active' from all buttons and panels
                tabBtns.forEach(b => b.classList.remove("active"));
                tabPanels.forEach(p => p.classList.remove("active"));

                // add 'active' to the button that was clicked
                btn.classList.add("active");

                // find the corresponding panel and display it
                const targetPanel = document.getElementById(btn.getAttribute("data-target"));

                if (targetPanel) {
                    targetPanel.classList.add("active");
                }
            });
        });
    }

    /**
     * Fetches the user data from backend and populates the Personal Info form
     */
    async function loadUserProfile() {
        try {
            const response = await fetch('/api/users/me', {
                method: 'GET',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'same-origin'
            });

            if (response.ok) {
                const data = await response.json();
                currentUserData = data;
                currentUserRole = data.role; // Save role for PUT requests

                // UI Logic: Show/Hide fields based on role
                if (currentUserRole === 'TEACHER') {
                    if (teacherFields) teacherFields.style.display = 'flex';
                } else if (currentUserRole === 'STUDENT') {
                    if (studentFields) studentFields.style.display = 'flex';
                }

                // Populate Common Fields
                if (usernameInput) usernameInput.value = data.username || "";
                if (emailInput) emailInput.value = data.email || "";
                if (fullNameInput) fullNameInput.value = data.fullName || "";
                if (addressInput) addressInput.value = data.address || "";

                // Populate Specific Fields
                if (currentUserRole === 'TEACHER') {
                    if (specialtyInput) specialtyInput.value = data.specialty || "";
                    if (bioInput) bioInput.value = data.bio || "";
                } else if (currentUserRole === 'STUDENT') {
                    if (ageInput) ageInput.value = data.age || "";
                    if (schoolClassInput) schoolClassInput.value = data.schoolClass || "";
                    if (parentFullNameInput) parentFullNameInput.value = data.parentFullName || "";
                    if (parentTaxIdInput) parentTaxIdInput.value = data.parentTaxId || "";
                }

                // Populate Preferences Tab
                if (data.themeColor) {
                    localStorage.setItem('tms_theme', data.themeColor);
                    const activeRadio = document.querySelector(`input[name="themeColor"][value="${data.themeColor}"]`);
                    if (activeRadio) {
                        activeRadio.checked = true;
                    }
                }

                // Populate Avatar Tab
                if (data.avatarName) {
                    const activeAvatarRadio = document.querySelector(`input[name="avatarName"][value="${data.avatarName}"]`);
                    if (activeAvatarRadio) {
                        activeAvatarRadio.checked = true;
                    }

                    // updated the navbar with what came from the database
                    if (navAvatarImage) {
                        navAvatarImage.src = `/images/avatars/${data.avatarName}`;
                    }
                }

            } else {
                console.error("Failed to load profile data.");
            }
        } catch (error) {
            console.error("Network error:", error);
        }
    }

    /**
     * Handles the Save button action
     * @param event
     * @returns {Promise<void>}
     */
    async function handlePersonalInfoSubmit(event) {
        event.preventDefault();

        // Dynamically build payload based on role
        let payload = {
            role: currentUserRole,
            email: emailInput.value,
            fullName: fullNameInput.value,
            address: addressInput.value
        };

        if (currentUserRole === 'TEACHER') {
            payload.specialty = specialtyInput.value;
            payload.bio = bioInput.value;
        } else if (currentUserRole === 'STUDENT') {
            payload.age = ageInput.value ? parseInt(ageInput.value, 10) : null;
            payload.schoolClass = schoolClassInput.value;
            payload.parentFullName = parentFullNameInput.value;
            payload.parentTaxId = parentTaxIdInput.value;
        }

        const submitBtn = event.target.querySelector('button[type="submit"]');
        const originalText = submitBtn.innerHTML;

        try {
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving...';
            submitBtn.disabled = true;

            const response = await fetch('/api/users/me', {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'same-origin',
                body: JSON.stringify(payload)
            });

            if (response.ok) {
                Toast.show("Profile updated successfully!", "success");
                await loadUserProfile();
            } else {
                const errorData = await response.json();
                Toast.show(errorData.message || "Failed to update profile.", "danger");
            }
        } catch (error) {
            Toast.show("A network error occurred.", "danger");
        } finally {
            submitBtn.innerHTML = originalText;
            submitBtn.disabled = false;
        }
    }

    // --- TAB 2 ---

    /**
     *
     * @param event
     * @returns {Promise<void>}
     */
    async function handlePreferencesSubmit(event) {
        event.preventDefault();

        // find which radio button is selected
        const selectedThemeRadio = document.querySelector('input[name="themeColor"]:checked');
        const newTheme = selectedThemeRadio ? selectedThemeRadio.value : "theme-default";

        // create the payload by taking the OLD data, and changing ONLY the theme
        const payload = {
            ...currentUserData, // Spread operator (copies email, fullName, etc.)
            themeColor: newTheme
        };

        const submitBtn = event.target.querySelector('button[type="submit"]');
        const originalText = submitBtn.innerHTML;

        try {
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving...';
            submitBtn.disabled = true;

            const response = await fetch('/api/users/me', {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'same-origin',
                body: JSON.stringify(payload)
            });

            if (response.ok) {
                Toast.show("Preferences saved successfully!", "success");

                // Updating the browser with the new theme
                localStorage.setItem('tms_theme', newTheme);

                await loadUserProfile();
            } else {
                const errorData = await response.json();
                Toast.show(errorData.message || "Failed to save preferences.", "danger");
            }
        } catch (error) {
            Toast.show("A network error occurred.", "danger");
        } finally {
            submitBtn.innerHTML = originalText;
            submitBtn.disabled = false;
        }
    }

    // --- TAB 3 ---

    /**
     * Handles the Update Password form submission
     * @param event
     * @returns {Promise<void>}
     */
    async function handleSecuritySubmit(event) {
        event.preventDefault();

        if (newPasswordInput.value !== confirmNewPasswordInput.value) {
            Toast.show("The new passwords do not match.", "danger");
            return;
        }

        const payload = {
            oldPassword: oldPasswordInput.value,
            newPassword: newPasswordInput.value,
            confirmNewPassword: confirmNewPasswordInput.value
        };

        const submitBtn = event.target.querySelector('button[type="submit"]');
        const originalText = submitBtn.innerHTML;

        try {
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Updating...';
            submitBtn.disabled = true;

            const response = await fetch('/api/users/me/password', {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'same-origin',
                body: JSON.stringify(payload)
            });

            if (response.ok) {
                Toast.show("Password successfully updated!", "success");
                securityForm.reset(); // leaving the fields blank for security reasons

                // Hide and reset the Password Strength container if everything went well
                if (passwordStrengthContainer) {
                    passwordStrengthContainer.style.display = "none";
                    passwordStrengthBadge.className = "strength-badge";
                }

                // restore the icons to the "closed" eye
                togglePasswordBtns.forEach(btn => {
                    const icon = btn.querySelector("i");
                    icon.classList.replace("fa-eye-slash", "fa-eye");
                    btn.previousElementSibling.type = "password";
                });
            } else {
                const errorData = await response.json();
                Toast.show(errorData.message || "Failed to update password.", "danger");
            }
        } catch (error) {
            Toast.show("A network error occurred.", "danger");
        } finally {
            submitBtn.innerHTML = originalText;
            submitBtn.disabled = false;
        }
    }

    // --- TAB 4 ---

    /**
     * Handles the Save button action (Avatar)
     * @param event
     * @returns {Promise<void>}
     */
    async function handleAvatarSubmit(event) {
        event.preventDefault();

        const selectedAvatarRadio = document.querySelector('input[name="avatarName"]:checked');
        const newAvatar = selectedAvatarRadio ? selectedAvatarRadio.value : "avatar-default.svg";

        const payload = {
            ...currentUserData,
            avatarName: newAvatar
        };

        const submitBtn = event.target.querySelector('button[type="submit"]');
        const originalText = submitBtn.innerHTML;

        try {
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving...';
            submitBtn.disabled = true;

            const response = await fetch('/api/users/me', {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'same-origin',
                body: JSON.stringify(payload)
            });

            if (response.ok) {
                Toast.show("Avatar successfully updated!", "success");
                await loadUserProfile();
            } else {
                const errorData = await response.json();
                Toast.show(errorData.message || "Failed to save avatar.", "danger");
            }
        } catch (error) {
            Toast.show("A network error occurred.", "danger");
        } finally {
            submitBtn.innerHTML = originalText;
            submitBtn.disabled = false;
        }
    }
});
