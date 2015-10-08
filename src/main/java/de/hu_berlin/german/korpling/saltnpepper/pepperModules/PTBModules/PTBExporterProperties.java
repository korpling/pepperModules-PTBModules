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

import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperties;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperty;

public class PTBExporterProperties extends PepperModuleProperties {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1197293758770984614L;
	public static final String PREFIX = "ptb.Exporter.";
	/**
	 * Name of namespace for nodes to be exported and their annotations, e.g.
	 * 'ptb'.
	 */
	public static final String PROP_NODENAMESPACE = PREFIX + "nodeNamespace";
	/**
	 * Name of pos annotation name for tokens, e.g. 'pos'.
	 */
	public static final String PROP_POSNAME = PREFIX + "posName";
	/**
	 * Name of category annotation for non-terminal nodes, e.g. 'cat'
	 */
	public static final String PROP_CATNAME = PREFIX + "catName";
	/**
	 * Name of relation type for dominance relations, e.g. 'relation'.
	 */
	public static final String PROP_EDGETYPE = PREFIX + "relationType";
	/**
	 * Separator character for relation labels following node annotation, e.g.
	 * the '-' in (NP-subj (....
	 */
	public static final String PROP_EDGEANNOSEPARATOR = PREFIX + "relationAnnoSeparator";
	/**
	 * Namespace for relation annotations (represented within a node label after
	 * a separator), e.g. 'ptb'.
	 */
	public static final String PROP_EDGEANNONAMESPACE = PREFIX + "relationAnnoNamespace";
	/**
	 * Name of PTB dominance relation annotation name, e.g. 'func'.
	 */
	public static final String PROP_EDGEANNONAME = PREFIX + "relationAnnoName";
	/**
	 * Boolean, whether to output relation annotations after a separator.
	 */
	public static final String PROP_EXPORTEDGEANNOS = PREFIX + "importRelationAnnos";
	/**
	 * Boolean, whether to create Penn atis-style tokens, which are non
	 * bracketed and separate the pos tag with a slash, e.g.: (NP two/CD
	 * friends/NNS ).
	 */
	public static final String PROP_HANDLESLASHTOKENS = PREFIX + "handleSlashTokens";

	public PTBExporterProperties() {
		this.addProperty(new PepperModuleProperty<String>(PROP_EDGETYPE, String.class, "Name of relation type for dominance relations to be exported, e.g. 'relation'.", "relation", false));
		this.addProperty(new PepperModuleProperty<Boolean>(PROP_EXPORTEDGEANNOS, Boolean.class, "Boolean, whether to output relation annotations after a separator.", true, false));
		this.addProperty(new PepperModuleProperty<String>(PROP_CATNAME, String.class, "Name of category annotation for non-terminal nodes to be exported, e.g. 'cat'", "cat", false));
		this.addProperty(new PepperModuleProperty<String>(PROP_EDGEANNONAME, String.class, "Name of dominance relation annotation name to be exported, e.g. 'func'.", "func", false));
		this.addProperty(new PepperModuleProperty<String>(PROP_EDGEANNONAMESPACE, String.class, "Namespace for relation annotations to be exported (represented within a node label after a separator), e.g. 'ptb'.", "ptb", false));
		this.addProperty(new PepperModuleProperty<String>(PROP_EDGEANNOSEPARATOR, String.class, "Separator character for relation labels following node annotation, e.g. the '-' in (NP-subj (....", "-", false));
		this.addProperty(new PepperModuleProperty<Boolean>(PROP_HANDLESLASHTOKENS, Boolean.class, "Boolean, whether to create Penn atis-style tokens, which are non bracketed and separate the pos tag with a slash, e.g.: (NP two/CD friends/NNS ).", false, false));
		this.addProperty(new PepperModuleProperty<String>(PROP_NODENAMESPACE, String.class, "Name of namespace for nodes to export and their annotations, e.g. 'ptb'.", "ptb", false));
		this.addProperty(new PepperModuleProperty<String>(PROP_POSNAME, String.class, "Name of pos annotation name for tokens, e.g. 'pos'.", "pos", false));

	}

	public synchronized String getCatName() {
		return ((String) this.getProperty(PROP_CATNAME).getValue());
	}

	public synchronized String getPosName() {
		return ((String) this.getProperty(PROP_POSNAME).getValue());
	}

	public synchronized String getRelationType() {
		return ((String) this.getProperty(PROP_EDGETYPE).getValue());
	}

	public synchronized String getRelationAnnoName() {
		return ((String) this.getProperty(PROP_EDGEANNONAME).getValue());
	}

	public synchronized String getRelationAnnoNamespace() {
		return ((String) this.getProperty(PROP_EDGEANNONAMESPACE).getValue());
	}

	public synchronized String getRelationAnnoSeparator() {
		return ((String) this.getProperty(PROP_EDGEANNOSEPARATOR).getValue());
	}

	public synchronized String getNodeNamespace() {
		return ((String) this.getProperty(PROP_NODENAMESPACE).getValue());
	}

	public synchronized Boolean getImportRelationAnnos() {
		return ((Boolean) this.getProperty(PROP_EXPORTEDGEANNOS).getValue());
	}

	public synchronized Boolean getHandleSlashTokens() {
		return ((Boolean) this.getProperty(PROP_HANDLESLASHTOKENS).getValue());
	}

}
