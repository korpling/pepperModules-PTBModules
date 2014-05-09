/**
 * Copyright 2009 Humboldt University of Berlin, INRIA.
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
package de.hu_berlin.german.korpling.saltnpepper.pepperModules.ptbModules.tests;

import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Test;

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.CorpusDesc;
import de.hu_berlin.german.korpling.saltnpepper.pepper.common.FormatDesc;
import de.hu_berlin.german.korpling.saltnpepper.pepper.testFramework.PepperExporterTest;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.PTBModules.PTBExporter;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltSample.SaltSample;

public class PTBExporterTest extends PepperExporterTest {

	@Before
	public void setUp(){		
		setFixture(new PTBExporter());
		getFixture().setSaltProject(SaltFactory.eINSTANCE.createSaltProject());
		//set formats to support
		FormatDesc formatDef= new FormatDesc();
		formatDef.setFormatName("PTB");
		formatDef.setFormatVersion("1.0");
		this.supportedFormatsCheck.add(formatDef);
	}
	
	@Test
	public void testExportPureSyntax() {
		//SaltSample.createCorpusStructure(getFixture().getSaltProject());
		getFixture().getSaltProject().getSCorpusGraphs().add(SaltFactory.eINSTANCE.createSCorpusGraph());
		SDocument docMyDocument = getFixture().getSaltProject().getSCorpusGraphs().get(0).createSDocument(URI.createURI("mycorpus/doc1"));
		docMyDocument.setSDocumentGraph(SaltFactory.eINSTANCE.createSDocumentGraph());
		SaltSample.createPrimaryData(docMyDocument);
		SaltSample.createTokens2(docMyDocument);
		SaltSample.createSyntaxStructure2(docMyDocument);
		SaltSample.createSyntaxAnnotations2(docMyDocument);
		CorpusDesc corpusDesc= new CorpusDesc();
		corpusDesc.setCorpusPath(URI.createURI(getTempPath("ptbTest").toString()));
		getFixture().setCorpusDesc(corpusDesc);
		this.start();
	}

}
