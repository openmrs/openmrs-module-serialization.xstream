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
import org.openmrs.ConceptComplex;
import org.openmrs.api.context.Context;
import org.openmrs.module.serialization.xstream.XStreamSerializer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * Test class that tests the serialization and deserialization of a conceptComplex
 */
public class ConceptComplexSerialization1_9Test extends BaseModuleContextSensitiveTest {
	
	/**
	 * create a conceptComplex and make sure it can be serialized correctly
	 * 
	 * @throws Exception
	 */
	@Test
	@SkipBaseSetup
	public void shouldSerializeConceptComplex() throws Exception {
		//instantiate object
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/module/xstream/include/ConceptComplexSerialization1_9Test.xml");
		authenticate();
		
		ConceptComplex cc = Context.getConceptService().getConceptComplex(3);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		
		//serialize and compare with a give string
		String xmlOutput = Context.getSerializationService().serialize(cc, XStreamSerializer.class);
		XMLAssert.assertXpathEvaluatesTo("0cbe2ed3-cd5f-4f46-9459-26127c9265ab", "/conceptComplex/@uuid", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo("3", "/conceptComplex/conceptId", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo("false", "/conceptComplex/@retired", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo("4", "/conceptComplex/datatype/conceptDatatypeId", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo("3", "/conceptComplex/conceptClass/conceptClassId", xmlOutput);
		XMLAssert.assertXpathExists("/conceptComplex/creator", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo("false", "/conceptComplex/set", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo(sdf.format(cc.getDateCreated()), "/conceptComplex/dateCreated", xmlOutput);
		XMLAssert.assertXpathExists("/conceptComplex/names/conceptName[conceptNameId=2456]", xmlOutput);
		XMLAssert.assertXpathExists("/conceptComplex/answers/conceptAnswer[conceptAnswerId=762]", xmlOutput);
		XMLAssert.assertXpathExists("/conceptComplex/conceptSets/conceptSet[conceptSetId=1]", xmlOutput);
		XMLAssert.assertXpathExists("/conceptComplex/descriptions/conceptDescription[conceptDescriptionId=9]", xmlOutput);
		XMLAssert.assertXpathExists("/conceptComplex/conceptMappings/conceptMap[conceptMapId=1]", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo("test purpose", "/conceptComplex/handler", xmlOutput);
	}
	
	/**
	 * Construct a serialized xml string and make sure it can be deserialized correctly
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldDeserializeConceptComplex() throws Exception {
		String xml = Test1_9Util.getFileContents("org/openmrs/module/xstream/include/TestConceptComplex.xml");
		ConceptComplex cc = Context.getSerializationService()
		        .deserialize(xml, ConceptComplex.class, XStreamSerializer.class);
		assertEquals("0cbe2ed3-cd5f-4f46-9459-26127c9265ab", cc.getUuid());
		assertEquals(3, cc.getConceptId().intValue());
		assertFalse("The retired shouldn't be " + cc.isRetired(), cc.isRetired());
		assertEquals(4, cc.getDatatype().getConceptDatatypeId().intValue());
		assertEquals(3, cc.getConceptClass().getConceptClassId().intValue());
		assertEquals(1, cc.getCreator().getUserId().intValue());
		assertFalse("The set shouldn't be " + cc.getSet(), cc.getSet());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		assertEquals(sdf.parse("2008-08-15 15:27:51 CST"), cc.getDateCreated());
		assertEquals(1, cc.getNames().size());
		assertEquals(1, cc.getAnswers().size());
		assertEquals(1, cc.getDescriptions().size());
		assertEquals(1, cc.getConceptSets().size());
		assertEquals(1, cc.getConceptMappings().size());
		assertEquals("test purpose", cc.getHandler());
	}
}
