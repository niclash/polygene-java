/*
 * Copyright (c) 2012, Paul Merlin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.qi4j.runtime.structure;

import java.util.Iterator;
import org.junit.Test;
import org.qi4j.api.composite.AmbiguousTypeException;
import org.qi4j.api.entity.Identity;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.service.ServiceReference;
import org.qi4j.api.structure.Module;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkCompletionException;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.bootstrap.SingletonAssembler;
import org.qi4j.functional.Iterables;
import org.qi4j.test.EntityTestAssembler;

import static org.junit.Assert.*;

/**
 * Theses tests ensure that Type to Composite lookup is consistent
 * over Objects, Transients, Values, Entities and Services.
 */
public class TypeToCompositeLookupTest
{

    private static final String CATHEDRAL = "cathedral";

    private static final String BAZAR = "bazar";

    public interface Foo
    {

        String bar();

    }

    public static class BasicFooImpl
            implements Foo
    {

        public String bar()
        {
            return BAZAR;
        }

    }

    public static class SomeOtherFooImpl
            extends BasicFooImpl
    {

        @Override
        public String bar()
        {
            return CATHEDRAL;
        }

    }

    @Mixins( BasicFooImpl.class )
    public interface BasicFoo
            extends Foo
    {
    }

    @Mixins( SomeOtherFooImpl.class )
    public interface SomeOtherFoo
            extends BasicFoo
    {
    }

    @Test
    public void objects()
    {
        Module module = new SingletonAssembler()
        {

            public void assemble( ModuleAssembly module )
                    throws AssemblyException
            {
                module.objects( SomeOtherFooImpl.class );
            }

        }.module();

        assertEquals( CATHEDRAL, module.newObject( SomeOtherFooImpl.class ).bar() );
        assertEquals( CATHEDRAL, module.newObject( BasicFooImpl.class ).bar() );
        assertEquals( CATHEDRAL, module.newObject( Foo.class ).bar() );
    }

    @Test
    public void objectsAmbiguousDeclaration()
    {
        Module module = new SingletonAssembler()
        {

            public void assemble( ModuleAssembly module )
                    throws AssemblyException
            {
                module.objects( SomeOtherFooImpl.class, BasicFooImpl.class );
            }

        }.module();

        assertEquals( CATHEDRAL, module.newObject( SomeOtherFooImpl.class ).bar() );
        assertEquals( BAZAR, module.newObject( BasicFooImpl.class ).bar() );

        try {

            module.newObject( Foo.class );
            fail( "Ambiguous type exception not detected for Objects" );

        } catch ( AmbiguousTypeException expected ) {
        }
    }

    @Test
    public void transients()
    {
        Module module = new SingletonAssembler()
        {

            public void assemble( ModuleAssembly module )
                    throws AssemblyException
            {
                module.transients( SomeOtherFoo.class );
            }

        }.module();

        assertEquals( CATHEDRAL, module.newTransientBuilder( SomeOtherFoo.class ).newInstance().bar() );
        assertEquals( CATHEDRAL, module.newTransientBuilder( BasicFoo.class ).newInstance().bar() );
        assertEquals( CATHEDRAL, module.newTransientBuilder( Foo.class ).newInstance().bar() );
    }

    @Test
    public void transientsAmbiguousDeclaration()
    {
        Module module = new SingletonAssembler()
        {

            public void assemble( ModuleAssembly module )
                    throws AssemblyException
            {
                module.transients( SomeOtherFoo.class, BasicFoo.class );
            }

        }.module();

        assertEquals( CATHEDRAL, module.newTransientBuilder( SomeOtherFoo.class ).newInstance().bar() );
        assertEquals( BAZAR, module.newTransientBuilder( BasicFoo.class ).newInstance().bar() );

        try {

            module.newTransientBuilder( Foo.class );
            fail( "Ambiguous type exception not detected for Transients" );

        } catch ( AmbiguousTypeException expected ) {
        }
    }

    @Test
    public void values()
    {
        Module module = new SingletonAssembler()
        {

            public void assemble( ModuleAssembly module )
                    throws AssemblyException
            {
                module.values( SomeOtherFoo.class );
            }

        }.module();

        assertEquals( CATHEDRAL, module.newValueBuilder( SomeOtherFoo.class ).newInstance().bar() );
        assertEquals( CATHEDRAL, module.newValueBuilder( BasicFoo.class ).newInstance().bar() );
        assertEquals( CATHEDRAL, module.newValueBuilder( Foo.class ).newInstance().bar() );
    }

    @Test
    public void valuesAmbiguousDeclaration()
    {
        Module module = new SingletonAssembler()
        {

            public void assemble( ModuleAssembly module )
                    throws AssemblyException
            {
                module.values( SomeOtherFoo.class, BasicFoo.class );
            }

        }.module();

        assertEquals( CATHEDRAL, module.newValueBuilder( SomeOtherFoo.class ).newInstance().bar() );
        assertEquals( BAZAR, module.newValueBuilder( BasicFoo.class ).newInstance().bar() );

        try {

            module.newValueBuilder( Foo.class );
            fail( "Ambiguous type exception not detected for Values" );

        } catch ( AmbiguousTypeException expected ) {
        }
    }

