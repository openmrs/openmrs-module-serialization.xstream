package org.openmrs.module.serialization.xstream.mapper;

import org.openmrs.util.OpenmrsClassLoader;

import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * Mapper that removes the annoying Javassist signature which generates an unsuable XML (the classes
 * mentioned in there do not exist). This mapper takes care only of the writing to the XML
 * (deflating) not the other way around (inflating) because there is no need.
 * 
 * Borrowed from Costin Leau - http://jira.codehaus.org/browse/XSTR-238
 */
public class JavassistMapper extends MapperWrapper {
	
	public static final String marker = "_$$_javassist_";
	
	public JavassistMapper(Mapper wrapped) {
		super(wrapped);
	}
	
	/**
	 * @see com.thoughtworks.xstream.mapper.Mapper#serializedClass(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public String serializedClass(Class type) {
		String classNameWithoutEnhanced = removeSignature(super.serializedClass(type));
		Class actualClass = null;
		try {
			//here re-get the actual class in order to put the alias name of the acutal class(which is delegated by javassist proxy) into xml string
			actualClass = OpenmrsClassLoader.getInstance().loadClass(classNameWithoutEnhanced);
		}
		catch (ClassNotFoundException e) {
			return classNameWithoutEnhanced;
		}
		//here assure xstream can get the alias name of the actual class which is proxied by cglib
		return super.serializedClass(actualClass);
	}
	
	/**
	 * @see com.thoughtworks.xstream.mapper.Mapper#serializedMember(java.lang.Class,
	 *      java.lang.String)
	 */
	public String serializedMember(Class type, String memberName) {
		return removeSignature(super.serializedMember(type, memberName));
	}
	
	/**
	 * Convenience method to remove "_$$_javassist_" string.
	 * 
	 * @param name the class name that has "_$$_javassist_" in it.
	 * @return the class name with the marker (_$$_javassist_) in it
	 */
	private String removeSignature(String name) {
		int count = name.indexOf(marker);
		if (count >= 0) {
			return name.substring(0, count);
		}
		return name;
	}
}
