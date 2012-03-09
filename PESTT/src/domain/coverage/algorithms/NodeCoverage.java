package domain.coverage.algorithms;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import adt.graph.Graph;
import adt.graph.Node;
import adt.graph.Path;
import adt.graph.SimplePath;

public class NodeCoverage<V extends Comparable<V>> implements ICoverageAlgorithms<V> {

        private Graph<V> graph;
        
        public NodeCoverage(Graph<V> graph) {
                this.graph = graph;
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
		public Set<Path<V>> getTestRequirements() {
                Set<Path<V>> paths = new TreeSet<Path<V>>();
                for (Node<V> node : graph.getNodes()) {
                        ArrayList<Node<V>> nodeList = new ArrayList<Node<V>>();
                        nodeList.add(node);
                        paths.add(new SimplePath(nodeList));
                }
                return paths;
        }
}