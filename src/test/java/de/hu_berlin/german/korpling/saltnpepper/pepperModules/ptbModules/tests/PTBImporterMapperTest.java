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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Test;

import de.hu_berlin.german.korpling.saltnpepper.pepper.testFramework.PepperModuleTest;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.PTBModules.PTBImporterMapper;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.PTBModules.PTBImporterProperties;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;

public class PTBImporterMapperTest{

	private PTBImporterMapper fixture= null;
	
	public PTBImporterMapper getFixture() {
		return fixture;
	}

	public void setFixture(PTBImporterMapper fixture) {
		this.fixture = fixture;
	}

	@Before
	public void setUp(){		
		setFixture(new PTBImporterMapper());
		PTBImporterProperties myProps = new PTBImporterProperties();
		getFixture().setSDocument(SaltFactory.eINSTANCE.createSDocument());
		getFixture().setProperties(myProps);
	}
	
	/**
	 * Test for the standard PTB dialect with edge labels, 
	 * node labels and POS at left bracket and terminals at right bracket
	 * @throws IOException 
	 */
	@Test
	public void testStandardDialect() throws IOException {
		String strStandardDialectExample = "( (S \n" +
		"    (S" + 
		"      (PP (IN In) \n" +
		"        (NP (JJ American) (NN romance) ))\n" +
		"      (, ,) \n" +
		"      (NP-SBJ-2 (RB almost) (NN nothing) )\n" +
		"      (VP (VBZ rates) \n" +
		"        (S \n" +
		"          (NP-SBJ (-NONE- *-2) )\n" +
		"          (ADJP-PRD \n" +
		"            (ADJP (JJR higher) )\n" +
		"            (PP (IN than) \n" +
		"              (SBAR-NOM \n" +
		"                (WHNP-1 (WP what) )\n" +
		"                (S \n" +
		"                  (NP-SBJ (DT the) (NN movie) (NNS men) )\n" +
		"                  (VP (VB have) \n" +
		"                    (VP (VBN called) \n" +
		"                      (S \n" +
		"                        (NP-SBJ (-NONE- *T*-1) )\n" +
		"                        (`` ``) \n" +
		"                        (S-NOM-PRD \n" +
		"                          (NP-SBJ (-NONE- *) )\n" +
		"                          (VP (NN meeting) \n" +
		"                            (NP (JJ cute) )))\n" +
		"                        ('' '') ))))))))))))\n";
		
		File tmpFolder= PepperModuleTest.getTempPath_static("ptbImporter");
		File flTemp = new File(tmpFolder.getAbsolutePath() + "testStandardDialect.txt");
		FileWriter flwTemp = new FileWriter(flTemp);
		try{
			flwTemp.write(strStandardDialectExample);
			flwTemp.flush();
		}finally{
			flwTemp.close();
		}
		getFixture().setResourceURI(URI.createFileURI(flTemp.getAbsolutePath()));
		
		getFixture().mapSDocument();
		//Salt2DOT salt2dot= new Salt2DOT();
		//salt2dot.salt2Dot(getFixture().getSDocument().getSDocumentGraph(), URI.createFileURI("D:/dot_out.dot"));
		
		//Check number of texts
		assertEquals(1, getFixture().getSDocument().getSDocumentGraph().getSTextualDSs().size());
		//Check text is identical
		assertEquals("In American romance , almost nothing rates *-2 higher than what the movie men have called *T*-1 `` * meeting cute '' ", getFixture().getSDocument().getSDocumentGraph().getSTextualDSs().get(0).getSText());
		//Count number of tokens
		assertEquals(22, getFixture().getSDocument().getSDocumentGraph().getSTokens().size());
		//Count number of nodes
		assertEquals(23, getFixture().getSDocument().getSDocumentGraph().getSStructures().size());
		//Check first token is annotated as IN
		assertEquals("IN", getFixture().getSDocument().getSDocumentGraph().getSTokens().get(0).getSAnnotation("ptb::pos").getSValue());
	}
	
	
	/**
	 * Test for atis style slash dialect with no edge labels, 
	 * node labels and terminals and POS separated by slash and not 
	 * surrounded by brackets
	 * @throws IOException 
	 */
	@Test
	public void testSlashDialect() throws IOException {
		getFixture().setResourceURI(URI.createFileURI("./src/test/resources/atis3_mini.mrg"));
		
		getFixture().mapSDocument();
		//System.out.println(getFixture().getSDocument().getSDocumentGraph().getSTextualDSs().get(0).getSText());
		
		//Check number of texts
		assertEquals(1, getFixture().getSDocument().getSDocumentGraph().getSTextualDSs().size());
		//Check text length is identical
		assertEquals(139, getFixture().getSDocument().getSDocumentGraph().getSTextualDSs().get(0).getSText().length());
		//Count number of tokens
		assertEquals(17, getFixture().getSDocument().getSDocumentGraph().getSTokens().size());
		//Count number of nodes
		assertEquals(16, getFixture().getSDocument().getSDocumentGraph().getSStructures().size());
		//Check first token is annotated as IN
		assertEquals("CD", getFixture().getSDocument().getSDocumentGraph().getSTokens().get(0).getSAnnotation("ptb::pos").getSValue());
	}
	
