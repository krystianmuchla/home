{
    const signUp = document.getElementById('sign-up');
    document.onkeydown = function(event) {
        if (event.key === 'Enter') {
            event.preventDefault();
            signUp.click();
        }
    }
    signUp.onclick = async function() {
        signUp.disabled = true;
        let token;
        try {
            const response = await fetch('/api/id/sign_up/init', { method: 'POST' });
            if (response.ok) {
                token = prompt('Enter sing up token');
            } else {
                console.error('Cannot initialize sign up. Try again later.');
            }
        } catch (error) {
            console.error(error.message);
        }
        const password = document.getElementById('password');
        if (token) {
            const login = document.getElementById('login');
            try {
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
                    window.location.replace('/drive');
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
    }
}