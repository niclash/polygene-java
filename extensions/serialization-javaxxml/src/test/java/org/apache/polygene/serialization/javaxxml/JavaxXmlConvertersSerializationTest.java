/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.polygene.serialization.javaxxml;

import java.io.StringReader;
import java.util.Optional;
import javax.xml.parsers.DocumentBuilder;
import org.apache.polygene.api.injection.scope.Service;
import org.apache.polygene.bootstrap.ModuleAssembly;
import org.apache.polygene.serialization.javaxxml.assembly.JavaxXmlSerializationAssembler;
import org.apache.polygene.test.serialization.AbstractConvertersSerializationTest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class JavaxXmlConvertersSerializationTest extends AbstractConvertersSerializationTest
{
    @Override
    public void assemble( ModuleAssembly module )
    {
        new JavaxXmlSerializationAssembler().assemble( module );
        super.assemble( module );
    }

    @Service
    private JavaxXmlFactories xmlFactories;

    @Override
    protected String getStringFromValueState( String state, String key ) throws Exception
    {
        JavaxXmlSettings settings = serviceFinder.findService( JavaxXmlSerialization.class )
                                                 .metaInfo( JavaxXmlSettings.class );
        settings = JavaxXmlSettings.orDefault( settings );
        DocumentBuilder docBuilder = xmlFactories.documentBuilderFactory().newDocumentBuilder();
        Document doc = docBuilder.parse( new InputSource( new StringReader( state ) ) );
        Optional<Element> stateElement = JavaxXml.firstChildElementNamed( doc, settings.getRootTagName() );
        if( stateElement.isPresent() )
        {
            Optional<Element> valueNode = JavaxXml.firstChildElementNamed( stateElement.get(),
                                                                           settings.getValueTagName() );
            if( valueNode.isPresent() )
            {
                Optional<Element> element = JavaxXml.firstChildElementNamed( valueNode.get(), key );
                if( element.isPresent() )
                {
                    return element.get().getFirstChild().getNodeValue();
                }
            }
        }
        return null;
    }
}
