/**
 * Global Toast Notification System
 * Usage: Toast.show("Message", "success|danger|info");
 */
const Toast = {
    container: null,

    init() {
        // Create the container dynamically if it does not exist
        if (!document.getElementById('global-toast-container')) {
            this.container = document.createElement('div');
            this.container.id = 'global-toast-container';
            document.body.appendChild(this.container);
        } else {
            this.container = document.getElementById('global-toast-container');
        }
    },

    /**
     * @param {string} message - The message we want to show
     * @param {string} type - 'success', 'danger', ή 'info'
     */
    show(message, type = 'info') {
        if (!this.container) this.init();

        const toastEl = document.createElement('div');
        toastEl.className = `toast-msg toast-${type}`;

        // Dynamically insert the correct icon
        let icon = 'fa-info-circle';
        if (type === 'success') icon = 'fa-check-circle';
        if (type === 'danger') icon = 'fa-exclamation-circle';

        toastEl.innerHTML = `<i class="fas ${icon}"></i> <span>${message}</span>`;

        // Add it to the screen
        this.container.appendChild(toastEl);

        // After 6 seconds, we give it the fade-out class
        setTimeout(() => {
            toastEl.classList.add('fade-out');

            // Once the fade-out animation is finished, delete it from the HTML
            toastEl.addEventListener('animationend', () => {
                toastEl.remove();
            });
        }, 6000);
    }
};

// Initialize when the page loads
document.addEventListener('DOMContentLoaded', () => Toast.init());
