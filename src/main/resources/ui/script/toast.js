/** @type {number} */
let _toastId = 0;

/** @type {HTMLDivElement[]} */
let _pendingToasts = [];

/** 
 * @param {string} id
 * @returns {void}
 */
function closeToast(id) {
    /** @type {HTMLDivElement} */
    let toast = document.getElementById(id);
    if (!toast) {
        return;
    }
    toast.remove();
    /** @type {HTMLDivElement | undefined} */
    let pendingToast = _pendingToasts.shift();
    if (pendingToast) {
        let container = document.getElementById('toasts-container');
        container.appendChild(pendingToast);
    }
}

/**
 * @param {string} level
 * @param {string} text
 * @returns {string}
 */
function queueToast(level, text) {
    /** @type {HTMLDivElement} */
    let toast = document.createElement('div');
    toast.id = ++_toastId;
    toast.className = `toast toast-${level}`;
    toast.innerHTML = `<span class="toast-text">${text}</span><div class="toast-close" onmousedown="closeToast(${toast.id})">close</div>`;
    /** @type {HTMLCollectionOf<HTMLDivElement>} */
    let toasts = document.getElementsByClassName('toast');
    if (toasts.length > 1) {
        _pendingToasts.push(toast);
    } else {
        /** @type {HTMLDivElement} */
        let container = document.getElementById('toasts-container');
        container.appendChild(toast);
    }
    return toast.id;
}

/**
 * @param {string} id
 * @param {string} text
 * @returns {void}
 */
function setToastText(id, text) {
    /** @type {HTMLDivElement} */
    let toast = document.getElementById(id);
    if (!toast) {
        return;
    }
    /** @type {HTMLSpanElement} */
    let textSpan = toast.getElementsByClassName('toast-text')[0];
    if (!textSpan) {
        return;
    }
    textSpan.textContent = text;
}

/**
 * @param {string} id
 * @param {string} level
 * @returns {void}
 */
function setToastLevel(id, level) {
    /** @type {HTMLDivElement} */
    let toast = document.getElementById(id);
    if (!toast) {
        return;
    }
    toast.className = `toast toast-${level}`;
}

{
    /** @type {HTMLDivElement} */
    let container = document.createElement('div');
    container.id = 'toasts-container';
    container.className = 'column';
    document.body.appendChild(container);
}
