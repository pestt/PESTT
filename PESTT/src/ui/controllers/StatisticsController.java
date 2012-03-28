package ui.controllers;

import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import ui.StatisticsSet;
import adt.graph.Path;

public class StatisticsController extends Observable {

	private StatisticsSet statisticsSet;
	
	public StatisticsController(StatisticsSet statisticsSet) {
		this.statisticsSet = statisticsSet;
	}
	
	public void addObserverStatistics(Observer o) {
		statisticsSet.addObserver(o);
	}
	
	public void getStatistics(Set<Path<Integer>> selectedTestPaths) {
		statisticsSet.getStatsitics(selectedTestPaths);
	}

	public void cleanStatisticsSet() {
		statisticsSet.clean();
	}
}