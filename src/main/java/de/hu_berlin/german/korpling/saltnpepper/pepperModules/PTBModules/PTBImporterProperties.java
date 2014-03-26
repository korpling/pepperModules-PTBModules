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

import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperties;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperty;

public class PTBImporterProperties extends PepperModuleProperties {

	
public static final String PREFIX="ptb.importer.";
	

	/**
	 * Name of namespace for PTB nodes and their annotations, e.g. 'ptb'.
	 */
	public static final String PROP_NODENAMESPACE=PREFIX+"nodeNamespace";
	/**
	 * Name of pos annotation name for PTB tokens, e.g. 'pos'.
	 */
	public static final String PROP_POSNAME=PREFIX+"posName";
	/**
	 * Name of category annotation for PTB non-terminal nodes, e.g. 'cat'
	 */
	public static final String PROP_CATNAME=PREFIX+"catName";
	/**
	 * Name of edge type for PTB dominance edges, e.g. 'edge'.
	 */
	public static final String PROP_EDGETYPE=PREFIX+"edgeType";
	/**
	 * Separator character for edge labels following node annotation, e.g. the '-' in (NP-subj (.... 
	 */
	public static final String PROP_EDGEANNOSEPARATOR=PREFIX+"edgeAnnoSeparator";
	/**
	 * Namespace for PTB edge annotations (represented within a node label after a separator), e.g. 'ptb'.
	 */
	public static final String PROP_EDGEANNONAMESPACE=PREFIX+"edgeAnnoNamespace";
	/**
	 * Name of PTB dominance edge annotation name, e.g. 'func'.
	 */
	public static final String PROP_EDGEANNONAME=PREFIX+"edgeAnnoName";
	/**
	 * Boolean, whether to look for edge annotations after a separator.
	 */
	public static final String PROP_IMPORTEDGEANNOS=PREFIX+"importEdgeAnnos";
	/**
	 * Boolean, whether to handle Penn atis-style tokens, which are non bracketed and separate the pos tag with a slash, e.g.: (NP two/CD friends/NNS ).
	 */
	public static final String PROP_HANDLESLASHTOKENS=PREFIX+"handleSlashTokens";

	
	public PTBImporterProperties()
	{
		this.addProperty(new PepperModuleProperty<String>(PROP_CATNAME, String.class, "Name of category annotation for PTB non-terminal nodes, e.g. 'cat'", "cat",false));
		this.addProperty(new PepperModuleProperty<String>(PROP_EDGEANNONAME, String.class, "Name of PTB dominance edge annotation name, e.g. 'func'.","func", false));
		this.addProperty(new PepperModuleProperty<String>(PROP_EDGEANNONAMESPACE, String.class, "Namespace for PTB edge annotations (represented within a node label after a separator), e.g. 'ptb'.", "ptb" ,false));
		this.addProperty(new PepperModuleProperty<String>(PROP_EDGEANNOSEPARATOR, String.class, "Separator character for edge labels following node annotation, e.g. the '-' in (NP-subj (....","-", false));
		this.addProperty(new PepperModuleProperty<String>(PROP_EDGETYPE, String.class, "Name of edge type for PTB dominance edges, e.g. 'edge'.","edge", false));
		this.addProperty(new PepperModuleProperty<Boolean>(PROP_IMPORTEDGEANNOS, Boolean.class, "Boolean, whether to look for edge annotations after a separator.", true, false));
		this.addProperty(new PepperModuleProperty<Boolean>(PROP_HANDLESLASHTOKENS, Boolean.class, "Boolean, whether to handle Penn atis-style tokens, which are non bracketed and separate the pos tag with a slash, e.g.: (NP two/CD friends/NNS ).", true, false));
		this.addProperty(new PepperModuleProperty<String>(PROP_NODENAMESPACE, String.class, "Name of namespace for PTB nodes and their annotations, e.g. 'ptb'.","ptb", false));
		this.addProperty(new PepperModuleProperty<String>(PROP_POSNAME, String.class, "Name of pos annotation name for PTB tokens, e.g. 'pos'.", "pos", false));
	}
	
	public synchronized String getCatName()
	{
		return((String)this.getProperty(PROP_CATNAME).getValue());
	}
	public synchronized String getPosName()
	{
		return((String)this.getProperty(PROP_POSNAME).getValue());
	}
	public synchronized String getEdgeType()
	{
		return((String)this.getProperty(PROP_EDGETYPE).getValue());
	}
	public synchronized String getEdgeAnnoName()
	{
		return((String)this.getProperty(PROP_EDGEANNONAME).getValue());
	}
	public synchronized String getEdgeAnnoNamespace()
	{
		return((String)this.getProperty(PROP_EDGEANNONAMESPACE).getValue());
	}
	public synchronized String getEdgeAnnoSeparator()
	{
		return((String)this.getProperty(PROP_EDGEANNOSEPARATOR).getValue());
	}
	public synchronized String getNodeNamespace()
	{
		return((String)this.getProperty(PROP_NODENAMESPACE).getValue());
	}
	public synchronized Boolean getImportEdgeAnnos()
	{
		return((Boolean)this.getProperty(PROP_IMPORTEDGEANNOS).getValue());
	}
	public synchronized Boolean getHandleSlashTokens()
	{
		return((Boolean)this.getProperty(PROP_HANDLESLASHTOKENS).getValue());
	}

	
}
