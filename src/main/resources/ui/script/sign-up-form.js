{
    /** @type {HTMLButtonElement} */
    const signUp = document.getElementById('sign-up');
    /** @type {KeyboardEvent} */
    document.onkeydown = (event) => {
        if (event.key === 'Enter') {
            event.preventDefault();
            signUp.dispatchEvent(new MouseEvent('mousedown'));
        }
    };
    signUp.onmousedown = async () => {
        signUp.disabled = true;
        /** @type {string | null} */
        let token;
        /** @type {Response | null} */
        const response = await makeRequest('/api/id/sign_up/init', { method: 'POST' });
        if (!response) {
            return;
        }
        if (response.ok) {
            token = prompt('Enter sing up token');
        } else if (response.status === 409) {
            queueToast(ToastLevel.WARN, 'Cannot initialize sign up. Try again later.');
        } else {
            queueToast(ToastLevel.ERROR, 'Something went wrong when initializing sing up.');
        }
        /** @type {HTMLInputElement} */
        const password = document.getElementById('password');
        if (token) {
            /** @type {HTMLInputElement} */
            const login = document.getElementById('login');
            /** @type {Response | null} */
            const response = await makeRequest('/api/id/sign_up', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    'login': login.value,
                    'password': password.value,
                    'token': token
                })
            });
            if (!response) {
                return;
            }
            if (response.ok) {
                location.replace('/drive');
                return;
            }
            switch (response.status) {
                case 401:
                    queueToast(ToastLevel.WARN, 'Invalid token.');
                    break;
                case 409:
                    queueToast(ToastLevel.WARN, 'User already exists.');
                    break;
                default:
                    queueToast(ToastLevel.ERROR, 'Something went wrong when signing up.');
            }
        }
        password.value = '';
        signUp.disabled = false;
    };
}
