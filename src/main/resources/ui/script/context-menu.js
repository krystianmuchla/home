/** @param {KeyboardEvent} event */
document.onkeydown = (event) => {
    if (event.key === 'Escape') {
        /** @type {HTMLDivElement | null} */
        let menu = document.getElementById('context-menu');
        if (menu) {
            event.preventDefault();
            menu.parentElement.remove();
        }
    }
};

/**
 * @typedef {Object} ContextMenuOption
 * @property {string} name
 * @property {(event: MouseEvent) => void} onmousedown
 */

/**
 * @param {MouseEvent} event
 * @param {ContextMenuOption[]} options
 * @returns {void}
 */
function showContextMenu(event, options) {
    /** @type {HTMLDivElement | null} */
    let menu = document.getElementById('context-menu');
    if (menu) {
        return;
    }
    /** @type {HTMLDivElement} */
    let container = document.createElement('div');
    container.id = 'context-menu-container';
    container.className = 'background';
    container.style.zIndex = '1';
    container.onmousedown = () => {
        container.remove();
    };
    menu = document.createElement('div');
    menu.id = 'context-menu';
    menu.className = 'column on-top';
    menu.style.left = event.clientX + 'px';
    menu.style.top = event.clientY + 'px';
    for (let optionData of options) {
        /** @type {HTMLDivElement} */
        let option = document.createElement('div');
        option.className = 'context-menu-option';
        option.innerText = optionData.name;
        option.onmousedown = optionData.onmousedown;
        menu.appendChild(option);
    }
    container.appendChild(menu);
    document.body.appendChild(container);
    menu = document.getElementById('context-menu');
    /** @type {DOMRect} */
    let rect = menu.getBoundingClientRect();
    if (rect.right > window.innerWidth) {
        menu.style.left = (rect.left - rect.width) + 'px';
    }
    if (rect.bottom > window.innerHeight) {
        menu.style.top = (rect.top - rect.height) + 'px';
    }
}
