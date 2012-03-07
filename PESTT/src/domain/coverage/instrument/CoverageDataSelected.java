package domain.coverage.instrument;

import adt.graph.Path;

public class CoverageDataSelected {

	public final Path<Integer> selected;
	
	public CoverageDataSelected(Path<Integer> selected) {
		this.selected = selected;
	}
}
