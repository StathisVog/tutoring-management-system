/**
 * @typedef {Object} TeacherResource
 * @property {string} fullName
 * @property {string} specialty
 */

/**
 * @typedef {Object} CourseResource
 * @property {number} id
 * @property {string} title
 * @property {string} description
 * @property {"A_GUMNASIOU" | "B_GUMNASIOU" | "C_GUMNASIOU" | "A_LUKEIOU" | "B_LUKEIOU" | "C_LUKEIOU"} gradeLevel
 * @property {boolean} active
 * @property {TeacherResource[]} teachers
 */

/**
 * @typedef {Object} ScheduledSlotResource
 * @property {number} id
 * @property {number} courseId
 * @property {string} courseTitle
 * @property {number} teacherId
 * @property {string} teacherName
 * @property {"MONDAY" | "TUESDAY" | "WEDNESDAY" | "THURSDAY" | "FRIDAY" | "SATURDAY" | "SUNDAY"} dayOfWeek
 * @property {string} startTime
 * @property {string} endTime
 * @property {string} classroom
 * @property {number} capacity
 * @property {number} availableSeats
 */

document.addEventListener("DOMContentLoaded", () => {

    // --- DOM Elements Caching ---

    /** @type {HTMLDivElement} */ const coursesGrid = document.getElementById("coursesGrid");
    /** @type {HTMLDivElement} */ const scheduleModal = document.getElementById("scheduleModal");
    /** @type {HTMLSpanElement} */ const closeModalBtn = document.getElementById("closeModalBtn");
    /** @type {HTMLHeadingElement} */ const modalCourseTitle = document.getElementById("modalCourseTitle");
    /** @type {HTMLTableSectionElement} */ const modalScheduleBody = document.getElementById("modalScheduleBody");
    /** @type {HTMLDivElement} */ const modalEmptyState = document.getElementById("modalEmptyState");
    /** @type {HTMLDivElement} */ const tableResponsive = document.querySelector(".table-responsive");
    /** @type {HTMLDivElement} */ const modalAlertBox = document.getElementById("modalAlertBox");

    // --- State Management ---

    /** @type {string|null} */ let currentUserRole = null;


    // --- Initialization ---

    /*fetchActiveCourses().catch(error => console.error("Initialization error:", error));*/
    init().catch(error => console.error("Initialization error:", error));

    async function init() {
        await fetchUserRole();
        await fetchActiveCourses();
        attachEventListeners();
    }


    // --- Core Functions ---

    /**
     * Silently fetches the user's profile to determine their role.
     */
    async function fetchUserRole() {
        try {
            const response = await fetch('/api/users/me', {
                method: 'GET',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'same-origin'
            });

            if (response.ok) {
                const data = await response.json();
                currentUserRole = data.role;
            } else {
                currentUserRole = null; // Unauthorized / Guest
            }
        } catch (error) {
            currentUserRole = null; // Network error / Guest
        }
    }

    /**
     * Fetches all active courses from the backend.
     */
    async function fetchActiveCourses() {
        try {
            const response = await ApiService.request('/api/courses?active=true', { method: 'GET' });

            if (!response.ok) {
                showError("Failed to load courses. Please try again later.");
                return;
            }

            const courses = await response.json();
            renderCourses(courses);

        } catch (error) {
            console.error('Fetch error:', error);
            showError("Network error while loading courses.");
        }
    }

    // --- Rendering ---

    /**
     * Renders the course cards dynamically into the grid.
     * @param {CourseResource[]} courses
     */
    function renderCourses(courses) {
        coursesGrid.innerHTML = "";

        if (!courses || courses.length === 0) {
            coursesGrid.innerHTML = `<div class="empty-state">No active courses found.</div>`;
            return;
        }

        courses.forEach(course => {

            let teachersHtml = `<div class="teacher-item" style="color: #95a5a6; font-style: italic;">None assigned</div>`;

            if (course.teachers && course.teachers.length > 0) {
                teachersHtml = course.teachers.map(t =>
                    `<div class="teacher-item"><i class="fas fa-chalkboard-teacher"></i> <span>${t.fullName} (${t.specialty})</span></div>`
                ).join('');
            }

            const card = document.createElement("div");
            card.className = "course-card";

            card.innerHTML = `
                <h3 class="course-title">${course.title}</h3>
                
                <div class="info-row">
                    <span class="info-label">Grade Level</span>
                    <span class="info-value">${course.gradeLevel}</span>
                </div>
                
                <div class="info-row">
                    <span class="info-label">Description</span>
                    <span class="info-value">${course.description || "No description provided."}</span>
                </div>
                
                <div class="info-row">
                    <span class="info-label">Taught By</span>
                    <div class="teachers-list">
                        ${teachersHtml}
                    </div>
                </div>
                
                <div class="click-hint">
                    Click to view schedule & availability &rarr;
                </div>
            `;

            card.addEventListener("click", () => {
                openScheduleModal(course.id, course.title)
                    .catch(error => console.error("Failed to open modal:", error));
            });

            coursesGrid.appendChild(card);
        });
    }

    /**
     * Displays an error message inside the courses grid.
     * @param {string} message
     */
    function showError(message) {
        coursesGrid.innerHTML = `<div class="alert-error">${message}</div>`;
    }

    // --- Modal & Schedule Rendering ---

    /**
     * Fetches and displays the schedule for a specific course in a modal.
     * @param {number} courseId
     * @param {string} courseTitle
     */
    async function openScheduleModal(courseId, courseTitle) {

        // Preparing the Modal Header & State
        modalCourseTitle.innerText = `Schedule: ${courseTitle}`;
        modalScheduleBody.innerHTML = `<tr><td colspan="8" style="text-align:center;"><i class="fas fa-spinner fa-spin"></i> Loading timetable...</td></tr>`;
        modalEmptyState.style.display = "none";
        tableResponsive.style.display = "block";

        // Clearing the Alert Box (hide any old messages from a previous lesson)
        modalAlertBox.style.display = 'none';
        modalAlertBox.className = '';
        modalAlertBox.innerHTML = '';

        // Show Modal
        scheduleModal.style.display = "flex";

        try {
            const response = await ApiService.request(`/api/courses/${courseId}/slots`, { method: 'GET' });

            if (!response.ok) {
                modalScheduleBody.innerHTML = `<tr><td colspan="8" style="text-align:center; color:red;">Failed to load schedule.</td></tr>`;
                return;
            }

            const slots = await response.json();
            renderSlotsInModal(slots);

        } catch (error) {
            console.error(error);
            modalScheduleBody.innerHTML = `<tr><td colspan="8" style="text-align:center; color:red;">Network error.</td></tr>`;
        }
    }

    /**
     * Renders the schedule rows inside the modal table.
     * Evaluates User Role to display 'Enroll' actions.
     * @param {ScheduledSlotResource[]} slots
     */
    function renderSlotsInModal(slots) {
        modalScheduleBody.innerHTML = "";

        if (!slots || slots.length === 0) {
            tableResponsive.style.display = "none";
            modalEmptyState.style.display = "block";
            return;
        }

        slots.forEach(slot => {
            const tr = document.createElement("tr");

            const occupiedSeats = slot.capacity - slot.availableSeats;
            const isFull = slot.availableSeats === 0;

            // Status Badge Logic
            const statusDisplay = isFull
                ? `<span class="badge-full"><i class="fas fa-exclamation-triangle"></i> FULL</span>`
                : `<span class="badge-available">Available</span>`;

            // Action Column Logic (Role based)
            let actionHtml;
            if (currentUserRole === 'STUDENT') {
                if (isFull) {
                    actionHtml = `<button class="btn-enroll" disabled>Class Full</button>`;
                } else {
                    actionHtml = `<button class="btn-enroll" id="enrollBtn-${slot.id}" onclick="window.triggerEnrollment(${slot.id})">Enroll Now</button>`;
                }
            } else {
                // Not a student (Guest, Admin, Teacher)
                actionHtml = `<i class="fas fa-lock lock-icon" title="Login as a student to enroll in this class."></i>`;
            }

            tr.innerHTML = `
                <td><strong>${slot.dayOfWeek}</strong></td>
                <td>${slot.startTime.substring(0, 5)} - ${slot.endTime.substring(0, 5)}</td>
                <td>${slot.teacherName}</td>
                <td>${slot.classroom || 'Online'}</td>
                <td><strong>${occupiedSeats}</strong></td>
                <td>${slot.capacity}</td>
                <td>${statusDisplay}</td>
                <td class="action-cell">${actionHtml}</td>
            `;

            modalScheduleBody.appendChild(tr);
        });
    }

    // --- Enrollment POST Logic ---

    /**
     * Triggers the enrollment POST request.
     * Evaluates backend response and displays messages inside the Modal.
     * @param {number} slotId
     */
    window.triggerEnrollment = async function(slotId) {
        const btn = document.getElementById(`enrollBtn-${slotId}`);
        if (!btn) return;

        // Hide the previous alert (if any) at the beginning of each new request
        modalAlertBox.style.display = 'none';
        modalAlertBox.className = '';
        modalAlertBox.innerHTML = '';

        try {
            // Lock UI to prevent double submission
            btn.disabled = true;
            btn.innerHTML = `<i class="fas fa-spinner fa-spin"></i> Processing...`;

            const response = await fetch('/api/students/me/enrollments', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'same-origin',
                body: JSON.stringify({ slotId: slotId })
            });

            // Handle Specific Backend Errors
            if (!response.ok) {
                let errorMessage = 'Failed to enroll. Please try again later.';

                try {
                    const errorData = await response.json();
                    if (errorData && errorData.message) {
                        errorMessage = errorData.message;
                    }
                } catch (e) {
                    console.warn("Could not parse error JSON from backend.");
                }

                // Displaying Danger Alert within Modal
                modalAlertBox.className = 'alert-danger';
                modalAlertBox.innerHTML = `<i class="fas fa-exclamation-triangle"></i> <div>${errorMessage}</div>`;
                modalAlertBox.style.display = 'flex';
                return;
            }

            // Success (200 OK) - Displaying Success Alert within Modal
            modalAlertBox.className = 'alert-success';
            modalAlertBox.innerHTML = `<i class="fas fa-check-circle"></i> <div>Enrollment successful! Redirecting...</div>`;
            modalAlertBox.style.display = 'flex';

            // hide the table to focus the user on the message
            tableResponsive.style.display = 'none';

            // Redirect to Student Enrollments Panel
            setTimeout(() => {
                window.location.href = '/student/enrollments';
            }, 3000);

        } catch (error) {
            console.error('Enrollment network error:', error);
            modalAlertBox.className = 'alert-danger';
            modalAlertBox.innerHTML = `<i class="fas fa-wifi"></i> <div>Network error. Please check your connection.</div>`;
            modalAlertBox.style.display = 'flex';
        } finally {
            // Unlock UI if it didn't redirect
            if (btn && modalAlertBox.className !== 'alert-success') {
                btn.disabled = false;
                btn.innerHTML = 'Enroll Now';
            }
        }
    };

    // --- Event Listeners ---

    function attachEventListeners() {
        if (closeModalBtn) {
            closeModalBtn.addEventListener("click", () => {
                scheduleModal.style.display = "none";
            });
        }

        window.addEventListener("click", (event) => {
            if (event.target === scheduleModal) {
                scheduleModal.style.display = "none";
            }
        });
    }

});
