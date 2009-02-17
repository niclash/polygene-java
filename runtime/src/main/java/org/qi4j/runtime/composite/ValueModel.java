/*
 * Copyright (c) 2008, Rickard Öberg. All Rights Reserved.
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

package org.qi4j.runtime.composite;

import java.io.Serializable;
import java.lang.reflect.Method;
import org.qi4j.api.common.MetaInfo;
import org.qi4j.api.common.Visibility;
import org.qi4j.api.composite.Composite;
import org.qi4j.api.composite.InvalidValueCompositeException;
import org.qi4j.api.property.Property;
import org.qi4j.api.property.StateHolder;
import org.qi4j.bootstrap.PropertyDeclarations;
import org.qi4j.runtime.property.PropertiesModel;
import org.qi4j.runtime.structure.ModelVisitor;
import org.qi4j.runtime.structure.ModuleInstance;
import org.qi4j.spi.composite.InvalidCompositeException;

/**
 * TODO
 */
public final class ValueModel extends AbstractCompositeModel
    implements Serializable
{
    public static ValueModel newModel( final Class<? extends Composite> compositeType,
                                       final Visibility visibility,
                                       final MetaInfo metaInfo,
                                       final PropertyDeclarations propertyDeclarations,
                                       final Iterable<Class<?>> concerns,
                                       final Iterable<Class<?>> sideEffects)
    {
        ConstraintsModel constraintsModel = new ConstraintsModel( compositeType );
        PropertiesModel propertiesModel = new PropertiesModel( constraintsModel, propertyDeclarations, true );
        StateModel stateModel = new StateModel( propertiesModel );
        MixinsModel mixinsModel = new MixinsModel( compositeType, stateModel );
        ConcernsDeclaration concernsModel = new ConcernsDeclaration( compositeType, concerns );
        SideEffectsDeclaration sideEffectsModel = new SideEffectsDeclaration( compositeType, sideEffects );
        // TODO: Disable constraints, concerns and sideeffects??
        CompositeMethodsModel compositeMethodsModel =
            new CompositeMethodsModel( compositeType, constraintsModel, concernsModel, sideEffectsModel, mixinsModel );

        return new ValueModel( compositeType, visibility, metaInfo, mixinsModel, stateModel, compositeMethodsModel );
    }

    private ValueModel( final Class<? extends Composite> compositeType, final Visibility visibility, final MetaInfo metaInfo, final MixinsModel mixinsModel, final StateModel stateModel, final CompositeMethodsModel compositeMethodsModel )
    {
        super( compositeType, visibility, metaInfo, mixinsModel, stateModel, compositeMethodsModel );
    }

    public void visitModel( ModelVisitor modelVisitor )
    {
        modelVisitor.visit( this );

        compositeMethodsModel.visitModel( modelVisitor );
        mixinsModel.visitModel( modelVisitor );
    }

    public void bind( Resolution resolution )
        throws BindingException
    {
        resolution = new Resolution( resolution.application(), resolution.layer(), resolution.module(), this, null, null, null );
        compositeMethodsModel.bind( resolution );
        mixinsModel.bind( resolution );
        
        for( Method method : compositeMethodsModel.methods() )
        {
            if( !Composite.class.equals( method.getDeclaringClass() ) )
            {
                if( !Property.class.isAssignableFrom( method.getReturnType() ) )
                {
                    throw new InvalidValueCompositeException( "Only Property methods are allowed in ValueComposites: " + method.getReturnType() );
                }
            }
        }
    }

    public ValueCompositeInstance newValueInstance( ModuleInstance moduleInstance,
                                                   StateHolder state, boolean isPrototype )
    {
        stateModel.checkConstraints( state, isPrototype );

        Object[] mixins = mixinsModel.newMixinHolder();

        ValueCompositeInstance compositeInstance = new ValueCompositeInstance( this, moduleInstance, mixins, state );

        try
        {
            // Instantiate all mixins
            mixinsModel.newMixins( compositeInstance,
                                   UsesInstance.NO_USES,
                                   state,
                                   mixins );

        }
        catch( InvalidCompositeException e )
        {
            e.setFailingCompositeType( type() );
            e.setMessage( "Invalid Cyclic Mixin usage dependency" );
            throw e;
        }
        // Return
        return compositeInstance;
    }
}