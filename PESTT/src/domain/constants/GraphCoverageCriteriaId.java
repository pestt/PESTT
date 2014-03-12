package domain.constants;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class GraphCoverageCriteriaId extends CoverageCriteriaId {
	
	public static final GraphCoverageCriteriaId COMPLETE_PATH = new GraphCoverageCriteriaId("COMPLETE_PATH");
	public static final GraphCoverageCriteriaId PRIME_PATH = new GraphCoverageCriteriaId("PRIME_PATH");
	public static final GraphCoverageCriteriaId ALL_DU_PATHS = new GraphCoverageCriteriaId("ALL_DU_PATHS");
	public static final GraphCoverageCriteriaId EDGE_PAIR = new GraphCoverageCriteriaId("EDGE_PAIR");
	public static final GraphCoverageCriteriaId COMPLETE_ROUND_TRIP = new GraphCoverageCriteriaId("COMPLETE_ROUND_TRIP");
	public static final GraphCoverageCriteriaId ALL_USES = new GraphCoverageCriteriaId("ALL_USES");
	public static final GraphCoverageCriteriaId EDGE = new GraphCoverageCriteriaId("EDGE");
	public static final GraphCoverageCriteriaId SIMPLE_ROUND_TRIP = new GraphCoverageCriteriaId("SIMPLE_ROUND_TRIP"); 
	public static final GraphCoverageCriteriaId ALL_DEFS = new GraphCoverageCriteriaId("ALL_DEFS"); 
	public static final GraphCoverageCriteriaId NODE = new GraphCoverageCriteriaId("NODE");
	
	protected GraphCoverageCriteriaId (String name) {
		super(name);
	}
	
	protected GraphCoverageCriteriaId () {
	}
	
	public static boolean isADefUsesCoverageCriteria(GraphCoverageCriteriaId criteria) {
		return criteria == ALL_DU_PATHS || criteria == ALL_USES || criteria == ALL_DEFS;
	}
	
	public static GraphCoverageCriteriaId valueOf(String s) {
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
}
