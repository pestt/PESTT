package coverage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;

import sourcegraph.Graph;
import sourcegraph.Path;
import statistics.StatisticsFactory;
import tour.Tour;
import view.GraphsCreator;
import constants.Description_ID;
import constants.Graph_ID;
import constants.Statistics_ID;
import constants.Tour_ID;
import editor.ActiveEditor;
import graphvisitors.ExecutedGraphVisitor;

public class CoverageInformation implements ICoverage {
	
	private Graph<Integer> sourceGraph;
	private CodeCoverage codeCoverage;
	private List<List<ICoverageData>> data;
	private List<Object> executedPaths;
	private List<Path<Integer>> coveredPaths;
	
	@SuppressWarnings("unchecked")
	public CoverageInformation(ActiveEditor editor) {
		this.sourceGraph = (Graph<Integer>) GraphsCreator.INSTANCE.getGraphs().get(Graph_ID.SOURCE_GRAPH_NUM);
		codeCoverage = new CodeCoverage(editor);
		data = codeCoverage.getCodeCoverageStatus();
		executedPaths = new LinkedList<Object>();
	}
	
	public List<Object> getExecutedPaths() {
		executedPaths.clear();
		List<Integer> toRemove = new ArrayList<Integer>();
		for(int i = 0; i < data.size(); i++) {
			ExecutedGraphVisitor<Integer> executedGraphVisitor = new ExecutedGraphVisitor<Integer>(data.get(i).get(0));
			sourceGraph.accept(executedGraphVisitor);
			Graph<Integer> executedGraph = executedGraphVisitor.getExecutedGraph();
			if(executedGraph.getNodes().size() > 0)
				executedPaths.add(executedGraph);
			else
				toRemove.add(i);
		}
		for(int i : toRemove)
			data.remove(i);
		return executedPaths;
	}
	
	public List<Path<Integer>> getCoveredTestRequirements(Object executedPath, List<Path<Integer>> testRequirements, String tourType) {	
		if(tourType == null) 
			try {
				ICommandService cmdService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class); // get the ICommandService.
				HandlerUtil.updateRadioState(cmdService.getCommand(Description_ID.TOUR_BUTTON), Tour_ID.TOUR_ID);
				tourType = Tour_ID.TOUR_ID;
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		Tour tour = new Tour(executedPaths, testRequirements);
		
		switch(Tour_ID.valueOf(tourType)) {
			case DETOUR:
				coveredPaths = tour.getDetourPathCoverage(executedPath);
				break;
			case SIDETRIP:
				coveredPaths = tour.getSidetripPathCoverage(executedPath);
				break;
			default:
				coveredPaths = tour.getTourPathCoverage(executedPath);
		}
		
		return coveredPaths;
	}
	
	public int getStatusOfRun(Object executedGraph) {			
		return executedPaths.indexOf(executedGraph);
	}
	
	public List<ICoverageData> getCoverageStatus(int index) {
		return data.get(index);
	}
	
	public List<List<ICoverageData>> getCoverageData() {
		return data;
	}
	
	public List<String> getCoverageStatistics(int index, Object executedGraph, List<Path<Integer>> testRequirements, String tour) {
		List<Object> param = new LinkedList<Object>();
		if(executedGraph instanceof String) {
			param.add(executedPaths);
			param.add(data);
			param.add(getCoveredData());
			param.add(getCoveredPaths(testRequirements, tour));
			param.add(testRequirements);
			return new StatisticsFactory().getStatisticType(Statistics_ID.TOTAL_BASIC_ID, param).getStatistics();
		} else {
			param.clear();
			param.add(executedGraph);
			param.add(data.get(index).get(0));
			param.add(coveredPaths);
			param.add(testRequirements);
			return new StatisticsFactory().getStatisticType(Statistics_ID.INDIVIDUAL_BASIC_ID, param).getStatistics();
		}
	}
	
	private List<ICoverageData> getCoveredData() {
		List<ICoverageData> aux = new LinkedList<ICoverageData>();
		for(List<ICoverageData> dataList : data)
			for(ICoverageData iData : dataList)
				aux.add(iData);
		return aux;	
	}
	
	private List<List<Path<Integer>>> getCoveredPaths(List<Path<Integer>> testRequirements, String tour) {
		List<List<Path<Integer>>> coveredPaths = new LinkedList<List<Path<Integer>>>();
		for(Object executedGraph : executedPaths)
			coveredPaths.add(getCoveredTestRequirements(executedGraph, testRequirements, tour));
		return coveredPaths;
	}
}
