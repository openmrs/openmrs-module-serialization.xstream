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
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.serialization.xstream.Version;
import org.openmrs.module.serialization.xstream.XStreamSerializer;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.util.OpenmrsConstants;

/**
 * Test class that tests the serialization and deserialization of a concept
 */
public class ConceptSerializationTest extends BaseVersionSensitiveTest {
	
	/**
	 * create a concept and make sure it can be serialized correctly
	 * 
	 * @throws Exception
	 */
	@Test
	@SkipBaseSetup
	public void shouldSerializeConcept() throws Exception {
		//instantiate object
		initializeInMemoryDatabase();
		String testdatasetFilename = resolveTestDatasetFilename("org/openmrs/module/xstream/include/ConceptSerializationTest"
		        + VERSION_PLACE_HOLDER + ".xml");
		executeDataSet(testdatasetFilename);
		authenticate();
		
		Concept concept = Context.getConceptService().getConcept(3);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		
		//serialize and compare with a give string
		String xmlOutput = Context.getSerializationService().serialize(concept, XStreamSerializer.class);
		XMLAssert.assertXpathEvaluatesTo("0cbe2ed3-cd5f-4f46-9459-26127c9265ab", "//concept/@uuid", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo("3", "/concept/conceptId", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo("false", "/concept/@retired", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo("4", "/concept/datatype/conceptDatatypeId", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo("3", "/concept/conceptClass/conceptClassId", xmlOutput);
		XMLAssert.assertXpathExists("/concept/creator", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo("false", "/concept/set", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo(sdf.format(concept.getDateCreated()), "/concept/dateCreated", xmlOutput);
		XMLAssert.assertXpathExists("/concept/names/conceptName[conceptNameId=2456]", xmlOutput);
		XMLAssert.assertXpathExists("/concept/answers/conceptAnswer[conceptAnswerId=762]", xmlOutput);
		XMLAssert.assertXpathExists("/concept/conceptSets/conceptSet[conceptSetId=1]", xmlOutput);
		XMLAssert.assertXpathExists("/concept/descriptions/conceptDescription[conceptDescriptionId=9]", xmlOutput);
		XMLAssert.assertXpathExists("/concept/conceptMappings/conceptMap[conceptMapId=1]", xmlOutput);
		if (VERSION_ONE_NINE.compareTo(new Version(OpenmrsConstants.OPENMRS_VERSION_SHORT)) <= 0) {
			XMLAssert.assertXpathExists("/concept/conceptMappings/conceptMap/conceptMapType[conceptMapTypeId=1]", xmlOutput);
			XMLAssert.assertXpathExists(
			    "/concept/conceptMappings/conceptMap/conceptReferenceTerm[conceptReferenceTermId=1]", xmlOutput);
		}
	}
	
	/**
	 * Construct a serialized xml string and make sure it can be deserialized correctly
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldDeserializeConcept() throws Exception {
		String serializedTest = getSerializedContents("org/openmrs/module/xstream/include/ConceptDeserializationTest"
		        + VERSION_PLACE_HOLDER + ".xml");
		//deserialize and make sure everything has been put into object
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		
		Concept concept = Context.getSerializationService().deserialize(serializedTest, Concept.class,
		    XStreamSerializer.class);
		assertEquals("0cbe2ed3-cd5f-4f46-9459-26127c9265ab", concept.getUuid());
		assertEquals(3, concept.getConceptId().intValue());
		assertFalse("The retired shouldn't be " + concept.isRetired(), concept.isRetired());
		assertEquals(4, concept.getDatatype().getConceptDatatypeId().intValue());
		assertEquals(3, concept.getConceptClass().getConceptClassId().intValue());
		assertEquals(1, concept.getCreator().getUserId().intValue());
		assertFalse("The set shouldn't be " + concept.getSet(), concept.getSet());
		assertEquals(sdf.parse("2008-08-15 15:27:51 CST"), concept.getDateCreated());
		assertEquals(1, concept.getNames().size());
		assertEquals(1, concept.getAnswers().size());
		assertEquals(1, concept.getDescriptions().size());
		assertEquals(1, concept.getConceptSets().size());
		assertEquals(1, concept.getConceptMappings().size());
	}
}
