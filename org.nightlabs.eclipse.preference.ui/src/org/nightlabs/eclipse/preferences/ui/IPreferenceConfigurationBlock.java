/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.nightlabs.eclipse.preferences.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


/**
 * Interface for preference configuration blocks which can either be
 * wrapped by a {@link org.eclipse.jdt.internal.ui.preferences.AbstractConfigurationBlockPreferencePage}
 * or be included some preference page.
 * <p>
 * Clients may implement this interface.
 * </p>
 * <p>
 * This class was originally taken from the Eclipse JDT project. 
 * </p>
 * 
 * @author unascribed
 * @version $Revision: 1734 $ - $Date: 2008-01-08 17:02:20 +0100 (Di, 08 Jan 2008) $
 * @since 3.0
 */
public interface IPreferenceConfigurationBlock {
	
	/**
	 * Creates the preference control.
	 * 
	 * @param parent the parent composite to which to add the preferences control
	 * @return the control that was added to <code>parent</code> 
	 */
	Control createControl(Composite parent);
	
	/**
	 * Called after creating the control. Implementations should load the 
	 * preferences values and update the controls accordingly.
	 */
	void initialize();
	
	/**
	 * Called when the <code>OK</code> button is pressed on the preference
	 * page. Implementations should commit the configured preference settings
	 * into their form of preference storage.
	 */
	void performOk();
	
	/**
	 * Called when the <code>Defaults</code> button is pressed on the
	 * preference page. Implementation should reset any preference settings to
	 * their default values and adjust the controls accordingly.
	 */
	void performDefaults();
	
	/**
	 * Called when the preference page is being disposed. Implementations should
	 * free any resources they are holding on to.
	 */
	void dispose();
}