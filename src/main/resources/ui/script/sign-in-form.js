{
    /** @type {HTMLButtonElement} */
    const signIn = document.getElementById('sign-in');
    /** @param {KeyboardEvent} event */
    document.onkeydown = (event) => {
        if (event.key === 'Enter') {
            event.preventDefault();
            signIn.click();
        }
    };
    signIn.onmousedown = async () => {
        signIn.disabled = true;
        /** @type {HTMLInputElement} */
        const login = document.getElementById('login');
        /** @type {HTMLInputElement} */
        const password = document.getElementById('password');
        try {
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
            } else if (response.status === 401) {
                console.error('Incorrect credentials given.');
            } else {
                console.error('Cannot sign in. Try again later.');
            }
        } catch (error) {
            console.error(error.message);
        }
        password.value = '';
        signIn.disabled = false;
    };
}
