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
package org.apache.axiom.om.impl.mixin;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.intf.AxiomCharacterDataNode;

public aspect AxiomCharacterDataNodeSupport {
    public final int AxiomCharacterDataNode.getType() {
        return coreIsIgnorable() ? OMNode.SPACE_NODE : OMNode.TEXT_NODE;
    }

    public final void AxiomCharacterDataNode.internalSerialize(XmlHandler handler, OMOutputFormat format, boolean cache) throws StreamException {
        handler.processCharacterData(coreGetCharacterData(), coreIsIgnorable());
    }

    public final void AxiomCharacterDataNode.serialize(XMLStreamWriter writer, boolean cache) throws XMLStreamException {
        writer.writeCharacters(coreGetCharacterData().toString());
    }
}
