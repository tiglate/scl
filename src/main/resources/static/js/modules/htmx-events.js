/**
 * Register an event at the document for the specified selector,
 * so events are still catch after DOM changes.
 */
export function handleEvent(eventType, selector, handler) {
    document.addEventListener(eventType, function (event) {
        if (event.target.matches(selector + ', ' + selector + ' *')) {
            handler.apply(event.target.closest(selector), arguments);
        }
    });
}

handleEvent('change', '.js-selectlinks', function () {
    htmx.ajax('get', this.value, document.body);
    history.pushState({htmx: true}, '', this.value);
});

handleEvent('click', '.js-file-delete', function (event) {
    const $fileDiv = event.target.parentElement;
    const $fileRow = $fileDiv.previousElementSibling;
    $fileRow.removeAttribute('disabled');
    $fileRow.classList.remove('d-none');
    $fileDiv.remove();
});