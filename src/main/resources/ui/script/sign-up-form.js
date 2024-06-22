{
    /** @type {HTMLButtonElement} */
    const signUp = document.getElementById('sign-up');
    /** @type {KeyboardEvent} */
    document.onkeydown = (event) => {
        if (event.key === 'Enter') {
            event.preventDefault();
            signUp.click();
        }
    };
    signUp.onmousedown = async () => {
        signUp.disabled = true;
        /** @type {string | null} */
        let token;
        try {
            /** @type {Response} */
            const response = await fetch('/api/id/sign_up/init', { method: 'POST' });
            if (response.ok) {
                token = prompt('Enter sing up token');
            } else {
                console.error('Cannot initialize sign up. Try again later.');
            }
        } catch (error) {
            console.error(error.message);
        }
        /** @type {HTMLInputElement} */
        const password = document.getElementById('password');
        if (token) {
            /** @type {HTMLInputElement} */
            const login = document.getElementById('login');
            try {
                /** @type {Response} */
                const response = await fetch('/api/id/sign_up', {
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
                if (response.ok) {
                    location.replace('/drive');
                } else if (response.status === 401) {
                    console.error('Invalid token.');
                } else {
                    console.error('Something went wrong when signing up. Try again later.');
                }
            } catch (error) {
                console.error(error.message);
            }
        }
        password.value = '';
        signUp.disabled = false;
    };
}
