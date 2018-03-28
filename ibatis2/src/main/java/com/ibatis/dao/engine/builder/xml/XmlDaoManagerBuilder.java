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
package com.ibatis.dao.engine.builder.xml;

import com.ibatis.common.io.ReaderInputStream;
import com.ibatis.common.resources.Resources;
import com.ibatis.dao.client.Dao;
import com.ibatis.dao.client.DaoException;
import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.engine.impl.DaoContext;
import com.ibatis.dao.engine.impl.DaoImpl;
import com.ibatis.dao.engine.impl.StandardDaoManager;
import com.ibatis.dao.engine.transaction.DaoTransactionManager;
import com.ibatis.dao.engine.transaction.external.ExternalDaoTransactionManager;
//import com.ibatis.dao.engine.transaction.hibernate.HibernateDaoTransactionManager;
import com.ibatis.dao.engine.transaction.jdbc.JdbcDaoTransactionManager;
import com.ibatis.dao.engine.transaction.jta.JtaDaoTransactionManager;
import com.ibatis.dao.engine.transaction.sqlmap.SqlMapDaoTransactionManager;
import org.w3c.dom.*;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * NOT THREAD SAFE.  USE SEPARATE INSTANCES PER THREAD.
 */
public class XmlDaoManagerBuilder {


    private static final String DAO_CONFIG_ELEMENT = "daoConfig";
    private static final String PROPERTIES_ELEMENT = "properties";
    private static final String CONTEXT_ELEMENT = "context";
    private static final String TRANS_MGR_ELEMENT = "transactionManager";
    private static final String PROPERTY_ELEMENT = "property";
    private static final String DAO_ELEMENT = "dao";

    private Properties properties = null;
    private boolean validationEnabled = true;
    private Map typeAliases = new HashMap();

    public XmlDaoManagerBuilder() {
        typeAliases.put("EXTERNAL", ExternalDaoTransactionManager.class.getName());
//    typeAliases.put("HIBERNATE", HibernateDaoTransactionManager.class.getName());
        typeAliases.put("JDBC", JdbcDaoTransactionManager.class.getName());
        typeAliases.put("JTA", JtaDaoTransactionManager.class.getName());
        typeAliases.put("OJB", "com.ibatis.dao.engine.transaction.ojb.OjbBrokerTransactionManager");
        typeAliases.put("SQLMAP", SqlMapDaoTransactionManager.class.getName());
//    typeAliases.put("TOPLINK", "com.ibatis.dao.engine.transaction.toplink.ToplinkDaoTransactionManager");
    }

    public DaoManager buildDaoManager(Reader reader, Properties props)
            throws DaoException {
        properties = props;
        return buildDaoManager(reader);
    }

