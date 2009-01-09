/*
 * Copyright 2008 Niclas Hedhman.
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
package org.qi4j.entitystore.swift;

import org.qi4j.spi.entity.EntityStore;
import org.qi4j.spi.entity.EntityStoreListenerNotificationSideEffect;
import org.qi4j.api.service.ServiceComposite;
import org.qi4j.api.configuration.Configuration;
import org.qi4j.api.service.Activatable;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.concern.Concerns;
import org.qi4j.api.sideeffect.SideEffects;
import org.qi4j.entitystore.memory.ConcurrentModificationCheckConcern;
import org.qi4j.library.locking.LockingAbstractComposite;

@Concerns( ConcurrentModificationCheckConcern.class )
@SideEffects( EntityStoreListenerNotificationSideEffect.class )
@Mixins( QuickEntityStoreMixin.class )
public interface QuickEntityStoreService extends Activatable,EntityStore, ServiceComposite, LockingAbstractComposite, Configuration
{
}
