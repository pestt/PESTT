package domain.constants;

public enum TourType {
	
	TOUR("TOUR"), 
	DETOUR("DETOUR"), 
	SIDETRIP("SIDETRIP");
	
	private final String tour;
	
	TourType(String tour) {
		this.tour = tour;
	}
	
	public String getTour() {
		return tour;
	}
	
}