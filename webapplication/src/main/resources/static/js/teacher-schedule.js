/**
 * @typedef {Object} TeacherScheduleSlotResponseResource
 * @property {number} slotId
 * @property {string} courseTitle
 * @property {string} startTime
 * @property {string} endTime
 * @property {string} classroom
 * @property {"SCHEDULED" | "CANCELLED"} status
 * @property {string|null} cancelReason
 * @property {number} enrolledStudentsCount
 */

/**
 * @typedef {Object} TeacherDailyScheduleResponseResource
 * @property {string} date
 * @property {"MONDAY" | "TUESDAY" | "WEDNESDAY" | "THURSDAY" | "FRIDAY" | "SATURDAY" | "SUNDAY"} dayOfWeek
 * @property {TeacherScheduleSlotResponseResource[]} slots
 */

/**
 * @typedef {Object} LessonActivityResponseResource
 * @property {number} id
 * @property {number} slotId
 * @property {string} courseTitle
 * @property {string} date
 * @property {string} description
 */

document.addEventListener("DOMContentLoaded", () => {

    // --- DOM ELEMENTS CACHING ---

    // Grid & Layout
    /** @type {HTMLDivElement} */ const scheduleGrid = document.getElementById("scheduleGrid");
    /** @type {HTMLSpanElement} */ const weekRangeText = document.getElementById("weekRangeText");

    // Pagination Controls
    /** @type {HTMLButtonElement} */ const prevWeekBtn = document.getElementById("prevWeekBtn");
    /** @type {HTMLButtonElement} */ const nextWeekBtn = document.getElementById("nextWeekBtn");
    /** @type {HTMLButtonElement} */ const todayBtn = document.getElementById("todayBtn");

    // Modal Elements
    /** @type {HTMLDivElement} */ const activityModal = document.getElementById("activityModal");
    /** @type {HTMLSpanElement} */ const closeActivityModalBtn = document.getElementById("closeActivityModalBtn");
    /** @type {HTMLHeadingElement} */ const activityModalSubtitle = document.getElementById("activityModalSubtitle");
    /** @type {HTMLDivElement} */ const activityAlert = document.getElementById("activityAlert");

    // Form Elements
    /** @type {HTMLDivElement} */ const activityFormSection = document.querySelector(".activity-form-section");
    /** @type {HTMLFormElement} */ const activityForm = document.getElementById("activityForm");
    /** @type {HTMLInputElement} */ const activityDateInput = document.getElementById("activityDate");
    /** @type {HTMLTextAreaElement} */ const activityDescriptionInput = document.getElementById("activityDescription");
    /** @type {HTMLButtonElement} */ const saveActivityBtn = document.getElementById("saveActivityBtn");
    /** @type {HTMLButtonElement} */ const cancelEditBtn = document.getElementById("cancelEditBtn");

    // Timeline Elements
    /** @type {HTMLDivElement} */ const activityTimeline = document.getElementById("activityTimeline");
    /** @type {HTMLInputElement} */ const timelineFilterFrom = document.getElementById("timelineFilterFrom");
    /** @type {HTMLInputElement} */ const timelineFilterTo = document.getElementById("timelineFilterTo");
    /** @type {HTMLButtonElement} */ const clearTimelineFilterBtn = document.getElementById("clearTimelineFilterBtn");
    /** @type {HTMLDivElement} */ const timelineFilterEmptyMsg = document.getElementById("timelineFilterEmptyMsg");

    // --- GLOBAL STATE MANAGEMENT ---

    /** @type {Date} Tracks the currently viewed week's Monday */
    let currentDisplayedMonday = getMonday(new Date());

    /** @type {Object|null} The full slot object currently open in the modal */
    let currentSlot = null;

    /** @type {number|null} The ID of the slot currently open in the modal */
    let currentSlotId = null;

    /** @type {string|null} The Date string (YYYY-MM-DD) of the clicked slot card */
    let currentClickedDate = null;

    /** @type {number|null} The ID of the activity currently being edited (null if creating new) */
    let currentEditingActivityId = null;

    /** @type {LessonActivityResponseResource[]} Holds the fetched timeline data in memory for safe editing */
    let currentSlotActivities = [];

    // --- INITIALIZATION ---
    loadSchedule(currentDisplayedMonday).catch(e => console.error("Initial load error:", e));

    // --- EVENT LISTENERS ---

    // Pagination
    prevWeekBtn.addEventListener("click", () => {
        currentDisplayedMonday.setUTCDate(currentDisplayedMonday.getUTCDate() - 7);
        loadSchedule(currentDisplayedMonday).catch(e => console.error(e));
    });

    nextWeekBtn.addEventListener("click", () => {
        currentDisplayedMonday.setUTCDate(currentDisplayedMonday.getUTCDate() + 7);
        loadSchedule(currentDisplayedMonday).catch(e => console.error(e));
    });

    todayBtn.addEventListener("click", () => {
        currentDisplayedMonday = getMonday(new Date());
        loadSchedule(currentDisplayedMonday).catch(e => console.error(e));
    });

    // Modal Close
    closeActivityModalBtn.addEventListener("click", () => {
        activityModal.classList.add("d-none");
    });

    // Close modal when clicking completely outside the modal content
    window.addEventListener("click", (e) => {
        if (e.target === activityModal) {
            activityModal.classList.add("d-none");
        }
    });

    // Form Submissions
    activityForm.addEventListener("submit", (e) => {
        e.preventDefault();
        submitActivity().catch(err => console.error("Submit error:", err));
    });

    cancelEditBtn.addEventListener("click", () => {
        resetActivityForm();
    });

    // Timeline Filters
    [timelineFilterFrom, timelineFilterTo].forEach(input => {
        input.addEventListener("change", applyTimelineFilter);
    });

    clearTimelineFilterBtn.addEventListener("click", () => {
        timelineFilterFrom.value = "";
        timelineFilterTo.value = "";
        applyTimelineFilter();
    });

    // --- FUNCTIONS ---

    /**
     * Fetches and renders the teacher's schedule for a given week.
     * @param {Date} mondayDate - The Monday of the requested week.
     * @returns {Promise<void>}
     */
    async function loadSchedule(mondayDate) {
        scheduleGrid.innerHTML = `<div class="loading-spinner">Fetching your schedule...</div>`;

        // Sunday calculation
        const sundayDate = new Date(mondayDate);
        sundayDate.setUTCDate(mondayDate.getUTCDate() + 6);

        const startDateStr = getLocalIsoDate(mondayDate);
        const endDateStr = getLocalIsoDate(sundayDate);

        try {
            const response = await ApiService.request(`/api/teachers/me/schedule?startDate=${startDateStr}&endDate=${endDateStr}`,
                { method: 'GET' });

            if (response.ok) {

                /** @type {TeacherDailyScheduleResponseResource[]} */
                const weeklyData = await response.json();
                renderScheduleGrid(weeklyData);
                updateWeekBadge(weeklyData);
                checkAcademicYearBounds(mondayDate, sundayDate);

            } else if (response.status === 403) {

                const errorData = await response.json();
                showGridError(errorData.message || "Date range error.");
            } else {

                showGridError("Failed to load your schedule. Please try again later.");
            }
        } catch (error) {
            Toast.show("Network error. Could not connect to the server.", "danger");
            showGridError("Network error. Could not connect to the server.");
        }
    }

    /**
     * Renders the CSS Grid timeline for the week.
     * @param {TeacherDailyScheduleResponseResource[]} weeklyData
     */
    function renderScheduleGrid(weeklyData) {
        scheduleGrid.innerHTML = "";

        if (!weeklyData || weeklyData.length === 0) {
            const emptyEl = document.createElement("div");
            emptyEl.className = "grid-empty-msg";
            emptyEl.innerText = "No schedule data available.";
            scheduleGrid.appendChild(emptyEl);
            return;
        }

        weeklyData.forEach(dayData => {
            // Creating the column of the day
            const dayCol = document.createElement("div");
            dayCol.className = "day-column";

            // Header of the day
            const dateObj = new Date(dayData.date);
            const formattedDate = dateObj.toLocaleDateString('en-GB', { day: '2-digit', month: '2-digit' });

            dayCol.innerHTML = `
                <div class="day-header">
                    <span class="day-name">${dayData.dayOfWeek}</span>
                    <span class="day-date">${formattedDate}</span>
                </div>
            `;

            // Container for this day's lessons
            const slotsContainer = document.createElement("div");
            slotsContainer.className = "slots-container";

            if (!dayData.slots || dayData.slots.length === 0) {
                slotsContainer.innerHTML = `<div class="no-slots-msg">No classes</div>`;
            } else {
                // Building each lesson card
                dayData.slots.forEach(slot => {
                    const slotCard = createSlotCard(slot, dayData.date);
                    slotsContainer.appendChild(slotCard);
                });
            }

            dayCol.appendChild(slotsContainer);
            scheduleGrid.appendChild(dayCol);
        });
    }

    /**
     * Creates a DOM element for a single schedule slot card.
     * @param {TeacherScheduleSlotResponseResource} slot
     * @param {string} dateStr
     * @returns {HTMLDivElement}
     */
    function createSlotCard(slot, dateStr) {
        const card = document.createElement("div");
        const isCancelled = slot.status === "CANCELLED";

        card.className = `slot-card ${isCancelled ? 'is-cancelled' : 'is-scheduled'}`;

        // Time configuration
        const start = slot.startTime.substring(0, 5);
        const end = slot.endTime.substring(0, 5);

        // Student Badge
        const studentBadgeClass = slot.enrolledStudentsCount > 0 ? "has-students" : "no-students";
        const studentText = slot.enrolledStudentsCount === 1 ? "1 Student" : `${slot.enrolledStudentsCount} Students`;

        let html = `
            <h4 class="course-title">${slot.courseTitle}</h4>
            <div class="slot-detail"><i class="far fa-clock"></i> ${start} - ${end}</div>
            <div class="slot-detail"><i class="fas fa-map-marker-alt"></i> ${slot.classroom || 'Online'}</div>
        `;

        // Always show the student badge
        html += `<span class="students-badge ${studentBadgeClass}"><i class="fas fa-users"></i> ${studentText}</span>`;

        // And if it is canceled, we add the reason for the cancellation below
        if (isCancelled) {
            html += `<div class="cancelled-reason"><i class="fas fa-ban"></i> ${slot.cancelReason}</div>`;
        }

        card.innerHTML = html;

        card.addEventListener("click", () => {
            openActivityModal(slot, dateStr);
        });

        return card;
    }

    // --- ACTIVITY MODAL & TIMELINE LOGIC ---

    /**
     * Opens the modal, configures dynamic date limits [-14, +30], and fetches the timeline.
     * @param {TeacherScheduleSlotResponseResource} slot
     * @param {string} clickedDateStr
     */
    function openActivityModal(slot, clickedDateStr) {
        currentSlot = slot;
        currentSlotId = slot.slotId;
        currentClickedDate = clickedDateStr;
        activityModalSubtitle.innerText = `${slot.courseTitle} (${slot.classroom || 'Online'})`;

        // clean the filters
        if (typeof timelineFilterFrom !== 'undefined') timelineFilterFrom.value = "";
        if (typeof timelineFilterTo !== 'undefined') timelineFilterTo.value = "";

        resetActivityForm();

        // Calculate min/max dates [-14, +30] using LOCAL time
        const today = new Date();

        const minDate = new Date(today);
        minDate.setDate(minDate.getDate() - 14);
        const minDateStr = `${minDate.getFullYear()}-${String(minDate.getMonth() + 1).padStart(2, '0')}-${String(minDate.getDate()).padStart(2, '0')}`;

        const maxDate = new Date(today);
        maxDate.setDate(maxDate.getDate() + 30);
        const maxDateStr = `${maxDate.getFullYear()}-${String(maxDate.getMonth() + 1).padStart(2, '0')}-${String(maxDate.getDate()).padStart(2, '0')}`;

        // Safe timezone conversion
        activityDateInput.min = minDateStr;
        activityDateInput.max = maxDateStr;

        // Fetch existing timeline
        fetchActivityTimeline(currentSlotId).catch(err => console.error("Timeline fetch error:", err));

        activityModal.classList.remove("d-none");
    }

    /**
     * Fetches the activity timeline for the selected slot.
     * @param {number} slotId
     * @returns {Promise<void>}
     */
    async function fetchActivityTimeline(slotId) {
        activityTimeline.innerHTML = `<div class="loading-spinner">Loading history...</div>`;
        try {
            const response = await ApiService.request(`/api/teachers/me/activities?slotId=${slotId}`, { method: 'GET' });
            if (response.ok) {
                currentSlotActivities = await response.json();
                renderTimeline(currentSlotActivities);
            } else {
                activityTimeline.innerHTML = `<div class="grid-error-msg">Failed to load history.</div>`;
            }
        } catch (error) {
            activityTimeline.innerHTML = `<div class="grid-error-msg">Network error.</div>`;
        }
    }

    /**
     * Renders the timeline cards with Edit/Delete visibility using String comparison.
     * @param {LessonActivityResponseResource[]} activities
     */
    function renderTimeline(activities) {
        activityTimeline.innerHTML = "";

        if (!activities || activities.length === 0) {
            activityTimeline.innerHTML = `<div class="timeline-empty">No lesson activities recorded for this class yet.</div>`;
            return;
        }

        // String Comparison (YYYY-MM-DD) to avoid Timezone bugs
        const todayStr = getLocalIsoDate(new Date());

        const frozenDate = new Date();
        frozenDate.setUTCDate(frozenDate.getUTCDate() - 14);
        const frozenStr = getLocalIsoDate(frozenDate);

        activities.forEach(act => {
            const actDateStr = act.date; // e.g., "2026-06-08"

            // String comparison for ISO formatted dates (YYYY-MM-DD)
            const isFrozen = actDateStr < frozenStr;
            const isPast = actDateStr < todayStr;

            const card = document.createElement("div");
            card.className = `timeline-card ${isFrozen ? 'frozen' : ''} timeline-item-card`;
            card.setAttribute("data-date", actDateStr);

            // Formatting for UI (DD/MM/YYYY)
            const [year, month, day] = actDateStr.split('-');
            const formattedDate = `${day}/${month}/${year}`;
            const frozenBadge = isFrozen ? `<span class="badge-frozen"> Locked</span>` : '';

            let html = `
                <div class="timeline-date">
                    <span><i class="far fa-calendar-check"></i> ${formattedDate}</span>
                    ${frozenBadge}
                </div>
                <p class="timeline-desc">${act.description}</p>
            `;

            // Building Actions
            if (!isFrozen) {
                html += `<div class="timeline-actions">`;
                html += `<button class="btn-act btn-edit" data-id="${act.id}"><i class="fas fa-edit"></i> Edit</button>`;

                // Delete is strictly allowed only if it's not in the past
                if (!isPast) {
                    html += `<button class="btn-act btn-delete" data-id="${act.id}"><i class="fas fa-trash-alt"></i> Delete</button>`;
                }
                html += `</div>`;
            }

            card.innerHTML = html;
            activityTimeline.appendChild(card);
        });

        attachTimelineActionListeners();
    }

    /**
     * Attaches events to the dynamically created Edit and Delete buttons.
     */
    function attachTimelineActionListeners() {
        // Edit Event Listeners
        document.querySelectorAll(".btn-edit").forEach(btn => {
            btn.addEventListener("click", (e) => {
                const actId = parseInt(e.currentTarget.getAttribute("data-id"));

                // Prevents HTML breakage from quotes in description
                const activity = currentSlotActivities.find(a => a.id === actId);

                if (activity) {
                    currentEditingActivityId = activity.id;
                    activityDateInput.value = activity.date;
                    activityDescriptionInput.value = activity.description;

                    saveActivityBtn.innerText = "Update Activity";
                    cancelEditBtn.classList.remove("d-none");
                    document.querySelector(".activity-form-section h4").innerHTML = `<i class="fas fa-edit"></i> Edit Entry`;

                    // Unlock for Editing
                    activityDateInput.disabled = true;
                    activityDateInput.setAttribute("title", "Lesson Activity date cannot be modified after creation.");
                    activityDateInput.style.cursor = "not-allowed";

                    activityDescriptionInput.disabled = false;
                    saveActivityBtn.disabled = false;
                    activityFormSection.removeAttribute("title");
                    activityFormSection.classList.remove("form-disabled");

                    const todayStr = getLocalIsoDate(new Date());
                    const isPastActivity = activity.date < todayStr;

                    const alertMessage = isPastActivity
                        ? "You are currently editing a past lesson activity."
                        : "You are currently editing a lesson activity.";

                    // inform the user that they are now editing
                    showModalAlert(alertMessage, "info");

                    activityModal.scrollTo({ top: 0, behavior: 'smooth' });
                }
            });
        });

        // 2-Step Delete Logic
        document.querySelectorAll(".btn-delete").forEach(btn => {
            let isConfirming = false;
            let timeoutId = null;

            btn.addEventListener("click", async (e) => {
                const actId = parseInt(e.currentTarget.getAttribute("data-id"));

                if (!isConfirming) {
                    isConfirming = true;
                    btn.innerHTML = `<i class="fas fa-exclamation-triangle"></i> Sure?`;
                    btn.classList.add("confirming");

                    timeoutId = setTimeout(() => {
                        isConfirming = false;
                        btn.innerHTML = `<i class="fas fa-trash-alt"></i> Delete`;
                        btn.classList.remove("confirming");
                    }, 4000);
                } else {
                    clearTimeout(timeoutId);

                    btn.disabled = true;
                    btn.innerHTML = `<i class="fas fa-spinner fa-spin"></i> ...`;

                    try {
                        await deleteActivityAction(actId);
                    } catch (err) {
                        console.error("Delete failed:", err);
                    } finally {
                        // Reset state just in case of failure
                        isConfirming = false;
                        btn.disabled = false;
                        btn.innerHTML = `<i class="fas fa-trash-alt"></i> Delete`;
                        btn.classList.remove("confirming");
                    }
                }
            });
        });
    }

    /**
     * Submits the form data to either create (POST) or update (PUT) a lesson activity.
     * Separates the logic for a clean UI reset after a successful Post/Update.
     * @returns {Promise<void>}
     */
    async function submitActivity() {
        saveActivityBtn.disabled = true;
        saveActivityBtn.innerText = "Saving...";
        activityAlert.classList.add("d-none");

        const isUpdate = currentEditingActivityId !== null;

        const payload = {
            description: activityDescriptionInput.value.trim()
        };

        // If it is POST (New record), then we only add the date
        if (!isUpdate) {
            payload.date = activityDateInput.value;
        }
        const endpoint = isUpdate
            ? `/api/teachers/me/activities/${currentEditingActivityId}`
            : `/api/teachers/me/slots/${currentSlotId}/activities`;
        const method = isUpdate ? 'PUT' : 'POST';

        try {
            const response = await ApiService.request(endpoint, {
                method: method,
                body: JSON.stringify(payload)
            });

            if (response.status === 201 || response.status === 204) {

                Toast.show(isUpdate ? "Lesson Activity updated successfully!" : "Lesson Activity recorded successfully!", "success");

                // Clear Global IDs
                currentEditingActivityId = null;

                // Visual Reset (Title & Button)
                document.querySelector(".activity-form-section h4").innerHTML = `<i class="fas fa-plus-circle"></i> Add New Lesson Activity`;
                saveActivityBtn.innerText = "Save Lesson Activity";

                // Clearing & Restoring Fields
                activityForm.reset();
                if (currentClickedDate) {
                    activityDateInput.value = currentClickedDate;
                }
                activityDateInput.disabled = false;
                activityDateInput.removeAttribute("title");

                applyFormLocks(true);

                // Timeline refresh
                await fetchActivityTimeline(currentSlotId);

            } else {
                const errorData = await response.json();
                showModalAlert(errorData.message || "Failed to save lesson activity.", "danger");
            }
        } catch (error) {
            Toast.show("Network error. Please try again.", "danger");
        } finally {
            // Resets the button to its active state
            saveActivityBtn.disabled = false;

            saveActivityBtn.innerText = currentEditingActivityId !== null ? "Update Lesson Activity" : "Save Lesson Activity";
        }
    }

    /**
     * Executes the API call to delete a specific lesson activity.
     * @param {number} activityId
     * @returns {Promise<void>}
     */
    async function deleteActivityAction(activityId) {
        try {
            const response = await ApiService.request(`/api/teachers/me/activities/${activityId}`, { method: 'DELETE' });

            if (response.status === 204) {
                Toast.show("Lesson Activity deleted successfully.", "success");
                await fetchActivityTimeline(currentSlotId);
            } else {
                const errorData = await response.json();
                Toast.show(errorData.message || "Failed to delete lesson activity.", "danger");
            }
        } catch (error) {
            Toast.show("Network error during deletion.", "danger");
        }
    }

    /**
     * Resets the form to its default 'Add New' state and re-evaluates all locks.
     * Used primarily for the 'Cancel Edit' action.
     */
    function resetActivityForm() {
        currentEditingActivityId = null;
        activityForm.reset();

        // Safely restore the originally clicked date
        if (currentClickedDate) {
            activityDateInput.value = currentClickedDate;
        }

        cancelEditBtn.classList.add("d-none");
        saveActivityBtn.innerText = "Save Lesson Activity";
        document.querySelector(".activity-form-section h4").innerHTML = `<i class="fas fa-plus-circle"></i> Add New Lesson Activity`;
        activityAlert.classList.add("d-none");

        // Re-evaluation of locks
        applyFormLocks();
    }

    // --- HELPERS ---

    /**
     * Calculates the Monday of the week for a given date in UTC.
     * @param {Date} d
     * @returns {Date}
     */
    function getMonday(d) {
        const dCopy = new Date(d);
        dCopy.setUTCHours(0, 0, 0, 0);
        const day = dCopy.getUTCDay();
        const diff = dCopy.getUTCDate() - day + (day === 0 ? -6 : 1); // adjust when day is sunday
        return new Date(dCopy.setUTCDate(diff));
    }

    /**
     * Safely formats a Date to YYYY-MM-DD using UTC timezone.
     * @param {Date} dateObj
     * @returns {string}
     */
    function getLocalIsoDate(dateObj) {
        const year = dateObj.getUTCFullYear();
        const month = String(dateObj.getUTCMonth() + 1).padStart(2, '0');
        const day = String(dateObj.getUTCDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    }

    /**
     * Checks if the week navigation should be disabled based on Academic Year limits.
     * @param {Date} currentMonday
     * @param {Date} currentSunday
     */
    function checkAcademicYearBounds(currentMonday, currentSunday) {
        const today = new Date();
        let academicYearStartYear = today.getUTCFullYear();

        if (today.getUTCMonth() < 8) { // Before September
            academicYearStartYear--;
        }

        const academicYearStart = new Date(Date.UTC(academicYearStartYear, 8, 1, 0, 0, 0)); // Sep 1st
        const academicYearEnd = new Date(Date.UTC(academicYearStartYear + 1, 7, 31, 23, 59, 59)); // Aug 31st next year

        // Check Previous
        const prevWeekMonday = new Date(currentMonday);
        prevWeekMonday.setUTCDate(currentMonday.getUTCDate() - 7);
        prevWeekBtn.disabled = prevWeekMonday < academicYearStart;

        // Check Next
        const nextWeekSunday = new Date(currentSunday);
        nextWeekSunday.setUTCDate(currentSunday.getUTCDate() + 7);
        nextWeekBtn.disabled = nextWeekSunday > academicYearEnd;
    }

    /**
     * Filters the timeline cards based on the From/To date inputs natively via DOM.
     * Includes an Empty State message if all cards are filtered out.
     */
    function applyTimelineFilter() {
        const fromDate = timelineFilterFrom.value;
        const toDate = timelineFilterTo.value;
        const allCards = document.querySelectorAll("#activityTimeline .timeline-item-card");

        let visibleCount = 0;

        allCards.forEach(card => {
            const cardDate = card.getAttribute("data-date");
            let isVisible = true;

            if (fromDate && cardDate < fromDate) isVisible = false;
            if (toDate && cardDate > toDate) isVisible = false;

            if (isVisible) {
                card.classList.remove("d-none");
                visibleCount++;
            } else {
                card.classList.add("d-none");
            }
        });

        if (visibleCount === 0 && allCards.length > 0) {
            timelineFilterEmptyMsg.classList.remove("d-none");
        } else {
            timelineFilterEmptyMsg.classList.add("d-none");
        }
    }

    /**
     * Re-evaluates and applies form locks based on the current slot's status.
     * Includes a 'gentle' locking mode to prevent visual gray-out after a success.
     * @param {boolean} [isGentleLock=false] - If true, visual form-disabled effects are skipped.
     */
    function applyFormLocks(isGentleLock = false) {
        if (!currentSlot || !currentClickedDate) return;

        const today = new Date();
        const minDate = new Date(today); minDate.setDate(today.getDate() - 14);
        const maxDate = new Date(today); maxDate.setDate(today.getDate() + 30);

        const slotDateObj = new Date(currentClickedDate);
        slotDateObj.setHours(0,0,0,0);
        minDate.setHours(0,0,0,0);
        maxDate.setHours(0,0,0,0);

        activityFormSection.removeAttribute("title");
        activityFormSection.classList.remove("form-disabled");

        if (slotDateObj < minDate || slotDateObj > maxDate) {
            activityFormSection.classList.add("d-none");
            let limitReason = slotDateObj < minDate ? "Historical logs are locked (>14 days)." : "Cannot plan so far ahead (>30 days).";
            showModalAlert(`Adding new lesson activities is disabled for this date. ${limitReason}`, "info");
        } else {
            // Display the form, but check for teacher absences or active students
            activityFormSection.classList.remove("d-none");

            // Canceled due to teacher absence or Empty Class
            if (currentSlot.status === "CANCELLED" || currentSlot.enrolledStudentsCount === 0) {

                // Padlocks (disabled) are always inserted
                activityDateInput.disabled = true;
                activityDescriptionInput.disabled = true;
                saveActivityBtn.disabled = true;

                // Visual Effects (Title & Class) are only included if they are not gentle lock
                if (!isGentleLock) {

                    if (currentSlot.status === "CANCELLED") {
                        activityFormSection.setAttribute("title", `Lesson cancelled: ${currentSlot.cancelReason || 'Teacher Absence'}`);
                        activityFormSection.classList.add("form-disabled");
                        showModalAlert(`This lesson is cancelled (${currentSlot.cancelReason || 'Absence'}). Adding new activities is disabled.`, "warning");

                    } else if (currentSlot.enrolledStudentsCount === 0) {
                        activityFormSection.setAttribute("title", "There are no active students in this class yet.");
                        activityFormSection.classList.add("form-disabled");
                        activityAlert.classList.add("d-none");
                    }
                } else {
                    // In the case of Success, hide any old alerts
                    activityAlert.classList.add("d-none");
                }

            } else {
                // Unlock
                activityDateInput.disabled = false;
                activityDateInput.removeAttribute("title");
                activityDateInput.style.cursor = "";
                activityDescriptionInput.disabled = false;
                saveActivityBtn.disabled = false;
                activityAlert.classList.add("d-none");
            }
        }
    }

    /**
     * Updates the header badge showing the current week's range.
     * @param {TeacherDailyScheduleResponseResource[]} weeklyData
     */
    function updateWeekBadge(weeklyData) {
        if (weeklyData.length > 0) {
            const firstDay = new Date(weeklyData[0].date).toLocaleDateString('en-GB');
            const lastDay = new Date(weeklyData[weeklyData.length - 1].date).toLocaleDateString('en-GB');
            weekRangeText.innerText = `${firstDay} - ${lastDay}`;
        }
    }

    /**
     * Displays an inline alert inside the main grid.
     * @param {string} msg
     */
    function showGridError(msg) {
        scheduleGrid.innerHTML = "";
        const errorEl = document.createElement("div");
        errorEl.className = "grid-error-msg";
        errorEl.innerText = msg;
        scheduleGrid.appendChild(errorEl);
    }

    /**
     * Displays an inline alert inside the activity modal.
     * @param {string} msg
     * @param {string} type - 'danger', 'info', etc.
     */
    function showModalAlert(msg, type) {

        // Default icon for info
        let iconClass = "fa-info-circle";

        if (type === "danger") {
            iconClass = "fa-exclamation-circle";
        } else if (type === "warning") {
            iconClass = "fa-exclamation-triangle";
        } else if (type === "success") {
            iconClass = "fa-check-circle";
        }

        activityAlert.className = `alert alert-${type}`;
        activityAlert.innerHTML = `<i class="fas ${iconClass}"></i> ${msg}`;
        activityAlert.classList.remove("d-none");
    }

});
