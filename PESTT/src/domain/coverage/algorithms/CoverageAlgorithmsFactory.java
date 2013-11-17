package domain.coverage.algorithms;

import domain.SourceGraph;
import domain.constants.GraphCoverageCriteriaId;

public enum CoverageAlgorithmsFactory {

	INSTANCE;
	
	public ICoverageAlgorithms<Integer> getCoverageAlgorithm(SourceGraph sourceGraph, GraphCoverageCriteriaId coverageCriteria) {
		// verify what is the coverage to apply.
		if (coverageCriteria == GraphCoverageCriteriaId.COMPLETE_PATH)
				return new CompletePathCoverage<Integer>(sourceGraph.getSourceGraph());
		else if (coverageCriteria == GraphCoverageCriteriaId.PRIME_PATH)
				return new PrimePathCoverage<Integer>(sourceGraph.getSourceGraph());
		else if (coverageCriteria == GraphCoverageCriteriaId.ALL_DU_PATHS)
				return new AllDuPathsCoverage<Integer>(sourceGraph.getSourceGraph());
		else if (coverageCriteria == GraphCoverageCriteriaId.EDGE_PAIR)
				return new EdgeNCoverage<Integer>(sourceGraph.getSourceGraph(),2);
		else if (coverageCriteria == GraphCoverageCriteriaId.COMPLETE_ROUND_TRIP)
				return new CompleteRoundTripCoverage<Integer>(sourceGraph.getSourceGraph());	
		else if (coverageCriteria == GraphCoverageCriteriaId.ALL_USES)
				return new AllUsesCoverage<Integer>(sourceGraph.getSourceGraph());	
		else if (coverageCriteria == GraphCoverageCriteriaId.EDGE)
				return new EdgeNCoverage<Integer>(sourceGraph.getSourceGraph(),1);
		else if (coverageCriteria == GraphCoverageCriteriaId.SIMPLE_ROUND_TRIP)
				return new SimpleRoundTripCoverage<Integer>(sourceGraph.getSourceGraph());
		else if (coverageCriteria == GraphCoverageCriteriaId.ALL_DEFS)
				return new AllDefsCoverage<Integer>(sourceGraph.getSourceGraph());				
		else if (coverageCriteria == GraphCoverageCriteriaId.NODE)
				return new EdgeNCoverage<Integer>(sourceGraph.getSourceGraph(),0);
		else
			return null;			
	}
}
