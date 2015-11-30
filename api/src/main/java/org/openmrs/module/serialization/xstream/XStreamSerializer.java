/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.serialization.xstream;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNameTag;
import org.openmrs.api.SerializationService;
import org.openmrs.module.serialization.xstream.converter.CustomCGLIBEnhancedConverter;
import org.openmrs.module.serialization.xstream.converter.CustomJavassistEnhancedConverter;
import org.openmrs.module.serialization.xstream.converter.CustomReflectionConverter;
import org.openmrs.module.serialization.xstream.converter.CustomSQLTimestampConverter;
import org.openmrs.module.serialization.xstream.converter.HibernateCollectionConverter;
import org.openmrs.module.serialization.xstream.converter.UserConverter;
import org.openmrs.module.serialization.xstream.mapper.CGLibMapper;
import org.openmrs.module.serialization.xstream.mapper.HibernateCollectionMapper;
import org.openmrs.module.serialization.xstream.mapper.JavassistMapper;
import org.openmrs.module.serialization.xstream.mapper.NullValueMapper;
import org.openmrs.module.serialization.xstream.strategy.CustomReferenceByIdMarshallingStrategy;
import org.openmrs.serialization.OpenmrsSerializer;
import org.openmrs.serialization.SerializationException;
import org.openmrs.util.OpenmrsClassLoader;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.thoughtworks.xstream.converters.extended.DynamicProxyConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * Provides serialization using XStream. <br/>
 * <br/>
 * It is recommended that you use the {@link SerializationService} to get the current serializer.
 * 
 * <pre>
 * Person person = Context.getPersonService().getPerson(123);
 * String xml = Context.getSerializationService().serialize(person, XStreamSerializer.class);
 * </pre>
 */
public class XStreamSerializer implements OpenmrsSerializer {
	
	public XStream xstream = null;
	
	/**
	 * Default Constructor
	 * 
	 * @throws SerializationException
	 */
	public XStreamSerializer() throws SerializationException {
		this(null);
	}
	
	/**
	 * Constructor that takes a custom XStream object
	 * @param customXstream
	 * @throws SerializationException
	 */
	public XStreamSerializer(XStream customXstream) throws SerializationException {
		if (customXstream == null) {
			/*
			 * use own-defined mapper to wrap xstream's default mapper, 
			 * The purpose:
			 * (1) we can use the reasonable name for cglib and Hibernate's Collection
			 * (2) ignore unknow element while deserializing
			 */
			xstream = new XStream() {
				protected MapperWrapper wrapMapper(MapperWrapper next) {
					MapperWrapper mapper = new CGLibMapper(next);
					mapper = new JavassistMapper(mapper);
					mapper = new HibernateCollectionMapper(mapper);
					mapper = new NullValueMapper(mapper);
					//mapper = new IgnoreUnknownElementMapper(mapper);
					return mapper;
				}
			};
		} else {
			this.xstream = customXstream;
		}
		
		// config the basic attributes
		
		// BaseOpenmrsObject
		xstream.useAttributeFor(BaseOpenmrsObject.class, "uuid");
		
		// BaseOpenmrsData
		xstream.useAttributeFor(BaseOpenmrsData.class, "voided");
		
		// BaseOpenmrsMetadata
		xstream.useAttributeFor(BaseOpenmrsMetadata.class, "retired");
		
		// Other classes has voided or retired property
		xstream.useAttributeFor(Concept.class, "retired");
		xstream.useAttributeFor(ConceptName.class, "voided");
		xstream.useAttributeFor(ConceptNameTag.class, "voided");
		//xstream.useAttributeFor(ConceptSource.class, "retired");

		/*
		 * alias className for all classses current need to serialize
		 */
		this.commonConfig();
		
		// CustomReflectionConverter to avoid the exception thrown when xstream deserialize a unknown elment
		xstream.registerConverter(new CustomReflectionConverter(xstream.getMapper(), xstream.getReflectionProvider()), xstream.PRIORITY_LOW);
		
		// register this converter, so that we can let xstream serialize User.user only as its uuid
		xstream.registerConverter(new UserConverter(xstream));
		
		/*
		 * Converters so that we can better deal with the serialization/deserializtion 
		 * of cglib, sql-timestamp, hibernate collections, etc
		 */
		xstream.registerConverter(new HibernateCollectionConverter(xstream.getConverterLookup()));
		xstream.registerConverter(new CustomCGLIBEnhancedConverter(xstream.getMapper(), xstream.getConverterLookup()));
		xstream.registerConverter(new CustomJavassistEnhancedConverter(xstream.getMapper(), xstream.getConverterLookup()));
		xstream.registerConverter(new CustomSQLTimestampConverter());
		xstream.registerConverter(new DateConverter("yyyy-MM-dd HH:mm:ss z", new String[] { "yyyy-MM-dd HH:mm:ss.S z",
		        "yyyy-MM-dd HH:mm:ssz", "yyyy-MM-dd HH:mm:ss.S a", "yyyy-MM-dd HH:mm:ssa" }));
		
		xstream.registerConverter(new CustomDynamicProxyConverter(), XStream.PRIORITY_VERY_HIGH);
		// set our own defined marshalling strategy so that we can build references for cglib
		xstream.setMarshallingStrategy(new CustomReferenceByIdMarshallingStrategy());
	}
	
