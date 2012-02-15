package editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import constants.Markers_ID;

public class Markers {
	
	private IFile file;
	private IMarker marker;

	public Markers(IFile file) {
		this.file = file;
	}
	
	public void createMarks(String markerType, int offset, int length) {
		try {
			marker = file.createMarker(markerType);
			marker.setAttribute(IMarker.CHAR_START, offset);
			marker.setAttribute(IMarker.CHAR_END, offset + length);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteAllMarkers() {
		try {
			file.deleteMarkers(Markers_ID.LINK_MARKER, true, IResource.DEPTH_INFINITE);
			file.deleteMarkers(Markers_ID.FULL_COVERAGE_MARKER, true, IResource.DEPTH_INFINITE);
			file.deleteMarkers(Markers_ID.NO_COVERAGE_MARKER, true, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
