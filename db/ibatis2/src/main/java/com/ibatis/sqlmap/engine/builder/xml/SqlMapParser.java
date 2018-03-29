package com.ibatis.sqlmap.engine.builder.xml;

import com.ibatis.common.xml.*;
import com.ibatis.common.resources.*;
import com.ibatis.sqlmap.client.*;
import com.ibatis.sqlmap.engine.config.*;
import com.ibatis.sqlmap.engine.mapping.statement.*;
import com.ibatis.sqlmap.engine.cache.*;
import org.w3c.dom.Node;

import java.io.*;
import java.util.Properties;


/**
 * 该类主要是处理解析SQL Map映射文件
 */
public class SqlMapParser {

    private final NodeletParser parser; // parser变量是解析XML的核心

    private XmlParserState state; // state变量充当一个中间变量的容器，或者是一个配置信息的门户

    private SqlStatementParser statementParser; // statementParser变量充当一个中间变量的容器，主要针对SqlStatement

    public SqlMapParser(XmlParserState state) {
        this.parser = new NodeletParser();
        this.state = state;

        // 验证dtd文件，设置验证参数
        parser.setValidation(true);
        //设置dtd格式验证
        parser.setEntityResolver(new SqlMapClasspathEntityResolver());

        statementParser = new SqlStatementParser(this.state);

        addSqlMapNodelets();
        addSqlNodelets();
        addTypeAliasNodelets();
        addCacheModelNodelets();
        addParameterMapNodelets();
        addResultMapNodelets();
        addStatementNodelets();

    }

    public void parse(Reader reader) throws NodeletException {
        parser.parse(reader);
    }

    public void parse(InputStream inputStream) throws NodeletException {
        parser.parse(inputStream);
    }

    // 解析SQL Map映射文件中sqlMap节点，主要是处理namespace的属性
    private void addSqlMapNodelets() {
        parser.addNodelet("/sqlMap", new Nodelet() {
            public void process(Node node) throws Exception {
                Properties attributes = NodeletUtils.parseAttributes(node, state.getGlobalProps());
                state.setNamespace(attributes.getProperty("namespace"));
            }
        });
    }

    // 解析SQL Map映射文件中/sqlMap/sql节点，主要是处理映射文件中sql节点的文本内容
    private void addSqlNodelets() {
        parser.addNodelet("/sqlMap/sql", new Nodelet() {
            public void process(Node node) throws Exception {
                Properties attributes = NodeletUtils.parseAttributes(node, state.getGlobalProps());
                String id = attributes.getProperty("id");
                if (state.isUseStatementNamespaces()) {
                    id = state.applyNamespace(id);
                }
                if (state.getSqlIncludes().containsKey(id)) {
                    throw new SqlMapException("Duplicate <sql>-include '" + id
                            + "' found.");
                } else {
                    state.getSqlIncludes().put(id, node);
                }
            }
        });
    }

    // 解析SQL Map映射文件中/sqlMap/typeAlias节点，处理别名（typeAlias）转化
    private void addTypeAliasNodelets() {
        parser.addNodelet("/sqlMap/typeAlias", new Nodelet() {
            public void process(Node node) throws Exception {
                Properties prop = NodeletUtils.parseAttributes(node, state.getGlobalProps());
                String alias = prop.getProperty("alias");
                String type = prop.getProperty("type");
                state.getConfig().getTypeHandlerFactory().putTypeAlias(alias,
                        type);
            }
        });
    }

