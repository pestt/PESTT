package ui.controllers;

import java.util.Observer;
import java.util.Set;

import ui.StatisticsSet;
import adt.graph.Path;

public class StatisticsController {

	private StatisticsSet statisticsSet;

	public StatisticsController(StatisticsSet statisticsSet) {
		this.statisticsSet = statisticsSet;
	}

	public void addObserverStatistics(Observer o) {
		statisticsSet.addObserver(o);
	}

	public void getStatistics(Set<Path<Integer>> selectedTestPaths) {
		statisticsSet.getStatistics(selectedTestPaths);
	}

	public void cleanStatisticsSet() {
		statisticsSet.clean();
	}
}