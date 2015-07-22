package org.fiteagle.adapters.containers.docker;

import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

import java.util.logging.Logger;

import org.fiteagle.adapters.containers.docker.internal.ContainerConfiguration;
import org.fiteagle.adapters.containers.docker.internal.DockerClient;
import org.fiteagle.adapters.containers.docker.internal.DockerException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class DockerContainer {
	private final Logger logger = Logger.getLogger(getClass().getName());

	private DockerAdapter adapter;

	private Resource instanceResource;
	private String instanceIdentifier;

	private String containerID = null;
	private ContainerConfiguration containerConfig = null;

	public DockerContainer(
		DockerAdapter parent,
		String uri,
		Resource resource
	) {
		adapter = parent;

		instanceIdentifier = uri;
		instanceResource = resource;
	}

	public void update(Model updateModel) {
		Resource newState = updateModel.getResource(instanceIdentifier);

		if (newState == null || !newState.hasProperty(adapter.propConfig)) {
			logger.severe("Invalid update model");
			return;
		}

		String configURI = newState.getProperty(adapter.propConfig).getObject().asResource().getURI();
		Resource configResource = adapter.getAdapterDescriptionModel().getResource(configURI);

		ContainerConfiguration newConfig = configurationFromResource(configResource);

		if (newConfig != null && (containerID == null || containerConfig == null || !newConfig.equals(containerConfig))) {
			try {
//				This is how it should be, but we cant do it without having conflicting port maps:
//				String newID = adapter.getDockerClient().create(newConfig);
//
//				if (newID != null && !newID.isEmpty()) {
//					delete();
//
//					containerID = newID;
//					containerConfig = newConfig;
//				}

				delete();

				containerID = adapter.getDockerClient().create(newConfig);
				containerConfig = newConfig;
			} catch (DockerException e) {
				logger.throwing(DockerClient.class.getName(), "create", e);
			}
		}
	}

	public ContainerConfiguration configurationFromResource(Resource resource) {
		if (resource == null || !resource.hasProperty(adapter.propImage) || !resource.hasProperty(adapter.propCommand))
			return null;

		ContainerConfiguration config = new ContainerConfiguration(
			null,
			resource.getProperty(adapter.propImage).getObject().asLiteral().getString()
		);

		config.setCommandEasily(resource.getProperty(adapter.propCommand).getObject().asLiteral().getString());

		return config;
	}

	public void delete() {
		if (containerID == null)
			return;

		try {
			adapter.getDockerClient().delete(containerID, true, true);
		} catch (DockerException e) {
			logger.throwing(DockerClient.class.getName(), "delete", e);
		}
	}

	public Model serializeModel() {
		Resource resource = ModelFactory.createDefaultModel().createResource(instanceIdentifier);

        resource.addProperty(RDF.type, instanceResource);
        resource.addProperty(RDF.type, Omn.Resource);
        resource.addProperty(RDFS.label, resource.getLocalName());

        // Add Omn-related property
        Property property = resource.getModel().createProperty(
    		Omn_lifecycle.hasState.getNameSpace(),
    		Omn_lifecycle.hasState.getLocalName()
    	);
        property.addProperty(RDF.type, OWL.FunctionalProperty);
        resource.addProperty(property, Omn_lifecycle.Ready);

//        resource.addProperty(adapter.propConfig, "http://docker.com/schema/docker#ubuntu");

		return resource.getModel();
	}
}
