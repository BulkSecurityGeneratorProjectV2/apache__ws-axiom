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

package org.apache.axiom.ts.om.builder;

import java.io.OutputStream;

import javax.activation.DataHandler;

import org.apache.axiom.blob.Blobs;
import org.apache.axiom.blob.MemoryBlob;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.testutils.activation.RandomDataSource;

public class TestRegisterCustomBuilderForPayloadJAXBPlain
        extends RegisterCustomBuilderForPayloadJAXBTestCase {
    public TestRegisterCustomBuilderForPayloadJAXBPlain(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        DataHandler dh = new DataHandler(new RandomDataSource(10000));
        MemoryBlob blob = Blobs.createMemoryBlob();
        OutputStream out = blob.getOutputStream();
        createTestDocument(dh).serialize(out);
        out.close();
        test(
                dh,
                OMXMLBuilderFactory.createOMBuilder(
                        metaFactory.getOMFactory(), blob.getInputStream()),
                false);
    }
}