	/**
	 * Test for the standard PTB dialect with no edge labels, one sentence per line, 
	 * node labels and POS at left bracket and terminals at right bracket
	 * @throws IOException 
	 */
	@Test
	public void testOneLineDialect() throws IOException {
		getFixture().setResourceURI(URI.createFileURI("./src/test/resources/Feigenblatt.ptb"));
		
		getFixture().mapSDocument();
		
		//Check number of texts
		assertEquals(1, getFixture().getSDocument().getSDocumentGraph().getSTextualDSs().size());
		//Check text length is identical
		assertEquals(1238, getFixture().getSDocument().getSDocumentGraph().getSTextualDSs().get(0).getSText().length());
		//Count number of tokens
		assertEquals(212, getFixture().getSDocument().getSDocumentGraph().getSTokens().size());
		//Count number of nodes
		assertEquals(287, getFixture().getSDocument().getSDocumentGraph().getSStructures().size());
		//Check first token is annotated as IN
		assertEquals("VVPP", getFixture().getSDocument().getSDocumentGraph().getSTokens().get(0).getSAnnotation("ptb::pos").getSValue());
	}
	/**
	 * Checks that even output from Stanford parser is parsable. Stanford produces partly entire sentences in one line.
	 * @throws IOException 
	 */
//	@Test
//	public void test_stanford_output() throws IOException{
//		String input= "# D:\\stanford-tregex-2013-06-20\\mag-editorial\\c_m_ed_bjr_001.txt.prd_705_705_18_694:\n"+
//			"328: (NP (PRP$ their) (NN development))\n"+
//			"\n"+
//			"# D:\\stanford-tregex-2013-06-20\\mag-editorial\\c_m_ed_bjr_002.txt.prd_574_577_16_566:\n"+
//			"619: (NP\n"+
//			"  (NP (NNP China) (POS 's))\n"+
//			"  (ADJP (JJ sustainable))\n"+
//			"  (NN development))\n";
//		
//		File tmpFolder= PepperModuleTest.getTempPath_static("ptbImporter");
//		File flTemp = new File(tmpFolder.getAbsolutePath() + "testStandardDialect.txt");
//		FileWriter flwTemp = new FileWriter(flTemp);
//		try{
//			flwTemp.write(input);
//			flwTemp.flush();
//		}finally{
//			flwTemp.close();
//		}
//		getFixture().setResourceURI(URI.createFileURI(flTemp.getAbsolutePath()));
//		
//		getFixture().mapSDocument();
//		//Salt2DOT salt2dot= new Salt2DOT();
//		//salt2dot.salt2Dot(getFixture().getSDocument().getSDocumentGraph(), URI.createFileURI("D:/dot_out.dot"));
//		
//		//Check number of texts
//		assertEquals(1, getFixture().getSDocument().getSDocumentGraph().getSTextualDSs().size());
//		//Check text is identical
//		assertEquals("In American romance , almost nothing rates *-2 higher than what the movie men have called *T*-1 `` * meeting cute '' ", getFixture().getSDocument().getSDocumentGraph().getSTextualDSs().get(0).getSText());
//		//Count number of tokens
//		assertEquals(22, getFixture().getSDocument().getSDocumentGraph().getSTokens().size());
//		//Count number of nodes
//		assertEquals(23, getFixture().getSDocument().getSDocumentGraph().getSStructures().size());
//		//Check first token is annotated as IN
//		assertEquals("IN", getFixture().getSDocument().getSDocumentGraph().getSTokens().get(0).getSAnnotation("ptb::pos").getSValue());
//
//	}

}
