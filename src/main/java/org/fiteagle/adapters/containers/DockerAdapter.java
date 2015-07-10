package org.fiteagle.adapters.containers;

import info.openmultinet.ontology.vocabulary.Omn_service;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.adapters.containers.docker.internal.ContainerConfiguration;
import org.fiteagle.adapters.containers.docker.internal.DockerClient;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;



public class DockerAdapter extends AbstractAdapter {
	
	
	Resource adapter;
	
	@Override
	public Model createInstance(String instanceURI, Model instanceModel)
			throws ProcessingException, InvalidRequestException {
		// TODO Auto-generated method stub
		
		Resource requestedCont = instanceModel.getResource(instanceURI);
		
		
		Statement userNameStatement = requestedCont.getProperty(Omn_service.username);
		String userName = userNameStatement.getObject().toString();
		Statement hosteNameStatement = requestedCont.getProperty(Omn_service.hostname);
		String hostname = hosteNameStatement.getObject().toString();
		
		Statement portStatement = requestedCont.getProperty(Omn_service.port);
		
		Statement image = requestedCont.getProperty(omn_service.)
		
		DockerClient client = new DockerClient("", 0); 
		


		ContainerConfiguration contConf = new ContainerConfiguration(instanceURI, );
		contConf.user = userName;
		
		//client.createContainer(contConf);
		
		
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
