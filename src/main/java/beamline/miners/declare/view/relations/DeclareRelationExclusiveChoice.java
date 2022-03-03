package beamline.miners.declare.view.relations;

import beamline.graphviz.DotEdge;
import beamline.graphviz.DotNode;

public class DeclareRelationExclusiveChoice extends DotEdge {

	public DeclareRelationExclusiveChoice(DotNode source, DotNode target) {
		super(source, target);
		
		setOption("arrowhead", "none");
		setOption("arrowtail", "none");
		setOption("label", "&#9830;");
		setOption("dir", "both");
		setOption("color", "black");
	}
}
