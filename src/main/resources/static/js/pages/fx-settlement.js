import {SettlementGrid} from "../modules/settlement-grid.js";
import {SettlementWorkflow} from "../modules/settlement-workflow.js";

/**
 * SettlementPage Class
 * Main orchestrator for the FX Settlement view.
 */
class SettlementPage {
    constructor() {
        this.startDateInput = document.getElementById('startDateEdit');
        this.endDateInput = document.getElementById('endDateEdit');
        this.loader = document.getElementById('globalLoader');
        this.grid = null;
        this.workflow = null;
    }

    /**
     * Toggles the global loading screen
     */
    toggleLoader(show) {
        if (show) {
            this.loader.classList.remove('d-none');
            this.loader.style.display = 'flex';
        } else {
            this.loader.classList.add('d-none');
            this.loader.style.display = 'none';
        }
    }

    async init() {
        this.toggleLoader(true); // Start loading

        try {
            await this.initializeDates();

            this.grid = new SettlementGrid('fxSettlementTable', 'workflowRefreshButton', this);

            this.workflow = new SettlementWorkflow('workflowModal', 'fxSettlementTable', this.grid, () => {
                this.grid.refresh();
            });

            this.workflow.init();
        } finally {
            this.toggleLoader(false); // End loading
        }
    }

    async initializeDates() {
        const response = await fetch('/api/v1/fxSettlements/lastTradeDate');
        const lastDate = await response.text();
        this.startDateInput.value = lastDate?.toString().replaceAll('"', '');
        this.endDateInput.value = new Date().toISOString().split('T')[0];
    }
}

document.addEventListener('DOMContentLoaded', () => {
    if (!document.getElementById('fxSettlementTable')) return;
    console.log("Initializing FX Settlement Workflow");
    const page = new SettlementPage();
    page.init().then(() => console.log("FX Settlement Workflow initialized")).catch(console.error);
});