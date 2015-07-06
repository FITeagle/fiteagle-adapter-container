package org.fiteagle.adapters.containers;

import org.fiteagle.abstractAdapter.AbstractAdapter;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;




public class DockerAdapter extends AbstractAdapter {
	
	
	Resource adapter;
	
	@Override
	public Model createInstance(String instanceURI, Model instanceModel)
			throws ProcessingException, InvalidRequestException {
		// TODO Auto-generated method stub
		
		return null;
	}

	@Override
	public void deleteInstance(String arg0) throws InstanceNotFoundException,
			InvalidRequestException, ProcessingException {
		// TODO Auto-generated method stub

	}

	@Override
	public Model getAdapterDescriptionModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource getAdapterInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource getAdapterType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Model getAllInstances() throws InstanceNotFoundException,
			ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Model getInstance(String arg0) throws InstanceNotFoundException,
			ProcessingException, InvalidRequestException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void refreshConfig() throws ProcessingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAdapterDescription() throws ProcessingException {
		// TODO Auto-generated method stub

	}

	@Override
	public Model updateInstance(String arg0, Model arg1)
			throws InvalidRequestException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}
}
