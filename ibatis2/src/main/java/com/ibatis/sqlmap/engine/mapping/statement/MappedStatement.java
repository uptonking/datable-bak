/*
 *  Copyright 2004 Clinton Begin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.ibatis.sqlmap.engine.mapping.statement;

import com.ibatis.common.jdbc.exception.NestedSQLException;
import com.ibatis.common.io.ReaderInputStream;

import com.ibatis.sqlmap.client.event.RowHandler;
import com.ibatis.sqlmap.client.*;
import com.ibatis.sqlmap.engine.execution.SqlExecutor;
import com.ibatis.sqlmap.engine.mapping.parameter.ParameterMap;
import com.ibatis.sqlmap.engine.mapping.result.ResultMap;
import com.ibatis.sqlmap.engine.mapping.sql.Sql;
import com.ibatis.sqlmap.engine.scope.ErrorContext;
import com.ibatis.sqlmap.engine.scope.StatementScope;
import com.ibatis.sqlmap.engine.transaction.Transaction;
import com.ibatis.sqlmap.engine.transaction.TransactionException;
import com.ibatis.sqlmap.engine.type.*;
import com.ibatis.sqlmap.engine.cache.*;
import com.ibatis.sqlmap.engine.impl.*;
import org.w3c.dom.Document;

import javax.xml.parsers.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.io.*;

//这是SQL Map中映射语句处理的核心包装类
public class MappedStatement {
  private String id; //当前MappedStatement对象的全局唯一内码
  private Integer resultSetType; //当前MappedStatement处理返回的resultSet的类型
  private Integer fetchSize; //当前MappedStatement为 JDBC 驱动程序提供关于需要更多行时应该从数据库获取的行数。
  private ResultMap resultMap; //当前MappedStatement对象拥有的输出结果对象
  private ParameterMap parameterMap; //当前MappedStatement对象拥有的输入参数对象
  private Class parameterClass; //当前MappedStatement对象拥有的输入参数的Class类型
  private Sql sql; //当前MappedStatement对象拥有的SQL语句对象
  private int baseCacheKey; // MappedStatement对象形成唯一缓存Hash码的最基本的内码
  private SqlMapClientImpl sqlMapClient; // MappedStatement对象对应的外部SqlMapClientImpl对象
  private Integer timeout; // 耗时标志
  private ResultMap[] additionalResultMaps = new ResultMap[0]; // 附加的输出结果对象集合
  private List executeListeners = new ArrayList(); // MappedStatement对象包含的缓存对象列表
  private String resource; //

  public StatementType getStatementType() {
    return StatementType.UNKNOWN;
  }

  //执行新增、修改和删除等操作
  public int executeUpdate(StatementScope statementScope, Transaction trans, Object parameterObject)
      throws SQLException {
    ErrorContext errorContext = statementScope.getErrorContext();
    errorContext.setActivity("preparing the mapped statement for execution");
    errorContext.setObjectId(this.getId());
    errorContext.setResource(this.getResource());

    statementScope.getSession().setCommitRequired(true);

    try {
    	//验证parameterObject是否正确
      parameterObject = validateParameter(parameterObject);

      //获取当前MappedStatement对象配置形成的Sql实例化对象
      Sql sql = getSql();

      //获取当前MappedStatement对象配置形成的ParameterMap实例化对象
      errorContext.setMoreInfo("Check the parameter map.");
      ParameterMap parameterMap = sql.getParameterMap(statementScope, parameterObject);

      //获取当前MappedStatement对象配置形成的ResultMap实例化对象
      errorContext.setMoreInfo("Check the result map.");
      ResultMap resultMap = sql.getResultMap(statementScope, parameterObject);

      //把ParameterMap对象和ResultMap对象赋值给状态变量statementScope
      statementScope.setResultMap(resultMap);
      statementScope.setParameterMap(parameterMap);

      int rows = 0;

      //获取ParameterMap对象中的ParameterMapping对象组
      errorContext.setMoreInfo("Check the parameter map.");
      Object[] parameters = parameterMap.getParameterObjectValues(statementScope, parameterObject);

      //从Sql实例化对象中获取字符串的Sql语句
      errorContext.setMoreInfo("Check the SQL statement.");
      String sqlString = sql.getSql(statementScope, parameterObject);

      //提交执行语句
      errorContext.setActivity("executing mapped statement");
      errorContext.setMoreInfo("Check the statement or the result map.");
      rows = sqlExecuteUpdate(statementScope, trans.getConnection(), sqlString, parameters);

      errorContext.setMoreInfo("Check the output parameters.");
      if (parameterObject != null) {
        postProcessParameterObject(statementScope, parameterObject, parameters);
      }

      errorContext.reset();
      //在sql对象中清除相关的statementScope信息      
      sql.cleanup(statementScope);
      
      //从缓存池中清除与当前MappedStatement对象相关联的缓存对象
      notifyListeners();
      return rows;
    } catch (SQLException e) {
      errorContext.setCause(e);
      throw new NestedSQLException(errorContext.toString(), e.getSQLState(), e.getErrorCode(), e);
    } catch (Exception e) {
      errorContext.setCause(e);
      throw new NestedSQLException(errorContext.toString(), e);
    }
  }

  //执行查询等操作，返回一个对象
  public Object executeQueryForObject(StatementScope statementScope, Transaction trans, Object parameterObject, Object resultObject)
      throws SQLException {
    try {
      Object object = null;

      DefaultRowHandler rowHandler = new DefaultRowHandler();
      executeQueryWithCallback(statementScope, trans.getConnection(), parameterObject, resultObject, rowHandler, SqlExecutor.NO_SKIPPED_RESULTS, SqlExecutor.NO_MAXIMUM_RESULTS);
     
      //获取列表的第一个对象
      List list = rowHandler.getList();

      if (list.size() > 1) {
        throw new SQLException("Error: executeQueryForObject returned too many results.");
      } else if (list.size() > 0) {
        object = list.get(0);
      }

      return object;
    } catch (TransactionException e) {
      throw new NestedSQLException("Error getting Connection from Transaction.  Cause: " + e, e);
    }
  }

  //执行查询等操作，返回一个列表
  public List executeQueryForList(StatementScope statementScope, Transaction trans, Object parameterObject, int skipResults, int maxResults)
      throws SQLException {
    try {
      DefaultRowHandler rowHandler = new DefaultRowHandler();
      executeQueryWithCallback(statementScope, trans.getConnection(), parameterObject, null, rowHandler, skipResults, maxResults);
      return rowHandler.getList();
    } catch (TransactionException e) {
      throw new NestedSQLException("Error getting Connection from Transaction.  Cause: " + e, e);
    }
  }

  public void executeQueryWithRowHandler(StatementScope statementScope, Transaction trans, Object parameterObject, RowHandler rowHandler)
      throws SQLException {
    try {
      executeQueryWithCallback(statementScope, trans.getConnection(), parameterObject, null, rowHandler, SqlExecutor.NO_SKIPPED_RESULTS, SqlExecutor.NO_MAXIMUM_RESULTS);
    } catch (TransactionException e) {
      throw new NestedSQLException("Error getting Connection from Transaction.  Cause: " + e, e);
    }
  }

  //
  //  PROTECTED METHODS
  //

  protected void executeQueryWithCallback(StatementScope statementScope, Connection conn, Object parameterObject, Object resultObject, RowHandler rowHandler, int skipResults, int maxResults)
      throws SQLException {
    ErrorContext errorContext = statementScope.getErrorContext();
    errorContext.setActivity("preparing the mapped statement for execution");
    errorContext.setObjectId(this.getId());
    errorContext.setResource(this.getResource());

    try {
      //验证parameterObject是否正确
      parameterObject = validateParameter(parameterObject);

      //获取当前MappedStatement对象配置形成的Sql实例化对象
      Sql sql = getSql();

      //获取当前MappedStatement对象配置形成的ParameterMap实例化对象
      errorContext.setMoreInfo("Check the parameter map.");
      ParameterMap parameterMap = sql.getParameterMap(statementScope, parameterObject);

      //获取当前MappedStatement对象配置形成的ResultMap实例化对象
      errorContext.setMoreInfo("Check the result map.");
      ResultMap resultMap = sql.getResultMap(statementScope, parameterObject);

      //把ParameterMap对象和ResultMap对象赋值给状态变量statementScope
      statementScope.setResultMap(resultMap);
      statementScope.setParameterMap(parameterMap);

      //获取ParameterMap对象中的ParameterMapping对象组
      errorContext.setMoreInfo("Check the parameter map.");
      Object[] parameters = parameterMap.getParameterObjectValues(statementScope, parameterObject);

      //从Sql实例化对象中获取字符串的Sql语句
      errorContext.setMoreInfo("Check the SQL statement.");
      String sqlString = sql.getSql(statementScope, parameterObject);

      //提交执行语句
      errorContext.setActivity("executing mapped statement");
      errorContext.setMoreInfo("Check the SQL statement or the result map.");
      RowHandlerCallback callback = new RowHandlerCallback(resultMap, resultObject, rowHandler);
      sqlExecuteQuery(statementScope, conn, sqlString, parameters, skipResults, maxResults, callback);

      errorContext.setMoreInfo("Check the output parameters.");
      if (parameterObject != null) {
        postProcessParameterObject(statementScope, parameterObject, parameters);
      }

      errorContext.reset();
      
      //在sql对象中清除相关的statementScope信息
      sql.cleanup(statementScope);
      
      //从缓存池中清除与当前MappedStatement对象相关联的缓存对象
      notifyListeners();
    } catch (SQLException e) {
      errorContext.setCause(e);
      throw new NestedSQLException(errorContext.toString(), e.getSQLState(), e.getErrorCode(), e);
    } catch (Exception e) {
      errorContext.setCause(e);
      throw new NestedSQLException(errorContext.toString(), e);
    }
  }

  protected void postProcessParameterObject(StatementScope statementScope, Object parameterObject, Object[] parameters) {
  }

  protected int sqlExecuteUpdate(StatementScope statementScope, Connection conn, String sqlString, Object[] parameters) throws SQLException {
    //判断是否采用了批处理操作
	if (statementScope.getSession().isInBatch()) {
      //调用SqlExecutor对象的addBatch方法
      getSqlExecutor().addBatch(statementScope, conn, sqlString, parameters);
      return 0;
    } else {
      //调用SqlExecutor对象的executeUpdate方法
      return getSqlExecutor().executeUpdate(statementScope, conn, sqlString, parameters);
    }
  }

  protected void sqlExecuteQuery(StatementScope statementScope, Connection conn, String sqlString, Object[] parameters, int skipResults, int maxResults, RowHandlerCallback callback) throws SQLException {
    getSqlExecutor().executeQuery(statementScope, conn, sqlString, parameters, skipResults, maxResults, callback);
  }

  //验证输入参数是否对应
  protected Object validateParameter(Object param)
      throws SQLException {
    Object newParam = param;
    Class parameterClass = getParameterClass();
    if (newParam != null && parameterClass != null) {
      if (DomTypeMarker.class.isAssignableFrom(parameterClass)) {
        if (XmlTypeMarker.class.isAssignableFrom(parameterClass)) {
          if (!(newParam instanceof String)
              && !(newParam instanceof Document)) {
            throw new SQLException("Invalid parameter object type.  Expected '" + String.class.getName() + "' or '" + Document.class.getName() + "' but found '" + newParam.getClass().getName() + "'.");
          }
          if (!(newParam instanceof Document)) {
            newParam = stringToDocument ((String)newParam);
          }
        } else {
          if (!Document.class.isAssignableFrom(newParam.getClass())) {
            throw new SQLException("Invalid parameter object type.  Expected '" + Document.class.getName() + "' but found '" + newParam.getClass().getName() + "'.");
          }
        }
      } else {
        if (!parameterClass.isAssignableFrom(newParam.getClass())) {
          throw new SQLException("Invalid parameter object type.  Expected '" + parameterClass.getName() + "' but found '" + newParam.getClass().getName() + "'.");
        }
      }
    }
    return newParam;
  }

  private Document stringToDocument (String s) {
    try {
      DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
      return documentBuilder.parse(new ReaderInputStream(new StringReader(s)));
    } catch (Exception e) {
      throw new RuntimeException("Error occurred.  Cause: " + e, e);
    }
  }

  public String getId() {
    return id;
  }

  public Integer getResultSetType() {
    return resultSetType;
  }

  public void setResultSetType(Integer resultSetType) {
    this.resultSetType = resultSetType;
  }

  public Integer getFetchSize() {
    return fetchSize;
  }

  public void setFetchSize(Integer fetchSize) {
    this.fetchSize = fetchSize;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Sql getSql() {
    return sql;
  }

  public void setSql(Sql sql) {
    this.sql = sql;
  }

  public ResultMap getResultMap() {
    return resultMap;
  }

  public void setResultMap(ResultMap resultMap) {
    this.resultMap = resultMap;
  }

  public ParameterMap getParameterMap() {
    return parameterMap;
  }

  public void setParameterMap(ParameterMap parameterMap) {
    this.parameterMap = parameterMap;
  }

  public Class getParameterClass() {
    return parameterClass;
  }

  public void setParameterClass(Class parameterClass) {
    this.parameterClass = parameterClass;
  }

  public String getResource() {
    return resource;
  }

  public void setResource(String resource) {
    this.resource = resource;
  }

  public CacheKey getCacheKey(StatementScope statementScope, Object parameterObject) {
    Sql sql = statementScope.getSql();
    ParameterMap pmap = sql.getParameterMap(statementScope, parameterObject);
    CacheKey cacheKey = pmap.getCacheKey(statementScope, parameterObject);
    cacheKey.update(id);
    cacheKey.update(baseCacheKey);
    cacheKey.update(sql.getSql(statementScope, parameterObject)); //Fixes bug 953001
    return cacheKey;
  }

  public void setBaseCacheKey(int base) {
    this.baseCacheKey = base;
  }

  public void addExecuteListener(ExecuteListener listener) {
    executeListeners.add(listener);
  }

  public void notifyListeners() {
    for (int i = 0, n = executeListeners.size(); i < n; i++) {
      ((ExecuteListener) executeListeners.get(i)).onExecuteStatement(this);
    }
  }

  public SqlExecutor getSqlExecutor() {
    return sqlMapClient.getSqlExecutor();
  }

  public SqlMapClient getSqlMapClient() {
    return sqlMapClient;
  }

  public void setSqlMapClient(SqlMapClient sqlMapClient) {
    this.sqlMapClient = (SqlMapClientImpl) sqlMapClient;
  }

  public void initRequest(StatementScope statementScope) {
    statementScope.setStatement(this);
    statementScope.setParameterMap(parameterMap);
    statementScope.setResultMap(resultMap);
    statementScope.setSql(sql);
  }

  public Integer getTimeout() {
    return timeout;
  }

  public void setTimeout(Integer timeout) {
    this.timeout = timeout;
  }

  public void addResultMap(ResultMap resultMap) {
    List resultMapList = Arrays.asList(additionalResultMaps);
    resultMapList = new ArrayList(resultMapList);
    resultMapList.add(resultMap);
    additionalResultMaps = (ResultMap[])resultMapList.toArray(new ResultMap[resultMapList.size()]);
  }

  public boolean hasMultipleResultMaps() {
    return additionalResultMaps.length > 0;
  }

  public ResultMap[] getAdditionalResultMaps() {
    return additionalResultMaps;
  }
}
