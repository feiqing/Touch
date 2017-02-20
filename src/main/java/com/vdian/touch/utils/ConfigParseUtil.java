package com.vdian.touch.utils;

import com.google.common.base.Strings;
import com.vdian.touch.constant.Constant;
import com.vdian.touch.exceptions.TouchException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author jifang
 * @since 2016/11/23 下午3:51.
 */
public class ConfigParseUtil {

    public static ConfigEntity parseConfigFile(InputStream is) {

        if (is == null) {
            throw new TouchException("touch need a config file touch.xml on classpath");
        }

        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);

            Set<String> packages;
            NodeList packageNodes = document.getElementsByTagName(Constant.PACKAGE);
            if (packageNodes.getLength() > 0) {
                packages = toStrSet(packageNodes);
            } else {
                throw new TouchException("touch.xml need at least one <package .../> item");
            }

            Set<String> converters;
            NodeList converterNodes = document.getElementsByTagName(Constant.CONVERTER);
            if (converterNodes.getLength() > 0) {
                converters = toStrSet(converterNodes);
            } else {
                converters = Collections.emptySet();
            }

            Map<String, Map<String, String>> switcherMap;
            NodeList switcherNodes = document.getElementsByTagName(Constant.SWITCHER);
            if (switcherNodes.getLength() > 0) {
                switcherMap = parseSwitchers(switcherNodes);
            } else {
                switcherMap = Collections.emptyMap();
            }

            return new ConfigEntity(converters, packages, switcherMap);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new TouchException(e);
        }
    }

    private static Set<String> toStrSet(NodeList nodeList) {
        Set<String> set = new HashSet<>();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            String text = nodeList.item(i).getTextContent().trim();
            if (!Strings.isNullOrEmpty(text)) {
                set.add(text);
            }
        }

        return set;
    }

    private static Map<String, Map<String, String>> parseSwitchers(NodeList switcherNodes) {
        Map<String, Map<String, String>> switchers = new HashMap<>();
        for (int i = 0; i < switcherNodes.getLength(); ++i) {
            Node switcherNode = switcherNodes.item(i);
            NamedNodeMap switcherAttributes = switcherNode.getAttributes();
            if (switcherAttributes != null) {
                String switcherClassName = switcherAttributes.getNamedItem("class").getTextContent();
                Map<String, String> configs = parseSwitcherConfigs(switcherNode.getChildNodes());

                switchers.put(switcherClassName, configs);
            }
        }

        return switchers;
    }

    private static Map<String, String> parseSwitcherConfigs(NodeList switcherConfigNodes) {
        Map<String, String> configs = new HashMap<>();
        for (int i = 0; i < switcherConfigNodes.getLength(); ++i) {
            NamedNodeMap attributes = switcherConfigNodes.item(i).getAttributes();
            if (attributes != null) {
                String[] keyValue = parseConfigAttr(attributes);
                configs.put(keyValue[0], keyValue[1]);
            }
        }

        return configs;
    }

    private static String[] parseConfigAttr(NamedNodeMap attributes) {
        String key = attributes.getNamedItem("key").getTextContent();
        String value = attributes.getNamedItem("value").getTextContent();
        return new String[]{key, value};
    }
}
