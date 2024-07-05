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
        const toastId = queueToast(ToastLevel.INFO, uploadingText(0, files.length));
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
            if ([...files].find((file) => file.textContent === fileName)) {
                const confirmed = confirm(`Are you sure you want do overwrite the existing ${fileName} file?`);
                if (!confirmed) {
                    return;
                }
            }
            /** @type {URLSearchParams} */
            const query = new URLSearchParams(location.search);
            query.append('file', fileName);
            setToastText(toastId, uploadingText(fileI + 1, files.length));
            try {
                /** @type {Response} */
                const response = await fetch(
                    '/api/drive?' + query.toString(),
                    {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/octet-stream'
                        },
                        body: file
                    }
                );
                if (response.ok) {
                    uploadCount++;
                } else {
                    setToastLevel(toastId, ToastLevel.WARN);
                }
            } catch (error) {
                console.error(error.message);
                setToastLevel(toastId, ToastLevel.WARN);
            }
        }
        setToastText(toastId, `Uploaded ${uploadCount} of ${files.length} files.`);
        if (uploadCount === files.length) {
            setToastLevel(toastId, ToastLevel.SUCCESS);
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
            try {
                const response = await fetch('/api/drive?' + query.toString(), { method: 'POST' });
                if (response.ok) {
                    queueToast(ToastLevel.SUCCESS, 'Directory created.');
                    return;
                }
                switch (response.status) {
                    case 409:
                        queueToast(ToastLevel.WARN, 'Directory already exists.');
                        break;
                    default:
                        queueToast(ToastLevel.ERROR, 'Something went wrong when creating directory.');
                }
            } catch (error) {
                console.error(error.message);
                queueToast(ToastLevel.ERROR, 'Something went wrong.');
            }
        }
    };
}
{
    /** @type {HTMLCollectionOf<HTMLDivElement>} */
    const dirs = document.getElementsByClassName('dir');
    for (const dir of dirs) {
        dir.onmousedown = () => {
            /** @type {string} */
            const name = dir.textContent;
            /** @type {URLSearchParams} */
            const query = new URLSearchParams(location.search);
            query.append('dir', name);
            location.href = '/drive?' + query.toString();
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
            query.append('file', name);
            /** @type {HTMLAnchorElement} */
            const a = document.createElement('a');
            a.href = '/api/drive?' + query.toString();
            a.download = name;
            a.click();
        };
    }
}
