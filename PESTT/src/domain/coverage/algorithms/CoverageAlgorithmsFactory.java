package domain.coverage.algorithms;

import domain.SourceGraph;
import domain.constants.GraphCoverageCriteriaId;

public enum CoverageAlgorithmsFactory {

	INSTANCE;
	
	public ICoverageAlgorithms<Integer> getCoverageAlgorithm(SourceGraph sourceGraph, GraphCoverageCriteriaId coverageCriteria) {
		
		// verify what is the coverage to apply.
		switch(coverageCriteria) {
			case COMPLETE_PATH:
				return new CompletePathCoverage<Integer>(sourceGraph.getSourceGraph());
			case PRIME_PATH:
				return new PrimePathCoverage<Integer>(sourceGraph.getSourceGraph());
//			case ALL_DU_PATHS;
//				return new AllDuPathsCoverage<Integer>(sourceGraph.getSourceGraph());
			case EDGE_PAIR:
				return new EdgePairCoverage<Integer>(sourceGraph.getSourceGraph());
			case COMPLETE_ROUND_TRIP:
				return new CompleteRoundTripCoverage<Integer>(sourceGraph.getSourceGraph());	
//			case ALL_USES;
//				return new AllUsesCoverage<Integer>(sourceGraph.getSourceGraph());	
			case EDGE:
				return new EdgeCoverage<Integer>(sourceGraph.getSourceGraph());
			case SIMPLE_ROUND_TRIP:
				return new SimpleRoundTripCoverage<Integer>(sourceGraph.getSourceGraph());
//			case ALL_DEFS;
//				return new AllDefsCoverage<Integer>(sourceGraph.getSourceGraph());				
			case NODE:
				return new NodeCoverage<Integer>(sourceGraph.getSourceGraph());
			default:
				return null;			
		}
	}
}
