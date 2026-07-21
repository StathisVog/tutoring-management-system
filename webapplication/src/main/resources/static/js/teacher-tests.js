/**
 * @typedef {Object} TestResponseResource
 * @property {number} id
 * @property {number} courseId
 * @property {number} slotId
 * @property {string} courseTitle
 * @property {string} date
 * @property {string} description
 * @property {number} totalStudentsCount
 * @property {number} gradedStudentsCount
 */

/**
 * @typedef {Object} TeachersTestResultResource
 * @property {number} testResultId
 * @property {string} studentFullName
 * @property {number|null} grade
 * @property {string|null} comments
 */

/**
 * @typedef {Object} ActiveStudentResource
 * @property {number} id
 * @property {string} fullName
 * @property {string} username
 */

document.addEventListener("DOMContentLoaded", async () => {

    // --- DOM ELEMENTS CACHING ---

    // Views
    /** @type {HTMLDivElement} */ const testsDashboardView = document.getElementById("testsDashboardView");
    /** @type {HTMLDivElement} */ const rosterView = document.getElementById("rosterView");

    // Dashboard Elements
    /** @type {HTMLDivElement} */ const testsGrid = document.getElementById("testsGrid");
    /** @type {HTMLInputElement} */ const filterFromDate = document.getElementById("filterFromDate");
    /** @type {HTMLInputElement} */ const filterToDate = document.getElementById("filterToDate");
    /** @type {HTMLButtonElement} */ const applyFilterBtn = document.getElementById("applyFilterBtn");
    /** @type {HTMLButtonElement} */ const clearFilterBtn = document.getElementById("clearFilterBtn");
    /** @type {HTMLSelectElement} */ const filterSlot = document.getElementById("filterSlot");

    // Roster Elements
    /** @type {HTMLButtonElement} */ const backToTestsBtn = document.getElementById("backToTestsBtn");
    /** @type {HTMLHeadingElement} */ const rosterCourseTitle = document.getElementById("rosterCourseTitle");
    /** @type {HTMLParagraphElement} */ const rosterTestDesc = document.getElementById("rosterTestDesc");
    /** @type {HTMLTableSectionElement} */ const rosterTableBody = document.getElementById("rosterTableBody");

    // Modal Elements
    /** @type {HTMLButtonElement} */ const openCreateTestModalBtn = document.getElementById("openCreateTestModalBtn");
    /** @type {HTMLDivElement} */ const createTestModal = document.getElementById("createTestModal");
    /** @type {HTMLSpanElement} */ const closeTestModalBtn = document.getElementById("closeTestModalBtn");
    /** @type {HTMLDivElement} */ const testAlert = document.getElementById("testAlert");
    /** @type {HTMLFormElement} */ const createTestForm = document.getElementById("createTestForm");
    /** @type {HTMLSelectElement} */ const slotSelect = document.getElementById("slotSelect");
    /** @type {HTMLInputElement} */ const testDate = document.getElementById("testDate");
    /** @type {HTMLInputElement} */ const testDescription = document.getElementById("testDescription");
    /** @type {HTMLButtonElement} */ const saveTestBtn = document.getElementById("saveTestBtn");

    // Advanced Options Elements
    const assignmentScopeRadios = document.querySelectorAll('input[name="assignmentScope"]');
    /** @type {HTMLDivElement} */ const specificStudentsContainer = document.getElementById("specificStudentsContainer");


    // --- GLOBAL STATE ---
    let currentTestId = null;

    // --- INITIALIZATION ---

    // Set min date for new tests to today
    const now = new Date();
    testDate.min = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`;

    await fetchTests();
    await populateFilterDropdown();


    // --- EVENT LISTENERS ---

    // Filters
    applyFilterBtn.addEventListener("click", () => {
        fetchTests(filterFromDate.value, filterToDate.value);
    });

    clearFilterBtn.addEventListener("click", () => {
        filterFromDate.value = "";
        filterToDate.value = "";
        filterSlot.value = "ALL";
        fetchTests();
    });

    // Class (ScheduledSlot) Filter
    filterSlot.addEventListener("change", () => {
        applyCurrentSlotFilter();
    });

    // View Switching
    backToTestsBtn.addEventListener("click", async () => {
        rosterView.classList.add("d-none");
        testsDashboardView.classList.remove("d-none");
        currentTestId = null;

        // Refreshing tests to fill progress bars
        await fetchTests(filterFromDate.value, filterToDate.value);
    });

    // Modal Controls
    openCreateTestModalBtn.addEventListener("click", async() => {
        createTestModal.classList.remove("d-none");
        await populateSlotDropdown();
    });

    closeTestModalBtn.addEventListener("click", () => {
        createTestModal.classList.add("d-none");
        resetForm();
    });

    window.addEventListener("click", (e) => {
        if (e.target === createTestModal) {
            createTestModal.classList.add("d-none");
            resetForm();
        }
    });

    // Advanced Options Toggle
    assignmentScopeRadios.forEach(radio => {
        radio.addEventListener("change", async(e) => {
            if (e.target.value === "SPECIFIC") {
                specificStudentsContainer.classList.remove("d-none");
                await loadStudentsForSelectedSlot();
            } else {
                specificStudentsContainer.classList.add("d-none");
            }
        });
    });

    // Re-fetch students if slot changes while SPECIFIC is selected
    slotSelect.addEventListener("change", async() => {
        const selectedScope = document.querySelector('input[name="assignmentScope"]:checked').value;
        if (selectedScope === "SPECIFIC") {
            await loadStudentsForSelectedSlot();
        }
    });

    // Form Submission
    createTestForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        await submitNewTest();
    });


    // --- API & RENDER FUNCTIONS ---

    /**
     * Fetches tests from the backend.
     * @param {string} [fromDate]
     * @param {string} [toDate]
     */
    async function fetchTests(fromDate = "", toDate = "") {
        testsGrid.innerHTML = `<div class="loading-spinner">Loading your tests...</div>`;

        let url = `/api/teachers/me/tests`;
        const params = new URLSearchParams();
        if (fromDate) params.append("fromDate", fromDate);
        if (toDate) params.append("toDate", toDate);
        if (params.toString()) url += `?${params.toString()}`;

        try {
            const response = await ApiService.request(url, { method: 'GET' });
            if (response.ok) {
                const tests = await response.json();
                renderTestCards(tests);
            } else {
                showGridError("Failed to load tests.");
            }
        } catch (error) {
            safeToast("Network error while loading tests.", "danger");
            showGridError("Network connection error.");
        }
    }

    /**
     * Renders the test cards in the dashboard.
     * @param {TestResponseResource[]} tests
     */
    function renderTestCards(tests) {
        testsGrid.innerHTML = "";

        if (!tests || tests.length === 0) {
            testsGrid.innerHTML = `
                <div class="grid-empty-msg">
                    <i class="fas fa-folder-open"></i>
                    <p>No tests found for this period.</p>
                </div>
            `;
            return;
        }

        tests.forEach(test => {
            const card = document.createElement("div");
            card.className = "test-card";
            card.setAttribute("data-slot-id", test.slotId.toString());

            const d = new Date(test.date);
            const formattedDate = `${String(d.getUTCDate()).padStart(2, '0')}/${String(d.getUTCMonth() + 1).padStart(2, '0')}/${d.getUTCFullYear()}`;

            // --- Progress Bar Calculation ---
            const total = test.totalStudentsCount || 0;
            const graded = test.gradedStudentsCount || 0;
            const percentage = total === 0 ? 0 : Math.round((graded / total) * 100);

            let progressClass = "progress-none";
            if (percentage === 100) progressClass = "progress-complete";
            else if (percentage > 0) progressClass = "progress-partial";

            card.innerHTML = `
                <div class="test-card-header">
                    <h4 class="test-card-course">${test.courseTitle}</h4>
                    <span class="test-card-date"><i class="far fa-calendar-alt"></i> ${formattedDate}</span>
                </div>
                <p class="test-card-desc">${test.description}</p>
                
                <div class="test-progress-container">
                    <div class="test-progress-text">
                        <span>Graded: <strong>${graded}/${total}</strong></span>
                        <span>${percentage}%</span>
                    </div>
                    <div class="test-progress-bar-bg">
                        <div class="test-progress-bar-fill ${progressClass}" style="width: ${percentage}%;"></div>
                    </div>
                </div>

                <div class="test-card-footer">
                    <button class="btn-grade" data-id="${test.id}" data-title="${test.courseTitle}" data-desc="${test.description}" data-date="${formattedDate}">
                        <i class="fas fa-edit"></i> Grade Students
                    </button>
                </div>
            `;

            // Open roster when clicking the grade button (or the card itself)
            card.querySelector(".btn-grade").addEventListener("click", async(e) => {
                e.stopPropagation(); // Prevent double triggering if card is also clickable
                await openRosterView(test.id, test.courseTitle, test.description, formattedDate);
            });

            testsGrid.appendChild(card);
        });

        applyCurrentSlotFilter();
    }

    /**
     * Applies the Dropdown filter (hides/shows cards).
     * If 0 cards remain, it shows an empty state message.
     */
    function applyCurrentSlotFilter() {
        const selectedSlotId = filterSlot.value;
        const allCards = document.querySelectorAll(".test-card");
        let visibleCount = 0;

        allCards.forEach(card => {
            if (selectedSlotId === "ALL" || card.getAttribute("data-slot-id") === selectedSlotId) {
                card.classList.remove("d-none");
                visibleCount++;
            } else {
                card.classList.add("d-none");
            }
        });

        // Empty State Management (If all cards were hidden due to a filter)
        let emptyMsg = document.getElementById("slotFilterEmptyMsg");

        if (visibleCount === 0 && allCards.length > 0) {
            if (!emptyMsg) {
                emptyMsg = document.createElement("div");
                emptyMsg.id = "slotFilterEmptyMsg";
                emptyMsg.className = "grid-empty-msg";
                emptyMsg.innerHTML = `
                    <i class="fas fa-folder-open"></i>
                    <p>There are no Tests in this Class yet.</p>
                `;
                testsGrid.appendChild(emptyMsg);
            }
            emptyMsg.classList.remove("d-none");
        } else if (emptyMsg) {
            emptyMsg.classList.add("d-none");
        }
    }

    /**
     * Opens the Detail View (Roster) for a specific test.
     */
    async function openRosterView(testId, courseTitle, testDesc, formattedDate) {
        currentTestId = testId;

        // Update UI Titles
        rosterCourseTitle.innerText = courseTitle;
        rosterTestDesc.innerText = `${testDesc} (${formattedDate})`;

        // Switch Views
        testsDashboardView.classList.add("d-none");
        rosterView.classList.remove("d-none");

        // Fetch Students
        await fetchTestResults(testId);
    }

    /**
     * Fetches the roster (student results) for the selected test.
     * @param {number} testId
     */
    async function fetchTestResults(testId) {
        rosterTableBody.innerHTML = `<tr><td colspan="3" class="text-center loading-spinner">Loading students...</td></tr>`;

        try {
            const response = await ApiService.request(`/api/teachers/me/tests/${testId}/results`, { method: 'GET' });
            if (response.ok) {
                const results = await response.json();
                renderRoster(results);
            } else {
                rosterTableBody.innerHTML = `<tr><td colspan="3" class="text-center text-danger">Failed to load students.</td></tr>`;
            }
        } catch (error) {
            safeToast("Network error while loading roster.", "danger");
            rosterTableBody.innerHTML = `<tr><td colspan="3" class="text-center text-danger">Network error.</td></tr>`;
        }
    }

    /**
     * Renders the student table with inline grading inputs.
     * @param {TeachersTestResultResource[]} results
     */
    function renderRoster(results) {
        rosterTableBody.innerHTML = "";

        if (!results || results.length === 0) {
            rosterTableBody.innerHTML = `<tr><td colspan="3" class="text-center text-muted">No students assigned to this test.</td></tr>`;
            return;
        }

        results.forEach(res => {
            const row = document.createElement("tr");

            const currentGrade = res.grade !== null ? res.grade.toFixed(2) : "";
            const currentComment = res.comments || "";

            row.innerHTML = `
                <td><strong>${res.studentFullName}</strong></td>
                <td>
                    <input type="number" class="grade-input" data-id="${res.testResultId}" value="${currentGrade}" min="0" max="20" step="0.10" placeholder="--.--">
                </td>
                <td>
                    <input type="text" class="comment-input" data-id="${res.testResultId}" value="${currentComment}" placeholder="Add feedback...">
                </td>
                <td class="col-action">
                    <button class="btn-save-row" data-id="${res.testResultId}">
                        <i class="fas fa-save"></i> Save
                    </button>
                </td>
            `;

            rosterTableBody.appendChild(row);
        });

        attachInlineGradingListeners();
    }

    /**
     * Attaches "click" events to the explicitly added Save buttons per row.
     */
    function attachInlineGradingListeners() {
        const saveButtons = document.querySelectorAll(".btn-save-row");

        saveButtons.forEach(btn => {
            btn.addEventListener("click", async (e) => {
                const targetBtn = e.currentTarget;
                await handleRowSave(targetBtn);
            });
        });
    }

    /**
     * Handles the PATCH request when the Save button is clicked.
     * @param {HTMLButtonElement} saveBtn
     */
    async function handleRowSave(saveBtn) {
        const resultId = saveBtn.getAttribute("data-id");

        // Find inputs in the same row
        const row = saveBtn.closest("tr");
        const gradeInput = row.querySelector(".grade-input");
        const commentInput = row.querySelector(".comment-input");

        const gradeVal = gradeInput.value.trim();
        const commentVal = commentInput.value.trim();

        // Basic validation before sending
        let gradeNumber = null;
        if (gradeVal !== "") {
            gradeNumber = parseFloat(gradeVal);
            if (isNaN(gradeNumber) || gradeNumber < 0 || gradeNumber > 20) {
                safeToast("Grade must be between 0.00 and 20.00", "danger");
                return;
            }
        } else {
            safeToast("Grade cannot be empty.", "danger");
            return;
        }

        const payload = {
            grade: gradeNumber,
            comments: commentVal
        };

        // Disable button and show spinner
        saveBtn.disabled = true;
        const originalText = saveBtn.innerHTML;
        saveBtn.innerHTML = `<i class="fas fa-spinner fa-spin"></i> Saving...`;

        try {
            const response = await ApiService.request(`/api/teachers/me/test-results/${resultId}`, {
                method: 'PATCH',
                body: JSON.stringify(payload)
            });

            if (response.status === 204) {
                // Visual feedback on the input
                gradeInput.classList.add("saved");
                setTimeout(() => gradeInput.classList.remove("saved"), 1500);

                safeToast("Grade successfully saved.", "success");
            } else {
                const errorData = await response.json();
                safeToast(errorData.message || "Failed to save grade.", "danger");
            }
        } catch (error) {
            safeToast("Network error. Grade not saved.", "danger");
        } finally {
            // Restore button state
            saveBtn.disabled = false;
            saveBtn.innerHTML = originalText;
        }
    }

    // --- MODAL SUBMISSION LOGIC ---

    /**
     * Submits the POST request for a new test.
     */
    async function submitNewTest() {

        // hide any previous alert
        testAlert.classList.add("d-none");

        // Basic Validation Dropdown
        if (!slotSelect.value || slotSelect.selectedIndex === 0) {
            showModalAlert("Please select a class to assign the test.", "danger");
            return;
        }

        saveTestBtn.disabled = true;
        saveTestBtn.innerText = "Assigning...";

        // Extract courseId and slotId from the selected option
        const selectedOption = slotSelect.options[slotSelect.selectedIndex];
        const slotIdStr = selectedOption.value;
        const courseIdStr = selectedOption.getAttribute("data-course-id");

        const assignmentScope = document.querySelector('input[name="assignmentScope"]:checked').value;
        let selectedStudentIds = [];

        if (assignmentScope === "SPECIFIC") {
            const checkboxes = document.querySelectorAll('input[name="studentCheck"]:checked');
            selectedStudentIds = Array.from(checkboxes).map(cb => parseInt(cb.value));

            if (selectedStudentIds.length === 0) {
                showModalAlert("Please select at least one student.", "danger");
                saveTestBtn.disabled = false;
                saveTestBtn.innerText = "Assign Test";
                return;
            }
        }

        const payload = {
            courseId: parseInt(courseIdStr),
            scheduledSlotId: parseInt(slotIdStr),
            date: testDate.value,
            description: testDescription.value.trim(),
            studentIds: selectedStudentIds
        };

        try {
            const response = await ApiService.request(`/api/teachers/me/tests`, {
                method: 'POST',
                body: JSON.stringify(payload)
            });

            if (response.status === 201) {
                safeToast("Test successfully assigned!", "success");
                createTestModal.classList.add("d-none");
                resetForm();
                await fetchTests(filterFromDate.value, filterToDate.value); // Refresh grid
            } else {
                const errorData = await response.json();
                showModalAlert(errorData.message || "Failed to create test.", "danger");
            }
        } catch (error) {
            safeToast("Network error. Test not created.", "danger");
        } finally {
            saveTestBtn.disabled = false;
            saveTestBtn.innerText = "Assign Test";
        }
    }


    // --- HELPER FUNCTIONS FOR MODAL DATA ---

    /**
     * Fetches the teacher's schedule and extracts unique active scheduled slots.
     * @returns {Promise<Map<number, Object>>} A Map of unique slots keyed by slotId.
     */
    async function fetchUniqueTeacherSlots() {
        const today = new Date();

        const start = new Date(today);
        start.setUTCDate(start.getUTCDate() - 7);

        const end = new Date(today);
        end.setUTCDate(end.getUTCDate() + 14);

        const startStr = start.toISOString().split('T')[0];
        const endStr = end.toISOString().split('T')[0];

        const response = await ApiService.request(`/api/teachers/me/schedule?startDate=${startStr}&endDate=${endStr}`, { method: 'GET' });

        if (!response.ok) {
            throw new Error("Failed to fetch schedule from API.");
        }

        const days = await response.json();
        const uniqueSlots = new Map();

        days.forEach(day => {
            day.slots.forEach(slot => {
                if (!uniqueSlots.has(slot.slotId)) {
                    uniqueSlots.set(slot.slotId, {
                        ...slot,
                        dayOfWeek: day.dayOfWeek
                    });
                }
            });
        });

        return uniqueSlots;
    }

    /**
     * Populates the Modal <select> for creating a new test.
     */
    async function populateSlotDropdown() {
        slotSelect.innerHTML = `<option value="" disabled selected>Loading your classes...</option>`;

        try {
            const uniqueSlots = await fetchUniqueTeacherSlots();

            if (uniqueSlots.size === 0) {
                slotSelect.innerHTML = `<option value="" disabled selected>No active classes found.</option>`;
                return;
            }

            slotSelect.innerHTML = `<option value="" disabled selected>-- Select a Class --</option>`;
            uniqueSlots.forEach(slot => {
                const option = document.createElement("option");
                option.value = slot.slotId;
                option.setAttribute("data-course-id", slot.courseId);
                option.innerText = `${slot.courseTitle} (${slot.dayOfWeek}, ${slot.startTime.substring(0,5)} - ${slot.endTime.substring(0,5)})`;
                slotSelect.appendChild(option);
            });
        } catch (error) {
            slotSelect.innerHTML = `<option value="" disabled selected>Failed to load classes.</option>`;
        }
    }

    /**
     * Populates the Top Filter Dropdown in the dashboard.
     */
    async function populateFilterDropdown() {
        try {
            const uniqueSlots = await fetchUniqueTeacherSlots();

            uniqueSlots.forEach(slot => {
                const option = document.createElement("option");
                option.value = slot.slotId;
                option.innerText = `${slot.courseTitle} (${slot.dayOfWeek}, ${slot.startTime.substring(0,5)} - ${slot.endTime.substring(0,5)})`;
                filterSlot.appendChild(option);
            });
        } catch (error) {
            console.error("Failed to load classes for filter", error);
        }
    }

    /**
     * Fetches active students for the Advanced Option (Specific Students).
     * Leverages the newly created endpoint to build dynamic checkboxes.
     */
    async function loadStudentsForSelectedSlot() {
        specificStudentsContainer.innerHTML = `<p class="text-muted small"><i class="fas fa-spinner fa-spin"></i> Loading students...</p>`;

        const slotId = slotSelect.value;
        if (!slotId) {
            specificStudentsContainer.innerHTML = `<p class="text-muted small">Please select a class first to see students.</p>`;
            return;
        }

        try {
            const response = await ApiService.request(`/api/teachers/me/slots/${slotId}/students`, { method: 'GET' });

            if (response.ok) {
                /** @type {ActiveStudentResource[]} */
                const students = await response.json();

                specificStudentsContainer.innerHTML = ''; // Clear loading message

                if (students.length === 0) {
                    specificStudentsContainer.innerHTML = `<p class="text-muted small">No active students found in this class.</p>`;
                    return;
                }

                students.forEach(student => {
                    const label = document.createElement("label");
                    label.className = "checkbox-label";

                    // Display format: Full Name (Username)
                    const displayText = student.username ? `${student.fullName} (${student.username})` : student.fullName;

                    label.innerHTML = `
                        <input type="checkbox" name="studentCheck" value="${student.id}">
                        ${displayText}
                    `;
                    specificStudentsContainer.appendChild(label);
                });
            } else {
                specificStudentsContainer.innerHTML = `<p class="text-danger small">Failed to load students.</p>`;
            }
        } catch (error) {
            specificStudentsContainer.innerHTML = `<p class="text-danger small">Network error while loading students.</p>`;
        }
    }

    /**
     * Displays an inline alert inside the main grid using the error-state CSS modifier.
     * @param {string} msg
     */
    function showGridError(msg) {
        testsGrid.innerHTML = `
            <div class="grid-empty-msg error-state">
                <i class="fas fa-exclamation-circle"></i>
                <p>${msg}</p>
            </div>
        `;
    }

    /**
     * Displays an inline alert inside the creation test modal.
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

        testAlert.className = `alert alert-${type}`;
        testAlert.innerHTML = `<i class="fas ${iconClass}"></i> ${msg}`;
        testAlert.classList.remove("d-none");
    }

    /**
     * Resets the modal form to its default clean state.
     */
    function resetForm() {
        createTestForm.reset();
        specificStudentsContainer.classList.add("d-none");
        testAlert.classList.add("d-none"); // hides the alert
    }

    /**
     * Safe wrapper for Toast to prevent UI freezing if toast.js is missing.
     */
    function safeToast(msg, type = 'info') {
        if (typeof Toast !== 'undefined' && Toast.show) {
            Toast.show(msg, type);
        } else {
            alert(msg);
        }
    }

});
