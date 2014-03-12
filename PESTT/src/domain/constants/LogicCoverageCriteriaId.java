package domain.constants;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class LogicCoverageCriteriaId extends CoverageCriteriaId {
	
	public static final LogicCoverageCriteriaId COMPLETE_CLAUSE = new LogicCoverageCriteriaId ("COMPLETE_CLAUSE");
	public static final LogicCoverageCriteriaId RESTRICTED_ACTIVE_CLAUSE = new LogicCoverageCriteriaId ("RESTRICTED_ACTIVE_CLAUSE");
	public static final LogicCoverageCriteriaId RESTRICTED_INACTIVE_CLAUSE = new LogicCoverageCriteriaId ("RESTRICTED_INACTIVE_CLAUSE");
	public static final LogicCoverageCriteriaId CORRELATED_ACTIVE_CLAUSE = new LogicCoverageCriteriaId ("CORRELATED_ACTIVE_CLAUSE");
	public static final LogicCoverageCriteriaId GENERAL_ACTIVE_CLAUSE = new LogicCoverageCriteriaId ("GENERAL_ACTIVE_CLAUSE");
	public static final LogicCoverageCriteriaId GENERAL_INACTIVE_CLAUSE = new LogicCoverageCriteriaId ("GENERAL_INACTIVE_CLAUSE");
	public static final LogicCoverageCriteriaId CLAUSE = new LogicCoverageCriteriaId ("CLAUSE");
	public static final LogicCoverageCriteriaId PREDICATE = new LogicCoverageCriteriaId ("PREDICATE");

	protected LogicCoverageCriteriaId (String name) {
		super(name);
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

}
