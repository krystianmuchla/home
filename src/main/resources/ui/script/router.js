/**
 * @typedef {Object} RouteState
 * @property {string} content
 */

/**
 * @callback RouteCallback
 * @param {URL} url
 * @returns {Promise<string | null>}
 */

/**
 * @callback PostRouteCallback
 * @returns {Promise<void>}
 */

/** @type {HTMLElement} */
let router = document.getElementById('router');

/** @type {RouteCallback | null} */
let _routeCallback = null;

/** @type {PostRouteCallback | null} */
let _postRouteCallback = null;

/**
 * @param {RouteCallback} routeCallback
 * @param {PostRouteCallback | null} [postRouteCallback = null]
 * @returns {Promise<void>}
 */
export async function initRouter(routeCallback, postRouteCallback = null) {
    _routeCallback = routeCallback;
    _postRouteCallback = postRouteCallback;
    await refreshRoute();
    /** @param {PopStateEvent} event */
    onpopstate = async (event) => {
        /** @type {RouteState} */
        let state = event.state;
        await consumeRouteState(state);
    };
}

/**
 * @param {URL} url
 * @returns {Promise<void>}
 */
export async function route(url) {
    /** @type {RouteState | null} */
    let state = await routeState(url);
    if (state) {
        history.pushState(state, '', url);
        await consumeRouteState(state);
    }
}

/**
 * @returns {Promise<void>}
 */
export async function refreshRoute() {
    /** @type {URL} */
    const url = new URL(location);
    /** @type {RouteState | null} */
    let state = await routeState(url);
    if (state) {
        history.replaceState(state, '', url);
        await consumeRouteState(state);
    }
}

/**
 * @param {URL} url 
 * @returns {Promise<RouteState | null>}
 */
async function routeState(url) {
    if (!_routeCallback) {
        console.error('Route callback not found');
        return null;
    }
    /** @type {string | null} */
    let result = await _routeCallback(url);
    if (result == null) {
        return null;
    }
    return {
        content: result,
    };
}

/**
 * @param {RouteState} state
 * @returns {Promise<void>}
 */
async function consumeRouteState(state) {
    if (!router) {
        console.error('Router not found');
        return;
    }
    router.innerHTML = state.content;
    if (_postRouteCallback) {
        await _postRouteCallback();
    }
}
