/*  Copyright 2008 Edward Yakop.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.qi4j.entity.ibatis.internal;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.jmock.Mockery;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.composite.Composite;
import org.qi4j.composite.CompositeBuilderFactory;
import org.qi4j.entity.ibatis.AbstractTestCase;
import org.qi4j.entity.ibatis.HasFirstName;
import org.qi4j.entity.ibatis.HasLastName;
import org.qi4j.entity.ibatis.IBatisEntityStoreServiceInfo;
import org.qi4j.entity.ibatis.PersonComposite;
import static org.qi4j.entity.ibatis.internal.IBatisEntityStateStatus.statusNew;
import org.qi4j.entity.ibatis.internal.property.MutablePropertyInstance;
import org.qi4j.property.Property;
import org.qi4j.runtime.composite.CompositeContext;
import org.qi4j.runtime.structure.ModuleContext;
import org.qi4j.spi.composite.CompositeBinding;
import org.qi4j.spi.entity.EntityState;
import org.qi4j.spi.property.ImmutablePropertyInstance;
import org.qi4j.spi.property.PropertyBinding;

/**
 * @author edward.yakop@gmail.com
 */
public final class IBatisEntityStateTest extends AbstractTestCase
{
    private static final String DEFAULT_FIRST_NAME = "Edward";
    private static final String DEFAULT_LAST_NAME = "Yakop";

    /**
     * Test constructor of entity state.
     */
    @SuppressWarnings( "unchecked" )
    public void testGetProperty()
    {
        // =======================
        // Test with default value
        // =======================
        HashMap<String, Object> initialValues1 = new HashMap<String, Object>();
        EntityState personEntityState1 = newPersonEntityState( initialValues1 );

        // ----------
        // First name
        // ----------
        try
        {
            Method firstNamePropertyAccessor = HasFirstName.class.getMethod( "firstName" );

            Property<String> firstNameProperty = personEntityState1.getProperty( firstNamePropertyAccessor );
            assertNotNull( firstNameProperty );

            String firstNameValue = firstNameProperty.get();
            assertEquals( DEFAULT_FIRST_NAME, firstNameValue );

        }
        catch( NoSuchMethodException e )
        {
            fail( "HasFirstName must have [firstName] method." );
        }

        // ---------
        // Last name
        // ---------
        try
        {
            Method lastNamePropertyAccessor = HasLastName.class.getMethod( "lastName" );

            Property<String> lastNameProperty = personEntityState1.getProperty( lastNamePropertyAccessor );
            assertNotNull( lastNameProperty );

            String lastNameValue = lastNameProperty.get();
            assertEquals( DEFAULT_LAST_NAME, lastNameValue );

        }
        catch( NoSuchMethodException e )
        {
            fail( "HasFirstName must have [firstName] method." );
        }

        // ==================================
        // Test with initialzed default value
        // ==================================
        HashMap<String, Object> initialValues2 = new HashMap<String, Object>();
        String expectedFirstNameValue = "Jane";
        initialValues2.put( "firstName", expectedFirstNameValue );
        EntityState personEntityState2 = newPersonEntityState( initialValues2 );

        // ----------
        // First name
        // ----------
        try
        {
            Method firstNamePropertyAccessor = HasFirstName.class.getMethod( "firstName" );

            Property<String> firstNameProperty = personEntityState2.getProperty( firstNamePropertyAccessor );
            assertNotNull( firstNameProperty );

            String firstNameValue = firstNameProperty.get();
            assertEquals( expectedFirstNameValue, firstNameValue );
        }
        catch( NoSuchMethodException e )
        {
            fail( "HasFirstName must have [firstName] method." );
        }

        // ---------
        // Last name
        // ---------
        try
        {
            Method lastNamePropertyAccessor = HasLastName.class.getMethod( "lastName" );

            Property<String> lastNameProperty = personEntityState2.getProperty( lastNamePropertyAccessor );
            assertNotNull( lastNameProperty );

            String lastNameValue = lastNameProperty.get();
            assertEquals( DEFAULT_LAST_NAME, lastNameValue );

        }
        catch( NoSuchMethodException e )
        {
            fail( "HasFirstName must have [firstName] method." );
        }

        // Test get first name on 
    }

    private IBatisEntityState newPersonEntityState( HashMap<String, Object> initialValues )
    {
        ModuleContext moduleContext = moduleInstance.getModuleContext();
        Map<Class<? extends Composite>, CompositeContext> compositeContexts = moduleContext.getCompositeContexts();
        CompositeContext personCompositeContext = compositeContexts.get( PersonComposite.class );
        assertNotNull( personCompositeContext );

        CompositeBinding personCompositeBinding = personCompositeContext.getCompositeBinding();
        assertNotNull( personCompositeBinding );

        Mockery mockery = new Mockery();
        IBatisEntityStateDao dao = mockery.mock( IBatisEntityStateDao.class );
        IBatisEntityStoreServiceInfo serviceInfo = new IBatisEntityStoreServiceInfo( "a" );
        serviceInfo.setIsDebugMode( true );
        return new IBatisEntityState( "1", personCompositeBinding, initialValues, statusNew, dao, serviceInfo );
    }

