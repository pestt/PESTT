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
			default:
				return null;
		}
	}
}
