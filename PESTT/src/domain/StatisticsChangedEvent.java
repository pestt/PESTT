package domain;

public class StatisticsChangedEvent {

	public final Object statisticsSet;
	
	public StatisticsChangedEvent(Object statisticsSet) {
		this.statisticsSet = statisticsSet;
	}
}
