package coveragealgorithms;

import constants.CoverageAlgorithms_ID;

public class CoverageAlgorithmsFactory<V> {

	public ICoverageAlgorithms<V> getCoverageAlgorithm(String option) {
		// verify what is the coverage to apply.
		switch(CoverageAlgorithms_ID.valueOf(option)) {
			case COMPLETE_PATH:
				return new CompletePathCoverage<V>();
			case PRIME_PATH:
				return new PrimePathCoverage<V>();
//			case ALL_DU_PATHS;
//				return new AllDuPathsCoverage<V>();
			case EDGE_PAIR:
				return new EdgePairCoverage<V>();
			case COMPLETE_ROUND_TRIP:
				return new CompleteRoundTripCoverage<V>();	
//			case ALL_USES;
//				return new AllUsesCoverage<V>();	
			case EDGE:
				return new EdgeCoverage<V>();
			case SIMPLE_ROUND_TRIP:
				return new SimpleRoundTripCoverage<V>();
//			case ALL_DEFS;
//				return new AllDefsCoverage<V>();				
			case NODE:
				return new NodeCoverage<V>();
			default:
				return null;
		}
	}
}
