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

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.openmrs.util.OpenmrsClassLoader;

/**
 * Utility methods used during unit testing
 */
@Ignore
public class Test1_9Util extends TestUtil {
	
	/**
	 * Reads and returns the contents of the file with the specified name, assumes the file is on
	 * the classpath
	 * 
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public static String getFileContents(String filename) throws Exception {
		return IOUtils.toString(OpenmrsClassLoader.getInstance().getResourceAsStream(filename), "UTF-8");
	}
}
