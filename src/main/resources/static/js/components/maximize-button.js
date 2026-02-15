export function initMaximizeButtons() {
    const buttons = document.getElementsByClassName("toggle-maximize");
    for (const btn of buttons) {
        const card = document.getElementById(btn.dataset.target);
        btn.addEventListener('click', function() {
            card.classList.toggle('card-maximize');
            if (card.classList.contains('card-maximize')) {
                btn.textContent = 'Restore';
                document.body.style.overflow = 'hidden';
            } else {
                btn.textContent = 'Maximize';
                document.body.style.overflow = '';
            }
        });
    }
}

document.addEventListener('DOMContentLoaded', initMaximizeButtons);
document.addEventListener('htmx:afterSwap', initMaximizeButtons);