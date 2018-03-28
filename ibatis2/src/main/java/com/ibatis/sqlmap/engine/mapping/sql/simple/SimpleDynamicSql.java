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
package com.ibatis.sqlmap.engine.mapping.sql.simple;

import com.ibatis.common.beans.Probe;
import com.ibatis.common.beans.ProbeFactory;
import com.ibatis.sqlmap.client.SqlMapException;
import com.ibatis.sqlmap.engine.impl.SqlMapExecutorDelegate;
import com.ibatis.sqlmap.engine.mapping.parameter.ParameterMap;
import com.ibatis.sqlmap.engine.mapping.result.ResultMap;
import com.ibatis.sqlmap.engine.mapping.sql.Sql;
import com.ibatis.sqlmap.engine.scope.StatementScope;


import java.util.StringTokenizer;

//主要处理简单动态sql
public class SimpleDynamicSql implements Sql {

  private static final Probe PROBE = ProbeFactory.getProbe();

  private static final String ELEMENT_TOKEN = "$";

  private String sqlStatement;

  private SqlMapExecutorDelegate delegate;

  public SimpleDynamicSql(SqlMapExecutorDelegate delegate, String sqlStatement) {
    this.delegate = delegate;
    this.sqlStatement = sqlStatement;
  }
  
  public SimpleDynamicSql() {
	    
	  }

  public String getSql(StatementScope statementScope, Object parameterObject) {
    return processDynamicElements(sqlStatement, parameterObject);
  }

  public ParameterMap getParameterMap(StatementScope statementScope, Object parameterObject) {
    return statementScope.getParameterMap();
  }

  public ResultMap getResultMap(StatementScope statementScope, Object parameterObject) {
    return statementScope.getResultMap();
  }

  public void cleanup(StatementScope statementScope) {
  }

  public static boolean isSimpleDynamicSql(String sql) {
    return sql != null && sql.indexOf(ELEMENT_TOKEN) > -1;
  }

  //处理sql中包含有"$"的内容
  private String processDynamicElements(String sql, Object parameterObject) {
    StringTokenizer parser = new StringTokenizer(sql, ELEMENT_TOKEN, true);
    StringBuffer newSql = new StringBuffer();

    String token = null;
    String lastToken = null;
    while (parser.hasMoreTokens()) {
      token = parser.nextToken();

      if (ELEMENT_TOKEN.equals(lastToken)) {
        if (ELEMENT_TOKEN.equals(token)) {
          newSql.append(ELEMENT_TOKEN);
          token = null;
        } else {

          Object value = null;
          if (parameterObject != null) {
        	//判断参数的类型，
            if (delegate.getTypeHandlerFactory().hasTypeHandler(parameterObject.getClass())) {
            //如果不是一个bean,那么调用通用java数据类型处理
            	value = parameterObject;
            } else {
            //如果参数是一个bean，通过下列来获得bean中parameterObject的内容
              value = PROBE.getObject(parameterObject, token);
            }
          }
          if (value != null) {
            newSql.append(String.valueOf(value));
          }

          token = parser.nextToken();
          if (!ELEMENT_TOKEN.equals(token)) {
            throw new SqlMapException("Unterminated dynamic element in sql (" + sql + ").");
          }
          token = null;
        }
      } else {
        if (!ELEMENT_TOKEN.equals(token)) {
          newSql.append(token);
        }
      }

      lastToken = token;
    }

    return newSql.toString();
  }
  
  
  


}

