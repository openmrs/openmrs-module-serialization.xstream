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

import static org.junit.Assert.assertEquals;

import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.serialization.xstream.XStreamShortSerializer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * Test class that test the short serialization and short deserialization of a location
 */
public class LocationShortSerialization1_9Test extends BaseModuleContextSensitiveTest {
	
	/**
	 * generate the relative objects and make sure the short serialization can work
	 * 
	 * @throws Exception
	 */
	@Test
	@SkipBaseSetup
	public void shouldLocationShortSerialization() throws Exception {
		
		//prepare the necessary data
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/module/xstream/include/EncounterTypeShortSerialization1_9Test.xml");
		authenticate();
		
		Encounter e = Context.getEncounterService().getEncounter(4);
		String xmlOutput = Context.getSerializationService().serialize(e, XStreamShortSerializer.class);
		//should only serialize "uuid"
		XMLAssert.assertXpathEvaluatesTo("dc5c1fcc-0459-4201-bf70-0b90535ba362", "/encounter/location/@uuid", xmlOutput);
		//with short serialization, the "location" element shouldn't contain any child element in the serialized xml
		XMLAssert.assertXpathNotExists("/encounter/location/*", xmlOutput);
	}
	
	/**
	 * give a expected xml string and make sure it can be shortly deserialized
	 * 
	 * @throws Exception
	 */
	@Test
	@SkipBaseSetup
	public void shouldEncounterTypeShortDeserialization() throws Exception {
		//prepare the necessary data
		
		/*
		 * Because "XXXShortConverter.unmarshal(HierarchicalStreamReader, UnmarshallingContext)" has operations accessing data in database,
		 * We also need to use the "EncounterTypeShortSerializationTest.xml" here 
		 */
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/module/xstream/include/EncounterTypeShortSerialization1_9Test.xml");
		authenticate();
		
		//prepare the necessary data
		StringBuilder xmlBuilder = new StringBuilder();
		xmlBuilder.append("<encounter id=\"1\" uuid=\"eec646cb-c847-45a7-98bc-91c8c4f70add\" voided=\"false\">\n");
		xmlBuilder.append("  <creator id=\"2\" uuid=\"6adb7c42-cfd2-4301-b53b-ff17c5654ff7\"/>\n");
		xmlBuilder.append("  <dateCreated class=\"sql-timestamp\" id=\"3\">2008-08-18 14:22:17 CST</dateCreated>\n");
		xmlBuilder.append("  <encounterId>4</encounterId>\n");
		xmlBuilder
		        .append("  <encounterDatetime class=\"sql-timestamp\" id=\"4\">2008-08-15 00:00:00 CST</encounterDatetime>\n");
		xmlBuilder.append("  <patient id=\"5\" uuid=\"5946f880-b197-400b-9caa-a3c661d23041\"/>\n");
		xmlBuilder.append("  <patientId>7</patientId>\n");
		xmlBuilder.append("  <location id=\"6\" uuid=\"dc5c1fcc-0459-4201-bf70-0b90535ba362\" retired=\"false\"/>\n");
		xmlBuilder.append("  <form id=\"10\" uuid=\"d9218f76-6c39-45f4-8efa-4c5c6c199f50\" retired=\"false\">\n");
		xmlBuilder.append("    <name>Basic Form</name>\n");
		xmlBuilder.append("    <description>Test form</description>\n");
		xmlBuilder.append("    <creator reference=\"2\"/>\n");
		xmlBuilder.append("    <dateCreated class=\"sql-timestamp\" id=\"11\">2005-08-07 00:00:00 CST</dateCreated>\n");
		xmlBuilder.append("    <changedBy reference=\"2\"/>\n");
		xmlBuilder.append("    <dateChanged class=\"sql-timestamp\" id=\"12\">2007-10-24 14:51:53 CST</dateChanged>\n");
		xmlBuilder.append("    <formId>1</formId>\n");
		xmlBuilder.append("    <version>0.1</version>\n");
		xmlBuilder.append("    <build>0</build>\n");
		xmlBuilder.append("    <published>false</published>\n");
		xmlBuilder.append("    <formFields id=\"13\"/>\n");
		xmlBuilder.append("  </form>\n");
		xmlBuilder.append("  <encounterType id=\"14\" uuid=\"61ae96f4-6afe-4351-b6f8-cd4fc383cce1\"/>\n");
		xmlBuilder.append("  <provider id=\"15\" uuid=\"341b4e41-790c-484f-b6ed-71dc8da222de\"/>\n");
		xmlBuilder.append("  <orders id=\"16\"/>\n");
		xmlBuilder.append("  <obs id=\"17\"/>\n");
		xmlBuilder.append("</encounter>\n");
		
		Encounter e = Context.getSerializationService().deserialize(xmlBuilder.toString(), Encounter.class,
		    XStreamShortSerializer.class);
		assertEquals("dc5c1fcc-0459-4201-bf70-0b90535ba362", e.getLocation().getUuid());
	}
}
