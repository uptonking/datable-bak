$(function() {
    var validator = new Validator();

    $('#cancel').click(function() {
        location.href = 'product.html';
    });

    $('#product_create_form').ajaxForm({
        type: 'post',
        url: '/product/create',
        beforeSubmit: function() {
            return validator.required('product_create_form');
        },
        success: function(result) {
            if (result.success) {
                location.href = 'product.html';
            }
        }
    });
});