// Logic to show the toast when clicking the notification button
function initToasts() {
    const toastTrigger = document.getElementById('notifBtn')
    const toastLiveExample = document.getElementById('notifToast')

    if (toastTrigger) {
        const toastBootstrap = bootstrap.Toast.getOrCreateInstance(toastLiveExample)
        toastTrigger.addEventListener('click', () => {
            toastBootstrap.show()
        })
    }
}
document.addEventListener("htmx:load", initToasts);