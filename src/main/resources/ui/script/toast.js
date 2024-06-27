/** @type {number} */
let toastId = 0;

/** @type {HTMLDivElement[]} */
const pendingToasts = [];

/** 
 * @param {string} id
 * @returns {void}
 */
function closeToast(id) {
    /** @type {HTMLDivElement} */
    const toast = document.getElementById(id);
    if (!toast) {
        return;
    }
    toast.remove();
    /** @type {HTMLDivElement | undefined} */
    const pendingToast = pendingToasts.shift();
    if (pendingToast) {
        const container = document.getElementById('toasts-container');
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
    const toast = document.createElement('div');
    toast.id = ++toastId;
    toast.className = `toast toast-${level}`;
    toast.innerHTML = `<span class="toast-text">${text}</span><div class="toast-close" onmousedown="closeToast(${toast.id})">close</div>`;
    /** @type {HTMLCollectionOf<HTMLDivElement>} */
    const toasts = document.getElementsByClassName('toast');
    if (toasts.length > 1) {
        pendingToasts.push(toast);
    } else {
        /** @type {HTMLDivElement} */
        const container = document.getElementById('toasts-container');
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
    const toast = document.getElementById(id);
    if (!toast) {
        return;
    }
    /** @type {HTMLSpanElement} */
    const textSpan = toast.getElementsByClassName('toast-text')[0];
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
    const toast = document.getElementById(id);
    if (!toast) {
        return;
    }
    toast.className = `toast toast-${level}`;
}

const ToastLevel = {
    INFO: 'info',
    SUCCESS: 'success',
    WARN: 'warn',
    ERROR: 'error'
};

{
    /** @type {HTMLDivElement} */
    const container = document.createElement('div');
    container.id = 'toasts-container';
    container.className = 'column';
    document.body.appendChild(container);
}
