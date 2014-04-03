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
import org.openmrs.PatientIdentifierType;
import org.openmrs.ProgramWorkflow;
import org.openmrs.api.context.Context;
import org.openmrs.module.serialization.xstream.XStreamShortSerializer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.test.TestUtil;

/**
 * Test class that test the short serialization and short deserialization of a Program object
 */
public class ProgramShortSerializationTest extends BaseModuleContextSensitiveTest {
	/**
	 * generate the relative objects and make sure the short serialization can work
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldProgramShortSerialization() throws Exception {
		//prepare the necessary data
		
		ProgramWorkflow pwf = Context.getProgramWorkflowService().getWorkflow(1);
		String xmlOutput = Context.getSerializationService().serialize(pwf, XStreamShortSerializer.class);
		//should only serialize "uuid"
		XMLAssert.assertXpathEvaluatesTo("da4a0391-ba62-4fad-ad66-1e3722d16380", "/programWorkflow/program/@uuid",
		    xmlOutput);
		//with short serialization, the "program" element shouldn't contain any child element in the serialized xml
		XMLAssert.assertXpathNotExists("/programWorkflow/program/*", xmlOutput);
	}
	
	/**
	 * give a expected xml string and make sure it can be shortly deserialized
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldProgramShortDeserialization() throws Exception {
		//prepare the necessary data
		
		StringBuilder xmlBuilder = new StringBuilder();
		xmlBuilder.append("<programWorkflow id=\"1\" uuid=\"84f0effa-dd73-46cb-b931-7cd6be6c5f81\" retired=\"false\">\n");
		xmlBuilder.append("  <creator id=\"2\" uuid=\"ba1b19c2-3ed6-4f63-b8c0-f762dc8d7562\"/>\n");
		xmlBuilder.append("  <dateCreated class=\"sql-timestamp\" id=\"3\">2008-08-15 17:01:00 CST</dateCreated>\n");
		xmlBuilder.append("  <changedBy />\n");
		xmlBuilder.append("  <dateChanged class=\"sql-timestamp\" id=\"4\">2008-08-15 17:04:50 CST</dateChanged>\n");
		xmlBuilder.append("  <programWorkflowId>1</programWorkflowId>\n");
		xmlBuilder.append("  <program id=\"5\" uuid=\"da4a0391-ba62-4fad-ad66-1e3722d16380\"/>\n");
		xmlBuilder.append("  <concept id=\"6\" uuid=\"0955b484-b364-43dd-909b-1fa3655eaad2\"/>\n");
		xmlBuilder.append("  <states id=\"7\">\n");
		xmlBuilder.append("    <programWorkflowState id=\"8\" uuid=\"92584cdc-6a20-4c84-a659-e035e45d36b0\"/>\n");
		xmlBuilder.append("    <programWorkflowState id=\"9\" uuid=\"0d5f1bb4-2edb-4dd1-8d9f-34489bb4d9ea\"/>\n");
		xmlBuilder.append("    <programWorkflowState id=\"10\" uuid=\"e938129e-248a-482a-acea-f85127251472\"/>\n");
		xmlBuilder.append("  </states>\n");
		xmlBuilder.append("</programWorkflow>\n");
		
		ProgramWorkflow pwf = Context.getSerializationService().deserialize(xmlBuilder.toString(),
				ProgramWorkflow.class, XStreamShortSerializer.class);
		assertEquals("da4a0391-ba62-4fad-ad66-1e3722d16380", pwf.getProgram().getUuid());
	}
}