    public DaoManager buildDaoManager(Reader reader)
            throws DaoException {
        StandardDaoManager daoManager = new StandardDaoManager();

        try {

            Document doc = getDoc(reader);
            Element root = (Element) doc.getLastChild();

            String rootname = root.getNodeName();
            if (!DAO_CONFIG_ELEMENT.equals(rootname)) {
                throw new IOException("Error while configuring DaoManager.  The root tag of the DAO configuration XML " +
                        "document must be '" + DAO_CONFIG_ELEMENT + "'.");
            }

            //解析第一层节点信息
            NodeList children = root.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {

                    if (CONTEXT_ELEMENT.equals(child.getNodeName())) {
                        // 解析第一层节点中context节点的信息，首先创建一个DaoContext对象，然后加到StandardDaoManager对象中
                        DaoContext daoContext = parseContext((Element) child, daoManager);
                        daoManager.addContext(daoContext);
                    } else if (PROPERTIES_ELEMENT.equals(child.getNodeName())) {
                        // 解析第一层节点中property节点的信息
                        Properties attributes = parseAttributes(child);
                        if (attributes.containsKey("resource")) {
                            String resource = attributes.getProperty("resource");
                            if (properties == null) {
                                properties = Resources.getResourceAsProperties(resource);
                            } else {
                                Properties tempProps = Resources.getResourceAsProperties(resource);
                                tempProps.putAll(properties);
                                properties = tempProps;
                            }
                        } else if (attributes.containsKey("url")) {
                            String url = attributes.getProperty("url");
                            if (properties == null) {
                                properties = Resources.getUrlAsProperties(url);
                            } else {
                                Properties tempProps = Resources.getUrlAsProperties(url);
                                tempProps.putAll(properties);
                                properties = tempProps;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new DaoException("Error while configuring DaoManager.  Cause: " + e.toString(), e);
        }
        return daoManager;
    }

    public boolean isValidationEnabled() {
        return validationEnabled;
    }

    public void setValidationEnabled(boolean validationEnabled) {
        this.validationEnabled = validationEnabled;
    }

    //解析第一层节点中context节点的信息
    private DaoContext parseContext(Element contextElement, StandardDaoManager daoManager)
            throws DaoException {
        DaoContext daoContext = new DaoContext();

        daoContext.setDaoManager(daoManager);
        String id = contextElement.getAttribute("id");
        if (id != null && id.length() > 0) {
            daoContext.setId(id);
        }

        NodeList children = contextElement.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                //  解析context节点中transactionManager节点的信息
                if (TRANS_MGR_ELEMENT.equals(child.getNodeName())) {
                    DaoTransactionManager txMgr = parseTransactionManager((Element) child);
                    daoContext.setTransactionManager(txMgr);
                } else if (DAO_ELEMENT.equals(child.getNodeName())) {
                    // 解析context节点中dao节点的信息，首先创建一个Dao对象，然后加到DaoContext对象中
                    DaoImpl daoImpl = parseDao((Element) child, daoManager, daoContext);
                    daoContext.addDao(daoImpl);
                }
            }
        }

        return daoContext;
    }

    //解析context节点中transactionManager节点的信息
    private DaoTransactionManager parseTransactionManager(Element transPoolElement)
            throws DaoException {
        DaoTransactionManager txMgr = null;

        Properties attributes = parseAttributes(transPoolElement);

        String implementation = attributes.getProperty("type");
        implementation = resolveAlias(implementation);

        //根据字符名称动态实例化一个DaoTransactionManager对象
        try {
            txMgr = (DaoTransactionManager) Resources.classForName(implementation).newInstance();
        } catch (Exception e) {
            throw new DaoException("Error while configuring DaoManager.  Cause: " + e.toString(), e);
        }

        Properties props = properties;

        if (props == null) {
            props = parsePropertyElements(transPoolElement);
        } else {
            props.putAll(parsePropertyElements(transPoolElement));
        }

        //初始化DaoTransactionManager对象
        txMgr.configure(props);

        if (txMgr == null) {
            throw new DaoException("Error while configuring DaoManager.  Some unknown condition caused the " +
                    "DAO Transaction Manager to be null after configuration.");
        }

        return txMgr;
    }


    //解析context节点中dao节点的信息
    private DaoImpl parseDao(Element element, StandardDaoManager daoManager, DaoContext daoContext) {
        DaoImpl daoImpl = new DaoImpl();
        if (element.getNodeType() == Node.ELEMENT_NODE) {
            if (DAO_ELEMENT.equals(element.getNodeName())) {

                Properties attributes = parseAttributes(element);

                try {
                    String iface = attributes.getProperty("interface");
                    String impl = attributes.getProperty("implementation");
                    daoImpl.setDaoManager(daoManager);
                    daoImpl.setDaoContext(daoContext);
                    daoImpl.setDaoInterface(Resources.classForName(iface));
                    daoImpl.setDaoImplementation(Resources.classForName(impl));

                    Class daoClass = daoImpl.getDaoImplementation();
                    Dao dao = null;


                    // 根据字符名称动态实例化一个dao对象，先是生成构造方对象，从构造对象实例化对象
                    try {
                        Constructor constructor = daoClass.getConstructor(new Class[]{DaoManager.class});
                        dao = (Dao) constructor.newInstance(new Object[]{daoManager});
                    } catch (Exception e) {
                        dao = (Dao) daoClass.newInstance();
                    }

                    daoImpl.setDaoInstance(dao);

                    // 生成dao的代理
                    daoImpl.initProxy();
                } catch (Exception e) {
                    throw new DaoException("Error configuring DAO.  Cause: " + e, e);
                }
            }
        }
        return daoImpl;
    }

    private Properties parsePropertyElements(Element propsParentElement) {

        Properties props = new Properties();

        NodeList children = propsParentElement.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);

            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if (PROPERTY_ELEMENT.equals(child.getNodeName())) {

                    Properties attributes = parseAttributes(child);

                    String name = attributes.getProperty("name");
                    String value = attributes.getProperty("value");

                    props.setProperty(name, value);
                }
            }

        }

