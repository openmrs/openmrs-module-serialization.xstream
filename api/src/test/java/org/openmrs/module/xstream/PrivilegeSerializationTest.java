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
import org.openmrs.Privilege;
import org.openmrs.api.context.Context;
import org.openmrs.module.serialization.xstream.XStreamSerializer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

import static org.junit.Assert.assertEquals;
import static org.xmlunit.assertj.XmlAssert.assertThat;

/**
 * Test class that tests the serialization and deserialization of a privilege
 */
public class PrivilegeSerializationTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * create a privilege and make sure it can be serialized correctly
	 * 
	 * @throws Exception
	 */
	@Test
	@SkipBaseSetup
	public void shouldSerializePrivilege() throws Exception {
		//instantiate object
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/module/xstream/include/PrivilegeSerializationTest.xml");
		authenticate();
		
		Privilege p = Context.getUserService().getPrivilege("Delete Cohorts");
		
		//serialize and compare with a give string
		String xmlOutput = Context.getSerializationService().serialize(p, XStreamSerializer.class);
		assertThat(xmlOutput).valueByXPath("/privilege/privilege").isEqualTo("Delete Cohorts");
		assertThat(xmlOutput).valueByXPath("/privilege/@uuid").isEqualTo("afc993ec-8b43-4af2-9974-54c7f98214ce");
		assertThat(xmlOutput).valueByXPath("/privilege/description").isEqualTo("Able to add a cohort to the system");
	}
	
	/**
	 * Construct a serialized xml string and make sure it can be deserialized correctly
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldDeserializePrivilege() throws Exception {
		//construct given string to be deserialized
		StringBuilder xmlBuilder = new StringBuilder();
		xmlBuilder.append("<privilege id=\"1\" uuid=\"afc993ec-8b43-4af2-9974-54c7f98214ce\" retired=\"false\">\n");
		xmlBuilder.append("  <description>Able to add a cohort to the system</description>\n");
		xmlBuilder.append("  <privilege>Delete Cohorts</privilege>\n");
		xmlBuilder.append("</privilege>\n");
		
		//deserialize and make sure everything has been put into object
		Privilege p = Context.getSerializationService().deserialize(xmlBuilder.toString(), Privilege.class,
		    XStreamSerializer.class);
		assertEquals("Delete Cohorts", p.getPrivilege());
		assertEquals("afc993ec-8b43-4af2-9974-54c7f98214ce", p.getUuid());
		assertEquals("Able to add a cohort to the system", p.getDescription());
	}
}
