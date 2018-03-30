package player.data.util;

import player.data.constant.DispatchActionConstant;

/**
 * 视图类，对一些客户端响应操作进行封装，其中包括跳转的路径 、展现到页面的数据、跳转方式
 **/
public class View {

    //跳转路径
    private String url;

    private String dispathAction = DispatchActionConstant.FORWARD;//跳转方式

    public View(String url) {
        this.url = url;
    }

    public View(String url, String name, Object value) {
        this.url = url;
        ViewData view = new ViewData();
        view.put(name, value);
    }


    public View(String url, String name, String dispathAction, Object value) {
        this.dispathAction = dispathAction;
        this.url = url;
        ViewData view = new ViewData();
        view.put(name, value);
    }


    public String getUrl() {
        return url;
    }


    public void setUrl(String url) {
        this.url = url;
    }

    public String getDispathAction() {
        return dispathAction;
    }

    public void setDispathAction(String dispathAction) {
        this.dispathAction = dispathAction;
    }
}
