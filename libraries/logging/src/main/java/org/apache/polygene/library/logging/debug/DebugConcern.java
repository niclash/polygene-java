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

package org.apache.polygene.library.logging.debug;

import org.apache.polygene.api.PolygeneAPI;
import org.apache.polygene.api.common.Optional;
import org.apache.polygene.api.composite.Composite;
import org.apache.polygene.api.injection.scope.Service;
import org.apache.polygene.api.injection.scope.Structure;
import org.apache.polygene.api.injection.scope.This;
import org.apache.polygene.library.logging.debug.service.DebuggingService;

public class DebugConcern
    implements Debug
{
    @Structure private PolygeneAPI api;
    @Optional @Service private DebuggingService loggingService;
    @This private Composite composite;

    @Override
    public Integer debugLevel()
    {
        if( loggingService != null )
        {
            return loggingService.debugLevel();
        }
        return OFF;
    }

    @Override
    public void debug( int priority, String message )
    {
        if( loggingService == null )
        {
            return;
        }
        if( priority >= loggingService.debugLevel() )
        {
            Composite derefed = api.dereference( composite );
            loggingService.debug( derefed, message );
        }
    }

    @Override
    public void debug( int priority, String message, Object param1 )
    {
        if( loggingService == null )
        {
            return;
        }
        if( priority >= loggingService.debugLevel() )
        {
            loggingService.debug( api.dereference( composite ), message, param1 );
        }
    }

    @Override
    public void debug( int priority, String message, Object param1, Object param2 )
    {
        if( loggingService == null )
        {
            return;
        }
        if( priority >= loggingService.debugLevel() )
        {
            loggingService.debug( api.dereference( composite ), message, param1, param2 );
        }
    }

    @Override
    public void debug( int priority, String message, Object... params )
    {
        if( loggingService == null )
        {
            return;
        }
        if( priority >= loggingService.debugLevel() )
        {
            loggingService.debug( api.dereference( composite ), message, params );
        }
    }
}
