package com.wtz.tools.utils.data_transfer_format;

import android.content.Context;
import android.util.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

/**
 * HTML 的设计目标是显示数据并集中于数据外观，用于显示数据；
 * XML 的设计目标是描述数据并集中于数据的内容，用于传输和存放数据；
 */
public class XmlDemo {

    private static final String TEST_XML_NAME = "river.xml";

    private static final String TAG_RIVER = "river";
    private static final String TAG_INTRODUCTION = "introduction";
    private static final String TAG_IMAGEURL = "imageurl";

    private static final String ATTRIBUTE_NAME = "name";
    private static final String ATTRIBUTE_LENGTH = "length";

    public static void test(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                parseByDom(context);
                parseBySax(context);
                parseByPull(context);
            }
        }).start();
    }

    /**
     * DOM需要加载整个文档和构造树形结构，然后才可以检索和更新节点信息。
     * 利用DOM中的对象，可以对XML文档进行读取、搜索、修改、添加和删除等操作。
     * 由于DOM在内存中以树形结构存放，因此检索和更新效率会更高。
     * 但是对于特别大的文档，解析和加载整个文档将会很耗资源。
     */
    private static void parseByDom(Context context) {
        InputStream inputStream = null;
        try {
            inputStream = context.getResources().getAssets().open(TEST_XML_NAME);
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(inputStream);
            Element root = document.getDocumentElement();
            NodeList nodes = root.getElementsByTagName(TAG_RIVER);
            int size = nodes.getLength();
            for (int i = 0; i < size; i++) {
                System.out.println("DOM---i: " + i);
                Element river = (Element) nodes.item(i);
                System.out.println("DOM---name: " + river.getAttribute(ATTRIBUTE_NAME));
                System.out.println("DOM---length: " + river.getAttribute(ATTRIBUTE_LENGTH));

                Element introduction = (Element) river.getElementsByTagName(TAG_INTRODUCTION).item(0);
                System.out.println("DOM---introduction: " + introduction.getFirstChild().getNodeValue());

                Element imageUrl = (Element) river.getElementsByTagName(TAG_IMAGEURL).item(0);
                System.out.println("DOM---imageUrl: " + imageUrl.getFirstChild().getNodeValue());
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
            }
        }
    }

    /**
     * SAX(Simple API for XML)解析器是一种基于事件的解析器。
     * 事件驱动的流式解析方式是，从文件的开始顺序解析到文档的结束，不可暂停或倒退。
     * 当事件源产生事件后，调用事件处理器相应的处理方法，一个事件就可以得到处理。
     * SAX解析器的优点是解析速度快，占用内存少。非常适合在Android移动设备中使用。
     */
    private static void parseBySax(Context context) {
        InputStream inputStream = null;
        try {
            inputStream = context.getResources().getAssets().open(TEST_XML_NAME);
            XMLReader reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            reader.setContentHandler(new MySaxHandler());
            reader.parse(new InputSource(inputStream));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
            }
        }
    }

    static class MySaxHandler extends DefaultHandler {

        @Override
        public void startDocument() throws SAXException {
            System.out.println("SAX---startDocument");
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            System.out.println("SAX---startElement...uri=" + uri + "; localName=" + localName + "; qName=" + qName);
            // 例如：<sina:website sina:blog="blog.sina.com">新浪</sina:website>
            // blog 就是 localName，sina:blog 就是 qName；
            // sax2 支持 LocalName，sax 不支持 LocalName，用sax解析后：LocalName=QName="sina:blog"
            if (TAG_RIVER.equalsIgnoreCase(qName) && attributes != null) {
                System.out.println("SAX---river name: " + attributes.getValue(ATTRIBUTE_NAME));
                System.out.println("SAX---river length: " + attributes.getValue(ATTRIBUTE_LENGTH));
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            System.out.println("SAX---characters...length=" + length + "; content=" + new String(ch, start, length));
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            System.out.println("SAX---endElement...uri=" + uri + "; localName=" + localName + "; qName=" + qName);
        }

        @Override
        public void endDocument() throws SAXException {
            System.out.println("SAX---endDocument");
        }

    }

    /**
     * Android附带了一个pull解析器，其工作方式类似于SAX。
     * 它允许用户的应用程序代码从解析器中主动获取事件，这与SAX解析器自动将事件推入处理程序相反。
     * 在PULL解析过程中返回的是数字，且我们需要自己获取产生的事件然后做相应的操作，
     * 而不像SAX那样由处理器触发一种事件的方法，执行我们的代码。
     */
    private static void parseByPull(Context context) {
        InputStream inputStream = null;
        try {
            inputStream = context.getResources().getAssets().open(TEST_XML_NAME);
            XmlPullParser xmlParser = Xml.newPullParser();
            xmlParser.setInput(inputStream, "utf-8");
            int eventType = xmlParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        System.out.println("PULL---START_DOCUMENT");
                        break;

                    case XmlPullParser.START_TAG:
                        String startTag = xmlParser.getName();
                        System.out.println("PULL---startTag: " + startTag);
                        if (TAG_RIVER.equalsIgnoreCase(startTag)) {
                            System.out.println("PULL---name: " + xmlParser.getAttributeValue(null, ATTRIBUTE_NAME));
                            System.out.println("PULL---length: " + xmlParser.getAttributeValue(null, ATTRIBUTE_LENGTH));
                        } else if (TAG_INTRODUCTION.equalsIgnoreCase(startTag)) {
                            System.out.println("PULL---introduction: " + xmlParser.nextText());
                        } else if (TAG_IMAGEURL.equalsIgnoreCase(startTag)) {
                            System.out.println("PULL---imageurl: " + xmlParser.nextText());
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        String endTag = xmlParser.getName();
                        System.out.println("PULL---endTag: " + endTag);
                        break;

                    default:
                        break;
                }
                eventType = xmlParser.next();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
            }
        }
    }

}
