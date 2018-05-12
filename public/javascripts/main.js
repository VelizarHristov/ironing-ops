const bindDeleteConfirmationDialog = () => {
    $(".delete-btn").click(() =>
        // The form is only submitted if the return value of confirm is true
        confirm("Are you sure?")
    );
};

$(() => {
    bindDeleteConfirmationDialog();
});
