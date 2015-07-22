package org.fiteagle.adapters.containers.docker;

import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

import java.util.LinkedList;

import org.fiteagle.adapters.containers.docker.internal.ContainerConfiguration;
import org.fiteagle.adapters.containers.docker.internal.DockerClient;
import org.fiteagle.adapters.containers.docker.internal.DockerException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class DockerContainer {
	private static final String COMMAND_PROPERTY = "command";
	private static final String IMAGE_PROPERTY = "image";
	private static final String NAME_PROPERTY = "name";

	private DockerClient client;

	private Resource instanceResource;
	private String instanceIdentifier;
	private LinkedList<Property> instanceProperties;

	private String containerID = null;
	private String containerName = null;
	private String containerImage = null;
	private String containerCommand = null;

	public DockerContainer(
		String uri,
		Resource resource,
		LinkedList<Property> properties,
		DockerClient dockerClient
	) {
		instanceIdentifier = uri;
		instanceResource = resource;
		instanceProperties = properties;

		client = dockerClient;
	}

	public boolean update(Model resource) {
		String wantedImage = null, wantedCommand = null, wantedName = null;

		StmtIterator iter = resource.listStatements();
		while (iter.hasNext()) {
			Statement stmt = iter.next();
			if (!stmt.getSubject().getURI().equals(instanceIdentifier))
				continue;

			String localName = stmt.getPredicate().getLocalName();

			if (localName.equals(COMMAND_PROPERTY)) {
				wantedCommand = stmt.getString();
			} else if (localName.equals(IMAGE_PROPERTY)) {
				wantedImage = stmt.getString();
			} else if (localName.equals(NAME_PROPERTY)) {
				wantedName = stmt.getString();
			}
		}

		if (wantedImage == null || wantedCommand == null || wantedName == null)
			return false;

		// Check if we really need to recreate the container
		boolean newContainer = containerID == null;
		newContainer |= containerName == null    || !containerName.equals(wantedName);
		newContainer |= containerImage == null   || !containerImage.equals(wantedImage);
		newContainer |= containerCommand == null || !containerCommand.equals(wantedCommand);

		if (newContainer) {
			// Assemble configuration
			ContainerConfiguration conf = new ContainerConfiguration(wantedName, wantedImage);
			conf.setCommandEasily(wantedCommand);

			// Delete if we manage a container already
			if (containerID != null)
				delete();

			// Try to start the new container
			try {
				containerID = client.create(conf);
				return (containerID != null);
			} catch (DockerException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return true;
		}
	}

	public void delete() {
		try {
			client.delete(containerID, true, true);
		} catch (DockerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

        // Add container properties
        for (Property prop: instanceProperties) {
            String localName = prop.getLocalName();

            if (localName.equals(NAME_PROPERTY)) {
    			resource.addLiteral(prop, containerName == null ? "" : containerName);
            } else if (localName.equals(IMAGE_PROPERTY)) {
    			resource.addLiteral(prop, containerImage == null ? "" : containerImage);
            } else if (localName.equals(COMMAND_PROPERTY)) {
        		resource.addLiteral(prop, containerCommand == null ? "" : containerCommand);
            }
        }

		return resource.getModel();
	}
}
