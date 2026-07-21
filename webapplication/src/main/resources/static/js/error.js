document.addEventListener("DOMContentLoaded", () => {

    const errorDataElement = document.getElementById("errorData");
    const statusCode = errorDataElement.getAttribute("data-status");

    const errorCard = document.querySelector(".error-card");
    const errorIcon = document.getElementById("errorIcon");
    const errorTitle = document.getElementById("errorTitle");
    const errorDescription = document.getElementById("errorDescription");

    if (statusCode === "403") {
        // Forbidden
        errorCard.style.borderTopColor = "#e74c3c";
        errorIcon.className = "fas fa-ban color-403";
        errorTitle.innerText = "Access Denied (403)";
        errorDescription.innerText = "You do not have the required permissions to view this page. If you believe this is a mistake, please contact the administrator.";

    } else if (statusCode === "404") {
        // Not Found
        errorCard.style.borderTopColor = "#e67e22";
        errorIcon.className = "fas fa-compass color-404";
        errorTitle.innerText = "Page Not Found (404)";
        errorDescription.innerText = "It looks like you're lost. The page you are looking for does not exist, might have been removed, or is temporarily unavailable.";

    } else if (statusCode === "500") {
        // Internal Server Error
        errorCard.style.borderTopColor = "#9b59b6";
        errorIcon.className = "fas fa-server color-500";
        errorTitle.innerText = "Server Error (500)";
        errorDescription.innerText = "Oops! Something went wrong on our servers. Our team has been notified and is working to fix the issue. Please try again later.";

    } else {
        // Default Fallback status code
        errorCard.style.borderTopColor = "#34495e";
        errorIcon.className = "fas fa-exclamation-triangle color-default";
        errorTitle.innerText = `Unexpected Error (${statusCode || 'Unknown'})`;
        errorDescription.innerText = "An unexpected error occurred. Please go back to the homepage and try again.";
    }

});
