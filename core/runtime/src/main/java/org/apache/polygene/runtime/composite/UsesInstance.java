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

package org.apache.polygene.runtime.composite;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * JAVADOC
 */
public final class UsesInstance
{
    public static final UsesInstance EMPTY_USES;
    private final Set<Object> uses;

    static
    {
        EMPTY_USES = new UsesInstance( new HashSet<>() );
    }

    private UsesInstance( HashSet<Object> uses )
    {
        this.uses = Collections.unmodifiableSet( uses );
    }

    public UsesInstance use( Object... objects )
    {
        // There is some case where we get here with only partially initialized composite as "objects". That fails with NPE in useObjects.addAll() below. Should be figured out when this happens and prevent it.
        HashSet<Object> useObjects = new HashSet<>();
        if( !uses.isEmpty() )
        {
            useObjects.addAll( uses );
            for( Object object : objects )
            {
                Object oldUseForType = useForType( object.getClass() );
                if( oldUseForType != null )
                {
                    useObjects.remove( oldUseForType );
                }
            }
        }
        useObjects.addAll( Arrays.asList( objects ) );
        return new UsesInstance( useObjects );
    }

    public Object useForType( Class<?> type )
    {
        // Check instances first
        for( Object use : uses )
        {
            if( type.isInstance( use ) )
            {
                return use;
            }
        }

        return null;
    }

    public Object[] toArray()
    {
        return uses.toArray();
    }

    @Override
    public boolean equals( Object o )
    {
        if( this == o )
        {
            return true;
        }
        if( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        UsesInstance that = (UsesInstance) o;
        return uses.equals( that.uses );
    }

    @Override
    public int hashCode()
    {
        return uses.hashCode();
    }

    @Override
    public String toString()
    {
        return "UsesInstance{" +
               "uses=" + uses +
               '}';
    }
}
