package constants;

public enum Description_ID {
	
	INSTANCE;
	
	public static final String VIEW_GRAPH = "PESTT.ViewGraph";
	public static final String VIEW_REQUIREMENT_SET = "PESTT.ViewRequirementSet";
	public static final String VIEW_COVERAGE_CRITERIA = "PESTT.GraphCoverageCriteria";
	public static final String LINK_BUTTON = "PESTT.Link";
	public static final String LAYER_BUTTON = "PESTT.Layers";
	public static final String NONE = "NONE";
	public static final String EMPTY = "";
	public static final String NODE = "node";
	public static final String EDGE = "edge" ;
	public static final String GRAPH = "graph";
	public static final String STOP = "stop";
	public static final String DOT_COVERAGE_CRITERIA = "digraph grafo {\nrankdir=TD\nsize=\"10,10\"\n" +
														"0 -> 1;\n" +
														"1 -> 2;\n" +
														"1 -> 3;\n" +
														"1 -> 4;\n" +
														"2 -> 5;\n" +
														"3 -> 6;\n" +
														"4 -> 7;\n" +
														"5 -> 6;\n" +
														"5 -> 8;\n" +
														"6 -> 9;\n" +
														"}\n";

}
