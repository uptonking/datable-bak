package player.data.jdbc;

import java.sql.ResultSet;

/**
 * 结果集处理器接口
 */
public interface ResultSetHandler {

    /**
     * 结果集处理方法
     *
     * @param rs 查询结果集
     * @return 单个对象
     */
    Object handler(ResultSet rs);
}
