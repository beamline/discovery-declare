package beamline.miners.declare.events.budgetlossycounting;

import java.util.ArrayList;
import java.util.List;

import beamline.miners.declare.events.budgetlossycounting.constraints.AlternatePrecedence;
import beamline.miners.declare.events.budgetlossycounting.constraints.AlternateResponse;
import beamline.miners.declare.events.budgetlossycounting.constraints.AlternateSuccession;
import beamline.miners.declare.events.budgetlossycounting.constraints.ChainPrecedence;
import beamline.miners.declare.events.budgetlossycounting.constraints.ChainResponse;
import beamline.miners.declare.events.budgetlossycounting.constraints.ChainSuccession;
import beamline.miners.declare.events.budgetlossycounting.constraints.CoExistence;
import beamline.miners.declare.events.budgetlossycounting.constraints.Precedence;
import beamline.miners.declare.events.budgetlossycounting.constraints.RespondedExistence;
import beamline.miners.declare.events.budgetlossycounting.constraints.Response;
import beamline.miners.declare.events.budgetlossycounting.constraints.Succession;
import beamline.miners.declare.model.DeclareModel;

public class BudgetLCReplayer {
	
	List<BudgetLCTemplateReplayer> replayers = new ArrayList<BudgetLCTemplateReplayer>();
	
	public BudgetLCReplayer(int budget) {
		replayers.add(new AlternatePrecedence(budget));
		replayers.add(new AlternateResponse(budget));
		replayers.add(new ChainPrecedence(budget));
		replayers.add(new ChainResponse(budget));
		replayers.add(new CoExistence(budget));
		replayers.add(new Precedence(budget));
		replayers.add(new RespondedExistence(budget));
		replayers.add(new Response(budget));
		replayers.add(new Succession(budget));
		
		// new constraints
		replayers.add(new AlternateSuccession(budget));
		replayers.add(new ChainSuccession(budget));
	}
	
	/**
	 * 
	 * @param event
	 * @param caseId
	 */
	public void process(String event, String caseId) {
		for(BudgetLCTemplateReplayer t : replayers) {
			try {
				t.process(event, caseId);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public DeclareModel getModel() {
		DeclareModel d = new DeclareModel();
		for(BudgetLCTemplateReplayer t : replayers) {
			t.updateModel(d);
		}
		return d;
	}
	
	/**
	 * 
	 * @return
	 */
	public Integer getSize() {
		Integer i = 0;
		for(BudgetLCTemplateReplayer t : replayers) {
			i += t.getSize();
		}
		return i;
	}
}
