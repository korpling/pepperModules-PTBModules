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
package de.hu_berlin.german.korpling.saltnpepper.pepperModules.ptbModules.tests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SStructure;
import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Test;

import de.hu_berlin.german.korpling.saltnpepper.pepper.testFramework.PepperModuleTest;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.PTBModules.PTBImporterMapper;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.PTBModules.PTBImporterProperties;

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
		getFixture().setDocument(SaltFactory.createSDocument());
		getFixture().setProperties(myProps);
	}
	
	/**
	 * Test for the standard PTB dialect with relation labels, 
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
		//salt2dot.salt2Dot(getFixture().getDocument().getDocumentGraph(), URI.createFileURI("D:/dot_out.dot"));
		
		//Check number of texts
		assertEquals(1, getFixture().getDocument().getDocumentGraph().getTextualDSs().size());
		//Check text is identical
		assertEquals("In American romance , almost nothing rates *-2 higher than what the movie men have called *T*-1 `` * meeting cute '' ", getFixture().getDocument().getDocumentGraph().getTextualDSs().get(0).getText());
		//Count number of tokens
		assertEquals(22, getFixture().getDocument().getDocumentGraph().getTokens().size());
		//Count number of nodes
		assertEquals(23, getFixture().getDocument().getDocumentGraph().getStructures().size());
		//Check first token is annotated as IN
		assertEquals("IN", getFixture().getDocument().getDocumentGraph().getTokens().get(0).getAnnotation("ptb::pos").getValue());
	}
	
	
	/**
	 * Test for atis style slash dialect with no relation labels, 
	 * node labels and terminals and POS separated by slash and not 
	 * surrounded by brackets
	 * @throws IOException 
	 */
	@Test
	public void testSlashDialect() throws IOException {
		getFixture().setResourceURI(URI.createFileURI("./src/test/resources/atis3_mini.mrg"));
		
		getFixture().mapSDocument();
		
		//Check number of texts
		assertEquals(1, getFixture().getDocument().getDocumentGraph().getTextualDSs().size());
		//Check text length is identical
		assertEquals(139, getFixture().getDocument().getDocumentGraph().getTextualDSs().get(0).getText().length());
		//Count number of tokens
		assertEquals(17, getFixture().getDocument().getDocumentGraph().getTokens().size());
		//Count number of nodes
		assertEquals(16, getFixture().getDocument().getDocumentGraph().getStructures().size());
		//Check first token is annotated as IN
		assertEquals("CD", getFixture().getDocument().getDocumentGraph().getTokens().get(0).getAnnotation("ptb::pos").getValue());
	}
	
	/**
	 * Test for the standard PTB dialect with no relation labels, one sentence per line, 
	 * node labels and POS at left bracket and terminals at right bracket
	 * @throws IOException 
	 */
	@Test
	public void testOneLineDialect() throws IOException {
		getFixture().setResourceURI(URI.createFileURI("./src/test/resources/Feigenblatt.ptb"));
		
		getFixture().mapSDocument();
		
		//Check number of texts
		assertEquals(1, getFixture().getDocument().getDocumentGraph().getTextualDSs().size());
		//Check text length is identical
		assertEquals(1238, getFixture().getDocument().getDocumentGraph().getTextualDSs().get(0).getText().length());
		//Count number of tokens
		assertEquals(212, getFixture().getDocument().getDocumentGraph().getTokens().size());
		//Count number of nodes
		assertEquals(287, getFixture().getDocument().getDocumentGraph().getStructures().size());
		//Check first token is annotated as IN
		assertEquals("VVPP", getFixture().getDocument().getDocumentGraph().getTokens().get(0).getAnnotation("ptb::pos").getValue());
	}
	/**
	 * Checks that even output from Stanford parser is parsable. Stanford produces partly entire sentences in one line.
	 * @throws IOException 
	 */
	@Test
	public void test_stanford_output() throws IOException{
//		String input= "# D:\\stanford-tregex-2013-06-20\\mag-editorial\\c_m_ed_bjr_001.txt.prd_705_705_18_694:\n"+
//			"328: (NP (PRP$ their) (NN development))\n"+
//			"\n"+
//			"# D:\\stanford-tregex-2013-06-20\\mag-editorial\\c_m_ed_bjr_002.txt.prd_574_577_16_566:\n"+
//			"619: (NP\n"+
//			"  (NP (NNP China) (POS 's))\n"+
//			"  (ADJP (JJ sustainable))\n"+
//			"  (NN development))\n";
		
		String input= "# D:\\stanford-tregex-2013-06-20\\mag-editorial\\c_m_ed_bjr_001.txt.prd_705_705_18_694:\n"+
				"(NP (PRP$ their) (NN development))\n"+
				"\n"+
				"# D:\\stanford-tregex-2013-06-20\\mag-editorial\\c_m_ed_bjr_002.txt.prd_574_577_16_566:\n"+
				"(NP\n"+
				"  (NP (NNP China) (POS 's))\n"+
				"  (ADJP (JJ sustainable))\n"+
				"  (NN development))\n";
		
		File tmpFolder= PepperModuleTest.getTempPath_static("ptbImporter");
		File flTemp = new File(tmpFolder.getAbsolutePath() + "stanfordOutput.txt");
		FileWriter flwTemp = new FileWriter(flTemp);
		try{
			flwTemp.write(input);
			flwTemp.flush();
		}finally{
			flwTemp.close();
		}
		getFixture().setResourceURI(URI.createFileURI(flTemp.getAbsolutePath()));
		
		getFixture().mapSDocument();
		
		//Check number of texts
		assertEquals(1, getFixture().getDocument().getDocumentGraph().getTextualDSs().size());
		//Check text is identical
		assertEquals("their development China 's sustainable development ", getFixture().getDocument().getDocumentGraph().getTextualDSs().get(0).getText());
		//Count number of tokens
		assertEquals(6, getFixture().getDocument().getDocumentGraph().getTokens().size());
		//Count number of nodes
		assertEquals(4, getFixture().getDocument().getDocumentGraph().getStructures().size());
		//Check first token is annotated as IN
		assertEquals("PRP$", getFixture().getDocument().getDocumentGraph().getTokens().get(0).getAnnotation("ptb::pos").getValue());
		assertEquals("NN", getFixture().getDocument().getDocumentGraph().getTokens().get(1).getAnnotation("ptb::pos").getValue());
		assertEquals("NNP", getFixture().getDocument().getDocumentGraph().getTokens().get(2).getAnnotation("ptb::pos").getValue());
		assertEquals("POS", getFixture().getDocument().getDocumentGraph().getTokens().get(3).getAnnotation("ptb::pos").getValue());
		assertEquals("JJ", getFixture().getDocument().getDocumentGraph().getTokens().get(4).getAnnotation("ptb::pos").getValue());
		assertEquals("NN", getFixture().getDocument().getDocumentGraph().getTokens().get(5).getAnnotation("ptb::pos").getValue());
		

	}

}
