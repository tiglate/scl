export class SettlementWorkflow {
    constructor(modalId, tableId, gridInstance, onSuccessCallback) {
        this.modalElement = document.getElementById(modalId);
        this.tableElement = document.getElementById(tableId);
        this.grid = gridInstance;
        this.onSuccess = onSuccessCallback;
        this.form = document.getElementById('workflowForm');
        this.bsModal = bootstrap.Modal.getOrCreateInstance(this.modalElement);

        this.btnConfirm = this.modalElement.querySelector('.btn-success');
        this.btnReject = this.modalElement.querySelector('.btn-outline-danger');

        this.fields = {
            counterparty: '.detail-value:contains-label("Counterparty")',
            im: '.detail-value:contains-label("IM")',
            contract: '.detail-value:contains-label("Contract")',
            currency: '.detail-value:contains-label("Currency")',
            type: '.detail-value:contains-label("Type")',
            g10Amt: '.detail-value:contains-label("G10 AMT")',
            brlAmt: '.detail-value:contains-label("BRL AMT")',
            tradeDate: '.detail-value:contains-label("Trade Date")',
            beneficiary: '.detail-value:contains-label("Beneficiary")'
        };
    }

    init() {
        this.tableElement.addEventListener('click', (e) => {
            const btn = e.target.closest('button[data-step]');
            if (btn) {
                const row = btn.closest('tr');
                this.handleWorkflowClick(row, btn);
            }
        });

        this.btnConfirm.addEventListener('click', () => this.submitWorkflow('SET'));
        this.btnReject.addEventListener('click', () => this.submitWorkflow('UNSET'));
    }

    async handleWorkflowClick(row, btn) {
        const step = btn.dataset.step;
        const data = this.grid.getRowData(row);
        const isCompleted = btn.querySelector('i').classList.contains('bi-check-square-fill');

        const modalData = {
            id: data.idFxTrade,
            counterparty: data.counterparty,
            im: data.investorManager,
            contract: data.contractId,
            currency: data.currency,
            type: data.tradeType,
            g10Amt: data.g10Amount.toString(),
            brlAmt: data.brlAmount.toString(),
            tradeDate: data.tradeDate,
            beneficiary: data.beneficiary
        };

        document.getElementById('currentStep').value = step;
        document.getElementById('fxTradeId').value = data.idFxTrade;

        this.syncStepperState(row, step);
        this.populateModal(modalData);

        this.form.reset();

        if (isCompleted) {
            await this.loadExistingStepData(data.id, step);
            this.toggleUIState(true); // Read-only / Delete mode
        } else {
            this.toggleUIState(false); // Editable / Confirm mode
        }

        this.resetButtons();
        this.bsModal.show();
    }

    async loadExistingStepData(id, step) {
        try {
            const response = await fetch(`/api/v1/fxSettlements/view?fxSettlementId=${id}&step=${step}`);
            if (response.ok) {
                const details = await response.json();

                // Populate the comment
                document.getElementById('comments').value = details.comments || '';

                // If there's a file, show a link (You might need a placeholder div in your HTML for this)
                this.renderExistingFile(details.fileName, details.fileId);

                const formattedDate = new Date(details.timestamp).toLocaleString('en-US', {
                    month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit', second: '2-digit' });

                // Update modal title to reflect audit mode
                this.modalElement.querySelector('.modal-title').innerHTML =
                    `<i class="bi bi-info-circle me-2"></i> Step Details (by ${details.userName} on ${formattedDate})`;
            }
        } catch (error) {
            console.error("Failed to fetch step details", error);
        }
    }

    /**
     * Synchronizes the modal stepper with the current row state.
     * @param {HTMLElement} row - The active table row.
     * @param {string} activeStep - The step code clicked by the user.
     */
    syncStepperState(row, activeStep) {
        const stepsInModal = this.modalElement.querySelectorAll('.step');

        stepsInModal.forEach(stepDiv => {
            const stepId = stepDiv.dataset.stepId;
            const iconContainer = stepDiv.querySelector('.step-icon');

            // 1. Reset classes
            stepDiv.classList.remove('active');
            iconContainer.innerHTML = ''; // Clear previous icons

            // 2. Find the corresponding button in the table row to check status
            const tableBtn = row.querySelector(`button[data-step="${stepId}"]`);
            const btnIcon = tableBtn ? tableBtn.querySelector('i') : null;

            if (btnIcon) {
                // If it contains bi-check-square, it means it's already completed
                if (btnIcon.classList.contains('bi-check-square')) {
                    iconContainer.innerHTML = '<i class="bi bi-check"></i>';
                }
            }

            // 3. Highlight the current active step
            if (stepId === activeStep) {
                stepDiv.classList.add('active');
                // If not completed yet, we can show the current step ID or icon
                if (iconContainer.innerHTML === '') {
                    iconContainer.innerText = stepId.charAt(0);
                }
            }
        });
    }

    toggleUIState(isCompleted) {
        const comments = document.getElementById('comments');
        const fileInput = document.getElementById('fileUpload');

        if (isCompleted) {
            // Delete Mode
            comments.readOnly = true;
            fileInput.classList.add('d-none');
            this.btnConfirm.classList.add('d-none');
            this.btnReject.classList.remove('d-none');
            this.btnReject.innerHTML = `<i class="bi bi-trash me-1"></i> DELETE STEP`;
        } else {
            // Confirm Mode
            comments.readOnly = false;
            fileInput.classList.remove('d-none');
            this.btnConfirm.classList.remove('d-none');
            this.btnReject.classList.add('d-none'); // Hide Reject/Delete button
            this.btnConfirm.innerHTML = `<i class="bi bi-check2-circle me-1"></i> CONFIRM`;
            this.modalElement.querySelector('.modal-title').innerText = "Confirm Step";
            this.removeExistingFileLink();
        }
    }

    renderExistingFile(name, id) {
        this.removeExistingFileLink();
        if (!name) return;
        const html = `
            <div id="existingFileLink" class="mt-2 p-2 bg-light border rounded small">
                <i class="bi bi-paperclip me-2"></i>
                <a href="/api/v1/download/${id}" target="_blank" class="text-decoration-none">${name}</a>
            </div>`;
        document.getElementById('fileUpload').insertAdjacentHTML('afterend', html);
    }

    removeExistingFileLink() {
        const link = document.getElementById('existingFileLink');
        if (link) link.remove();
    }

    async submitWorkflow(action) {
        this.setLoadingState(true, action);
        const formData = this.#getFormData(action);
        try {
            const response = await fetch('/api/v1/fxSettlements/step', { method: 'POST', body: formData });
            if (response.ok) {
                this.bsModal.hide();
                this.form.reset();
                if (this.onSuccess) this.onSuccess();
            } else {
                alert("Error on processing this transaction. Contact local IT.");
                console.log(response.statusText);
            }
        } catch (error) {
            alert("Error on processing this transaction.");
            console.error(error);
        } finally {
            this.setLoadingState(false);
        }
    }

    #getFormData(action) {
        const formData = new FormData();

        const file = this.form.elements['fileUpload'].files[0];
        formData.append('file', file);

        const details = {'action': action};

        for (const el of this.form.elements) {
            if (el.name === 'fileUpload') continue;
            details[el.name] = el.value;
        }

        formData.append('details', new Blob([JSON.stringify(details)], {
            type: "application/json"
        }));
        return formData;
    }

    populateModal(data) {
        this.setDetailValue('Counterparty', data.counterparty);
        this.setDetailValue('IM', data.im);
        this.setDetailValue('Contract', data.contract);
        this.setDetailValue('Currency', data.currency);
        this.setDetailValue('Type', data.type);
        this.setDetailValue('Trade Date', data.tradeDate);
        this.setDetailValue('Beneficiary', data.beneficiary);

        this.setFormattedAmount('G10 AMT', data.g10Amt);
        this.setFormattedAmount('BRL AMT', data.brlAmt);
    }

    setDetailValue(label, value) {
        const elements = this.modalElement.querySelectorAll('.col-md-3, .col-md-4, .col-md-6');
        elements.forEach(el => {
            const labelSpan = el.querySelector('.detail-label');
            if (labelSpan && labelSpan.innerText.includes(label)) {
                el.querySelector('.detail-value').innerText = value;
            }
        });
    }

    setFormattedAmount(label, value) {
        const elements = this.modalElement.querySelectorAll('.col-md-4');
        elements.forEach(el => {
            const labelSpan = el.querySelector('.detail-label');
            if (labelSpan && labelSpan.innerText.includes(label)) {
                const valueEl = el.querySelector('.detail-value');
                valueEl.innerText = value;

                valueEl.classList.remove('text-success', 'text-danger');

                const numericValue = parseFloat(value.replace(/\./g, '').replace(',', '.'));

                if (numericValue < 0) {
                    valueEl.classList.add('text-danger');
                } else {
                    valueEl.classList.add('text-success');
                }
            }
        });
    }

    setLoadingState(isLoading, action = null) {
        // 1. Disable all action buttons
        const allButtons = this.modalElement.querySelectorAll('button');
        allButtons.forEach(btn => btn.disabled = isLoading);

        // 2. Lock form inputs (Textarea and File Upload)
        const inputs = this.form.querySelectorAll('textarea, input');
        inputs.forEach(input => {
            if (input.tagName === 'TEXTAREA') {
                input.readOnly = isLoading;
            } else {
                input.disabled = isLoading;
            }
        });

        // 3. Prevent Modal from closing via background click or ESC key during loading
        this.bsModal._config.backdrop = isLoading ? 'static' : true;
        this.bsModal._config.keyboard = !isLoading;

        // 4. Visual feedback for the active button
        if (isLoading) {
            const spinner = `<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Processing...`;
            if (action === 'SET') {
                this.btnConfirm.innerHTML = spinner;
            } else if (action === 'UNSET') {
                this.btnReject.innerHTML = spinner;
            }
        } else {
            // Restore original button labels
            this.btnConfirm.innerHTML = `<i class="bi bi-check2-circle me-1"></i> CONFIRM`;
            this.btnReject.innerHTML = `<i class="bi bi-x-circle me-1"></i> DELETE STEP`;
        }
    }

    resetButtons() {
        this.setLoadingState(false);
    }
}