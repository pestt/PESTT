package domain.coverage.instrument;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import adt.graph.Path;


public class CoverageDataSet extends Observable {

	private Map<Path<Integer>, List<ICoverageData>> coverageDataMap;

	public CoverageDataSet() {
		coverageDataMap = new HashMap<Path<Integer>, List<ICoverageData>>();
	}

	public void addCoverageData(Path<Integer> path, List<ICoverageData> data) {
		coverageDataMap.put(path, data);
		setChanged();
		notifyObservers(new CoverageDataChanged(coverageDataMap));
	}

	public void removeCoverageData(Path<Integer> selected) {
		coverageDataMap.remove(selected);
		setChanged();
		notifyObservers(new CoverageDataChanged(coverageDataMap));
	}
	
	public void clean() {
		coverageDataMap.clear();
		setChanged();
		notifyObservers(new CoverageDataChanged(coverageDataMap));
	}
	
	public ICoverageData getData(Path<Integer> path) {
		return coverageDataMap.get(path).get(0);
	}
}