/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.axiom.om.impl.dom;

import junit.framework.TestCase;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.dom.factory.OMDOMFactory;
import org.apache.axiom.om.impl.dom.jaxp.DOOMDocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;

public class ElementImplTest extends TestCase {
    public void testSetText() {
        OMDOMFactory factory = new OMDOMFactory();
        String localName = "TestLocalName";
        String namespace = "http://ws.apache.org/axis2/ns";
        String prefix = "axis2";
        OMElement elem = factory.createOMElement(localName, namespace, prefix);

        String text = "The quick brown fox jumps over the lazy dog";

        elem.setText(text);

        assertEquals("Text value mismatch", text, elem.getText());

    }

    public void testSerialize() throws Exception {
        OMDOMFactory factory = new OMDOMFactory();
        String localName = "TestLocalName";
        String namespace = "http://ws.apache.org/axis2/ns";
        String prefix = "axis2";
        String tempText = "The quick brown fox jumps over the lazy dog";
        String textToAppend = " followed by another";

        OMElement elem = factory.createOMElement(localName, namespace, prefix);
        OMText textNode = factory.createOMText(elem, tempText);

        ((Text) textNode).appendData(textToAppend);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        elem.serialize(baos);
        String xml = new String(baos.toByteArray());
        assertEquals("Incorrect serialized xml", 0, xml.indexOf("<axis2:TestLocalName"));
    }

    public void testAddChild() {
        OMDOMFactory factory = new OMDOMFactory();
        String localName = "TestLocalName";
        String childLocalName = "TestChildLocalName";
        String namespace = "http://ws.apache.org/axis2/ns";
        String prefix = "axis2";

        OMElement elem = factory.createOMElement(localName, namespace, prefix);
        OMElement childElem = factory.createOMElement(childLocalName, namespace, prefix);

        elem.addChild(childElem);

        Iterator it = elem.getChildrenWithName(new QName(namespace, childLocalName));

        int count = 0;
        while (it.hasNext()) {
            OMElement child = (OMElement) it.next();
            assertEquals("Child local name mismatch", childLocalName, child.getLocalName());
            assertEquals("Child namespace mismatch", namespace,
                         child.getNamespace().getNamespaceURI());
            count ++;
        }
        assertEquals("In correct number of children", 1, count);
    }

    public void testAppendChild() throws Exception {
        DOMTestUtil.execute(new DOMTestUtil.Test() {
            public void execute(DocumentBuilderFactory dbf) throws Exception {
                String elementName = "TestElem";
                String childElemName = "TestChildElem";
                String childTextValue = "text value of the child text node";
        
                //Apending am Element node
                Document doc = dbf.newDocumentBuilder().newDocument();
                Element elem = doc.createElement(elementName);
                Element childElem = doc.createElement(childElemName);
        
                elem.appendChild(childElem);
        
                Element addedChild = (Element) elem.getFirstChild();
                assertNotNull("Child Element node missing", addedChild);
                assertEquals("Incorre node object", childElem, addedChild);
        
                elem = doc.createElement(elementName);
                Text text = doc.createTextNode(childTextValue);
                elem.appendChild(text);
        
                Text addedTextnode = (Text) elem.getFirstChild();
                assertNotNull("Child Text node missing", addedTextnode);
                assertEquals("Incorrect node object", text, addedTextnode);
            }
        });
    }

    /** Testing the NodeList returned with the elements's children */
    public void testGetElementsbyTagName() throws Exception {
        DOMTestUtil.execute(new DOMTestUtil.Test() {
            public void execute(DocumentBuilderFactory dbf) throws Exception {
                String childElementLN = "Child";
        
                Document doc = dbf.newDocumentBuilder().newDocument();
                Element docElem = doc.getDocumentElement();
                assertNull("The document element shoudl be null", docElem);
        
                docElem = doc.createElement("Test");
                docElem.appendChild(doc.createElement(childElementLN));
                docElem.appendChild(doc.createElement(childElementLN));
                docElem.appendChild(doc.createElement(childElementLN));
                docElem.appendChild(doc.createElement(childElementLN));
                docElem.appendChild(doc.createElement(childElementLN));
                docElem.appendChild(doc.createElement(childElementLN));
                docElem.appendChild(doc.createElement(childElementLN));
        
                NodeList list = docElem.getElementsByTagName(childElementLN);
        
                assertEquals("Incorrect number of child elements", 7, list.getLength());
            }
        });
    }

    public void testGetElementsbyTagNameNS() throws Exception {
        DOMTestUtil.execute(new DOMTestUtil.Test() {
            public void execute(DocumentBuilderFactory dbf) throws Exception {
                String childElementQN = "test:Child";
                String childElementLN = "Child";
                String childElementNS = "http://ws.apache.org/ns/axis2/dom";
        
                Document doc = dbf.newDocumentBuilder().newDocument();
                Element docElem = doc.getDocumentElement();
                assertNull("The document element shoudl be null", docElem);
        
                docElem = doc.createElementNS("http://test.org", "test:Test");
        
                docElem.appendChild(doc.createElementNS(childElementNS, childElementQN));
                docElem.appendChild(doc.createElementNS(childElementNS, childElementQN));
                docElem.appendChild(doc.createElementNS(childElementNS, childElementQN));
                docElem.appendChild(doc.createElementNS(childElementNS, childElementQN));
                docElem.appendChild(doc.createElementNS(childElementNS, childElementQN));
                docElem.appendChild(doc.createElementNS(childElementNS, childElementQN));
                docElem.appendChild(doc.createElementNS(childElementNS, childElementQN));
        
                NodeList list = docElem.getElementsByTagNameNS(childElementNS, childElementLN);
        
                assertEquals("Incorrect number of child elements", 7, list.getLength());
            }
        });
    }
}
