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
        [...newFiles].forEach(async (newFile) => {
            /** @type {string} */
            let fileName = newFile.name;
            /** @type {number} */
            const i = fileName.lastIndexOf('.');
            if (i >= 0) {
                fileName = fileName.slice(0, i);
            }
            if ([...files].find((file) => file.textContent === fileName)) {
                const overwrite = confirm('Are you sure you want do overwrite existing file?');
                if (!overwrite) {
                    return;
                }
            }
            /** @type {URLSearchParams} */
            const query = new URLSearchParams(location.search);
            query.append('file', fileName);
            // todo info for user
            /** @type {Response} */
            const response = await fetch(
                '/api/drive?' + query.toString(),
                {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/octet-stream'
                    },
                    body: newFile
                }
            );
            // todo info for user
        });
    };
    uploadFile.onmousedown = () => {
        fileInput.click();
    };
}
