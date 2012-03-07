package domain;

import java.util.Observable;
import java.util.Observer;

public class StatisticsController extends Observable {

	private StatisticsSet statisticsSet;
	
	public StatisticsController(StatisticsSet statisticsSet) {
		this.statisticsSet = statisticsSet;
	}
	
	public void addObserverStatistics(Observer o) {
		statisticsSet.addObserver(o);
	}
	
	public void getIndividualStatistics() {
		statisticsSet.getStatsitics();
	}
	
	public void getTotalStatistics() {
		statisticsSet.getTotalStatsitics();
	}

	public void cleanStatistics() {
		statisticsSet.clean();
	}
}