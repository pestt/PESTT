package ui.constants;

public class Tooltips {

	public static final String dotString = "digraph finite_state_machine { "
			+ "rankdir=TD;" + "size=\"10,10\";" + "node [shape = box];"
			+ "CPC [label=\"Complete Path\\nCoverage\"];"
			+ "PPC [label=\"Prime Path\\nCoverage\"];"
			+ "EC [label=\"Edge\\nCoverage\"];"
			+ "ADUPC [label=\"All DU Paths\\nCoverage\"];"
			+ "AUC [label=\"All Uses\\nCoverage\"];"
			+ "CRTC [label = \"Complete Round\\nTrip Coverage\"];"
			+ "SRTC [label = \"Simple Round\\nTrip Coverage\"];"
			+ "ADC [label = \"All Defs\\nCoverage\"];"
			+ "NC [label = \"Node\\nCoverage\"];"
			+ "EPC [label = \"Edge pair\\nCoverage\"];" + "CPC -> EPC;"
			+ "CPC -> PPC;" + "PPC -> ADUPC;" + "PPC -> EC;" + "PPC -> CRTC;"
			+ "CRTC -> SRTC;" + "ADUPC -> AUC;" + "AUC -> ADC;" + "AUC -> EC;"
			+ "EPC -> EC;" + "EC -> NC;" + "}\n";
	
	// -- GRAPH --
	public static final String CPC_NODE = "Complete Path\n      Coverage\n";
	public static final String CPC = "Complete Path Coverage (CPC):"
			+ "\nTest requirements contains all paths in Graph.";
	public static final String PPC_NODE = "Prime Path\n Coverage\n";
	public static final String PPC = "Prime Path Coverage (PPC):"
			+ "\nTest requirements contains each prime path in Graph.";
	public static final String EC_NODE = "     Edge\n Coverage\n";
	public static final String EC = "Edge Coverage (EC):"
			+ "\nTest requirements contains each reachable path of length up to 1, inclusive, in Graph.";
	public static final String ADUPC_NODE = "All-du-Paths\n  Coverage\n ";
	public static final String ADUPC = "All-du-Paths Coverage (ADUPC):"
			+ "\nFor each def-pair set S = du(ni, nj, v),\nTest requirements contains every path d in S.";
	public static final String AUC_NODE = "  All-Uses\nCoverage\n";
	public static final String AUC = "All-Uses Coverage (AUC):"
			+ "\nFor each def-pair set S = du(ni, nj, v),\nTest requirements contains at least one path d in S.";
	public static final String CRTC_NODE = "Complete Round\n   Trip Coverage\n";
	public static final String CRTC = "Complete Round Trip Coverage (CRTC):"
			+ "\nTest requirements contains all round-trip paths for each reachable node in Graph.";
	public static final String SRTC_NODE = "Simple Round\nTrip Coverage\n";
	public static final String SRTC = "Simple Round Trip Coverage (SRTC):"
			+ "\nTest requirements contains at least one round-trip path\n for each reachable node in Graph that begins and ends a round-trip path.";
	public static final String ADC_NODE = "  All-Defs\nCoverage\n";
	public static final String ADC = "All-Defs Coverage (ADC):"
			+ "\nFor each def-path set S = du(n, v),\nTest requirements contains at least one path d in S.";
	public static final String EPC_NODE = "Edge-Pair\nCoverage\n";
	public static final String EPC = "Edge-Pair Coverage (EPC):"
			+ "\nTest requirements contains each reachable path of length up to 2, inclusive, in Graph.";
	public static final String NC_NODE = "    Node\nCoverage\n";
	public static final String NC = "Node Coverage (NC):"
			+ "\nTest requirements contains each reachable node in Graph.";

