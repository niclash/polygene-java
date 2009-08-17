/*
 * Copyright 2009 Niclas Hedhman.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.qi4j.index.reindexer.internal;

import java.util.ArrayList;
import org.qi4j.api.configuration.Configuration;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.injection.scope.This;
import org.qi4j.api.service.ServiceReference;
import org.qi4j.index.reindexer.Reindexer;
import org.qi4j.index.reindexer.ReindexerConfiguration;
import org.qi4j.spi.entity.EntityState;
import org.qi4j.spi.entity.EntityStore;
import org.qi4j.spi.structure.ModuleSPI;
import org.qi4j.spi.unitofwork.StateChangeListener;

public class ReindexerMixin
    implements Reindexer
{
    @This private Configuration<ReindexerConfiguration> configuration;

    @Service private EntityStore store;
    @Service private Iterable<ServiceReference<StateChangeListener>> listeners;
    @Structure private ModuleSPI module;

    public void reindex()
    {
        configuration.refresh();
        ReindexerConfiguration conf = configuration.configuration();
        Integer loadValue = conf.loadValue().get();
        if( loadValue == null )
        {
            loadValue = 50;
        }
        store.visitEntityStates( new ReindexerVisitor( loadValue ), module );
    }

    private class ReindexerVisitor
        implements EntityStore.EntityStateVisitor
    {
        private int count = 0;
        private int loadValue;
        private ArrayList<EntityState> states;

        public ReindexerVisitor( Integer loadValue )
        {
            this.loadValue = loadValue;
            states = new ArrayList<EntityState>();
        }

        public void visitEntityState( EntityState entityState )
        {
            states.add( entityState );
            if( count++ > loadValue )
            {
                for( ServiceReference<StateChangeListener> listener : listeners )
                {
                    listener.get().notifyChanges( states );
                }
                count = 0;
                states.clear();
            }
        }
    }
}
