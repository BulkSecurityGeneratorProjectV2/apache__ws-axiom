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
package org.apache.axiom.om.impl.stream.ds;

import java.util.HashMap;
import java.util.Map;

import javax.activation.DataHandler;

import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.xop.AbstractXOPDecodingFilterHandler;
import org.apache.axiom.om.impl.intf.TextContent;
import org.apache.axiom.om.impl.stream.xop.XOPHandler;
import org.apache.axiom.util.UIDGenerator;

final class PushOMDataSourceXOPHandler extends AbstractXOPDecodingFilterHandler
        implements XOPHandler {
    private final Map<String, DataHandler> dataHandlers = new HashMap<String, DataHandler>();

    PushOMDataSourceXOPHandler(XmlHandler parent) {
        super(parent);
    }

    @Override
    public String prepareDataHandler(DataHandler dataHandler) {
        String contentID = UIDGenerator.generateContentId();
        dataHandlers.put(contentID, dataHandler);
        return contentID;
    }

    @Override
    protected Object buildCharacterData(String contentID) throws StreamException {
        DataHandler dataHandler = dataHandlers.get(contentID);
        if (dataHandler == null) {
            throw new StreamException("No DataHandler found for content ID " + contentID);
        }
        return new TextContent(contentID, dataHandler, true);
    }
}
