package beamline.miners.declare.view;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Triple;

import beamline.graphviz.Dot;
import beamline.graphviz.DotEdge;
import beamline.graphviz.DotNode;
import beamline.miners.declare.model.SimplifiedDeclareModel;
import beamline.miners.declare.model.SimplifiedDeclareModel.RELATION;
import beamline.models.responses.GraphvizResponse;

public class DeclareModelView extends Dot implements GraphvizResponse {

	private SimplifiedDeclareModel model;
	Map<String, DotNode> activityToNode;
	
	public DeclareModelView(SimplifiedDeclareModel model) {
		this.model = model;
		this.activityToNode = new HashMap<String, DotNode>();
		
		realize();
	}
	
	private void realize() {
		for(Triple<String, String, RELATION> r : model.getRelations()) {
			addRelation(r.getLeft(), r.getMiddle(), r.getRight());
		}
	}
	
	public DotNode getNodeIfNeeded(String activity) {
		if (!activityToNode.containsKey(activity)) {
			DeclareActivity node = new DeclareActivity(activity);
			addNode(node);
			activityToNode.put(activity, node);
		}
		return activityToNode.get(activity);
	}
	
	public void addRelation(String source, String target, RELATION relation) {
		DotNode sourceNode = getNodeIfNeeded(source);
		DotNode targetNode = getNodeIfNeeded(target);
		DotEdge edge = DeclareRelationsFactory.getByType(sourceNode, targetNode, relation);
		if (edge != null) {
			addEdge(edge);
		}
	}

	@Override
	public Dot generateDot() {
		return this;
	}
}
