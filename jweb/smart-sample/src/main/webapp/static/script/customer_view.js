$(function() {
    $('#edit').click(function() {
        var customerId = $('#id').val();
        location.href = '/customer/edit/' + customerId;
    });

    $('#cancel').click(function() {
        location.href = '/customer';
    });
});