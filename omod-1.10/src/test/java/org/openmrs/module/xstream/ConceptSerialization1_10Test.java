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
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.context.Context;
import org.openmrs.module.serialization.xstream.XStreamSerializer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.stereotype.Component;

/**
 * Test class that tests the serialization and deserialization of a concept
 */
@Component
@OpenmrsProfile(openmrsVersion = "1.10")
public class ConceptSerialization1_10Test extends BaseModuleContextSensitiveTest {
	
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
		executeDataSet("org/openmrs/module/xstream/include/ConceptSerialization1_10Test.xml");
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
		XMLAssert.assertXpathExists("/concept/answers/conceptAnswer[conceptAnswerId=1]", xmlOutput);
		XMLAssert.assertXpathExists("/concept/conceptSets/conceptSet[conceptSetId=1]", xmlOutput);
		XMLAssert.assertXpathExists("/concept/descriptions/conceptDescription[conceptDescriptionId=9]", xmlOutput);
        logger.info(xmlOutput);
	}
	
	/**
	 * Construct a serialized xml string and make sure it can be deserialized correctly
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldDeserializeConcept() throws Exception {
		//construct given string to be deserialized
		StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<concept id=\"1\" uuid=\"0cbe2ed3-cd5f-4f46-9459-26127c9265ab\" retired=\"false\">\n");
        xmlBuilder.append("  <conceptId>3</conceptId>\n");
        xmlBuilder.append("  <datatype id=\"2\" uuid=\"8d4a4c94-c2cc-11de-8d13-0010c6dffd0f\" retired=\"false\">\n");
        xmlBuilder.append("    <name>N/A</name>\n");
        xmlBuilder.append("    <description>Not associated with a datatype (e.g., term answers, sets)</description>\n");
        xmlBuilder.append("    <creator id=\"3\" uuid=\"1010d442-e134-11de-babe-001e378eb67e\" retired=\"false\">\n");
        xmlBuilder.append("      <creator reference=\"3\"/>\n");
        xmlBuilder.append("      <dateCreated class=\"sql-timestamp\" id=\"4\">2005-01-01 00:00:00 CET</dateCreated>\n");
        xmlBuilder.append("      <changedBy reference=\"3\"/>\n");
        xmlBuilder.append("      <dateChanged class=\"sql-timestamp\" id=\"5\">2007-09-20 21:54:12 CEST</dateChanged>\n");
        xmlBuilder.append("      <retireReason></retireReason>\n");
        xmlBuilder.append("      <userId>1</userId>\n");
        xmlBuilder.append("      <person id=\"6\" uuid=\"6adb7c42-cfd2-4301-b53b-ff17c5654ff7\" voided=\"false\">\n");
        xmlBuilder.append("        <personId>1</personId>\n");
        xmlBuilder.append("        <addresses class=\"sorted-set\" id=\"7\"/>\n");
        xmlBuilder.append("        <names class=\"sorted-set\" id=\"8\"/>\n");
        xmlBuilder.append("        <attributes class=\"sorted-set\" id=\"9\"/>\n");
        xmlBuilder.append("        <gender></gender>\n");
        xmlBuilder.append("        <birthdate class=\"sql-timestamp\" id=\"10\">1975-06-30 00:00:00 CET</birthdate>\n");
        xmlBuilder.append("        <birthdateEstimated>false</birthdateEstimated>\n");
        xmlBuilder.append("        <dead>false</dead>\n");
        xmlBuilder.append("        <personCreator reference=\"3\"/>\n");
        xmlBuilder.append("        <personDateCreated class=\"sql-timestamp\" reference=\"4\"/>\n");
        xmlBuilder.append("        <personChangedBy reference=\"3\"/>\n");
        xmlBuilder.append("        <personDateChanged class=\"sql-timestamp\" reference=\"5\"/>\n");
        xmlBuilder.append("        <personVoided>false</personVoided>\n");
        xmlBuilder.append("        <personVoidReason></personVoidReason>\n");
        xmlBuilder.append("        <isPatient>false</isPatient>\n");
        xmlBuilder.append("      </person>\n");
        xmlBuilder.append("      <systemId>1-8</systemId>\n");
        xmlBuilder.append("      <username>admin</username>\n");
        xmlBuilder.append("      <secretQuestion></secretQuestion>\n");
        xmlBuilder.append("      <roles id=\"11\">\n");
        xmlBuilder.append("        <role id=\"12\" uuid=\"0e43640b-67d1-4458-b47f-b64fd8ce4b0d\" retired=\"false\">\n");
        xmlBuilder.append("          <description>Administrators of the OpenMRS .. have additional access to change fundamental structure of the database model.</description>\n");
        xmlBuilder.append("          <role>System Developer</role>\n");
        xmlBuilder.append("          <privileges id=\"13\"/>\n");
        xmlBuilder.append("          <inheritedRoles id=\"14\"/>\n");
        xmlBuilder.append("          <childRoles id=\"15\"/>\n");
        xmlBuilder.append("        </role>\n");
        xmlBuilder.append("      </roles>\n");
        xmlBuilder.append("      <userProperties id=\"16\"/>\n");
        xmlBuilder.append("      <parsedProficientLocalesProperty></parsedProficientLocalesProperty>\n");
        xmlBuilder.append("    </creator>\n");
        xmlBuilder.append("    <dateCreated class=\"sql-timestamp\" id=\"17\">2004-02-02 00:00:00 CET</dateCreated>\n");
        xmlBuilder.append("    <conceptDatatypeId>4</conceptDatatypeId>\n");
        xmlBuilder.append("    <hl7Abbreviation>ZZ</hl7Abbreviation>\n");
        xmlBuilder.append("  </datatype>\n");
        xmlBuilder.append("  <conceptClass id=\"18\" uuid=\"3d065ed4-b0b9-4710-9a17-6d8c4fd259b7\" retired=\"false\">\n");
        xmlBuilder.append("    <name>Drug</name>\n");
        xmlBuilder.append("    <description>Drug</description>\n");
        xmlBuilder.append("    <creator reference=\"3\"/>\n");
        xmlBuilder.append("    <dateCreated class=\"sql-timestamp\" reference=\"17\"/>\n");
        xmlBuilder.append("    <conceptClassId>3</conceptClassId>\n");
        xmlBuilder.append("  </conceptClass>\n");
        xmlBuilder.append("  <set>false</set>\n");
        xmlBuilder.append("  <version></version>\n");
        xmlBuilder.append("  <creator reference=\"3\"/>\n");
        xmlBuilder.append("  <dateCreated class=\"sql-timestamp\" id=\"19\">2008-08-15 15:27:51 CEST</dateCreated>\n");
        xmlBuilder.append("  <names class=\"set\" id=\"20\">\n");
        xmlBuilder.append("    <conceptName id=\"21\" uuid=\"b8159118-c97b-4d5a-a63e-d4aa4be0c4d3\" voided=\"false\">\n");
        xmlBuilder.append("      <conceptNameId>2456</conceptNameId>\n");
        xmlBuilder.append("      <concept reference=\"1\"/>\n");
        xmlBuilder.append("      <name>COUGH SYRUP</name>\n");
        xmlBuilder.append("      <locale id=\"22\">en</locale>\n");
        xmlBuilder.append("      <creator reference=\"3\"/>\n");
        xmlBuilder.append("      <dateCreated class=\"sql-timestamp\" reference=\"19\"/>\n");
        xmlBuilder.append("      <tags class=\"set\" id=\"23\"/>\n");
        xmlBuilder.append("      <conceptNameType>FULLY_SPECIFIED</conceptNameType>\n");
        xmlBuilder.append("      <localePreferred>true</localePreferred>\n");
        xmlBuilder.append("    </conceptName>\n");
        xmlBuilder.append("  </names>\n");
        xmlBuilder.append("  <answers class=\"set\" id=\"24\">\n");
        xmlBuilder.append("    <conceptAnswer id=\"25\" uuid=\"b1230431-2fe5-49fc-b535-ae42bc849747\">\n");
        xmlBuilder.append("      <conceptAnswerId>1</conceptAnswerId>\n");
        xmlBuilder.append("      <concept reference=\"1\"/>\n");
        xmlBuilder.append("      <answerConcept id=\"26\" uuid=\"b055abd8-a420-4a11-8b98-02ee170a7b54\" retired=\"false\">\n");
        xmlBuilder.append("        <conceptId>7</conceptId>\n");
        xmlBuilder.append("        <datatype reference=\"2\"/>\n");
        xmlBuilder.append("        <conceptClass id=\"27\" uuid=\"ecdee8a7-d741-4fe7-8e01-f79cacbe97bc\" retired=\"false\">\n");
        xmlBuilder.append("          <name>Misc</name>\n");
        xmlBuilder.append("          <description>Terms which don&apos;t fit other categories</description>\n");
        xmlBuilder.append("          <creator reference=\"3\"/>\n");
        xmlBuilder.append("          <dateCreated class=\"sql-timestamp\" id=\"28\">2004-03-02 00:00:00 CET</dateCreated>\n");
        xmlBuilder.append("          <conceptClassId>11</conceptClassId>\n");
        xmlBuilder.append("        </conceptClass>\n");
        xmlBuilder.append("        <set>false</set>\n");
        xmlBuilder.append("        <version></version>\n");
        xmlBuilder.append("        <creator reference=\"3\"/>\n");
        xmlBuilder.append("        <dateCreated class=\"sql-timestamp\" id=\"29\">2008-08-15 13:52:53 CEST</dateCreated>\n");
        xmlBuilder.append("        <names class=\"set\" id=\"30\"/>\n");
        xmlBuilder.append("        <answers class=\"set\" id=\"31\"/>\n");
        xmlBuilder.append("        <conceptSets class=\"set\" id=\"32\"/>\n");
        xmlBuilder.append("        <descriptions class=\"set\" id=\"33\">\n");
        xmlBuilder.append("          <conceptDescription id=\"34\" uuid=\"be3321b3-c1c7-4339-aaca-1b60db12e1df\">\n");
        xmlBuilder.append("            <conceptDescriptionId>3</conceptDescriptionId>\n");
        xmlBuilder.append("            <concept reference=\"26\"/>\n");
        xmlBuilder.append("            <description>Affirmative</description>\n");
        xmlBuilder.append("            <locale reference=\"22\"/>\n");
        xmlBuilder.append("            <creator reference=\"3\"/>\n");
        xmlBuilder.append("            <dateCreated class=\"sql-timestamp\" reference=\"29\"/>\n");
        xmlBuilder.append("          </conceptDescription>\n");
        xmlBuilder.append("        </descriptions>\n");
        xmlBuilder.append("        <conceptMappings class=\"set\" id=\"35\"/>\n");
        xmlBuilder.append("      </answerConcept>\n");
        xmlBuilder.append("      <creator reference=\"3\"/>\n");
        xmlBuilder.append("      <dateCreated class=\"sql-timestamp\" id=\"36\">2008-08-18 12:34:26 CEST</dateCreated>\n");
        xmlBuilder.append("    </conceptAnswer>\n");
        xmlBuilder.append("  </answers>\n");
        xmlBuilder.append("  <conceptSets class=\"set\" id=\"37\">\n");
        xmlBuilder.append("    <conceptSet id=\"38\" uuid=\"1a111827-639f-4cb4-961f-1e025bf88d90\">\n");
        xmlBuilder.append("      <conceptSetId>1</conceptSetId>\n");
        xmlBuilder.append("      <concept id=\"39\" uuid=\"0f97e14e-cdc2-49ac-9255-b5126f8a5147\" retired=\"false\">\n");
        xmlBuilder.append("        <conceptId>23</conceptId>\n");
        xmlBuilder.append("        <datatype reference=\"2\"/>\n");
        xmlBuilder.append("        <conceptClass id=\"40\" uuid=\"0248f513-d023-40b6-a274-235a33f6e25f\" retired=\"false\">\n");
        xmlBuilder.append("          <name>ConvSet</name>\n");
        xmlBuilder.append("          <description>Term to describe convenience sets</description>\n");
        xmlBuilder.append("          <creator reference=\"3\"/>\n");
        xmlBuilder.append("          <dateCreated class=\"sql-timestamp\" reference=\"28\"/>\n");
        xmlBuilder.append("          <conceptClassId>10</conceptClassId>\n");
        xmlBuilder.append("        </conceptClass>\n");
        xmlBuilder.append("        <set>true</set>\n");
        xmlBuilder.append("        <version></version>\n");
        xmlBuilder.append("        <creator reference=\"3\"/>\n");
        xmlBuilder.append("        <dateCreated class=\"sql-timestamp\" id=\"41\">2008-08-18 12:38:58 CEST</dateCreated>\n");
        xmlBuilder.append("        <names class=\"set\" id=\"42\"/>\n");
        xmlBuilder.append("        <answers class=\"set\" id=\"43\"/>\n");
        xmlBuilder.append("        <conceptSets class=\"set\" id=\"44\">\n");
        xmlBuilder.append("          <conceptSet id=\"45\" uuid=\"df26fc1a-6fc0-4867-8982-9e5dfe3f8265\">\n");
        xmlBuilder.append("            <conceptSetId>2</conceptSetId>\n");
        xmlBuilder.append("            <concept id=\"46\" uuid=\"96408258-000b-424e-af1a-403919332938\" retired=\"false\">\n");
        xmlBuilder.append("              <conceptId>19</conceptId>\n");
        xmlBuilder.append("              <datatype id=\"47\" uuid=\"8d4a4ab4-c2cc-11de-8d13-0010c6dffd0f\" retired=\"false\">\n");
        xmlBuilder.append("                <name>Text</name>\n");
        xmlBuilder.append("                <description>Free text</description>\n");
        xmlBuilder.append("                <creator reference=\"3\"/>\n");
        xmlBuilder.append("                <dateCreated class=\"sql-timestamp\" reference=\"17\"/>\n");
        xmlBuilder.append("                <conceptDatatypeId>3</conceptDatatypeId>\n");
        xmlBuilder.append("                <hl7Abbreviation>ST</hl7Abbreviation>\n");
        xmlBuilder.append("              </datatype>\n");
        xmlBuilder.append("              <conceptClass id=\"48\" uuid=\"a82ef63c-e4e4-48d6-988a-fdd74d7541a7\" retired=\"false\">\n");
        xmlBuilder.append("                <name>Question</name>\n");
        xmlBuilder.append("                <description>Question (eg, patient history, SF36 items)</description>\n");
        xmlBuilder.append("                <creator reference=\"3\"/>\n");
        xmlBuilder.append("                <dateCreated class=\"sql-timestamp\" reference=\"28\"/>\n");
        xmlBuilder.append("                <conceptClassId>7</conceptClassId>\n");
        xmlBuilder.append("              </conceptClass>\n");
        xmlBuilder.append("              <set>false</set>\n");
        xmlBuilder.append("              <version></version>\n");
        xmlBuilder.append("              <creator reference=\"3\"/>\n");
        xmlBuilder.append("              <dateCreated class=\"sql-timestamp\" id=\"49\">2008-08-18 12:32:35 CEST</dateCreated>\n");
        xmlBuilder.append("              <names class=\"set\" id=\"50\"/>\n");
        xmlBuilder.append("              <answers class=\"set\" id=\"51\"/>\n");
        xmlBuilder.append("              <conceptSets class=\"set\" id=\"52\"/>\n");
        xmlBuilder.append("              <descriptions class=\"set\" id=\"53\"/>\n");
        xmlBuilder.append("              <conceptMappings class=\"set\" id=\"54\"/>\n");
        xmlBuilder.append("            </concept>\n");
        xmlBuilder.append("            <conceptSet reference=\"39\"/>\n");
        xmlBuilder.append("            <sortWeight>2.0</sortWeight>\n");
        xmlBuilder.append("            <creator reference=\"3\"/>\n");
        xmlBuilder.append("            <dateCreated class=\"sql-timestamp\" reference=\"41\"/>\n");
        xmlBuilder.append("          </conceptSet>\n");
        xmlBuilder.append("          <conceptSet id=\"55\" uuid=\"fdefaa94-3f99-464c-8b1f-3e1d1c61335c\">\n");
        xmlBuilder.append("            <conceptSetId>3</conceptSetId>\n");
        xmlBuilder.append("            <concept id=\"56\" uuid=\"11716f9c-1434-4f8d-b9fc-9aa14c4d6126\" retired=\"false\">\n");
        xmlBuilder.append("              <conceptId>20</conceptId>\n");
        xmlBuilder.append("              <datatype id=\"57\" uuid=\"8d4a5af4-c2cc-11de-8d13-0010c6dffd0f\" retired=\"false\">\n");
        xmlBuilder.append("                <name>Datetime</name>\n");
        xmlBuilder.append("                <description>Absolute date and time</description>\n");
        xmlBuilder.append("                <creator reference=\"3\"/>\n");
        xmlBuilder.append("                <dateCreated class=\"sql-timestamp\" id=\"58\">2004-07-22 00:00:00 CEST</dateCreated>\n");
        xmlBuilder.append("                <conceptDatatypeId>8</conceptDatatypeId>\n");
        xmlBuilder.append("                <hl7Abbreviation>TS</hl7Abbreviation>\n");
        xmlBuilder.append("              </datatype>\n");
        xmlBuilder.append("              <conceptClass reference=\"48\"/>\n");
        xmlBuilder.append("              <set>false</set>\n");
        xmlBuilder.append("              <version></version>\n");
        xmlBuilder.append("              <creator reference=\"3\"/>\n");
        xmlBuilder.append("              <dateCreated class=\"sql-timestamp\" id=\"59\">2008-08-18 12:33:37 CEST</dateCreated>\n");
        xmlBuilder.append("              <names class=\"set\" id=\"60\"/>\n");
        xmlBuilder.append("              <answers class=\"set\" id=\"61\"/>\n");
        xmlBuilder.append("              <conceptSets class=\"set\" id=\"62\"/>\n");
        xmlBuilder.append("              <descriptions class=\"set\" id=\"63\">\n");
        xmlBuilder.append("                <conceptDescription id=\"64\" uuid=\"78441eab-42d0-408e-8577-ed369e56aa79\">\n");
        xmlBuilder.append("                  <conceptDescriptionId>13</conceptDescriptionId>\n");
        xmlBuilder.append("                  <concept reference=\"56\"/>\n");
        xmlBuilder.append("                  <description>When did the patient receive the food package?</description>\n");
        xmlBuilder.append("                  <locale reference=\"22\"/>\n");
        xmlBuilder.append("                  <creator reference=\"3\"/>\n");
        xmlBuilder.append("                  <dateCreated class=\"sql-timestamp\" reference=\"59\"/>\n");
        xmlBuilder.append("                </conceptDescription>\n");
        xmlBuilder.append("              </descriptions>\n");
        xmlBuilder.append("              <conceptMappings class=\"set\" id=\"65\"/>\n");
        xmlBuilder.append("            </concept>\n");
        xmlBuilder.append("            <conceptSet reference=\"39\"/>\n");
        xmlBuilder.append("            <sortWeight>1.0</sortWeight>\n");
        xmlBuilder.append("            <creator reference=\"3\"/>\n");
        xmlBuilder.append("            <dateCreated class=\"sql-timestamp\" reference=\"41\"/>\n");
        xmlBuilder.append("          </conceptSet>\n");
        xmlBuilder.append("        </conceptSets>\n");
        xmlBuilder.append("        <descriptions class=\"set\" id=\"66\">\n");
        xmlBuilder.append("          <conceptDescription id=\"67\" uuid=\"0a7e860b-d73c-4033-be1e-2053ee025c5b\">\n");
        xmlBuilder.append("            <conceptDescriptionId>14</conceptDescriptionId>\n");
        xmlBuilder.append("            <concept reference=\"39\"/>\n");
        xmlBuilder.append("            <description>Holder for all things edible</description>\n");
        xmlBuilder.append("            <locale reference=\"22\"/>\n");
        xmlBuilder.append("            <creator reference=\"3\"/>\n");
        xmlBuilder.append("            <dateCreated class=\"sql-timestamp\" reference=\"41\"/>\n");
        xmlBuilder.append("          </conceptDescription>\n");
        xmlBuilder.append("        </descriptions>\n");
        xmlBuilder.append("        <conceptMappings class=\"set\" id=\"68\"/>\n");
        xmlBuilder.append("      </concept>\n");
        xmlBuilder.append("      <conceptSet reference=\"1\"/>\n");
        xmlBuilder.append("      <sortWeight>0.0</sortWeight>\n");
        xmlBuilder.append("      <creator reference=\"3\"/>\n");
        xmlBuilder.append("      <dateCreated class=\"sql-timestamp\" reference=\"41\"/>\n");
        xmlBuilder.append("    </conceptSet>\n");
        xmlBuilder.append("  </conceptSets>\n");
        xmlBuilder.append("  <descriptions class=\"set\" id=\"69\">\n");
        xmlBuilder.append("    <conceptDescription id=\"70\" uuid=\"79a3efa7-3a43-4b38-ac5d-9b68aee086c6\">\n");
        xmlBuilder.append("      <conceptDescriptionId>9</conceptDescriptionId>\n");
        xmlBuilder.append("      <concept reference=\"1\"/>\n");
        xmlBuilder.append("      <description>This is used for coughs</description>\n");
        xmlBuilder.append("      <locale reference=\"22\"/>\n");
        xmlBuilder.append("      <creator reference=\"3\"/>\n");
        xmlBuilder.append("      <dateCreated class=\"sql-timestamp\" reference=\"19\"/>\n");
        xmlBuilder.append("    </conceptDescription>\n");
        xmlBuilder.append("  </descriptions>\n");
        xmlBuilder.append("  <conceptMappings class=\"set\" id=\"71\">\n");
        xmlBuilder.append("    <conceptMap id=\"72\" uuid=\"23b6e712-49d8-11e0-8fed-18a905e044dc\">\n");
        xmlBuilder.append("      <conceptMapType id=\"73\" uuid=\"35543629-7d8c-11e1-909d-c80aa9edcf4e\" retired=\"false\">\n");
        xmlBuilder.append("        <name>same-as</name>\n");
        xmlBuilder.append("        <description>Indicates similarity</description>\n");
        xmlBuilder.append("        <creator reference=\"3\"/>\n");
        xmlBuilder.append("        <dateCreated class=\"sql-timestamp\" id=\"74\">2004-08-12 00:00:00 CEST</dateCreated>\n");
        xmlBuilder.append("        <conceptMapTypeId>2</conceptMapTypeId>\n");
        xmlBuilder.append("        <isHidden>false</isHidden>\n");
        xmlBuilder.append("      </conceptMapType>\n");
        xmlBuilder.append("      <creator reference=\"3\"/>\n");
        xmlBuilder.append("      <dateCreated class=\"sql-timestamp\" reference=\"74\"/>\n");
        xmlBuilder.append("      <conceptMapId>1</conceptMapId>\n");
        xmlBuilder.append("      <concept reference=\"1\"/>\n");
        xmlBuilder.append("      <conceptReferenceTerm id=\"75\" uuid=\"SSTRM-WGT234\" retired=\"false\">\n");
        xmlBuilder.append("        <name>weight term</name>\n");
        xmlBuilder.append("        <description>A person&apos;s weight in kilograms</description>\n");
        xmlBuilder.append("        <creator reference=\"3\"/>\n");
        xmlBuilder.append("        <dateCreated class=\"sql-timestamp\" reference=\"74\"/>\n");
        xmlBuilder.append("        <conceptReferenceTermId>1</conceptReferenceTermId>\n");
        xmlBuilder.append("        <conceptSource id=\"76\" uuid=\"00001827-639f-4cb4-961f-1e025bf80000\" retired=\"false\">\n");
        xmlBuilder.append("          <name>Some Standardized Terminology</name>\n");
        xmlBuilder.append("          <description>A made up source</description>\n");
        xmlBuilder.append("          <creator reference=\"3\"/>\n");
        xmlBuilder.append("          <dateCreated class=\"sql-timestamp\" id=\"77\">2005-02-24 00:00:00 CET</dateCreated>\n");
        xmlBuilder.append("          <conceptSourceId>1</conceptSourceId>\n");
        xmlBuilder.append("          <hl7Code>SSTRM</hl7Code>\n");
        xmlBuilder.append("        </conceptSource>\n");
        xmlBuilder.append("        <code>WGT234</code>\n");
        xmlBuilder.append("        <conceptReferenceTermMaps id=\"78\">\n");
        xmlBuilder.append("          <conceptReferenceTermMap id=\"79\" uuid=\"dff198e4-562d-11e0-b169-18a905e044dc\">\n");
        xmlBuilder.append("            <conceptMapType id=\"80\" uuid=\"0e7a8536-49d6-11e0-8fed-18a905e044dc\" retired=\"false\">\n");
        xmlBuilder.append("              <name>is-parent-to</name>\n");
        xmlBuilder.append("              <creator reference=\"3\"/>\n");
        xmlBuilder.append("              <dateCreated class=\"sql-timestamp\" reference=\"74\"/>\n");
        xmlBuilder.append("              <conceptMapTypeId>4</conceptMapTypeId>\n");
        xmlBuilder.append("              <isHidden>false</isHidden>\n");
        xmlBuilder.append("            </conceptMapType>\n");
        xmlBuilder.append("            <creator reference=\"3\"/>\n");
        xmlBuilder.append("            <dateCreated class=\"sql-timestamp\" reference=\"74\"/>\n");
        xmlBuilder.append("            <conceptReferenceTermMapId>1</conceptReferenceTermMapId>\n");
        xmlBuilder.append("            <termA reference=\"75\"/>\n");
        xmlBuilder.append("            <termB id=\"81\" uuid=\"SSTRM-CD41003\" retired=\"false\">\n");
        xmlBuilder.append("              <name>cd4 term</name>\n");
        xmlBuilder.append("              <description>A person&apos;s CD4</description>\n");
        xmlBuilder.append("              <creator reference=\"3\"/>\n");
        xmlBuilder.append("              <dateCreated class=\"sql-timestamp\" reference=\"74\"/>\n");
        xmlBuilder.append("              <conceptReferenceTermId>2</conceptReferenceTermId>\n");
        xmlBuilder.append("              <conceptSource reference=\"76\"/>\n");
        xmlBuilder.append("              <code>CD41003</code>\n");
        xmlBuilder.append("              <conceptReferenceTermMaps id=\"82\">\n");
        xmlBuilder.append("                <conceptReferenceTermMap id=\"83\" uuid=\"756c5a64-d36d-11e0-a71a-00248140a5eb\">\n");
        xmlBuilder.append("                  <conceptMapType reference=\"73\"/>\n");
        xmlBuilder.append("                  <creator reference=\"3\"/>\n");
        xmlBuilder.append("                  <dateCreated class=\"sql-timestamp\" reference=\"74\"/>\n");
        xmlBuilder.append("                  <conceptReferenceTermMapId>3</conceptReferenceTermMapId>\n");
        xmlBuilder.append("                  <termA reference=\"81\"/>\n");
        xmlBuilder.append("                  <termB id=\"84\" uuid=\"SNOMED CT-7345693\" retired=\"false\">\n");
        xmlBuilder.append("                    <name>cd4 term2</name>\n");
        xmlBuilder.append("                    <description>A person&apos;s CD4</description>\n");
        xmlBuilder.append("                    <creator reference=\"3\"/>\n");
        xmlBuilder.append("                    <dateCreated class=\"sql-timestamp\" reference=\"74\"/>\n");
        xmlBuilder.append("                    <conceptReferenceTermId>4</conceptReferenceTermId>\n");
        xmlBuilder.append("                    <conceptSource id=\"85\" uuid=\"j3nfjk33-639f-4cb4-961f-1e025b908433\" retired=\"false\">\n");
        xmlBuilder.append("                      <name>SNOMED CT</name>\n");
        xmlBuilder.append("                      <description>&apos;Systematized Nomenclature of Medicine--Clinical Terms&apos; is a comprehensive clinical terminology</description>\n");
        xmlBuilder.append("                      <creator reference=\"3\"/>\n");
        xmlBuilder.append("                      <dateCreated class=\"sql-timestamp\" reference=\"77\"/>\n");
        xmlBuilder.append("                      <conceptSourceId>2</conceptSourceId>\n");
        xmlBuilder.append("                      <hl7Code>SNOMED-CT</hl7Code>\n");
        xmlBuilder.append("                    </conceptSource>\n");
        xmlBuilder.append("                    <code>7345693</code>\n");
        xmlBuilder.append("                    <conceptReferenceTermMaps id=\"86\"/>\n");
        xmlBuilder.append("                  </termB>\n");
        xmlBuilder.append("                </conceptReferenceTermMap>\n");
        xmlBuilder.append("              </conceptReferenceTermMaps>\n");
        xmlBuilder.append("            </termB>\n");
        xmlBuilder.append("          </conceptReferenceTermMap>\n");
        xmlBuilder.append("          <conceptReferenceTermMap id=\"87\" uuid=\"f7edaa46-562d-11e0-b169-18a905e044dc\">\n");
        xmlBuilder.append("            <conceptMapType reference=\"73\"/>\n");
        xmlBuilder.append("            <creator reference=\"3\"/>\n");
        xmlBuilder.append("            <dateCreated class=\"sql-timestamp\" reference=\"74\"/>\n");
        xmlBuilder.append("            <conceptReferenceTermMapId>2</conceptReferenceTermMapId>\n");
        xmlBuilder.append("            <termA reference=\"75\"/>\n");
        xmlBuilder.append("            <termB reference=\"84\"/>\n");
        xmlBuilder.append("          </conceptReferenceTermMap>\n");
        xmlBuilder.append("        </conceptReferenceTermMaps>\n");
        xmlBuilder.append("      </conceptReferenceTerm>\n");
        xmlBuilder.append("    </conceptMap>\n");
        xmlBuilder.append("  </conceptMappings>\n");
        xmlBuilder.append("</concept>\n");
		
		//deserialize and make sure everything has been put into object
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		
		Concept concept = Context.getSerializationService().deserialize(xmlBuilder.toString(), Concept.class, XStreamSerializer.class);
		assertEquals("0cbe2ed3-cd5f-4f46-9459-26127c9265ab", concept.getUuid());
		assertEquals(3, concept.getConceptId().intValue());
		assertFalse("The retired shouldn't be " + concept.isRetired(), concept.isRetired());
		assertEquals(4, concept.getDatatype().getConceptDatatypeId().intValue());
		assertEquals(3, concept.getConceptClass().getConceptClassId().intValue());
		assertEquals(1, concept.getCreator().getUserId().intValue());
		assertFalse("The set shouldn't be " + concept.getSet(), concept.getSet());
		assertEquals(sdf.parse("2008-08-15 7:27:51 CST"), concept.getDateCreated());
		assertEquals(1, concept.getNames().size());
		assertEquals(1, concept.getAnswers().size());
		assertEquals(1, concept.getDescriptions().size());
		assertEquals(1, concept.getConceptSets().size());
		assertEquals(1, concept.getConceptMappings().size());
	}
}
