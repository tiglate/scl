/**
 * SettlementGrid Class
 * Encapsulates DataTables logic and AJAX data fetching.
 */
export class SettlementGrid {
    constructor(tableId, refreshBtnId, pageInstance) {
        this.tableId = tableId;
        this.refreshBtn = document.getElementById(refreshBtnId);
        this.statusEl = document.getElementById('syncStatus');
        this.errorBanner = document.getElementById('serverErrorBanner');
        this.page = pageInstance;
        this.table = null;
        this.isUserBusy = false;
        this.init();
    }

    init() {
        this.table = $(`#${this.tableId}`).DataTable({
            pageLength: 50,
            ajax: {
                url: '/api/v1/fxSettlements/steps',
                dataSrc: '',
                data: (d) => {
                    d.startDate = document.getElementById('startDateEdit').value;
                    d.endDate = document.getElementById('endDateEdit').value;
                },
                error: (xhr, error, thrown) => {
                    this.handleServerError(xhr.status);
                }
            },
            drawCallback: () => {
                this.updateSyncStatus();
            },
            columns: [
                { data: 'counterparty', render: (data, type, row) => `<a href="/counterparties/view/${row.idCounterparty}" target="_blank">${data}</a>` },
                { data: 'investorManager' },
                { data: 'contractId', render: (data, type, row) => `<a href="/fxTrades/view/${row.idFxTrade}" target="_blank">${data}</a>` },
                { data: 'currency', className: 'text-center' },
                { data: 'tradeType' },
                { data: 'g10Amount', className: 'text-end', render: (data) => this.formatCurrency(data) },
                { data: 'brlAmount', className: 'text-end', render: (data) => this.formatCurrency(data) },
                { data: 'tradeDate' },
                { data: 'beneficiary' },
                { data: 'instruction', className: 'text-center', render: (data) => this.renderWorkflowBtn(data, 'INS') },
                { data: 'g10', className: 'text-center', render: (data) => this.renderWorkflowBtn(data, 'G10') },
                { data: 'brl', className: 'text-center', render: (data) => this.renderWorkflowBtn(data, 'BRL') },
                { data: 'ion', className: 'text-center', render: (data) => this.renderWorkflowBtn(data, 'ION') }
            ],
            createdRow: (row, data) => $(row).attr('data-fx-trade-id', data.idFxTrade)
        });

        // Detect when the user is interacting with the grid to prevent it from refreshing and annoying the person
        //const container = document.getElementById(this.tableId).closest('.table-responsive');
        //container.addEventListener('mouseenter', () => this.isUserBusy = true);
        //container.addEventListener('mouseleave', () => this.isUserBusy = false);

        this.refreshBtn.addEventListener('click', () => this.refresh(true));
        this.startAutoRefresh();
    }

    startAutoRefresh() {
        setInterval(() => {
            const isModalOpen = document.querySelector('.modal.show') !== null;
            if (!this.isUserBusy && !isModalOpen) {
                this.refresh(false);
            }
        }, 30000);
    }

    refresh(showLoader = true) {
        if (!this.table) return;
        if (showLoader) this.page.toggleLoader(true);

        this.table.ajax.reload(() => {
            if (showLoader) this.page.toggleLoader(false);
            this.hideServerError(); // Se carregou, esconde o erro
        }, false);
    }

    updateSyncStatus() {
        const now = new Date();
        const timeStr = now.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', second: '2-digit' });
        this.statusEl.innerHTML = `<i class="bi bi-check-all text-success me-1"></i> Last updated at ${timeStr}`;
        this.statusEl.classList.replace('text-danger', 'text-muted');
    }

    handleServerError(status) {
        this.statusEl.innerHTML = `<i class="bi bi-x-circle-fill text-danger me-1"></i> Connection error (${status || 'Offline'})`;
        this.statusEl.classList.replace('text-muted', 'text-danger');
        this.errorBanner.classList.remove('d-none');
        this.page.toggleLoader(false);
    }

    hideServerError() {
        this.errorBanner.classList.add('d-none');
    }

    formatCurrency(value) {
        const color = value < 0 ? 'style="color: red"' : '';
        const formatted = new Intl.NumberFormat('en-US', { minimumFractionDigits: 2 }).format(value);
        return `<span ${color}>${formatted}</span>`;
    }

    renderWorkflowBtn(isDone, step) {
        const icon = isDone ? 'bi-check-square' : 'bi-square';
        return `<button type="button" class="btn btn-link p-0 btn-${step.toLowerCase()}-workflow" data-step="${step}">
                    <i class="bi ${icon}"></i>
                </button>`;
    }

    getRowData(rowElement) {
        return this.table.row(rowElement).data();
    }
}