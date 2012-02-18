package statistics;

import java.util.List;

import constants.Statistics_ID;

public class StatisticsFactory {

	public IStatistics getStatisticType(String option, List<Object> param) {
		// verify what is the statistics to apply.
		switch(Statistics_ID.valueOf(option)) {
			case INDIVIDUAL_BASIC:
				return new IndividualBasicStatistics(param);
			case TOTAL_BASIC:
				return new TotalBasicStatistics(param);
			default:
				return null;
		}
	}
}
