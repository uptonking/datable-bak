package player.data.util;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.dbutils.ResultSetHandler;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class JdbcUtil {

    private static String driver = null;
    private static String url = null;
    private static String username = null;
    private static String password = null;
    //使用ThreadLocal存储当前线程中的Connection对象
    private static ThreadLocal<Connection> threadLocal = new ThreadLocal<>();
    private static ComboPooledDataSource ds = null;


    static {
        try {
            //读取db.properties文件中的数据库连接信息
            InputStream in = JdbcUtil.class.getClassLoader().getResourceAsStream("db.properties");
            Properties prop = new Properties();
            prop.load(in);

            //获取数据库连接驱动
            driver = prop.getProperty("driver");
            //获取数据库连接URL地址
            url = prop.getProperty("url");
            //获取数据库连接用户名
            username = prop.getProperty("username");
            //获取数据库连接密码
            password = prop.getProperty("password");

            //加载数据库驱动
            Class.forName(driver);

            //通过读取C3P0的xml配置文件创建数据源，C3P0的xml配置文件c3p0-config.xml必须放在src目录下
            //使用C3P0的命名配置来创建数据源
            ds = new ComboPooledDataSource();
            ds.setDriverClass(driver);
            ds.setJdbcUrl(url);
            ds.setUser(username);
            ds.setPassword(password);
            ds.setInitialPoolSize(10);
            ds.setMinPoolSize(5);
            ds.setMaxPoolSize(20);


        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * 获取数据库连接对象
     *
     * @return Connection数据库连接对象
     */
    public static Connection getConnection() throws SQLException {
//        return DriverManager.getConnection(url, username, password);
        //从当前线程中获取Connection
        Connection conn = threadLocal.get();
        if (conn == null) {
            //从数据源中获取数据库连接
            conn = getDataSource().getConnection();
            //将conn绑定到当前线程
            threadLocal.set(conn);
        }
        return conn;
    }

    /**
     * 释放资源
     * 要释放的资源包括Connection数据库连接对象，负责执行SQL命令的Statement对象，存储查询结果的ResultSet对象
     */
    public static void release(Connection conn, Statement st, ResultSet rs) {
        if (rs != null) {
            try {
                //关闭存储查询结果的ResultSet对象
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            rs = null;
        }
        if (st != null) {
            try {
                //关闭负责执行SQL命令的Statement对象
                st.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (conn != null) {
            try {
                //关闭Connection数据库连接对象
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 万能更新
     * 所有实体的CUD操作代码基本相同，仅仅发送给数据库的SQL语句不同而已，
     * 因此可以把CUD操作的所有相同代码抽取到工具类的一个update方法中，并定义参数接收变化的SQL语句
     *
     * @param sql    要执行的SQL
     * @param params 执行SQL时使用的参数
     */
    public static void update(String sql, Object params[]) throws SQLException {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            st = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                st.setObject(i + 1, params[i]);
            }
            st.executeUpdate();

        } finally {
            release(conn, st, rs);
        }
    }

    /**
     * 通用查询
     * 实体的查询操作，除SQL语句不同之外，根据操作的实体不同，对ResultSet的映射也各不相同，
     * 因此可义一个query方法，除以参数形式接收变化的SQL语句外，可以使用策略模式由qurey方法的调用者决定如何把ResultSet中的数据映射到实体对象中。
     *
     * @param sql    要执行的SQL
     * @param params 执行SQL时使用的参数
     * @param rsh    查询返回的结果集处理器
     * @return 单条记录相应的对象
     */
    public static Object query(String sql, Object params[], ResultSetHandler rsh) throws SQLException {

        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            st = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                st.setObject(i + 1, params[i]);
            }
            rs = st.executeQuery();
            /**
             * 对于查询返回的结果集处理使用到了策略模式，
             * 在设计query方法时，query方法事先是无法知道用户对返回的查询结果集如何进行处理的，即不知道结果集的处理策略，
             * 那么这个结果集的处理策略就让用户自己提供，query方法内部就调用用户提交的结果集处理策略进行处理
             * 为了能够让用户提供结果集的处理策略，需要对用户暴露出一个结果集处理接口ResultSetHandler
             * 用户只要实现了ResultSetHandler接口，那么query方法内部就知道用户要如何处理结果集了
             */
            return rsh.handle(rs);

        } finally {
            release(conn, st, rs);
        }
    }

    public static DataSource getDataSource() {

        return ds;
    }

    /**
     * 开启事务
     */
    public static void startTransaction() {
        try {
            Connection conn = threadLocal.get();
            if (conn == null) {
                conn = getConnection();
                //把 conn绑定到当前线程上
                threadLocal.set(conn);
            }
            //开启事务
            conn.setAutoCommit(false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 回滚事务
     */
    public static void rollback() {
        try {
            //从当前线程中获取Connection
            Connection conn = threadLocal.get();
            if (conn != null) {
                //回滚事务
                conn.rollback();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 提交事务
     */
    public static void commit() {
        try {
            //从当前线程中获取Connection
            Connection conn = threadLocal.get();
            if (conn != null) {
                //提交事务
                conn.commit();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 关闭数据库连接
     * 注意，并不是真的关闭，而是把连接还给数据库连接池
     */
    public static void close() {
        try {
            //从当前线程中获取Connection
            Connection conn = threadLocal.get();
            if (conn != null) {
                conn.close();
                //解除当前线程上绑定conn
                threadLocal.remove();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
