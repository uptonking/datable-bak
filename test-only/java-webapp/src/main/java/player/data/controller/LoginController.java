package player.data.controller;

import player.data.anno.Controller;
import player.data.anno.RequestMapping;
import player.data.util.View;

/**
 * 登录 控制器
 *
 * @author yaoo on 3/30/18
 */
@Controller
public class LoginController {

    //使用RequestMapping注解指明forward1方法的访问路径
    @RequestMapping("login/login2")
    public View forward2() {
        //执行完forward1方法之后返回的视图
        return new View("/jsp/login2.jsp");
    }

    //使用RequestMapping注解指明forward2方法的访问路径
    @RequestMapping("login/login3")
    public View forward3() {
        //执行完forward2方法之后返回的视图
        return new View("/jsp/login3.jsp");
    }
}
