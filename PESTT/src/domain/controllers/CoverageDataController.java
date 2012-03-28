package domain.controllers;

import java.util.List;
import java.util.Observable;

import adt.graph.Path;
import domain.CoverageDataSet;
import domain.coverage.instrument.ICoverageData;


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

	public void addCoverageData(Path<Integer> newTestPath, List<ICoverageData> newData) {
		coverageDataMap.put(newTestPath, newData);
	}

	public void removeSelectedCoverageData(Path<Integer> path) {
		coverageDataMap.remove(path);
	}
	
	public ICoverageData getCoverageData(Path<Integer> path) {
		return coverageDataMap.getData(path);
	}
	
	public void clearCoverageDataSet() {
		coverageDataMap.clean();
	}
}