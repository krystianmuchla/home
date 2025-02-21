/**
 * @callback PostRouteCallback
 * @param {URL} url
 * @returns {Promise<void>}
 */

export class InternalRouter {
    /**
     * @param {Element} anchor
     * @param {PostRouteCallback} postCallback
     */
    constructor(anchor, postCallback) {
        this.anchor = anchor;
        this.postCallback = postCallback;
    }

    /**
     * @param {URL} url
     */
    async route(url) {
        /** @type {Response} */
        let response = await fetch(url.pathname + url.search);
        if (response.ok) {
            this.anchor.innerHTML = await response.text();
            await this.postCallback(url);
        }
    }
}
