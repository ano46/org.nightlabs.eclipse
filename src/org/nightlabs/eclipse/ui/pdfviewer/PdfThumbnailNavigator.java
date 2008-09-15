package org.nightlabs.eclipse.ui.pdfviewer;

import java.util.Collection;

public class PdfThumbnailNavigator
implements ContextElement<PdfThumbnailNavigator>
{
	public static final ContextElementType<PdfThumbnailNavigator> CONTEXT_ELEMENT_TYPE = new ContextElementType<PdfThumbnailNavigator>(PdfThumbnailNavigator.class);

	private PdfViewer pdfViewer;
	private String contextElementId;

	/**
	 * Get the <code>PdfThumbnailNavigator</code> that is assigned to the given <code>pdfViewer</code>.
	 *
	 * @param pdfViewer the {@link PdfViewer} for which to get the <code>PdfThumbnailNavigator</code>.
	 * @return the <code>PdfThumbnailNavigator</code> or <code>null</code>, if none has been created for the given <code>pdfViewer</code>.
	 */
	public static PdfThumbnailNavigator getPdfThumbnailNavigator(PdfViewer pdfViewer, String contextElementId)
	{
		if (pdfViewer == null)
			throw new IllegalArgumentException("pdfViewer must not be null!");

		return pdfViewer.getContextElement(CONTEXT_ELEMENT_TYPE, contextElementId);
	}

	public static Collection<? extends PdfThumbnailNavigator> getPdfThumbnailNavigators(PdfViewer pdfViewer)
	{
		if (pdfViewer == null)
			throw new IllegalArgumentException("pdfViewer must not be null!");

		return pdfViewer.getContextElements(CONTEXT_ELEMENT_TYPE);
	}

	public PdfThumbnailNavigator(PdfViewer pdfViewer) {
		this(pdfViewer, null);
	}

	public PdfThumbnailNavigator(PdfViewer pdfViewer, String contextElementId) {
		if (pdfViewer == null)
			throw new IllegalArgumentException("pdfViewer must not be null!");

		this.pdfViewer = pdfViewer;
		this.contextElementId = contextElementId;
		pdfViewer.registerContextElement(this);
	}

	@Override
	public PdfViewer getPdfViewer() {
		return pdfViewer;
	}
	@Override
	public ContextElementType<PdfThumbnailNavigator> getContextElementType() {
		return CONTEXT_ELEMENT_TYPE;
	}
	@Override
	public String getContextElementId() {
		return contextElementId;
	}
}
