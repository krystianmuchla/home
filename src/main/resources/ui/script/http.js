/**
 * @param {RequestInfo | URL} input
 * @param {RequestInit} [init]
 * @returns {Promise<Response | null>}
 */
async function makeRequest(input, init) {
    try {
        const response = await fetch(input, init);
        return response;
    } catch (error) {
        console.error(error.message);
        queueToast(ToastLevel.ERROR, 'Something went wrong.');
        return null;
    }
}
