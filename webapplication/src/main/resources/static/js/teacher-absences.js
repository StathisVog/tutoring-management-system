/**
 * @typedef {Object} TeacherAbsenceResponseResource
 * @property {number} id
 * @property {string} date
 * @property {string} reason
 * @property {number|null} slotId - The specific slot ID, or null if it's a full-day absence.
 */

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

document.addEventListener("DOMContentLoaded", async () => {

    // --- DOM CACHING ---
    /** @type {HTMLDivElement} */ const absencesGrid = document.getElementById("absencesGrid");

    // Modal Elements
    /** @type {HTMLDivElement} */ const absenceModal = document.getElementById("absenceModal");
    /** @type {HTMLButtonElement} */ const openAbsenceModalBtn = document.getElementById("openAbsenceModalBtn");
    /** @type {HTMLSpanElement} */ const closeAbsenceModalBtn = document.getElementById("closeAbsenceModalBtn");
    /** @type {HTMLHeadingElement} */ const absenceModalTitle = document.getElementById("absenceModalTitle");
    /** @type {HTMLDivElement} */ const absenceAlert = document.getElementById("absenceAlert");
    /** @type {HTMLFormElement} */ const absenceForm = document.getElementById("absenceForm");

    // Form Inputs
    /** @type {HTMLInputElement} */ const absenceDate = document.getElementById("absenceDate");
    /** @type {NodeListOf<HTMLInputElement>} */ const absenceTypeRadios = document.querySelectorAll('input[name="absenceType"]');
    /** @type {HTMLDivElement} */ const specificClassContainer = document.getElementById("specificClassContainer");
    /** @type {HTMLSelectElement} */ const absenceSlotSelect = document.getElementById("absenceSlotSelect");
    /** @type {HTMLTextAreaElement} */ const absenceReason = document.getElementById("absenceReason");
    /** @type {HTMLButtonElement} */ const saveAbsenceBtn = document.getElementById("saveAbsenceBtn");

    // --- STATE ---
    let currentEditingId = null;

    // Cache to prevent hitting the /schedule endpoint multiple times for the same date
    const scheduleCache = {};

    // --- INIT ---
    // Prevent selecting past dates
    absenceDate.min = new Date().toISOString().split("T")[0];
    await fetchAndRenderAbsences();

    // --- EVENT LISTENERS ---

    // Open Modal (Create)
    openAbsenceModalBtn.addEventListener("click", () => {
        resetForm();
        absenceModal.classList.remove("d-none");
    });

    // Close Modal
    const closeModal = () => absenceModal.classList.add("d-none");
    closeAbsenceModalBtn.addEventListener("click", closeModal);
    window.addEventListener("click", (e) => { if (e.target === absenceModal) closeModal(); });

    // Handle Radio Button changes
    absenceTypeRadios.forEach(radio => {
        radio.addEventListener("change", (e) => {
            if (e.target.value === "SPECIFIC_CLASS") {
                specificClassContainer.classList.remove("d-none");
                populateSlotDropdown(absenceDate.value);
            } else {
                specificClassContainer.classList.add("d-none");
            }
        });
    });

    // Handle Date changes (Updates the Dropdown if Specific Class is selected)
    absenceDate.addEventListener("change", (e) => {
        const type = document.querySelector('input[name="absenceType"]:checked').value;
        if (type === "SPECIFIC_CLASS") {
            populateSlotDropdown(e.target.value);
        }
    });

    // Form Submit (POST / PUT)
    absenceForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        await saveAbsence();
    });


    // --- Render Logic ---

    /**
     * Fetches the teacher's declared future and present absences
     * and triggers the UI rendering process.
     * @returns {Promise<void>}
     */
    async function fetchAndRenderAbsences() {
        absencesGrid.innerHTML = `<div class="loading-spinner">Loading your absences...</div>`;
        try {
            const response = await ApiService.request(`/api/teachers/me/absences`, { method: 'GET' });
            if (response.ok) {
                const absences = await response.json();
                await renderAbsenceCards(absences);
            } else {
                showError("Failed to load absences.");
            }
        } catch (error) {
            showError("Network error while loading absences.");
        }
    }

    /**
     * Renders the grid of absence cards. Dynamically maps the slotId of specific
     * class absences to their respective course details using the schedule API.
     * @param {TeacherAbsenceResponseResource[]} absences - The list of absences to render.
     * @returns {Promise<void>}
     */
    async function renderAbsenceCards(absences) {
        absencesGrid.innerHTML = "";

        if (!absences || absences.length === 0) {
            absencesGrid.innerHTML = `
                <div class="grid-empty-msg">
                    <i class="fas fa-umbrella-beach"></i>
                    <p>You have no declared absences.</p>
                </div>`;
            return;
        }

        // Calculate today's date once (ignoring time)
        const today = new Date();
        today.setHours(0, 0, 0, 0);

        for (const abs of absences) {
            const card = document.createElement("div");
            const isFullDay = abs.slotId === null;

            // Check if the absence date is in the past
            const absenceDateObj = new Date(abs.date);
            absenceDateObj.setHours(0, 0, 0, 0);
            const isPastAbsence = absenceDateObj < today;

            card.className = `absence-card ${isFullDay ? 'card-type-full' : 'card-type-specific'} ${isPastAbsence ? 'past-absence' : ''}`;

            // Format date to DD/MM/YYYY
            const [y, m, d] = abs.date.split("-");
            const formattedDate = `${d}/${m}/${y}`;

            // Map slotId to Course Info
            let courseInfoHtml = `<p class="absence-course"><i class="fas fa-calendar-day"></i> All classes cancelled</p>`;
            if (!isFullDay) {
                const scheduleForDay = await fetchScheduleForDate(abs.date);
                const slotDetails = scheduleForDay.find(s => s.slotId === abs.slotId);

                if (slotDetails) {
                    courseInfoHtml = `
                        <p class="absence-course">
                            <i class="fas fa-book"></i> ${slotDetails.courseTitle} <br>
                            <small class="text-muted">
                                <i class="far fa-clock"></i> (${slotDetails.startTime.substring(0,5)} - ${slotDetails.endTime.substring(0,5)}) - 
                                    ${slotDetails.classroom || 'Online'}
                            </small>
                        </p>`;
                } else {
                    courseInfoHtml = `<p class="absence-course"><i class="fas fa-book"></i> Specific Class (ID: ${abs.slotId})</p>`;
                }
            }

            // Build the Footer conditionally based on if the absence is in the past
            let footerHtml = '';
            if (isPastAbsence) {
                footerHtml = `
                    <div class="absence-card-footer footer-logged">
                        <span class="text-muted"><i class="fas fa-history"></i> Logged (Past Absence)</span>
                    </div>
                `;
            } else {
                footerHtml = `
                    <div class="absence-card-footer">
                        <button class="btn-card edit" data-id="${abs.id}" data-full='${JSON.stringify(abs)}'><i class="fas fa-edit"></i> Edit</button>
                        <button class="btn-card delete" data-id="${abs.id}"><i class="fas fa-trash"></i> Delete</button>
                    </div>
                `;
            }

            card.innerHTML = `
                <div class="absence-card-header">
                    <span class="absence-date">${formattedDate}</span>
                    <span class="absence-badge ${isFullDay ? 'badge-full' : 'badge-specific'}">
                        ${isFullDay ? 'Full Day' : 'Specific Class'}
                    </span>
                </div>
                <div class="absence-card-body">
                    ${courseInfoHtml}
                    <p class="absence-reason">"${abs.reason}"</p>
                </div>
                ${footerHtml}
            `;
            absencesGrid.appendChild(card);
        }
        attachCardListeners();
    }

    /**
     * Helper function that fetches the teacher's schedule for a specific date.
     * Utilizes a local cache (`scheduleCache`) to prevent redundant API calls
     * for the same date during mapping or dropdown population.
     * @param {string} dateStr - The date in YYYY-MM-DD format.
     * @returns {Promise<TeacherScheduleSlotResponseResource[]>} An array of schedule slots for that date.
     */
    async function fetchScheduleForDate(dateStr) {
        if (!dateStr) return [];
        if (scheduleCache[dateStr]) return scheduleCache[dateStr];

        try {
            const res = await ApiService.request(`/api/teachers/me/schedule?startDate=${dateStr}&endDate=${dateStr}`, { method: 'GET' });
            if (res.ok) {
                const data = await res.json();
                // data is array of TeacherDailyScheduleResponseResource. If date matches, we grab the slots.
                scheduleCache[dateStr] = data.length > 0 ? data[0].slots : [];
                return scheduleCache[dateStr];
            }
        } catch (e) { console.error("Could not fetch schedule map for", dateStr); }
        return [];
    }

    /**
     * Populates the "Select Class" dropdown in the modal based on the selected date.
     * Enforces the rule: Only classes with active enrolled students (>0) are eligible.
     * @param {string} dateStr - The selected date from the form input.
     * @returns {Promise<void>}
     */
    async function populateSlotDropdown(dateStr) {
        absenceSlotSelect.innerHTML = `<option value="" disabled selected>Loading classes...</option>`;
        saveAbsenceBtn.disabled = true; // Lock until we verify

        if (!dateStr) {
            absenceSlotSelect.innerHTML = `<option value="" disabled selected>Please select a date first...</option>`;
            return;
        }

        const slots = await fetchScheduleForDate(dateStr);
        absenceSlotSelect.innerHTML = "";

        // Only show classes that have enrolled students
        const eligibleSlots = slots.filter(s => s.enrolledStudentsCount > 0);

        if (eligibleSlots.length === 0) {
            absenceSlotSelect.innerHTML = `<option value="" disabled selected>No eligible classes found for this date.</option>`;
            saveAbsenceBtn.disabled = true; // Keep locked to prevent bad requests
        } else {
            absenceSlotSelect.innerHTML = `<option value="" disabled selected>-- Select a Class --</option>`;
            eligibleSlots.forEach(s => {
                const opt = document.createElement("option");
                opt.value = s.slotId.toString();
                opt.innerText = `${s.courseTitle} (${s.startTime.substring(0,5)} - ${s.endTime.substring(0,5)})`;
                absenceSlotSelect.appendChild(opt);
            });
            saveAbsenceBtn.disabled = false; // Unlock
        }
    }

    /**
     * Submits the form data to either create (POST) or update (PUT) an absence.
     * Determines the request type dynamically based on the `currentEditingId` state.
     * @returns {Promise<void>}
     */
    async function saveAbsence() {

        // hide any previous error every time "Save" is clicked
        absenceAlert.classList.add("d-none");

        const type = document.querySelector('input[name="absenceType"]:checked').value;
        const payload = {
            date: absenceDate.value,
            reason: absenceReason.value.trim(),
            slotId: type === "SPECIFIC_CLASS" ? parseInt(absenceSlotSelect.value) : null
        };

        // Basic Validation
        if (type === "SPECIFIC_CLASS" && isNaN(payload.slotId)) {
            showModalAlert("Please select a valid class.", "danger");
            return;
        }

        saveAbsenceBtn.disabled = true;
        saveAbsenceBtn.innerText = "Saving...";

        const isUpdate = currentEditingId !== null;
        const endpoint = isUpdate ? `/api/teachers/me/absences/${currentEditingId}` : `/api/teachers/me/absences`;
        const method = isUpdate ? 'PUT' : 'POST';

        try {
            const response = await ApiService.request(endpoint, {
                method: method,
                body: JSON.stringify(payload)
            });

            if (response.status === 201 || response.status === 204) {
                safeToast("Absence saved successfully!", "success");
                closeModal();
                await fetchAndRenderAbsences();
            } else {
                const errorData = await response.json();
                showModalAlert(errorData.message || "Failed to save absence. Please check your inputs.", "danger");
            }
        } catch (e) {
            safeToast("Network error. Could not connect to the server.", "danger");
        } finally {
            saveAbsenceBtn.disabled = false;
            saveAbsenceBtn.innerText = isUpdate ? "Update Absence" : "Save Absence";
        }
    }

    /**
     * Attaches event listeners for the "Edit" and "Delete" actions on the generated absence cards.
     * Implements a 2-step confirmation logic for deletion.
     */
    function attachCardListeners() {
        // Edit
        document.querySelectorAll(".btn-card.edit").forEach(btn => {
            btn.addEventListener("click", async (e) => {
                const absData = JSON.parse(e.currentTarget.getAttribute("data-full"));
                currentEditingId = absData.id;
                absenceModalTitle.innerText = "Edit Absence";

                absenceDate.value = absData.date;
                absenceReason.value = absData.reason;

                if (absData.slotId !== null) {
                    document.querySelector('input[value="SPECIFIC_CLASS"]').checked = true;
                    specificClassContainer.classList.remove("d-none");
                    await populateSlotDropdown(absData.date);
                    absenceSlotSelect.value = absData.slotId;
                } else {
                    document.querySelector('input[value="FULL_DAY"]').checked = true;
                    specificClassContainer.classList.add("d-none");
                }
                absenceModal.classList.remove("d-none");
            });
        });

        // Delete (2-Step)
        document.querySelectorAll(".btn-card.delete").forEach(btn => {
            let isConfirming = false;
            let timeoutId = null;

            btn.addEventListener("click", async (e) => {
                const id = e.currentTarget.getAttribute("data-id");

                if (!isConfirming) {
                    isConfirming = true;
                    btn.innerHTML = `<i class="fas fa-exclamation-triangle"></i> Sure?`;
                    btn.classList.add("confirming");
                    timeoutId = setTimeout(() => {
                        isConfirming = false;
                        btn.innerHTML = `<i class="fas fa-trash"></i> Delete`;
                        btn.classList.remove("confirming");
                    }, 3000);
                } else {
                    clearTimeout(timeoutId);
                    btn.disabled = true;
                    btn.innerHTML = `<i class="fas fa-spinner fa-spin"></i> ...`;
                    try {
                        const res = await ApiService.request(`/api/teachers/me/absences/${id}`, { method: 'DELETE' });
                        if (res.status === 204) {
                            safeToast("Absence deleted.", "success");
                            await fetchAndRenderAbsences();
                        } else {
                            const err = await res.json();
                            safeToast(err.message || "Failed to delete.", "danger");
                            btn.disabled = false;
                        }
                    } catch (err) {
                        safeToast("Network error.", "danger");
                        btn.disabled = false;
                    }
                }
            });
        });
    }

    /**
     * Resets the modal form to its default clean state (Full-Day absence selected, no dates/reasons).
     * Clears any active editing IDs.
     */
    function resetForm() {
        currentEditingId = null;
        absenceForm.reset();
        absenceModalTitle.innerText = "Declare Absence";
        document.querySelector('input[value="FULL_DAY"]').checked = true;
        specificClassContainer.classList.add("d-none");
        saveAbsenceBtn.disabled = false;
        absenceAlert.classList.add("d-none");
    }

    /**
     * Displays a styled error message within the main absences grid.
     * @param {string} msg - The error message to display.
     */
    function showError(msg) {
        absencesGrid.innerHTML = `
            <div class="grid-empty-msg error-state">
                <i class="fas fa-exclamation-circle"></i>
                <p>${msg}</p>
            </div>
        `;
    }

    /**
     * Displays an inline alert inside the absence modal.
     * @param {string} msg
     * @param {string} type - 'danger', 'info', 'warning', 'success'
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

        absenceAlert.className = `alert alert-${type}`;
        absenceAlert.innerHTML = `<i class="fas ${iconClass}"></i> ${msg}`;
        absenceAlert.classList.remove("d-none");
    }

    /**
     * Safely triggers a UI Toast notification. Falls back to a standard browser alert
     * if the Toast library is not loaded.
     * @param {string} msg - The message to display.
     * @param {string} [type='info'] - The severity/type of the toast (e.g., 'success', 'danger').
     */
    function safeToast(msg, type = 'info') {
        if (typeof Toast !== 'undefined' && Toast.show)
            Toast.show(msg, type);
        else alert(msg);
    }

});
