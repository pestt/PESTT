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

}
