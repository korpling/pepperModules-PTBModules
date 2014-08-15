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

import org.junit.Before;

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.FormatDesc;
import de.hu_berlin.german.korpling.saltnpepper.pepper.testFramework.PepperImporterTest;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.PTBModules.PTBImporter;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;

public class PTBImporterTest extends PepperImporterTest{
	@Before
	public void setUp() {		
		setFixture(new PTBImporter());
		getFixture().setSaltProject(SaltFactory.eINSTANCE.createSaltProject());
		//set formats to support
		FormatDesc formatDef= new FormatDesc();
		formatDef.setFormatName("PTB");
		formatDef.setFormatVersion("1.0");
		this.supportedFormatsCheck.add(formatDef);
	}
}