        return props;
    }

    private Properties parseAttributes(Node n) {
        Properties attributes = new Properties();
        NamedNodeMap attributeNodes = n.getAttributes();
        for (int i = 0; i < attributeNodes.getLength(); i++) {
            Node attribute = attributeNodes.item(i);
            String value = parsePropertyTokens(attribute.getNodeValue());
            attributes.put(attribute.getNodeName(), value);
        }
        return attributes;
    }

    // 处理属性中带有"${}"中的内容，转化到一个properties文件去读取相关信息
    private String parsePropertyTokens(String string) {
        final String OPEN = "${";
        final String CLOSE = "}";
        String newString = string;
        if (newString != null && properties != null) {
            int start = newString.indexOf(OPEN);
            int end = newString.indexOf(CLOSE);

            while (start > -1 && end > start) {
                String prepend = newString.substring(0, start);
                String append = newString.substring(end + CLOSE.length());
                String propName = newString.substring(start + OPEN.length(), end);
                String propValue = properties.getProperty(propName);
                if (propValue == null) {
                    newString = prepend + append;
                } else {
                    newString = prepend + propValue + append;
                }
                start = newString.indexOf(OPEN);
                end = newString.indexOf(CLOSE);
            }
        }
        return newString;
    }


    private Document getDoc(Reader reader) {
        try {
            // Configuration
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            // 指定由此代码生成的解析器将提供对 XML 名称空间的支持。
            dbf.setNamespaceAware(false);
            // 开启验证属性。
            dbf.setValidating(true);
            // 指定由此代码生成的解析器将忽略注释。
            dbf.setIgnoringComments(true);
            // 指定由此工厂创建的解析器在解析 XML 文档时，必须删除元素内容中的空格
            dbf.setIgnoringElementContentWhitespace(true);
            // 指定由此代码生成的解析器将把 CDATA 节点转换为 Text 节点，并将其附加到相邻的 Text 节点。
            dbf.setCoalescing(false);
            // 指定由此代码产生的解析器将扩展实体引用节点。
            dbf.setExpandEntityReferences(false);

            OutputStreamWriter errorWriter = new OutputStreamWriter(System.err);

            DocumentBuilder db = dbf.newDocumentBuilder();
            // 注册错误事件处理程序。
            db.setErrorHandler(new SimpleErrorHandler(new PrintWriter(errorWriter, true)));
            // 注册自定义的实体解析器。
            db.setEntityResolver(new DaoClasspathEntityResolver());

            // Parse input file  解析XML文档
            Document doc = db.parse(new ReaderInputStream(reader));
            return doc;
        } catch (Exception e) {
            throw new RuntimeException("XML Parser Error.  Cause: " + e);
        }
    }

    private String resolveAlias(String string) {
        String newString = null;
        if (typeAliases.containsKey(string)) {
            newString = (String) typeAliases.get(string);
        }
        if (newString != null) {
            string = newString;
        }
        return string;
    }

    /**
     * **********************************
     * ******* SimpleErrorHandler *******
     * **********************************
     */

    // Error handler to report errors and warnings
    private static class SimpleErrorHandler implements ErrorHandler {
        /**
         * Error handler output goes here
         */
        private PrintWriter out;

        SimpleErrorHandler(PrintWriter out) {
            this.out = out;
        }

        /**
         * Returns a string describing parse exception details
         */
        private String getParseExceptionInfo(SAXParseException spe) {
            String systemId = spe.getSystemId();
            if (systemId == null) {
                systemId = "null";
            }
            String info = "URI=" + systemId +
                    " Line=" + spe.getLineNumber() +
                    ": " + spe.getMessage();
            return info;
        }

        // The following methods are standard SAX ErrorHandler methods.
        // See SAX documentation for more info.

        public void warning(SAXParseException spe) throws SAXException {
            out.println("Warning: " + getParseExceptionInfo(spe));
        }

        public void error(SAXParseException spe) throws SAXException {
            String message = "Error: " + getParseExceptionInfo(spe);
            throw new SAXException(message);
        }

        public void fatalError(SAXParseException spe) throws SAXException {
            String message = "Fatal Error: " + getParseExceptionInfo(spe);
            throw new SAXException(message);
        }
    }

}
