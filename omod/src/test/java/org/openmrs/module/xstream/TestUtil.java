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
package org.openmrs.module.xstream;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Ignore;
import org.openmrs.api.context.Context;
import org.openmrs.module.serialization.xstream.Version;
import org.openmrs.module.serialization.xstream.XStreamSerializer;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.Reflect;

/**
 * Utility methods used during unit testing
 */
@Ignore
public class TestUtil {
	
	protected final static Version VERSION_ONE_NINE = new Version("1.9");
	
	protected final static String VERSION_PLACE_HOLDER = "{VERSION}";
	
	/**
	 * Tests deserialization of a given object.
	 * 
	 * @param object the object to test
	 * @throws Exception
	 */
	public static void testDeserialize(Object object) throws Exception {
		String xml = Context.getSerializationService().serialize(object, XStreamSerializer.class);
		Object deserializedObject = Context.getSerializationService().deserialize(xml, object.getClass(),
		    XStreamSerializer.class);
		
		List<Field> fields = Reflect.getAllFields(object.getClass());
		for (Field field : fields) {
			Assert.assertEquals(field.get(object), field.get(deserializedObject));
		}
	}
	
	/**
	 * Reads and returns the contents of the file with the specified name, it reads the appropriate
	 * test data set depending on the openmrs version the tests are being run against
	 * 
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public static String getSerializedContents(String filename) throws Exception {
		filename = resolveTestDatasetFilename(filename);
		InputStream in = TestUtil.class.getClassLoader().getResourceAsStream(resolveTestDatasetFilename(filename));
		try {
			return IOUtils.toString(in);
		}
		finally {
			in.close();
		}
	}
	
	/**
	 * Convenience method that checks sets the appropriate filename depending on the openmrs version
	 * the tests are being run against
	 * 
	 * @param filename
	 * @return
	 */
	public static String resolveTestDatasetFilename(String filename) {
		if (VERSION_ONE_NINE.compareTo(new Version(OpenmrsConstants.OPENMRS_VERSION_SHORT)) <= 0) {
			return StringUtils.replace(filename, VERSION_PLACE_HOLDER, "-19");
		}
		return StringUtils.replace(filename, VERSION_PLACE_HOLDER, "");
	}
}
