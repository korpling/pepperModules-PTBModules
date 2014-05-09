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

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.FormatDesc;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.impl.PepperImporterImpl;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.PTBModules.PTBExporter;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.PTBModules.PTBImporter;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.PTBModules.PTBImporterProperties;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.PTBModules.PTBImporterMapper;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.pepper.testFramework.PepperImporterTest;

public class PTBImporterTest extends PepperImporterTest{

	@Before
	public void setUp(){		
		setFixture(new PTBImporter());
		getFixture().setSaltProject(SaltFactory.eINSTANCE.createSaltProject());
		//set formats to support
		FormatDesc formatDef= new FormatDesc();
		formatDef.setFormatName("PTB");
		formatDef.setFormatVersion("1.0");
		this.supportedFormatsCheck.add(formatDef);
	}
	
	/**
	 * Test for the standard PTB dialect with edge labels, 
	 * node labels and POS at left bracket and terminals at right bracket
	 * @throws IOException 
	 */
	@Test
	public void testStandardDialect() throws IOException {
		
		PTBImporterProperties myProps = new PTBImporterProperties();
		
		PTBImporterMapper myMapper = new PTBImporterMapper(); 
		myMapper.setSDocument(SaltFactory.eINSTANCE.createSDocument());
		
		
		myMapper.setProperties(myProps);
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
		
		String strTemp = System.getProperty("java.io.tmpdir");
		if (!strTemp.endsWith("/")){
			strTemp= strTemp+ "/";
		}
		File flTemp = new File(strTemp + "testStandardDialect.txt");
		FileWriter flwTemp = new FileWriter(flTemp);
		try{
			flwTemp.write(strStandardDialectExample);
			flwTemp.flush();
		}finally{
			flwTemp.close();
		}
		myMapper.setResourceURI(URI.createFileURI(flTemp.getAbsolutePath()));
		
		myMapper.mapSDocument();
		//Salt2DOT salt2dot= new Salt2DOT();
		//salt2dot.salt2Dot(myMapper.getSDocument().getSDocumentGraph(), URI.createFileURI("D:/dot_out.dot"));
		
		//Check number of texts
		assertEquals(1, myMapper.getSDocument().getSDocumentGraph().getSTextualDSs().size());
		//Check text is identical
		assertEquals("In American romance , almost nothing rates *-2 higher than what the movie men have called *T*-1 `` * meeting cute '' ", myMapper.getSDocument().getSDocumentGraph().getSTextualDSs().get(0).getSText());
		//Count number of tokens
		assertEquals(22, myMapper.getSDocument().getSDocumentGraph().getSTokens().size());
		//Count number of nodes
		assertEquals(23, myMapper.getSDocument().getSDocumentGraph().getSStructures().size());
		//Check first token is annotated as IN
		assertEquals("IN", myMapper.getSDocument().getSDocumentGraph().getSTokens().get(0).getSAnnotation("ptb::pos").getSValue());


		//fail("Not yet implemented");
	}
	
	
	/**
	 * Test for atis style slash dialect with no edge labels, 
	 * node labels and terminals and POS separated by slash and not 
	 * surrounded by brackets
	 * @throws IOException 
	 */
	@Test
	public void testSlashDialect() throws IOException {
		
		PTBImporterProperties myProps = new PTBImporterProperties();
		
		PTBImporterMapper myMapper = new PTBImporterMapper(); 
		myMapper.setSDocument(SaltFactory.eINSTANCE.createSDocument());
		
		
		myMapper.setProperties(myProps);
		
		
		myMapper.setResourceURI(URI.createFileURI("./src/test/resources/atis3_mini.mrg"));
		
		myMapper.mapSDocument();
		//System.out.println(myMapper.getSDocument().getSDocumentGraph().getSTextualDSs().get(0).getSText());
		
		//Check number of texts
		assertEquals(1, myMapper.getSDocument().getSDocumentGraph().getSTextualDSs().size());
		//Check text length is identical
		assertEquals(139, myMapper.getSDocument().getSDocumentGraph().getSTextualDSs().get(0).getSText().length());
		//Count number of tokens
		assertEquals(17, myMapper.getSDocument().getSDocumentGraph().getSTokens().size());
		//Count number of nodes
		assertEquals(16, myMapper.getSDocument().getSDocumentGraph().getSStructures().size());
		//Check first token is annotated as IN
		assertEquals("CD", myMapper.getSDocument().getSDocumentGraph().getSTokens().get(0).getSAnnotation("ptb::pos").getSValue());


		//fail("Not yet implemented");
	}
	
	/**
	 * Test for the standard PTB dialect with no edge labels, one sentence per line, 
	 * node labels and POS at left bracket and terminals at right bracket
	 * @throws IOException 
	 */
	@Test
	public void testOneLineDialect() throws IOException {
		
		PTBImporterProperties myProps = new PTBImporterProperties();
		
		PTBImporterMapper myMapper = new PTBImporterMapper(); 
		myMapper.setSDocument(SaltFactory.eINSTANCE.createSDocument());
		
		
		myMapper.setProperties(myProps);
		
		
		myMapper.setResourceURI(URI.createFileURI("./src/test/resources/Feigenblatt.ptb"));
		
		myMapper.mapSDocument();
		//System.out.println(myMapper.getSDocument().getSDocumentGraph().getSTextualDSs().get(0).getSText().length());
		
		//Check number of texts
		assertEquals(1, myMapper.getSDocument().getSDocumentGraph().getSTextualDSs().size());
		//Check text length is identical
		assertEquals(1238, myMapper.getSDocument().getSDocumentGraph().getSTextualDSs().get(0).getSText().length());
		//Count number of tokens
		assertEquals(212, myMapper.getSDocument().getSDocumentGraph().getSTokens().size());
		//Count number of nodes
		assertEquals(287, myMapper.getSDocument().getSDocumentGraph().getSStructures().size());
		//Check first token is annotated as IN
		assertEquals("VVPP", myMapper.getSDocument().getSDocumentGraph().getSTokens().get(0).getSAnnotation("ptb::pos").getSValue());


		//fail("Not yet implemented");
	}

}
