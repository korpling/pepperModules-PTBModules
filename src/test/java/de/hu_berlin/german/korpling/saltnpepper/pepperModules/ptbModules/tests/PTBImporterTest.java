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
