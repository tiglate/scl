class CounterpartyDocumentsManager {
    #template;
    #container;
    #documents;
    #index = 0;

    constructor(containerSelector = "#documents-container", templateSelector = "#document-template", existingDocuments = []) {
        this.#container = document.querySelector(containerSelector);
        this.#template = document.querySelector(templateSelector);
        this.#documents = existingDocuments;
    }

    init() {
        if (!this.#container || !this.#template) return;

        if (this.#documents) {
            this.#documents.forEach(doc => this.#renderDocumentRow(doc));
        }
        
        this.#bindAddButton();
    }

    #bindAddButton() {
        const addBtn = document.querySelector("#add-document-btn");
        if (addBtn) {
            addBtn.removeEventListener("click", this.#addDocumentHandler);
            addBtn.addEventListener("click", this.#addDocumentHandler);
        }
    }

    #addDocumentHandler = () => {
        this.#renderDocumentRow({ action: "new" });
    };

    markForDeletion(button) {
        const row = button.closest(".document-row");
        const actionField = row.querySelector("input[name$='.action']");
        if (actionField && actionField.value !== "new") {
            actionField.value = "delete";
            row.style.display = "none";
        } else {
            row.remove();
        }
    }

    #renderDocumentRow(doc) {
        let html = this.#template.innerHTML
            .replace(/__INDEX__/g, this.#index)
            .replace(/__ID__/g, doc.id ?? "")
            .replace(/__VALUE__/g, doc.value ?? "")
            .replace(/__EXPIRATION__/g, doc.expiration ?? "")
            .replace(/__ACTION__/g, doc.action ?? "update");

        const wrapper = document.createElement("div");
        wrapper.innerHTML = html.trim();

        const row = wrapper.firstElementChild;

        // Set selected documentTypeId (AFTER row is part of the DOM)
        const select = row.querySelector(`select[name="documents[${this.#index}].documentTypeId"]`);
        if (select && doc.documentTypeId) {
            select.value = String(doc.documentTypeId);
        }

        // Hook delete button
        const deleteBtn = row.querySelector(".btn-danger");
        if (deleteBtn) {
            deleteBtn.addEventListener("click", () => this.markForDeletion(deleteBtn));
        }

        this.#container.appendChild(row);
        this.#index++;
    }
    
    validateAll() {
        let valid = true;

        const rows = this.#container.querySelectorAll(".document-row");
        rows.forEach(row => {
            const action = row.querySelector("input[name$='.action']")?.value;
            if (action === "delete") return; // skip deleted rows

            const valueInput = row.querySelector("input[name$='.value']");
            const typeSelect = row.querySelector("select[name$='.documentTypeId']");

            [valueInput, typeSelect].forEach(el => {
                el?.classList.remove("is-invalid");
            });

            if (!valueInput || valueInput.value.trim() === "") {
                valueInput?.classList.add("is-invalid");
                valid = false;
            }

            if (!typeSelect || !typeSelect.value) {
                typeSelect?.classList.add("is-invalid");
                valid = false;
            }
        });

        return valid;
    }
}
