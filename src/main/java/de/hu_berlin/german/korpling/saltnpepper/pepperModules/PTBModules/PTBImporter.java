/**
 * Copyright 2009 Humboldt-Universität zu Berlin, INRIA.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */
package de.hu_berlin.german.korpling.saltnpepper.pepperModules.PTBModules;

import org.eclipse.emf.common.util.URI;
import org.osgi.service.component.annotations.Component;

import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperImporter;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperMapper;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.impl.PepperImporterImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpus;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SElementId;

/**
 * This importer transforms data in Penn Trebank format (ptb). to a Salt model
 * 
 * @author Amir Zeldes
 * @version 1.0
 * 
 */
@Component(name = "PTBImporterComponent", factory = "PepperImporterComponentFactory")
public class PTBImporter extends PepperImporterImpl implements PepperImporter {

	// =================================================== mandatory
	// ===================================================
	/**
	 * <strong>OVERRIDE THIS METHOD FOR CUSTOMIZATION</strong>
	 * 
	 * A constructor for your module. Set the coordinates, with which your
	 * module shall be registered. The coordinates (modules name, version and
	 * supported formats) are a kind of a fingerprint, which should make your
	 * module unique.
	 */
	public PTBImporter() {
		super();
		this.setName("PTBImporter");
		setSupplierContact(URI.createURI("saltnpepper@lists.hu-berlin.de"));
		setSupplierHomepage(URI.createURI("https://github.com/korpling/pepperModules-PTBModules"));
		setDesc("This importer transforms data in Penn Trebank format (ptb). to a Salt model");
		this.addSupportedFormat("PTB", "1.0", null);
		this.getSDocumentEndings().add("ptb");
		this.getSDocumentEndings().add("txt");
		this.getSDocumentEndings().add("mrg");
		this.setProperties(new PTBImporterProperties());
	}

	/**
	 * <strong>OVERRIDE THIS METHOD FOR CUSTOMIZATION</strong>
	 * 
	 * This method creates a customized {@link PepperMapper} object and returns
	 * it. You can here do some additional initialisations. Things like setting
	 * the {@link SElementId} of the {@link SDocument} or {@link SCorpus} object
	 * and the {@link URI} resource is done by the framework (or more in detail
	 * in method {@link #start()}). The parameter <code>sElementId</code>, if a
	 * {@link PepperMapper} object should be created in case of the object to
	 * map is either an {@link SDocument} object or an {@link SCorpus} object of
	 * the mapper should be initialized differently. <br/>
	 * 
	 * @param sElementId
	 *            {@link SElementId} of the {@link SCorpus} or {@link SDocument}
	 *            to be processed.
	 * @return {@link PepperMapper} object to do the mapping task for object
	 *         connected to given {@link SElementId}
	 */
	public PepperMapper createPepperMapper(SElementId sElementId) {
		PepperMapper ptbmap = new PTBImporterMapper();
		return (ptbmap);
	}
}
