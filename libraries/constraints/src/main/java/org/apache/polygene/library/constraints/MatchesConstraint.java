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
package org.apache.polygene.library.constraints;

import java.util.regex.Pattern;
import org.apache.polygene.api.constraint.Constraint;
import org.apache.polygene.library.constraints.annotation.Matches;

/**
 * Implement @Matches constraint.
 */
public class MatchesConstraint
    implements Constraint<Matches, String>
{

    private static final long serialVersionUID = 1L;

    @Override
    public boolean isValid( Matches annotation, String argument )
    {
        if( argument != null )
        {
            Pattern pattern = Pattern.compile( annotation.value() );
            return pattern.matcher( argument ).matches();
        }

        return false;
    }

}