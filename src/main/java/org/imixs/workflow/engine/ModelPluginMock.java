package org.imixs.workflow.engine;

import java.util.List;
import java.util.Vector;

import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.Model;
import org.imixs.workflow.bpmn.BPMNModel;
import org.imixs.workflow.exceptions.ModelException;

/**
 * The ModelPluginMock registers a subset of plugins for a junit test.
 * 
 * <code>
 * 
 * </code>
 * 
 */ 
public class ModelPluginMock extends BPMNModel {
	BPMNModel internalModel = null;
	ItemCollection internalDefinition = null;
	Vector<String> plugins = null;

	/** 
	 * this constructor changes the registered plugins
	 * 
	 */
	public ModelPluginMock(Model aModel, String... pluginList) {
		super();
		internalModel = (BPMNModel) aModel;
		// define custom plugin set....
		plugins = new Vector<String>();
		for (String sPlugin : pluginList) {
			plugins.add(sPlugin);
		}
		internalDefinition = internalModel.getDefinition();
		internalDefinition.replaceItemValue("txtplugins", plugins);
	}

	@Override
	public byte[] getRawData() {
		return internalModel.getRawData();
	}

	@Override
	public String getVersion() {
		return internalModel.getVersion();
	}

	@Override
	public ItemCollection getDefinition() {
		return internalDefinition;
	}

	@Override
	public ItemCollection getTask(int processid) throws ModelException {
		return internalModel.getTask(processid);
	}

	@Override
	public ItemCollection getEvent(int processid, int activityid) throws ModelException {
		return internalModel.getEvent(processid, activityid);
	}

	@Override
	public List<String> getGroups() {
		return internalModel.getGroups();
	}

	@Override
	public List<ItemCollection> findAllTasks() {
		return internalModel.findAllTasks();
	}

	@Override
	public List<ItemCollection> findAllEventsByTask(int processid) {
		return super.findAllEventsByTask(processid);
	}

	@Override
	public List<ItemCollection> findTasksByGroup(String group) {
		return internalModel.findTasksByGroup(group);
	}

}