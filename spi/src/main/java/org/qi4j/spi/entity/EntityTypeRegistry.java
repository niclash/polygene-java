/*
 * Copyright (c) 2009, Rickard Öberg. All Rights Reserved.
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

package org.qi4j.spi.entity;

import org.qi4j.api.mixin.Mixins;
import org.qi4j.spi.entity.typeregistry.EntityTypeRegistryMixin;

/**
 * JAVADOC
 */
@Mixins( EntityTypeRegistryMixin.class )
public interface EntityTypeRegistry
{
    void registerEntityType( EntityType type )
        throws EntityTypeRegistrationException;

    EntityType getEntityType( EntityTypeReference reference )
        throws UnknownEntityTypeException;

    public EntityType getEntityTypeByVersion(String version)
            throws UnknownEntityTypeException;

    public EntityType getEntityTypeByClassname(String className)
            throws UnknownEntityTypeException;

    public EntityType getEntityTypeByRDF(String rdf)
            throws UnknownEntityTypeException;
    
    Iterable<EntityType> getEntityTypes();
}