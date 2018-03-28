package com.ibatis.sqlmap.engine.builder.xml;

import com.ibatis.common.resources.*;
import com.ibatis.common.xml.*;
import com.ibatis.sqlmap.client.*;
import com.ibatis.sqlmap.engine.config.*;
import com.ibatis.sqlmap.engine.transaction.*;
import com.ibatis.sqlmap.engine.datasource.*;
import com.ibatis.sqlmap.engine.mapping.result.*;
import org.w3c.dom.Node;

import java.io.*;
import java.util.Properties;

//该类主要是处理解析SQL Map配置文件
public class SqlMapConfigParser {

	// parser变量是解析XML的核心
	protected final NodeletParser parser = new NodeletParser();

	// state变量充当一个中间变量的容器，或者是一个配置信息的门户
	private XmlParserState state = new XmlParserState();

	private boolean usingStreams = false;

	public SqlMapConfigParser() {

		// 验证dtd文件，设置验证参数
		parser.setValidation(true);
		parser.setEntityResolver(new SqlMapClasspathEntityResolver());

		//进行初始化的赋值处理
		addSqlMapConfigNodelets();
		addGlobalPropNodelets();
		addSettingsNodelets();
		addTypeAliasNodelets();
		addTypeHandlerNodelets();
		addTransactionManagerNodelets();
		addSqlMapNodelets();
		addResultObjectFactoryNodelets();

	}

	public SqlMapClient parse(Reader reader, Properties props) {
		if (props != null)
			state.setGlobalProps(props);
		return parse(reader);
	}

	//基于字符模式解析XML配置文件
	public SqlMapClient parse(Reader reader) {
		try {
			usingStreams = false;

			parser.parse(reader);
			return state.getConfig().getClient();
		} catch (Exception e) {
			throw new RuntimeException("Error occurred.  Cause: " + e, e);
		}
	}

	public SqlMapClient parse(InputStream inputStream, Properties props) {
		if (props != null)
			state.setGlobalProps(props);
		return parse(inputStream);
	}

    //	基于字节模式解析XML配置文件
	public SqlMapClient parse(InputStream inputStream) {
		try {
			usingStreams = true;

			parser.parse(inputStream);
			return state.getConfig().getClient();
		} catch (Exception e) {
			throw new RuntimeException("Error occurred.  Cause: " + e, e);
		}
	}

	// 处理sqlMapConfig根节点信息，表明其已经结束
	private void addSqlMapConfigNodelets() {
		parser.addNodelet("/sqlMapConfig/end()", new Nodelet() {
			public void process(Node node) throws Exception {
				state.getConfig().finalizeSqlMapConfig();
			}
		});
	}

	// 处理节点/sqlMapConfig/properties信息，产生一个全局的Properties
	private void addGlobalPropNodelets() {
		parser.addNodelet("/sqlMapConfig/properties", new Nodelet() {
			public void process(Node node) throws Exception {
				// 获得/sqlMapConfig/properties节点下的全部属性值，并把这些属性值赋值给Properties类型的变量attributes
				Properties attributes = NodeletUtils.parseAttributes(node,
						state.getGlobalProps());
				String resource = attributes.getProperty("resource");
				String url = attributes.getProperty("url");

				// 由state变量作为中间体传递信息
				state.setGlobalProperties(resource, url);
			}
		});
	}

	
	//处理节点/sqlMapConfig/settings信息，产生全局变量
	private void addSettingsNodelets() {
		parser.addNodelet("/sqlMapConfig/settings", new Nodelet() {
			public void process(Node node) throws Exception {
				Properties attributes = NodeletUtils.parseAttributes(node,
						state.getGlobalProps());
				SqlMapConfiguration config = state.getConfig();

				String classInfoCacheEnabledAttr = attributes
						.getProperty("classInfoCacheEnabled");
				boolean classInfoCacheEnabled = (classInfoCacheEnabledAttr == null || "true"
						.equals(classInfoCacheEnabledAttr));
				config.setClassInfoCacheEnabled(classInfoCacheEnabled);

				String lazyLoadingEnabledAttr = attributes
						.getProperty("lazyLoadingEnabled");
				boolean lazyLoadingEnabled = (lazyLoadingEnabledAttr == null || "true"
						.equals(lazyLoadingEnabledAttr));
				config.setLazyLoadingEnabled(lazyLoadingEnabled);

				String statementCachingEnabledAttr = attributes
						.getProperty("statementCachingEnabled");
				boolean statementCachingEnabled = (statementCachingEnabledAttr == null || "true"
						.equals(statementCachingEnabledAttr));
				config.setStatementCachingEnabled(statementCachingEnabled);

				String cacheModelsEnabledAttr = attributes
						.getProperty("cacheModelsEnabled");
				boolean cacheModelsEnabled = (cacheModelsEnabledAttr == null || "true"
						.equals(cacheModelsEnabledAttr));
				config.setCacheModelsEnabled(cacheModelsEnabled);

				String enhancementEnabledAttr = attributes
						.getProperty("enhancementEnabled");
				boolean enhancementEnabled = (enhancementEnabledAttr == null || "true"
						.equals(enhancementEnabledAttr));
				config.setEnhancementEnabled(enhancementEnabled);

				String useColumnLabelAttr = attributes
						.getProperty("useColumnLabel");
				boolean useColumnLabel = (useColumnLabelAttr == null || "true"
						.equals(useColumnLabelAttr));
				config.setUseColumnLabel(useColumnLabel);

				String forceMultipleResultSetSupportAttr = attributes
						.getProperty("forceMultipleResultSetSupport");
				boolean forceMultipleResultSetSupport = "true"
						.equals(forceMultipleResultSetSupportAttr);
				config
						.setForceMultipleResultSetSupport(forceMultipleResultSetSupport);

				String defaultTimeoutAttr = attributes
						.getProperty("defaultStatementTimeout");
				Integer defaultTimeout = defaultTimeoutAttr == null ? null
						: Integer.valueOf(defaultTimeoutAttr);
				config.setDefaultStatementTimeout(defaultTimeout);

				String useStatementNamespacesAttr = attributes
						.getProperty("useStatementNamespaces");
				boolean useStatementNamespaces = "true"
						.equals(useStatementNamespacesAttr);
				state.setUseStatementNamespaces(useStatementNamespaces);
			}
		});
	}

	
    //	处理节点/sqlMapConfig/typeAlias信息，处理别名（typeAlias）转化
	private void addTypeAliasNodelets() {
		parser.addNodelet("/sqlMapConfig/typeAlias", new Nodelet() {
			public void process(Node node) throws Exception {
				Properties prop = NodeletUtils.parseAttributes(node, state
						.getGlobalProps());
				String alias = prop.getProperty("alias");
				String type = prop.getProperty("type");
				//获取别名及其对应的Class类型，注册到TypeHandlerFactory工厂对象中
				state.getConfig().getTypeHandlerFactory().putTypeAlias(alias,
						type);
			}
		});
	}

