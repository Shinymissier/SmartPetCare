// Main Application Interactions
document.addEventListener("DOMContentLoaded", function () {
    // Auto-dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll(".alert");
    alerts.forEach(function (alert) {
        setTimeout(function () {
            alert.style.transition = "opacity 0.5s ease";
            alert.style.opacity = "0";
            setTimeout(function () { alert.remove(); }, 500);
        }, 5000);
    });

    // Delete confirmation prompt
    const deleteButtons = document.querySelectorAll("[data-confirm]");
    deleteButtons.forEach(function (btn) {
        btn.addEventListener("click", function (e) {
            const message = btn.getAttribute("data-confirm") || "Are you sure you want to delete this record?";
            if (!confirm(message)) {
                e.preventDefault();
            }
        });
    });

    // Tab switcher
    const tabButtons = document.querySelectorAll(".tab-btn");
    tabButtons.forEach(function (btn) {
        btn.addEventListener("click", function () {
            const targetId = btn.getAttribute("data-target");
            const container = btn.closest(".tabs-container") || document;

            container.querySelectorAll(".tab-btn").forEach(function (b) { b.classList.remove("active"); });
            container.querySelectorAll(".tab-panel").forEach(function (p) { p.classList.remove("active"); });

            btn.classList.add("active");
            const targetPanel = container.querySelector("#" + targetId);
            if (targetPanel) {
                targetPanel.classList.add("active");
            }
        });
    });
});
