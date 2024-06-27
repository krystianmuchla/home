{
    /** @type {HTMLButtonElement} */
    const createDir = document.getElementById('create-dir');
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
    /** @type {HTMLButtonElement} */
    const uploadFile = document.getElementById('upload-file');
    /** @type {HTMLInputElement} */
    const fileInput = document.createElement('input');
    fileInput.type = 'file';
    fileInput.multiple = true;
    fileInput.onchange = async () => {
        /** @type {FileList} */
        const newFiles = fileInput.files;
        /** @type {number} */
        let uploadCount = 0;
        /** @type {HTMLDivElement} */
        const toastId = queueToast(ToastLevel.INFO, uploadingText(0, newFiles.length));
        for (let fileI = 0; fileI < newFiles.length; fileI++) {
            /** @type {File} */
            const file = newFiles[fileI];
            /** @type {string} */
            let fileName = file.name;
            /** @type {number} */
            const dotI = fileName.lastIndexOf('.');
            if (dotI >= 0) {
                fileName = fileName.slice(0, dotI);
            }
            if ([...files].find((file) => file.textContent === fileName)) {
                const overwrite = confirm(`Are you sure you want do overwrite the existing ${fileName} file?`);
                if (!overwrite) {
                    return;
                }
            }
            /** @type {URLSearchParams} */
            const query = new URLSearchParams(location.search);
            query.append('file', fileName);
            setToastText(toastId, uploadingText(fileI + 1, newFiles.length));
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
        setToastText(toastId, `Uploaded ${uploadCount} of ${newFiles.length} files.`);
        if (uploadCount === newFiles.length) {
            setToastLevel(toastId, ToastLevel.SUCCESS);
        }
    };
    uploadFile.onmousedown = () => {
        fileInput.click();
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
