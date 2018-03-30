package player.data.util;

import javax.servlet.http.HttpServletRequest;


/**
 * 需要发送到客户端显示的数据模型
 * request范围的数据存储类，当需要发送数据到客户端显示时，就可以将要显示的数据存储到ViewData类中
 */
public class ViewData {

    private HttpServletRequest request;

    public ViewData() {
        initRequest();
    }

    private void initRequest() {
        //从requestHodler中获取request对象
        this.request = WebContext.requestHodler.get();
    }

    public void put(String name, Object value) {
        this.request.setAttribute(name, value);
    }
}
