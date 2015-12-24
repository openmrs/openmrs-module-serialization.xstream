package org.openmrs.module.serialization.xstream.converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.springframework.stereotype.Component;

/**
 * XStream converter that strips HB collection specific information and retrieves the underlying
 * collection which is then parsed by the delegated converter. This converter only takes care of the
 * values inside the collections while the mapper takes care of the collection naming.
 * 
 * Borrowed from Costin Leau - http://jira.codehaus.org/browse/XSTR-226
 */
@Component("collectionConverter")
public class HibernateCollectionConverter implements Converter {
	
	private static Log log = LogFactory.getLog(HibernateCollectionConverter.class);
	
	private ConverterLookup converterLookup;
	
	@Autowired
	private CollectionCompatibility collection;
	
	public HibernateCollectionConverter() {
		
	}
	
	public void setConverterLookup(ConverterLookup converterLookup) {
		this.converterLookup = converterLookup;
	}
	
	public HibernateCollectionConverter(ConverterLookup converterLookup) {
		this.converterLookup = converterLookup;
	}
	
	/**
	 * @see com.thoughtworks.xstream.converters.Converter#canConvert(java.lang.Class)
	 */
	public boolean canConvert(Class type) {
		return collection.canConvert(type);
	}
	
	/**
	 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
	 *      com.thoughtworks.xstream.io.HierarchicalStreamWriter,
	 *      com.thoughtworks.xstream.converters.MarshallingContext)
	 */
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		collection.marshal(source, writer, context, converterLookup);
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
