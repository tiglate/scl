export function initDataTable() {
    const table = document.getElementById("fxSettlementTable");
    if (!table) {
        return;
    }
    if (table.classList.contains("dataTable")) {
        return;
    }
    new DataTable("#fxSettlementTable", { paging: false });
}
document.addEventListener("htmx:load", initDataTable);

class SettlementWorkflow {
    constructor(modalId, tableId) {
        this.modalElement = document.getElementById(modalId);
        this.tableElement = document.getElementById(tableId);
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
                this.handleWorkflowClick(row, btn.dataset.step);
            }
        });

        this.btnConfirm.addEventListener('click', () => this.submitWorkflow('APPROVED'));
        this.btnReject.addEventListener('click', () => this.submitWorkflow('REJECTED'));
    }

    handleWorkflowClick(row, step) {
        const data = {
            id: row.dataset.fxTradeId,
            counterparty: row.cells[0].innerText.trim(),
            im: row.cells[1].innerText.trim(),
            contract: row.cells[2].innerText.trim(),
            currency: row.cells[3].innerText.trim(),
            type: row.cells[4].innerText.trim(),
            g10Amt: row.cells[5].innerText.trim(),
            brlAmt: row.cells[6].innerText.trim(),
            tradeDate: row.cells[7].innerText.trim(),
            beneficiary: row.cells[8].innerText.trim()
        };

        // Set hidden fields
        document.getElementById('currentStep').value = step;
        document.getElementById('fxTradeId').value = data.id;

        // Synchronize Stepper visual state based on table row icons
        this.syncStepperState(row, step);

        this.populateModal(data);
        this.resetButtons()
        this.bsModal.show();
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

    async submitWorkflow(action) {
        this.setLoadingState(true, action);

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

        try {
            const response = await fetch('/api/v1/fxSettlements/step', { method: 'POST', body: formData });
            alert(await response.text());

            this.bsModal.hide();
            this.form.reset();
        } catch (error) {
            alert("Erro ao processar operação.");
        } finally {
            this.setLoadingState(false);
        }
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
            if (action === 'APPROVED') {
                this.btnConfirm.innerHTML = spinner;
            } else if (action === 'REJECTED') {
                this.btnReject.innerHTML = spinner;
            }
        } else {
            // Restore original button labels
            this.btnConfirm.innerHTML = `<i class="bi bi-check2-circle me-1"></i> CONFIRM`;
            this.btnReject.innerHTML = `<i class="bi bi-x-circle me-1"></i> REJECT`;
        }
    }

    resetButtons() {
        this.setLoadingState(false);
    }
}

function initSettlementWorkflow() {
    if (!document.getElementById('fxSettlementTable')) return;
    console.log("Initializing FX Settlement Workflow");
    new SettlementWorkflow('workflowModal', 'fxSettlementTable').init();
}

document.addEventListener('htmx:afterSwap', initSettlementWorkflow);
document.addEventListener('DOMContentLoaded', initSettlementWorkflow);