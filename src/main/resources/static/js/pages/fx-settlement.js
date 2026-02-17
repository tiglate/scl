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
        this.grid = null;
        this.workflow = null;
    }

    async init() {
        await this.initializeDates();

        // Initialize Grid
        this.grid = new SettlementGrid('fxSettlementTable', 'workflowRefreshButton');

        // Initialize Workflow Popup and pass a callback to refresh the grid on success
        this.workflow = new SettlementWorkflow('workflowModal', 'fxSettlementTable', this.grid, () => {
            this.grid.refresh();
        });

        this.workflow.init();
    }

    async initializeDates() {
        try {
            // 2.1 Get the last trade date from REST
            const response = await fetch('/api/v1/fxSettlements/lastTradeDate');
            const lastDate = await response.text(); // Expected: "2026-02-17"
            this.startDateInput.value = lastDate.replace(/"/g, "");

            // 2.2 Set the end date as today (local time)
            const today = new Date().toISOString().split('T')[0];
            this.endDateInput.value = today;
        } catch (error) {
            console.error("Failed to initialize dates", error);
        }
    }
}

document.addEventListener('DOMContentLoaded', () => {
    if (!document.getElementById('fxSettlementTable')) return;
    console.log("Initializing FX Settlement Workflow");
    const page = new SettlementPage();
    page.init().then(() => console.log("FX Settlement Workflow initialized")).catch(console.error);
});