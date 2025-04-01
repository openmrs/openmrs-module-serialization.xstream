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

import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.serialization.xstream.XStreamSerializer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

import java.text.SimpleDateFormat;

import static org.xmlunit.assertj.XmlAssert.assertThat;

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
		assertThat(xmlOutput).valueByXPath("/cohort/cohortId").isEqualTo("1");
		assertThat(xmlOutput).valueByXPath("/cohort/@uuid").isEqualTo("57b9e333-751c-4291-80d1-449412ac2cd3");
		assertThat(xmlOutput).valueByXPath("/cohort/@voided").isEqualTo("false");
		assertThat(xmlOutput).nodesByXPath("/cohort/creator").exist();
		assertThat(xmlOutput).valueByXPath("/cohort/dateCreated").isEqualTo(sdf.format(cohort.getDateCreated()));
		assertThat(xmlOutput).valueByXPath("/cohort/name").isEqualTo("old cohorts");
		assertThat(xmlOutput).valueByXPath("/cohort/description")
			.isEqualTo("This is a cohort in which every one's age is above 60");

		assertThat(xmlOutput).hasXPath("//cohortMemberId[text()='6']").exist();
		assertThat(xmlOutput).nodesByXPath("//cohortMemberId[text()='7']").exist();
		assertThat(xmlOutput).nodesByXPath("//cohortMemberId[text()='8']").exist();
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
