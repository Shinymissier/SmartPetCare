// Smart Pet Care - Dashboard JS (no charts)
// Live clock, sync timer, counter animation, auto-refresh

document.addEventListener("DOMContentLoaded", function () {

    // --- Live Clock ---
    function updateClock() {
        const now = new Date();
        const h = String(now.getHours()).padStart(2, '0');
        const m = String(now.getMinutes()).padStart(2, '0');
        const s = String(now.getSeconds()).padStart(2, '0');
        const el = document.getElementById('liveClock');
        if (el) el.textContent = h + ':' + m + ':' + s;
    }
    updateClock();
    setInterval(updateClock, 1000);

    // --- Last Sync Timer ---
    let syncSeconds = 0;
    setInterval(function () {
        syncSeconds++;
        const el = document.getElementById('lastSyncTime');
        if (el) {
            el.textContent = syncSeconds < 60
                ? syncSeconds + 's ago'
                : Math.floor(syncSeconds / 60) + 'm ago';
        }
    }, 1000);

    // --- Counter Animation ---
    document.querySelectorAll('.counter-animate').forEach(function (el) {
        const target = parseInt(el.getAttribute('data-count')) || 0;
        let current = 0;
        const increment = Math.max(1, Math.ceil(target / 30));
        const timer = setInterval(function () {
            current += increment;
            if (current >= target) { current = target; clearInterval(timer); }
            el.textContent = current;
        }, 40);
    });

    // --- Auto-refresh data every 30 seconds ---
    setInterval(function () {
        fetch(window.APP_CONTEXT + '/dashboard?format=json')
            .then(function (r) { return r.json(); })
            .then(function () {
                syncSeconds = 0;
                const el = document.getElementById('lastSyncTime');
                if (el) {
                    el.textContent = 'Just now';
                    el.style.color = 'var(--accent-emerald)';
                }
            })
            .catch(function () { /* silent */ });
    }, 30000);
});
