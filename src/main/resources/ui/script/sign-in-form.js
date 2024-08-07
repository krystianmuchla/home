{
    /** @type {HTMLButtonElement} */
    const signIn = document.getElementById('sign-in');
    /** @param {KeyboardEvent} event */
    document.onkeydown = (event) => {
        if (event.key === 'Enter') {
            event.preventDefault();
            signIn.dispatchEvent(new MouseEvent('mousedown'));
        }
    };
    signIn.onmousedown = async () => {
        signIn.disabled = true;
        /** @type {HTMLInputElement} */
        const login = document.getElementById('login');
        /** @type {HTMLInputElement} */
        const password = document.getElementById('password');
        /** @type {Response} */
        const response = await fetch('/api/id/sign_in', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                'login': login.value,
                'password': password.value
            })
        });
        if (response.ok) {
            location.replace('/drive');
            return;
        }
        switch (response.status) {
            case 400:
                queueToast('warn', 'Invalid data provided.');
                break;
            case 401:
                queueToast('warn', 'Incorrect credentials given.');
                break;
            case 429:
                queueToast('warn', 'Cannot sign in. Try again later.');
                break;
            default:
                queueToast('error', 'Something went wrong when signing in.');
        }
        password.value = '';
        signIn.disabled = false;
    };
}
