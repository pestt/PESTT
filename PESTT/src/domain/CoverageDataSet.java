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

	private Map<Path<Integer>, List<ICoverageData>> coverageDataMap;

	public CoverageDataSet() {
		coverageDataMap = new HashMap<Path<Integer>, List<ICoverageData>>();
	}
	
	public void addObserver() {
		Activator.getDefault().getSourceGraphController().addObserverSourceGraph(this);
	}
	
	public void deleteObserver() {
		Activator.getDefault().getSourceGraphController().deleteObserverSourceGraph(this);
	}

	public void put(Path<Integer> path, List<ICoverageData> data) {
		coverageDataMap.put(path, data);
	}

	public void remove(Path<Integer> selected) {
		coverageDataMap.remove(selected);
	}
	
	public void clean() {
		coverageDataMap.clear();
		Activator.getDefault().getSourceGraphController().deleteObserverSourceGraph(this);
		
	}
	
	public ICoverageData getData(Path<Integer> path) {
		return coverageDataMap.get(path).get(0);
	}
	
	@Override
	
	public void update(Observable obs, Object data) {
		if(data instanceof CFGUpdateEvent) {
			List<Path<Integer>> keys = new ArrayList<Path<Integer>>();
			for(Path<Integer> path : coverageDataMap.keySet())
				keys.add(path);
			coverageDataMap.clear();
			for(Path<Integer> path : keys) {
				List<ICoverageData> coverageData = new LinkedList<ICoverageData>();
				coverageData.add(new CoverageData(path));
				put(path, coverageData);
			}
		}
	}
}