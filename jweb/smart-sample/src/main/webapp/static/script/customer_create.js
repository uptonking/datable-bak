$(function() {
    var validator = new Validator();

    $('#customer_create_form').ajaxForm({
        type: 'post',
        url: '/customer/create',
        beforeSubmit: function() {
            return validator.required('customer_create_form');
        },
        success: function(result) {
            if (result.success) {
                location.href = '/customer';
            }
        }
    });

    $('#cancel').click(function() {
        location.href = '/customer';
    });
});