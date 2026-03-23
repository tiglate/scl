function __getFormIdFromDataSet(caller) {
    const formId = caller.dataset.formId;
    if (!formId) {
        console.error("Error: Form ID not found in data-form-id attribute.");
        return null;
    }
    const form = document.getElementById(formId);
    if (form?.tagName.toLowerCase() !== 'form') {
        console.error("Error: Element with ID '" + formId + "' is not a form or does not exist.");
        return null;
    }
    return form;
}

globalThis.resetForm = function(caller) {
    const form = __getFormIdFromDataSet(caller);
    if (form != null) {
        form.reset();
    }
};

globalThis.submitForm = function(caller) {
    const form = __getFormIdFromDataSet(caller);
    if (form != null) {
        form.dispatchEvent(new Event('submit', { bubbles: true, cancelable: true }));
    }
};

globalThis.clearForm = function() {
    const form = document.getElementById('searchForm');
    const inputElements = form.querySelectorAll('input, select, textarea');

    inputElements.forEach(input => {
        if (input.type === 'checkbox' || input.type === 'radio') {
            input.checked = false;
        } else if (input.tagName.toLowerCase() === 'select') {
            // Deselect all options first
            for (let option of input.options) {
                option.selected = false;
            }
            const emptyOption = input.querySelector('option[value=""]');
            if (emptyOption) {
                emptyOption.selected = true;
            } else {
                input.selectedIndex = -1;
            }
        } else if (input.type !== 'button' && input.type !== 'submit' && input.type !== 'reset') {
            input.value = '';
        }
    });
    return false;
};