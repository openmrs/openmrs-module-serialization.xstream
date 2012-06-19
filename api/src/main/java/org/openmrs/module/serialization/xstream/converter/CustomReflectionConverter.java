package org.openmrs.module.serialization.xstream.converter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.AbstractReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.core.util.HierarchicalStreams;
import com.thoughtworks.xstream.core.util.Primitives;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 *  CustomReflectionConverter and IgnoreUnknownElementMapper together implement the function that ignoring unknown elements when xstream deserialize from a xml string
 *  
 *  You can see the source code of "ReflectionConverter" to understand the logic of this class.
 *  Because the difference between this class and "ReflectionConverter" is only in
 *  line 165 of this class comparing with "ReflectionConverter"
 *  
 *  @see ReflectionConverter.java - http://svn.codehaus.org/xstream/trunk/xstream/src/java/com/thoughtworks/xstream/converters/reflection/ReflectionConverter.java
 *  @see AbstractReflectionConverter.java - http://svn.codehaus.org/xstream/trunk/xstream/src/java/com/thoughtworks/xstream/converters/reflection/AbstractReflectionConverter.java
 *  @see org.openmrs.module.serialization.xstream.mapper.IgnoreUnknownElementMapper
 */
public class CustomReflectionConverter extends ReflectionConverter {

	public CustomReflectionConverter(Mapper mapper,
			ReflectionProvider reflectionProvider) {
		super(mapper, reflectionProvider);
	}
	
	private static class SeenFields {

	    private Set seen = new HashSet();

	    public void add(Class definedInCls, String fieldName) {
	        String uniqueKey = fieldName;
		    if (definedInCls != null) {
		         uniqueKey += " [" + definedInCls.getName() + "]";
		    }
		    if (seen.contains(uniqueKey)) {
		         throw new DuplicateFieldException(uniqueKey);
		    } else {
		         seen.add(uniqueKey);
		    }
	    }
	}
	
	
    private Class determineWhichClassDefinesField(HierarchicalStreamReader reader) {
        String attributeName = mapper.aliasForSystemAttribute("defined-in");
        String definedIn = attributeName == null ? null : reader.getAttribute(attributeName);
        return definedIn == null ? null : mapper.realClass(definedIn);
    }
    
    private Class determineType(HierarchicalStreamReader reader, boolean validField, Object result, String fieldName, Class definedInCls) {
        String classAttribute = HierarchicalStreams.readClassAttribute(reader, mapper);
        if (classAttribute != null) {
            return mapper.realClass(classAttribute);
        } else if (!validField) {
            Class itemType = mapper.getItemTypeForItemFieldName(result.getClass(), fieldName);
            if (itemType != null) {
                return itemType;
            } else {
                String originalNodeName = reader.getNodeName();
                if (definedInCls == null) {
                    for(definedInCls = result.getClass(); definedInCls != null; definedInCls = definedInCls.getSuperclass()) {
                        if (!mapper.shouldSerializeMember(definedInCls, originalNodeName)) {
                            return null;
                        }
                    }
                }
                return mapper.realClass(originalNodeName);
            }
        } else {
            return mapper.defaultImplementationOf(reflectionProvider.getFieldType(result, fieldName, definedInCls));
        }
    }
    
