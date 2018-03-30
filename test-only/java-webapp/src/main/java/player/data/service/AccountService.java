package player.data.service;


import player.data.dao.AccountDao;
import player.data.entity.Account;

import java.sql.SQLException;

public class AccountService {

    /**
     * 在业务层处理两个账户之间的转账问题
     */
    public void transfer(int sourceid, int tartgetid, float money) throws SQLException {
        AccountDao dao = new AccountDao();
        Account source = dao.find(sourceid);
        Account target = dao.find(tartgetid);
        System.out.println("====AccountService的对象： " + source + " -> " + target);

        source.setMoney(source.getMoney() - money);
        target.setMoney(target.getMoney() + money);
        dao.update(source);
        // 模拟程序出现异常让事务回滚
//        int x = 1 / 0;
        dao.update(target);

        System.out.println("====AccountService执行完毕");


    }

}
