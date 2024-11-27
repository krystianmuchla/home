/** @type {number} */
let toastId = 0;

/** @type {HTMLDivElement[]} */
let pendingToasts = [];

/** 
 * @param {string} id
 * @returns {void}
 */
export function closeToast(id) {
    /** @type {HTMLDivElement} */
    let toast = document.getElementById(id);
    if (!toast) {
        return;
    }
    toast.remove();
    /** @type {HTMLDivElement | undefined} */
    let pendingToast = pendingToasts.shift();
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
export function queueToast(level, text) {
    /** @type {HTMLDivElement} */
    let toast = document.createElement('div');
    toast.id = ++toastId;
    toast.className = `toast toast-${level}`;
    /** @type {HTMLSpanElement} */
    let toastText = document.createElement('span');
    toastText.className = 'toast-text';
    toastText.textContent = text;
    toast.appendChild(toastText);
    /** @type {HTMLDivElement} */
    let toastClose = document.createElement('div');
    toastClose.className = 'toast-close';
    toastClose.onmousedown = () => closeToast(toast.id);
    toastClose.textContent = 'close';
    toast.appendChild(toastClose);
    /** @type {HTMLCollectionOf<HTMLDivElement>} */
    let toasts = document.getElementsByClassName('toast');
    if (toasts.length > 1) {
        pendingToasts.push(toast);
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
export function setToastText(id, text) {
    /** @type {HTMLDivElement | null | undefined} */
    let toast = findToast(id);
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
export function setToastLevel(id, level) {
    /** @type {HTMLDivElement | null | undefined} */
    let toast = findToast(id);
    if (!toast) {
        return;
    }
    toast.className = `toast toast-${level}`;
}

/**
 * @param {string} id
 * @returns {HTMLDivElement | null | undefined}
 */
function findToast(id) {
    return document.getElementById(id) ?? pendingToasts.find(toast => toast.id === id);
}

/** @type {HTMLDivElement} */
let container = document.createElement('div');
container.id = 'toasts-container';
container.className = 'column';
document.body.appendChild(container);
