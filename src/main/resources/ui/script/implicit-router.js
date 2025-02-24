/**
 * @callback RouteCallback
 * @param {URL} url
 * @returns {Promise<void>}
 */

export class ImplicitRouter {
    /**
     * @param {Element} anchor
     * @param {RouteCallback} callback
     */
    constructor(anchor, callback) {
        this.anchor = anchor;
        this.callback = callback;
    }

    /**
     * @param {URL} url
     */
    async route(url) {
        /** @type {Response} */
        let response = await fetch(url.pathname + url.search);
        if (response.ok) {
            this.anchor.innerHTML = await response.text();
            await this.callback(url);
        }
    }
}
