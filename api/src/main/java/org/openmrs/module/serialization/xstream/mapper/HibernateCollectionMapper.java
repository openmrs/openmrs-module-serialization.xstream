package org.openmrs.module.serialization.xstream.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * Replaces Hibernate specific collections with java.util implementations. This mapper takes care
 * only of the writing to the XML (deflating) not the other way around (inflating) because there is
 * no need.
 * 
 * Borrowed from Costin Leau - http://jira.codehaus.org/browse/XSTR-226
 */
public class HibernateCollectionMapper extends MapperWrapper {
	
	public HibernateCollectionMapper(Mapper wrapped) {
		super(wrapped);
	}
	
	/**
	 * @see com.thoughtworks.xstream.mapper.Mapper#serializedClass(java.lang.Class)
	 */
	public String serializedClass(Class type) {
		return super.serializedClass(replaceClasses(type));
	}
	
	/**
	 * @see com.thoughtworks.xstream.mapper.Mapper#serializedMember(java.lang.Class,
	 *      java.lang.String)
	 */
	public String serializedMember(Class type, String fieldName) {
		return super.serializedMember(replaceClasses(type), fieldName);
	}
	
	/**
	 * Simple replacements between the Hibernate collections and their underlying collections from
	 * java.util.
	 * 
	 * @param clazz
	 * @return the equivalent JDK class
	 */
	private Class<?> replaceClasses(Class<?> clazz) {
		if (clazz.getName().startsWith("org.hibernate")) {
			if (List.class.isAssignableFrom(clazz)) {
				return ArrayList.class;
			} else if (SortedSet.class.isAssignableFrom(clazz)) {
				return TreeSet.class;
			} else if (SortedMap.class.isAssignableFrom(clazz)) {
				return TreeMap.class;
			} else if (Set.class.isAssignableFrom(clazz)) {
				return HashSet.class;
			} else if (Map.class.isAssignableFrom(clazz)) {
				return HashMap.class;
			}
		}
		return clazz;
	}
}
