{
    /** @type {URL} */
    const url = new URL(location);
    /** @type {URLSearchParams} */
    const query = url.searchParams;
    {
        /** @type {HTMLButtonElement} */
        const btn = document.getElementById('upload-file');
        /** @type {HTMLInputElement} */
        const input = document.createElement('input');
        input.type = 'file';
        input.multiple = true;
        input.onchange = async () => {
            /** @type {FileList} */
            const files = input.files;
            /** @type {number} */
            let uploadCount = 0;
            /** @type {HTMLDivElement} */
            const toastId = queueToast('info', uploadingText(0, files.length));
            for (let fileI = 0; fileI < files.length; fileI++) {
                /** @type {File} */
                const file = files[fileI];
                /** @type {string} */
                let fileName = file.name;
                /** @type {number} */
                const dotI = fileName.lastIndexOf('.');
                if (dotI >= 0) {
                    fileName = fileName.slice(0, dotI);
                }
                /** @type {HTMLDivElement} */
                const container = document.getElementById('ls-container');
                if ([...container.children].find((row) => row.textContent === fileName)) {
                    const confirmed = confirm(`Are you sure you want do overwrite the existing ${fileName} file?`);
                    if (!confirmed) {
                        return;
                    }
                }
                /** @type {URLSearchParams} */
                const query = new URLSearchParams(location.search);
                query.set('file', fileName);
                setToastText(toastId, uploadingText(fileI + 1, files.length));
                /** @type {Response} */
                const response = await fetch(
                    '/api/drive?' + query.toString(),
                    {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/octet-stream'
                        },
                        body: file
                    },
                    (error) => {
                        console.error(error.message);
                        setToastLevel(toastId, 'warn');
                    }
                );
                if (response.ok) {
                    uploadCount++;
                } else {
                    setToastLevel(toastId, 'warn');
                }
            }
            setToastText(toastId, `Uploaded ${uploadCount} of ${files.length} files.`);
            if (uploadCount > 0) {
                ls();
            }
            if (uploadCount === files.length) {
                setToastLevel(toastId, 'success');
            }
        };
        btn.onmousedown = () => {
            input.click();
        };

        /**
         * @param {number} i
         * @param {number} of
         * @returns {string}
         */
        function uploadingText(i, of) {
            return `Uploading ${i} of ${of} files...`;
        }
    }
    {
        /** @type {HTMLButtonElement} */
        const btn = document.getElementById('create-dir');
        btn.onmousedown = async () => {
            /** @type {string | null} */
            const name = prompt('Enter new directory name');
            if (name) {
                /** @type {URLSearchParams} */
                const query = new URLSearchParams(location.search);
                query.append('dir', name);
                /** @type {Response} */
                const response = await fetch('/api/drive?' + query.toString(), { method: 'POST' });
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
            }
        };
    }
    {
        ls();
        history.replaceState({ dirs: query.getAll('dir') }, '', url);
        /** @param {PopStateEvent} event */
        onpopstate = (event) => {
            /** @type {string[]} */
            const dirs = event.state.dirs;
            query.delete('dir');
            for (const dir of dirs) {
                query.append('dir', dir);
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
        if (response.ok) {
            /** @type {HTMLDivElement} */
            const container = document.getElementById('ls-container');
            /** @type {string} */
            const html = await response.text();
            container.innerHTML = html;
            /** @type {HTMLCollectionOf<HTMLDivElement>} */
            const dirs = document.getElementsByClassName('dir');
            for (const dir of dirs) {
                dir.onmousedown = () => {
                    /** @type {string} */
                    const name = dir.textContent;
                    query.append('dir', name);
                    ls();
                    history.pushState({ dirs: query.getAll('dir') }, '', url);
                };
            }
            /** @type {HTMLCollectionOf<HTMLDivElement>} */
            const files = document.getElementsByClassName('file');
            for (const file of files) {
                file.onmousedown = () => {
                    /** @type {string} */
                    const name = file.textContent;
                    /** @type {URLSearchParams} */
                    const query = new URLSearchParams(location.search);
                    query.set('file', name);
                    /** @type {HTMLAnchorElement} */
                    const a = document.createElement('a');
                    a.href = '/api/drive?' + query.toString();
                    a.download = name;
                    a.click();
                };
            }
        }
    }
}
