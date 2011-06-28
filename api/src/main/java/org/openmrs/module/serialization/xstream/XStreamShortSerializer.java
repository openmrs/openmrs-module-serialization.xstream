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
package org.openmrs.module.serialization.xstream;

import org.openmrs.api.SerializationService;
import org.openmrs.module.serialization.xstream.converter.ConceptShortConverter;
import org.openmrs.module.serialization.xstream.converter.EncounterTypeShortConverter;
import org.openmrs.module.serialization.xstream.converter.FieldTypeShortConverter;
import org.openmrs.module.serialization.xstream.converter.FormShortConverter;
import org.openmrs.module.serialization.xstream.converter.LocationShortConverter;
import org.openmrs.module.serialization.xstream.converter.OrderTypeShortConverter;
import org.openmrs.module.serialization.xstream.converter.PatientIdentifierTypeShortConverter;
import org.openmrs.module.serialization.xstream.converter.PatientShortConverter;
import org.openmrs.module.serialization.xstream.converter.PersonAttributeTypeShortConverter;
import org.openmrs.module.serialization.xstream.converter.PersonShortConverter;
import org.openmrs.module.serialization.xstream.converter.ProgramShortConverter;
import org.openmrs.module.serialization.xstream.converter.ProgramWorkflowShortConverter;
import org.openmrs.module.serialization.xstream.converter.ProgramWorkflowStateShortConverter;
import org.openmrs.module.serialization.xstream.converter.RelationshipTypeShortConverter;
import org.openmrs.module.serialization.xstream.converter.UserShortConverter;
import org.openmrs.serialization.SerializationException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * Provides short serialization using XStream. <br/>
 * <br/>
 * It is recommended that you use the {@link SerializationService} to get the current short
 * serializer. Example about how to call serializer
 * 
 * <pre>
 *   Person person = Context.getPersonService().getPerson(123);
 *   String xml = Context.getSerializationService().serialize(user, XStreamShortSerializer.class);
 * </pre>
 * 
 * A small example about what does the xml output of short serialization express. <br/>
 * This is xml output after shortly serializing {@link org.openmrs.Concept}.
 * 
 * <pre>
 * 	    <conceptDescription id="1" uuid="79a3efa7-3a43-4b38-ac5d-9b68aee086c6">
 * 			<conceptDescriptionId>9</conceptDescriptionId>
 * 			<concept id="2" uuid="0cbe2ed3-cd5f-4f46-9459-26127c9265ab"/>
 * 			<description>This is used for coughs</description>
 * 			<locale id="3">en</locale>
 * 			<creator id="4" uuid="6adb7c42-cfd2-4301-b53b-ff17c5654ff7"/>
 * 			<dateCreated class="sql-timestamp" id="5">2008-08-15 15:27:51 CST</dateCreated>
 *		</conceptDescription>	
 * </pre>
 */
public class XStreamShortSerializer extends XStreamSerializer {
	
	/**
	 * Default Constructor
	 * 
	 * @throws SerializationException
	 */
	public XStreamShortSerializer() throws SerializationException {
		this(null);
	}
	
	public XStreamShortSerializer(XStream customXstream) throws SerializationException {
		super(customXstream);

		//all base config information for serialization inherited from XStreamSerializer
		Mapper mapper = xstream.getMapper();
		ConverterLookup conerterLookup = xstream.getConverterLookup();
		//config short converters for those classes which need short serialization
		xstream.registerConverter(new PersonShortConverter(mapper, conerterLookup));
		//here use the very high priority, so that this converter can replace UserConverter in long serializer
		xstream.registerConverter(new UserShortConverter(mapper, conerterLookup), xstream.PRIORITY_VERY_HIGH);
		xstream.registerConverter(new PatientShortConverter(mapper, conerterLookup));
		xstream.registerConverter(new ConceptShortConverter(mapper, conerterLookup));
		xstream.registerConverter(new EncounterTypeShortConverter(mapper, conerterLookup));
		xstream.registerConverter(new FieldTypeShortConverter(mapper, conerterLookup));
		xstream.registerConverter(new FormShortConverter(mapper, conerterLookup));
		xstream.registerConverter(new LocationShortConverter(mapper, conerterLookup));
		xstream.registerConverter(new OrderTypeShortConverter(mapper, conerterLookup));
		xstream.registerConverter(new PatientIdentifierTypeShortConverter(mapper, conerterLookup));
		xstream.registerConverter(new PersonAttributeTypeShortConverter(mapper, conerterLookup));
		xstream.registerConverter(new RelationshipTypeShortConverter(mapper, conerterLookup));
		xstream.registerConverter(new ProgramShortConverter(mapper, conerterLookup));
		xstream.registerConverter(new ProgramWorkflowShortConverter(mapper, conerterLookup));
		xstream.registerConverter(new ProgramWorkflowStateShortConverter(mapper, conerterLookup));
	}
}
