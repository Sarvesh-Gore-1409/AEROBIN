/* 
   AeroBins Dashboard Logic
   Updated for new Grid Layout
*/

document.addEventListener('DOMContentLoaded', () => {
    // Initialize any dashboard specific logic here
    console.log("Dashboard loaded");
});

// Toast Notification System (Reusable)
function showToast(message, type = "info") {
    const container = document.getElementById('toast-container');
    if (!container) return; // Guard clause

    const toast = document.createElement('div');
    toast.className = `toast ${type}`;

    // Icons based on type
    let icon = '';
    if (type === 'success') icon = '✔';
    if (type === 'error') icon = '✖';
    if (type === 'info') icon = 'ℹ';

    toast.innerHTML = `<span>${icon}</span> <span>${message}</span>`;

    container.appendChild(toast);

    // Remove after 3 seconds
    setTimeout(() => {
        toast.style.animation = 'toastOut 0.4s forwards';
        toast.addEventListener('animationend', () => {
            if (container.contains(toast)) {
                container.removeChild(toast);
            }
        });
    }, 3000);
}
