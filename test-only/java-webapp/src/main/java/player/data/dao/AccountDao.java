package player.data.dao;


import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import player.data.entity.Account;
import player.data.transaction.ConnectionContext;

import java.sql.SQLException;

/*
create table account(
    id int primary key auto_increment,
    name varchar(40),
    money float
)character set utf8 collate utf8_general_ci;

insert into account(name,money) values('A',1000);
insert into account(name,money) values('B',1000);
insert into account(name,money) values('C',1000);
*/

/**
 * 针对Account对象的CRUD
 */
public class AccountDao {

    public void update(Account account) throws SQLException {

        QueryRunner qr = new QueryRunner();
        String sql = "update account set name=?,money=? where id=?";
        Object params[] = {account.getName(), account.getMoney(), account.getId()};
        //ConnectionContext.getInstance().getConnection()获取当前线程中的Connection对象
        qr.update(ConnectionContext.getInstance().getConnection(), sql, params);

    }

    public Account find(int id) throws SQLException {
        QueryRunner qr = new QueryRunner();
        String sql = "select * from account where id=?";
        //ConnectionContext.getInstance().getConnection()获取当前线程中的Connection对象
        return (Account) qr.query(ConnectionContext.getInstance().getConnection(), sql, id, new BeanHandler(Account.class));
    }
}
