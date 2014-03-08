package domain.constants;

public class GraphCoverageCriteriaId extends CoverageCriteriaId {
	
	public static final GraphCoverageCriteriaId COMPLETE_PATH = new GraphCoverageCriteriaId(1);
	public static final GraphCoverageCriteriaId PRIME_PATH = new GraphCoverageCriteriaId(2);
	public static final GraphCoverageCriteriaId ALL_DU_PATHS = new GraphCoverageCriteriaId(3);
	public static final GraphCoverageCriteriaId EDGE_PAIR = new GraphCoverageCriteriaId(4);
	public static final GraphCoverageCriteriaId COMPLETE_ROUND_TRIP = new GraphCoverageCriteriaId(5);
	public static final GraphCoverageCriteriaId ALL_USES = new GraphCoverageCriteriaId(6);
	public static final GraphCoverageCriteriaId EDGE = new GraphCoverageCriteriaId(7);
	public static final GraphCoverageCriteriaId SIMPLE_ROUND_TRIP = new GraphCoverageCriteriaId(8); 
	public static final GraphCoverageCriteriaId ALL_DEFS = new GraphCoverageCriteriaId(9); 
	public static final GraphCoverageCriteriaId NODE = new GraphCoverageCriteriaId(10);
	
	private int id;
	
	private GraphCoverageCriteriaId (int id) {
		this.id = id;
	}
	
	protected GraphCoverageCriteriaId () {
	}
	
	public static boolean isADefUsesCoverageCriteria(GraphCoverageCriteriaId criteria) {
		return criteria == ALL_DU_PATHS || criteria == ALL_USES || criteria == ALL_DEFS;
	}
	
	public static CoverageCriteriaId valueOf(String s) {
		if (s.equals("COMPLETE_PATH"))
			return COMPLETE_PATH;
		else if (s.equals("PRIME_PATH"))
			return PRIME_PATH;
		else if (s.equals("ALL_DU_PATHS"))
			return ALL_DU_PATHS;
		else if (s.equals("EDGE_PAIR"))
			return EDGE_PAIR;
		else if (s.equals("COMPLETE_ROUND_TRIP"))
			return COMPLETE_ROUND_TRIP;
		else if (s.equals("ALL_USES"))
			return ALL_USES;
		else if (s.equals("EDGE"))
			return EDGE;
		else if (s.equals("SIMPLE_ROUND_TRIP"))
			return SIMPLE_ROUND_TRIP;
		else if (s.equals("ALL_DEFS"))
			return ALL_DEFS;
		else if (s.equals("NODE"))
			return NODE;
		throw new IllegalArgumentException("Value " + s + " is not a valid GraphCoverageCriteriaID!");
	}

	public static CoverageCriteriaId valueOf(int i) {
		if (i == 1)
			return COMPLETE_PATH;
		else if (i == 2)
			return PRIME_PATH;
		else if (i == 3)
			return ALL_DU_PATHS;
		else if (i == 4)
			return EDGE_PAIR;
		else if (i == 5)
			return COMPLETE_ROUND_TRIP;
		else if (i == 6)
			return ALL_USES;
		else if (i == 7)
			return EDGE;
		else if (i == 8)
			return SIMPLE_ROUND_TRIP;
		else if (i == 9)
			return ALL_DEFS;
		else if (i == 10)
			return NODE;
		throw new IllegalArgumentException("Value " + Integer.toString(i) + " is not a valid GraphCoverageCriteriaID!");
	}

}
