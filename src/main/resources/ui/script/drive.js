{
    /** @type {URL} */
    let url = new URL(location);
    /** @type {URLSearchParams} */
    let query = url.searchParams;
    {
        /** @type {HTMLButtonElement} */
        let button = document.getElementById('upload-file');
        /** @type {HTMLInputElement} */
        let input = document.createElement('input');
        input.type = 'file';
        input.multiple = true;
        input.onchange = async () => {
            /** @type {FileList} */
            let files = input.files;
            /** @type {number} */
            let count = 0;
            /** @type {string} */
            let toastId = queueToast('info', `Uploading 0 of ${files.length} files...`);
            for (let index = 0; index < files.length; index++) {
                /** @type {File} */
                let file = files[index];
                /** @type {HTMLCollectionOf<HTMLDivElement>} */
                let fileNames = document.getElementsByClassName('file-name');
                if ([...fileNames].find((fName) => fName.textContent === file.name)) {
                    let confirmed = confirm(`Are you sure you want do overwrite the existing ${file.name} file?`);
                    if (!confirmed) {
                        closeToast(toastId);
                        return;
                    }
                }
                setToastText(toastId, `Uploading ${index + 1} of ${files.length} files...`);
                /** @type {Response} */
                let response = await fetch(
                    '/api/drive?' + query.toString(),
                    {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/octet-stream',
                            'File-Name': file.name
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
                refreshMain();
            } else {
                setToastLevel(toastId, 'error');
            }
            if (count === files.length) {
                setToastLevel(toastId, 'success');
            }
        };
        button.onmousedown = () => {
            input.click();
        };
    }
    {
        /** @type {HTMLButtonElement} */
        let button = document.getElementById('create-dir');
        button.onmousedown = async () => {
            /** @type {string | null} */
            let name = prompt('Enter new directory name');
            if (!name) {
                return;
            }
            /** @type {Response} */
            let response = await fetch(
                '/api/drive',
                {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        'dir': query.get('dir'),
                        'name': name
                    })
                }
            );
            if (response.ok) {
                queueToast('success', 'Directory created.');
                refreshMain();
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
    }
    {
        refreshMain();
        history.replaceState({ dir: query.get('dir') }, '', url);
        /** @param {PopStateEvent} event */
        onpopstate = (event) => {
            query.delete('dir');
            /** @type {string | null} */
            let dir = event.state.dir;
            if (dir) {
                query.set('dir', dir);
            }
            refreshMain();
        };
    }

    /** @returns {Promise<void>} */
    async function refreshMain() {
        /** @type {Response} */
        let response = await fetch('/ui/drive/main?' + query.toString());
        if (!response.ok) {
            switch (response.status) {
                case 401:
                    location.replace('/id/sign_in');
                    break;
                default:
                    queueToast('error', 'Something went wrong when listing a directory.');
            }
            return;
        }
        let main = document.getElementById('main');
        /** @type {string} */
        let html = await response.text();
        main.innerHTML = html;
        /** @type {HTMLCollectionOf<HTMLSpanElement>} */
        let segments = document.getElementsByClassName('segment');
        for (let segment of segments) {
            if (segment.id !== (query.get('dir') ?? '')) {
                segment.classList.add('path-nav');
                segment.onmousedown = () => {
                    if (segment.id) {
                        query.set('dir', segment.id);
                    } else {
                        query.delete('dir');
                    }
                    refreshMain();
                    history.pushState({ dir: query.get('dir') }, '', url);
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
                    { name: 'Delete', onmousedown: () => deleteFile(file.id, name.textContent) },
                ]);
            };
        }
    }

    /**
     * @param {string} id
     * @returns {void}
     */
    function openDir(id) {
        query.set('dir', id);
        refreshMain();
        history.pushState({ dir: query.get('dir') }, '', url);
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
        let response = await fetch(`/api/drive?dir=${id}`, { method: 'DELETE' });
        if (response.ok) {
            queueToast('success', 'Directory deleted.');
            refreshMain();
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
        a.href = `/api/drive?file=${id}`;
        a.download = name;
        a.click();
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
        let response = await fetch(`/api/drive?file=${id}`, { method: 'DELETE' });
        if (response.ok) {
            queueToast('success', 'File deleted.');
            refreshMain();
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
}
