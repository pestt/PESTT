package domain.coverage.instrument;

import java.util.HashMap;

public interface ICoverageData {

	public HashMap<Integer, String> getLineStatus(); 
	
	public String getLineStatus(int line);
	
}
