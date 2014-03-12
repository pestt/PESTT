package domain.constants;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlValue;

// abstract class because JAXB cannot handle interfaces
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class CoverageCriteriaId {
	
	@XmlValue private String name;
	
	protected CoverageCriteriaId (String name) {
		this.name = name;
	}

	// for Jaxb
	protected CoverageCriteriaId() {
	}
	
	public String getName() {
		return name;
	} 
}
