// XML Management & Testing Tool Helper
function loadSampleXml(type, targetTextareaId) {
    const contextPath = window.APP_CONTEXT || "";
    fetch(contextPath + "/xml-management?action=sample-xml&type=" + type)
        .then(function (response) {
            if (!response.ok) throw new Error("Could not load sample");
            return response.text();
        })
        .then(function (text) {
            const el = document.getElementById(targetTextareaId);
            if (el) {
                el.value = text;
            }
        })
        .catch(function (err) {
            alert("Error loading sample: " + err.message);
        });
}

function copyToClipboard(elementId) {
    const el = document.getElementById(elementId);
    if (!el) return;
    const text = el.value || el.innerText;
    navigator.clipboard.writeText(text).then(function () {
        alert("Copied XML to clipboard!");
    });
}

function handleXPathPresetChange(selectEl) {
    const customInput = document.getElementById("customXPathInput");
    const petInputWrapper = document.getElementById("petNameParamWrapper");
    const val = selectEl.value;

    if (val === "findPetByName" || val === "retrieveMedicalRecordsForPet") {
        if (petInputWrapper) petInputWrapper.style.display = "block";
    } else {
        if (petInputWrapper) petInputWrapper.style.display = "none";
    }

    if (val === "custom") {
        if (customInput) customInput.style.display = "block";
    } else {
        if (customInput) customInput.style.display = "none";
    }
}
