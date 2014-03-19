package de.hu_berlin.german.korpling.saltnpepper.pepperModules.PTBModules;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;
import java.util.Vector;

import org.eclipse.emf.common.util.URI;

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.DOCUMENT_STATUS;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.exceptions.PepperModuleException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.impl.PepperMapperImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.resources.dot.Salt2DOT;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDominanceRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SLayer;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;

public class PTBMapper extends PepperMapperImpl {
	

	
	//manage settings
	private String strNamespace ; 
	private String strPosName ;
	private String strCatName ; 
	private String strEdgeType; 
	private String strEdgeAnnoSeparator; 
	private String strEdgeAnnoNameSpace; 
	private String strEdgeAnnoName; 
	private Boolean bolEdgeAnnos; 
	private Boolean bolHandleSlashTokens; 
	
	
	//Declare Salt Objects
	private StringBuilder stbText = new StringBuilder();
	private STextualDS txtText = null;
	private SLayer lyrPTB = SaltFactory.eINSTANCE.createSLayer();
	
	//Other globals
	private Stack<Vector<SNode>> stNodeVectors = new Stack<Vector<SNode>>();
	private String strPos = ""; //holds the current pos tag
	private String strTok = ""; //holds the current token
	private String strNode = ""; //holds the current node (i.e. cat annotation value)
	private String strSplitCompoundAnno[] = new String[2];
	private String strTempAnno;
	private SAnnotation anoTemp = null;
	private int intTokStartChar;
	private int intTokEndChar;
	private Vector<SNode> vecNodeList = new Vector<SNode>(); 	//Node List - to be dominated by next node
	
	private int intCountClosingBrackets=0;
	
	@Override
	protected void initialize(){
	//do some initilizations
	}
	@Override
	public DOCUMENT_STATUS mapSCorpus() {
		//returns the resource in case of module is an importer or exporter
		getResourceURI();
		//returns the SDocument object to be manipulated
		getSDocument();
		//returns that process was successful
		return(DOCUMENT_STATUS.COMPLETED);
	}
	
	@Override
	public DOCUMENT_STATUS mapSDocument() {

		PTBImporterProperties myProps = new PTBImporterProperties();
		this.setProperties(myProps);
		getSettings(); //initialize values for special parameters (configurable annotation names etc.)
		lyrPTB.setSName(strNamespace);

		
		if (getSDocument().getSDocumentGraph()== null)
			{getSDocument().setSDocumentGraph(SaltFactory.eINSTANCE.createSDocumentGraph());}
		
		txtText = getSDocument().getSDocumentGraph().createSTextualDS("");

		
		BufferedReader br = null;
	    try{
	    	
	    	br = new BufferedReader(new FileReader(getResourceURI().toFileString()));

	        String line;
	        String strValidate = br.readLine();

	        
        	while (!(strValidate.trim().startsWith("("))) //every line should start with a '(', otherwise ignore it
        	{
        		strValidate = br.readLine();
        	}
	        line = strValidate.trim();
        	
	        while (strValidate != null) {
	            if (CountInString(line, "(") == CountInString(line, ")"))
	            {
		            //sentence is complete
            		//line = line.replaceAll("([^ ])\\(", "'$1' (");
	            	if (bolHandleSlashTokens){
	            		line = line.replaceAll(" ([^ /]+)/([^ ]+) "," ('$1' '$2') ");
	            	}
	            	
		            mapSentence(line);
		            strValidate = br.readLine();
		            if (strValidate == null) {break;}
		        	while (!(strValidate.trim().startsWith("("))) //every line should start with a '(', otherwise ignore it
		        	{
		        		strValidate = br.readLine();
			            if (strValidate == null) {break;}
		        	}
		        	if (!(strValidate == null)){
		            line = strValidate.trim();
		        	}
	            }
	            else
	            {
	            	//sentence not yet complete, keep adding lines until equal amount of "(" and ")"
		            strValidate = br.readLine();
		            if (strValidate == null) {
		            	break;
		            	//document may have ended in an unfinished sentence!
		            }
		        	while (!(strValidate.trim().startsWith("("))) //every line should start with a '(', otherwise ignore it
		        	{
	        			strValidate = br.readLine();
		            	if (strValidate == null) {
		            		break;
		            	}
		        	}
	            	
		        	if (!(strValidate == null))
		        	{
		            	line += " " + strValidate.trim();		        		
		        	}
	            }
	        }
	    } 
	    catch(FileNotFoundException e)
	    {
	    	throw new PepperModuleException("The file " + getResourceURI().toFileString() + " was not found",e);
	    }
	    catch(IOException e)
	    {
	    	throw new PepperModuleException("Cannot read the input file " + getResourceURI().toFileString(),e);
	    }
	    finally 
	    {
	    	try{
	        if (br!= null)
	    	 br.close();
	    	}
	    	catch(IOException e)
		    {
		    	throw new PepperModuleException("Cannot close the stream for input file " + getResourceURI().toFileString(),e);
		    }
	    }
		
		txtText.setSText(stbText.toString());
		
		return(DOCUMENT_STATUS.COMPLETED);
	}
	
