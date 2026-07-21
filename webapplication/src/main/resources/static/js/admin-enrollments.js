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

    // --- DOM Elements Caching ---

    // Tabs
    /** @type {HTMLButtonElement} */ const tabBtnEnroll = document.getElementById("tabBtnEnroll");
    /** @type {HTMLButtonElement} */ const tabBtnDrop = document.getElementById("tabBtnDrop");
    /** @type {HTMLDivElement} */ const paneEnroll = document.getElementById("paneEnroll");
    /** @type {HTMLDivElement} */ const paneDrop = document.getElementById("paneDrop");
    /** @type {HTMLSpanElement} */ const enrollCount= document.getElementById("enrollCount");
    /** @type {HTMLSpanElement} */ const dropCount= document.getElementById("dropCount");

    // Enroll UI Elements
    /** @type {HTMLDivElement} */ const enrollLoading = document.getElementById("enrollLoading");
    /** @type {HTMLDivElement} */ const enrollEmpty = document.getElementById("enrollEmpty");
    /** @type {HTMLDivElement} */ const enrollCardsContainer = document.getElementById("enrollCardsContainer");

    // Drop UI Elements
    /** @type {HTMLDivElement} */ const dropLoading = document.getElementById("dropLoading");
    /** @type {HTMLDivElement} */ const dropEmpty = document.getElementById("dropEmpty");
    /** @type {HTMLDivElement} */ const dropCardsContainer = document.getElementById("dropCardsContainer");

    // --- Initialization ---
    initInbox().catch(e => console.error("Initialization error:", e));

    /**
     * Initializes the inbox by fetching both queues concurrently.
     * @returns {Promise<void>}
     */
    async function initInbox() {
        // Use Promise.all to start both calls to the Backend at the same time
        await Promise.all([
            fetchEnrollRequests(),
            fetchDropRequests()
        ]);
    }

    // --- Core Logic ---

    /**
     * Fetches pending enrollment requests and renders the action cards.
     * @returns {Promise<void>}
     */
    async function fetchEnrollRequests() {
        enrollLoading.style.display = "block";
        enrollEmpty.style.display = "none";
        enrollCardsContainer.innerHTML = "";

        try {
            const response = await ApiService.request('/api/admin/enrollments/enroll-requests', { method: 'GET' });

            /** @type {EnrollmentResponseResource[]} */
            const requests = await response.json();

            enrollLoading.style.display = "none";

            // Update the Badge on the Tab
            enrollCount.innerText = requests.length.toString();

            if (!requests || requests.length === 0) {
                enrollEmpty.style.display = "block";
            } else {
                requests.forEach(req => {
                    const card = createEnrollActionCard(req);
                    enrollCardsContainer.appendChild(card);
                });
            }
        } catch (error) {
            console.error("Error fetching enroll requests:", error);
            enrollLoading.style.display = "none";
            Toast.show("Failed to load enrollment requests. Please check your connection.", "danger");
        }
    }

    /**
     * Creates a DOM element representing a single pending enrollment request.
     * @param {EnrollmentResponseResource} req
     * @returns {HTMLDivElement}
     */
    function createEnrollActionCard(req) {
        const card = document.createElement("div");
        card.className = "action-card";

        // Time (18:00:00 -> 18:00) and room formatting
        const start = req.startTime.substring(0, 5);
        const end = req.endTime.substring(0, 5);
        const classroomHtml = req.classroom
            ? `<i class="fas fa-door-open"></i> ${req.classroom}`
            : `<i class="fas fa-globe"></i> Online`;

        card.innerHTML = `
            <div class="action-card-content">
                
                <div class="card-section card-student">
                    <div class="student-name"><i class="fas fa-user-graduate text-muted"></i> ${req.studentFullName}</div>
                    <div class="student-email"><i class="far fa-envelope"></i> ${req.studentEmail}</div>
                    <div class="request-date">Requested on: ${req.enrollmentDate}</div>
                </div>
                
                <div class="card-section card-course">
                    <div class="course-title">${req.courseTitle}</div>
                    <div class="course-details">
                        <i class="far fa-clock"></i> ${req.dayOfWeek}, ${start} &ndash; ${end} | ${classroomHtml}
                    </div>
                    <div class="course-details">
                        <i class="fas fa-chalkboard-teacher"></i> ${req.teacherName}
                    </div>
                </div>
                
                <div class="card-section card-actions">
                    <button class="btn-action-sm btn-reject" title="Reject Request"><i class="fas fa-times"></i> Reject</button>
                    <button class="btn-action-sm btn-approve" title="Approve Request"><i class="fas fa-check"></i> Approve</button>
                </div>
                
            </div>
        `;

        // Add Event Listeners to the buttons of this specific card
        const approveBtn = card.querySelector('.btn-approve');
        const rejectBtn = card.querySelector('.btn-reject');

        approveBtn.addEventListener('click', async () => {
            await processEnrollmentAction(req.enrollmentId, 'APPROVE', approveBtn, rejectBtn);
        });

        // --- Inline Confirmation Pattern for Reject ---
        let timerId = null;
        const originalRejectHtml = '<i class="fas fa-times"></i> Reject';

        rejectBtn.addEventListener('click', async () => {

            // 2nd Click: If the button is already in the "Sure?" state
            if (rejectBtn.classList.contains("btn-sure-state")) {
                clearTimeout(timerId); // Ακυρώνουμε το χρονόμετρο

                // Restore the HTML before calling process...
                // so that if the API fails, it returns to the correct verb ("Reject")
                rejectBtn.innerHTML = originalRejectHtml;
                rejectBtn.classList.remove("btn-sure-state");

                // Perform the actual deletion
                await processEnrollmentAction(req.enrollmentId, 'REJECT', approveBtn, rejectBtn);
            }
            // 1st Click: ask for confirmation
            else {
                rejectBtn.innerHTML = '<i class="fas fa-question-circle"></i> Sure?';
                rejectBtn.classList.add("btn-sure-state");

                // Begin the 4-second countdown
                timerId = setTimeout(() => {
                    rejectBtn.innerHTML = originalRejectHtml;
                    rejectBtn.classList.remove("btn-sure-state");
                }, 4000);
            }
        });

        return card;
    }

    /**
     * Handles the API call for Approving or Rejecting an enrollment.
     * @param {number} enrollmentId
     * @param {string} actionType - 'APPROVE' or 'REJECT'
     * @param {HTMLButtonElement} approveBtn
     * @param {HTMLButtonElement} rejectBtn
     */
    async function processEnrollmentAction(enrollmentId, actionType, approveBtn, rejectBtn) {
        // Defensive UX: Disabling both buttons while we wait for the API
        approveBtn.disabled = true;
        rejectBtn.disabled = true;

        const isApprove = actionType === 'APPROVE';
        const activeBtn = isApprove ? approveBtn : rejectBtn;
        const originalText = activeBtn.innerHTML;

        // Show Spinner on pressed button
        activeBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';

        try {
            const endpoint = isApprove
                ? `/api/admin/enrollments/${enrollmentId}/approve-enroll`
                : `/api/admin/enrollments/${enrollmentId}/reject-enroll`;

            const httpMethod = isApprove ? 'PATCH' : 'DELETE';

            const response = await ApiService.request(endpoint, { method: httpMethod });

            if (response.status === 204) {
                // Reload the list so that the card is gone and the Badge is renewed
                await fetchEnrollRequests();
            } else {
                Toast.show(`Failed to ${actionType.toLowerCase()} request.`, "danger");
                resetButtons();
            }
        } catch (error) {
            console.error(`Error during ${actionType}:`, error);
            Toast.show("Network error occurred.", "danger");
            resetButtons();
        }

        function resetButtons() {
            approveBtn.disabled = false;
            rejectBtn.disabled = false;
            activeBtn.innerHTML = originalText;
        }
    }

    /**
     * Fetches pending drop requests and renders the action cards.
     * @returns {Promise<void>}
     */
    async function fetchDropRequests() {
        dropLoading.style.display = "block";
        dropEmpty.style.display = "none";
        dropCardsContainer.innerHTML = "";

        try {
            const response = await ApiService.request('/api/admin/enrollments/drop-requests', { method: 'GET' });

            /** @type {EnrollmentResponseResource[]} */
            const requests = await response.json();

            dropLoading.style.display = "none";

            // Update the Badge on the Tab
            dropCount.innerText = requests.length.toString();

            if (!requests || requests.length === 0) {
                dropEmpty.style.display = "block";
            } else {
                requests.forEach(req => {
                    const card = createDropActionCard(req);
                    dropCardsContainer.appendChild(card);
                });
            }
        } catch (error) {
            console.error("Error fetching drop requests:", error);
            dropLoading.style.display = "none";
            Toast.show("Failed to load drop requests. Please check your connection.", "danger");
        }
    }

    /**
     * Creates a DOM element representing a single pending drop request.
     * @param {EnrollmentResponseResource} req
     * @returns {HTMLDivElement}
     */
    function createDropActionCard(req) {
        const card = document.createElement("div");
        card.className = "action-card card-drop"; // Προσθήκη της πορτοκαλί κλάσης

        // Time and room formatting
        const start = req.startTime.substring(0, 5);
        const end = req.endTime.substring(0, 5);
        const classroomHtml = req.classroom
            ? `<i class="fas fa-door-open"></i> ${req.classroom}`
            : `<i class="fas fa-globe"></i> Online`;

        card.innerHTML = `
            <div class="action-card-content">
                
                <div class="card-section card-student">
                    <div class="student-name"><i class="fas fa-user-minus text-muted"></i> ${req.studentFullName}</div>
                    <div class="student-email"><i class="far fa-envelope"></i> ${req.studentEmail}</div>
                    <div class="request-date">Enrolled on: ${req.enrollmentDate}</div>
                </div>
                
                <div class="card-section card-course">
                    <div class="course-title">${req.courseTitle}</div>
                    <div class="course-details">
                        <i class="far fa-clock"></i> ${req.dayOfWeek}, ${start} &ndash; ${end} | ${classroomHtml}
                    </div>
                    <div class="course-details">
                        <i class="fas fa-chalkboard-teacher"></i> ${req.teacherName}
                    </div>
                </div>
                
                <div class="card-section card-actions">
                    <button class="btn-action-sm btn-approve-drop" title="Approve Unenrollment">
                        <i class="fas fa-sign-out-alt"></i> Approve Drop
                    </button>
                </div>
                
            </div>
        `;

        // Add Event Listener to the single button
        const approveDropBtn = card.querySelector('.btn-approve-drop');

        // --- Inline Confirmation Pattern (Sure?) ---
        let timerId = null;
        const originalDropHtml = '<i class="fas fa-sign-out-alt"></i> Approve Drop';

        approveDropBtn.addEventListener('click', async () => {

            // 2nd Click: If the button is already in the "Sure?" state
            if (approveDropBtn.classList.contains("btn-sure-state")) {
                clearTimeout(timerId); // Ακυρώνουμε το χρονόμετρο

                // Restore the HTML before calling the API
                approveDropBtn.innerHTML = originalDropHtml;
                approveDropBtn.classList.remove("btn-sure-state");

                // Perform the withdrawal approval (Soft Delete)
                await processDropAction(req.enrollmentId, approveDropBtn);
            }
            // 1st Click: ask for confirmation
            else {
                approveDropBtn.innerHTML = '<i class="fas fa-question-circle"></i> Sure?';
                approveDropBtn.classList.add("btn-sure-state");

                // Begin the 4-second countdown
                timerId = setTimeout(() => {
                    approveDropBtn.innerHTML = originalDropHtml;
                    approveDropBtn.classList.remove("btn-sure-state");
                }, 4000);
            }
        });

        return card;
    }

    /**
     * Handles the API call for Approving a drop request.
     * @param {number} enrollmentId
     * @param {HTMLButtonElement} btn
     */
    async function processDropAction(enrollmentId, btn) {
        btn.disabled = true;
        const originalText = btn.innerHTML;
        btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Processing...';

        try {
            const response = await ApiService.request(`/api/admin/enrollments/${enrollmentId}/approve-drop`, { method: 'PATCH' });

            if (response.status === 204) {
                // Reload the list so that the card is gone and the Badge is renewed
                await fetchDropRequests();
            } else {
                Toast.show("Failed to approve drop request.", "danger");
                resetButton();
            }
        } catch (error) {
            console.error(`Error during Drop Approval:`, error);
            Toast.show("Network error occurred.", "danger");
            resetButton();
        }

        function resetButton() {
            btn.disabled = false;
            btn.innerHTML = originalText;
        }
    }

    // --- Static Event Listeners (Control Panel) ---

    // Tab Switching Logic
    tabBtnEnroll.addEventListener("click", () => switchTab("ENROLL"));
    tabBtnDrop.addEventListener("click", () => switchTab("DROP"));

    /**
     * Toggles active classes in Tabs and Panes
     * @param {string} tabName - "ENROLL" ή "DROP"
     */
    function switchTab(tabName) {
        if (tabName === "ENROLL") {
            tabBtnEnroll.classList.add("active");
            paneEnroll.classList.add("active");

            tabBtnDrop.classList.remove("active");
            paneDrop.classList.remove("active");
        } else {
            tabBtnDrop.classList.add("active");
            paneDrop.classList.add("active");

            tabBtnEnroll.classList.remove("active");
            paneEnroll.classList.remove("active");
        }
    }

});
