package org.openmrs.module.serialization.xstream.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.collection.PersistentList;
import org.hibernate.collection.PersistentMap;
import org.hibernate.collection.PersistentSet;
import org.hibernate.collection.PersistentSortedMap;
import org.hibernate.collection.PersistentSortedSet;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * XStream converter that strips HB collection specific information and retrieves the underlying
 * collection which is then parsed by the delegated converter. This converter only takes care of the
 * values inside the collections while the mapper takes care of the collection naming.
 * 
 * Borrowed from Costin Leau - http://jira.codehaus.org/browse/XSTR-226
 */
public class HibernateCollectionConverter implements Converter {
	
	private static Log log = LogFactory.getLog(HibernateCollectionConverter.class);
	
	private final ConverterLookup converterLookup;
	
	public HibernateCollectionConverter(ConverterLookup converterLookup) {
		this.converterLookup = converterLookup;
	}
	
	/**
	 * @see com.thoughtworks.xstream.converters.Converter#canConvert(java.lang.Class)
	 */
	public boolean canConvert(Class type) {
		return PersistentCollection.class.isAssignableFrom(type);
	}
	
	/**
	 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
	 *      com.thoughtworks.xstream.io.HierarchicalStreamWriter,
	 *      com.thoughtworks.xstream.converters.MarshallingContext)
	 */
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		if (source instanceof PersistentList) {
			source = new ArrayList((Collection) source);
		} else if (source instanceof PersistentMap) {
			source = new HashMap((Map) source);
		} else if (source instanceof PersistentSortedMap) {
			source = new TreeMap((SortedMap) source);
		} else if (source instanceof PersistentSortedSet) {
			source = new TreeSet((SortedSet) source);
		} else if (source instanceof PersistentSet) {
			source = new HashSet((Set) source);
		}

		// delegate the collection to the appropriate converter
		converterLookup.lookupConverterForType(source.getClass()).marshal(source, writer, context);
	}
	
	/**
	 * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader,
	 *      com.thoughtworks.xstream.converters.UnmarshallingContext)
	 */
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		log.debug("**** UNMARSHAL **** " + context.getRequiredType());
		return null;
	}
}
