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

}
