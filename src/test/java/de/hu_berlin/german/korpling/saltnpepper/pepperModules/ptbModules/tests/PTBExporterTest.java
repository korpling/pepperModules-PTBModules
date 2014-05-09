package de.hu_berlin.german.korpling.saltnpepper.pepperModules.ptbModules.tests;

import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Test;

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.CorpusDesc;
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
		System.out.println(corpusDesc.getCorpusPath());
		this.start();
		
	}

}
