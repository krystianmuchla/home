/**
 * @typedef {Object} RouteState
 * @property {string} content
 */

/**
 * @callback URLMapper
 * @param {URL} url
 * @returns {URL}
 */

/**
 * @callback RouteCallback
 * @returns {Promise<void>}
 */

export class ExplicitRouter {
    /**
     * @param {Element} anchor
     * @param {URLMapper} urlMapper
     * @param {RouteCallback} callback
     */
    constructor(anchor, urlMapper, callback) {
        this.anchor = anchor;
        this.urlMapper = urlMapper;
        this.callback = callback;
        /** @param {PopStateEvent} event */
        onpopstate = async (event) => {
            /** @type {RouteState} */
            let state = event.state;
            this.anchor.innerHTML = state.content;
            await this.callback();
        };
    }

    /**
     * @returns {Promise<void>}
     */
    async refresh() {
        /** @type {URL} */
        let url = new URL(location);
        /** @type {URL} */
        let contentUrl = this.urlMapper(url);
        /** @type {Response} */
        let response = await fetch(contentUrl.pathname + contentUrl.search);
        if (response.ok) {
            /** @type {string} */
            let content = await response.text();
            this.anchor.innerHTML = content;
            /** @type {RouteState} */
            let state = {
                content: content,
            };
            history.replaceState(state, '', url);
            await this.callback();
        }
    }

    /**
     * @param {URL} url
     * @returns {Promise<void>}
     */
    async route(url) {
        /** @type {URL} */
        let contentUrl = this.urlMapper(url);
        /** @type {Response} */
        let response = await fetch(contentUrl.pathname + contentUrl.search);
        if (response.ok) {
            /** @type {string} */
            let content = await response.text();
            this.anchor.innerHTML = content;
            /** @type {RouteState} */
            let state = {
                content: content,
            };
            history.pushState(state, '', url);
            await this.callback();
        }
    }
}
