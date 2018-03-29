$(function() {
    $('.ext-customer-delete').click(function() {
        var $tr = $(this).closest('tr');
        var customerId = $tr.data('id');
        var customerName = $tr.data('name');
        if (confirm('Do you want to delete customer [' + customerName + ']?')) {
            $.ajax({
                type: 'delete',
                url: '/customer/delete/' + customerId,
                success: function(result) {
                    if (result.success) {
                        $tr.remove();
                    }
                }
            });
        }
    });
});