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
 * @property {string} [classroom]
 * @property {number} capacity
 * @property {number} availableSeats
 */

/**
 * @typedef {Object} CourseResource
 * @property {number} id
 * @property {string} title
 */

/**
 * @typedef {Object} CourseAssignmentResource
 * @property {number} courseId
 * @property {number} teacherId
 * @property {string} teacherName
 */

/**
 * @typedef {Object} EnrolledStudentResource
 * @property {number} studentId
 * @property {string} fullName
 * @property {string} email
 * @property {string} username
 * @property {string} [address]
 * @property {string} enrollmentDate
 * @property {"PENDING_ENROLL" | "ACTIVE" | "PENDING_DROP" | "DROPPED"} status
 */

document.addEventListener("DOMContentLoaded", () => {

    // --- DOM Elements Caching ---

    // Board Element
    /** @type {HTMLDivElement} */ const timetableBoard = document.getElementById("timetableBoard");

    // Slot Modal Elements
    /** @type {HTMLDivElement} */ const slotModal = document.getElementById("slotModal");
    /** @type {HTMLSpanElement} */ const closeModalBtn = document.getElementById("closeModalBtn");
    /** @type {HTMLButtonElement} */ const openCreateSlotModalBtn = document.getElementById("openCreateSlotModalBtn");

    // Roster Modal Elements
    /** @type {HTMLDivElement} */ const rosterModal = document.getElementById("rosterModal");
    /** @type {HTMLSpanElement} */ const closeRosterModalBtn = document.getElementById("closeRosterModalBtn");
    /** @type {HTMLHeadingElement} */ const rosterModalTitle = document.getElementById("rosterModalTitle");
    /** @type {HTMLParagraphElement} */ const rosterModalSubtitle = document.getElementById("rosterModalSubtitle");
    /** @type {HTMLDivElement} */ const rosterLoading = document.getElementById("rosterLoading");
    /** @type {HTMLDivElement} */ const rosterEmptyState = document.getElementById("rosterEmptyState");
    /** @type {HTMLDivElement} */ const rosterTableContainer = document.getElementById("rosterTableContainer");
    /** @type {HTMLTableSectionElement} */ const rosterTableBody = document.getElementById("rosterTableBody");

    // Form Elements
    /** @type {HTMLFormElement} */ const slotForm = document.getElementById("slotForm");
    /** @type {HTMLSelectElement} */ const slotCourse = document.getElementById("slotCourse");
    /** @type {HTMLSelectElement} */ const slotTeacher = document.getElementById("slotTeacher");
    /** @type {HTMLSelectElement} */ const slotDay = document.getElementById("slotDay");
    /** @type {HTMLInputElement} */ const slotStartTime = document.getElementById("slotStartTime");
    /** @type {HTMLInputElement} */ const slotEndTime = document.getElementById("slotEndTime");
    /** @type {HTMLInputElement} */ const slotClassroom = document.getElementById("slotClassroom");
    /** @type {HTMLInputElement} */ const slotCapacity = document.getElementById("slotCapacity");
    /** @type {HTMLButtonElement} */ const saveSlotBtn = document.getElementById("saveSlotBtn");
    /** @type {HTMLDivElement} */ const slotAlert = document.getElementById("slotAlert");

    // --- Global State ---
    /** @type {ScheduledSlotResource[]} */ let allSlots = [];
    /** @type {CourseResource[]} */ let allCourses = [];
    /** @type {CourseAssignmentResource[]} */ let allAssignments = [];

    const DAYS_OF_WEEK = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];

    // --- Initialization ---
    initData().catch(e => console.error("Initialization error:", e));

    /**
     * Initializes the dashboard by fetching all required data in parallel.
     * @returns {Promise<void>}
     */
    async function initData() {
        timetableBoard.innerHTML = `<div class="loading-spinner">Fetching timetable data...</div>`;

        try {
            // Fetch everything in parallel for maximum performance
            const [slotsRes, coursesRes, assignmentsRes] = await Promise.all([
                ApiService.request('/api/slots', { method: 'GET' }),
                ApiService.request('/api/courses', { method: 'GET' }),
                ApiService.request('/api/courses/assignments', { method: 'GET' })
            ]);

            if (slotsRes.ok && coursesRes.ok && assignmentsRes.ok) {
                allSlots = await slotsRes.json();
                allCourses = await coursesRes.json();
                allAssignments = await assignmentsRes.json();

                renderKanbanBoard();
                populateCourseDropdown();
            } else {
                showBoardError("Failed to fetch necessary data from the server.");
            }
        } catch (error) {
            showBoardError("Network error during initialization.");
        }
    }

    // --- Core UI Rendering (Kanban Board) ---

    /**
     * Renders the weekly Kanban board based on the global allSlots array.
     */
    function renderKanbanBoard() {
        timetableBoard.innerHTML = ""; // Clear board

        DAYS_OF_WEEK.forEach(day => {
            // Filter slots for this specific day
            const daySlots = allSlots.filter(slot => slot.dayOfWeek === day);

            // Create Column
            const col = document.createElement("div");
            col.className = "kanban-column";

            // Format Day String (e.g., MONDAY -> Monday)
            const formattedDay = day.charAt(0) + day.slice(1).toLowerCase();

            col.innerHTML = `
                <div class="kanban-column-header">${formattedDay} <span style="color:#7f8c8d; font-size:0.8rem;">(${daySlots.length})</span></div>
                <div class="kanban-cards-container" id="container-${day}"></div>
            `;
            timetableBoard.appendChild(col);

            const container = col.querySelector(`#container-${day}`);

            if (daySlots.length === 0) {
                container.innerHTML = `<div style="text-align:center; color:#bdc3c7; font-size:0.9rem; padding: 20px;">No classes</div>`;
            } else {
                daySlots.forEach(slot => {
                    const card = createSlotCard(slot);
                    container.appendChild(card);
                });
            }
        });
    }

    /**
     * Creates and returns a DOM element representing a scheduled slot card.
     * @param {ScheduledSlotResource} slot - The slot data object.
     * @returns {HTMLDivElement} The constructed card element.
     */
    function createSlotCard(slot) {
        const card = document.createElement("div");
        card.className = "slot-card";

        // Format Time: "16:30:00" -> "16:30"
        const start = slot.startTime.substring(0, 5);
        const end = slot.endTime.substring(0, 5);

        // Badges Logic
        const isOnline = !slot.classroom || slot.classroom.trim() === '';
        const classroomBadge = isOnline
            ? `<span class="badge-classroom badge-online"><i class="fas fa-globe"></i> Remote / Online</span>`
            : `<span class="badge-classroom"><i class="fas fa-door-open"></i> ${slot.classroom}</span>`;

        // Capacity & Seats Logic
        const isFull = slot.availableSeats === 0;

        // Calculation of occupied seats
        const occupiedSeats = slot.capacity - slot.availableSeats;

        const seatsText = isFull ? `${occupiedSeats} / ${slot.capacity} &dash; FULL` : `${occupiedSeats} / ${slot.capacity}`;
        const capacityIcon = isFull ? '<i class="fas fa-exclamation-triangle"></i>' : '<i class="fas fa-users"></i>';
        const capacityColorClass = isFull ? 'badge-full' : '';

        const capacityBadge = `<span class="badge-classroom ${capacityColorClass}">${capacityIcon} ${seatsText}</span>`;

        card.innerHTML = `
        <div class="slot-time"><i class="far fa-clock"></i> ${start} &ndash; ${end}</div>
        <div class="slot-course">${slot.courseTitle}</div>
        <div class="slot-teacher"><i class="fas fa-chalkboard-teacher"></i> ${slot.teacherName}</div>
        <div class="slot-badges">
            ${classroomBadge}
            ${capacityBadge} </div>
        <div class="slot-actions"></div>
        `;

        const actionsContainer = card.querySelector('.slot-actions');
        const hasStudents = slot.availableSeats < slot.capacity;

        if (hasStudents) {
            // Defensive UX
            actionsContainer.innerHTML = `<button class="btn-icon-delete" disabled title="Cannot delete: There are students enrolled in this class.">
                                            <i class="fas fa-trash-alt"></i></button>`;
        } else {
            // If it is empty, create the "Smart" Button
            const deleteBtn = document.createElement("button");
            deleteBtn.className = "btn-icon-delete";
            deleteBtn.title = "Delete Class";
            deleteBtn.innerHTML = '<i class="fas fa-trash-alt"></i>';

            let timerId = null;

            deleteBtn.addEventListener("click", async (e) => {

                // Stops Event Bubbling to the class card
                e.stopPropagation();

                if (deleteBtn.classList.contains("btn-icon-sure")) {
                    clearTimeout(timerId);
                    deleteBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';
                    deleteBtn.disabled = true;

                    try {
                        const response = await ApiService.request(`/api/courses/${slot.courseId}/slots/${slot.id}`, { method: 'DELETE' });
                        if (response.ok) {
                            // Refreshes the entire Board
                            await refreshSlotsData();
                        } else {
                            Toast.show("Failed to delete Class.", "danger");
                            resetButton();
                        }
                    } catch (error) {
                        Toast.show("Network error.", "danger");
                        resetButton();
                    }
                }
                else {
                    deleteBtn.innerText = "Sure?";
                    deleteBtn.classList.add("btn-icon-sure");

                    timerId = setTimeout(() => {
                        resetButton();
                    }, 4000);
                }
            });

            // Helper function to restore the button
            function resetButton() {
                deleteBtn.innerHTML = '<i class="fas fa-trash-alt"></i>';
                deleteBtn.classList.remove("btn-icon-sure");
                deleteBtn.disabled = false;
            }

            actionsContainer.appendChild(deleteBtn);
        }

        // Make the card Clickable for the Roster
        card.addEventListener('click', async (e) => {
            // If the click was on the delete button (or on an element within it, e.g. an icon), ignore it
            if (e.target.closest('.btn-icon-delete')) {
                return;
            }
            // Otherwise, open the Roster properly awaiting the promise
            await openRosterModal(slot);
        });

        return card;
    }

    /**
     * Fetches and displays the class roster (enrolled students) for a specific slot.
     * @param {ScheduledSlotResource} slot - The slot object to fetch students for.
     * @returns {Promise<void>}
     */
    async function openRosterModal(slot) {
        // 1. Display Modal and Set Titles
        rosterModal.style.display = "flex";
        rosterModalTitle.innerText = slot.courseTitle;
        rosterModalSubtitle.innerHTML = `<i class="far fa-clock"></i> ${slot.dayOfWeek}, 
            ${slot.startTime.substring(0, 5)} &ndash; ${slot.endTime.substring(0, 5)} | <i class="fas fa-chalkboard-teacher"></i> ${slot.teacherName}`;

        // 2. Show Loading, hide previous data
        rosterLoading.style.display = "block";
        rosterEmptyState.style.display = "none";
        rosterTableContainer.style.display = "none";
        rosterTableBody.innerHTML = "";

        try {
            // 3. API Call
            const response = await ApiService.request(`/api/admin/slots/${slot.id}/students`);

            /** @type {EnrolledStudentResource[]} */
            const students = await response.json();

            rosterLoading.style.display = "none";

            // 4. Empty State or Table Display
            if (!students || students.length === 0) {
                rosterEmptyState.style.display = "block";
            } else {
                rosterTableContainer.style.display = "block";

                students.forEach(student => {
                    const tr = document.createElement("tr");

                    // Styling for Status
                    let statusBadge = "status-active-sm";
                    if (student.status !== "ACTIVE") {
                        statusBadge = "status-pending-sm";
                    }

                    tr.innerHTML = `
                    <td><strong>${student.fullName}</strong></td>
                    <td class="text-muted">${student.username}</td>
                    <td><a href="mailto:${student.email}" class="email-link"><i class="far fa-envelope"></i> ${student.email}</a></td>
                    <td>${student.enrollmentDate}</td>
                    <td><span class="status-badge-sm ${statusBadge}">${student.status}</span></td>
                    `;
                    rosterTableBody.appendChild(tr);
                });
            }
        } catch (error) {
            console.error("Error fetching roster:", error);
            rosterLoading.style.display = "none";
            Toast.show("Failed to load class roster. Please try again.", "danger");
        }
    }

    // --- Helper Functions ---

    /**
     * Populates the course select dropdown in the creation form.
     */
    function populateCourseDropdown() {
        slotCourse.innerHTML = `<option value="">Select a course...</option>`;
        allCourses.forEach(c => {
            const opt = document.createElement("option");
            opt.value = c.id.toString();
            opt.innerText = c.title;
            slotCourse.appendChild(opt);
        });
    }

    /**
     * Fetches the latest scheduled slots from the server and re-renders the board.
     * @returns {Promise<void>}
     */
    async function refreshSlotsData() {
        const response = await ApiService.request('/api/slots', { method: 'GET' });
        if (response.ok) {
            allSlots = await response.json();
            renderKanbanBoard();
        }
    }

    /**
     * Displays an alert message inside the slot creation modal.
     * @param {string} msg - The message to display.
     * @param {string} type - The alert type (e.g., "danger", "success").
     */
    function showSlotAlert(msg, type) {
        slotAlert.className = `modal-alert alert-${type}`;
        slotAlert.innerText = msg;
        slotAlert.style.display = "block";
    }

    /**
     * Displays a global error message on the kanban board area.
     * @param {string} msg - The error message.
     */
    function showBoardError(msg) {
        timetableBoard.innerHTML = `<div style="width:100%; text-align:center; color: #e74c3c; padding: 40px;">${msg}</div>`;
    }

    // --- Static Event Listeners (Control Panel) ---

    // 1. Slot Form & Modal Listeners
    openCreateSlotModalBtn.addEventListener("click", () => {
        slotForm.reset();
        slotTeacher.innerHTML = `<option value="">Select a course first...</option>`;
        slotTeacher.disabled = true;
        slotAlert.style.display = "none";
        slotModal.style.display = "flex";
    });

    closeModalBtn.addEventListener("click", () => { slotModal.style.display = "none"; });

    // 2. Roster Modal Listeners
    closeRosterModalBtn.addEventListener("click", () => { rosterModal.style.display = "none"; });

    // 3. Global Window Click (Close modals when clicking outside)
    window.addEventListener("click", (e) => {
        if (e.target === slotModal) slotModal.style.display = "none";
        if (e.target === rosterModal) rosterModal.style.display = "none";
    });

    // 4. Cascading Dropdown Logic (Course -> Teacher)
    slotCourse.addEventListener("change", () => {
        const selectedCourseId = parseInt(slotCourse.value, 10);

        if (!selectedCourseId) {
            slotTeacher.innerHTML = `<option value="">Select a course first...</option>`;
            slotTeacher.disabled = true;
            return;
        }

        // Defensive UI: Filter only teachers assigned to THIS course
        const availableTeachers = allAssignments.filter(a => a.courseId === selectedCourseId);

        slotTeacher.innerHTML = `<option value="">Select a teacher...</option>`;

        if (availableTeachers.length === 0) {
            slotTeacher.innerHTML = `<option value="">No teachers assigned to this course!</option>`;
            slotTeacher.disabled = true;
        } else {
            availableTeachers.forEach(t => {
                const opt = document.createElement("option");
                opt.value = t.teacherId.toString();
                opt.innerText = t.teacherName;
                slotTeacher.appendChild(opt);
            });
            slotTeacher.disabled = false;
        }
    });

    // 5. Submit New Class (Slot)
    slotForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        saveSlotBtn.disabled = true;
        saveSlotBtn.innerText = "Saving...";
        slotAlert.style.display = "none";

        const courseId = slotCourse.value;
        const payload = {
            teacherId: parseInt(slotTeacher.value, 10),
            dayOfWeek: slotDay.value,
            startTime: slotStartTime.value,
            endTime: slotEndTime.value,
            classroom: slotClassroom.value.trim() === '' ? null : slotClassroom.value.trim(),
            capacity: parseInt(slotCapacity.value, 10)
        };

        try {
            const response = await ApiService.request(`/api/courses/${courseId}/slots`, {
                method: 'POST',
                body: JSON.stringify(payload)
            });

            if (response.status === 201) {
                // Success! Refresh the board
                await refreshSlotsData();
                slotModal.style.display = "none";
            } else if (response.status === 400 || response.status === 403) {
                // Catch overlapping issues and validation errors
                const errorData = await response.json();
                showSlotAlert(errorData.message || "Scheduling conflict detected.", "danger");
            } else {
                showSlotAlert("An unexpected error occurred.", "danger");
            }
        } catch (error) {
            showSlotAlert("Network error.", "danger");
        } finally {
            saveSlotBtn.disabled = false;
            saveSlotBtn.innerText = "Save Class";
        }
    });

});
