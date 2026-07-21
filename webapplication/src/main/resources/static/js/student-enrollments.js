/**
 * @typedef {Object} EnrollmentResponseResource
 * @property {number} studentId
 * @property {string} studentFullName
 * @property {string} studentEmail
 * @property {number} enrollmentId
 * @property {string} enrollmentDate
 * @property {"PENDING_ENROLL" | "ACTIVE" | "PENDING_DROP" | "DROPPED"} status
 * @property {number} scheduledSlotId
 * @property {string} courseTitle
 * @property {string} teacherName
 * @property {"MONDAY" | "TUESDAY" | "WEDNESDAY" | "THURSDAY" | "FRIDAY" | "SATURDAY" | "SUNDAY"} dayOfWeek
 * @property {string} startTime
 * @property {string} endTime
 * @property {string|null} classroom
 */

document.addEventListener("DOMContentLoaded", () => {

    // --- DOM ELEMENTS CACHING ---

    /** @type {HTMLDivElement} */ const enrollmentsGrid = document.getElementById('enrollmentsGrid');
    /** @type {NodeListOf<HTMLButtonElement>} */ const filterBtns = document.querySelectorAll('.filter-btn');

    // Modal Elements
    /** @type {HTMLDivElement} */ const dropConfirmModal = document.getElementById('dropConfirmModal');
    /** @type {HTMLSpanElement} */ const closeDropModalBtn = document.getElementById('closeDropModalBtn');
    /** @type {HTMLButtonElement} */ const cancelDropBtn = document.getElementById('cancelDropBtn');
    /** @type {HTMLButtonElement} */ const confirmDropBtn = document.getElementById('confirmDropBtn');
    /** @type {HTMLElement} */ const dropCourseName = document.getElementById('dropCourseName');

    // --- STATE MANAGEMENT ---

    /** @type {EnrollmentResponseResource[]} */ let allEnrollments = [];
    /** @type {number|null} */ let enrollmentIdToDrop = null;
    /** @type {string} */ let currentFilter = 'ALL';

    // --- INITIALIZATION ---

    init();

    function init() {
        fetchEnrollments().catch(console.error);
        attachEventListeners();
    }

    // --- FUNCTIONS ---

    /**
     * Fetches all enrollments for the student.
     */
    async function fetchEnrollments() {
        try {
            enrollmentsGrid.innerHTML = '<div class="loading-spinner"><i class="fas fa-circle-notch fa-spin fa-2x"></i><p>Loading enrollments...</p></div>';

            const response = await fetch('/api/students/me/enrollments', {
                method: 'GET',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'same-origin'
            });

            if (!response.ok) {
                showErrorState();
                return;
            }

            allEnrollments = await response.json();
            renderGrid();

        } catch (error) {
            console.error('Fetch error:', error);
            showErrorState();
        }
    }

    /**
     * Submits a PATCH request to drop a specific enrollment.
     */
    async function submitDropRequest() {
        if (!enrollmentIdToDrop) return;

        try {
            // Disable button during request to prevent double-clicks
            confirmDropBtn.disabled = true;
            confirmDropBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Processing...';

            const response = await fetch(`/api/students/me/enrollments/${enrollmentIdToDrop}/drop`, {
                method: 'PATCH',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'same-origin'
            });

            if (!response.ok) {
                console.error('Drop error: Backend returned status ' + response.status);
                Toast.show('Failed to submit drop request. Please try again.', 'danger');
                return;
            }

            // Success! Close modal and refresh list
            closeModal();
            Toast.show('Drop request submitted successfully. Awaiting admin approval.', 'success');
            await fetchEnrollments();

        } catch (error) {
            console.error('Network error during drop request:', error);
            Toast.show('Network error. Please check your connection and try again.', 'danger');
        } finally {
            // Restore button state
            confirmDropBtn.disabled = false;
            confirmDropBtn.innerHTML = 'Yes, Request Drop';
        }
    }

    // --- RENDERING & FILTERING ---

    function renderGrid() {
        enrollmentsGrid.innerHTML = '';

        // Apply Client-Side Filtering
        let filtered = allEnrollments;
        if (currentFilter === 'ACTIVE') {
            filtered = allEnrollments.filter(e => e.status === 'ACTIVE');
        } else if (currentFilter === 'PENDING') {
            filtered = allEnrollments.filter(e => e.status === 'PENDING_ENROLL' || e.status === 'PENDING_DROP');
        } else if (currentFilter === 'DROPPED') {
            filtered = allEnrollments.filter(e => e.status === 'DROPPED');
        }

        if (filtered.length === 0) {
            enrollmentsGrid.innerHTML = '<div class="grid-empty-msg"><i class="fas fa-folder-open fa-2x"></i><p>No enrollments found for this category.</p></div>';
            return;
        }

        filtered.forEach(enrollment => {
            const card = buildEnrollmentCard(enrollment);
            enrollmentsGrid.appendChild(card);
        });
    }

    /**
     * Builds the HTML element for an enrollment card.
     * @param {EnrollmentResponseResource} enrollment
     * @returns {HTMLElement}
     */
    function buildEnrollmentCard(enrollment) {
        const card = document.createElement('div');
        card.className = 'enrollment-card';

        const startTimeShort = enrollment.startTime.substring(0, 5);
        const endTimeShort = enrollment.endTime.substring(0, 5);

        // Online vs Physical Logic
        const isOnline = !enrollment.classroom;
        const roomName = isOnline ? 'Online' : enrollment.classroom;
        const roomIcon = isOnline ? 'fas fa-laptop' : 'fas fa-map-marker-alt';

        // Format Date
        const dateObj = new Date(enrollment.enrollmentDate);
        const formattedDate = dateObj.toLocaleDateString('en-GB');

        // Status Badge Mapping
        let badgeClass;
        let badgeText = enrollment.status.replace('_', ' ');

        if (enrollment.status === 'ACTIVE')
            badgeClass = 'status-active';
        else if (enrollment.status === 'PENDING_ENROLL')
            badgeClass = 'status-pending-enroll';
        else if (enrollment.status === 'PENDING_DROP')
            badgeClass = 'status-pending-drop';
        else badgeClass = 'status-dropped';

        // Footer Action Area
        let actionHTML;
        if (enrollment.status === 'ACTIVE') {
            actionHTML = `<button class="btn-drop" onclick="window.triggerDropModal(${enrollment.enrollmentId}, 
                '${enrollment.courseTitle.replace(/'/g, "\\'")}')"><i class="fas fa-minus-circle"></i> Request Drop</button>`;
        } else if (enrollment.status === 'PENDING_DROP') {
            actionHTML = `<p class="action-msg"><i class="fas fa-clock"></i> Drop Pending Approval</p>`;
        } else if (enrollment.status === 'PENDING_ENROLL') {
            actionHTML = `<p class="action-msg"><i class="fas fa-hourglass-half"></i> Awaiting Admin Approval</p>`;
        } else {
            actionHTML = `<p class="action-msg"><i class="fas fa-history"></i> Inactive Course</p>`;
        }

        card.innerHTML = `
            <div class="card-header">
                <div class="card-title-group">
                    <h3>${enrollment.courseTitle}</h3>
                    <span class="teacher-name"><i class="fas fa-chalkboard-teacher"></i> ${enrollment.teacherName}</span>
                </div>
                <span class="status-badge ${badgeClass}">${badgeText}</span>
            </div>
            <div class="card-body">
                <div class="detail-row"><i class="far fa-calendar-alt"></i> Every ${enrollment.dayOfWeek}</div>
                <div class="detail-row"><i class="far fa-clock"></i> ${startTimeShort} - ${endTimeShort}</div>
                <div class="detail-row"><i class="${roomIcon}"></i> ${roomName}</div>
                
                <div class="enrollment-date"><i class="fas fa-info-circle"></i> Enrolled on: ${formattedDate}</div>
            </div>
            <div class="card-actions">
                ${actionHTML}
            </div>
        `;

        return card;
    }

    function showErrorState() {
        enrollmentsGrid.innerHTML = '<div class="grid-error-msg"><i class="fas fa-exclamation-triangle fa-2x"></i>' +
            '<p>Error loading enrollments. Please refresh the page.</p></div>';
    }

    // --- MODAL & EVENT LISTENERS ---

    // Make the function globally accessible for the onclick attribute in HTML string
    window.triggerDropModal = function(enrollmentId, courseTitle) {
        enrollmentIdToDrop = enrollmentId;
        dropCourseName.textContent = courseTitle;
        dropConfirmModal.classList.add('show');
    };

    function closeModal() {
        dropConfirmModal.classList.remove('show');
        enrollmentIdToDrop = null;
    }

    function attachEventListeners() {
        // Modal Buttons
        if (closeDropModalBtn) closeDropModalBtn.addEventListener('click', closeModal);
        if (cancelDropBtn) cancelDropBtn.addEventListener('click', closeModal);
        if (confirmDropBtn) confirmDropBtn.addEventListener('click', submitDropRequest);

        // Outside Click Modal Close
        window.addEventListener('click', (event) => {
            if (event.target === dropConfirmModal) {
                closeModal();
            }
        });

        // Tab Filters
        // noinspection DuplicatedCode
        filterBtns.forEach(btn => {
            btn.addEventListener('click', (e) => {
                // Update active class
                filterBtns.forEach(b => b.classList.remove('active'));
                e.target.classList.add('active');

                // Update state and re-render
                currentFilter = e.target.getAttribute('data-filter');
                renderGrid();
            });
        });
    }

});
