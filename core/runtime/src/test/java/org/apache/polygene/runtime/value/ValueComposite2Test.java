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
 *
 *
 */
package org.apache.polygene.runtime.value;

import java.security.Guard;
import org.apache.polygene.api.activation.ActivationException;
import org.apache.polygene.api.injection.scope.Service;
import org.apache.polygene.api.injection.scope.Structure;
import org.apache.polygene.api.injection.scope.Uses;
import org.apache.polygene.api.mixin.Mixins;
import org.apache.polygene.api.property.Property;
import org.apache.polygene.api.service.ServiceComposite;
import org.apache.polygene.api.structure.Module;
import org.apache.polygene.api.value.ValueBuilder;
import org.apache.polygene.api.value.ValueComposite;
import org.apache.polygene.bootstrap.AssemblyException;
import org.apache.polygene.bootstrap.ModuleAssembly;
import org.apache.polygene.bootstrap.SingletonAssembler;
import org.junit.Assert;
import org.junit.Test;

public class ValueComposite2Test
{
    @Test
    public void testServiceAndStructureInjectionsAllowedInValueComposite()
        throws ActivationException, AssemblyException
    {
        SingletonAssembler app = new SingletonAssembler()
        {
            public void assemble( ModuleAssembly module )
                throws AssemblyException
            {
                module.values( SomeValue.class );
                module.services( DummyService.class );
            }
        };
        ValueBuilder<Some> builder = app.module().newValueBuilder( Some.class );
        Some prototype = builder.prototype();
        Property<String> otherProperty = prototype.other();
        otherProperty.set( "Abc" );
        Some value = builder.newInstance();
        Assert.assertEquals( value.other().get(), "Abc" );
    }

    @Test( expected = AssemblyException.class )
    public void testUsesAnnotationIsNotAllowedInValueComposite()
        throws ActivationException, AssemblyException
    {
        new SingletonAssembler()
        {
            public void assemble( ModuleAssembly module )
                throws AssemblyException
            {
                module.values( SomeValue2.class );
            }
        };
    }

    public interface Some
    {
        DummyService service();

        Module module();

        Property<String> other();
    }

    @Mixins( SomeMixin.class )
    public interface SomeValue
        extends Some, ValueComposite
    {
    }

    public static abstract class SomeMixin
        implements Some
    {
        @Service
        DummyService service;
        @Structure
        Module module;

        public DummyService service()
        {
            return service;
        }

        public Module module()
        {
            return module;
        }
    }

    @Mixins( SomeMixin2.class )
    public interface SomeValue2
        extends Some, ValueComposite
    {
    }

    public static abstract class SomeMixin2
        implements Some
    {
        @Uses
        Guard illegal;

        public DummyService service()
        {
            return null;
        }

        public Module module()
        {
            return null;
        }
    }

    public interface DummyService
        extends ServiceComposite
    {
    }
}