	// -- LOGIC --
	public static final String COC_NODE = "Complete Clause\n        Coverage\n";
	public static final String COC = "Combinatorial Coverage (or Complete Clause Coverage) (CoC):"
			+ "\nFor each p ∈ P, "
			+ "Test requirements has test requirements for the clauses in Cp "
			+ "to evaluate to each possible combination of truth values.";
	public static final String RACC_NODE = "Restricted Active\n Clause Coverage\n";
	public static final String RACC = "Restricted Active Clause Coverage (RACC):"
			+ "\nFor each p ∈ P and each major clause ci ∈ Cp, "
			+ "choose minor clauses cj, j != i so that ci determines p.\n"
			+ "Test requirements has two requirements for each ci: "
			+ "ci evaluates to true and ci evaluates to false.\n"
			+ "The values chosen for the minor clauses cj must be the same when ci is true as when ci is false.";
	public static final String RICC_NODE = "Restricted Inactive\n  Clause Coverage\n";

	public static final String RICC = "Restricted Inactive Clause Coverage (RICC):"
			+ "\nFor each p ∈ P and each major clause ci ∈ Cp, "
			+ "choose minor clauses cj, j = i so that ci does not determine p.\n"
			+ "Test requirements has four requirements for ci under these circumstances:\n"
			+ "(1) ci evaluates to true with p true,\n"
			+ "(2) ci evaluates to false with p true,\n"
			+ "(3) ci evaluates to true with p false, and\n"
			+ "(4) ci evaluates to false with p false.\n"
			+ "The values chosen for the minor clauses cj must be the same in cases (1) and (2),\n"
			+ "and the values chosen for the minor clauses cj must also be the same in cases (3) and (4).";

	public static final String CACC_NODE = "Correlated Active\n Clause Coverage\n";

	public static final String CACC = "Correlated Active Clause Coverage (CACC):"
			+ "\nFor each p ∈ P and each major clause ci ∈ C p, "
			+ "choose minor clauses cj, j = i so that ci determines p.\n"
			+ "Test requirements has two requirements for each ci: "
			+ "ci evaluates to true and ci evaluates to false.\n"
			+ "The values chosen for the minor clauses cj "
			+ "must cause p to be true for one value of the major clause ci and false for the other.";

	public static final String GACC_NODE = "  General Active\nClause Coverage\n";

	public static final String GACC = "General Active Clause Coverage (GACC):"
			+ "\nFor each p ∈ P and each major clause ci ∈ Cp, "
			+ "choose minor clauses cj, j != i so that ci determines p.\n"
			+ "Test requirements has two requirements for each ci: "
			+ "ci evaluates to true and ci evaluates to false.\n"
			+ "The values chosen for the minor clauses cj "
			+ "do not need to be the same when ci is true as when ci is false.\n";

	public static final String GICC_NODE = "General Inactive\nClause Coverage\n";

	public static final String GICC = "General Inactive Clause Coverage (GICC):"
			+ "\nFor each p ∈ P "
			+ "and each major clause ci ∈ C p, "
			+ "choose minor clauses cj, j = i so that ci does not determine p.\n"
			+ "Test requirements has four requirements for ci under these circumstances:\n "
			+ "(1) ci evaluates to true with p true,\n"
			+ "(2) ci evaluates to false with p true,\n"
			+ "(3) ci evaluates to true with p false, and\n "
			+ "(4) ci evaluates to false with p false.\n"
			+ "The values chosen for the minor clauses cj may vary amongst the four cases.";

	public static final String CC_NODE = "   Clause\nCoverage\n";

	public static final String CC = "Clause Coverage (CC):"
			+ "\nFor each c ∈ C, "
			+ "Test Requirements contains two requirements: "
			+ "c evaluates to true, and c evaluates to false.";

	public static final String PC_NODE = "Predicate\nCoverage\n";

	public static final String PC = "Predicate Coverage (PC):"
			+ "\nFor each p ∈ P, "
			+ "Test requirements contains two requirements: "
			+ "p evaluates to true, and p evaluates to false.";

}