    @Test
    public void entities()
            throws UnitOfWorkCompletionException
    {
        Module module = new SingletonAssembler()
        {

            public void assemble( ModuleAssembly module )
                    throws AssemblyException
            {
                new EntityTestAssembler().assemble( module );
                module.entities( SomeOtherFoo.class );
            }

        }.module();

        UnitOfWork uow = module.newUnitOfWork();

        SomeOtherFoo someOtherFoo = uow.newEntityBuilder( SomeOtherFoo.class ).newInstance();
        BasicFoo basicFoo = uow.newEntityBuilder( BasicFoo.class ).newInstance();
        Foo foo = uow.newEntityBuilder( Foo.class ).newInstance();

        assertEquals( CATHEDRAL, someOtherFoo.bar() );
        assertEquals( CATHEDRAL, basicFoo.bar() );
        assertEquals( CATHEDRAL, foo.bar() );

        String someOtherFooIdentity = ( ( Identity ) someOtherFoo ).identity().get();
        String basicFooIdentity = ( ( Identity ) basicFoo ).identity().get();
        String fooIdentity = ( ( Identity ) foo ).identity().get();

        uow.complete();

        uow = module.newUnitOfWork();

        uow.get( SomeOtherFoo.class, someOtherFooIdentity );
        uow.get( BasicFoo.class, basicFooIdentity );
        uow.get( Foo.class, fooIdentity );

        uow.discard();
    }

    @Test
    public void entitiesAmbiguousDeclaration()
            throws UnitOfWorkCompletionException
    {
        Module module = new SingletonAssembler()
        {

            public void assemble( ModuleAssembly module )
                    throws AssemblyException
            {
                new EntityTestAssembler().assemble( module );
                module.entities( SomeOtherFoo.class, BasicFoo.class );
            }

        }.module();

        UnitOfWork uow = module.newUnitOfWork();

        SomeOtherFoo someOtherFoo = uow.newEntityBuilder( SomeOtherFoo.class ).newInstance();
        BasicFoo basicFoo = uow.newEntityBuilder( BasicFoo.class ).newInstance();
        Foo foo = uow.newEntityBuilder( Foo.class ).newInstance();

        // Specific Type used
        assertEquals( CATHEDRAL, uow.newEntityBuilder( SomeOtherFoo.class ).newInstance().bar() );

        // Specific Type used
        assertEquals( BAZAR, uow.newEntityBuilder( BasicFoo.class ).newInstance().bar() );

        // First matching Type used - This is where Entity lookup behaviour differ
        assertEquals( CATHEDRAL, uow.newEntityBuilder( Foo.class ).newInstance().bar() );

        String someOtherFooIdentity = ( ( Identity ) someOtherFoo ).identity().get();
        String basicFooIdentity = ( ( Identity ) basicFoo ).identity().get();
        String fooIdentity = ( ( Identity ) foo ).identity().get();

        uow.complete();

        uow = module.newUnitOfWork();

        uow.get( SomeOtherFoo.class, someOtherFooIdentity );
        uow.get( BasicFoo.class, basicFooIdentity );
        uow.get( Foo.class, fooIdentity );

        uow.discard();

        if ( false ) {
            // Here is the test that would validate a behaviour consistent with other composite types.
            // Disabled for now.
            uow = module.newUnitOfWork();
            try {
                try {
                    uow.newEntityBuilder( Foo.class );
                    fail( "Ambiguous type exception not detected for Entities" );
                } catch ( AmbiguousTypeException expected ) {
                }
            } finally {
                uow.discard();
            }
        }
    }

    @Test
    public void services()
    {
        Module module = new SingletonAssembler()
        {

            public void assemble( ModuleAssembly module )
                    throws AssemblyException
            {
                module.services( SomeOtherFoo.class );
            }

        }.module();

        assertEquals( CATHEDRAL, module.findService( SomeOtherFoo.class ).get().bar() );
        assertEquals( CATHEDRAL, module.findService( BasicFoo.class ).get().bar() );
        assertEquals( CATHEDRAL, module.findService( Foo.class ).get().bar() );
    }

    @Test
    public void servicesPluralDeclaration()
    {
        Module module = new SingletonAssembler()
        {

            public void assemble( ModuleAssembly module )
                    throws AssemblyException
            {
                module.services( SomeOtherFoo.class, BasicFoo.class );
            }

        }.module();

        assertEquals( 1, Iterables.count( module.findServices( SomeOtherFoo.class ) ) );
        assertEquals( 2, Iterables.count( module.findServices( BasicFoo.class ) ) );
        assertEquals( 2, Iterables.count( module.findServices( Foo.class ) ) );

        assertEquals( CATHEDRAL, module.findService( SomeOtherFoo.class ).get().bar() );

        // Follows assembly Type order
        Iterator<ServiceReference<BasicFoo>> fooServices = module.findServices( BasicFoo.class ).iterator();
        assertEquals( CATHEDRAL, fooServices.next().get().bar() );
        assertEquals( BAZAR, fooServices.next().get().bar() );

        // Follows assembly Type order
        Iterator<ServiceReference<Foo>> foos = module.findServices( Foo.class ).iterator();
        assertEquals( CATHEDRAL, foos.next().get().bar() );
        assertEquals( BAZAR, foos.next().get().bar() );
    }

}