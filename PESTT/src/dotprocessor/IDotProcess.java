package dotprocessor;
 
import java.util.ArrayList;
import java.util.Map;

/***
 * 
 * @author Rui Gameiro
 * @date 4/5/2011
 * @version 1.0
 * @project PEST
 * 
 * Interface that provides the signature of the methods responsible
  * for transforming the dot string in the dot plaintext information.
 *  
 ***/ 
public interface IDotProcess {
	
	/***
	 * 
	 * @param dotsource - The dot string.
	 * @return Scanner with information about the dot plaintext.
	 * 
	 * Method responsible for converting the dot string in plaintext.
	 * The plaintext contains the information associated with the graph elements.   
	 * 
	 ***/
	public Map<String, ArrayList<String>> DotToPlain(String dotsource);
	
}