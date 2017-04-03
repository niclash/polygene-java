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
package org.apache.polygene.bootstrap;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Thrown when one or more assembly problems has occurred.
 */
public class AssemblyResportException extends AssemblyException
{
    private List<Throwable> problems;
    private String modelReport;

    public AssemblyResportException( List<Throwable> problems )
    {
        this.problems = problems;
    }

    @Override
    public String getMessage()
    {
        String message;
        if( modelReport == null )
        {
            message = "\nComposition Problems Report:\n";
        }
        else
        {
            message = modelReport;
        }
        return message + problems.stream()
                                 .map( Throwable::getMessage )
                                 .map( m -> m + "\n--\n" )
                                 .collect( Collectors.joining( "" ) );
    }

    public void attacheModelReport( String modelReport )
    {
        this.modelReport = modelReport;
    }
}