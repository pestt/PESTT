package domain.coverage.algorithms;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import adt.graph.Edge;
import adt.graph.Graph;
import adt.graph.Node;
import adt.graph.Path;
import adt.graph.SimplePath;
import domain.graph.visitors.DepthFirstGraphVisitor;

public class EdgeCoverage<V extends Comparable<V>> implements ICoverageAlgorithms<V> {
        
        private Graph<V> graph;
        private Set<Path<V>> paths;
        
        public EdgeCoverage(Graph<V> graph) {
                this.graph = graph;
                paths = new TreeSet<Path<V>>();
        }

        public Set<Path<V>> getTestRequirements() {
                paths.clear();
                new EdgeCoverageVisitor().visit(graph);
                paths.addAll(new NodeCoverage<V>(graph).getTestRequirements());
                return paths;
        }

        private class EdgeCoverageVisitor extends DepthFirstGraphVisitor<V> {        
                
                @SuppressWarnings({ "unchecked", "rawtypes" })
				@Override
                public boolean visit(Edge<V> edge) {
                        ArrayList<Node<V>> nodeList = new ArrayList<Node<V>>();
                        nodeList.add(edge.getBeginNode());
                        nodeList.add(edge.getEndNode());
                        paths.add(new SimplePath(nodeList));
                        return false;
                }
        }
}