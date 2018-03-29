$(function() {
    $('#upload_form').ajaxForm({
        type: 'post',
        url: '/upload.do',
        beforeSubmit: function() {
            var file = $('#file').val();
            if (file == '') {
                alert('Please choose a JPG file!');
                return false;
            }
            if (file.substring(file.lastIndexOf('.') + 1).toLowerCase() != 'jpg') {
                alert('This file is not JPG format!');
                return false;
            }
            return true;
        },
        success: function(result) {
            if (result.success) {
                var data = result.data;
                if (data) {
                    var html = '';
                    html += '<div>File Name: ' + data.fileName + '</div>';
                    html += '<div>File Type: ' + data.fileType + '</div>';
                    html += '<div>File Size: ' + data.fileSize + '</div>';
                    html += '<img src="../upload/product/' + data.fileName + '" style="height: 400px;"/>';
                    $('#console').html(html);
                }
            }
        }
    });
});