    //	解析SQL Map映射文件中/sqlMap/cacheModel节点，处理缓存（cacheModel）
    private void addCacheModelNodelets() {

        // 解析SQL Map映射文件中/sqlMap/cacheModel节点属性，对缓存（cacheModel）的属性进行初始化
        parser.addNodelet("/sqlMap/cacheModel", new Nodelet() {
            public void process(Node node) throws Exception {
                Properties attributes = NodeletUtils.parseAttributes(node,
                        state.getGlobalProps());
                String id = state.applyNamespace(attributes.getProperty("id"));
                String type = attributes.getProperty("type");
                String readOnlyAttr = attributes.getProperty("readOnly");
                Boolean readOnly = readOnlyAttr == null
                        || readOnlyAttr.length() <= 0 ? null : new Boolean("true".equals(readOnlyAttr));
                String serializeAttr = attributes.getProperty("serialize");
                Boolean serialize = serializeAttr == null
                        || serializeAttr.length() <= 0 ? null : new Boolean("true".equals(serializeAttr));
                type = state.getConfig().getTypeHandlerFactory().resolveAlias(type);
                Class clazz = Resources.classForName(type);
                if (readOnly == null) {
                    readOnly = Boolean.TRUE;
                }
                if (serialize == null) {
                    serialize = Boolean.FALSE;
                }
                CacheModelConfig cacheConfig = state.getConfig()
                        .newCacheModelConfig(id,
                                (CacheController) Resources.instantiate(clazz),
                                readOnly.booleanValue(),
                                serialize.booleanValue());
                state.setCacheConfig(cacheConfig);
            }
        });


        parser.addNodelet("/sqlMap/cacheModel/end()", new Nodelet() {
            public void process(Node node) throws Exception {
                state.getCacheConfig().setControllerProperties(
                        state.getCacheProps());
            }
        });

        // 解析SQL Map映射文件中/sqlMap/cacheModel/property节点，对缓存（cacheModel）的属性进行初始化
        parser.addNodelet("/sqlMap/cacheModel/property", new Nodelet() {
            public void process(Node node) throws Exception {
                state.getConfig().getErrorContext().setMoreInfo(
                        "Check the cache model properties.");
                Properties attributes = NodeletUtils.parseAttributes(node,
                        state.getGlobalProps());
                String name = attributes.getProperty("name");
                String value = NodeletUtils.parsePropertyTokens(attributes
                        .getProperty("value"), state.getGlobalProps());
                state.getCacheProps().setProperty(name, value);
            }
        });

        //解析SQL Map映射文件中/sqlMap/cacheModel/flushOnExecute节点，对缓存（cacheModel）的flushOnExecute属性进行初始化
        parser.addNodelet("/sqlMap/cacheModel/flushOnExecute", new Nodelet() {
            public void process(Node node) throws Exception {
                Properties childAttributes = NodeletUtils.parseAttributes(node,
                        state.getGlobalProps());
                String statement = childAttributes.getProperty("statement");
                state.getCacheConfig().addFlushTriggerStatement(statement);
            }
        });

        // 解析SQL Map映射文件中/sqlMap/cacheModel/flushInterval节点，对缓存（cacheModel）的flushInterval属性进行初始化
        parser.addNodelet("/sqlMap/cacheModel/flushInterval", new Nodelet() {
            public void process(Node node) throws Exception {
                Properties childAttributes = NodeletUtils.parseAttributes(node,
                        state.getGlobalProps());
                try {
                    int milliseconds = childAttributes
                            .getProperty("milliseconds") == null ? 0 : Integer
                            .parseInt(childAttributes
                                    .getProperty("milliseconds"));
                    int seconds = childAttributes.getProperty("seconds") == null ? 0
                            : Integer.parseInt(childAttributes
                            .getProperty("seconds"));
                    int minutes = childAttributes.getProperty("minutes") == null ? 0
                            : Integer.parseInt(childAttributes
                            .getProperty("minutes"));
                    int hours = childAttributes.getProperty("hours") == null ? 0
                            : Integer.parseInt(childAttributes
                            .getProperty("hours"));
                    state.getCacheConfig().setFlushInterval(hours, minutes,
                            seconds, milliseconds);
                } catch (NumberFormatException e) {
                    throw new RuntimeException(
                            "Error building cache in '"
                                    + "resourceNAME"
                                    + "'.  Flush interval milliseconds must be a valid long integer value.  Cause: "
                                    + e, e);
                }
            }
        });
    }


