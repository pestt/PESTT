package ui.constants;

public class Messages {
	
	public static final String DRAW_GRAPH_TITLE = "PESTT - Draw Graph";
	public static final String DRAW_GRAPH_MSG = "You need to draw the Graph first.\n" +
												"Select a method in the editor then click in the button to draw.";
	public static final String GRAPH_UPDATE_MSG = "The graph will be updated.";
	
	public static final String COVERAGE_TITLE = "PESTT - Coverage Criteria";
	public static final String COVERAGE_SELECT_MSG = "You need to select a coverage criteria in the Graph Coverage Criteria view.";
	
	public static final String TEST_PATH_TITLE = "PESTT - Test path input";
	public static final String TEST_PATH_INPUT_MSG = "You need to enter a test path.";
	public static final String TEST_PATH_INVALID_INPUT_MSG = "You need to enter a valid test path.";
	public static final String TEST_PATH_BECAME_INVALID_INPUT_MSG = "The following test paths is no longer valid.";
	public static final String TEST_PATH_REMOVE_MSG = "It will be removed from the list";
	public static final String TEST_PATH_SUCCESS_REMOVE_MSG = "Test path successfully removed.";
	public static final String TEST_PATH_SELECT_TO_REMOVE_MSG = "You need to select a test path to be removed.";
	
	public static final String TEST_REQUIREMENT_TITLE = "PESTT - Test requirements input";
	public static final String TEST_REQUIREMENT_INPUT_MSG = "You need to enter a test requirement.";
	public static final String TEST_REQUIREMENT_INVALID_INPUT_MSG = "You need to enter a valid test requirement.";
	public static final String TEST_REQUIREMENT_BECAME_INVALID_INPUT_MSG = "The following test requirement is no longer valid.";
	public static final String TEST_REQUIREMENT_REMOVE_MSG = "It will be removed from the list";
	public static final String TEST_REQUIREMENT_SUCCESS_REMOVE_MSG = "Test requirement successfully removed.";
	public static final String TEST_REQUIREMENT_SELECT_TO_REMOVE_MSG = "You need to select a test requirement to be removed.";
	public static final String TEST_REQUIREMENT_INFINITE_MSG = "The number of test requirements is infinite. (The method contains cycles)."; 
	
	public static final String STATISTICS_TITLE = "PESTT - Statistics";
	public static final String STATISTICS_MSG = "Impossible to show statistics.";
	public static final String STATISTICS_REASON_MSG = "Graph contains loop(s) (infinite paths).";
	
	public static final String PREFERENCES_TITLE = "PESTT - Preferences";
	public static final String PREFERENCES_MSG = "Please check if the dot location is currect.\n (Window -> Preferences -> PESTT)";
}
