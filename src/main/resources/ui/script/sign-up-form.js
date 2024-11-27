import { queueToast } from './toast.js';

/** @type {HTMLButtonElement} */
let signUp = document.getElementById('sign-up');

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
    /** @type {Response} */
    let response = await fetch('/api/users/init', { method: 'POST' });
    if (response.ok) {
        token = prompt('Enter sing up token');
    } else if (response.status === 409) {
        queueToast('warn', 'Cannot initialize sign up. Try again later.');
    } else {
        queueToast('error', 'Something went wrong when initializing sign up.');
    }
    /** @type {HTMLInputElement} */
    let password = document.getElementById('password');
    if (token) {
        /** @type {HTMLInputElement} */
        let name = document.getElementById('name');
        /** @type {HTMLInputElement} */
        let login = document.getElementById('login');
        /** @type {Response} */
        let response = await fetch('/api/users', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                'name': name.value,
                'login': login.value,
                'password': password.value,
                'token': token
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
                queueToast('warn', 'Invalid token.');
                break;
            case 409:
                queueToast('warn', 'User already exists.');
                break;
            default:
                queueToast('error', 'Something went wrong when signing up.');
        }
    }
    password.value = '';
    signUp.disabled = false;
};
