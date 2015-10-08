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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.corpus_tools.salt.common.SDominanceRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.GraphTraverseHandler;
import org.corpus_tools.salt.core.SGraph.GRAPH_TRAVERSE_TYPE;
import org.corpus_tools.salt.core.SLayer;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SRelation;

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.DOCUMENT_STATUS;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.exceptions.PepperModuleException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.impl.PepperMapperImpl;

public class PTBExporterMapper extends PepperMapperImpl implements GraphTraverseHandler {

	public StringBuilder stbOutput = new StringBuilder();

	// manage settings
	private String strNamespace;
	private String strPosName;
	private String strCatName;
	private String strRelationAnnoSeparator;
	private String strRelationAnnoName;
	private Boolean bolRelationAnnos;
	private Boolean bolHandleSlashTokens;

	/**
	 * Called by the Pepper framework to start the mapping for one
	 * document-structure.
	 */
	@Override
	public DOCUMENT_STATUS mapSDocument() {

		if (getDocument() != null && getDocument().getDocumentGraph() != null) {
			// initializes setting variables (see above)
			getSettings();
			// traverses the document-structure (this is a call back and will
			// invoke #checkConstraint, #nodeReached and #nodeLeft())
			getDocument().getDocumentGraph().traverse(getDocument().getDocumentGraph().getRoots(), GRAPH_TRAVERSE_TYPE.TOP_DOWN_DEPTH_FIRST, "TraverseTrees", this);

			File outputFile = null;
			if (getResourceURI().toFileString() != null) {
				outputFile = new File(getResourceURI().toFileString());
			} else {
				outputFile = new File(getResourceURI().toString());
			}

			if ((!outputFile.isDirectory()) && (!outputFile.getParentFile().exists())) {
				outputFile.getParentFile().mkdirs();
			}
			FileWriter flwTemp = null;
			try {
				flwTemp = new FileWriter(outputFile);
				flwTemp.write(stbOutput.toString());
				flwTemp.flush();
			} catch (IOException e) {
				throw new PepperModuleException(this, "Unable to write output file for PTB export '" + getResourceURI() + "'.", e);
			} finally {
				try {
					if (flwTemp != null) {
						flwTemp.close();
					}
				} catch (IOException e) {
					throw new PepperModuleException(this, "Unable to close output file writer for PTB export '" + getResourceURI() + "'.", e);
				}
			}

		}

		return (DOCUMENT_STATUS.COMPLETED);

	}

	/**
	 * Check constraints: only process this subtree if the current node is a
	 * root or if it has an incoming {@link SDominanceRelation} If the user has
	 * defined the {@link PTBExporterProperties#PROP_NODENAMESPACE}, also check
	 * that the node is in a layer corresponding to the defined namespace value.
	 */
	@Override
	public boolean checkConstraint(GRAPH_TRAVERSE_TYPE trvTraverseType, String strTraverseName, SRelation relCurrentRelation, SNode nodCurrentNode, long lngChildNumber) {

		// Stores matching user defined namespace as soon as a corresponding
		// layer is found
		boolean bolFoundNamespace = false;

		if (relCurrentRelation == null || relCurrentRelation instanceof SDominanceRelation)
		// if this node is a root or it has an incoming dominance relationship
		{
			if (strNamespace != null && !strNamespace.isEmpty()) {
				for (SLayer sLayer : nodCurrentNode.getLayers()) {
					// iterate through layers of this node to find user defined
					// namespace setting
					if (sLayer.getName().equals(strNamespace)) {
						bolFoundNamespace = true; // found matching namespace,
													// done searching layers
						break;
					} else {
						bolFoundNamespace = false;
					}
				}
			} else {
				return true;
			}
		} else {
			return false;
		}

		return bolFoundNamespace;

	}

	@Override
	public void nodeLeft(GRAPH_TRAVERSE_TYPE trvTraverseType, String strTraverseName, SNode nodCurrentNode, SRelation relCurrentRelation, SNode nodPrevNode, long lngChildNumber) {
		// leaving a non-terminal, close the bracket
		if (!(nodCurrentNode instanceof SToken)) {
			stbOutput.append(") ");
		}
		// leaving a root, sentence is complete
		if (relCurrentRelation == null) {
			stbOutput.append("\n");
		}
	}

	@Override
	public void nodeReached(GRAPH_TRAVERSE_TYPE trvTraverseType, String strTraverseName, SNode nodCurrentNode, SRelation relCurrentRelation, SNode nodPrevNode, long lngChildNumber) {

		String strAnnoOut;
		String strTokenOut;
		// this is a token
		if (nodCurrentNode instanceof SToken) {
			strTokenOut = ((SToken) nodCurrentNode).getGraph().getText(nodCurrentNode);
			// there is a pos tag
			if (nodCurrentNode.getAnnotation(strPosName) != null) {
				strAnnoOut = nodCurrentNode.getAnnotation(strPosName).getValue_STEXT();

				if (bolHandleSlashTokens == false) {
					// normal style pos notation
					stbOutput.append(" (" + strAnnoOut + " " + strTokenOut + ") ");
				} else {
					// atis style pos notation with slash
					stbOutput.append(" " + strTokenOut + "/" + strAnnoOut + " ");
				}
			} else {
				stbOutput.append("( " + strTokenOut + " )");
			}
		} else if (nodCurrentNode.getAnnotation(strCatName) != null) {
			// This node has a cat annotation
			strAnnoOut = nodCurrentNode.getAnnotation(strCatName).getValue_STEXT();
			if (bolRelationAnnos == true) {
				// output relation annotations if available
				if (relCurrentRelation.getAnnotation(strRelationAnnoName) != null) {
					// relation annotation found
					strAnnoOut += strRelationAnnoSeparator + relCurrentRelation.getAnnotation(strRelationAnnoName).getValue_STEXT();
				}
			}
			stbOutput.append(" (" + strAnnoOut);
		}
	}

	/**
	 * Reads the propery object and maps its contend to several variables.
	 */
	private void getSettings() {
		strNamespace = ((PTBExporterProperties) this.getProperties()).getNodeNamespace();
		strPosName = ((PTBExporterProperties) this.getProperties()).getPosName();
		strCatName = ((PTBExporterProperties) this.getProperties()).getCatName();
		strRelationAnnoSeparator = ((PTBExporterProperties) this.getProperties()).getRelationAnnoSeparator();
		strRelationAnnoName = ((PTBExporterProperties) this.getProperties()).getRelationAnnoName();
		bolRelationAnnos = ((PTBExporterProperties) this.getProperties()).getImportRelationAnnos();
		bolHandleSlashTokens = ((PTBExporterProperties) this.getProperties()).getHandleSlashTokens();
	}
}