    //	处理节点/sqlMapConfig/typeHandler信息，增加新的jdbcType
	private void addTypeHandlerNodelets() {
		parser.addNodelet("/sqlMapConfig/typeHandler", new Nodelet() {
			public void process(Node node) throws Exception {
				Properties prop = NodeletUtils.parseAttributes(node, state
						.getGlobalProps());
				String jdbcType = prop.getProperty("jdbcType");
				String javaType = prop.getProperty("javaType");
				String callback = prop.getProperty("callback");

				javaType = state.getConfig().getTypeHandlerFactory()
						.resolveAlias(javaType);
				callback = state.getConfig().getTypeHandlerFactory()
						.resolveAlias(callback);

				state.getConfig().newTypeHandler(
						Resources.classForName(javaType), jdbcType,
						Resources.instantiate(callback));
			}
		});
	}

    //	处理节点/sqlMapConfig/transactionManager信息，包括transactionManager和dataSource
	private void addTransactionManagerNodelets() {
        // 处理节点/sqlMapConfig/transactionManager/property的信息，转化到transactionManager对象的相关属性中
		parser.addNodelet("/sqlMapConfig/transactionManager/property",
				new Nodelet() {
					public void process(Node node) throws Exception {
						Properties attributes = NodeletUtils.parseAttributes(
								node, state.getGlobalProps());
						String name = attributes.getProperty("name");
						String value = NodeletUtils.parsePropertyTokens(
								attributes.getProperty("value"), state
										.getGlobalProps());
						state.getTxProps().setProperty(name, value);
					}
				});
		
        //	处理节点/sqlMapConfig/transactionManager/，生成transactionManager对象并进行结束处理
		parser.addNodelet("/sqlMapConfig/transactionManager/end()",
				new Nodelet() {
					public void process(Node node) throws Exception {
						Properties attributes = NodeletUtils.parseAttributes(
								node, state.getGlobalProps());
						String type = attributes.getProperty("type");
						boolean commitRequired = "true".equals(attributes
								.getProperty("commitRequired"));

						state.getConfig().getErrorContext().setActivity(
								"configuring the transaction manager");
						type = state.getConfig().getTypeHandlerFactory()
								.resolveAlias(type);
						TransactionManager txManager;
						try {
							state.getConfig().getErrorContext().setMoreInfo(
											"Check the transaction manager type or class.");
							TransactionConfig config = (TransactionConfig) Resources
									.instantiate(type);
							config.setDataSource(state.getDataSource());
							state
									.getConfig()
									.getErrorContext()
									.setMoreInfo(
											"Check the transactio nmanager properties or configuration.");
							config.setProperties(state.getTxProps());
							config.setForceCommit(commitRequired);
							config.setDataSource(state.getDataSource());
							state.getConfig().getErrorContext().setMoreInfo(
									null);
							txManager = new TransactionManager(config);
						} catch (Exception e) {
							if (e instanceof SqlMapException) {
								throw (SqlMapException) e;
							} else {
								throw new SqlMapException(
										"Error initializing TransactionManager.  Could not instantiate TransactionConfig.  Cause: "
												+ e, e);
							}
						}
						state.getConfig().setTransactionManager(txManager);
					}
				});
		
		
         // 处理节点/sqlMapConfig/transactionManager/dataSource/property，处理dataSource节点的property内容
		parser.addNodelet(
				"/sqlMapConfig/transactionManager/dataSource/property",
				new Nodelet() {
					public void process(Node node) throws Exception {
						Properties attributes = NodeletUtils.parseAttributes(
								node, state.getGlobalProps());
						String name = attributes.getProperty("name");
						String value = NodeletUtils.parsePropertyTokens(
								attributes.getProperty("value"), state
										.getGlobalProps());
						state.getDsProps().setProperty(name, value);
					}
				});
		
        // 处理节点/sqlMapConfig/transactionManager/dataSource/end()，动态实例化dataSource对象，然后进行属性初始化
		parser.addNodelet("/sqlMapConfig/transactionManager/dataSource/end()",
				new Nodelet() {
					public void process(Node node) throws Exception {
						state.getConfig().getErrorContext().setActivity(
								"configuring the data source");

						Properties attributes = NodeletUtils.parseAttributes(
								node, state.getGlobalProps());

						String type = attributes.getProperty("type");
						Properties props = state.getDsProps();

						type = state.getConfig().getTypeHandlerFactory()
								.resolveAlias(type);
						try {
							state.getConfig().getErrorContext().setMoreInfo(
									"Check the data source type or class.");
							DataSourceFactory dsFactory = (DataSourceFactory) Resources
									.instantiate(type);
							state.getConfig().getErrorContext().setMoreInfo(
											"Check the data source properties or configuration.");
							dsFactory.initialize(props);
							state.setDataSource(dsFactory.getDataSource());
							state.getConfig().getErrorContext().setMoreInfo(
									null);
						} catch (Exception e) {
							if (e instanceof SqlMapException) {
								throw (SqlMapException) e;
							} else {
								throw new SqlMapException(
										"Error initializing DataSource.  Could not instantiate DataSourceFactory.  Cause: "
												+ e, e);
							}
						}
					}
				});
	}

	
    //处理节点/sqlMapConfig/sqlMap，去获取sqlMap的映射文件，然后对各个映射文件进行读取处理
	protected void addSqlMapNodelets() {
		parser.addNodelet("/sqlMapConfig/sqlMap", new Nodelet() {
			public void process(Node node) throws Exception {
				state.getConfig().getErrorContext().setActivity(
						"loading the SQL Map resource");

				Properties attributes = NodeletUtils.parseAttributes(node,
						state.getGlobalProps());

				String resource = attributes.getProperty("resource");
				String url = attributes.getProperty("url");

				if (usingStreams) {
					//采用字节流模式来读取文件
					InputStream inputStream = null;
					if (resource != null) {
						state.getConfig().getErrorContext().setResource(
								resource);
						inputStream = Resources.getResourceAsStream(resource);
					} else if (url != null) {
						state.getConfig().getErrorContext().setResource(url);
						inputStream = Resources.getUrlAsStream(url);
					} else {
						throw new SqlMapException(
								"The <sqlMap> element requires either a resource or a url attribute.");
					}

					new SqlMapParser(state).parse(inputStream);
				} else {
                     //	采用字符流模式来读取文件
					Reader reader = null;
					if (resource != null) {
						state.getConfig().getErrorContext().setResource(
								resource);
						reader = Resources.getResourceAsReader(resource);
					} else if (url != null) {
						state.getConfig().getErrorContext().setResource(url);
						reader = Resources.getUrlAsReader(url);
					} else {
						throw new SqlMapException(
								"The <sqlMap> element requires either a resource or a url attribute.");
					}

					new SqlMapParser(state).parse(reader);
				}
			}
		});
	}

