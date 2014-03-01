package domain.controllers;

import java.util.List;
import java.util.Observable;

import adt.graph.Path;
import domain.CoverageDataSet;
import domain.coverage.data.ICoverageData;

public class CoverageDataController extends Observable {

	private CoverageDataSet coverageDataMap;

	public void addObserverToCoverageData() {
		coverageDataMap.addObserver();
	}

	public void deleteObserverToCoverageData() {
		coverageDataMap.deleteObserver();
	}

	public CoverageDataController(CoverageDataSet coverageDataMap) {
		this.coverageDataMap = coverageDataMap;
	}

	public void addCoverageData(Path newTestPath,
			List<ICoverageData> newData) {
		coverageDataMap.put(newTestPath, newData);
	}

	public void removeSelectedCoverageData(Path path) {
		coverageDataMap.remove(path);
	}

	public ICoverageData getCoverageData(Path path) {
		return coverageDataMap.getData(path);
	}

	public void clearCoverageDataSet() {
		coverageDataMap.clean();
	}
}