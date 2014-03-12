package domain.constants;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public enum TourType {

	TOUR("TOUR"), DETOUR("DETOUR"), SIDETRIP("SIDETRIP");

	private final String tour;

	TourType(String tour) {
		this.tour = tour;
	}

	public String getTour() {
		return tour;
	}

}