    /**
     * Tests {@link IBatisEntityState#computePropertyValue(PropertyBinding,Map,boolean)}.
     *
     * @since 0.1.0
     */
    public final void testComputePropertyValue()
    {
        HashMap<String, Object> propertyValues = new HashMap<String, Object>();
        IBatisEntityState personEntityState1 = newPersonEntityState( propertyValues );
        Map<String, PropertyBinding> personPropertyBindings = getPersonCompositePropertyBindings();

        String firstNamePropertyName = "firstName";
        String firstNamePropertyCapitalizeName = firstNamePropertyName.toUpperCase();
        // ****************************
        // Test to return default value
        // ****************************
        PropertyBinding firstNameProperty = personPropertyBindings.get( firstNamePropertyName );
        assertNotNull( firstNameProperty );
        Object testValue1 = personEntityState1.computePropertyValue( firstNameProperty, propertyValues, true );
        assertEquals( DEFAULT_FIRST_NAME, testValue1 );

        // Test to return null, because empty property values and use default value to false
        Object testValue2 = personEntityState1.computePropertyValue( firstNameProperty, propertyValues, false );
        assertNull( testValue2 );

        // *****************************
        // Test to return assigned value
        // *****************************
        String expectedValue3 = "value3";
        propertyValues.put( firstNamePropertyCapitalizeName, expectedValue3 );
        Object testValue3 = personEntityState1.computePropertyValue( firstNameProperty, propertyValues, true );
        assertEquals( expectedValue3, testValue3 );

        // ***********************
        // Test with debug mode on
        // ***********************
        propertyValues.put( firstNamePropertyCapitalizeName, 2 );

        String failMsg = "Must [fail]. Mismatch between expected (String) and actual (Integer) property value type.";
        try
        {
            personEntityState1.computePropertyValue( firstNameProperty, propertyValues, false );
            fail( failMsg );
        }
        catch( IllegalStateException e )
        {
            // Expected
        }
        catch( Exception e )
        {
            fail( failMsg );
        }
    }

    /**
     * Tests new property instance.
     *
     * @since 0.1.0
     */
    public final void testNewPropertyInstance()
    {
        HashMap<String, Object> propertyValues = new HashMap<String, Object>();
        IBatisEntityState personEntityState1 = newPersonEntityState( propertyValues );

        // Set up test arguments
        Map<String, PropertyBinding> properties = getPersonCompositePropertyBindings();

        // ***********************
        // Test immutable property
        // ***********************
        PropertyBinding identityBinding = properties.get( "identity" );
        assertNotNull( "Property binding [identity] must exists.", identityBinding );
        String expectedValue1 = "anIdentityValue";
        Property<Object> identityProperty = personEntityState1.newPropertyInstance( identityBinding, expectedValue1 );
        assertNotNull( identityProperty );
        assertTrue( ImmutablePropertyInstance.class.equals( identityProperty.getClass() ) );
        assertEquals( expectedValue1, identityProperty.get() );

        // *********************
        // Test mutable property
        // *********************
        PropertyBinding firstNameBinding = properties.get( "firstName" );
        assertNotNull( "Property binding [firstName] must exists.", firstNameBinding );
        String expectedValue2 = "Edward";
        Property<Object> firstNameProperty = personEntityState1.newPropertyInstance( firstNameBinding, expectedValue2 );
        assertNotNull( firstNameProperty );
        assertTrue( MutablePropertyInstance.class.equals( firstNameProperty.getClass() ) );
        assertEquals( expectedValue2, firstNameProperty.get() );
    }

    /**
     * Returns all person composite property bindings.
     *
     * @return All person composite property bindings.
     * @since 0.1.0
     */
    private Map<String, PropertyBinding> getPersonCompositePropertyBindings()
    {
        CompositeBuilderFactory builderFactory = moduleInstance.getStructureContext().getCompositeBuilderFactory();
        PersonComposite composite = builderFactory.newComposite( PersonComposite.class );
        CompositeBinding personBinding = runtime.getCompositeBinding( composite );
        Iterable<PropertyBinding> propertyBindings = personBinding.getPropertyBindings();
        Map<String, PropertyBinding> properties = new HashMap<String, PropertyBinding>();
        for( PropertyBinding aBinding : propertyBindings )
        {
            String propertyName = aBinding.getName();
            properties.put( propertyName, aBinding );
        }
        assertFalse( "Properties must not be empty.", properties.isEmpty() );
        return properties;
    }

    public void assemble( ModuleAssembly aModule ) throws AssemblyException
    {
        super.assemble( aModule );

        aModule.addComposites( PersonComposite.class );

        // Has Name
        HasFirstName hasFirstName = aModule.addProperty().withAccessor( HasFirstName.class );
        hasFirstName.firstName().set( DEFAULT_FIRST_NAME );

        HasLastName hasLastName = aModule.addProperty().withAccessor( HasLastName.class );
        hasLastName.lastName().set( DEFAULT_LAST_NAME );
    }
}
