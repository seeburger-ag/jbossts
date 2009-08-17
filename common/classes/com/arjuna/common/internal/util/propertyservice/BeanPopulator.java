/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 *
 * (C) 2009,
 * @author JBoss, a division of Red Hat.
 */
package com.arjuna.common.internal.util.propertyservice;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.LinkedList;
import java.util.Enumeration;
import java.util.Collections;

import com.arjuna.common.util.propertyservice.PropertyManager;

/**
 * Utility class that configures *EnvironmentBean objects using a PropertyManager, which is usually
 * backed by a -properties.xml file.
 *
 * @author Jonathan Halliday (jonathan.halliday@redhat.com)
 */
public class BeanPopulator
{
    /**
     * Examine the properties of the provided bean and update them to match the values of the corresponding
     * properties in the PropertyManager.
     * This will normally be used at startup to configure a freshly created default bean to match
     * the configuration read from a properties file.
     *
     * The algorithm is as follows: for each field in the bean, which must have a getter and setter method
     * matching according to the JavaBeans naming conventions, determine the corresponding property key.
     *
     * Several key names are tried, with the first match being used:
     * For scalar properties: The FQN of the bean followed by the field name,
     *   the short name of the bean followed by the field name, and finally the bean classes' PropertyPrefix annotation
     *   value followed by the name of the field, the last being except in cases where the field has a FullPropertyName
     *   annotation, in which case its value is used instead.
     * For vector (in the math sense - the type is actually normally List) properties, a single property key matched
     *   according to the prior rules will be treated as having a compund vlaue which will be tokenized into list
     *   elements based on whitespace and inserted into the list in token order.
     * If no such key is found, the value of the field's ConcatenationPrefix annotation
     * is treated as a name prefix and the list elements are assembled from the values of any properties having
     * key values starting with the prefix. These are inserted into the list in order of the key's name sort position.
     *
     * This allows for the convention that all properties in a given bean will share
     * the same prefix e.g. com.arjuna.ats.arjuna.foo. whilst still allowing for changing of the property
     * name in cases where this makes for more readable code.
     *
     * Obtain the value of the property from the PropertyManager and if it's not null, type convert it to match
     * the bean's property field type.  Obtain the value of the property from the bean and if it's different
     * from the value read from the propertyManager, use the setter to update the bean.
     *
     * @param bean a JavaBean, the target of the property updates and source for defaults.
     * @param propertyManager a PropertyManager, the source of the configuration overrides.
     * @throws Exception if the configuration of the bean fails.
     */
    public static void configureFromPropertyManager(Object bean, PropertyManager propertyManager) throws Exception {

        if(!bean.getClass().isAnnotationPresent(PropertyPrefix.class)) {
            throw new Exception("no PropertyPrefix found on "+bean.getClass().getName());
        }

        for(Field field : bean.getClass().getDeclaredFields()) {
            Class type = field.getType();

            String setterMethodName = "set"+capitalizeFirstLetter(field.getName());
            Method setter = bean.getClass().getMethod(setterMethodName, new Class[] {field.getType()});

            String getterMethodName;
            Method getter = null;
            if(field.getType().equals(Boolean.TYPE)) {
                getterMethodName = "is"+capitalizeFirstLetter(field.getName());
                try {
                    getter = bean.getClass().getMethod(getterMethodName, new Class[] {});
                } catch (NoSuchMethodException e) {}
            }

            if(getter == null) {
                getterMethodName = "get"+capitalizeFirstLetter(field.getName());
                getter = bean.getClass().getMethod(getterMethodName, new Class[] {});
            }

            if(field.isAnnotationPresent(ConcatenationPrefix.class)) {
                handleGroupProperty(bean, propertyManager, field, setter, getter);
            } else {
                handleSimpleProperty(bean, propertyManager, field, setter, getter);
            }
        }
    }

