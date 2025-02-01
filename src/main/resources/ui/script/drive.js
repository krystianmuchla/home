import { showContextMenu } from './context-menu.js';
import { initRouter, refreshRoute, route } from './router.js';
import { queueToast, setToastLevel, setToastText } from './toast.js';

/** @type {HTMLButtonElement} */
let uploadFileButton = document.getElementById('upload-file');

/** @type {HTMLInputElement} */
let uploadFileInput = document.createElement('input');

uploadFileInput.type = 'file';
uploadFileInput.multiple = true;
uploadFileInput.onchange = async () => {
    /** @type {FileList} */
    let files = uploadFileInput.files;
    /** @type {number} */
    let count = 0;
    /** @type {string} */
    let toastId = queueToast('info', `Uploading 0 of ${files.length} files...`);
    /** @type {URL} */
    let url = new URL(location);
    for (let index = 0; index < files.length; index++) {
        /** @type {File} */
        let file = files[index];
        setToastText(toastId, `Uploading ${index + 1} of ${files.length} files...`);
        /** @type {Response} */
        let response = await fetch(
            '/api/drive/files' + url.search,
            {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/octet-stream',
                    'File-Name': encodeURI(file.name)
                },
                body: file
            }
        );
        if (response.ok) {
            count++;
        } else {
            setToastLevel(toastId, 'warn');
        }
    }
    setToastText(toastId, `Uploaded ${count} of ${files.length} files.`);
    if (count > 0) {
        refreshRoute();
    } else {
        setToastLevel(toastId, 'error');
    }
    if (count === files.length) {
        setToastLevel(toastId, 'success');
    }
    uploadFileInput.value = '';
};

uploadFileButton.onmousedown = () => {
    uploadFileInput.click();
};

/** @type {HTMLButtonElement} */
let createDirButton = document.getElementById('create-dir');

createDirButton.onmousedown = async () => {
    /** @type {string | null} */
    let name = prompt('Enter new directory name');
    if (!name) {
        return;
    }
    /** @type {URLSearchParams} */
    let query = new URL(location).searchParams;
    /** @type {Response} */
    let response = await fetch(
        '/api/drive/directories',
        {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                'dir': query.get('dir'),
                'name': name,
            }),
        }
    );
    if (response.ok) {
        queueToast('success', 'Directory created.');
        refreshRoute();
        return;
    }
    switch (response.status) {
        case 401:
            location.replace('/id/sign_in');
            break;
        case 409:
            queueToast('warn', 'Directory already exists.');
            break;
        default:
            queueToast('error', 'Something went wrong when creating a directory.');
    }
};

initRouter(async (url) => {
    /** @type {Response} */
    let response = await fetch('/ui/drive' + url.search);
    if (!response.ok) {
        switch (response.status) {
            case 401:
                location.replace('/id/sign_in');
                break;
            default:
                queueToast('error', 'Something went wrong when listing a directory.');
        }
        return null;
    }
    return await response.text();
}, () => {
    /** @type {HTMLCollectionOf<HTMLSpanElement>} */
    let segments = document.getElementsByClassName('segment');
    /** @type {URL} */
    let url = new URL(location);
    /** @type {URLSearchParams} */
    let query = url.searchParams;
    for (let segment of segments) {
        if (segment.id !== (query.get('dir') ?? '')) {
            segment.classList.add('path-nav');
            segment.onmousedown = () => {
                if (segment.id) {
                    query.set('dir', segment.id);
                } else {
                    query.delete('dir');
                }
                route(url);
            };
        }
    }
    /** @type {HTMLCollectionOf<HTMLDivElement>} */
    let dirs = document.getElementsByClassName('dir');
    for (let dir of dirs) {
        /** @type {HTMLDivElement} */
        let name = [...dir.children].find((d) => d.classList.contains('dir-name'));
        name.onmousedown = () => openDir(dir.id);
        /** @type {HTMLDivElement} */
        let menu = [...dir.children].find((d) => d.classList.contains('dir-menu'));
        /** @param {MouseEvent} event */
        menu.onmousedown = (event) => {
            showContextMenu(event, [
                { name: 'Open', onmousedown: () => openDir(dir.id) },
                { name: 'Rename', onmousedown: () => renameDir(dir.id, name.textContent) },
                { name: 'Delete', onmousedown: () => deleteDir(dir.id, name.textContent) },
            ]);
        };
    }
    /** @type {HTMLCollectionOf<HTMLDivElement>} */
    let files = document.getElementsByClassName('file');
    for (let file of files) {
        /** @type {HTMLDivElement} */
        let name = [...file.children].find((f) => f.classList.contains('file-name'));
        /** @type {HTMLDivElement} */
        let menu = [...file.children].find((f) => f.classList.contains('file-menu'));
        /** @param {MouseEvent} event */
        menu.onmousedown = (event) => {
            showContextMenu(event, [
                { name: 'Download', onmousedown: () => downloadFile(file.id, name.textContent) },
                { name: 'Rename', onmousedown: () => renameFile(file.id, name.textContent) },
                { name: 'Delete', onmousedown: () => deleteFile(file.id, name.textContent) },
            ]);
        };
    }
});

