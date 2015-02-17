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

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.DOCUMENT_STATUS;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.exceptions.PepperModuleException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.impl.PepperMapperImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.GRAPH_TRAVERSE_TYPE;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDominanceRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SGraphTraverseHandler;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SLayer;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SRelation;

public class PTBExporterMapper extends PepperMapperImpl implements SGraphTraverseHandler {

	public StringBuilder stbOutput = new StringBuilder();

	// manage settings
	private String strNamespace;
	private String strPosName;
	private String strCatName;
	private String strEdgeAnnoSeparator;
	private String strEdgeAnnoName;
	private Boolean bolEdgeAnnos;
	private Boolean bolHandleSlashTokens;

	/**
	 * Called by the Pepper framework to start the mapping for one
	 * document-structure.
	 */
	@Override
	public DOCUMENT_STATUS mapSDocument() {

		if (getSDocument() != null && getSDocument().getSDocumentGraph() != null) {
			// initializes setting variables (see above)
			getSettings();
			// traverses the document-structure (this is a call back and will
			// invoke #checkConstraint, #nodeReached and #nodeLeft())
			getSDocument().getSDocumentGraph().traverse(getSDocument().getSDocumentGraph().getSRoots(), GRAPH_TRAVERSE_TYPE.TOP_DOWN_DEPTH_FIRST, "TraverseTrees", this);

			File outputFile = new File(getResourceURI().toFileString());
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
				for (SLayer sLayer : nodCurrentNode.getSLayers()) {
					// iterate through layers of this node to find user defined
					// namespace setting
					if (sLayer.getSName().equals(strNamespace)) {
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
			strTokenOut = ((SToken) nodCurrentNode).getSDocumentGraph().getSText(nodCurrentNode);
			// there is a pos tag
			if (nodCurrentNode.getSAnnotation(strPosName) != null) {
				strAnnoOut = nodCurrentNode.getSAnnotation(strPosName).getSValueSTEXT();

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
		} else if (nodCurrentNode.getSAnnotation(strCatName) != null) {
			// This node has a cat annotation
			strAnnoOut = nodCurrentNode.getSAnnotation(strCatName).getSValueSTEXT();
			if (bolEdgeAnnos == true) {
				// output edge annotations if available
				if (relCurrentRelation.getSAnnotation(strEdgeAnnoName) != null) {
					// edge annotation found
					strAnnoOut += strEdgeAnnoSeparator + relCurrentRelation.getSAnnotation(strEdgeAnnoName).getSValueSTEXT();
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
		strEdgeAnnoSeparator = ((PTBExporterProperties) this.getProperties()).getEdgeAnnoSeparator();
		strEdgeAnnoName = ((PTBExporterProperties) this.getProperties()).getEdgeAnnoName();
		bolEdgeAnnos = ((PTBExporterProperties) this.getProperties()).getImportEdgeAnnos();
		bolHandleSlashTokens = ((PTBExporterProperties) this.getProperties()).getHandleSlashTokens();
	}
}