    private static void handleGroupProperty(Object bean, PropertyManager propertyManager, Field field, Method setter, Method getter)
        throws Exception
    {
        List<String> values = new LinkedList<String>();

        String valueFromPropertyManager = getValueFromPropertyManager(bean, propertyManager, field, bean.getClass().getSimpleName());

        if(valueFromPropertyManager != null)
        {
            // it's a single value which needs parsing

            String[] tokens = valueFromPropertyManager.split("\\s+");

            // the order we want them is is the order they appear in the string, so we can just add in sequence:
            for(String token : tokens) {
                values.add(token);
            }
        }
        else
        {
            // it's set of values that need gathering together
            // the order we want them in is the lex sort order of the keys, so we need to buffer and sort them:

            List<String> listOfMatchingPropertyNames = new LinkedList<String>();
            Enumeration propertyNamesEnumeration = propertyManager.propertyNames();

            if (propertyNamesEnumeration != null)
            {
                String prefix = field.getAnnotation(ConcatenationPrefix.class).prefix();

                while (propertyNamesEnumeration.hasMoreElements())
                {
                    String name = (String) propertyNamesEnumeration.nextElement();

                    if (name.startsWith(prefix))
                    {
                        listOfMatchingPropertyNames.add(name);
                    }
                }
            }

            Collections.sort(listOfMatchingPropertyNames);

            for(String name : listOfMatchingPropertyNames) {
                String value = propertyManager.getProperty(name);
                values.add(value);
            }            
        }
        
        Object valueFromBean = getter.invoke(bean, new Object[] {});

        if(!valueFromBean.equals(values)) {
            setter.invoke(bean, new Object[] {values});
        }
    }

    private static void handleSimpleProperty(Object bean, PropertyManager propertyManager, Field field, Method setter, Method getter)
            throws Exception
    {
        PropertyPrefix prefixAnnotation = bean.getClass().getAnnotation(PropertyPrefix.class);
        String prefix = prefixAnnotation.prefix();

        String valueFromPropertyManager = getValueFromPropertyManager(bean, propertyManager, field, prefix);

        if(valueFromPropertyManager != null) {

            Object valueFromBean = getter.invoke(bean, new Object[] {});

            if(field.getType().equals(Boolean.TYPE)) {

                if(!((Boolean)valueFromBean).booleanValue() && isPositive(valueFromPropertyManager)) {
                    setter.invoke(bean, new Object[]{ Boolean.TRUE });
                }

                if(((Boolean)valueFromBean).booleanValue() && isNegative(valueFromPropertyManager)) {
                    setter.invoke(bean, new Object[] { Boolean.FALSE});
                }

            } else if(field.getType().equals(String.class)) {

                if(!valueFromPropertyManager.equals(valueFromBean)) {
                    setter.invoke(bean, new Object[] {valueFromPropertyManager});
                }

            } else if(field.getType().equals(Long.TYPE)) {

                Long longValue = Long.valueOf(valueFromPropertyManager);
                if(!longValue.equals(valueFromBean)) {
                    setter.invoke(bean, new Object[] {longValue});
                }

            } else if(field.getType().equals(Integer.TYPE)) {

                Integer intValue = Integer.valueOf(valueFromPropertyManager);
                if(!intValue.equals(valueFromBean)) {
                    setter.invoke(bean, new Object[] {intValue});
                }

            } else {

                throw new Exception("unknown field type "+field.getType());

            }
        }
    }

    private static String getValueFromPropertyManager(Object bean, PropertyManager propertyManager, Field field, String prefix)
    {
        String propertyFileKey;
        String valueFromPropertyManager = null;

        if(valueFromPropertyManager == null) {
            propertyFileKey = bean.getClass().getCanonicalName()+"."+field.getName();
            valueFromPropertyManager = propertyManager.getProperty(propertyFileKey);
        }

        if(valueFromPropertyManager == null) {
            propertyFileKey = bean.getClass().getSimpleName()+"."+field.getName();
            valueFromPropertyManager = propertyManager.getProperty(propertyFileKey);
        }

        if (valueFromPropertyManager == null) {
            propertyFileKey = prefix+field.getName();

            if(field.isAnnotationPresent(FullPropertyName.class)) {
                FullPropertyName fullPropertyName = field.getAnnotation(FullPropertyName.class);
                propertyFileKey = fullPropertyName.name();
            }

            valueFromPropertyManager = propertyManager.getProperty(propertyFileKey);
        }

        return valueFromPropertyManager;
    }

    private static String capitalizeFirstLetter(String string) {
        return (string.length()>0) ? (Character.toUpperCase(string.charAt(0))+string.substring(1)) : string;
    }

    private static boolean isPositive(String value) {
        return "YES".equalsIgnoreCase(value) || "ON".equalsIgnoreCase(value) || "TRUE".equalsIgnoreCase(value);
    }

    private static boolean isNegative(String value) {
        return "NO".equalsIgnoreCase(value) || "OFF".equalsIgnoreCase(value) || "FALSE".equalsIgnoreCase(value);
    }
}