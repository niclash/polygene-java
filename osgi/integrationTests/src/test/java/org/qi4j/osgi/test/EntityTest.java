/*  Copyright 2008 Edward Yakop.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
* implied.
*
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.qi4j.osgi.test;

import org.osgi.framework.ServiceReference;
import org.qi4j.core.test.osgi.AnEntity;
import org.qi4j.entity.EntityCompositeNotFoundException;
import org.qi4j.entity.UnitOfWork;
import org.qi4j.entity.UnitOfWorkCompletionException;
import org.qi4j.entity.UnitOfWorkFactory;
import org.qi4j.property.Property;
import org.qi4j.structure.Module;

/**
 * @author edward.yakop@gmail.com
 * @since 0.5
 */
public final class EntityTest extends AbstractTest
{
    private UnitOfWorkFactory getUnitOfWorkFactory( ServiceReference moduleRef )
    {
        assertNotNull( moduleRef );

        Module module = (Module) bundleContext.getService( moduleRef );
        assertNotNull( module );

        return module.unitOfWorkFactory();
    }

    private String createNewEntity( UnitOfWorkFactory uowf )
        throws UnitOfWorkCompletionException
    {
        UnitOfWork uow = uowf.newUnitOfWork();
        AnEntity entity = uow.newEntity( AnEntity.class );
        assertNotNull( entity );

        String identity = entity.identity().get();

        Property<String> property = entity.property();
        assertNotNull( property );

        assertNull( property.get() );
        property.set( "abc" );
        assertEquals( "abc", property.get() );

        uow.complete();

        return identity;
    }

    public final void testCRUD()
        throws Throwable
    {
        ServiceReference moduleRef = getModuleServiceRef();
        UnitOfWorkFactory uowf = getUnitOfWorkFactory( moduleRef );

        // Test creational
        String identity = createNewEntity( uowf );

        // Test retrieval
        UnitOfWork work = uowf.newUnitOfWork();
        AnEntity entity = work.find( identity, AnEntity.class );
        assertNotNull( entity );

        // Test update
        String newPropValue = entity.property().get() + "a";
        entity.property().set( newPropValue );
        work.complete();

        work = uowf.newUnitOfWork();
        entity = work.find( identity, AnEntity.class );
        assertNotNull( entity );
        assertEquals( newPropValue, entity.property().get() );
        work.complete();

        // Test removal
        work = uowf.newUnitOfWork();
        entity = work.find( identity, AnEntity.class );
        assertNotNull( entity );
        work.remove( entity );
        work.complete();

        // Commented out: The odd thing is, removal fails here.
        work = uowf.newUnitOfWork();
        try
        {
            entity = work.find( identity, AnEntity.class );
            fail( "Test removal fail. [" + ( entity == null ) + "] identity [" + identity + "]" );
        }
        catch( EntityCompositeNotFoundException e )
        {
            // Expected
        }
        work.complete();

        // Clean up
        bundleContext.ungetService( moduleRef );
    }
}
