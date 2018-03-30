package player.data.util;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * WebContext主要是用来存储当前线程中的HttpServletRequest和HttpServletResponse
 * <p>
 * 可以在作为Controller的普通java类中获取当前请求的request、response或者session相关请求的实例变量，且线程间互不干扰
 **/
public class WebContext {

    public static ThreadLocal<HttpServletRequest> requestHodler = new ThreadLocal<>();
    public static ThreadLocal<HttpServletResponse> responseHodler = new ThreadLocal<>();

    public HttpServletRequest getRequest() {
        return requestHodler.get();
    }

    public HttpSession getSession() {
        return requestHodler.get().getSession();
    }

    public ServletContext getServletContext() {
        return requestHodler.get().getSession().getServletContext();
    }

    public HttpServletResponse getResponse() {
        return responseHodler.get();
    }

}
