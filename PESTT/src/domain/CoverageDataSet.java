package domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adt.graph.Path;
import domain.coverage.instrument.ICoverageData;


public class CoverageDataSet {

	private Map<Path<Integer>, List<ICoverageData>> coverageDataMap;

	public CoverageDataSet() {
		coverageDataMap = new HashMap<Path<Integer>, List<ICoverageData>>();
	}

	public void addCoverageData(Path<Integer> path, List<ICoverageData> data) {
		coverageDataMap.put(path, data);
	}

	public void removeCoverageData(Path<Integer> selected) {
		coverageDataMap.remove(selected);
	}
	
	public void clean() {
		coverageDataMap.clear();
	}
	
	public ICoverageData getData(Path<Integer> path) {
		return coverageDataMap.get(path).get(0);
	}
}