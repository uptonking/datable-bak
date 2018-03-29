$(function() {
    // 清空会话存储
    window.sessionStorage.removeItem('product.id');

    // 定义对象
    var productTable = new ProductTable();

    // 查看
    $(document).on('click', '.ext-product-view', function() {
        var $tr = $(this).closest('tr');
        var productId = $tr.data('id');
        window.sessionStorage.setItem('product.id', productId);
        location.href = 'product_view.html';
    });

    // 编辑
    $(document).on('click', '.ext-product-edit', function() {
        var $tr = $(this).closest('tr');
        var productId = $tr.data('id');
        window.sessionStorage.setItem('product.id', productId);
        location.href = 'product_edit.html';
    });

    // 删除
    $(document).on('click', '.ext-product-delete', function() {
        var $tr = $(this).closest('tr');
        var productId = $tr.data('id');
        var productName = $tr.data('name');
        if (confirm('Do you want to delete product [' + productName + ']?')) {
            $.ajax({
                type: 'delete',
                url: '/product/delete/' + productId,
                success: function(result) {
                    if (result.success) {
                        $tr.remove();
                    }
                }
            });
        }
        return false;
    });

    // 查询
    $('#product_search_form').submit(function() {
        productTable.load(1);
        return false;
    });
});

var ProductTable = function() {
    // 定义对象
    var pager = new Pager('product_pager', this);
    var renderer = new Renderer();

    // 初始化
    (function() {
        $.ajax({
            type: 'get',
            url: '/product',
            success: function(result) {
                render(result);
            }
        });
    })();

    // 加载
    this.load = function(pageNumber) {
        $.ajax({
            type: 'post',
            url: '/product/search',
            data: {
                'pageNumber': pageNumber,
                'pageSize': $.trim($('#product_pager').find('.ext-pager-ps').val()),
                'queryString': 'productName:' + $.trim($('#product_name').val())
            },
            success: function(result) {
                render(result);
            }
        });
    };

    // 渲染
    var render = function(result) {
        if (result.success) {
            var data = result.data;
            renderTable(data);
            renderPager(data);
        }
    };

    // 渲染表格
    var renderTable = function(data) {
        var template = $('#product_table_template').html();
        var tableHTML = '';
        $.each(data.recordList, function(i, product) {
            tableHTML += renderer.render(template, product);
        });
        $('#product_table').find('tbody').html(tableHTML);
    };

    // 渲染分页
    var renderPager = function(data) {
        pager.render(data);
    };
};