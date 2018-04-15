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
package org.apache.polygene.runtime.activation;

import java.util.Arrays;
import org.apache.polygene.api.activation.ActivationException;
import org.apache.polygene.api.activation.ActivatorAdapter;
import org.apache.polygene.api.activation.Activators;
import org.apache.polygene.api.activation.PassivationException;
import org.apache.polygene.api.injection.scope.This;
import org.apache.polygene.api.mixin.Mixins;
import org.apache.polygene.api.service.ServiceReference;
import org.apache.polygene.bootstrap.AssemblyException;
import org.apache.polygene.bootstrap.ModuleAssembly;
import org.apache.polygene.bootstrap.SingletonAssembler;
import org.apache.polygene.runtime.activation.ActivatorOrderTestSupport.ActivationStep;
import org.apache.polygene.runtime.activation.ActivatorOrderTestSupport.ActivationStepsRecorder;
import org.apache.polygene.runtime.activation.ActivatorOrderTestSupport.ActivationStepsRecorderInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Assert that intra-mixin activation order is correct.
 * Alpha* depends on Beta* to be activated.
 */
public class IntraMixinActivationOrderTest
{

    public static final ActivationStepsRecorder RECORDER = new ActivationStepsRecorderInstance();

    @BeforeEach
    public void beforeEachTest()
    {
        RECORDER.reset();
    }

    public interface AlphaMixinType
    {
    }

    public static class AlphaMixin
        implements AlphaMixinType, AlphaActivation
    {

        @This
        private BetaMixinType beta;

        @Override
        public void setupAlpha()
        {
            beta.ensureActivated();
            RECORDER.record( new ActivationStep( "alpha", "setup" ) );
        }

        @Override
        public void tearDownAlpha()
        {
            RECORDER.record( new ActivationStep( "alpha", "tear-down" ) );
        }

    }

    public interface BetaMixinType
    {

        void ensureActivated();

    }

    public static class BetaMixin
        implements BetaMixinType, BetaActivation
    {

        private boolean activated = false;

        @Override
        public void ensureActivated()
        {
            if( !activated )
            {
                throw new IllegalStateException( "BetaMixin is not activated" );
            }
        }

        @Override
        public void setupBeta()
        {
            RECORDER.record( new ActivationStep( "beta", "setup" ) );
            activated = true;
        }

        @Override
        public void tearDownBeta()
        {
            RECORDER.record( new ActivationStep( "beta", "tear-down" ) );
            activated = false;
        }

    }

    @Activators( AlphaActivation.AlphaActivator.class )
    @Mixins( AlphaMixin.class )
    public interface AlphaActivation
    {

        void setupAlpha();

        void tearDownAlpha();

        public class AlphaActivator
            extends ActivatorAdapter<ServiceReference<AlphaActivation>>
        {

            @Override
            public void afterActivation( ServiceReference<AlphaActivation> activated )
                throws Exception
            {
                activated.get().setupAlpha();
            }

            @Override
            public void beforePassivation( ServiceReference<AlphaActivation> passivating )
                throws Exception
            {
                passivating.get().tearDownAlpha();
            }

        }

    }

    @Activators( BetaActivation.BetaActivator.class )
    @Mixins( BetaMixin.class )
    public interface BetaActivation
    {

        void setupBeta();

        void tearDownBeta();

        public class BetaActivator
            extends ActivatorAdapter<ServiceReference<BetaActivation>>
        {

            @Override
            public void afterActivation( ServiceReference<BetaActivation> activated )
                throws Exception
            {
                activated.get().setupBeta();
            }

            @Override
            public void beforePassivation( ServiceReference<BetaActivation> passivating )
                throws Exception
            {
                passivating.get().tearDownBeta();
            }

        }

    }

    // Order of declaration here ensure that Beta is activated before Alpha
    public interface UnderTestServiceType
        extends BetaActivation, AlphaActivation
    {
    }

    @Test
    public void test()
        throws AssemblyException, ActivationException, PassivationException
    {
        new SingletonAssembler()
        {
            @Override
            public void assemble( ModuleAssembly module )
                throws AssemblyException
            {
                module.services( UnderTestServiceType.class ).instantiateOnStartup();
            }
        }.application().passivate();
        // System.out.println( RECORDER.steps() );
        String expected = Arrays.toString( new String[]
        {
            "beta.setup",
            "alpha.setup",
            "alpha.tear-down",
            "beta.tear-down",
        } );
        String actual = Arrays.toString( RECORDER.steps().toArray() );
        assertThat( actual, equalTo( expected ) );
    }

}
