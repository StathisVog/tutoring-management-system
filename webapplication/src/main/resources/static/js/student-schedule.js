/**
 * @typedef {Object} StudentScheduleSlot
 * @property {number} slotId
 * @property {string} courseTitle
 * @property {string} teacherName
 * @property {string} startTime
 * @property {string} endTime
 * @property {string|null} classroom
 * @property {"SCHEDULED" | "CANCELLED"} status
 * @property {string|null} cancelReason
 * @property {number|null} activityId
 * @property {string|null} activityDescription
 */

/**
 * @typedef {Object} StudentDailySchedule
 * @property {string} date
 * @property {"MONDAY" | "TUESDAY" | "WEDNESDAY" | "THURSDAY" | "FRIDAY" | "SATURDAY" | "SUNDAY"} dayOfWeek
 * @property {StudentScheduleSlot[]} slots
 */

/**
 * @typedef {Object} StudentLessonActivity
 * @property {number} activityId
 * @property {number} slotId
 * @property {string} courseTitle
 * @property {string} teacherFullName
 * @property {string} date
 * @property {string} description
 */

document.addEventListener("DOMContentLoaded", () => {

    // --- DOM ELEMENTS CACHING ---

    // Navigation & Header Elements
    /** @type {HTMLButtonElement} */ const prevWeekBtn = document.getElementById('prevWeekBtn');
    /** @type {HTMLButtonElement} */ const nextWeekBtn = document.getElementById('nextWeekBtn');
    /** @type {HTMLButtonElement} */ const todayBtn = document.getElementById('todayBtn');
    /** @type {HTMLSpanElement} */ const currentWeekRange = document.getElementById('currentWeekRange');

    // Grid & Feed Containers
    /** @type {HTMLDivElement} */ const scheduleGrid = document.getElementById('scheduleGrid');
    /** @type {HTMLDivElement} */ const activitiesFeed = document.getElementById('activitiesFeed');

    // Modal Elements
    /** @type {HTMLDivElement} */ const lessonModal = document.getElementById('lessonModal');
    /** @type {HTMLSpanElement} */ const closeLessonModal = document.getElementById('closeLessonModal');
    /** @type {HTMLHeadingElement} */ const modalCourseTitle = document.getElementById('modalCourseTitle');
    /** @type {HTMLSpanElement} */ const modalTeacherName = document.getElementById('modalTeacherName');
    /** @type {HTMLSpanElement} */ const modalTime = document.getElementById('modalTime');
    /** @type {HTMLSpanElement} */ const modalClassroomContainer = document.getElementById('modalClassroomContainer');
    /** @type {HTMLSpanElement} */ const modalClassroom = document.getElementById('modalClassroom');
    /** @type {HTMLDivElement} */ const modalActivityBody = document.getElementById('modalActivityBody');

    // --- STATE MANAGEMENT ---

    /** @type {Date|null} */
    let currentStartDate = null;
    /** @type {Date|null} */
    let currentEndDate = null;

    // --- INITIALIZATION ---

    init();

    /**
     * Initializes the dashboard by fetching the default current week schedule
     * and the upcoming activities feed.
     */
    function init() {
        fetchSchedule().catch(console.error);
        fetchUpcomingActivities().catch(console.error);
        attachEventListeners();
    }

    // --- CORE FETCH FUNCTIONS ---

    /**
     * Fetches the student's schedule from the backend.
     * @param {string} [startDateStr] - Optional start date in YYYY-MM-DD format
     * @param {string} [endDateStr] - Optional end date in YYYY-MM-DD format
     * @returns {Promise<void>}
     */
    async function fetchSchedule(startDateStr, endDateStr) {
        try {
            scheduleGrid.innerHTML = '<div class="loading-spinner"><i class="fas fa-circle-notch fa-spin fa-2x"></i><p>Loading schedule...</p></div>';

            let url = '/api/students/me/schedule';
            if (startDateStr && endDateStr) {
                url += `?startDate=${startDateStr}&endDate=${endDateStr}`;
            }

            const response = await fetch(url, {
                method: 'GET',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'same-origin'
            });

            if (!response.ok) {
                showScheduleError();
                return;
            }

            /** @type {StudentDailySchedule[]} */
            const dailySchedules = await response.json();

            if (dailySchedules.length > 0) {
                updateDateRangeState(dailySchedules[0].date, dailySchedules[dailySchedules.length - 1].date);
                renderScheduleGrid(dailySchedules);
            } else {
                scheduleGrid.innerHTML = '<div class="grid-empty-msg"><i class="far fa-calendar-times fa-2x"></i><p>No schedule found for this period.</p></div>';
            }

        } catch (error) {
            console.error('Network error during fetchSchedule:', error);
            showScheduleError();
        }
    }

    /**
     * Fetches the upcoming activities feed from the backend.
     * @returns {Promise<void>}
     */
    async function fetchUpcomingActivities() {
        try {
            const response = await fetch('/api/students/me/activities', {
                method: 'GET',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'same-origin'
            });

            if (!response.ok) {
                showActivitiesError();
                return;
            }

            /** @type {StudentLessonActivity[]} */
            const activities = await response.json();
            renderActivitiesFeed(activities);

        } catch (error) {
            console.error('Network error during fetchUpcomingActivities:', error);
            showActivitiesError();
        }
    }

    // --- RENDERING FUNCTIONS ---

    /**
     * Renders the 7-day grid using the data from the backend.
     * @param {StudentDailySchedule[]} dailySchedules
     */
    function renderScheduleGrid(dailySchedules) {
        scheduleGrid.innerHTML = '';

        dailySchedules.forEach(dayData => {
            const dateObj = new Date(dayData.date);
            const formattedDate = `${String(dateObj.getUTCDate()).padStart(2, '0')}/${String(dateObj.getUTCMonth() + 1).padStart(2, '0')}`;

            const columnDiv = document.createElement('div');
            columnDiv.className = 'day-column';

            // Day Header
            columnDiv.innerHTML = `
                <div class="day-header">
                    <span class="day-name">${dayData.dayOfWeek}</span>
                    <span class="day-date">${formattedDate}</span>
                </div>
                <div class="slots-container" id="slots-container-${dayData.date}"></div>
            `;

            scheduleGrid.appendChild(columnDiv);

            const slotsContainer = columnDiv.querySelector(`#slots-container-${dayData.date}`);

            // Slots Rendering
            if (!dayData.slots || dayData.slots.length === 0) {
                slotsContainer.innerHTML = `<div class="no-slots-msg">No classes</div>`;
            } else {
                dayData.slots.forEach(slot => {
                    const card = buildSlotCard(slot);
                    slotsContainer.appendChild(card);
                });
            }
        });
    }

    /**
     * Builds the HTML element for a single class slot card.
     * @param {StudentScheduleSlot} slot
     * @returns {HTMLElement}
     */
    function buildSlotCard(slot) {
        const card = document.createElement('div');
        const isCancelled = slot.status === 'CANCELLED';
        card.className = `slot-card ${isCancelled ? 'is-cancelled' : 'is-scheduled'}`;

        const startTimeShort = slot.startTime.substring(0, 5);
        const endTimeShort = slot.endTime.substring(0, 5);

        // Logic for Online or Physical Classroom
        const isOnline = !slot.classroom;
        const roomName = isOnline ? 'Online' : slot.classroom;
        const roomIcon = isOnline ? 'fas fa-laptop' : 'fas fa-map-marker-alt';

        let statusBadgeHTML;
        if (isCancelled) {
            statusBadgeHTML = `<span class="status-badge status-cancelled"><i class="fas fa-ban"></i> Cancelled</span>`;
        } else {
            statusBadgeHTML = `<span class="status-badge status-scheduled"><i class="fas fa-check-circle"></i> Scheduled</span>`;
        }

        card.innerHTML = `
            <h4 class="course-title">${slot.courseTitle}</h4>
            <div class="slot-detail"><i class="far fa-clock"></i> ${startTimeShort} - ${endTimeShort}</div>
            <div class="slot-detail"><i class="${roomIcon}"></i> ${roomName}</div>
            ${statusBadgeHTML}
        `;

        // Click event to open Modal
        card.addEventListener('click', () => openLessonModal(slot));

        return card;
    }

    /**
     * Renders the upcoming tasks/activities feed in the sidebar.
     * @param {StudentLessonActivity[]} activities
     */
    function renderActivitiesFeed(activities) {
        if (!activitiesFeed) return;

        if (!activities || activities.length === 0) {
            activitiesFeed.innerHTML = `
                <div class="timeline-empty">
                    <i class="fas fa-glass-cheers"></i>
                    <p>No upcoming tasks. You are all caught up!</p>
                </div>`;
            return;
        }

        activitiesFeed.innerHTML = '';
        const container = document.createElement('div');
        container.className = 'timeline-container';

        activities.forEach(act => {
            const dateObj = new Date(act.date);
            const formattedDate = `${String(dateObj.getUTCDate()).padStart(2, '0')}/${String(dateObj.getUTCMonth() + 1)
                .padStart(2, '0')}/${dateObj.getUTCFullYear()}`;

            const card = document.createElement('div');
            card.className = 'timeline-card';
            card.innerHTML = `
                <div class="timeline-date">
                    <span>${formattedDate}</span>
                </div>
                <div style="font-weight: bold; color: #34495e; margin-bottom: 5px;">${act.courseTitle}</div>
                <p class="timeline-desc">${act.description}</p>
            `;
            container.appendChild(card);
        });

        activitiesFeed.appendChild(container);
    }

    // --- MODAL MANAGEMENT ---

    /**
     * Populates and opens the detailed lesson modal.
     * @param {StudentScheduleSlot} slot
     */
    function openLessonModal(slot) {
        modalCourseTitle.textContent = slot.courseTitle;
        modalTeacherName.textContent = slot.teacherName;
        modalTime.textContent = `${slot.startTime.substring(0, 5)} - ${slot.endTime.substring(0, 5)}`;

        const iconElement = modalClassroomContainer.querySelector('i');

        if (slot.classroom) {
            iconElement.className = 'fas fa-map-marker-alt';
            modalClassroom.textContent = slot.classroom;
        } else {
            // If it is null, it is an Online course
            iconElement.className = 'fas fa-laptop';
            modalClassroom.textContent = 'Online';
        }

        modalClassroomContainer.style.display = 'inline-flex';

        // Reset Box styles
        modalActivityBody.className = 'modal-body activity-status-box';

        if (slot.status === 'CANCELLED') {
            modalActivityBody.classList.add('cancelled-box');
            modalActivityBody.innerHTML = `
                <h4><i class="fas fa-exclamation-circle"></i> Class Canceled</h4>
                <p>${slot.cancelReason || 'No reason provided.'}</p>
            `;
        } else {
            const desc = slot.activityDescription ? slot.activityDescription : 'No specific lesson activities or homework assigned for this lesson.';
            modalActivityBody.innerHTML = `
                <h4><i class="fas fa-tasks"></i> Lesson Plan & Homework</h4>
                <p>${desc}</p>
            `;
        }

        lessonModal.classList.add('show');
    }

    /**
     * Closes the lesson modal.
     */
    function closeActivityModal() {
        lessonModal.classList.remove('show');
    }

    // --- UTILITIES & DATE MANAGEMENT ---

    /**
     * Updates the internal date state and UI badge based on backend response.
     * @param {string} startStr - Start date string from API
     * @param {string} endStr - End date string from API
     */
    function updateDateRangeState(startStr, endStr) {
        currentStartDate = new Date(startStr);
        currentEndDate = new Date(endStr);

        const formatOptions = { day: '2-digit', month: '2-digit', year: 'numeric' };
        const displayStart = currentStartDate.toLocaleDateString('en-GB', formatOptions);
        const displayEnd = currentEndDate.toLocaleDateString('en-GB', formatOptions);

        currentWeekRange.innerHTML = `<i class="far fa-calendar-alt"></i> ${displayStart} - ${displayEnd}`;
    }

    /**
     * Calculates new dates and triggers a schedule fetch.
     * @param {number} daysOffset - Number of days to shift
     */
    function navigateWeek(daysOffset) {
        if (!currentStartDate || !currentEndDate) return;

        const newStart = new Date(currentStartDate);
        newStart.setUTCDate(newStart.getUTCDate() + daysOffset);

        const newEnd = new Date(currentEndDate);
        newEnd.setUTCDate(newEnd.getUTCDate() + daysOffset);

        const startStr = newStart.toISOString().split('T')[0];
        const endStr = newEnd.toISOString().split('T')[0];

        fetchSchedule(startStr, endStr).catch(console.error);
    }

    /**
     * Helper function to display schedule fetch errors / out of bounds
     */
    function showScheduleError() {
        scheduleGrid.innerHTML = `
            <div class="grid-empty-msg">
                <i class="far fa-calendar-times fa-3x"></i>
                <p class="empty-title">Out of Academic Year Bounds</p>
                <p class="empty-subtitle">Schedules are only available for the current academic year (September 1st - August 31st).</p>
            </div>
        `;
    }

    /**
     * Helper function to display activities feed errors
     */
    function showActivitiesError() {
        if (activitiesFeed) {
            activitiesFeed.innerHTML = '<div class="timeline-empty"><p>Error loading activities.</p></div>';
        }
    }

    // --- EVENT LISTENERS ---

    /**
     * Attaches all static DOM event listeners.
     */
    function attachEventListeners() {

        // Navigation Buttons
        if (prevWeekBtn) {
            prevWeekBtn.addEventListener('click', () => navigateWeek(-7));
        }

        if (nextWeekBtn) {
            nextWeekBtn.addEventListener('click', () => navigateWeek(7));
        }

        if (todayBtn) {
            todayBtn.addEventListener('click', () => {
                fetchSchedule().catch(console.error); // No dates = defaults to current week
            });
        }

        // Modal Close Mechanisms
        if (closeLessonModal) {
            closeLessonModal.addEventListener('click', closeActivityModal);
        }

        // Close Modal on outside click
        window.addEventListener('click', (event) => {
            if (event.target === lessonModal) {
                closeActivityModal();
            }
        });
    }

});