	public void mapSentence(String strSentence) {
		
		if (strSentence.startsWith("( (")) { 
			//remove superfluous wrapping brackets around sentence if found
			strSentence = strSentence.substring(2, strSentence.length()-2);	
		}
		
		SNode nodCurrentNode = null;
		
		String[] strAllNodes = strSentence.split(" \\(");
		for (String strSingleNode : strAllNodes) {
			strSingleNode = strSingleNode.trim();
			if (strSingleNode.startsWith("("))
			{
				strSingleNode = strSingleNode.substring(1);
			}
			if (!strSingleNode.equals(")") && !strSingleNode.equals(" ") && !strSingleNode.equals("")){
				if (strSingleNode.length() > strSingleNode.replace(" ","").length()){
					//this node is a token
					strPos = strSingleNode.substring(0, strSingleNode.indexOf(' ')); //this is the pos tag
					strTok = strSingleNode.substring(strSingleNode.indexOf(' ')).trim();
					strTok = strTok.replace(")", "");
					

					//put strTok + " " into STextualDS
					intTokStartChar = stbText.toString().length();				
					stbText.append(strTok);
					stbText.append(" ");
					intTokEndChar = stbText.toString().length();
					
					//create SToken node covering the strTok position
					SToken tokCurrentToken = getSDocument().getSDocumentGraph().createSToken(txtText, intTokStartChar, intTokEndChar);
				
					//annotate the SToken with pos=strPos
					tokCurrentToken.createSAnnotation(strNamespace, strPosName, strPos);
					nodCurrentNode = tokCurrentToken;
				
				}
				else
				{
					//non-terminal node
					strNode = strSingleNode.replace("(","");
					
					//create new SNode
					nodCurrentNode = SaltFactory.eINSTANCE.createSStructure();
					nodCurrentNode.getSLayers().add(lyrPTB);
					getSDocument().getSDocumentGraph().addSNode(nodCurrentNode);
					
					//annotate the new SNode with cat=strNode
					nodCurrentNode.createSAnnotation(strNamespace, strCatName, strNode.trim());
					
				}	
				
				//put current node into a list of things to be dominated once we pop
				if (vecNodeList.isEmpty())
				{
					vecNodeList = new Vector<SNode>();
					vecNodeList.add(nodCurrentNode);
					stNodeVectors.push(vecNodeList);
					vecNodeList = new Vector<SNode>();
				}
				else
				{
					vecNodeList = stNodeVectors.pop();
					vecNodeList.add(nodCurrentNode);
					stNodeVectors.push(vecNodeList);					
				}
				
				
				intCountClosingBrackets = CountInString(strSingleNode,")");
				for (int i=0; i < intCountClosingBrackets; i++){

					if (!(stNodeVectors.isEmpty()))
					{
						vecNodeList = stNodeVectors.pop();
						if (!stNodeVectors.isEmpty()) //if root has been reached, do nothing
						{
						nodCurrentNode = stNodeVectors.peek().lastElement();
											
							for (SNode nodChild : vecNodeList)
							{
								if (!(nodCurrentNode instanceof SStructure))
								{
									//do not attempt to create a dominance edge if the parent is a token
								}
								else
								{
								SDominanceRelation domCurrentDom = SaltFactory.eINSTANCE.createSDominanceRelation();
								domCurrentDom.setSSource((SStructure) nodCurrentNode);
								domCurrentDom.setSTarget(nodChild);
								domCurrentDom.addSType(strEdgeType);
								domCurrentDom.getLayers().add(lyrPTB);
								
								getSDocument().getSDocumentGraph().addSRelation(domCurrentDom);
	
								//add edge annotations if desired and present
								if (bolEdgeAnnos)
								{
	
									anoTemp = nodChild.getSAnnotations().get(0);
									strTempAnno = anoTemp.getSValueSTEXT().toString();
									if (strTempAnno.contains(strEdgeAnnoSeparator))
									{
										if (!(strTempAnno.startsWith(strEdgeAnnoSeparator)) && !(strTempAnno.indexOf(strEdgeAnnoSeparator)==strTempAnno.length()))
										{
											strSplitCompoundAnno[0] = strTempAnno.substring(0, strTempAnno.indexOf(strEdgeAnnoSeparator));
											strSplitCompoundAnno[1] = strTempAnno.substring(strTempAnno.indexOf(strEdgeAnnoSeparator)+1);
											anoTemp.setSValue(strSplitCompoundAnno[0]);
											domCurrentDom.createSAnnotation(strEdgeAnnoNameSpace, strEdgeAnnoName, strSplitCompoundAnno[1]);
										}
									}
								}
								
								
							}
							}
					}
		
					//empty the node list
					vecNodeList.removeAllElements();					
				
				}
				
			}
		}
		
		//clean up global lists for next sentence
		vecNodeList.removeAllElements();
		}
	}
	
	public int CountInString(String strInput, String strToCount){
	
		return strInput.length() - strInput.replace(strToCount, "").length();

	}

	/**
	 * @Amir: Is it possible, to make unit tests out of this?
	 * @param args
	 */
	public static void main(String[] args) {
		//System.out.println("hallo");
		PTBImporterProperties myProps = new PTBImporterProperties();
		
		PTBMapper myMapper = new PTBMapper(); 
		myMapper.setSDocument(SaltFactory.eINSTANCE.createSDocument());
		myMapper.setResourceURI(URI.createFileURI("C:/Pepper/corpora/ptb_test/brown_test.ptb"));
		myMapper.setProperties(myProps);
		DOCUMENT_STATUS myMappingResult = myMapper.mapSDocument();
		Salt2DOT salt2dot= new Salt2DOT();
		salt2dot.salt2Dot(myMapper.getSDocument().getSDocumentGraph(), URI.createFileURI("D:/dot_out.dot"));
		System.out.println("done");
		
	}
	
	private void getSettings(){
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


