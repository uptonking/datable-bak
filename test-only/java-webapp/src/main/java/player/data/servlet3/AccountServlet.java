package player.data.servlet3;

import player.data.anno.WebServletx;
import player.data.service.AccountService;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServletx("/account/test")
public class AccountServlet extends HttpServlet {


    public void doGet(HttpServletRequest request, HttpServletResponse response) {

        AccountService service = new AccountService();
        try {
            service.transfer(1, 2, 100);
        } catch (SQLException e) {
            e.printStackTrace();
            //注意：调用service层的方法出异常之后，继续将异常抛出，这样在TransactionFilter就能捕获到抛出的异常，继而执行事务回滚操作
            throw new RuntimeException(e);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        doGet(request, response);
    }


}
