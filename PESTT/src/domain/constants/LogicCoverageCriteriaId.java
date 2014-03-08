package domain.constants;

public class LogicCoverageCriteriaId extends CoverageCriteriaId {
	
	public static final LogicCoverageCriteriaId COMPLETE_CLAUSE = new LogicCoverageCriteriaId (15);
	public static final LogicCoverageCriteriaId RESTRICTED_ACTIVE_CLAUSE = new LogicCoverageCriteriaId (16);
	public static final LogicCoverageCriteriaId RESTRICTED_INACTIVE_CLAUSE = new LogicCoverageCriteriaId (17);
	public static final LogicCoverageCriteriaId CORRELATED_ACTIVE_CLAUSE = new LogicCoverageCriteriaId (18);
	public static final LogicCoverageCriteriaId GENERAL_ACTIVE_CLAUSE = new LogicCoverageCriteriaId (19);
	public static final LogicCoverageCriteriaId GENERAL_INACTIVE_CLAUSE = new LogicCoverageCriteriaId (20);
	public static final LogicCoverageCriteriaId CLAUSE = new LogicCoverageCriteriaId (21);
	public static final LogicCoverageCriteriaId PREDICATE = new LogicCoverageCriteriaId (22);

	private int id;
	
	private LogicCoverageCriteriaId (int id) {
		this.id = id;
	}
	
	protected LogicCoverageCriteriaId () {
	}
	
	
	public CoverageCriteriaId valueOf(String s) {
		if (s.equals("COMPLETE_CLAUSE"))
			return COMPLETE_CLAUSE;
		else if (s.equals("RESTRICTED_ACTIVE_CLAUSE"))
			return RESTRICTED_ACTIVE_CLAUSE;
		else if (s.equals("RESTRICTED_INACTIVE_CLAUSE"))
			return RESTRICTED_INACTIVE_CLAUSE;
		else if (s.equals("CORRELATED_ACTIVE_CLAUSE"))
			return CORRELATED_ACTIVE_CLAUSE;
		else if (s.equals("GENERAL_ACTIVE_CLAUSE"))
			return GENERAL_ACTIVE_CLAUSE;
		else if (s.equals("GENERAL_INACTIVE_CLAUSE"))
			return GENERAL_INACTIVE_CLAUSE;
		else if (s.equals("CLAUSE"))
			return CLAUSE;
		else if (s.equals("PREDICATE"))
			return PREDICATE;
		throw new IllegalArgumentException("Value " + s + " is not a valid LogicCoverageCriteriaID!");
	}

	public CoverageCriteriaId valueOf(int i) {
		if (i == 15)
			return COMPLETE_CLAUSE;
		else if (i == 16)
			return RESTRICTED_ACTIVE_CLAUSE;
		else if (i == 17)
			return RESTRICTED_INACTIVE_CLAUSE;
		else if (i == 18)
			return CORRELATED_ACTIVE_CLAUSE;
		else if (i == 19)
			return GENERAL_ACTIVE_CLAUSE;
		else if (i == 20)
			return GENERAL_INACTIVE_CLAUSE;
		else if (i == 21)
			return CLAUSE;
		else if (i == 22)
			return PREDICATE;
		throw new IllegalArgumentException("Value " + Integer.toString(i) + " is not a valid LogicCoverageCriteriaID!");
	}

}
