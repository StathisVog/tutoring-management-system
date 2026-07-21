/**
 * @typedef {Object} AdminTeacherAbsenceResponseResource
 * @property {number} absenceId
 * @property {number} teacherId
 * @property {string} teacherFullName
 * @property {string} date
 * @property {string} reason
 * @property {number|null} slotId
 * @property {string|null} courseTitle
 */

/**
 * @typedef {Object} AdminLessonActivityResponseResource
 * @property {number} slotId
 * @property {number} courseId
 * @property {string} courseTitle
 * @property {number} teacherId
 * @property {string} teacherFullName
 * @property {number} activityId
 * @property {string} date
 * @property {string} description
 */

/**
 * @typedef {Object} TeacherResource
 * @property {number} id
 * @property {string} fullName
 */

/**
 * @typedef {Object} CourseResource
 * @property {number} id
 * @property {string} title
 */

/**
 * @typedef {Object} ScheduledSlotResource
 * @property {number} id
 * @property {number} teacherId
 * @property {number} courseId
 * @property {string} courseTitle
 * @property {"MONDAY" | "TUESDAY" | "WEDNESDAY" | "THURSDAY" | "FRIDAY" | "SATURDAY" | "SUNDAY"} dayOfWeek
 * @property {string} startTime
 * @property {string} endTime
 */

/**
 * @typedef {Object} AdminTestResultResponseResource
 * @property {number} testResultId
 * @property {number} studentId
 * @property {string} studentFullName
 * @property {number|null} grade
 * @property {string|null} comments
 */

/**
 * @typedef {Object} AdminTestResponseResource
 * @property {number} testId
 * @property {number} courseId
 * @property {string} courseTitle
 * @property {number} teacherId
 * @property {string} teacherFullName
 * @property {string} date
 * @property {string} description
 * @property {AdminTestResultResponseResource[]} results
 */

