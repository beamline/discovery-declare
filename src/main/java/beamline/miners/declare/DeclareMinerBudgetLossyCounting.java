package beamline.miners.declare;

import beamline.events.BEvent;
import beamline.miners.declare.events.budgetlossycounting.BudgetLCReplayer;
import beamline.miners.declare.model.DeclareModel;
import beamline.miners.declare.model.SimplifiedDeclareModel;
import beamline.miners.declare.view.DeclareModelView;
import beamline.models.algorithms.StreamMiningAlgorithm;

public class DeclareMinerBudgetLossyCounting extends StreamMiningAlgorithm<DeclareModelView> {

	private static final long serialVersionUID = 5043914613224009971L;
	// configuration variables
	private static int CONSTRAINT_NUMBER = 11;
	private BudgetLCReplayer replayer;
	
	private int constraintsToShow = 10;
	private int modelRefreshRate = 10;
	
	public DeclareMinerBudgetLossyCounting(int budgetSize, int constraintsToShow) {
		setBudgetSize(budgetSize);
		setConstraintsToShow(constraintsToShow);
	}
	
	public DeclareMinerBudgetLossyCounting setModelRefreshRate(int modelRefreshRate) {
		this.modelRefreshRate = modelRefreshRate;
		return this;
	}

	public void setBudgetSize(int budgetSize) {
		replayer = new BudgetLCReplayer((int) (budgetSize / CONSTRAINT_NUMBER));
	}
	
	public void setConstraintsToShow(int constraintsToShow) {
		this.constraintsToShow = constraintsToShow;
	}

	@Override
	public DeclareModelView ingest(BEvent event) {
		String caseID = event.getTraceName();
		String activityName = event.getEventName();
		
		// data structure update
		replayer.process(activityName, caseID);

		// incrementally update the model
		if (getProcessedEvents() % modelRefreshRate == 0) {
			return getModel();
		}
		
		return null;
	}

	public DeclareModelView getModel() {
		DeclareModel model = replayer.getModel();
		DeclareModel filteredModel;
		if (model.hasTraces()) {
			filteredModel = DeclareModel.filterOnTraceSupport(model, 1.0);
		} else {
			filteredModel = DeclareModel.filterOnFulfillmentRatio(model, 1.0);
		}
		DeclareModel filteredSmallModel = DeclareModel.getTopConstraints(filteredModel, constraintsToShow);

		SimplifiedDeclareModel simplifiedModel = new SimplifiedDeclareModel();
		simplifiedModel.addConstraintsFromModel(filteredSmallModel);

		return new DeclareModelView(simplifiedModel);
	}
}
