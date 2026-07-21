/**
 * @typedef {Object} TeacherResource
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
 */

/**
 * @typedef {Object} CourseResource
 * @property {number} id
 * @property {string} title
 * @property {string} description
 * @property {"A_GUMNASIOU" | "B_GUMNASIOU" | "C_GUMNASIOU" | "A_LUKEIOU" | "B_LUKEIOU" | "C_LUKEIOU"} gradeLevel
 * @property {boolean} active
 * @property {TeacherResource[]} [teachers]
 */

/**
 * @typedef {Object} CourseAssignmentResource
 * @property {number} courseId
 * @property {string} courseTitle
 * @property {number} teacherId
 * @property {string} teacherName
 * @property {string} specialty
 */

/**
 * @typedef {Object} ScheduledSlotResource
 * @property {number} id
 * @property {number} courseId
 */

document.addEventListener("DOMContentLoaded", () => {

    // --- DOM Elements Caching ---
    /** @type {HTMLDivElement} */ const coursesGrid = document.getElementById("coursesGrid");
    /** @type {HTMLInputElement} */ const courseSearchInput = document.getElementById("courseSearchInput");
    /** @type {HTMLSelectElement} */ const courseStatusFilter = document.getElementById("courseStatusFilter");
    /** @type {HTMLButtonElement} */ const openCreateModalBtn = document.getElementById("openCreateModalBtn");

    /** @type {HTMLDivElement} */ const standardActions = document.getElementById("standardActions");
    /** @type {HTMLDivElement} */ const deleteConfirmActions = document.getElementById("deleteConfirmActions");
    /** @type {HTMLButtonElement} */ const cancelDeleteBtn = document.getElementById("cancelDeleteBtn");
    /** @type {HTMLButtonElement} */ const confirmDeleteBtn = document.getElementById("confirmDeleteBtn");

    /** @type {HTMLDivElement} */ const courseModal = document.getElementById("courseModal");
    /** @type {HTMLSpanElement} */ const closeModalBtn = document.getElementById("closeModalBtn");
    /** @type {HTMLHeadingElement} */ const modalTitle = document.getElementById("modalTitle");
    /** @type {HTMLDivElement} */ const modalAlert = document.getElementById("modalAlert");

    /** @type {NodeListOf<HTMLButtonElement>} */ const tabBtns = document.querySelectorAll(".tab-btn");
    /** @type {NodeListOf<HTMLDivElement>} */ const tabContents = document.querySelectorAll(".tab-content");

    /** @type {HTMLFormElement} */ const courseForm = document.getElementById("courseForm");
    /** @type {HTMLInputElement} */ const courseTitleInput = document.getElementById("courseTitle");
    /** @type {HTMLSelectElement} */ const courseGradeInput = document.getElementById("courseGrade");
    /** @type {HTMLTextAreaElement} */ const courseDescriptionInput = document.getElementById("courseDescription");
    /** @type {HTMLDivElement} */ const statusToggleGroup = document.getElementById("statusToggleGroup");
    /** @type {HTMLInputElement} */ const courseActiveStatusCheckbox = document.getElementById("courseActiveStatus");

    /** @type {HTMLButtonElement} */ const saveCourseBtn = document.getElementById("saveCourseBtn");
    /** @type {HTMLButtonElement} */ const deleteCourseBtn = document.getElementById("deleteCourseBtn");

    /** @type {HTMLDivElement} */ const teacherLockState = document.getElementById("teacherLockState");
    /** @type {HTMLDivElement} */ const teacherActiveState = document.getElementById("teacherActiveState");
    /** @type {HTMLDivElement} */ const teacherAlert = document.getElementById("teacherAlert");
    /** @type {HTMLSelectElement} */ const availableTeachersSelect = document.getElementById("availableTeachersSelect");
    /** @type {HTMLButtonElement} */ const assignTeacherBtn = document.getElementById("assignTeacherBtn");
    /** @type {HTMLDivElement} */ const assignedTeachersList = document.getElementById("assignedTeachersList");

    // --- Global State ---

    /** @type {CourseResource[]} */ let allFetchedCourses = [];

    /** @type {number|null} */ let currentEditingCourseId = null;

    /** @type {TeacherResource[]} */ let allSystemTeachers = [];

    /** @type {CourseAssignmentResource[]} */ let currentCourseAssignments = [];


    // --- Initialization ---
    initData().catch(error => console.error("Init error:", error));

    /**
     * Fetches the initial data needed for the page.
     * Runs course and teacher fetches in parallel for optimal performance.
     */
    async function initData() {
        coursesGrid.innerHTML = `<div class="loading-spinner">Fetching data...</div>`;

        // Fetch courses and teachers simultaneously for speed
        await Promise.all([fetchCourses(), fetchAllTeachers()]);
    }

    // --- Event Listeners ---
    courseSearchInput.addEventListener("input", applyClientFilters);

    courseStatusFilter.addEventListener("change", applyClientFilters);

    openCreateModalBtn.addEventListener("click", openCreateMode);

    closeModalBtn.addEventListener("click", closeModal);

    window.addEventListener("click", (e) => {
        if (e.target === courseModal) closeModal();
    });

    courseForm.addEventListener("submit", (e) => {
        e.preventDefault();
        saveCourse().catch(error => console.error("Save error:", error));
    });

    // Shows the confirmation box
    deleteCourseBtn.addEventListener("click", () => {
        standardActions.style.display = "none";
        deleteConfirmActions.style.display = "flex";
    });

    // Cancels the deletion and goes back to standard buttons
    cancelDeleteBtn.addEventListener("click", () => {
        deleteConfirmActions.style.display = "none";
        standardActions.style.display = "flex";
    });

    // Proceeds with the actual deletion
    confirmDeleteBtn.addEventListener("click", () => {
        if (currentEditingCourseId) {
            deleteCourseAction(currentEditingCourseId).catch(error => console.error("Delete error:", error));
        }
    });

    assignTeacherBtn.addEventListener("click", () => {
        const selectedValue = availableTeachersSelect.value;

        if (!selectedValue) {
            showTeacherAlert("Please select a teacher from the dropdown.", "danger");
            return;
        }

        const teacherId = parseInt(selectedValue, 10);

        assignTeacherAction(currentEditingCourseId, teacherId).catch(e => console.error(e));
    });

    // Tab Switching Logic
    tabBtns.forEach(btn => {
        btn.addEventListener("click", () => {
            tabBtns.forEach(b => b.classList.remove("active"));
            tabContents.forEach(c => c.classList.remove("active"));

            btn.classList.add("active");
            document.getElementById(btn.getAttribute("data-tab")).classList.add("active");
        });
    });

    // --- Functions ---

    /**
     * Fetches all courses (active and inactive) for the admin view.
     * @returns {Promise<void>}
     */
    async function fetchCourses() {
        try {
            //coursesGrid.innerHTML = `<div class="loading-spinner">Fetching courses...</div>`; Check if this is duplicate CODE!!!
            const response = await ApiService.request('/api/courses', { method: 'GET' });

            if (response.ok) {
                allFetchedCourses = await response.json();
                applyClientFilters();
            } else {
                showGridError("Failed to fetch courses.");
            }
        } catch (error) {
            showGridError("Network error while loading courses.");
        }
    }

    /**
     * Filters courses locally based on BOTH search input and status dropdown.
     */
    function applyClientFilters() {
        const searchTerm = courseSearchInput.value.toLowerCase();
        const statusFilterValue = courseStatusFilter.value; // "all", "active", "inactive"

        const filtered = allFetchedCourses.filter(c => {
            // Text Check
            const matchesSearch = c.title.toLowerCase().includes(searchTerm) ||
                c.gradeLevel.toLowerCase().includes(searchTerm);

            // Status Check
            let matchesStatus = true;
            if (statusFilterValue === "active") {
                matchesStatus = (c.active === true);
            } else if (statusFilterValue === "inactive") {
                matchesStatus = (c.active === false);
            }

            return matchesSearch && matchesStatus;
        });

        renderCourses(filtered);
    }

    /**
     * Renders the course cards.
     * @param {CourseResource[]} courses
     */
    function renderCourses(courses) {
        coursesGrid.innerHTML = "";

        if (!courses || courses.length === 0) {
            coursesGrid.innerHTML = `<div style="grid-column: 1/-1; text-align: center; color: #7f8c8d; padding: 40px;">No courses found.</div>`;
            return;
        }

        courses.forEach(course => {
            const card = document.createElement("div");
            card.className = `course-card ${course.active ? '' : 'status-inactive'}`;

            const badgeClass = course.active ? "badge-active" : "badge-inactive";
            const badgeText = course.active ? "ACTIVE" : "INACTIVE";

            card.innerHTML = `
                <h3 class="course-title">${course.title}</h3>
                <div class="course-grade">${course.gradeLevel}</div>
                <div class="course-desc"><i class="fas fa-book"></i> ${course.description || "No description provided."}</div>
                <span class="status-badge ${badgeClass}">${badgeText}</span>
            `;

            card.addEventListener("click", () => openEditMode(course));
            coursesGrid.appendChild(card);
        });
    }

    /**
     * Prepares the modal for creating a new course.
     */
    function openCreateMode() {
        currentEditingCourseId = null;
        courseForm.reset();

        modalTitle.innerText = "Create New Course";
        statusToggleGroup.style.display = "none";
        deleteCourseBtn.style.display = "none";
        modalAlert.style.display = "none";
        saveCourseBtn.innerText = "Create Course";
        standardActions.style.display = "flex";
        deleteConfirmActions.style.display = "none";

        // Lock the teacher tab
        teacherLockState.style.display = "block";
        teacherActiveState.style.display = "none";
        teacherAlert.style.display = "none";

        // Reset Tabs to Info
        tabBtns[0].click();
        courseModal.style.display = "flex";
    }

    /**
     * Prepares the modal for editing an existing course.
     * @param {CourseResource} course
     */
    function openEditMode(course) {
        currentEditingCourseId = course.id;

        modalTitle.innerText = "Edit Course: " + course.title;
        courseTitleInput.value = course.title;
        courseGradeInput.value = course.gradeLevel;
        courseDescriptionInput.value = course.description || "";

        courseActiveStatusCheckbox.checked = course.active;
        statusToggleGroup.style.display = "flex";
        modalAlert.style.display = "none";
        saveCourseBtn.innerText = "Update Course";
        standardActions.style.display = "flex";
        deleteConfirmActions.style.display = "none";
        deleteCourseBtn.style.display = "block";

        // Defensive UX: Async check for Active Enrollments
        courseActiveStatusCheckbox.disabled = true;
        courseActiveStatusCheckbox.parentElement.style.opacity = "0.6";
        courseActiveStatusCheckbox.parentElement.style.cursor = "wait";
        courseActiveStatusCheckbox.title = "Checking course usage...";

        validateCourseDeactivationEligibility(course.id).catch(e => console.error("Validation error:", e));

        // Reset tooltip and styles temporarily until assignments load
        deleteCourseBtn.disabled = false;
        deleteCourseBtn.style.opacity = "1";
        deleteCourseBtn.style.cursor = "pointer";
        deleteCourseBtn.title = "";

        // Unlock and load teachers
        teacherLockState.style.display = "none";
        teacherActiveState.style.display = "block";
        teacherAlert.style.display = "none";
        loadCourseAssignments(course.id).catch(error => console.error("Error loading assignments:", error));

        tabBtns[0].click();
        courseModal.style.display = "flex";
    }

    /**
     * Handles both Create (POST) and Update (PUT) logic dynamically.
     * @returns {Promise<void>}
     */
    async function saveCourse() {
        saveCourseBtn.disabled = true;
        saveCourseBtn.innerText = "Saving...";
        modalAlert.style.display = "none";

        const isUpdate = currentEditingCourseId !== null;
        const endpoint = isUpdate ? `/api/courses/${currentEditingCourseId}` : '/api/courses';
        const method = isUpdate ? 'PUT' : 'POST';

        const payload = {
            title: courseTitleInput.value.trim(),
            description: courseDescriptionInput.value.trim(),
            gradeLevel: courseGradeInput.value.trim()
        };

        if (isUpdate) {
            payload.active = courseActiveStatusCheckbox.checked;
        }

        try {
            const response = await ApiService.request(endpoint, {
                method: method,
                body: JSON.stringify(payload)
            });

            if (response.status === 201 || response.status === 200) {
                await fetchCourses();
                closeModal();
            } else if (response.status === 409) {
                // Catch the CourseAlreadyExistsException
                const errorData = await response.json();
                showModalAlert(errorData.message || "A course with this title already exists.", "danger");
            } else {
                showModalAlert("An error occurred while saving the course.", "danger");
            }
        } catch (error) {
            showModalAlert("Network error. Please try again.", "danger");
        } finally {
            saveCourseBtn.disabled = false;
            saveCourseBtn.innerText = isUpdate ? "Update Course" : "Create Course";
        }
    }

    /**
     * Handles course deletion.
     * @param {number} courseId
     * @returns {Promise<void>}
     */
    async function deleteCourseAction(courseId) {

        deleteCourseBtn.disabled = true;
        deleteCourseBtn.innerText = "Deleting...";

        try {
            const response = await ApiService.request(`/api/courses/${courseId}`, { method: 'DELETE' });

            if (response.status === 204) {
                await fetchCourses();
                closeModal();
            } else {
                showModalAlert("Failed to delete course.", "danger");
                // reset buttons if it fails
                deleteConfirmActions.style.display = "none";
                standardActions.style.display = "flex";
            }
        } catch (error) {
            showModalAlert("Network error during deletion.", "danger");
        } finally {
            confirmDeleteBtn.disabled = false;
            confirmDeleteBtn.innerText = "Yes, Delete!";
        }
    }

    function closeModal() {
        courseModal.style.display = "none";
    }

    /**
     * @param {string} msg
     * @param {string} type - 'success' or 'danger'
     */
    function showModalAlert(msg, type) {
        modalAlert.className = `modal-alert alert-${type}`;
        modalAlert.innerText = msg;
        modalAlert.style.display = "block";
    }

    /**
     * Displays a formatted error message directly inside the main courses grid.
     * @param {string} msg - The error message to display.
     */
    function showGridError(msg) {
        coursesGrid.innerHTML = `<div style="color: #e74c3c; grid-column: 1/-1; text-align:center;">${msg}</div>`;
    }

    // --- Assignments ---

    /**
     * Fetches all teachers from the system (from User Management module)
     */
    async function fetchAllTeachers() {
        try {
            // Using the user API to get all teachers
            const response = await ApiService.request('/api/admin/users?role=TEACHER', { method: 'GET' });
            if (response.ok) {
                allSystemTeachers = await response.json();
            }
        } catch (error) { console.error("Failed to fetch system teachers", error); }
    }

    /**
     * Loads assignments for the specific course from the backend
     */
    async function loadCourseAssignments(courseId) {
        assignedTeachersList.innerHTML = `<div style="text-align:center; color:#7f8c8d;">Loading teachers...</div>`;
        try {
            // The endpoint returns ALL assignments globally
            const response = await ApiService.request('/api/courses/assignments', { method: 'GET' });
            if (response.ok) {
                const allAssignments = await response.json();

                // Filter assignments to keep only the ones for THIS course
                currentCourseAssignments = allAssignments.filter(a => a.courseId === courseId);

                renderAssignedTeachersList();
                updateAvailableTeachersDropdown();

                updateDeleteButtonState();
            } else {
                assignedTeachersList.innerHTML = `<div style="color:red;">Failed to load assignments.</div>`;
            }
        } catch (error) {
            assignedTeachersList.innerHTML = `<div style="color:red;">Network error.</div>`;
        }
    }

    /**
     * Populates the dropdown, excluding teachers who are already assigned, and
     * applying Contextual Filtering to show only teachers who are eligible for this course.
     */
    function updateAvailableTeachersDropdown() {
        availableTeachersSelect.innerHTML = `<option value="">Select a teacher...</option>`;

        // find the title of the current lesson
        const currentCourse = allFetchedCourses.find(c => c.id === currentEditingCourseId);
        const courseTitle = currentCourse ? currentCourse.title : "";

        // extract the IDs of already assigned teachers
        const assignedTeacherIds = currentCourseAssignments.map(a => a.teacherId);

        // Contextual Filter
        const availableTeachers = allSystemTeachers.filter(t => {

            // Must be an active user
            const isActive = t.enabled === true;

            // Not already assigned to this course
            const isNotAssigned = !assignedTeacherIds.includes(t.id);

            // Must have declared that they teach this course
            const isEligible = t.eligibleCourses && t.eligibleCourses.includes(courseTitle);

            return isActive && isNotAssigned && isEligible;
        });

        // If no suitable teacher is found, inform the UI
        if (availableTeachers.length === 0) {
            availableTeachersSelect.innerHTML = `<option value="">No eligible teachers found for this course.</option>`;
            availableTeachersSelect.disabled = true;
            assignTeacherBtn.disabled = true;
            return;
        }

        availableTeachersSelect.disabled = false;
        assignTeacherBtn.disabled = false;

        // Fill the Dropdown with the appropriate teacher, adding their specialty
        availableTeachers.forEach(t => {
            const opt = document.createElement("option");
            opt.value = t.id.toString();
            opt.innerText = `${t.fullName} (${t.username}) - ${t.specialty || 'Teacher'}`;
            availableTeachersSelect.appendChild(opt);
        });
    }

    /**
     * Renders the rows in the "Currently Assigned" section
     */
    function renderAssignedTeachersList() {
        assignedTeachersList.innerHTML = "";

        if (currentCourseAssignments.length === 0) {
            assignedTeachersList.innerHTML = `<div style="padding: 15px; text-align: center; color: #7f8c8d; background: #f8f9fa; border-radius: 6px;">
                No teachers assigned to this course yet.</div>`;
            return;
        }

        currentCourseAssignments.forEach(assignment => {
            const row = document.createElement("div");
            row.className = "teacher-row";
            row.innerHTML = `
                <div class="teacher-info">
                    <span class="teacher-name">${assignment.teacherName}</span>
                    <span class="teacher-specialty">${assignment.specialty || 'Teacher'}</span>
                </div>
            `;

            // Generate the Two-Step defensive remove button
            const removeBtn = createDefensiveRemoveButton(assignment.courseId, assignment.teacherId);
            row.appendChild(removeBtn);

            assignedTeachersList.appendChild(row);
        });
    }

    /**
     * Creates a defensive "Two-Step" remove button to prevent accidental deletions.
     * The button requires a second confirmation click within 4 seconds to proceed.
     * @param {number} courseId - The ID of the course.
     * @param {number} teacherId - The ID of the teacher to be removed.
     * @returns {HTMLButtonElement} The fully configured DOM button element.
     */
    function createDefensiveRemoveButton(courseId, teacherId) {
        const btn = document.createElement("button");
        btn.className = "btn-remove";
        btn.innerText = "Remove";

        let isConfirming = false;
        let timeoutId = null;

        btn.addEventListener("click", () => {
            if (!isConfirming) {
                // Step 1: Ask for confirmation
                isConfirming = true;
                btn.innerText = "Sure?";
                btn.classList.add("btn-remove-confirm");

                // Reset back to normal if they don't click within 4 seconds
                timeoutId = setTimeout(() => {
                    isConfirming = false;
                    btn.innerText = "Remove";
                    btn.classList.remove("btn-remove-confirm");
                }, 4000);
            } else {
                // Step 2: Proceed with deletion
                clearTimeout(timeoutId);
                unassignTeacherAction(courseId, teacherId).catch(e => console.error(e));
            }
        });

        return btn;
    }

    /**
     * Sends a POST request to link a selected teacher to the current course.
     * Disables the button during the network request and reloads the list upon success.
     * @param {number} courseId - The ID of the course.
     * @param {number} teacherId - The ID of the teacher.
     * @returns {Promise<void>}
     */
    async function assignTeacherAction(courseId, teacherId) {
        assignTeacherBtn.disabled = true;
        assignTeacherBtn.innerText = "Assigning...";
        teacherAlert.style.display = "none";

        try {
            const response = await ApiService.request(`/api/courses/${courseId}/teachers/${teacherId}`, { method: 'POST' });
            if (response.status === 204) {
                // Success! Reload the assignments to refresh UI
                await loadCourseAssignments(courseId);
            } else {
                showTeacherAlert("Failed to assign teacher.", "danger");
            }
        } catch (error) {
            showTeacherAlert("Network error during assignment.", "danger");
        } finally {
            assignTeacherBtn.disabled = false;
            assignTeacherBtn.innerText = "Assign";
        }
    }

    /**
     * Sends a DELETE request to remove a teacher from the current course.
     * Gracefully handles 403 Forbidden errors if the teacher has scheduled slots.
     * @param {number} courseId - The ID of the course.
     * @param {number} teacherId - The ID of the teacher.
     * @returns {Promise<void>}
     */
    async function unassignTeacherAction(courseId, teacherId) {
        teacherAlert.style.display = "none";
        try {
            const response = await ApiService.request(`/api/courses/${courseId}/teachers/${teacherId}`, { method: 'DELETE' });
            if (response.status === 204) {
                // Success!
                await loadCourseAssignments(courseId);
            } else if (response.status === 403) {
                // Catch the IllegalOperationException for scheduled slots!
                const errorData = await response.json();
                showTeacherAlert(errorData.message || "Cannot remove teacher due to scheduled slots.", "danger");
            } else {
                showTeacherAlert("Failed to remove teacher.", "danger");
            }
        } catch (error) {
            showTeacherAlert("Network error during unassignment.", "danger");
        }
    }

    /**
     * Displays a formatted alert message specifically inside the Teacher Assignments tab.
     * @param {string} msg - The message to display.
     * @param {string} type - The type of the alert ('success', 'danger', 'info').
     */
    function showTeacherAlert(msg, type) {
        teacherAlert.className = `modal-alert alert-${type}`;
        teacherAlert.innerText = msg;
        teacherAlert.style.display = "block";
    }

    /**
     * Disables the delete button and adds a tooltip
     * if the course has assigned teachers.
     */
    function updateDeleteButtonState() {
        if (currentCourseAssignments.length > 0) {
            deleteCourseBtn.disabled = true;
            deleteCourseBtn.style.opacity = "0.5";
            deleteCourseBtn.style.cursor = "not-allowed";
            deleteCourseBtn.title = "Cannot delete: Please remove all assigned teachers first.";
        } else {
            deleteCourseBtn.disabled = false;
            deleteCourseBtn.style.opacity = "1";
            deleteCourseBtn.style.cursor = "pointer";
            deleteCourseBtn.title = "Permanently delete this course";
        }
    }

    /**
     * Asynchronously checks if a course can be safely deactivated.
     * A course CANNOT be deactivated if it has Scheduled Slots containing Enrolled Students.
     * Breaks the loop upon finding the very first student.
     * @param {number} courseId - The ID of the course being edited.
     */
    async function validateCourseDeactivationEligibility(courseId) {
        try {
            // Fetch all classes for the Course
            const slotsRes = await ApiService.request(`/api/courses/${courseId}/slots`, { method: 'GET' });

            if (!slotsRes.ok) {
                console.error("Failed to fetch course slots. Status:", slotsRes.status);
                courseActiveStatusCheckbox.title = "Error checking course status. Action disabled.";
                courseActiveStatusCheckbox.parentElement.style.cursor = "not-allowed";
                return;
            }

            /** @type {ScheduledSlotResource[]} */
            const slots = await slotsRes.json();

            let isInUse = false;

            // If it has Classes, check one by one for students
            if (slots && slots.length > 0) {
                for (const slot of slots) {

                    const rosterRes = await ApiService.request(`/api/admin/slots/${slot.id}/students`, { method: 'GET' });
                    if (rosterRes.ok) {

                        const roster = await rosterRes.json();
                        // Student found - not status DROPPED
                        if (roster && roster.length > 0) {
                            isInUse = true;
                            break; // stop the for-loop, don't need to search the remaining classes
                        }
                    }
                }
            }

            // update the UI based on the result
            if (isInUse) {
                // There are active students -> locked
                courseActiveStatusCheckbox.disabled = true;
                courseActiveStatusCheckbox.title = "Cannot deactivate: This course is currently taught in active classes.";
                courseActiveStatusCheckbox.parentElement.style.opacity = "0.6";
                courseActiveStatusCheckbox.parentElement.style.cursor = "not-allowed";
            } else {
                // There are no active students -> unlocked
                courseActiveStatusCheckbox.disabled = false;
                courseActiveStatusCheckbox.title = "Toggle course visibility for students";
                courseActiveStatusCheckbox.parentElement.style.opacity = "1";
                courseActiveStatusCheckbox.parentElement.style.cursor = "pointer";
            }

        } catch (error) {
            console.error("Error validating course deactivation eligibility:", error);
            // If an error occurs (e.g. network error), leave it locked for security
            courseActiveStatusCheckbox.title = "Error checking course status. Action disabled.";
            courseActiveStatusCheckbox.parentElement.style.cursor = "not-allowed";
        }
    }

});
