package constants;

public enum CoverageAlgorithms_ID {
	
	COMPLETE_PATH,
	PRIME_PATH,
//	ALL_DU_PATHS,
	EDGE_PAIR,
	COMPLETE_ROUND_TRIP,
//	ALL_USES,
	EDGE, 
	SIMPLE_ROUND_TRIP, 
//	ALL_DEFS, 
	NODE;
	
	public static final String COMPLETE_PATH_ID = "COMPLETE_PATH";
	public static final String PRIME_PATH_ID = "PRIME_PATH";
//	public static final String ALL_DU_PATHS_ID = "ALL_DU_PATHS";
	public static final String EDGE_PAIR_ID = "EDGE_PAIR";
	public static final String COMPLETE_ROUND_TRIP_ID = "COMPLETE_ROUND_TRIP";
//	public static final String ALL_USES_ID = "ALL_USES";
	public static final String EDGE_ID = "EDGE";
	public static final String SIMPLE_ROUND_TRIP_ID = "SIMPLE_ROUND_TRIP";
//	public static final String ALL_DEFS_ID = "ALL_DEFS";
	public static final String NODE_ID = "NODE";	
	
}
