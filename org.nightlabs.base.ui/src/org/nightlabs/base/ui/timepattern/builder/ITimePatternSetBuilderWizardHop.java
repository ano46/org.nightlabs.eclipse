/**
 * 
 */
package org.nightlabs.base.ui.timepattern.builder;

import org.nightlabs.base.ui.wizard.IWizardHop;
import org.nightlabs.timepattern.TimePatternFormatException;
import org.nightlabs.timepattern.TimePatternSet;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public interface ITimePatternSetBuilderWizardHop extends IWizardHop {

	public String getHopDescription();
	
	public void configureTimePatternSet(TimePatternSet timePatternSet) throws TimePatternFormatException;
}
