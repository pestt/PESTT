package domain.coverage.instrument;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import main.activator.Activator;

import org.eclipse.jdt.core.dom.ASTNode;

import ui.editor.Line;
import adt.graph.Graph;
import adt.graph.Node;
import adt.graph.Path;
import domain.constants.Colors;
import domain.constants.Layer;

public class FakeCoverageData implements ICoverageData {

	private HashMap<Integer, String> lineStatus;
	
	public FakeCoverageData(Path<Integer> fakeExecutedPath) {
		lineStatus = new LinkedHashMap<Integer, String>();
		setLineStatus(fakeExecutedPath);
	}
	public FakeCoverageData(LinkedHashMap<Integer, String> fakeLineStatus) {
		lineStatus = fakeLineStatus;
	}
	
	public HashMap<Integer, String> getLineStatus() {
		return lineStatus;
	}
	
	public String getLineStatus(int line) {
		return lineStatus.get(line);
	}
	
	@SuppressWarnings("unchecked")
	private void setLineStatus(Path<Integer> fakeExecutedPath) {
		Graph<Integer> sourceGraph = Activator.getDefault().getSourceGraphController().getSourceGraph();
		sourceGraph.selectMetadataLayer(Layer.INSTRUCTIONS.getLayer()); // select the layer to get the information.
		for(Node<Integer> node : sourceGraph.getNodes()) {
			LinkedHashMap<ASTNode, Line> map = (LinkedHashMap<ASTNode, Line>) sourceGraph.getMetadata(node); // get the information in this layer to this node.
			if(map != null) 
				for(Entry<ASTNode, Line> entry : map.entrySet()) {
					int line = entry.getValue().getStartLine();
					if(fakeExecutedPath.containsNode(node)) 
						lineStatus.put(line, Colors.GRENN_ID);
					else 
						lineStatus.put(line, Colors.RED_ID);
				}
		}
	}
}