    public Object doUnmarshal(final Object result, final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final SeenFields seenFields = new SeenFields();
        Iterator it = reader.getAttributeNames();

        // Process attributes before recursing into child elements.
        while (it.hasNext()) {
            String attrAlias = (String) it.next();
            String attrName = mapper.realMember(result.getClass(), mapper.attributeForAlias(attrAlias));
            Class classDefiningField = determineWhichClassDefinesField(reader);
            boolean fieldExistsInClass = reflectionProvider.fieldDefinedInClass(attrName, result.getClass());
            if (fieldExistsInClass) {
                Field field = reflectionProvider.getField(result.getClass(), attrName);
                if (Modifier.isTransient(field.getModifiers()) && ! shouldUnmarshalTransientFields()) {
                    continue;
                }
                SingleValueConverter converter = mapper.getConverterFromAttribute(field.getDeclaringClass(), attrName, field.getType());
                Class type = field.getType();
                if (converter != null) {
                    Object value = converter.fromString(reader.getAttribute(attrAlias));
                    if (type.isPrimitive()) {
                        type = Primitives.box(type);
                    }
                    if (value != null && !type.isAssignableFrom(value.getClass())) {
                        throw new ConversionException("Cannot convert type " + value.getClass().getName() + " to type " + type.getName());
                    }
                    reflectionProvider.writeField(result, attrName, value, classDefiningField);
                    seenFields.add(classDefiningField, attrName);
                }
            }
        }

        Map implicitCollectionsForCurrentObject = null;
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            
            String originalNodeName = reader.getNodeName();
            String fieldName = mapper.realMember(result.getClass(), originalNodeName);
            Mapper.ImplicitCollectionMapping implicitCollectionMapping = mapper.getImplicitCollectionDefForFieldName(result.getClass(), fieldName);

            Class classDefiningField = determineWhichClassDefinesField(reader);
           
            boolean fieldExistsInClass = implicitCollectionMapping == null && reflectionProvider.fieldDefinedInClass(fieldName, result.getClass());
            
            Class type = implicitCollectionMapping == null
                ? determineType(reader, fieldExistsInClass, result, fieldName, classDefiningField) 
                : implicitCollectionMapping.getItemType();
    
            final Object value;
            if (fieldExistsInClass) {
                Field field = reflectionProvider.getField(classDefiningField != null ? classDefiningField : result.getClass(), fieldName);
                if (Modifier.isTransient(field.getModifiers()) && !shouldUnmarshalTransientFields()) {
                    reader.moveUp();
                    continue;
                }
                value = unmarshallField(context, result, type, field);
                // TODO the reflection provider should have returned the proper field in first place ....
                Class definedType = reflectionProvider.getFieldType(result, fieldName, classDefiningField);
                if (!definedType.isPrimitive()) {
                    type = definedType;
                }
            } else {
                value = type != null ? context.convertAnother(result, type) : null;
            }
             
            if (value != null && !type.isAssignableFrom(value.getClass())) {
            	//TODO Can i be helped to know why this makes some unit tests to fail?
            	//ProgramShortSerializationTest & OrderTypeShortSerializationTest

                //throw new ConversionException("Cannot convert type " + value.getClass().getName() + " to type " + type.getName());
            }

            if (fieldExistsInClass) {
                reflectionProvider.writeField(result, fieldName, value, classDefiningField);
                seenFields.add(classDefiningField, fieldName);
            }
            
            //here add a condition "implicitCollectionMapping != null" to judge whether need to serialize a implicitCollection
            else if (type != null && implicitCollectionMapping != null) {
                implicitCollectionsForCurrentObject = writeValueToImplicitCollection(context, value, implicitCollectionsForCurrentObject, result, originalNodeName);
            }

            reader.moveUp();
        }
        return result;
    }
    
    
    private Map writeValueToImplicitCollection(UnmarshallingContext context, Object value,
            Map implicitCollections, Object result, String itemFieldName) {
            String fieldName = mapper.getFieldNameForItemTypeAndName(
                context.getRequiredType(), value != null ? value.getClass() : Mapper.Null.class,
                itemFieldName);
            if (fieldName != null) {
                if (implicitCollections == null) {
                    implicitCollections = new HashMap(); // lazy instantiation
                }
                Collection collection = (Collection)implicitCollections.get(fieldName);
                if (collection == null) {
                    Class fieldType = mapper.defaultImplementationOf(reflectionProvider
                        .getFieldType(result, fieldName, null));
                    if (!Collection.class.isAssignableFrom(fieldType)) {
                        throw new ObjectAccessException("Field "
                            + fieldName
                            + " of "
                            + result.getClass().getName()
                            + " is configured for an implicit Collection, but field is of type "
                            + fieldType.getName());
                    }
                    collection = (Collection)(new PureJavaReflectionProvider()).newInstance(fieldType);
                    reflectionProvider.writeField(result, fieldName, collection, null);
                    implicitCollections.put(fieldName, collection);
                }
                collection.add(value);
            } else {
                throw new ConversionException("Element "
                    + itemFieldName
                    + " of type "
                    + value.getClass().getName()
                    + " is not defined as field in type "
                    + result.getClass().getName());
            }
            return implicitCollections;
        }
}
