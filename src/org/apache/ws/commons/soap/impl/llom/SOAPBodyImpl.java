/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ws.commons.soap.impl.llom;

import org.apache.ws.commons.om.OMConstants;
import org.apache.ws.commons.om.OMElement;
import org.apache.ws.commons.om.OMException;
import org.apache.ws.commons.om.OMNamespace;
import org.apache.ws.commons.om.OMNode;
import org.apache.ws.commons.om.OMXMLParserWrapper;
import org.apache.ws.commons.soap.SOAP11Constants;
import org.apache.ws.commons.soap.SOAP12Constants;
import org.apache.ws.commons.soap.SOAPBody;
import org.apache.ws.commons.soap.SOAPConstants;
import org.apache.ws.commons.soap.SOAPEnvelope;
import org.apache.ws.commons.soap.SOAPFactory;
import org.apache.ws.commons.soap.SOAPFault;
import org.apache.ws.commons.soap.SOAPProcessingException;

/**
 * Class SOAPBodyImpl
 */
public abstract class SOAPBodyImpl extends SOAPElement
        implements SOAPBody, OMConstants {
    /**
     * Field hasSOAPFault
     */
    private boolean hasSOAPFault = false;

    protected SOAPBodyImpl(String localName, OMNamespace ns, 
            SOAPFactory factory) {
        super(localName, ns, factory);
    }

    /**
     * @param envelope
     */
    public SOAPBodyImpl(SOAPEnvelope envelope, SOAPFactory factory) 
                            throws SOAPProcessingException {
        super(envelope, SOAPConstants.BODY_LOCAL_NAME, true, factory);

    }

    /**
     * Constructor SOAPBodyImpl
     *
     * @param envelope
     * @param builder
     */
    public SOAPBodyImpl(SOAPEnvelope envelope, OMXMLParserWrapper builder, 
            SOAPFactory factory) {
        super(envelope, SOAPConstants.BODY_LOCAL_NAME, builder, factory);
    }

    /**
     * Creates a new <code>SOAPFault</code> object and adds it to
     * this <code>SOAPBody</code> object.
     *
     * @param e
     * @return the new <code>SOAPFault</code> object
     * @throws org.apache.ws.commons.om.OMException
     *                     if there is a SOAP error
     * @throws OMException
     */
    public abstract SOAPFault addFault(Exception e) throws OMException;

    /**
     * Indicates whether a <code>SOAPFault</code> object exists in
     * this <code>SOAPBody</code> object.
     *
     * @return <code>true</code> if a <code>SOAPFault</code> object exists in
     *         this <code>SOAPBody</code> object; <code>false</code>
     *         otherwise
     */
    public boolean hasFault() {
        if (hasSOAPFault) {
            return true;
        } else {
            OMElement element = getFirstElement();
            if (element != null
                    &&
                    SOAPConstants.SOAPFAULT_LOCAL_NAME.equals(
                            element.getLocalName())
                    &&
                    (SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(
                            element.getNamespace().getName())
                    ||
                    SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(
                            element.getNamespace().getName()))) {  //added this line
                hasSOAPFault = true;
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Returns the <code>SOAPFault</code> object in this <code>SOAPBody</code>
     * object.
     *
     * @return the <code>SOAPFault</code> object in this <code>SOAPBody</code>
     *         object
     */
    public SOAPFault getFault() {
        OMElement element = getFirstElement();
        if (hasSOAPFault) {
            return (SOAPFault) element;
        } else if (element != null
                &&
                SOAPConstants.SOAPFAULT_LOCAL_NAME.equals(
                        element.getLocalName())
                &&
                (SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(
                        element.getNamespace().getName())
                ||
                SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(
                        element.getNamespace().getName()))) {     //added this line
            hasSOAPFault = true;
            return (SOAPFault) element;
        } else {
            return null;
        }

    }

    /**
     * @param soapFault
     * @throws org.apache.ws.commons.om.OMException
     *
     * @throws OMException
     */
    public void addFault(SOAPFault soapFault) throws OMException {
        if (hasSOAPFault) {
            throw new OMException(
                    "SOAP Body already has a SOAP Fault and there can not be more than one SOAP fault");
        }
        addChild(soapFault);
        hasSOAPFault = true;
    }

    protected void checkParent(OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAPEnvelopeImpl)) {
            throw new SOAPProcessingException(
                    "Expecting an implementation of SOAP Envelope as the parent. But received some other implementation");
        }
    }

    public OMNode detach() throws OMException {
        throw new SOAPProcessingException("Can not detach SOAP Body, SOAP Envelope must have a Body !!");
    }
}
