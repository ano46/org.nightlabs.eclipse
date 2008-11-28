/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 * Project author: Daniel Mazurek <Daniel.Mazurek [at] nightlabs [dot] org>    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.editor2d.ui.actions;

import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;
import org.nightlabs.editor2d.ui.EditorStateManager;
import org.nightlabs.editor2d.ui.request.EditorRequestConstants;
import org.nightlabs.editor2d.ui.resource.Messages;


public class NormalSelectionAction
extends SelectionAction
implements EditorRequestConstants
{
  public static final String ID = NormalSelectionAction.class.getName();
    
  /**
   * @param part
   */
  public NormalSelectionAction(IWorkbenchPart part) {
    super(part);
  }

  @Override
	protected boolean calculateEnabled()
  {
    if (EditorStateManager.getCurrentState() != EditorStateManager.STATE_NORMAL_SELECTION) {
      return true;
    }
    return false;
  }
  
  /**
   * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#init()
   */
  @Override
	protected void init()
  {
  	super.init();
  	setText(Messages.getString("org.nightlabs.editor2d.ui.actions.NormalSelectionAction.text")); //$NON-NLS-1$
  	setToolTipText(Messages.getString("org.nightlabs.editor2d.ui.actions.NormalSelectionAction.tooltip")); //$NON-NLS-1$
  	setId(ID);
//  	setImageDescriptor(SharedImages.DESC_SELECTION_TOOL_16);
  }
    
  @Override
	public void run()
  {
  	if (!getSelectedObjects().isEmpty()) {
  		EditorStateManager.setNormalSelectionMode(getSelectedObjects());
  	}
  }
}
