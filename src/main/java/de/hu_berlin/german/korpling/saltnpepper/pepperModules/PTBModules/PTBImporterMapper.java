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
package de.hu_berlin.german.korpling.saltnpepper.pepperModules.PTBModules;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;
import java.util.Vector;

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.DOCUMENT_STATUS;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.exceptions.PepperModuleException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.impl.PepperMapperImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDominanceRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SLayer;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class PTBImporterMapper extends PepperMapperImpl {
	// manage settings
	private String strNamespace;
	private String strPosName;
	private String strCatName;
	private String strEdgeType;
	private String strEdgeAnnoSeparator;
	private String strEdgeAnnoNameSpace;
	private String strEdgeAnnoName;
	private Boolean bolEdgeAnnos;
	private Boolean bolHandleSlashTokens;

	// Declare Salt Objects
	private StringBuilder stbText = new StringBuilder();
	private STextualDS txtText = null;
	private SLayer lyrPTB = null;

	// Other globals
	private Stack<Vector<SNode>> stNodeVectors = new Stack<Vector<SNode>>();
	/** holds the current pos tag **/
	private String strPos = "";
	/** holds the current token **/
	private String strTok = "";
	/** holds the current node (i.e. cat annotation value) **/
	private String strNode = "";
	private String strSplitCompoundAnno[] = new String[2];
	private String strTempAnno;
	private SAnnotation anoTemp = null;
	private int intTokStartChar;
	private int intTokEndChar;
	/** Node List - to be dominated by next node **/
	private Vector<SNode> vecNodeList = new Vector<SNode>();

	private int intCountClosingBrackets = 0;

	@Override
	protected void initialize() {
		// do some initilizations
	}

	/**
	 * Called by the Pepper framework to map the corpus-structure.
	 */
	@Override
	public DOCUMENT_STATUS mapSCorpus() {
		// returns the resource in case of module is an importer or exporter
		getResourceURI();
		// returns the SDocument object to be manipulated
		getSDocument();
		// returns that process was successful
		return (DOCUMENT_STATUS.COMPLETED);
	}

	/**
	 * Called by the Pepper framework to start the mapping for one
	 * document-structure.
	 */
	@Override
	public DOCUMENT_STATUS mapSDocument() {
		getSettings();
		if (getSDocument().getSDocumentGraph() == null) {
			getSDocument().setSDocumentGraph(SaltFactory.eINSTANCE.createSDocumentGraph());
		}
		
		//create SLayer and add it to SDocumentGraph
		lyrPTB= SaltFactory.eINSTANCE.createSLayer();
		lyrPTB.setSName(strNamespace);
		getSDocument().getSDocumentGraph().addSLayer(lyrPTB);
		
		txtText = getSDocument().getSDocumentGraph().createSTextualDS("");

		BufferedReader br = null;
		try {
			br = new BufferedReader( new InputStreamReader(new FileInputStream(getResourceURI().toFileString()), "UTF8"));
			String line;
			String strValidate = br.readLine();
			if (strValidate == null) {
				throw new PepperModuleException("Cannot find text file to process - Input is null");
			}
			// every line should start with a '(', otherwise ignore it
			while (!(strValidate.trim().startsWith("("))) {
				strValidate = br.readLine();
			}

			line = strValidate;
			line.trim();

			while (strValidate != null) {
				if (CountInString(line, "(") == CountInString(line, ")")) {
					// sentence is complete
					// line = line.replaceAll("([^ ])\\(", "'$1' (");
					if (bolHandleSlashTokens) {
						// line =
						// line.replaceAll(" ([^ /]+)/([^ ]+) "," ('$1' '$2') ");
						line = line.replaceAll("(?<= )([^ \\(\\)]+)/([^ \\(\\)]+)(?= )", "($2 $1)");

					}
					mapSentence(line);
					strValidate = br.readLine();
					if (strValidate == null) {
						break;
					}
					// every line should start with a '(', otherwise ignore it
					while (!(strValidate.trim().startsWith("("))) {
						strValidate = br.readLine();
						if (strValidate == null) {
							break;
						}
					}
					if (!(strValidate == null)) {
						line = strValidate.trim();
					}
				} else {
					// sentence not yet complete, keep adding lines until equal
					// amount of "(" and ")"
					strValidate = br.readLine();
					if (strValidate == null) {
						break;
						// document may have ended in an unfinished sentence!
					}
					// every line should start with a '(', otherwise ignore it
					while (!(strValidate.trim().startsWith("("))) {
						strValidate = br.readLine();
						if (strValidate == null) {
							break;
						}
					}

					if (!(strValidate == null)) {
						line += " " + strValidate.trim();
					}
				}
			}
		} catch (FileNotFoundException e) {
			throw new PepperModuleException("The file " + getResourceURI().toFileString() + " was not found", e);
		} catch (IOException e) {
			throw new PepperModuleException("Cannot read the input file " + getResourceURI().toFileString(), e);
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				throw new PepperModuleException("Cannot close the stream for input file " + getResourceURI().toFileString(), e);
			}
		}
		txtText.setSText(stbText.toString());
		return (DOCUMENT_STATUS.COMPLETED);
	}

	/**
	 * The main logic to parse the ptb format. Here the given
	 * <code>strSentence</code> containing just one sentence in PTB format is
	 * parsed and mapped to Salt. The result is contained in
	 * {@link #vecNodeList}.
	 * 
	 * @param strSentence
	 *            sentence in PTB format
	 */
	public void mapSentence(String strSentence) {

		if (strSentence.startsWith("( (")) {
			// remove superfluous wrapping brackets around sentence if found
			strSentence = strSentence.substring(2, strSentence.length() - 2);
		}
		SNode nodCurrentNode = null;
		String[] strAllNodes = strSentence.split(" \\(");
		for (String strSingleNode : strAllNodes) {
			strSingleNode = strSingleNode.trim();
			if (strSingleNode.startsWith("(")) {
				strSingleNode = strSingleNode.substring(1);
			}
			if (!")".equals(strSingleNode) && !" ".equals(strSingleNode) && !strSingleNode.isEmpty()) {
				if (strSingleNode.length() > strSingleNode.replace(" ", "").length()) {
					// this node is a token
					// this is the pos tag
					strPos = strSingleNode.substring(0, strSingleNode.indexOf(' '));
					strTok = strSingleNode.substring(strSingleNode.indexOf(' ')).trim();
					strTok = strTok.replace(")", "");
					strTok = strTok.trim();

					// put strTok + " " into STextualDS
					intTokStartChar = stbText.toString().length();
					stbText.append(strTok);
					stbText.append(" ");
					intTokEndChar = stbText.toString().length() - 1;

					// create SToken node covering the strTok position
					SToken tokCurrentToken = getSDocument().getSDocumentGraph().createSToken(txtText, intTokStartChar, intTokEndChar);
		
					// annotate the SToken with pos=strPos
					tokCurrentToken.createSAnnotation(strNamespace, strPosName, strPos);
					nodCurrentNode = tokCurrentToken;

				} else {
					// non-terminal node
					strNode = strSingleNode.replace("(", "");

					// create new SNode
					nodCurrentNode = SaltFactory.eINSTANCE.createSStructure();
					nodCurrentNode.getSLayers().add(lyrPTB);
					getSDocument().getSDocumentGraph().addSNode(nodCurrentNode);

					// annotate the new SNode with cat=strNode
					nodCurrentNode.createSAnnotation(strNamespace, strCatName, strNode.trim());
				}

				// put current node into a list of things to be dominated once
				// we pop
				if (vecNodeList.isEmpty()) {
					vecNodeList = new Vector<SNode>();
					vecNodeList.add(nodCurrentNode);
					stNodeVectors.push(vecNodeList);
					vecNodeList = new Vector<SNode>();
				} else {
					vecNodeList = stNodeVectors.pop();
					vecNodeList.add(nodCurrentNode);
					stNodeVectors.push(vecNodeList);
				}

				intCountClosingBrackets = CountInString(strSingleNode, ")");
				for (int i = 0; i < intCountClosingBrackets; i++) {

					if (!(stNodeVectors.isEmpty())) {
						vecNodeList = stNodeVectors.pop();
						// if root has been reached, do nothing
						if (!stNodeVectors.isEmpty()) {
							nodCurrentNode = stNodeVectors.peek().lastElement();

							for (SNode nodChild : vecNodeList) {
								if (!(nodCurrentNode instanceof SStructure)) {
									// do not attempt to create a dominance edge
									// if the parent is a token
								} else {
									SDominanceRelation domCurrentDom = SaltFactory.eINSTANCE.createSDominanceRelation();
									domCurrentDom.setSSource((SStructure) nodCurrentNode);
									domCurrentDom.setSTarget(nodChild);
									domCurrentDom.addSType(strEdgeType);
									domCurrentDom.getLayers().add(lyrPTB);

									getSDocument().getSDocumentGraph().addSRelation(domCurrentDom);

									// add edge annotations if desired and
									// present
									if (bolEdgeAnnos) {

										anoTemp = nodChild.getSAnnotations().get(0);
										strTempAnno = anoTemp.getSValueSTEXT().toString();
										if (strTempAnno.contains(strEdgeAnnoSeparator)) {
											if (!(strTempAnno.startsWith(strEdgeAnnoSeparator)) && !(strTempAnno.indexOf(strEdgeAnnoSeparator) == strTempAnno.length())) {
												strSplitCompoundAnno[0] = strTempAnno.substring(0, strTempAnno.indexOf(strEdgeAnnoSeparator));
												strSplitCompoundAnno[1] = strTempAnno.substring(strTempAnno.indexOf(strEdgeAnnoSeparator) + 1);
												anoTemp.setSValue(strSplitCompoundAnno[0]);
												domCurrentDom.createSAnnotation(strEdgeAnnoNameSpace, strEdgeAnnoName, strSplitCompoundAnno[1]);
											}
										}
									}
								}
							}
						}
						// empty the node list
						vecNodeList.removeAllElements();
					}

				}
			}
			// clean up global lists for next sentence
			vecNodeList.removeAllElements();
		}
	}

	/**
	 * ???
	 * 
	 * @param strInput
	 * @param strToCount
	 * @return
	 */
	public int CountInString(String strInput, String strToCount) {
		return strInput.length() - strInput.replace(strToCount, "").length();
	}

	/**
	 * Reads the propery object and maps its contend to several variables.
	 */
	private void getSettings() {
		strNamespace = ((PTBImporterProperties) this.getProperties()).getNodeNamespace();
		strPosName = ((PTBImporterProperties) this.getProperties()).getPosName();
		strCatName = ((PTBImporterProperties) this.getProperties()).getCatName();
		strEdgeType = ((PTBImporterProperties) this.getProperties()).getEdgeType();
		strEdgeAnnoSeparator = ((PTBImporterProperties) this.getProperties()).getEdgeAnnoSeparator();
		strEdgeAnnoNameSpace = ((PTBImporterProperties) this.getProperties()).getEdgeAnnoNamespace();
		strEdgeAnnoName = ((PTBImporterProperties) this.getProperties()).getEdgeAnnoName();
		bolEdgeAnnos = ((PTBImporterProperties) this.getProperties()).getImportEdgeAnnos();
		bolHandleSlashTokens = ((PTBImporterProperties) this.getProperties()).getHandleSlashTokens();
	}

}
