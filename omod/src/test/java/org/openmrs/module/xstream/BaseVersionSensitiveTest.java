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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.openmrs.module.serialization.xstream.Version;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsConstants;

/**
 * Base class for tests that have to add test datasets that should depend on the openmrs version the
 * tests are being run against
 */
public abstract class BaseVersionSensitiveTest extends BaseModuleContextSensitiveTest {
	
	protected final static Version VERSION_ONE_NINE = new Version("1.9");
	
	protected final static String VERSION_PLACE_HOLDER = "{VERSION}";
	
	/**
	 * Reads and returns the contents of the file with the specified name, it reads the appropriate
	 * test data set depending on the openmrs version the tests are being run against
	 * 
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public String getSerializedContents(String filename) throws Exception {
		filename = resolveTestDatasetFilename(filename);
		InputStream in = getClass().getClassLoader().getResourceAsStream(resolveTestDatasetFilename(filename));
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
	protected String resolveTestDatasetFilename(String filename) {
		if (VERSION_ONE_NINE.compareTo(new Version(OpenmrsConstants.OPENMRS_VERSION_SHORT)) <= 0) {
			return StringUtils.replace(filename, VERSION_PLACE_HOLDER, "-19");
		}
		return StringUtils.replace(filename, VERSION_PLACE_HOLDER, "");
	}
}
