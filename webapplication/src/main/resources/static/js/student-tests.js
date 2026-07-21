/**
 * @typedef {Object} StudentTestResponseResource
 * @property {number} testResultId
 * @property {string} courseName
 * @property {string} teacherName
 * @property {string} date
 * @property {string} description
 * @property {number|null} grade
 * @property {string|null} comments
 * @property {"GRADED" | "PENDING"} status
 */

document.addEventListener("DOMContentLoaded", () => {

    // --- DOM ELEMENTS CACHING ---

    /** @type {HTMLDivElement} */ const testsGrid = document.getElementById('testsGrid');
    /** @type {NodeListOf<HTMLButtonElement>} */ const filterBtns = document.querySelectorAll('.filter-btn');

    // --- STATE MANAGEMENT ---

    /** @type {StudentTestResponseResource[]} */ let allTestResults = [];
    /** @type {string} */ let currentFilter = 'GRADED'; // Default: only show graded ones

    // --- INITIALIZATION ---

    init();

    function init() {
        fetchTestResults().catch(console.error);
        attachEventListeners();
    }

    // --- CORE FETCH FUNCTION ---

    async function fetchTestResults() {
        try {
            testsGrid.innerHTML = '<div class="loading-spinner"><i class="fas fa-circle-notch fa-spin fa-2x"></i><p>Loading your results...</p></div>';

            const response = await fetch('/api/students/me/test-results', {
                method: 'GET',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'same-origin'
            });

            if (!response.ok) {
                showErrorState();
                return;
            }

            allTestResults = await response.json();
            renderGrid();

        } catch (error) {
            console.error('Fetch error:', error);
            showErrorState();
        }
    }

    // --- RENDERING & FILTERING ---

    function renderGrid() {
        testsGrid.innerHTML = '';

        // Apply Tab Filter
        let filtered = allTestResults;
        if (currentFilter !== 'ALL') {
            filtered = allTestResults.filter(test => test.status === currentFilter);
        }

        if (filtered.length === 0) {
            let emptyMsg = currentFilter === 'GRADED' ? 'You have no graded tests yet.' : 'No tests found in this category.';
            testsGrid.innerHTML = `<div class="grid-empty-msg"><i class="fas fa-clipboard-check fa-2x"></i><p>${emptyMsg}</p></div>`;
            return;
        }

        filtered.forEach(test => {
            const card = buildTestCard(test);
            testsGrid.appendChild(card);
        });
    }

    /**
     * Builds the HTML element for a Report Card.
     * @param {StudentTestResponseResource} test
     * @returns {HTMLElement}
     */
    function buildTestCard(test) {
        const card = document.createElement('div');
        card.className = 'test-card';

        // Format Date
        const dateObj = new Date(test.date);
        const formattedDate = dateObj.toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' });

        // Logic for Grade Color & Display
        let gradeDisplay;
        let gradeLabel;
        let gradeClass;

        if (test.status === 'PENDING') {
            gradeDisplay = '<i class="fas fa-hourglass-half"></i>';
            gradeLabel = 'Pending';
            gradeClass = 'grade-pending';
        } else {
            // It is GRADED
            gradeDisplay = test.grade;

            if (test.grade >= 18) {
                gradeClass = 'grade-excellent';
                gradeLabel = 'Excellent';
            } else if (test.grade >= 15) {
                gradeClass = 'grade-good';
                gradeLabel = 'Good';
            } else if (test.grade >= 10) {
                gradeClass = 'grade-pass';
                gradeLabel = 'Pass';
            } else {
                gradeClass = 'grade-fail';
                gradeLabel = 'Fail';
            }
        }

        // Logic for Comments Box
        let commentsHtml = '';
        if (test.comments) {
            commentsHtml = `<div class="test-comments">" ${test.comments} "</div>`;
        }

        card.innerHTML = `
            <div class="test-info-section">
                <h3 class="course-name">${test.courseName}</h3>
                <div class="test-date"><i class="far fa-calendar-alt"></i> ${formattedDate}</div>
                <div class="test-description"><i class="fas fa-file-alt" style="color:#bdc3c7; width:16px;"></i> ${test.description}</div>
                <div class="teacher-name"><i class="fas fa-chalkboard-teacher" style="color:#bdc3c7; width:16px;"></i> ${test.teacherName}</div>
                ${commentsHtml}
            </div>
            
            <div class="test-grade-section ${gradeClass}">
                <div class="grade-circle">${gradeDisplay}</div>
                <div class="grade-label">${gradeLabel}</div>
            </div>
        `;

        return card;
    }

    function showErrorState() {
        testsGrid.innerHTML = '<div class="grid-error-msg"><i class="fas fa-exclamation-triangle fa-2x"></i><p>Error loading test results. ' +
            'Please refresh the page.</p></div>';
    }

    // --- EVENT LISTENERS ---

    function attachEventListeners() {
        // Tab Filters Logic
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
