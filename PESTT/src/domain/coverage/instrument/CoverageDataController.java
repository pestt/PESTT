package domain.coverage.instrument;

import java.util.List;
import java.util.Observable;

import main.activator.Activator;

import adt.graph.Path;


public class CoverageDataController extends Observable {

	private CoverageDataSet coverageDataMap;
	
	public CoverageDataController(CoverageDataSet coverageDataMap) {
		this.coverageDataMap = coverageDataMap;
	}

	public void addCoverageData(Path<Integer> newTestPath, List<ICoverageData> newData) {
		coverageDataMap.addCoverageData(newTestPath, newData);
	}

	@SuppressWarnings("unchecked")
	public void removeSelectedCoverageData() {
		Path<Integer> path = (Path<Integer>) Activator.getDefault().getTestPathController().getSelectedTestPath();
		coverageDataMap.removeCoverageData(path);
	}
	
	public ICoverageData getCoverageData(Path<Integer> path) {
		return coverageDataMap.getData(path);
	}
	
	public void cleanCoverageData() {
		coverageDataMap.clean();
	}
}