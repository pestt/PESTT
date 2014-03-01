package domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import main.activator.Activator;
import adt.graph.Path;
import domain.coverage.data.CoverageData;
import domain.coverage.data.ICoverageData;
import domain.events.CFGUpdateEvent;

public class CoverageDataSet implements Observer {

	private Map<Path, List<ICoverageData>> coverageDataMap;

	public CoverageDataSet() {
		coverageDataMap = new HashMap<Path, List<ICoverageData>>();
	}

	public void addObserver() {
		Activator.getDefault().getSourceGraphController()
				.addObserverSourceGraph(this);
	}

	public void deleteObserver() {
		Activator.getDefault().getSourceGraphController()
				.deleteObserverSourceGraph(this);
	}

	public void put(Path path, List<ICoverageData> data) {
		coverageDataMap.put(path, data);
	}

	public void remove(Path selected) {
		coverageDataMap.remove(selected);
	}

	public void clean() {
		coverageDataMap.clear();
		Activator.getDefault().getSourceGraphController()
				.deleteObserverSourceGraph(this);

	}

	public ICoverageData getData(Path path) {
		return coverageDataMap.get(path).get(0);
	}

	@Override
	public void update(Observable obs, Object data) {
		if (data instanceof CFGUpdateEvent) {
			List<Path> keys = new ArrayList<Path>();
			for (Path path : coverageDataMap.keySet())
				keys.add(path);
			coverageDataMap.clear();
			for (Path path : keys) {
				List<ICoverageData> coverageData = new LinkedList<ICoverageData>();
				coverageData.add(new CoverageData(path));
				put(path, coverageData);
			}
		}
	}
}