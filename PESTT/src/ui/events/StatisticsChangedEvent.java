package ui.events;

import java.util.Iterator;

public class StatisticsChangedEvent {

	public final Iterator<String> statisticsSet;

	public StatisticsChangedEvent(Iterator<String> statisticsSet) {
		this.statisticsSet = statisticsSet;
	}
}
