![SaltNPepper project](./gh-site/img/SaltNPepper_logo2010.png)
# pepperModules-PTBModules
This project provides an im- and an exporter to support the Penn Treebank Format (PTB) for the linguistic converter framework Pepper (see https://u.hu-berlin.de/saltnpepper). A detailed description of the importer can be found in section [PTBImporter](#importer) and one for the exporter can be found in [PTBExporter](#exporter).

Pepper is a pluggable framework to convert a variety of linguistic formats (like [TigerXML](http://www.ims.uni-stuttgart.de/forschung/ressourcen/werkzeuge/TIGERSearch/doc/html/TigerXML.html), the [EXMARaLDA format](http://www.exmaralda.org/), [PAULA](http://www.sfb632.uni-potsdam.de/paula.html) etc.) into each other. Furthermore Pepper uses Salt (see https://github.com/korpling/salt), the graph-based meta model for linguistic data, which acts as an intermediate model to reduce the number of mappings to be implemented. That means converting data from a format _A_ to format _B_ consists of two steps. First the data is mapped from format _A_ to Salt and second from Salt to format _B_. This detour reduces the number of Pepper modules from _n<sup>2</sup>-n_ (in the case of a direct mapping) to _2n_ to handle a number of n formats.

![n:n mappings via SaltNPepper](./gh-site/img/puzzle.png)

In Pepper there are three different types of modules:
* importers (to map a format _A_ to a Salt model)
* manipulators (to map a Salt model to a Salt model, e.g. to add additional annotations, to rename things to merge data etc.)
* exporters (to map a Salt model to a format _B_).

For a simple Pepper workflow you need at least one importer and one exporter.

## Requirements
Since the here provided module is a plugin for Pepper, you need an instance of the Pepper framework. If you do not already have a running Pepper instance, click on the link below and download the latest stable version (not a SNAPSHOT):

> Note:
> Pepper is a Java based program, therefore you need to have at least Java 7 (JRE or JDK) on your system. You can download Java from https://www.oracle.com/java/index.html or http://openjdk.java.net/ .


## Install module
If this Pepper module is not yet contained in your Pepper distribution, you can easily install it. Just open a command line and enter one of the following program calls:

**Windows**
```
pepperStart.bat 
```

**Linux/Unix**
```
bash pepperStart.sh 
```

Then type in command *is* and the path from where to install the module:
```
pepper> update de.hu_berlin.german.korpling.saltnpepper::pepperModules-pepperModules-PTBModules::https://korpling.german.hu-berlin.de/maven2/
```

## Usage
To use this module in your Pepper workflow, put the following lines into the workflow description file. Note the fixed order of xml elements in the workflow description file: &lt;importer/>, &lt;manipulator/>, &lt;exporter/>. The PTBImporter is an importer module, which can be addressed by one of the following alternatives.
A detailed description of the Pepper workflow can be found on the [Pepper project site](https://u.hu-berlin.de/saltnpepper). 

### a) Identify the module by name

```xml
<importer name="PTBImporter" path="PATH_TO_CORPUS"/>
```

or

```xml
<exporter name="PTBExporter" path="PATH_TO_CORPUS"/>
```

### b) Identify the module by formats
```xml
<importer formatName="PTB" formatVersion="1.0" path="PATH_TO_CORPUS"/>
```

or

```xml
<exporter formatName="PTB" formatVersion="1.0" path="PATH_TO_CORPUS"/>
```

### c) Use properties
```xml
<importer name="PTBImporter" path="PATH_TO_CORPUS">
  <property key="PROPERTY_NAME">PROPERTY_VALUE</property>
</importer>
```

or

```xml
<importer name="PTBExporter" path="PATH_TO_CORPUS">
  <property key="PROPERTY_NAME">PROPERTY_VALUE</property>
</importer>
```

## Contribute
Since this Pepper module is under a free license, please feel free to fork it from github and improve the module. If you even think that others can benefit from your improvements, don't hesitate to make a pull request, so that your changes can be merged.
If you have found any bugs, or have some feature request, please open an issue on github. If you need any help, please write an e-mail to saltnpepper@lists.hu-berlin.de .

## Funders
This project has been funded by the [department of corpus linguistics and morphology](https://www.linguistik.hu-berlin.de/institut/professuren/korpuslinguistik/) of the Humboldt-Universität zu Berlin and the [Sonderforschungsbereich 632](https://www.sfb632.uni-potsdam.de/en/). 

## License
  Copyright 2014 Humboldt-Universität zu Berlin.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.


# <a name="importer">PTBImporter</a>
This module imports text files in the PTB format into a Salt corpus.

Each PTB format text file is mapped to a single Salt document. Multiple files in a folder are interpreted as a corpus or subcorpus containing those documents. It is possible to have a folder hierarchy corresponding to a corpus with multiple corpora. The documents in each subcorpus are text files with the expected extension .txt, .ptb or .mrg. 
A single PTB text document for import has the following form: 

```xml
(S  
      (PP (IN In)  
        (NP (JJ American) (NN romance) )) 
      (, ,)  
      (NP-SBJ-2 (RB almost) (NN nothing) ) 
      (VP (VBZ rates)  
        (S  
          (NP-SBJ (-NONE- *-2) ) 
          (ADJP-PRD  
            (ADJP (JJR higher) ) 
            (PP (IN than)  
              (SBAR-NOM  
                (WHNP-1 (WP what) ) 
                (S  
                  (NP-SBJ (DT the) (NN movie) (NNS men) ) 
                  (VP (VB have)  
                    (VP (VBN called)  
                      (S  
                        (NP-SBJ (-NONE- *T*-1) ) 
                        (`` ``)  
                        (S-NOM-PRD  
                          (NP-SBJ (-NONE- *) ) 
                          (VP (NN meeting)  
                            (NP (JJ cute) ))) 
                        ('' '') ))))))))))));
```			
			
Each opening bracket represents deeper embedding in the tree hierarchy. Terminal nodes (i.e. tokens/word forms) are found in the inner-most brackets aligned with the closing bracket to the right. Their part-of-speech tag is aligned to the left, e.g. in the token "(VB have)", which stands for the word "have", with the part-of-speech "VB". Sentences can be indented as in the example above, for easier human readability, but one-line per sentence format is also supported: white space between tree nodes is completely ignored. A sentence beginning is recognized wherever an openning brackets is found, and the end of the sentence is detected when an equal number of opening and closing brackets have been found. Multiple sentences in one line are no supported. 
In some PTB style corpora, node 'functions' are designated after a separator in the node name, and these may optionally be interprested as edge labels. For example, in "(NP-SBJ" above, the segment "-SBJ" signifies that this node is the subject of the sentence. This part of the node label can be optionally removed and used as an edge annotation for the incoming edge (see  below). 
Some corpora have a different notation for tokens, where they are not bracketed and the word form precedes the part-of-speech, which is separated by a slash (a.k.a. "atis-style"). For example:

```xml
(NP two/CD friends/NNS )
```

This PTB 'dialect' is also supported, and support for such tokens can be switched on or off using the properties below. 

## Properties
The table contains an overview of all usable properties to customize the behaviour of this Pepper module. The following section contains a description of each property and describes the resulting differences in the mapping to the Salt model. 

|    Name of property          |	Type of property |	optional/ mandatory |	default value|
|------------------------------|------------------|----------------------|----------------|
|ptb.importer.nodeNamespace    | String           |optional	            |ptb|
|ptb.importer.posName          |	String           |optional	            |pos|
|ptb.importer.catName          |	String           |optional	            |cat|
|ptb.importer.edgeType         |	String           |optional	            |edge|
|ptb.importer.edgeAnnoSeparator|	String           |optional	            |-|
|ptb.importer.edgeAnnoNamespace|	String           |optional	            |ptb|
|ptb.importer.edgeAnnoName     |	String           |optional	            |func|
|ptb.importer.handleSlashTokens|	Boolean          |optional	            |true|

### nodeNamespace
ptb.importer.nodeNamespace=ptb
Determines the name of the Salt layer assigned to tree nodes on import.

### posName
ptb.importer.posName=pos
Name of pos annotation name for PTB tokens, e.g. 'pos'.

### catName
ptb.importer.catName=cat
Name of category annotation for PTB non-terminal nodes, e.g. 'cat'.

### edgeType
ptb.importer.edgeType=edge
Name of edge type for PTB dominance edges, e.g. 'edge'.

### edgeAnnoSeparator
ptb.importer.edgeAnnoSeparator=-
Separator character for edge labels following node annotation, e.g. the '-' in (NP-subj (....

### edgeAnnoNamespace
ptb.importer.edgeAnnoNamespace=ptb
Namespace for PTB edge annotations (represented within a node label after a separator), e.g. 'ptb'.

### edgeAnnoName
ptb.importer.edgeAnnoName=func
Name of PTB dominance edge annotation name, e.g. 'func'.

### nodeNamespace
ptb.importer.importEdgeAnnos=true
Boolean, whether to look for edge annotations after a separator.

### handleSlashTokens
ptb.importer.handleSlashTokens=true
Boolean, whether to handle Penn atis-style tokens, which are non bracketed and separate the pos tag with a slash, e.g.: (NP two/CD friends/NNS ).

# <a name="exporter">PTBExporter</a>
This module exports text files in the PTB format from a Salt representation.

Each Salt document is mapped to a single PTB text file. Multiple documents in subcorpora are interpreted as a folder structure with multiple text files in each subcorpus folder. 
A single PTB text document generated by the exporter might look like this: 

```xml
(S  
      (PP (IN In)  
        (NP (JJ American) (NN romance) )) 
      (, ,)  
      (NP-SBJ-2 (RB almost) (NN nothing) ) 
      (VP (VBZ rates)  
        (S  
          (NP-SBJ (-NONE- *-2) ) 
          (ADJP-PRD  
            (ADJP (JJR higher) ) 
            (PP (IN than)  
              (SBAR-NOM  
                (WHNP-1 (WP what) ) 
                (S  
                  (NP-SBJ (DT the) (NN movie) (NNS men) ) 
                  (VP (VB have)  
                    (VP (VBN called)  
                      (S  
                        (NP-SBJ (-NONE- *T*-1) ) 
                        (`` ``)  
                        (S-NOM-PRD  
                          (NP-SBJ (-NONE- *) ) 
                          (VP (NN meeting)  
                            (NP (JJ cute) ))) 
                        ('' '') ))))))))))));
```			
			
The Salt document graph is searched for tokens and hirearchical dominance relations above these. The tree of dominance relations is realized as a nested bracket structure, as shown above. Tokens (i.e. word forms) are found in the inner-most brackets aligned with the closing bracket to the right. Their part-of-speech tag is aligned to the left, e.g. in the token "(VB have)", which stands for the word "have", with the part-of-speech "VB".

In some PTB style corpora, node 'functions' are designated after a separator in the node name, and these may optionally be interprested as edge labels. For example, in "(NP-SBJ" above, the segment "-SBJ" signifies that this node is the subject of the sentence. This part of the node label can be generated from a specified edge annotation for the incoming edge with a specified separator (the '-' in this example; see properties below). 

Some corpora use a different notation for tokens, where they are not bracketed and the word form precedes the part-of-speech, which is separated by a slash (a.k.a. "atis-style"). For example:

```xml
(NP two/CD friends/NNS )
```

This PTB 'dialect' is also supported, and generation of such tokens can be switched on or off using the  below 

## Properties
The table  contains an overview of all usable properties to customize the behaviour of this Pepper module. The following section contains a description of each property and describes the resulting differences in the mapping to the Salt model.

Properties to customize exporter behaviour

|         Name of property     |	Type of property |	optional/ mandatory |	default value|
|------------------------------|-------------------------|--------------------------|----------------|
|ptb.Exporter.nodeNamespace    |        String           |optional	            |ptb|
|ptb.Exporter.posName          |	String           |optional	            |pos|
|ptb.Exporter.catName          |	String           |optional	            |cat|
|ptb.Exporter.edgeType         |	String           |optional	            |edge|
|ptb.Exporter.edgeAnnoSeparator|	String           |optional	            |-|
|ptb.Exporter.edgeAnnoNamespace|	String           |optional	            |ptb|
|ptb.Exporter.edgeAnnoName     |	String           |optional	            |func|
|ptb.Exporter.handleSlashTokens|	Boolean          |optional	            |false|

### nodeNamespace
ptb.Exporter.nodeNamespace=ptb
Name of namespace for nodes to export and their annotations, e.g. 'ptb'. Only nodes within this layer name in Salt will be exported.

### posName
ptb.Exporter.posName=pos
Name of pos annotation name for tokens, e.g. 'pos'. Only this annotation name will be taken to generate pos labels for the tokens.

### catName
ptb.Exporter.catName=cat
Name of category annotation for non-terminal nodes to be exported, e.g. 'cat'. Only this annotation name will be taken to generate non-terminal node lables.

### edgeType
ptb.Exporter.edgeType=edge
Name of edge type for dominance edges to be exported, e.g. 'edge'. Only this edge type will be taken to generate the PTB bracket structure.

### edgeAnnoSeparator
ptb.Exporter.edgeAnnoSeparator=-
Separator character for edge labels following node annotation, e.g. the '-' in (NP-subj (....

### edgeAnnoNamespace
ptb.Exporter.edgeAnnoNamespace=ptb
Namespace for edge annotations to be exported (represented within a node label after a separator), e.g. 'ptb'.

### edgeAnnoName
ptb.Exporter.edgeAnnoName=func
Name of dominance edge annotation name to be exported, e.g. 'func'.

###nodeNamespace
ptb.Exporter.exportEdgeAnnos=true
Boolean, whether to output edge annotations after a separator.

### handleSlashTokens
ptb.Exporter.handleSlashTokens=false
Boolean, whether to create Penn atis-style tokens, which are non bracketed and separate the pos tag with a slash, e.g.: (NP two/CD friends/NNS ).
