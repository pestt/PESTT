package perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

import constants.Description_ID;

/**
 *  This class is meant to serve as an example for how various contributions 
 *  are made to a perspective. Note that some of the extension point id's are
 *  referred to as API constants while others are hardcoded and may be subject 
 *  to change. 
 */
public class Perspective implements IPerspectiveFactory {

	private IPageLayout layout;

	public Perspective() {
		super();
	}

	public void createInitialLayout(IPageLayout layout) {
		this.layout = layout;
		addViews();
	}

	private void addViews() {
		// Creates the overall folder layout. 
		// Note that each new Folder uses a percentage of the remaining EditorArea.
	
		String editorarea = IPageLayout.ID_EDITOR_AREA;
				
		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.75f, editorarea);
		bottom.addView(Description_ID.VIEW_REQUIREMENT_SET);
		bottom.addView(IConsoleConstants.ID_CONSOLE_VIEW);
		bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
		bottom.addView("org.eclipse.pde.runtime.LogView");
		bottom.addView(IPageLayout.ID_TASK_LIST);
		
		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, 0.15f, editorarea);
		left.addView(IPageLayout.ID_PROJECT_EXPLORER);
		
		IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, 0.45f, editorarea);
		right.addView(Description_ID.VIEW_GRAPH);
		right.addView(Description_ID.VIEW_COVERAGE_CRITERIA);
	}

}