{
    /** @type {URL} */
    const url = new URL(location);
    /** @type {URLSearchParams} */
    const query = url.searchParams;
    {
        /** @type {HTMLButtonElement} */
        const button = document.getElementById('upload-file');
        /** @type {HTMLInputElement} */
        const input = document.createElement('input');
        input.type = 'file';
        input.multiple = true;
        input.onchange = async () => {
            /** @type {FileList} */
            const files = input.files;
            /** @type {number} */
            let count = 0;
            /** @type {HTMLDivElement} */
            const toastId = queueToast('info', `Uploading 0 of ${files.length} files...`);
            for (let index = 0; index < files.length; index++) {
                /** @type {File} */
                const file = files[index];
                /** @type {HTMLDivElement} */
                const container = document.getElementById('ls-container');
                if ([...container.children].find((element) => element.textContent === file.name)) {
                    const confirmed = confirm(`Are you sure you want do overwrite the existing ${file.name} file?`);
                    if (!confirmed) {
                        return;
                    }
                }
                setToastText(toastId, `Uploading ${index + 1} of ${files.length} files...`);
                /** @type {Response} */
                const response = await fetch(
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
                ls();
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
        const button = document.getElementById('create-dir');
        button.onmousedown = async () => {
            /** @type {string | null} */
            const name = prompt('Enter new directory name');
            if (!name) {
                return;
            }
            /** @type {Response} */
            const response = await fetch(
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
                ls();
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
                    queueToast('error', 'Something went wrong when creating directory.');
            }
        };
    }
    {
        ls();
        history.replaceState({ dir: query.get('dir') }, '', url);
        /** @param {PopStateEvent} event */
        onpopstate = (event) => {
            /** @type {string | null} */
            const dir = event.state.dir;
            query.delete('dir');
            if (dir) {
                query.set('dir', dir);
            }
            ls();
        };
    }

    /** @returns {Promise<void>} */
    async function ls() {
        /** @type {Response} */
        const response = await fetch('/ui/drive?' + query.toString());
        if (!response.ok) {
            switch (response.status) {
                case 401:
                    location.replace('/id/sign_in');
                    break;
                default:
                    queueToast('error', 'Something went wrong when listing directory.');
            }
            return;
        }
        /** @type {HTMLDivElement} */
        const container = document.getElementById('ls-container');
        /** @type {string} */
        const html = await response.text();
        container.innerHTML = html;
        /** @type {HTMLCollectionOf<HTMLDivElement>} */
        const dirs = document.getElementsByClassName('dir');
        for (const dir of dirs) {
            dir.onmousedown = () => {
                query.set('dir', dir.id);
                ls();
                history.pushState({ dir: query.get('dir') }, '', url);
            };
        }
        /** @type {HTMLCollectionOf<HTMLDivElement>} */
        const files = document.getElementsByClassName('file');
        for (const file of files) {
            file.onmousedown = async () => {
                /** @type {URLSearchParams} */
                const query = new URLSearchParams(location.search);
                query.set('file', file.id);
                /** @type {HTMLAnchorElement} */
                const a = document.createElement('a');
                a.href = '/api/drive?' + query.toString();
                a.download = file.textContent;
                a.click();
            };
        }
    }
}
