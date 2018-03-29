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
package com.ibatis.sqlmap.engine.scope;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapException;
import com.ibatis.sqlmap.client.SqlMapExecutor;
import com.ibatis.sqlmap.client.SqlMapTransactionManager;
import com.ibatis.sqlmap.engine.transaction.Transaction;
import com.ibatis.sqlmap.engine.transaction.TransactionState;
import com.ibatis.sqlmap.engine.impl.SqlMapExecutorDelegate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

/**
 * A Session based implementation of the Scope interface
 */
public class SessionScope {
  private static long nextId;  //是个静态Long变量，形成自增长序列
  private long id;    //该SessionScope实例化对象的id标示，具有唯一性。
  // Used by Any
  private SqlMapClient sqlMapClient; //当前Session的SqlMapClient对象，用于传递SqlMapClient实例化对象
  private SqlMapExecutor sqlMapExecutor; //当前Session的SqlMapExecutor对象，用于传递SqlMapExecutor实例化对象
  private SqlMapTransactionManager sqlMapTxMgr; //当前Session的SqlMapTransactionManager对象，用于事务的管理
  private int requestStackDepth;
  // Used by TransactionManager
  private Transaction transaction; //当前Session的Transaction对象，用于事务的处理
  private TransactionState transactionState; //当前Session的TransactionState对象,用于判断事务状态
  // Used by SqlMapExecutorDelegate.setUserProvidedTransaction()
  private TransactionState savedTransactionState;  
  // Used by StandardSqlMapClient and GeneralStatement
  private boolean inBatch;  //当前Session是否要求批处理
  // Used by SqlExecutor
  private Object batch;
  private boolean commitRequired;  //当前Session是否已经提交
  private Map preparedStatements;  //当前Session所拥有的映射语句（Statements）

  /**
   * Default constructor
   */
  public SessionScope() {
    this.preparedStatements = new HashMap();
    this.inBatch = false;
    this.requestStackDepth = 0;
    this.id = getNextId();
  }

  /**
   * Get the SqlMapClient for the session
   *
   * @return - the SqlMapClient
   */
  public SqlMapClient getSqlMapClient() {
    return sqlMapClient;
  }

  /**
   * Set the SqlMapClient for the session
   *
   * @param sqlMapClient - the SqlMapClient
   */
  public void setSqlMapClient(SqlMapClient sqlMapClient) {
    this.sqlMapClient = sqlMapClient;
  }

  /**
   * Get the SQL executor for the session
   *
   * @return - the SQL executor
   */
  public SqlMapExecutor getSqlMapExecutor() {
    return sqlMapExecutor;
  }

  /**
   * Get the SQL executor for the session
   *
   * @param sqlMapExecutor - the SQL executor
   */
  public void setSqlMapExecutor(SqlMapExecutor sqlMapExecutor) {
    this.sqlMapExecutor = sqlMapExecutor;
  }

  /**
   * Get the transaction manager
   *
   * @return - the transaction manager
   */
  public SqlMapTransactionManager getSqlMapTxMgr() {
    return sqlMapTxMgr;
  }

  /**
   * Set the transaction manager
   *
   * @param sqlMapTxMgr - the transaction manager
   */
  public void setSqlMapTxMgr(SqlMapTransactionManager sqlMapTxMgr) {
    this.sqlMapTxMgr = sqlMapTxMgr;
  }

  /**
   * Tells us if we are in batch mode or not
   *
   * @return - true if we are working with a batch
   */
  public boolean isInBatch() {
    return inBatch;
  }

  /**
   * Turn batch mode on or off
   *
   * @param inBatch - the switch
   */
  public void setInBatch(boolean inBatch) {
    this.inBatch = inBatch;
  }

  /**
   * Getter for the session transaction
   *
   * @return - the transaction
   */
  public Transaction getTransaction() {
    return transaction;
  }

  /**
   * Setter for the session transaction
   *
   * @param transaction - the transaction
   */
  public void setTransaction(Transaction transaction) {
    this.transaction = transaction;
  }

  /**
   * Getter for the transaction state of the session
   *
   * @return - the state
   */
  public TransactionState getTransactionState() {
    return transactionState;
  }

  /**
   * Setter for the transaction state of the session
   *
   * @param transactionState - the new transaction state
   */
  public void setTransactionState(TransactionState transactionState) {
    this.transactionState = transactionState;
  }

  /**
   * Getter for the batch of the session
   *
   * @return - the batch
   */
  public Object getBatch() {
    return batch;
  }

  /**
   * Stter for the batch of the session
   *
   * @param batch the new batch
   */
  public void setBatch(Object batch) {
    this.batch = batch;
  }

  /**
   * Get the request stack depth
   *
   * @return - the stack depth
   */
  public int getRequestStackDepth() {
    return requestStackDepth;
  }

  /**
   * Increment the stack depth by one.
   */
  public void incrementRequestStackDepth() {
    requestStackDepth++;
  }

  /**
   * Decrement the stack depth by one.
   */
  public void decrementRequestStackDepth() {
    requestStackDepth--;
  }

  /**
   * Getter to tell if a commit is required for the session
   *
   * @return - true if a commit is required
   */
  public boolean isCommitRequired() {
    return commitRequired;
  }

  /**
   * Setter to tell the session that a commit is required for the session
   *
   * @param commitRequired - the flag
   */
  public void setCommitRequired(boolean commitRequired) {
    this.commitRequired = commitRequired;
  }

  public boolean hasPreparedStatementFor(String sql) {
    return preparedStatements.containsKey(sql);
  }

  public boolean hasPreparedStatement(PreparedStatement ps) {
    return preparedStatements.containsValue(ps);
  }

  public PreparedStatement getPreparedStatement(String sql) throws SQLException {
    if (!hasPreparedStatementFor(sql))
      throw new SqlMapException("Could not get prepared statement.  This is likely a bug.");
    PreparedStatement ps = (PreparedStatement) preparedStatements.get(sql);
    return ps;
  }

  public void putPreparedStatement(SqlMapExecutorDelegate delegate, String sql, PreparedStatement ps) {
    if (delegate.isStatementCacheEnabled()) {
      if (!isInBatch()) {
        if (hasPreparedStatementFor(sql))
          throw new SqlMapException("Duplicate prepared statement found.  This is likely a bug.");
        preparedStatements.put(sql, ps);
      }
    }
  }

  public void closePreparedStatements() {
    Iterator keys = preparedStatements.keySet().iterator();
    while (keys.hasNext()) {
      PreparedStatement ps = (PreparedStatement) preparedStatements.get(keys.next());
      try {
        ps.close();
      } catch (Exception e) {
        // ignore -- we don't care if this fails at this point.
      }
    }
    preparedStatements.clear();
  }

  public void cleanup() {
    closePreparedStatements();
    preparedStatements.clear();
  }

  public boolean equals(Object parameterObject) {
    if (this == parameterObject) return true;
    if (!(parameterObject instanceof SessionScope)) return false;
    final SessionScope sessionScope = (SessionScope) parameterObject;
    if (id != sessionScope.id) return false;
    return true;
  }

  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  /**
   * Method to get a unique ID
   *
   * @return - the new ID
   */
  public synchronized static long getNextId() {
    return nextId++;
  }

  /**
   * Saves the current transaction state
   */
  public void saveTransactionState() {
    savedTransactionState = transactionState;
  }

  /**
   * Restores the previously saved transaction state
   */
  public void recallTransactionState() {
    transactionState = savedTransactionState;
  }

}
