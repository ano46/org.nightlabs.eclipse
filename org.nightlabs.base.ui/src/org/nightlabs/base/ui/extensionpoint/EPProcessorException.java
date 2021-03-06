/* *****************************************************************************
 * org.nightlabs.base.ui - NightLabs Eclipse utilities                            *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
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

package org.nightlabs.base.ui.extensionpoint;

import org.eclipse.core.runtime.IExtension;

/**
 * @deprecated The class has moved to the bundle org.nightlabs.eclipse.extension but is 
 * 		also deprecated there. Don't use it.
 */
@Deprecated
public class EPProcessorException
extends Exception
{
	private static final long serialVersionUID = 1L;

	public EPProcessorException() { }

	public EPProcessorException(String message) {
		super(message);
	}

	public EPProcessorException(Throwable cause) {
		super(cause);
	}

	public EPProcessorException(String message, Throwable cause) {
		super(message, cause);
	}

	public EPProcessorException(String message, IExtension extension) {
		super(message+" The extension is located in " + extension.getNamespaceIdentifier() + "."); //$NON-NLS-1$ //$NON-NLS-2$
		// and has the id "+extension.getExtensionPointUniqueIdentifier());
	}

	public EPProcessorException(String message, IExtension extension, Throwable cause) {
		super(message+" The extension is located in " + extension.getNamespaceIdentifier() + ".", cause); //$NON-NLS-1$ //$NON-NLS-2$
		// and has the id "+extension.getExtensionPointUniqueIdentifier());
	}
}
