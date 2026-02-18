/**
 * SettlementGrid Class
 * Encapsulates DataTables logic and AJAX data fetching.
 */
export class SettlementGrid {
    constructor(tableId, refreshBtnId, pageInstance) {
        this.tableId = tableId;
        this.tableElement = document.getElementById(tableId);
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
                { data: 'ion', className: 'text-center', render: (data) => this.renderWorkflowBtn(data, 'ION') },
                { data: 'id', // Use the trade interaction ID
                    className: 'text-center',
                    orderable: false,
                    render: (data) => {
                        if (data > 0) {
                            return `<button type="button" class="btn btn-link p-0 text-info btn-view-history" title="View History"><i class="bi bi-chat-left-text-fill"></i></button>`;
                        }
                        return ''; // Hide if ID is negative
                    }
                }
            ],
            createdRow: (row, data) => $(row).attr('data-fx-trade-id', data.idFxTrade)
        });

        // Detect when the user is interacting with the grid to prevent it from refreshing and annoying the person
        //const container = document.getElementById(this.tableId).closest('.table-responsive');
        //container.addEventListener('mouseenter', () => this.isUserBusy = true);
        //container.addEventListener('mouseleave', () => this.isUserBusy = false);

        // Delegate click event for the history button
        this.tableElement.addEventListener('click', (e) => {
            const btn = e.target.closest('.btn-view-history');
            if (btn) {
                // Get the data object from the DataTables row
                const rowData = this.table.row(btn.closest('tr')).data();
                // Call the page controller to handle the modal logic
                this.showHistory(rowData.id);
            }
        });

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
        const icon = isDone ? 'bi-check-square-fill' : 'bi-square';
        return `<button type="button" class="btn btn-link p-0 btn-${step.toLowerCase()}-workflow" data-step="${step}">
                    <i class="bi ${icon}"></i>
                </button>`;
    }

    getRowData(rowElement) {
        return this.table.row(rowElement).data();
    }

    /**
     * Fetches and displays the interaction log in a chat-like format.
     */
    async showHistory(id) {
        const container = document.getElementById('historyChatContainer');
        container.innerHTML = '<div class="text-center p-5"><div class="spinner-border text-primary" role="status"></div></div>';

        const hModal = new bootstrap.Modal('#historyModal');
        hModal.show();

        try {
            const response = await fetch(`/api/v1/fxSettlements/history/${id}`);
            const logs = await response.json();

            container.innerHTML = ''; // Clear spinner

            logs.forEach(log => {
                const isSystem = log.userName === 'System';
                const action = log.action === 'SET' ? `<i class="bi bi-patch-check-fill text-success"></i>` : `<i class="bi bi-x-octagon-fill text-danger"></i>`;
                const formattedDate = new Date(log.timestamp).toLocaleString('en-US', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit', second: '2-digit' });
                const chatHtml = `
                <div class="d-flex flex-column ${isSystem ? 'align-items-center' : 'align-items-start'}">
                    <div class="d-flex align-items-center mb-1">
                        <span class="fw-bold small me-2">${log.userName}</span>
                        <span class="text-muted" style="font-size: 0.7rem;">${formattedDate}</span>
                    </div>
                    <div class="p-3 rounded-3 shadow-sm border" style="max-width: 85%; background: white;">
                        <div class="fw-bold text-primary mb-1 small">${action} Step: ${log.step.toUpperCase()}</div>
                        <div class="text-dark small mb-2">${log.comments || 'No comments provided.'}</div>
                        ${log.fileName ? `
                            <div class="mt-2 pt-2 border-top">
                                <a href="/api/v1/download/${log.fileId}" class="text-decoration-none d-flex align-items-center bg-light p-2 rounded">
                                    <i class="bi bi-file-earmark-pdf-fill text-danger fs-5 me-2"></i>
                                    <span class="small text-truncate">${log.fileName}</span>
                                    <i class="bi bi-download ms-auto text-primary"></i>
                                </a>
                            </div>
                        ` : ''}
                    </div>
                </div>
            `;
                container.insertAdjacentHTML('beforeend', chatHtml);
            });

            // Auto-scroll to the bottom
            container.scrollTop = container.scrollHeight;

        } catch (error) {
            container.innerHTML = '<div class="alert alert-danger m-3 small">Failed to load history.</div>';
        }
    }
}