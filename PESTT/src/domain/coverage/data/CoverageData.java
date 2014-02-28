package domain.coverage.data;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import main.activator.Activator;

import org.eclipse.jdt.core.dom.ASTNode;

import ui.constants.Colors;
import ui.editor.Line;
import adt.graph.Graph;
import adt.graph.Node;
import adt.graph.Path;
import domain.constants.Layer;

public class CoverageData implements ICoverageData {

	private HashMap<Integer, String> lineStatus;

	public CoverageData(Path<Integer> executedPath) {
		lineStatus = new LinkedHashMap<Integer, String>();
		setLineStatus(executedPath);
	}

	public CoverageData(LinkedHashMap<Integer, String> lineStatus) {
		this.lineStatus = lineStatus;
	}

	public HashMap<Integer, String> getLineStatus() {
		return lineStatus;
	}

	public String getLineStatus(int line) {
		return lineStatus.get(line);
	}

	@SuppressWarnings("unchecked")
	private void setLineStatus(Path<Integer> executedPath) {
		Graph<Integer> sourceGraph = Activator.getDefault()
				.getSourceGraphController().getSourceGraph();
		sourceGraph.selectMetadataLayer(Layer.INSTRUCTIONS.getLayer()); // select the layer to get the information.
		for (Node<Integer> node : sourceGraph.getNodes()) {
			LinkedHashMap<ASTNode, Line> map = (LinkedHashMap<ASTNode, Line>) sourceGraph
					.getMetadata(node); // get the information in this layer to this node.
			if (map != null)
				for (Entry<ASTNode, Line> entry : map.entrySet()) {
					int line = entry.getValue().getStartLine();
					if (executedPath.containsNode(node))
						lineStatus.put(line, Colors.GREEN_ID);
					else if (!lineStatus.containsKey(line))
						lineStatus.put(line, Colors.RED_ID);
				}
		}
	}
}