document.addEventListener("DOMContentLoaded", () => {

    // --- DOM ELEMENTS CACHING ---

    // Tabs
    /** @type {HTMLButtonElement} */ const tabBtnAbsences = document.getElementById("tabBtnAbsences");
    /** @type {HTMLButtonElement} */ const tabBtnActivities = document.getElementById("tabBtnActivities");
    /** @type {HTMLButtonElement} */ const tabBtnTests = document.getElementById("tabBtnTests");

    /** @type {HTMLDivElement} */ const paneAbsences = document.getElementById("paneAbsences");
    /** @type {HTMLDivElement} */ const paneActivities = document.getElementById("paneActivities");
    /** @type {HTMLDivElement} */ const paneTests = document.getElementById("paneTests");

    // --- Teacher Absences Elements ---
    /** @type {HTMLDivElement} */ const absencesLoading = document.getElementById("absencesLoading");
    /** @type {HTMLDivElement} */ const absencesEmpty = document.getElementById("absencesEmpty");
    /** @type {HTMLDivElement} */ const absencesTableContainer = document.getElementById("absencesTableContainer");
    /** @type {HTMLTableSectionElement} */ const absencesTableBody = document.getElementById("absencesTableBody");

    /** @type {HTMLSelectElement} */ const filterTeacher = document.getElementById("filterTeacher");
    /** @type {HTMLSelectElement} */ const filterSlot = document.getElementById("filterSlot");
    /** @type {HTMLInputElement} */ const filterStartDate = document.getElementById("filterStartDate");
    /** @type {HTMLInputElement} */ const filterEndDate = document.getElementById("filterEndDate");
    /** @type {HTMLButtonElement} */ const searchAbsencesBtn = document.getElementById("searchAbsencesBtn");
    /** @type {HTMLButtonElement} */ const clearFiltersBtn = document.getElementById("clearFiltersBtn");

    /** @type {HTMLDivElement} */ const editAbsenceModal = document.getElementById("editAbsenceModal");
    /** @type {HTMLFormElement} */ const editAbsenceForm = document.getElementById("editAbsenceForm");
    /** @type {HTMLDivElement} */ const editAbsenceAlert = document.getElementById("editAbsenceAlert");
    /** @type {HTMLInputElement} */ const editAbsenceId = document.getElementById("editAbsenceId");
    /** @type {HTMLInputElement} */ const editAbsenceTeacher = document.getElementById("editAbsenceTeacher");
    /** @type {HTMLInputElement} */ const editAbsenceDate = document.getElementById("editAbsenceDate");
    /** @type {HTMLTextAreaElement} */ const editAbsenceReason = document.getElementById("editAbsenceReason");
    /** @type {HTMLInputElement} */ const editAbsenceIsFullDay = document.getElementById("editAbsenceIsFullDay");
    /** @type {HTMLSelectElement} */ const editAbsenceSlotId = document.getElementById("editAbsenceSlotId");
    /** @type {HTMLDivElement} */ const slotInputContainer = document.getElementById("slotInputContainer");
    /** @type {HTMLButtonElement} */ const closeAbsenceModalBtn = document.getElementById("closeAbsenceModalBtn");
    /** @type {HTMLButtonElement} */ const cancelAbsenceBtn = document.getElementById("cancelAbsenceBtn");
    /** @type {HTMLButtonElement} */ const saveAbsenceBtn = document.getElementById("saveAbsenceBtn");

    // --- Lesson Activities Elements ---
    /** @type {HTMLDivElement} */ const activitiesLoading = document.getElementById("activitiesLoading");
    /** @type {HTMLDivElement} */ const activitiesEmpty = document.getElementById("activitiesEmpty");
    /** @type {HTMLDivElement} */ const activitiesTableContainer = document.getElementById("activitiesTableContainer");
    /** @type {HTMLTableSectionElement} */ const activitiesTableBody = document.getElementById("activitiesTableBody");

    /** @type {HTMLSelectElement} */ const filterActivityCourse = document.getElementById("filterActivityCourse");
    /** @type {HTMLSelectElement} */ const filterActivityTeacher = document.getElementById("filterActivityTeacher");
    /** @type {HTMLSelectElement} */ const filterActivitySlot = document.getElementById("filterActivitySlot");
    /** @type {HTMLInputElement} */ const filterActivityStartDate = document.getElementById("filterActivityStartDate");
    /** @type {HTMLInputElement} */ const filterActivityEndDate = document.getElementById("filterActivityEndDate");
    /** @type {HTMLButtonElement} */ const searchActivitiesBtn = document.getElementById("searchActivitiesBtn");
    /** @type {HTMLButtonElement} */ const clearActivityFiltersBtn = document.getElementById("clearActivityFiltersBtn");

    /** @type {HTMLDivElement} */ const editActivityModal = document.getElementById("editActivityModal");
    /** @type {HTMLFormElement} */ const editActivityForm = document.getElementById("editActivityForm");
    /** @type {HTMLDivElement} */ const editActivityAlert = document.getElementById("editActivityAlert");
    /** @type {HTMLInputElement} */ const editActivityId = document.getElementById("editActivityId");
    /** @type {HTMLInputElement} */ const editActivityCourse = document.getElementById("editActivityCourse");
    /** @type {HTMLInputElement} */ const editActivityTeacher = document.getElementById("editActivityTeacher");
    /** @type {HTMLInputElement} */ const editActivityDate = document.getElementById("editActivityDate");
    /** @type {HTMLTextAreaElement} */ const editActivityDescription = document.getElementById("editActivityDescription");
    /** @type {HTMLSpanElement} */ const closeActivityModalBtn = document.getElementById("closeActivityModalBtn");
    /** @type {HTMLButtonElement} */ const cancelActivityBtn = document.getElementById("cancelActivityBtn");
    /** @type {HTMLButtonElement} */ const saveActivityBtn = document.getElementById("saveActivityBtn");

    // --- Tests & Test Results Elements ---
    /** @type {HTMLDivElement} */ const testsLoading = document.getElementById("testsLoading");
    /** @type {HTMLDivElement} */ const testsEmpty = document.getElementById("testsEmpty");
    /** @type {HTMLDivElement} */ const testsTableContainer = document.getElementById("testsTableContainer");
    /** @type {HTMLTableSectionElement} */ const testsTableBody = document.getElementById("testsTableBody");

    /** @type {HTMLSelectElement} */ const filterTestCourse = document.getElementById("filterTestCourse");
    /** @type {HTMLSelectElement} */ const filterTestTeacher = document.getElementById("filterTestTeacher");
    /** @type {HTMLInputElement} */ const filterTestStartDate = document.getElementById("filterTestStartDate");
    /** @type {HTMLInputElement} */ const filterTestEndDate = document.getElementById("filterTestEndDate");
    /** @type {HTMLButtonElement} */ const searchTestsBtn = document.getElementById("searchTestsBtn");
    /** @type {HTMLButtonElement} */ const clearTestFiltersBtn = document.getElementById("clearTestFiltersBtn");

    /** @type {HTMLDivElement} */ const editTestModal = document.getElementById("editTestModal");
    /** @type {HTMLFormElement} */ const editTestForm = document.getElementById("editTestForm");
    /** @type {HTMLDivElement} */ const editTestAlert = document.getElementById("editTestAlert");
    /** @type {HTMLInputElement} */ const editTestId = document.getElementById("editTestId");
    /** @type {HTMLInputElement} */ const editTestCourse = document.getElementById("editTestCourse");
    /** @type {HTMLInputElement} */ const editTestTeacher = document.getElementById("editTestTeacher");
    /** @type {HTMLInputElement} */ const editTestDate = document.getElementById("editTestDate");
    /** @type {HTMLInputElement} */ const editTestDescription = document.getElementById("editTestDescription");
    /** @type {HTMLTableSectionElement} */ const editTestResultsBody = document.getElementById("editTestResultsBody");
    /** @type {HTMLSpanElement} */ const closeTestModalBtn = document.getElementById("closeTestModalBtn");
    /** @type {HTMLButtonElement} */ const cancelTestBtn = document.getElementById("cancelTestBtn");
    /** @type {HTMLButtonElement} */ const saveTestBtn = document.getElementById("saveTestBtn");

    // --- GLOBAL STATE & INIT ---

    /** @type {TeacherResource[]} */ let allAuditTeachers = [];
    /** @type {CourseResource[]} */ let allAuditCourses = [];
    /** @type {ScheduledSlotResource[]} */ let allAuditSlots = [];

    // Track if activities have been loaded once to avoid redundant fetches
    let activitiesLoaded = false;
    let testsLoaded = false;

    initAuditPanels().catch(e => console.error("Initialization error:", e));

    /**
     * Initializes the audit panels by fetching reference data (Teachers, Courses, Slots)
     * and loading the first tab (Absences).
     */
    async function initAuditPanels() {

        // network requests concurrently to maximize performance
        const teachersPromise = ApiService.request('/api/admin/users?role=TEACHER', { method: 'GET' });
        const coursesPromise = ApiService.request('/api/courses', { method: 'GET' });
        const slotsPromise = ApiService.request('/api/slots', { method: 'GET' });

        // Fetch absences on initial load
        const absencesPromise = fetchAbsences();

        // Await them individually
        const teachersRes = await teachersPromise;
        const coursesRes = await coursesPromise;
        const slotsRes = await slotsPromise;
        await absencesPromise;

        if (teachersRes.ok && coursesRes.ok && slotsRes.ok) {
            allAuditTeachers = await teachersRes.json();
            allAuditCourses = await coursesRes.json();
            allAuditSlots = await slotsRes.json();

            populateAbsenceFilters();
            populateActivityFilters();
        }
    }

    // --- COMMON TAB NAVIGATION ---

    tabBtnAbsences.addEventListener("click", () => switchTab("ABSENCES"));
    tabBtnActivities.addEventListener("click", () => switchTab("ACTIVITIES"));
    tabBtnTests.addEventListener("click", () => switchTab("TESTS"));

    /**
     * Switches the active pane and highlights the corresponding tab button.
     * @param {string} tabName - The target tab identifier (e.g., "ABSENCES", "ACTIVITIES", "TESTS")
     */
    function switchTab(tabName) {
        [tabBtnAbsences, tabBtnActivities, tabBtnTests].forEach(btn => btn.classList.remove("active"));
        [paneAbsences, paneActivities, paneTests].forEach(pane => pane.classList.remove("active"));

        if (tabName === "ABSENCES") {
            tabBtnAbsences.classList.add("active");
            paneAbsences.classList.add("active");
        } else if (tabName === "ACTIVITIES") {
            tabBtnActivities.classList.add("active");
            paneActivities.classList.add("active");
            // Lazy load activities on first visit
            if (!activitiesLoaded) {
                fetchActivities().then(() => activitiesLoaded = true);
            }
        } else {
            tabBtnTests.classList.add("active");
            paneTests.classList.add("active");
            // Lazy load tests on first visit
            if (!testsLoaded) {
                populateTestFilters();
                fetchTests().then(() => testsLoaded = true);
            }
        }
    }

    // --- TEACHER ABSENCES LOGIC ---

    /**
     * Populates the teacher filter dropdown with data fetched from the API.
     */
    function populateAbsenceFilters() {
        filterTeacher.options.length = 1;
        allAuditTeachers.forEach(t => {
            const opt = document.createElement("option");
            opt.value = t.id.toString();
            opt.innerText = t.fullName;
            filterTeacher.appendChild(opt);
        });
    }

    // Cascading Dropdown: When teacher changes, bring his Classes
    filterTeacher.addEventListener("change", () => {
        const selectedTeacherId = parseInt(filterTeacher.value, 10);
        if (!selectedTeacherId) {
            filterSlot.innerHTML = `<option value="">Select a teacher first...</option>`;
            filterSlot.disabled = true;
            return;
        }

        const teacherSlots = allAuditSlots.filter(s => s.teacherId === selectedTeacherId);
        filterSlot.options.length = 1; // DRY: Keep placeholder

        if (teacherSlots.length === 0) {
            filterSlot.innerHTML = `<option value="">No Scheduled Classes found</option>`;
            filterSlot.disabled = true;
        } else {
            appendFormattedSlotOptions(filterSlot, teacherSlots);
            filterSlot.disabled = false;
        }
    });

    // Filter Buttons (Awaiting the promises)
    searchAbsencesBtn.addEventListener("click", async () => await fetchAbsences());

    clearFiltersBtn.addEventListener("click", async () => {
        filterTeacher.value = "";
        filterSlot.innerHTML = `<option value="">Select a teacher first...</option>`;
        filterSlot.disabled = true;
        filterStartDate.value = "";
        filterEndDate.value = "";
        await fetchAbsences();
    });

    editAbsenceIsFullDay.addEventListener("change", (e) => {
        if (e.target.checked) {
            slotInputContainer.classList.add("init-hidden");
            editAbsenceSlotId.required = false;
            editAbsenceSlotId.value = "";
        } else {
            slotInputContainer.classList.remove("init-hidden");
            editAbsenceSlotId.required = true;
        }
    });

    closeAbsenceModalBtn.addEventListener("click", closeEditAbsenceModal);
    cancelAbsenceBtn.addEventListener("click", closeEditAbsenceModal);

    /**
     * Closes the edit absence modal, resets its form fields, and hides any active alerts.
     */
    function closeEditAbsenceModal() {
        editAbsenceModal.classList.add("init-hidden");
        editAbsenceForm.reset();
        editAbsenceAlert.style.display = "none";
    }

    /**
     * Fetches teacher absences based on the current UI filter criteria and renders the board.
     * @returns {Promise<void>}
     */
    async function fetchAbsences() {
        absencesLoading.classList.remove("init-hidden");
        absencesEmpty.classList.add("init-hidden");
        absencesTableContainer.classList.add("init-hidden");
        absencesTableBody.innerHTML = "";

        try {

            // Building Query Parameters based on UI Filters
            const queryParams = new URLSearchParams();
            if (filterTeacher.value) queryParams.append('teacherId', filterTeacher.value);
            if (filterSlot.value && !filterSlot.disabled) queryParams.append('slotId', filterSlot.value);
            if (filterStartDate.value) queryParams.append('startDate', filterStartDate.value);
            if (filterEndDate.value) queryParams.append('endDate', filterEndDate.value);

            const queryString = queryParams.toString() ? `?${queryParams.toString()}` : "";
            const response = await ApiService.request(`/api/admin/absences${queryString}`, { method: 'GET' });

            /** @type {AdminTeacherAbsenceResponseResource[]} */
            const absences = await response.json();

            absencesLoading.classList.add("init-hidden");

            if (!absences || absences.length === 0) {
                absencesEmpty.classList.remove("init-hidden");
            } else {
                renderAbsencesTable(absences);
                absencesTableContainer.classList.remove("init-hidden");
            }
        } catch (error) {
            console.error("Error fetching absences:", error);
            absencesLoading.classList.add("init-hidden");
            Toast.show("Failed to load teacher absences.", "danger");
        }
    }

    /**
     * Renders the data table rows based on the API response.
     * @param {AdminTeacherAbsenceResponseResource[]} absences
     */
    function renderAbsencesTable(absences) {
        absences.forEach(abs => {
            const row = document.createElement("tr");

            // Hybrid Scope Presentation
            const scopeHtml = abs.slotId === null
                ? `<span class="scope-badge scope-full-day"><i class="fas fa-calendar-day"></i> Full Day</span>`
                : `<span class="scope-badge scope-slot"><i class="fas fa-chalkboard"></i> ${abs.courseTitle}</span>`;

            // Set the time to 12 noon so that the
            // Browser's Timezone doesn't change the day.
            const dateObj = new Date(`${abs.date}T12:00:00`);
            const dayName = dateObj.toLocaleDateString('en-US', { weekday: 'long' });

            row.innerHTML = `
                <td class="text-nowrap"><i class="fas fa-chalkboard-teacher text-muted"></i> ${abs.teacherFullName}</td>
                <td><strong>${dayName}</strong></td> 
                <td class="text-nowrap"><i class="far fa-calendar-alt text-muted"></i> ${abs.date}</td>
                <td>${scopeHtml}</td>
                <td>${abs.reason}</td>
                <td class="text-right">
                    <div class="actions-flex-container">
                        <button class="btn-icon btn-icon-edit" title="Edit Override"><i class="fas fa-pen"></i></button>
                        <div class="delete-btn-placeholder">
                            <button class="btn-icon btn-icon-delete" title="Delete Absence"><i class="fas fa-trash"></i></button>
                        </div>
                    </div>
                </td>
            `;

            row.querySelector('.btn-icon-edit').addEventListener("click", () => openEditAbsenceModal(abs));

            const deleteBtn = row.querySelector('.btn-icon-delete');
            let timerId = null;

            deleteBtn.addEventListener("click", async () => {
                if (deleteBtn.classList.contains("btn-sure-state")) {
                    clearTimeout(timerId);
                    deleteBtn.innerHTML = '<i class="fas fa-trash"></i>';
                    deleteBtn.classList.remove("btn-sure-state");
                    await executeDeleteAbsence(abs.absenceId, deleteBtn);
                } else {
                    deleteBtn.innerHTML = '<i class="fas fa-question-circle"></i> Sure?';
                    deleteBtn.classList.add("btn-sure-state");
                    timerId = setTimeout(() => {
                        deleteBtn.innerHTML = '<i class="fas fa-trash"></i>';
                        deleteBtn.classList.remove("btn-sure-state");
                    }, 4000);
                }
            });

            absencesTableBody.appendChild(row);
        });
    }

    /**
     * Populates and opens the Edit Modal applying hybrid logic rules.
     * @param {AdminTeacherAbsenceResponseResource} abs
     */
    function openEditAbsenceModal(abs) {
        editAbsenceId.value = abs.absenceId.toString();
        editAbsenceTeacher.value = abs.teacherFullName;
        editAbsenceDate.value = abs.date;
        editAbsenceReason.value = abs.reason;

        // Filtering Classes for a specific teacher
        const teacherSlots = allAuditSlots.filter(s => s.teacherId === abs.teacherId);

        // Filling the Dropdown
        editAbsenceSlotId.options.length = 1;
        appendFormattedSlotOptions(editAbsenceSlotId, teacherSlots);

        // Apply Hybrid Logic to inputs
        if (abs.slotId === null) {
            editAbsenceIsFullDay.checked = true;
            slotInputContainer.classList.add("init-hidden");
            editAbsenceSlotId.required = false;
        } else {
            editAbsenceIsFullDay.checked = false;
            slotInputContainer.classList.remove("init-hidden");
            editAbsenceSlotId.required = true;
            editAbsenceSlotId.value = abs.slotId.toString();
        }

        editAbsenceModal.classList.remove("init-hidden");
    }

    editAbsenceForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const absenceId = editAbsenceId.value;

        // Prepare Request Body
        const requestBody = {
            date: editAbsenceDate.value,
            reason: editAbsenceReason.value,
            slotId: editAbsenceIsFullDay.checked ? null : parseInt(editAbsenceSlotId.value, 10)
        };

        const originalBtnText = saveAbsenceBtn.innerHTML;
        saveAbsenceBtn.disabled = true;
        saveAbsenceBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving...';

        // hide any old error messages
        editAbsenceAlert.style.display = "none";

        try {
            const response = await ApiService.request(`/api/admin/absences/${absenceId}`, {
                method: 'PUT', body: JSON.stringify(requestBody)
            });

            if (response.status === 204) {
                closeEditAbsenceModal();
                await fetchAbsences(); // Refresh Table
            } else {

                // read the Exception from the Backend
                const errorData = await response.json();
                showModalAlert(editAbsenceAlert, errorData.message || 'Validation error.', 'danger');
            }
        } catch (error) {
            showModalAlert(editAbsenceAlert, "Network error occurred.", "danger");
        } finally {
            saveAbsenceBtn.disabled = false;
            saveAbsenceBtn.innerHTML = originalBtnText;
        }
    });

    /**
     * Executes the actual DELETE API call and updates the UI accordingly.
     * @param {number} absenceId - The unique identifier of the absence record to delete.
     * @param {HTMLButtonElement} btn - The button element that triggered the action.
     * @returns {Promise<void>}
     */
    async function executeDeleteAbsence(absenceId, btn) {
        btn.disabled = true;
        btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';

        try {
            const response = await ApiService.request(`/api/admin/absences/${absenceId}`, { method: 'DELETE' });

            if (response.status === 204) await fetchAbsences(); // Refresh List
            else {
                const error = await response.json();
                Toast.show(`Delete Failed: ${error.message || 'Unknown error'}`, "danger");
                btn.disabled = false;
                btn.innerHTML = '<i class="fas fa-trash"></i>';
            }
        } catch (error) {
            Toast.show("Network error occurred.", "danger");
            btn.disabled = false;
            btn.innerHTML = '<i class="fas fa-trash"></i>';
        }
    }


    // --- LESSON ACTIVITIES LOGIC ---

    /**
     * Populates the Course and Teacher filter dropdowns in the Activities tab
     * with data fetched during the initial application load.
     */
    function populateActivityFilters() {
        // Fill Courses
        filterActivityCourse.options.length = 1;
        allAuditCourses.forEach(c => {
            const opt = document.createElement("option");
            opt.value = c.id.toString();
            opt.innerText = c.title;
            filterActivityCourse.appendChild(opt);
        });

        // Fill Teachers
        filterActivityTeacher.options.length = 1;
        allAuditTeachers.forEach(t => {
            const opt = document.createElement("option");
            opt.value = t.id.toString();
            opt.innerText = t.fullName;
            filterActivityTeacher.appendChild(opt);
        });
    }

    // Cascade Logic: Filter slots based on selected Teacher AND Course
    /**
     * Dynamically updates the Scheduled Class (Slot) dropdown in the Activities tab
     * based on the currently selected Course and/or Teacher.
     * Handles enabling, disabling, and empty states of the dropdown.
     */
    function updateActivitySlotDropdown() {
        const teacherId = parseInt(filterActivityTeacher.value, 10);
        const courseId = parseInt(filterActivityCourse.value, 10);

        if (!teacherId && !courseId) {
            filterActivitySlot.innerHTML = `<option value="">Select a course or teacher first...</option>`;
            filterActivitySlot.disabled = true;
            return;
        }

        // Filter slots from global reference data
        let availableSlots = allAuditSlots;
        if (teacherId) availableSlots = availableSlots.filter(s => s.teacherId === teacherId);
        if (courseId) availableSlots = availableSlots.filter(s => s.courseId === courseId);

        filterActivitySlot.options.length = 1; // Keep placeholder

        if (availableSlots.length === 0) {
            filterActivitySlot.innerHTML = `<option value="">No Classes match criteria</option>`;
            filterActivitySlot.disabled = true;
        } else {
            filterActivitySlot.innerHTML = `<option value="">-- All Matching Classes --</option>`;

            // Call with true to include the teacher's name next to the course
            appendFormattedSlotOptions(filterActivitySlot, availableSlots, true);
            filterActivitySlot.disabled = false;
        }
    }

    filterActivityCourse.addEventListener("change", updateActivitySlotDropdown);
    filterActivityTeacher.addEventListener("change", updateActivitySlotDropdown);

    searchActivitiesBtn.addEventListener("click", async () => await fetchActivities());

    clearActivityFiltersBtn.addEventListener("click", async () => {
        filterActivityCourse.value = "";
        filterActivityTeacher.value = "";
        filterActivitySlot.innerHTML = `<option value="">Select a course or teacher first...</option>`;
        filterActivitySlot.disabled = true;
        filterActivityStartDate.value = "";
        filterActivityEndDate.value = "";
        await fetchActivities();
    });

    closeActivityModalBtn.addEventListener("click", closeEditActivityModal);
    cancelActivityBtn.addEventListener("click", closeEditActivityModal);

    /**
     * Closes the edit activity modal, resets its form fields, and hides any active alerts.
     */
    function closeEditActivityModal() {
        editActivityModal.classList.add("init-hidden");
        editActivityForm.reset();
        editActivityAlert.style.display = "none";
    }

    /**
     * Fetches lesson activities from the backend API based on the currently selected
     * UI filters and renders the results into the Activities data table.
     * @returns {Promise<void>}
     */
    async function fetchActivities() {
        activitiesLoading.classList.remove("init-hidden");
        activitiesEmpty.classList.add("init-hidden");
        activitiesTableContainer.classList.add("init-hidden");
        activitiesTableBody.innerHTML = "";

        try {
            const params = new URLSearchParams();
            if (filterActivityCourse.value) params.append('courseId', filterActivityCourse.value);
            if (filterActivityTeacher.value) params.append('teacherId', filterActivityTeacher.value);
            if (filterActivitySlot.value && !filterActivitySlot.disabled) params.append('slotId', filterActivitySlot.value);
            if (filterActivityStartDate.value) params.append('startDate', filterActivityStartDate.value);
            if (filterActivityEndDate.value) params.append('endDate', filterActivityEndDate.value);

            const qString = params.toString() ? `?${params.toString()}` : "";
            const response = await ApiService.request(`/api/admin/activities${qString}`, { method: 'GET' });

            /** @type {AdminLessonActivityResponseResource[]} */
            const activities = await response.json();

            activitiesLoading.classList.add("init-hidden");

            if (!activities || activities.length === 0) {
                activitiesEmpty.classList.remove("init-hidden");
            } else {
                renderActivitiesTable(activities);
                activitiesTableContainer.classList.remove("init-hidden");
            }
        } catch (error) {
            console.error("Error fetching activities:", error);
            activitiesLoading.classList.add("init-hidden");
            Toast.show("Failed to load lesson activities.", "danger");
        }
    }

    /**
     * Renders the fetched activities into the data table and attaches event listeners
     * for the edit and delete (with Inline Confirmation pattern) actions.
     * @param {AdminLessonActivityResponseResource[]} activities - The array of lesson activities to render.
     */
    function renderActivitiesTable(activities) {
        activities.forEach(act => {
            const row = document.createElement("tr");

            const dateObj = new Date(`${act.date}T12:00:00`);
            const dayName = dateObj.toLocaleDateString('en-US', { weekday: 'long' });

            row.innerHTML = `
                <td><span class="scope-badge scope-slot"><i class="fas fa-book"></i> ${act.courseTitle}</span></td>
                <td class="text-nowrap"><i class="fas fa-chalkboard-teacher text-muted"></i> ${act.teacherFullName}</td>
                <td><strong>${dayName}</strong></td> 
                <td class="text-nowrap"><i class="far fa-calendar-alt text-muted"></i> ${act.date}</td>
                <td>${act.description}</td>
                <td class="text-right">
                    <div class="actions-flex-container">
                        <button class="btn-icon btn-icon-edit" title="Edit Override"><i class="fas fa-pen"></i></button>
                        <div class="delete-btn-placeholder">
                            <button class="btn-icon btn-icon-delete" title="Delete Activity"><i class="fas fa-trash"></i></button>
                        </div>
                    </div>
                </td>
            `;

            row.querySelector('.btn-icon-edit').addEventListener("click", () => openEditActivityModal(act));

            const deleteBtn = row.querySelector('.btn-icon-delete');
            let timerId = null;
            deleteBtn.addEventListener("click", async () => {
                if (deleteBtn.classList.contains("btn-sure-state")) {
                    clearTimeout(timerId);
                    deleteBtn.innerHTML = '<i class="fas fa-trash"></i>';
                    deleteBtn.classList.remove("btn-sure-state");
                    await executeDeleteActivity(act.activityId, deleteBtn);
                } else {
                    deleteBtn.innerHTML = '<i class="fas fa-question-circle"></i> Sure?';
                    deleteBtn.classList.add("btn-sure-state");
                    timerId = setTimeout(() => {
                        deleteBtn.innerHTML = '<i class="fas fa-trash"></i>';
                        deleteBtn.classList.remove("btn-sure-state");
                    }, 4000);
                }
            });

            activitiesTableBody.appendChild(row);
        });
    }

    /**
     * Populates the Activity Edit Modal with the selected activity's data and displays it.
     * @param {AdminLessonActivityResponseResource} act - The lesson activity object to edit.
     */
    function openEditActivityModal(act) {
        editActivityId.value = act.activityId.toString();
        editActivityCourse.value = act.courseTitle;
        editActivityTeacher.value = act.teacherFullName;
        editActivityDate.value = act.date;
        editActivityDescription.value = act.description;

        editActivityModal.classList.remove("init-hidden");
    }

    editActivityForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const activityId = editActivityId.value;
        const requestBody = {
            date: editActivityDate.value,
            description: editActivityDescription.value
        };

        const originalBtnText = saveActivityBtn.innerHTML;
        saveActivityBtn.disabled = true;
        saveActivityBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving...';
        editActivityAlert.style.display = "none";

        try {
            const response = await ApiService.request(`/api/admin/activities/${activityId}`, {
                method: 'PUT', body: JSON.stringify(requestBody)
            });
            if (response.status === 204) {
                closeEditActivityModal();
                await fetchActivities();
            } else {
                const errorData = await response.json();
                showModalAlert(editActivityAlert, errorData.message || 'Validation error.', 'danger');
            }
        } catch (error) {
            showModalAlert(editActivityAlert, "Network error occurred.", "danger");
        } finally {
            saveActivityBtn.disabled = false;
            saveActivityBtn.innerHTML = originalBtnText;
        }
    });

    /**
     * Executes the API request to forcefully delete a lesson activity and refreshes the table upon success.
     * @param {number} activityId - The unique identifier of the lesson activity to delete.
     * @param {HTMLButtonElement} btn - The delete button element, used to manage UI loading states.
     * @returns {Promise<void>}
     */
    async function executeDeleteActivity(activityId, btn) {
        btn.disabled = true;
        btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';
        try {
            const response = await ApiService.request(`/api/admin/activities/${activityId}`, { method: 'DELETE' });
            if (response.status === 204) await fetchActivities();
            else {
                const error = await response.json();
                Toast.show(`Delete Failed: ${error.message || 'Unknown error'}`, "danger");
                btn.disabled = false; btn.innerHTML = '<i class="fas fa-trash"></i>';
            }
        } catch (error) {
            Toast.show("Network error occurred.", "danger");
            btn.disabled = false; btn.innerHTML = '<i class="fas fa-trash"></i>';
        }
    }

    // --- TESTS & TEST RESULT LOGIC ---

    /**
     * Populates the Course and Teacher filter dropdowns in the Tests tab
     * with reference data fetched during the initial application load.
     */
    function populateTestFilters() {
        filterTestCourse.options.length = 1;
        allAuditCourses.forEach(c => {
            const opt = document.createElement("option");
            opt.value = c.id.toString();
            opt.innerText = c.title;
            filterTestCourse.appendChild(opt);
        });

        filterTestTeacher.options.length = 1;
        allAuditTeachers.forEach(t => {
            const opt = document.createElement("option");
            opt.value = t.id.toString();
            opt.innerText = t.fullName;
            filterTestTeacher.appendChild(opt);
        });
    }

    searchTestsBtn.addEventListener("click", async () => await fetchTests());

    clearTestFiltersBtn.addEventListener("click", async () => {
        filterTestCourse.value = "";
        filterTestTeacher.value = "";
        filterTestStartDate.value = "";
        filterTestEndDate.value = "";
        await fetchTests();
    });

    closeTestModalBtn.addEventListener("click", closeEditTestModal);
    cancelTestBtn.addEventListener("click", closeEditTestModal);

    /**
     * Closes the edit test modal, resets the master form fields, hides any active alerts,
     * and clears the dynamically generated nested student results table.
     */
    function closeEditTestModal() {
        editTestModal.classList.add("init-hidden");
        editTestForm.reset();
        editTestAlert.style.display = "none";
        editTestResultsBody.innerHTML = ""; // Cleaning of the internal board
    }

    /**
     * Fetches tests and their nested student results from the backend API based on
     * the current UI filter criteria and renders the results into the data table.
     * @returns {Promise<void>}
     */
    async function fetchTests() {
        testsLoading.classList.remove("init-hidden");
        testsEmpty.classList.add("init-hidden");
        testsTableContainer.classList.add("init-hidden");
        testsTableBody.innerHTML = "";

        try {
            const params = new URLSearchParams();
            if (filterTestCourse.value) params.append('courseId', filterTestCourse.value);
            if (filterTestTeacher.value) params.append('teacherId', filterTestTeacher.value);
            if (filterTestStartDate.value) params.append('startDate', filterTestStartDate.value);
            if (filterTestEndDate.value) params.append('endDate', filterTestEndDate.value);

            const qString = params.toString() ? `?${params.toString()}` : "";
            const response = await ApiService.request(`/api/admin/tests${qString}`, { method: 'GET' });

            /** @type {AdminTestResponseResource[]} */
            const tests = await response.json();

            testsLoading.classList.add("init-hidden");

            if (!tests || tests.length === 0) {
                testsEmpty.classList.remove("init-hidden");
            } else {
                renderTestsTable(tests);
                testsTableContainer.classList.remove("init-hidden");
            }
        } catch (error) {
            console.error("Error fetching tests:", error);
            testsLoading.classList.add("init-hidden");
            Toast.show("Failed to load tests.", "danger");
        }
    }

    /**
     * Renders the fetched tests into the DOM table and attaches event listeners
     * for opening the edit modal and executing the inline confirmation delete action.
     * @param {AdminTestResponseResource[]} tests - The array of test objects to render.
     */
    function renderTestsTable(tests) {
        tests.forEach(test => {
            const row = document.createElement("tr");

            const dateObj = new Date(`${test.date}T12:00:00`);
            const dayName = dateObj.toLocaleDateString('en-US', { weekday: 'long' });

            row.innerHTML = `
                <td><span class="scope-badge scope-slot"><i class="fas fa-book"></i> ${test.courseTitle}</span></td>
                <td class="text-nowrap"><i class="fas fa-chalkboard-teacher text-muted"></i> ${test.teacherFullName}</td>
                <td><strong>${dayName}</strong></td> 
                <td class="text-nowrap"><i class="far fa-calendar-alt text-muted"></i> ${test.date}</td>
                <td>${test.description} <br><small class="text-muted"><i class="fas fa-users"></i> ${test.results.length} students graded</small></td>
                <td class="text-right">
                    <div class="actions-flex-container">
                        <button class="btn-icon btn-icon-edit" title="Edit Test & Grades"><i class="fas fa-pen"></i></button>
                        <div class="delete-btn-placeholder">
                            <button class="btn-icon btn-icon-delete" title="Delete Test"><i class="fas fa-trash"></i></button>
                        </div>
                    </div>
                </td>
            `;

            row.querySelector('.btn-icon-edit').addEventListener("click", () => openEditTestModal(test));

            const deleteBtn = row.querySelector('.btn-icon-delete');
            let timerId = null;
            deleteBtn.addEventListener("click", async () => {
                if (deleteBtn.classList.contains("btn-sure-state")) {
                    clearTimeout(timerId);
                    deleteBtn.innerHTML = '<i class="fas fa-trash"></i>';
                    deleteBtn.classList.remove("btn-sure-state");
                    await executeDeleteTest(test.testId, deleteBtn);
                } else {
                    deleteBtn.innerHTML = '<i class="fas fa-question-circle"></i> Sure?';
                    deleteBtn.classList.add("btn-sure-state");
                    timerId = setTimeout(() => {
                        deleteBtn.innerHTML = '<i class="fas fa-trash"></i>';
                        deleteBtn.classList.remove("btn-sure-state");
                    }, 4000);
                }
            });

            testsTableBody.appendChild(row);
        });
    }

    /**
     * Populates and opens the Edit Test Modal. Implements Master-Detail logic by
     * setting the parent test metadata (Master) and dynamically generating the
     * child table containing editable student grades and comments (Detail).
     * @param {AdminTestResponseResource} test - The comprehensive test object to edit.
     */
    function openEditTestModal(test) {
        editTestId.value = test.testId.toString();
        editTestCourse.value = test.courseTitle;
        editTestTeacher.value = test.teacherFullName;
        editTestDate.value = test.date;
        editTestDescription.value = test.description;

        // Dynamic building of the internal table (Master-Detail logic)
        editTestResultsBody.innerHTML = "";

        if (test.results.length === 0) {
            editTestResultsBody.innerHTML = `<tr><td colspan="3" class="text-center text-muted">No students enrolled in this test.</td></tr>`;
        } else {
            test.results.forEach(res => {
                const tr = document.createElement("tr");

                // Keep the result ID in the dataset to read it in Submit
                tr.dataset.resultId = res.testResultId.toString();

                // Preparing values (handle nulls)
                const gradeVal = res.grade !== null ? res.grade : "";
                const commentsVal = res.comments !== null ? res.comments : "";

                tr.innerHTML = `
                    <td><strong>${res.studentFullName}</strong></td>
                    <td><input type="number" step="0.1" min="0" max="20" class="form-control grade-input" value="${gradeVal}" placeholder="e.g. 18.5"></td>
                    <td><input type="text" class="form-control comments-input" value="${commentsVal}" placeholder="Add comment..."></td>
                `;
                editTestResultsBody.appendChild(tr);
            });
        }

        editTestModal.classList.remove("init-hidden");
    }

    editTestForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const testId = editTestId.value;
        const resultsToUpdate = [];

        // Scanning all rows of the internal table to create the JSON array
        const resultRows = editTestResultsBody.querySelectorAll("tr[data-result-id]");
        resultRows.forEach(row => {
            const resultId = row.dataset.resultId;
            const gradeInput = row.querySelector('.grade-input').value;
            const commentsInput = row.querySelector('.comments-input').value;

            // Type conversions & Null handling
            const grade = gradeInput !== "" ? parseFloat(gradeInput) : null;
            const comments = commentsInput.trim() !== "" ? commentsInput.trim() : null;

            resultsToUpdate.push({
                testResultId: parseInt(resultId, 10),
                grade: grade,
                comments: comments
            });
        });

        const requestBody = {
            date: editTestDate.value,
            description: editTestDescription.value,
            resultsToUpdate: resultsToUpdate
        };

        const originalBtnText = saveTestBtn.innerHTML;
        saveTestBtn.disabled = true;
        saveTestBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving...';
        editTestAlert.style.display = "none";

        try {
            const response = await ApiService.request(`/api/admin/tests/${testId}`, {
                method: 'PUT', body: JSON.stringify(requestBody)
            });
            if (response.status === 204) {
                closeEditTestModal();
                await fetchTests();
            } else {
                const errorData = await response.json();
                showModalAlert(editTestAlert, errorData.message || 'Validation error.', 'danger');
            }
        } catch (error) {
            showModalAlert(editTestAlert, "Network error occurred.", "danger");
        } finally {
            saveTestBtn.disabled = false;
            saveTestBtn.innerHTML = originalBtnText;
        }
    });

    /**
     * Executes the API request to forcefully delete a test (cascading to all its associated results)
     * and refreshes the table upon success.
     * @param {number} testId - The unique identifier of the test to delete.
     * @param {HTMLButtonElement} btn - The delete button element, used to manage UI loading states.
     * @returns {Promise<void>}
     */
    async function executeDeleteTest(testId, btn) {
        btn.disabled = true;
        btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';
        try {
            const response = await ApiService.request(`/api/admin/tests/${testId}`, { method: 'DELETE' });
            if (response.status === 204) await fetchTests();
            else {
                const error = await response.json();
                Toast.show(`Delete Failed: ${error.message || 'Unknown error'}`, "danger");
                btn.disabled = false; btn.innerHTML = '<i class="fas fa-trash"></i>';
            }
        } catch (error) {
            Toast.show("Network error occurred.", "danger");
            btn.disabled = false; btn.innerHTML = '<i class="fas fa-trash"></i>';
        }
    }

    // --- SHARED UTILITIES ---

    /**
     * Reusable function to display alert messages inside any Modal.
     * @param {HTMLElement} alertContainer - The div element meant to display the alert
     * @param {string} msg - The message
     * @param {string} type - 'danger' or 'success'
     */
    function showModalAlert(alertContainer, msg, type) {
        alertContainer.className = `modal-alert alert-${type}`;
        alertContainer.innerHTML = `<i class="fas fa-exclamation-circle"></i> ${msg}`;
        alertContainer.style.display = "block";
    }

    /**
     * Reusable function to format and append Scheduled Class options to a dropdown.
     * Applies correct text formatting for days and times, and optionally appends the teacher's name.
     * @param {HTMLSelectElement} selectElement - The target dropdown element.
     * @param {ScheduledSlotResource[]} slots - The list of slots to format and append.
     * @param {boolean} [includeTeacherName=false] - Whether to append the teacher's name (useful for global filters).
     */
    function appendFormattedSlotOptions(selectElement, slots, includeTeacherName = false) {
        slots.forEach(s => {
            const opt = document.createElement("option");
            opt.value = s.id.toString();

            // Format time and day
            const start = s.startTime.substring(0, 5);
            const end = s.endTime.substring(0, 5);
            const dayFormatted = s.dayOfWeek.charAt(0) + s.dayOfWeek.slice(1).toLowerCase();

            // Base format: "Mathematics (Monday, 16:30)"
            let displayText = `${s.courseTitle} (${dayFormatted}, ${start} - ${end})`;

            // Optional appendage for Activities global filter
            if (includeTeacherName) {
                const teacher = allAuditTeachers.find(t => t.id === s.teacherId);
                if (teacher) {
                    displayText += ` - ${teacher.fullName}`;
                }
            }

            opt.innerText = displayText;
            selectElement.appendChild(opt);
        });
    }

    // --- Global Modal Overlay Click Listener ---
    // Closes the modals when clicking outside the modal content (on the dark overlay)
    window.addEventListener("click", (e) => {

        if (e.target === editAbsenceModal) {
            closeEditAbsenceModal();
        }
        else if (e.target === editActivityModal) {
            closeEditActivityModal();
        }
        else if (e.target === editTestModal) {
            closeEditTestModal();
        }

    });

});