	/**
	 * Get a list of package in which we will serialize all classes in it. Here we will serialize
	 * classes as the unit of package
	 * 
	 * @return a List of package in which we will serialize all classes in it
	 */
	private List<String> getAllSerializedPackages() {
		List<String> packageNames = new ArrayList<String>();
		packageNames.add("org.openmrs");// do serialization/deserialization for all classes in package "org.openmrs"
		// here can add new packages which in a specified package need to be serialized
		return packageNames;
	}
	
	/**
	 * Get all classes in a given package
	 * 
	 * @param packageName the given package's name
	 * @return a list of all classes in the given package
	 * @throws SerializationException
	 * @throws IOException
	 */
	private List<Class<?>> getAllClassesInPackage(String packageName) throws SerializationException {
		List<Class<?>> list = new ArrayList<Class<?>>();
		Enumeration<URL> resources = null;
		try {
			/*
			 * when load resource by class loader,
			 * if run Openmrs in tomcat, all classes will be stored in "WEB-INF/classes" and "WEB-INF/lib/openmrs-xxx.jar"
			 * if just test serialization service through unit test without start tomcat, the classes will be stored in "build/org/..."
			 * so here to judge for both cases "file resource" or "jar resource"
			 */
			resources = OpenmrsClassLoader.getInstance().getResources(packageName.replace('.', '/'));
			while (resources.hasMoreElements()) {
				URL url = resources.nextElement();
				if ("jar".equals(url.getProtocol())) {
					JarURLConnection con = (JarURLConnection) url.openConnection();
					con.setUseCaches(false);
					JarFile jarFile = con.getJarFile();
					Enumeration<JarEntry> e = jarFile.entries();
					/*
					 * use regular expression to match all classes in the given package
					 * excluding classes in sub package and inner class
					 */
					String matchingPattern = packageName.replace('.', '/') + "/" + "[^/$]+.class";
					Pattern p = Pattern.compile(matchingPattern);
					Matcher m = null;
					Class<?> loadedClass = null;
					while (e.hasMoreElements()) {
						JarEntry entry = e.nextElement();
						m = p.matcher(entry.getName());
						if (m.matches() == true) {
							// load the matching class
							String tempName = entry.getName().replace('/', '.');
							String className = tempName.substring(0, tempName.indexOf(".class"));
							loadedClass = OpenmrsClassLoader.getInstance().loadClass(className);
							list.add(loadedClass);
						}
					}
				} else {
					File dir = new File(url.getFile());
					Class<?> loadedClass = null;
					if (dir.exists() && dir.isDirectory()) {
						File[] fileList = dir.listFiles();
						for (File f : fileList) {
							// get all class files excluding the inner class
							if (f.isFile() && f.getName().endsWith(".class") && !f.getName().contains("$")) {
								String className = packageName + "."
								        + f.getName().substring(0, f.getName().indexOf(".class"));
								loadedClass = OpenmrsClassLoader.getInstance().loadClass(className);
								list.add(loadedClass);
							}
						}
					}
				}
			}
			return list;
			
		}
		catch (Exception e) {
			String errMsg = "Unable to load serialized class";
			throw new SerializationException(errMsg, e);
		}
	}
	