    //	处理节点/sqlMapConfig/resultObjectFactory，初始化新的resultObjectFactory
	private void addResultObjectFactoryNodelets() {
		parser.addNodelet("/sqlMapConfig/resultObjectFactory", new Nodelet() {
			public void process(Node node) throws Exception {
				Properties attributes = NodeletUtils.parseAttributes(node,
						state.getGlobalProps());
				String type = attributes.getProperty("type");

				state.getConfig().getErrorContext().setActivity(
						"configuring the Result Object Factory");
				ResultObjectFactory rof;
				try {
					rof = (ResultObjectFactory) Resources.instantiate(type);
					state.getConfig().setResultObjectFactory(rof);
				} catch (Exception e) {
					throw new SqlMapException(
							"Error instantiating resultObjectFactory: " + type,
							e);
				}

			}
		});
		parser.addNodelet("/sqlMapConfig/resultObjectFactory/property",
				new Nodelet() {
					public void process(Node node) throws Exception {
						Properties attributes = NodeletUtils.parseAttributes(
								node, state.getGlobalProps());
						String name = attributes.getProperty("name");
						String value = NodeletUtils.parsePropertyTokens(
								attributes.getProperty("value"), state
										.getGlobalProps());
						state.getConfig().getDelegate()
								.getResultObjectFactory().setProperty(name,
										value);
					}
				});
	}

}
