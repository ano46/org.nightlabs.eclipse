package org.nightlabs.history.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.nightlabs.history.ui.resource.Messages;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class EditorHistoryBackAction extends Action {

	public static final String ID = EditorHistoryBackAction.class.getName();

	public EditorHistoryBackAction() {
		super();
		setId(ID);
		setText(Messages.getString("org.nightlabs.history.ui.EditorHistoryBackAction.text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("org.nightlabs.history.ui.EditorHistoryBackAction.tooltip")); //$NON-NLS-1$
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_BACK));
		setActionDefinitionId("org.eclipse.ui.navigate.backwardHistory"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		EditorHistory.sharedInstance().historyBack();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return EditorHistory.sharedInstance().canBackward();
	}

}
