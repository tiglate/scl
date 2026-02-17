/**
 * SettlementGrid Class
 * Encapsulates DataTables logic and AJAX data fetching.
 */
export class SettlementGrid {
    constructor(tableId, refreshBtnId) {
        this.tableId = tableId;
        this.refreshBtn = document.getElementById(refreshBtnId);
        this.table = null;
        this.init();
    }

    init() {
        this.table = $(`#${this.tableId}`).on('preXhr.dt', () => {
            if (window.pageInstance) {
                window.pageInstance.toggleLoader(true);
            }
        }).on('xhr.dt', () => {
            if (window.pageInstance) {
                window.pageInstance.toggleLoader(false);
            }
        }).DataTable({
            ajax: {
                url: '/api/v1/fxSettlements/steps',
                dataSrc: '',
                data: (d) => {
                    d.startDate = document.getElementById('startDateEdit').value;
                    d.endDate = document.getElementById('endDateEdit').value;
                }
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

        this.refreshBtn.addEventListener('click', () => this.refresh());
    }

    refresh() {
        if (this.table) this.table.ajax.reload(null, false); // false to stay on current page
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