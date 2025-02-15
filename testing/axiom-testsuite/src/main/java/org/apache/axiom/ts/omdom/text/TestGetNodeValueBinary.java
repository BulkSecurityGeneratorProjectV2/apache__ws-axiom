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
package org.apache.axiom.ts.omdom.text;

import static com.google.common.truth.Truth.assertThat;

import java.io.ByteArrayInputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;

import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.testutils.activation.RandomDataSource;
import org.apache.axiom.testutils.io.IOTestUtils;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Text;

public class TestGetNodeValueBinary extends AxiomTestCase {
    public TestGetNodeValueBinary(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        DataSource ds = new RandomDataSource(666L, 1000);
        Text text = (Text) factory.createOMText(new DataHandler(ds), false);
        String nodeValue = text.getNodeValue();
        assertThat(nodeValue).isNotNull();
        IOTestUtils.compareStreams(
                ds.getInputStream(), new ByteArrayInputStream(Base64.decodeBase64(nodeValue)));
    }
}
