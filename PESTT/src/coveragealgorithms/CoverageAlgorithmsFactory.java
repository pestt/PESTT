package coveragealgorithms;

import constants.CoverageAlgorithms_ID;

public class CoverageAlgorithmsFactory<V> {

	public ICoverageAlgorithms<V> getCoverageAlgorithm(String option) {
		// verify what is the coverage to apply.
		switch(CoverageAlgorithms_ID.valueOf(option)) {
			case NODE:
				return new NodeCoverage<V>();
			case EDGE:
				return new EdgeCoverage<V>();
			case EDGE_PAIR:
				return new EdgePairCoverage<V>();
			case PRIME_PATH:
				return new PrimePathCoverage<V>();
			case SIMPLE_ROUND_TRIP:
				return new SimpleRoundTripCoverage<V>();
			case COMPLETE_ROUND_TRIP:
				return new CompleteRoundTripCoverage<V>();
			default:
				return null;
		}
	}
}
