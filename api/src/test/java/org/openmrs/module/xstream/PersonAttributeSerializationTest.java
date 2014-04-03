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
import static org.junit.Assert.assertFalse;

import java.text.SimpleDateFormat;

import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Test;
import org.openmrs.PersonAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.module.serialization.xstream.XStreamSerializer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * Test class that tests the serialization and deserialization of a PersonAttribute
 */
public class PersonAttributeSerializationTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * create a person attribute and make sure it can be serialized correctly
	 * 
	 * @throws Exception
	 */
	@Test
	@SkipBaseSetup
	public void shouldSerializePersonAttribute() throws Exception {
		//instantiate object
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/module/xstream/include/PersonAttributeSerializationTest.xml");
		authenticate();
		PersonAttribute pa = Context.getPersonService().getPersonAttribute(1);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		
		//serialize and compare with a give string
		String xmlOutput = Context.getSerializationService().serialize(pa, XStreamSerializer.class);
		XMLAssert.assertXpathEvaluatesTo("1", "/personAttribute/personAttributeId", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo("false", "/personAttribute/@voided", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo("0768f3da-b692-44b7-a33f-abf2c450474e", "/personAttribute/@uuid", xmlOutput);
		XMLAssert.assertXpathExists("/personAttribute/creator", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo(sdf.format(pa.getDateCreated()), "/personAttribute/dateCreated", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo("501", "/personAttribute/person/personId", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo("1", "/personAttribute/attributeType/personAttributeTypeId", xmlOutput);
	}
	
	/**
	 * Construct a serialized xml string and make sure it can be deserialized correctly
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldDeserializePersonAttribute() throws Exception {
		//construct given string to be deserialized
		StringBuilder xmlBuilder = new StringBuilder();
		xmlBuilder.append("<personAttribute id=\"1\" uuid=\"0768f3da-b692-44b7-a33f-abf2c450474e\" voided=\"false\">\n");
		xmlBuilder.append("  <creator id=\"2\" uuid=\"6adb7c42-cfd2-4301-b53b-ff17c5654ff7\" voided=\"false\">\n");
		xmlBuilder.append("    <creator reference=\"2\"/>\n");
		xmlBuilder.append("    <dateCreated class=\"sql-timestamp\" id=\"3\">2005-01-01 00:00:00 CST</dateCreated>\n");
		xmlBuilder.append("    <changedBy reference=\"2\"/>\n");
		xmlBuilder.append("    <dateChanged class=\"sql-timestamp\" id=\"4\">2007-09-20 21:54:12 CST</dateChanged>\n");
		xmlBuilder.append("    <retireReason></retireReason>\n");
		xmlBuilder.append("    <userId>1</userId>\n");
		xmlBuilder.append("    <systemId>1-8</systemId>\n");
		xmlBuilder.append("    <username>admin</username>\n");
		xmlBuilder.append("    <secretQuestion></secretQuestion>\n");
		xmlBuilder.append("    <roles id=\"11\">\n");
		xmlBuilder.append("      <role id=\"12\" uuid=\"0e43640b-67d1-4458-b47f-b64fd8ce4b0d\" retired=\"false\">\n");
		xmlBuilder.append("        <description>Developers of the OpenMRS .. have additional access to change fundamental structure of the database model.</description>\n");
		xmlBuilder.append("        <role>System Developer</role>\n");
		xmlBuilder.append("        <privileges id=\"12\"/>\n");
		xmlBuilder.append("        <inheritedRoles id=\"13\"/>\n");
		xmlBuilder.append("      </role>\n");
		xmlBuilder.append("    </roles>\n");
		xmlBuilder.append("    <userProperties id=\"14\"/>\n");
		xmlBuilder.append("    <parsedProficientLocalesProperty></parsedProficientLocalesProperty>\n");
		xmlBuilder.append("  </creator>\n");
		xmlBuilder.append("  <dateCreated class=\"sql-timestamp\" id=\"16\">2008-08-15 15:46:47 CST</dateCreated>\n");
		xmlBuilder.append("  <personAttributeId>1</personAttributeId>\n");
		xmlBuilder.append("    <person id=\"5\" uuid=\"6adb7c42-cfd2-4301-b53b-ff17c5654ff7\" voided=\"false\">\n");
		xmlBuilder.append("    <personId>1</personId>\n");
		xmlBuilder.append("    <addresses class=\"tree-set\" id=\"5\">\n");
		xmlBuilder.append("      <no-comparator/>\n");
		xmlBuilder.append("    </addresses>\n");
		xmlBuilder.append("    <names class=\"tree-set\" id=\"6\">\n");
		xmlBuilder.append("      <no-comparator/>\n");
		xmlBuilder.append("    </names>\n");
		xmlBuilder.append("    <attributes class=\"tree-set\" id=\"7\">\n");
		xmlBuilder.append("      <no-comparator/>\n");
		xmlBuilder.append("    </attributes>\n");
		xmlBuilder.append("    <gender></gender>\n");
		xmlBuilder.append("    <birthdate class=\"sql-timestamp\" id=\"8\">1975-06-30 00:00:00 CST</birthdate>\n");
		xmlBuilder.append("    <birthdateEstimated>false</birthdateEstimated>\n");
		xmlBuilder.append("    <dead>false</dead>\n");
		xmlBuilder.append("    <personCreator reference=\"2\"/>\n");
		xmlBuilder.append("    <personDateCreated class=\"sql-timestamp\" id=\"9\">2005-01-01 00:00:00 CST</personDateCreated>\n");
		xmlBuilder.append("    <personChangedBy reference=\"2\"/>\n");
		xmlBuilder.append("    <personDateChanged class=\"sql-timestamp\" id=\"10\">2007-09-20 21:54:12 CST</personDateChanged>\n");
		xmlBuilder.append("    <personVoided>false</personVoided>\n");
		xmlBuilder.append("    <personVoidReason></personVoidReason>\n");
		xmlBuilder.append("    <isPatient>false</isPatient>\n");
		xmlBuilder.append("    </person>\n");
		xmlBuilder.append("  <attributeType id=\"20\" uuid=\"b3b6d540-a32e-44c7-91b3-292d97667518\" retired=\"true\">\n");
		xmlBuilder.append("    <name>Race</name>\n");
		xmlBuilder.append("    <description>Group of persons related by common descent or heredity</description>\n");
		xmlBuilder.append("    <creator reference=\"2\"/>\n");
		xmlBuilder.append("    <dateCreated class=\"sql-timestamp\" id=\"30\">2007-05-04 09:59:23 CST</dateCreated>\n");
		xmlBuilder.append("    <dateRetired class=\"sql-timestamp\" id=\"31\">2008-08-15 00:00:00 CST</dateRetired>\n");
		xmlBuilder.append("    <retiredBy reference=\"2\"/>\n");
		xmlBuilder.append("    <retireReason>test</retireReason>\n");
		xmlBuilder.append("    <personAttributeTypeId>1</personAttributeTypeId>\n");
		xmlBuilder.append("    <format>java.lang.String</format>\n");
		xmlBuilder.append("    <searchable>false</searchable>\n");
		xmlBuilder.append("  </attributeType>\n");
		xmlBuilder.append("  <value></value>\n");
		xmlBuilder.append("</personAttribute>\n");
		
		//deserialize and make sure everything has been put into object
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		
		PersonAttribute pa = Context.getSerializationService().deserialize(xmlBuilder.toString(), PersonAttribute.class,
		    XStreamSerializer.class);
		assertEquals(1, pa.getPersonAttributeId().intValue());
		assertEquals("0768f3da-b692-44b7-a33f-abf2c450474e", pa.getUuid());
		assertEquals(1, pa.getCreator().getUserId().intValue());
		assertEquals(sdf.parse("2008-08-15 15:46:47 CST"), pa.getDateCreated());
		assertEquals(1, pa.getPerson().getPersonId().intValue());
		assertEquals(1, pa.getAttributeType().getPersonAttributeTypeId().intValue());
		assertFalse("The voided shouldn't be " + pa.getVoided(), pa.getVoided());
	}
}
