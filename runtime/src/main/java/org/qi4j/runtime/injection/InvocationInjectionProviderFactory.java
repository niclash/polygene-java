package org.qi4j.runtime.injection;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import org.qi4j.InvocationContext;
import org.qi4j.spi.dependency.InjectionContext;
import org.qi4j.spi.dependency.InjectionProvider;
import org.qi4j.spi.dependency.InjectionProviderFactory;
import org.qi4j.spi.dependency.InjectionResolution;
import org.qi4j.spi.dependency.InvalidInjectionException;
import org.qi4j.spi.dependency.ModifierInjectionContext;

/**
 * TODO
 */
public class InvocationInjectionProviderFactory
    implements InjectionProviderFactory
{
    public InjectionProvider newInjectionProvider( InjectionResolution resolution ) throws InvalidInjectionException
    {
        Class injectionClass = resolution.getInjectionModel().getInjectionClass();
        if( injectionClass.equals( Method.class ) ||
            injectionClass.equals( AnnotatedElement.class ) ||
            injectionClass.equals( InvocationContext.class ) ||
            Annotation.class.isAssignableFrom( injectionClass ) )
        {
            return new InvocationDependencyResolution( resolution );
        }
        else
        {
            throw new InvalidInjectionException( "Invalid injection type " + injectionClass + " in " + resolution.getInjectionModel().getInjectedClass().getName() );
        }
    }

    private class InvocationDependencyResolution implements InjectionProvider
    {
        private InjectionResolution resolution;

        public InvocationDependencyResolution( InjectionResolution resolution )
        {
            this.resolution = resolution;
        }

        public Object provideInjection( InjectionContext context )
        {
            ModifierInjectionContext modifierContext = (ModifierInjectionContext) context;
            Class injectedClass = resolution.getInjectionModel().getInjectedClass();
            if( injectedClass.equals( Method.class ) )
            {
                // This needs to be updated to handle Apply and annotation aggregation correctly
                return modifierContext.getMethod();
            }
            else if( injectedClass.equals( AnnotatedElement.class ) )
            {
                // TODO This needs to be updated to handle annotation aggregation correctly
                return modifierContext.getMethod();
            }
            else if( injectedClass.equals( InvocationContext.class ) )
            {
                return modifierContext.getInvocationContext();
            }
            else
            {
                // TODO This needs to be updated to handle annotation aggregation correctly
                return modifierContext.getMethod().getAnnotation( injectedClass );
            }
        }
    }
}