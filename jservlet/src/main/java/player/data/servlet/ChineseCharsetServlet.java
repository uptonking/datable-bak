package player.data.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

public class ChineseCharsetServlet extends HttpServlet {

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //不设置ctype，默认为 text/plain
        response.setContentType("text/html;charset=GB2312 ");
//        response.setCharacterEncoding("GB2312");
//        response.setLocale(new Locale("zh", "CN"));

        PrintWriter out = response.getWriter();
        out.println("<s>this is the base of java web. 开发基础</s>");
    }


}
