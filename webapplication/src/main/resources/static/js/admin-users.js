/**
 * @typedef {Object} UserResource
 * @property {number} id
 * @property {string} username
 * @property {string} email
 * @property {string} fullName
 * @property {string} [address]
 * @property {"STUDENT" | "TEACHER" | "ADMIN"} role
 * @property {boolean} enabled
 * @property {string} createdAt
 * @property {string} [specialty]
 * @property {string} [bio]
 * @property {Array<string>} [courses]
 * @property {Array<string>} [eligibleCourses]
 * @property {number} [age]
 * @property {"A_GUMNASIOU" | "B_GUMNASIOU" | "C_GUMNASIOU" | "A_LUKEIOU" | "B_LUKEIOU" | "C_LUKEIOU"} [schoolClass]
 * @property {string} [parentFullName]
 * @property {string} [parentTaxId]
 */

document.addEventListener("DOMContentLoaded", () => {

    // --- DOM Elements Caching ---
    /** @type {HTMLDivElement} */ const usersGrid = document.getElementById("usersGrid");
    /** @type {HTMLSelectElement} */ const roleFilter = document.getElementById("roleFilter");
    /** @type {HTMLSelectElement} */ const statusFilter = document.getElementById("statusFilter");
    /** @type {HTMLInputElement} */ const userSearchInput = document.getElementById("userSearchInput");

    /** @type {HTMLDivElement} */ const userModal = document.getElementById("userModal");
    /** @type {HTMLSpanElement} */ const closeModalBtn = document.getElementById("closeModalBtn");
    /** @type {HTMLDivElement} */ const userDetailsContent = document.getElementById("userDetailsContent");
    /** @type {HTMLDivElement} */ const modalActions = document.getElementById("modalActions");
    /** @type {HTMLDivElement} */ const modalAlert = document.getElementById("modalAlert");

    /** @type {UserResource[]} */
    let allFetchedUsers = [];

    // --- Initialization ---
    fetchUsers().catch(error => console.error("Unhandled error in fetchUsers:", error));

    // --- Event Listeners ---
    roleFilter.addEventListener("change", (e) => {
        /** @type {HTMLSelectElement} */
        const target = e.target;
        fetchUsers(target.value).catch(error => console.error("Filter error:", error));
    });

    userSearchInput.addEventListener("input", applyClientFilters);
    statusFilter.addEventListener("change", applyClientFilters);

    closeModalBtn.addEventListener("click", () => { userModal.style.display = "none"; });
    window.addEventListener("click", (e) => {
        if (e.target === userModal) { userModal.style.display = "none"; }
    });

    // --- Functions ---

    /**
     * Fetches the user list from the backend, optionally filtered by role.
     * Updates the global state and triggers client-side filtering for display.
     * @param {string} [role=""] - The optional user role to filter by (e.g., 'STUDENT', 'TEACHER').
     * @returns {Promise<void>}
     */
    async function fetchUsers(role = "") {
        try {
            usersGrid.innerHTML = `<div class="loading-spinner">Fetching users...</div>`;

            let url = '/api/admin/users';
            if (role) url += `?role=${role}`;

            const response = await ApiService.request(url, { method: 'GET' });

            if (response.ok) {
                allFetchedUsers = await response.json();
                applyClientFilters();
            } else {
                showError("Failed to fetch users. Ensure you have Admin privileges.");
            }
        } catch (error) {
            showError("Network error while loading users.");
        }
    }

    /**
     * Applies client-side text and status filters to the fetched users array.
     */
    function applyClientFilters() {
        const searchTerm = userSearchInput.value.toLowerCase();
        const statusValue = statusFilter.value; // "ALL", "ACTIVE", "INACTIVE"

        const filteredUsers = allFetchedUsers.filter(user => {
            // Check text match
            const matchesText = user.username.toLowerCase().includes(searchTerm) ||
                user.fullName.toLowerCase().includes(searchTerm);

            // Check status match
            let matchesStatus = true;
            if (statusValue === "ACTIVE") matchesStatus = (user.enabled === true);
            else if (statusValue === "INACTIVE") matchesStatus = (user.enabled === false);

            return matchesText && matchesStatus;
        });

        renderUsers(filteredUsers);
    }

    /**
     * Renders the provided list of users as interactive cards in the grid.
     * Handles the empty state gracefully if no users match the given criteria.
     * @param {UserResource[]} users - The array of user objects to display.
     */
    function renderUsers(users) {
        usersGrid.innerHTML = "";

        if (!users || users.length === 0) {
            usersGrid.innerHTML = `<div style="grid-column: 1/-1; text-align: center; color: #7f8c8d; padding: 40px;">No users match your criteria.</div>`;
            return;
        }

        users.forEach(user => {
            const card = document.createElement("div");
            card.className = `user-card role-${user.role.toLowerCase()}`;

            const statusClass = user.enabled ? "status-active" : "status-inactive";
            const statusText = user.enabled ? "Active" : "Inactive";

            let roleIcon = '<i class="fas fa-user"></i>'; // Default icon

            const userRole = String(user.role).toUpperCase();

            if (userRole.includes('ADMIN')) {
                roleIcon = '<i class="fas fa-user-shield"></i>';
            } else if (userRole.includes('TEACHER')) {
                roleIcon = '<i class="fas fa-chalkboard-teacher"></i>';
            } else if (userRole.includes('STUDENT')) {
                roleIcon = '<i class="fas fa-user-graduate"></i>';
            }

            card.innerHTML = `
                <div class="user-header">
                    <div>
                        <h3 class="user-name">${user.fullName}</h3>
                        <span class="user-username">${user.username}</span>
                    </div>
                    <span class="status-badge ${statusClass}">${statusText}</span>
                </div>
                
                <div style="margin-top: 15px;">
                    <div style="font-size: 0.85rem; color: #7f8c8d; margin-bottom: 5px;">
                        <strong>${roleIcon} Role:</strong> ${user.role}
                    </div>
                    <div style="font-size: 0.85rem; color: #7f8c8d;">
                        <strong><i class="fas fa-envelope"></i> Email:</strong> ${user.email}
                    </div>
                </div>
                
                <div style="margin-top: auto; padding-top: 20px; text-align: center; color: #3498db; font-size: 0.85rem; font-weight: 600;">
                    Click to view & manage &rarr;
                </div>
            `;

            // Make card clickable to fetch details
            card.addEventListener("click", () => {
                fetchUserDetails(user.id).catch(error => console.error("Details error:", error));
            });
            usersGrid.appendChild(card);
        });
    }

    /**
     * Fetches detailed information for a specific user.
     * Opens the modal and displays a loading state during the network request.
     * @param {number} id - The unique identifier of the user.
     * @returns {Promise<void>}
     */
    async function fetchUserDetails(id) {
        userModal.style.display = "flex";
        userDetailsContent.innerHTML = `<div style="grid-column: 1/-1; text-align: center;">Loading details...</div>`;
        modalActions.style.display = "none";
        modalAlert.style.display = "none";

        try {
            const response = await ApiService.request(`/api/admin/users/${id}`, { method: 'GET' });
            if (response.ok) {
                const user = await response.json();
                renderUserDetailsModal(user);
            } else {
                userDetailsContent.innerHTML = `<div style="color: red; grid-column: 1/-1;">Error loading details.</div>`;
            }
        } catch (error) {
            userDetailsContent.innerHTML = `<div style="color: red; grid-column: 1/-1;">Network error.</div>`;
        }
    }

    /**
     * Populates the modal with the user's comprehensive details.
     * Dynamically injects role-specific fields and action buttons.
     * @param {UserResource} user - The detailed user object.
     */
    function renderUserDetailsModal(user) {
        const date = user.createdAt ? new Date(user.createdAt).toLocaleString() : "N/A";

        // Common fields for all roles
        let htmlContent = `
            <div class="detail-group">
                <span class="detail-label">Full Name</span>
                <span class="detail-value">${user.fullName}</span>
            </div>
            <div class="detail-group">
                <span class="detail-label">Username</span>
                <span class="detail-value">${user.username}</span>
            </div>
            <div class="detail-group">
                <span class="detail-label">Email Address</span>
                <span class="detail-value">${user.email}</span>
            </div>
            <div class="detail-group">
                <span class="detail-label">System Role</span>
                <span class="detail-value">${user.role}</span>
            </div>
            <div class="detail-group">
                <span class="detail-label">Account Status</span>
                <span class="detail-value">${user.enabled ? 'ACTIVE (Approved)' : 'INACTIVE (Pending/Disabled)'}</span>
            </div>
            <div class="detail-group">
                <span class="detail-label">Registration Date</span>
                <span class="detail-value">${date}</span>
            </div>
            <div class="detail-group">
                <span class="detail-label">Address</span>
                <span class="detail-value">${user.address || 'No address provided'}</span>
            </div>
        `;

        if (user.role === 'TEACHER') {

            // what the professor stated during registration
            let eligibleDisplay = "No courses declared";
            if (user.eligibleCourses && user.eligibleCourses.length > 0) {
                eligibleDisplay = user.eligibleCourses.map(course => `&bull; ${course}`).join("<br>");
            }

            // lessons we have assigned him to
            let assignedDisplay = "Not assigned to any courses yet";
            if (user.courses && user.courses.length > 0) {
                assignedDisplay = user.courses.map(course => `&bull; ${course}`).join("<br>");
            }

            htmlContent += `
            <div class="detail-group">
                <span class="detail-label">Teacher Specialty</span>
                <span class="detail-value">${user.specialty}</span>
            </div>
            <div class="detail-group">
                <span class="detail-label">Eligible Courses (Preferences)</span>
                <span class="detail-value">${eligibleDisplay}</span>
            </div>
            <div class="detail-group">
                <span class="detail-label">Currently Assigned To</span>
                <span class="detail-value">${assignedDisplay}</span>
            </div>
            <div class="detail-group" style="grid-column: 1/-1;">
                <span class="detail-label">Short Biography</span>
                <span class="detail-value">${user.bio || 'No biography provided'}</span>
            </div>
            `;
        }
        else if (user.role === 'STUDENT') {
            htmlContent += `
            <div class="detail-group">
                <span class="detail-label">Age</span>
                <span class="detail-value">${user.age}</span>
            </div>
            <div class="detail-group">
                <span class="detail-label">School Class</span>
                <span class="detail-value">${user.schoolClass}</span>
            </div>
            <div class="detail-group">
                <span class="detail-label">Parent Full Name</span>
                <span class="detail-value">${user.parentFullName}</span>
            </div>
            <div class="detail-group">
                <span class="detail-label">Parent Tax</span>
                <span class="detail-value">${user.parentTaxId}</span>
            </div>
        `;
        }

        // Embedding the final HTML into the Modal
        userDetailsContent.innerHTML = htmlContent;

        // Generate the standard buttons based on user status
        renderModalActionButtons(user);
    }

    /**
     * Renders the standard Activate and Delete buttons.
     * @param {UserResource} user
     */
    function renderModalActionButtons(user) {
        modalActions.innerHTML = "";
        modalActions.style.display = "flex";
        modalAlert.style.display = "none"; // Hide any existing alerts

        if (!user.enabled) {
            const activateBtn = document.createElement("button");
            activateBtn.className = "btn-action btn-activate";
            activateBtn.innerText = "Activate User";
            activateBtn.onclick = () => activateUserAction(user.id);
            modalActions.appendChild(activateBtn);
        }

        const deleteBtn = document.createElement("button");
        deleteBtn.className = "btn-action btn-delete";
        deleteBtn.innerText = "Delete User";
        deleteBtn.onclick = () => promptDeleteConfirmation(user);
        modalActions.appendChild(deleteBtn);
    }

    /**
     * Inline confirmation prompt.
     * @param {UserResource} user
     */
    function promptDeleteConfirmation(user) {
        modalActions.innerHTML = `
            <div class="delete-confirm-box">
                <div style="display: flex; gap: 10px;">
                    <button id="confirmDeleteBtn" class="btn-action btn-delete">Yes, Delete!</button>
                    <button id="cancelDeleteBtn" class="btn-action btn-cancel">Cancel</button>
                </div>
                <span class="delete-confirm-text"><i class="fas fa-exclamation-triangle"></i>
                    Permanently delete this user? This action cannot be undone.
                </span>
            </div>
        `;

        document.getElementById("cancelDeleteBtn").onclick = () => renderModalActionButtons(user);
        document.getElementById("confirmDeleteBtn").onclick = () => deleteUserAction(user.id);
    }

    /**
     * Displays a formatted error message directly inside the main user grid area.
     * @param {string} msg - The error message to display to the user.
     */
    function showError(msg) {
        usersGrid.innerHTML = `<div style="color: #e74c3c; grid-column: 1/-1; text-align:center;">${msg}</div>`;
    }

    /**
     * Sends a request to activate a previously inactive user.
     * Provides real-time UI feedback inside the modal and refreshes the grid upon success.
     * @param {number} userId - The unique identifier of the user to activate.
     * @returns {Promise<void>}
     */
    async function activateUserAction(userId) {
        modalAlert.className = "modal-alert alert-info";
        modalAlert.innerText = "Activating...";
        modalAlert.style.display = "block";

        try {
            const response = await ApiService.request(`/api/admin/users/${userId}/activate`, {
                method: 'PUT'
            });

            if (response.ok) {
                const message = await response.text();
                modalAlert.className = "modal-alert alert-success";
                modalAlert.innerText = message;

                await fetchUsers(roleFilter.value);

                // 4 seconds
                setTimeout(() => { userModal.style.display = "none"; }, 4000);
            } else {
                modalAlert.className = "modal-alert alert-danger";
                modalAlert.innerText = "Failed to activate user. Please try again.";
            }
        } catch (error) {
            modalAlert.className = "modal-alert alert-danger";
            modalAlert.innerText = "Network error during activation.";
        }
    }

    /**
     * Prompts the admin for confirmation, then permanently deletes a user.
     * Handles validation errors gracefully (e.g., attempting to delete oneself).
     * @param {number} userId - The unique identifier of the user to delete.
     * @returns {Promise<void>}
     */
    async function deleteUserAction(userId) {

        modalAlert.className = "modal-alert alert-info";
        modalAlert.innerText = "Deleting...";
        modalAlert.style.display = "block";

        try {
            const response = await ApiService.request(`/api/admin/users/${userId}`, {
                method: 'DELETE'
            });

            if (response.status === 204) {
                userModal.style.display = "none";
                await fetchUsers(roleFilter.value);
            } else {
                let errorMsg = "Failed to delete user.";
                try {
                    const errorData = await response.json();
                    if (errorData.message) errorMsg = errorData.message;
                } catch (e) {}

                modalAlert.className = "modal-alert alert-danger";
                modalAlert.innerText = errorMsg;

                // If it fails (e.g., trying to delete themselves),
                // leave the error message visible but remove the confirmation box.
                modalActions.innerHTML = "";
            }
        } catch (error) {
            modalAlert.className = "modal-alert alert-danger";
            modalAlert.innerText = "Network error during deletion.";
            modalActions.innerHTML = "";
        }
    }
});