	/**
	 * get all Classes which need serialization
	 * 
	 * @see XStreamSerializer#getAllSerializedPackages()
	 * @see XStreamSerializer#getAllClassesInPackage(String)
	 * @throws SerializationException
	 */
	private List<Class<?>> getAllSerializedClasses() throws SerializationException {
		List<Class<?>> allSerializedClasses = new ArrayList<Class<?>>();
		List<String> packageNames = this.getAllSerializedPackages();
		for (String p : packageNames) {
			List<Class<?>> classes = this.getAllClassesInPackage(p);
			if ((classes != null) && (classes.size() > 0)) {
				allSerializedClasses.addAll(classes);
			}
		}
		return allSerializedClasses;
	}
	
	/**
	 * alias className for class "c", we will use such a form, alias "user" for
	 * "org.openmrs.User.class". The alias principle is to low the first letter of the simple name
	 * of class "c" Note: if in module have a few new classes need to be serialize, call this method
	 * for each new class will automatically alias className for them
	 * 
	 * @param c - the class need to alias its className
	 */
	private void aliasClassName(Class<?> c) {
		//through Class.getSimpleName(), we get the short name of a class, such as get "User" for class "org.openmrs.User"
		String simpleName = c.getSimpleName();
		String firstLetter = simpleName.substring(0, 1);
		String leftName = simpleName.substring(1);
		String aliasName = firstLetter.toLowerCase() + leftName;
		xstream.alias(aliasName, c);
	}
	
	/**
	 * Alias className for all classes current need to serialize
	 * 
	 * @see XStreamSerializer#getAllSerializedClasses()
	 * @throws SerializationException
	 */
	public void commonConfig() throws SerializationException {
		List<Class<?>> allSerializedClasses = this.getAllSerializedClasses();
		for (Class<?> c : allSerializedClasses) {
			this.aliasClassName(c);
		}
	}
	
	/**
	 * Expose the xstream object, so that module can config with xstream as need
	 * 
	 * @return xstream can be configed by module
	 */
	public XStream getXstream() {
		return xstream;
	}
	
	/**
	 * @see OpenmrsSerializer#serialize(java.lang.Object)
	 * @should not serialize proxies
	 */
	public String serialize(Object o) throws SerializationException {
		return xstream.toXML(o);
	}
	
	/**
	 * @see OpenmrsSerializer#deserialize(String, Class)
	 * @should not deserialize proxies
	 * @should ignore entities
	 */
	@SuppressWarnings("unchecked")
	public <T extends Object> T deserialize(String serializedObject, Class<? extends T> clazz) throws SerializationException {
		return (T) xstream.fromXML(serializedObject);
	}
	
	/**
	 * An instance of this converter needs to be registered with a higher priority than the rest so
	 * that it's called early in the converter chain. This way, we can make sure we never get to
	 * xstream's DynamicProxyConverter that can deserialize proxies.
	 *
	 * @see <a href="http://tinyurl.com/ord2rry">this blog</a>
	 */
	private class CustomDynamicProxyConverter extends DynamicProxyConverter {
		
		CustomDynamicProxyConverter() {
			super(null);
		}
		
		@Override
		public boolean canConvert(Class type) {
			if (type == null) {
				return false;
			}
			return super.canConvert(type);
		}
		
		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
			throw new XStreamException("Can't serialize proxies");
		}
		
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			throw new XStreamException("Can't deserialize proxies");
		}
		
	}
}