    //解析SQL Map映射文件中/sqlMap/parameterMap节点，对输入参数（ParameterMap）的属性进行初始化
    private void addParameterMapNodelets() {

        //
        parser.addNodelet("/sqlMap/parameterMap/end()", new Nodelet() {
            public void process(Node node) throws Exception {
                state.getConfig().getErrorContext().setMoreInfo(null);
                state.getConfig().getErrorContext().setObjectId(null);
                state.setParamConfig(null);
            }
        });

        //解析文件中"/sqlMap/parameterMap"节点，对输入参数（ParameterMap）的属性进行初始化
        parser.addNodelet("/sqlMap/parameterMap", new Nodelet() {
            public void process(Node node) throws Exception {
                Properties attributes = NodeletUtils.parseAttributes(node,
                        state.getGlobalProps());
                String id = state.applyNamespace(attributes.getProperty("id"));
                String parameterClassName = attributes.getProperty("class");
                parameterClassName = state.getConfig().getTypeHandlerFactory()
                        .resolveAlias(parameterClassName);
                try {
                    state.getConfig().getErrorContext().setMoreInfo(
                            "Check the parameter class.");
                    ParameterMapConfig paramConf = state.getConfig()
                            .newParameterMapConfig(id,
                                    Resources.classForName(parameterClassName));
                    state.setParamConfig(paramConf);
                } catch (Exception e) {
                    throw new SqlMapException(
                            "Error configuring ParameterMap.  Could not set ParameterClass.  Cause: "
                                    + e, e);
                }
            }
        });

        //解析文件中"/sqlMap/parameterMap/parameter"节点，对输入参数（ParameterMap）的parameter属性进行初始化
        parser.addNodelet("/sqlMap/parameterMap/parameter", new Nodelet() {
            public void process(Node node) throws Exception {
                Properties childAttributes = NodeletUtils.parseAttributes(node,
                        state.getGlobalProps());
                String propertyName = childAttributes.getProperty("property");
                String jdbcType = childAttributes.getProperty("jdbcType");
                String type = childAttributes.getProperty("typeName");
                String javaType = childAttributes.getProperty("javaType");
                String resultMap = state.applyNamespace(childAttributes
                        .getProperty("resultMap"));
                String nullValue = childAttributes.getProperty("nullValue");
                String mode = childAttributes.getProperty("mode");
                String callback = childAttributes.getProperty("typeHandler");
                String numericScaleProp = childAttributes
                        .getProperty("numericScale");

                callback = state.getConfig().getTypeHandlerFactory()
                        .resolveAlias(callback);
                Object typeHandlerImpl = null;
                if (callback != null) {
                    typeHandlerImpl = Resources.instantiate(callback);
                }

                javaType = state.getConfig().getTypeHandlerFactory()
                        .resolveAlias(javaType);
                Class javaClass = null;
                try {
                    if (javaType != null && javaType.length() > 0) {
                        javaClass = Resources.classForName(javaType);
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(
                            "Error setting javaType on parameter mapping.  Cause: "
                                    + e);
                }

                Integer numericScale = null;
                if (numericScaleProp != null) {
                    numericScale = new Integer(numericScaleProp);
                }

                state.getParamConfig().addParameterMapping(propertyName,
                        javaClass, jdbcType, nullValue, mode, type,
                        numericScale, typeHandlerImpl, resultMap);
            }
        });
    }

    //解析SQL Map映射文件中"/sqlMap/resultMap"节点，对输出参数（resultMap）的属性进行初始化
    private void addResultMapNodelets() {


        parser.addNodelet("/sqlMap/resultMap/end()", new Nodelet() {
            public void process(Node node) throws Exception {
                state.getConfig().getErrorContext().setMoreInfo(null);
                state.getConfig().getErrorContext().setObjectId(null);
            }
        });

        //解析文件中"/sqlMap/resultMap"节点，对输出参数（resultMap）的属性进行初始化
        parser.addNodelet("/sqlMap/resultMap", new Nodelet() {
            public void process(Node node) throws Exception {
                Properties attributes = NodeletUtils.parseAttributes(node,
                        state.getGlobalProps());
                String id = state.applyNamespace(attributes.getProperty("id"));
                String resultClassName = attributes.getProperty("class");
                String extended = state.applyNamespace(attributes
                        .getProperty("extends"));
                String xmlName = attributes.getProperty("xmlName");
                String groupBy = attributes.getProperty("groupBy");

                resultClassName = state.getConfig().getTypeHandlerFactory()
                        .resolveAlias(resultClassName);
                Class resultClass;
                try {
                    state.getConfig().getErrorContext().setMoreInfo(
                            "Check the result class.");
                    resultClass = Resources.classForName(resultClassName);
                } catch (Exception e) {
                    throw new RuntimeException(
                            "Error configuring Result.  Could not set ResultClass.  Cause: "
                                    + e, e);
                }
                ResultMapConfig resultConf = state.getConfig()
                        .newResultMapConfig(id, resultClass, groupBy, extended,
                                xmlName);
                state.setResultConfig(resultConf);
            }
        });

        //解析文件中"/sqlMap/resultMap/result"节点，对输出参数（resultMap）的result属性进行初始化
        parser.addNodelet("/sqlMap/resultMap/result", new Nodelet() {
            public void process(Node node) throws Exception {
                Properties childAttributes = NodeletUtils.parseAttributes(node,
                        state.getGlobalProps());
                String propertyName = childAttributes.getProperty("property");
                String nullValue = childAttributes.getProperty("nullValue");
                String jdbcType = childAttributes.getProperty("jdbcType");
                String javaType = childAttributes.getProperty("javaType");
                String columnName = childAttributes.getProperty("column");
                String columnIndexProp = childAttributes
                        .getProperty("columnIndex");
                String statementName = childAttributes.getProperty("select");
                String resultMapName = childAttributes.getProperty("resultMap");
                String callback = childAttributes.getProperty("typeHandler");
                String notNullColumn = childAttributes
                        .getProperty("notNullColumn");

                state.getConfig().getErrorContext().setMoreInfo(
                        "Check the result mapping property type or name.");
                Class javaClass = null;
                try {
                    javaType = state.getConfig().getTypeHandlerFactory()
                            .resolveAlias(javaType);
                    if (javaType != null && javaType.length() > 0) {
                        javaClass = Resources.classForName(javaType);
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(
                            "Error setting java type on result discriminator mapping.  Cause: "
                                    + e);
                }

                state
                        .getConfig()
                        .getErrorContext()
                        .setMoreInfo(
                                "Check the result mapping typeHandler attribute '"
                                        + callback
                                        + "' (must be a TypeHandler or TypeHandlerCallback implementation).");
                Object typeHandlerImpl = null;
                try {
                    if (callback != null && callback.length() > 0) {
                        callback = state.getConfig().getTypeHandlerFactory()
                                .resolveAlias(callback);
                        typeHandlerImpl = Resources.instantiate(callback);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(
                            "Error occurred during custom type handler configuration.  Cause: "
                                    + e, e);
                }

                Integer columnIndex = null;
                if (columnIndexProp != null) {
                    try {
                        columnIndex = new Integer(columnIndexProp);
                    } catch (Exception e) {
                        throw new RuntimeException(
                                "Error parsing column index.  Cause: " + e, e);
                    }
                }

                state.getResultConfig().addResultMapping(propertyName,
                        columnName, columnIndex, javaClass, jdbcType,
                        nullValue, notNullColumn, statementName, resultMapName,
                        typeHandlerImpl);
            }
        });

        //解析文件中"/sqlMap/resultMap/discriminator/subMap"节点，对输出参数（resultMap）的discriminator节点的subMap属性进行初始化
        parser.addNodelet("/sqlMap/resultMap/discriminator/subMap",
                new Nodelet() {
                    public void process(Node node) throws Exception {
                        Properties childAttributes = NodeletUtils
                                .parseAttributes(node, state.getGlobalProps());
                        String value = childAttributes.getProperty("value");
                        String resultMap = childAttributes
                                .getProperty("resultMap");
                        resultMap = state.applyNamespace(resultMap);
                        state.getResultConfig().addDiscriminatorSubMap(value,
                                resultMap);
                    }
                });

        //解析文件中"/sqlMap/resultMap/discriminator"节点，对输出参数（resultMap）的discriminator属性进行初始化
        parser.addNodelet("/sqlMap/resultMap/discriminator", new Nodelet() {
            public void process(Node node) throws Exception {
                Properties childAttributes = NodeletUtils.parseAttributes(node,
                        state.getGlobalProps());
                String nullValue = childAttributes.getProperty("nullValue");
                String jdbcType = childAttributes.getProperty("jdbcType");
                String javaType = childAttributes.getProperty("javaType");
                String columnName = childAttributes.getProperty("column");
                String columnIndexProp = childAttributes
                        .getProperty("columnIndex");
                String callback = childAttributes.getProperty("typeHandler");

                state.getConfig().getErrorContext().setMoreInfo(
                        "Check the disriminator type or name.");
                Class javaClass = null;
                try {
                    javaType = state.getConfig().getTypeHandlerFactory()
                            .resolveAlias(javaType);
                    if (javaType != null && javaType.length() > 0) {
                        javaClass = Resources.classForName(javaType);
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(
                            "Error setting java type on result discriminator mapping.  Cause: "
                                    + e);
                }

                state
                        .getConfig()
                        .getErrorContext()
                        .setMoreInfo(
                                "Check the result mapping discriminator typeHandler attribute '"
                                        + callback
                                        + "' (must be a TypeHandlerCallback implementation).");
                Object typeHandlerImpl = null;
                try {
                    if (callback != null && callback.length() > 0) {
                        callback = state.getConfig().getTypeHandlerFactory()
                                .resolveAlias(callback);
                        typeHandlerImpl = Resources.instantiate(callback);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(
                            "Error occurred during custom type handler configuration.  Cause: "
                                    + e, e);
                }

                Integer columnIndex = null;
                if (columnIndexProp != null) {
                    try {
                        columnIndex = new Integer(columnIndexProp);
                    } catch (Exception e) {
                        throw new RuntimeException(
                                "Error parsing column index.  Cause: " + e, e);
                    }
                }

                state.getResultConfig().setDiscriminator(columnName,
                        columnIndex, javaClass, jdbcType, nullValue,
                        typeHandlerImpl);
            }
        });
    }

    //解析SQL Map映射文件中"/sqlMap/Statement"节点，对映射语句（Statement）的属性进行初始化
    protected void addStatementNodelets() {

        //解析文件中"/sqlMap/statement"节点，对映射语句（statement）进行初始化，实际上是转化给了SqlStatementParser对象进行处理
        parser.addNodelet("/sqlMap/statement", new Nodelet() {
            public void process(Node node) throws Exception {
                statementParser.parseGeneralStatement(node,
                        new MappedStatement());
            }
        });

        //解析文件中"/sqlMap/insert"节点，对映射语句（insert）进行初始化，实际上是转化给了SqlStatementParser对象进行处理
        parser.addNodelet("/sqlMap/insert", new Nodelet() {
            public void process(Node node) throws Exception {
                statementParser.parseGeneralStatement(node,
                        new InsertStatement());
            }
        });

        //解析文件中"/sqlMap/update"节点，对映射语句（update）进行初始化，实际上是转化给了SqlStatementParser对象进行处理
        parser.addNodelet("/sqlMap/update", new Nodelet() {
            public void process(Node node) throws Exception {
                statementParser.parseGeneralStatement(node,
                        new UpdateStatement());
            }
        });

        //解析文件中"/sqlMap/delete"节点，对映射语句（delete）进行初始化，实际上是转化给了SqlStatementParser对象进行处理
        parser.addNodelet("/sqlMap/delete", new Nodelet() {
            public void process(Node node) throws Exception {
                statementParser.parseGeneralStatement(node,
                        new DeleteStatement());
            }
        });

        //解析文件中"/sqlMap/select"节点，对映射语句（select）进行初始化，实际上是转化给了SqlStatementParser对象进行处理
        parser.addNodelet("/sqlMap/select", new Nodelet() {
            public void process(Node node) throws Exception {
                statementParser.parseGeneralStatement(node,
                        new SelectStatement());
            }
        });

        //解析文件中"/sqlMap/procedure"节点，对映射语句（procedure）进行初始化，实际上是转化给了SqlStatementParser对象进行处理
        parser.addNodelet("/sqlMap/procedure", new Nodelet() {
            public void process(Node node) throws Exception {
                statementParser.parseGeneralStatement(node,
                        new ProcedureStatement());
            }
        });
    }

}
