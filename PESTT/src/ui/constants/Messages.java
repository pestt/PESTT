package ui.constants;

public class Messages {
	
	public static final String DRAW_GRAPH_TITLE = "PESTT - Draw Graph";
	public static final String DRAW_GRAPH_MSG = "You need to draw the Graph first.\n" +
												"Select a method in the editor then click in the button to draw.";
	public static final String GRAPH_UPDATE_MSG = "The graph needs to be updated.";
	
	public static final String COVERAGE_TITLE = "PESTT - Coverage Criteria";
	public static final String COVERAGE_SELECT_MSG = "You need to select a coverage criteria in the Graph Coverage Criteria view.";
	
	public static final String SAVE_CHANGES = "Are you sure that you want to save the changes:";
	
	public static final String TEST_PATH_TITLE = "PESTT - Test Path";
	public static final String TEST_PATH_INPUT_MSG = "You need to enter a test path.";
	public static final String TEST_PATH_INVALID_INPUT_MSG = "You need to enter a valid test path.";
	public static final String TEST_PATH_BECAME_INVALID_INPUT_MSG = "The following test paths is no longer valid.";
	public static final String TEST_PATH_REMOVE_MSG = "It will be removed from the list";
	public static final String TEST_PATH_CONFIRM_REMOVE_MSG = "Are you sure that you want to delete this test path:\n";
	public static final String TEST_PATH_SELECT_TO_REMOVE_MSG = "You need to select a test path to be removed.";
	public static final String TEST_PATH_SELECT_TO_EDIT_MSG = "You need to select a test path to be edited.";
	public static final String TEST_PATH_WARNING_EDITED_MSG = "You can only edit one test path at a time.";
	
	public static final String TEST_REQUIREMENT_TITLE = "PESTT - Test Requirements";
	public static final String TEST_REQUIREMENT_INPUT_MSG = "You need to enter a test requirement.";
	public static final String TEST_REQUIREMENT_INVALID_INPUT_MSG = "You need to enter a valid test requirement.";
	public static final String TEST_REQUIREMENT_BECAME_INVALID_INPUT_MSG = "The following test requirement is no longer valid.";
	public static final String TEST_REQUIREMENT_REMOVE_MSG = "It will be removed from the list";
	public static final String TEST_REQUIREMENT_CONFIRM_REMOVE_MSG = "Are you sure that you want to delete this test requirement:\n";
	public static final String TEST_REQUIREMENT_SELECT_TO_REMOVE_MSG = "You need to select a test requirement to be removed.";
	public static final String TEST_REQUIREMENT_INFINITE_MSG = "The number of test requirements is infinite. (The method contains cycles)."; 
	public static final String TEST_REQUIREMENT_SELECT_TO_EDIT_MSG = "You need to select a test requirement to be edited.";
	public static final String TEST_REQUIREMENT_WARNING_EDITED_MSG = "You can only edit one test requirement at a time.";
	public static final String TEST_REQUIREMENT_NEED_UPDATE_MSG = "You to update the test requirements.";
	public static final String TEST_REQUIREMENT_NOT_INFEASIBLE_MSG = " is not infeasible.\nIt will be set as a feasible path and the covered status will be updated..";
	
	public static final String STATISTICS_TITLE = "PESTT - Statistics";
	public static final String STATISTICS_MSG = "Impossible to show statistics.";
	public static final String STATISTICS_REASON_MSG = "Graph contains loop(s) (infinite paths).";
	
	public static final String PREFERENCES_TITLE = "PESTT - Preferences";
	public static final String PREFERENCES_DOT_MSG = "Please check if the dot location is correct.\n (Window → Preferences → PESTT)";
	public static final String PREFERENCES_TOOLS_MSG = "Please check if the tools.jar location is correct.\n (Window → Preferences → PESTT)";
	
	public static final String DEF_USES_TITLE = "PESTT - Definitions and Uses";
	public static final String DEF_USES_MSG = "You need to generate the DefUses in the Data Flow Coverage Criteria View.";
}
