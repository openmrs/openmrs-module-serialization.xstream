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

import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Test;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.serialization.xstream.XStreamSerializer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test class that tests the serialization and deserialization of a user
 */
public class UserSerializaionTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * create a user and make sure it can be serialized correctly
	 * 
	 * @throws Exception
	 */
	@Test
	@SkipBaseSetup
	public void shouldSerializeUser() throws Exception {
		//instantiate object
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/module/xstream/include/UserSerializaionTest.xml");
		authenticate();
		
		User user = Context.getUserService().getUser(501);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		
		//serialize and compare with a give string
		String xmlOutput = Context.getSerializationService().serialize(user, XStreamSerializer.class);
		XMLAssert.assertXpathEvaluatesTo("55685062-1b48-11df-a5c7-001e378eb67e", "/user/@uuid", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo("true", "/user/@retired", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo("501", "/user/person/personId", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo(sdf.format(user.getDateCreated()), "/user/dateCreated", xmlOutput);
		XMLAssert.assertXpathExists("/user/changedBy/@reference", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo(sdf.format(user.getDateChanged()), "/user/dateChanged", xmlOutput);
		XMLAssert.assertXpathExists("/user/person/personVoidedBy/@reference", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo(sdf.format(user.getDateRetired()), "/user/dateRetired", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo("Test purposes", "/user/retireReason", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo("501", "/user/person/personId", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo("F", "/user/person/gender", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo("false", "/user/person/dead", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo("501", "/user/userId", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo("2-6", "/user/systemId", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo("bruno", "/user/username", xmlOutput);
		XMLAssert.assertXpathExists("/user/roles/role[role='Provider']", xmlOutput);
		XMLAssert.assertXpathExists("/user/userProperties/entry[string='lockoutTimestamp']", xmlOutput);
		XMLAssert.assertXpathExists("/user/userProperties/entry[string='loginAttempts']", xmlOutput);
	}
	
	/**
	 * Construct a serialized xml string and make sure it can be deserialized correctly
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldDeserializeUser() throws Exception {
		//construct given string to be deserialized
		StringBuilder xmlBuilder = new StringBuilder();
		xmlBuilder.append("<user id=\"1\" uuid=\"df8ae447-6745-45be-b859-403241d9913c\" retired=\"true\">\n");
		xmlBuilder.append("  <creator id=\"2\" uuid=\"6adb7c42-cfd2-4301-b53b-ff17c5654ff7\" voided=\"false\">\n");
		xmlBuilder.append("    <creator reference=\"2\"/>\n");
		xmlBuilder.append("    <dateCreated class=\"sql-timestamp\" id=\"3\">2005-01-01 00:00:00 CST</dateCreated>\n");
		xmlBuilder.append("    <changedBy reference=\"2\"/>\n");
		xmlBuilder.append("    <dateChanged class=\"sql-timestamp\" id=\"4\">2007-09-20 21:54:12 CST</dateChanged>\n");
		xmlBuilder.append("    <userId>1</userId>\n");
		xmlBuilder.append("    <systemId>1-8</systemId>\n");
		xmlBuilder.append("    <username>admin</username>\n");
		xmlBuilder.append("    <roles id=\"11\">\n");
		xmlBuilder.append("      <role id=\"12\" uuid=\"0e43640b-67d1-4458-b47f-b64fd8ce4b0d\" retired=\"false\">\n");
		xmlBuilder
		        .append("        <description>Developers of the OpenMRS .. have additional access to change fundamental structure of the database model.</description>\n");
		xmlBuilder.append("        <role>System Developer</role>\n");
		xmlBuilder.append("        <privileges id=\"13\"/>\n");
		xmlBuilder.append("        <inheritedRoles id=\"14\"/>\n");
		xmlBuilder.append("      </role>\n");
		xmlBuilder.append("    </roles>\n");
		xmlBuilder.append("    <userProperties id=\"15\"/>\n");
		xmlBuilder.append("    <parsedProficientLocalesProperty></parsedProficientLocalesProperty>\n");
		xmlBuilder.append("  </creator>\n");
		xmlBuilder.append("  <dateCreated class=\"sql-timestamp\" id=\"16\">2008-08-15 15:46:47 CST</dateCreated>\n");
		xmlBuilder.append("  <changedBy reference=\"2\"/>\n");
		xmlBuilder.append("  <dateChanged class=\"sql-timestamp\" id=\"17\">2008-08-15 15:47:07 CST</dateChanged>\n");
		xmlBuilder.append("  <dateVoided class=\"sql-timestamp\" id=\"18\">2008-08-30 15:47:07 CST</dateVoided>\n");
		xmlBuilder.append("  <retireReason>Test purposes</retireReason>\n");
		xmlBuilder.append("  <dateRetired class=\"sql-timestamp\" id=\"4\">2008-08-30 15:47:07 CST</dateRetired>\n");
		xmlBuilder.append("  <retiredBy reference=\"2\"/>\n");
		xmlBuilder.append("    <person id=\"6\" uuid=\"6adb7c42-cfd2-4301-b53b-ff17c5654ff7\" voided=\"false\">\n");
		xmlBuilder.append("    <personId>501</personId>\n");
		xmlBuilder.append("    <addresses class=\"tree-set\" id=\"5\">\n");
		xmlBuilder.append("      <no-comparator/>\n");
		xmlBuilder.append("    </addresses>\n");
		xmlBuilder.append("    <names class=\"tree-set\" id=\"6\">\n");
		xmlBuilder.append("      <no-comparator/>\n");
		xmlBuilder.append("    </names>\n");
		xmlBuilder.append("    <attributes class=\"tree-set\" id=\"7\">\n");
		xmlBuilder.append("      <no-comparator/>\n");
		xmlBuilder.append("    </attributes>\n");
		xmlBuilder.append("    <gender>F</gender>\n");
		xmlBuilder.append("    <birthdate class=\"sql-timestamp\" id=\"8\">1975-06-30 00:00:00 CST</birthdate>\n");
		xmlBuilder.append("    <birthdateEstimated>false</birthdateEstimated>\n");
		xmlBuilder.append("    <dead>false</dead>\n");
		xmlBuilder.append("    <personCreator reference=\"2\"/>\n");
		xmlBuilder
		        .append("    <personDateCreated class=\"sql-timestamp\" id=\"9\">2005-01-01 00:00:00 CST</personDateCreated>\n");
		xmlBuilder.append("    <personChangedBy reference=\"2\"/>\n");
		xmlBuilder
		        .append("    <personDateChanged class=\"sql-timestamp\" id=\"10\">2007-09-20 21:54:12 CST</personDateChanged>\n");
		xmlBuilder.append("    <personVoided>false</personVoided>\n");
		xmlBuilder.append("    <personVoidReason></personVoidReason>\n");
		xmlBuilder.append("    <isPatient>false</isPatient>\n");
		xmlBuilder.append("    </person>\n");
		xmlBuilder.append("  <addresses class=\"tree-set\" id=\"19\">\n");
		xmlBuilder.append("    <no-comparator/>\n");
		xmlBuilder.append("  </addresses>\n");
		xmlBuilder.append("  <names class=\"tree-set\" id=\"20\">\n");
		xmlBuilder.append("    <no-comparator/>\n");
		xmlBuilder.append("  </names>\n");
		xmlBuilder.append("  <attributes class=\"tree-set\" id=\"21\">\n");
		xmlBuilder.append("    <no-comparator/>\n");
		xmlBuilder.append("  </attributes>\n");
		xmlBuilder.append("  <userId>501</userId>\n");
		xmlBuilder.append("  <systemId>2-6</systemId>\n");
		xmlBuilder.append("  <username>bruno</username>\n");
		xmlBuilder.append("  <roles id=\"25\">\n");
		xmlBuilder.append("    <role id=\"26\" uuid=\"3480cb6d-c291-46c8-8d3a-96dc33d199fb\" retired=\"false\">\n");
		xmlBuilder.append("      <description>General privileges held by all providers</description>\n");
		xmlBuilder.append("      <role>Provider</role>\n");
		xmlBuilder.append("      <privileges id=\"27\"/>\n");
		xmlBuilder.append("      <inheritedRoles id=\"28\"/>\n");
		xmlBuilder.append("    </role>\n");
		xmlBuilder.append("  </roles>\n");
		xmlBuilder.append("  <userProperties id=\"29\">\n");
		xmlBuilder.append("    <entry>\n");
		xmlBuilder.append("      <string>lockoutTimestamp</string>\n");
		xmlBuilder.append("      <string>0</string>\n");
		xmlBuilder.append("    </entry>\n");
		xmlBuilder.append("    <entry>\n");
		xmlBuilder.append("      <string>loginAttempts</string>\n");
		xmlBuilder.append("      <string>0</string>\n");
		xmlBuilder.append("    </entry>\n");
		xmlBuilder.append("  </userProperties>\n");
		xmlBuilder.append("  <parsedProficientLocalesProperty></parsedProficientLocalesProperty>\n");
		xmlBuilder.append("</user>\n");
		
		//deserialize and make sure everything has been put into object
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		
		User user = Context.getSerializationService()
		        .deserialize(xmlBuilder.toString(), User.class, XStreamSerializer.class);
		assertEquals("df8ae447-6745-45be-b859-403241d9913c", user.getUuid());
		assertTrue("The retired shouldn't be " + user.getRetired(), user.getRetired());
		assertEquals(1, user.getCreator().getUserId().intValue());
		assertEquals(sdf.parse("2008-08-15 15:46:47 CST"), user.getDateCreated());
		assertEquals(1, user.getChangedBy().getUserId().intValue());
		assertEquals(sdf.parse("2008-08-15 15:47:07 CST"), user.getDateChanged());
		assertEquals(1, user.getRetiredBy().getUserId().intValue());
		assertEquals(sdf.parse("2008-08-30 15:47:07 CST"), user.getDateRetired());
		assertEquals("Test purposes", user.getRetireReason());
		assertEquals(501, user.getPerson().getPersonId().intValue());
		assertEquals("F", user.getPerson().getGender());
		assertFalse("The dead shouldn't be " + user.getPerson().getDead(), user.getPerson().getDead());
		assertEquals(501, user.getUserId().intValue());
		assertEquals("2-6", user.getSystemId());
		assertEquals("bruno", user.getUsername());
		assertEquals(1, user.getRoles().size());
		assertEquals(2, user.getUserProperties().entrySet().size());
	}
}
