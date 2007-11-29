/*
 * Copyright (c) 2007, Sianny Halim. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.qi4j.test.model1;

import org.qi4j.spi.composite.InvalidCompositeException;
import org.qi4j.test.AbstractQi4jTest;

public class CompositeFactoryImplTest extends AbstractQi4jTest
{
    protected void setUp()
        throws Exception
    {
        super.setUp();
    }

    public void testNewInstanceNotExtendingComposite()
        throws Exception
    {

        try
        {
            Class aClass = Composition8.class;
            Composition8 composition8 = (Composition8) compositeBuilderFactory.newCompositeBuilder( aClass ).newInstance();
            fail( "CompositeBuilderFactory.newInstance() should return CompositeInstantiationException when creating a new instance for " + aClass.getName() );
        }
        catch( InvalidCompositeException e )
        {
            // Correct
        }
    }

    public void testNewComposition9()
        throws Exception
    {
        try
        {
            Composition9 composition9 = compositeBuilderFactory.newCompositeBuilder( Composition9.class ).newInstance();
            composition9.setValue( "test value" );
        }
        catch( Exception e )
        {
            e.printStackTrace();
            fail( "Fail to instantiate composite: " + Composition9.class );
        }
    }
}
