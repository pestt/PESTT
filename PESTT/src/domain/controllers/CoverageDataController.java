package domain.controllers;

import java.util.List;
import java.util.Observable;

import adt.graph.Path;
import domain.CoverageDataSet;
import domain.coverage.instrument.ICoverageData;


public class CoverageDataController extends Observable {

	private CoverageDataSet coverageDataMap;
	
	public CoverageDataController(CoverageDataSet coverageDataMap) {
		this.coverageDataMap = coverageDataMap;
	}

	public void addCoverageData(Path<Integer> newTestPath, List<ICoverageData> newData) {
		coverageDataMap.addCoverageData(newTestPath, newData);
	}

	public void removeSelectedCoverageData(Path<Integer> path) {
		coverageDataMap.removeCoverageData(path);
	}
	
	public ICoverageData getCoverageData(Path<Integer> path) {
		return coverageDataMap.getData(path);
	}
	
	public void cleanCoverageData() {
		coverageDataMap.clean();
	}
}