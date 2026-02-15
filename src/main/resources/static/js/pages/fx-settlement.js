export function initDataTable() {
    const table = document.getElementById("fxSettlementTable");
    if (!table) {
        return;
    }
    if (table.classList.contains("dataTable")) {
        return;
    }
    new DataTable("#fxSettlementTable", { paging: false });
    console.log("DataTable initialized");
}
document.addEventListener("htmx:load", initDataTable);