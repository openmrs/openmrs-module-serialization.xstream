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

import java.text.SimpleDateFormat;

import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.serialization.xstream.XStreamSerializer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * Test class that tests the serialization and deserialization of a cohort
 */
public class CohortSerializationTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * create a cohort and make sure it can be serialized correctly
	 * 
	 * @throws Exception
	 */
	@Test
	@SkipBaseSetup
	public void shouldSerializeCohort() throws Exception {
		//instantiate object
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/module/xstream/include/CohortSerializationTest.xml");
		authenticate();
		
		Cohort cohort = Context.getCohortService().getCohort(1);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		
		User user = Context.getUserService().getUser(501);
		User user2 = Context.getUserService().getUser(502);
		cohort.getCreator().setCreator(user);
		cohort.getCreator().setChangedBy(user2);
		//serialize and compare with a give string
		String xmlOutput = Context.getSerializationService().serialize(cohort, XStreamSerializer.class);
		XMLAssert.assertXpathEvaluatesTo("1", "/cohort/cohortId", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo("57b9e333-751c-4291-80d1-449412ac2cd3", "/cohort/@uuid", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo("false", "/cohort/@voided", xmlOutput);
		XMLAssert.assertXpathExists("/cohort/creator", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo(sdf.format(cohort.getDateCreated()), "/cohort/dateCreated", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo("old cohorts", "/cohort/name", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo("This is a cohort in which every one's age is above 60", "/cohort/description",
		    xmlOutput);
		XMLAssert.assertXpathExists("/cohort/memberIds[int=6]", xmlOutput);
		XMLAssert.assertXpathExists("/cohort/memberIds[int=7]", xmlOutput);
		XMLAssert.assertXpathExists("/cohort/memberIds[int=8]", xmlOutput);
	}
	
	/**
	 * Construct a serialized xml string and make sure it can be deserialized correctly
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldDeserializeCohort() throws Exception {
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/module/xstream/include/CohortSerializationTest.xml");
		authenticate();
		
		TestUtil.testDeserialize(Context.getCohortService().getCohort(1));
	}
}