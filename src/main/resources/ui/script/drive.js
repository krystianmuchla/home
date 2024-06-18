{
    const dirs = document.getElementsByClassName('dir');
    for (const dir of dirs) {
        dir.onclick = () => {
            const name = dir.textContent;
            if (!name) {
                console.error('Could not resolve directory name');
                return;
            }
            const query = new URLSearchParams(location.search);
            query.append('dir', name);
            location.href = '/drive?' + query.toString();
        }
    }
    const files = document.getElementsByClassName('file');
    for (const file of files) {
        file.onclick = () => {
            const name = file.textContent;
            if (!name) {
                console.error('Could not resolve file name');
                return;
            }
            const query = new URLSearchParams(location.search);
            query.append('file', name);
            const a = document.createElement('a');
            a.href = '/api/drive?' + query.toString();
            a.download = name;
            a.click();
        }
    }
}
