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
package org.corpus_tools.peppermodules.ptbModules.tests;

import org.corpus_tools.pepper.common.CorpusDesc;
import org.corpus_tools.pepper.common.FormatDesc;
import org.corpus_tools.pepper.testFramework.PepperExporterTest;
import org.corpus_tools.peppermodules.PTBModules.PTBExporter;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.samples.SampleGenerator;
import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Test;

public class PTBExporterTest extends PepperExporterTest {

	@Before
	public void setUp() {
		setFixture(new PTBExporter());
		getFixture().setSaltProject(SaltFactory.createSaltProject());
		// set formats to support
		FormatDesc formatDef = new FormatDesc();
		formatDef.setFormatName("PTB");
		formatDef.setFormatVersion("1.0");
		this.supportedFormatsCheck.add(formatDef);
	}

	@Test
	public void testExportPureSyntax() {
		// SampleGenerator.createCorpusStructure(getFixture().getSaltProject());
		getFixture().getSaltProject().addCorpusGraph(SaltFactory.createSCorpusGraph());
		SDocument docMyDocument = getFixture().getSaltProject().getCorpusGraphs().get(0).createDocument(URI.createURI("mycorpus/doc1"));
		docMyDocument.setDocumentGraph(SaltFactory.createSDocumentGraph());
		SampleGenerator.createPrimaryData(docMyDocument);
		SampleGenerator.createTokens(docMyDocument);
		SampleGenerator.createSyntaxStructure(docMyDocument);
		SampleGenerator.createSyntaxAnnotations(docMyDocument);
		CorpusDesc corpusDesc = new CorpusDesc();
		corpusDesc.setCorpusPath(URI.createURI(getTempPath("ptbTest").toString()));
		getFixture().setCorpusDesc(corpusDesc);
		this.start();
	}

}