/**
 * @param {string} id
 * @returns {void}
 */
function openDir(id) {
    /** @type {URL} */
    let url = new URL(location);
    /** @type {URLSearchParams} */
    let query = url.searchParams;
    query.set('dir', id);
    route(url);
}

/**
 * @param {string} id
 * @param {string} name
 * @returns {Promise<void>}
 */
async function renameDir(id, name) {
    let newName = prompt('Enter the new directory name', name);
    if (!newName) {
        return;
    }
    let response = await fetch(
        `/api/drive/directories?dir=${id}`,
        {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                'name': newName,
            }),
        }
    );
    if (response.ok) {
        queueToast('success', 'Directory renamed.');
        refreshRoute();
        return;
    }
    switch (response.status) {
        case 401:
            location.replace('/id/sign_in');
            break;
        default:
            queueToast('error', 'Something went wrong when renaming a directory.');
    }
}

/**
 * @param {string} id
 * @param {string} name
 * @returns {Promise<void>}
 */
async function deleteDir(id, name) {
    let confirmation = confirm(`Are you sure you want to delete the ${name} directory and all of its content?`);
    if (!confirmation) {
        return;
    }
    /** @type {Response} */
    let response = await fetch(`/api/drive/directories?dir=${id}`, { method: 'DELETE' });
    if (response.ok) {
        queueToast('success', 'Directory deleted.');
        refreshRoute();
        return;
    }
    switch (response.status) {
        case 401:
            location.replace('/id/sign_in');
            break;
        default:
            queueToast('error', 'Something went wrong when deleting a directory.');
    }
}

/**
 * @param {string} id
 * @param {string} name
 * @returns {void}
 */
function downloadFile(id, name) {
    /** @type {HTMLAnchorElement} */
    let a = document.createElement('a');
    a.href = `/api/drive/files?file=${id}`;
    a.download = name;
    a.click();
}

/**
 * @param {string} id
 * @param {string} name
 * @returns {Promise<void>} 
 */
async function renameFile(id, name) {
    let newName = prompt('Enter the new file name', name);
    if (!newName) {
        return;
    }
    let response = await fetch(
        `/api/drive/files?file=${id}`,
        {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                'name': newName,
            }),
        }
    );
    if (response.ok) {
        queueToast('success', 'File renamed.');
        refreshRoute();
        return;
    }
    switch (response.status) {
        case 401:
            location.replace('/id/sign_in');
            break;
        default:
            queueToast('error', 'Something went wrong when renaming a file.');
    }
}

/**
 * @param {string} id
 * @param {string} name
 * @returns {Promise<void>}
 */
async function deleteFile(id, name) {
    let confirmation = confirm(`Are you sure you want to delete ${name} file?`);
    if (!confirmation) {
        return;
    }
    /** @type {Response} */
    let response = await fetch(`/api/drive/files?file=${id}`, { method: 'DELETE' });
    if (response.ok) {
        queueToast('success', 'File deleted.');
        refreshRoute();
        return;
    }
    switch (response.status) {
        case 401:
            location.replace('/id/sign_in');
            break;
        default:
            queueToast('error', 'Something went wrong when deleting a file.');
    }
}
