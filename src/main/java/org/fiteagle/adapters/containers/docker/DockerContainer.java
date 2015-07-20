package org.fiteagle.adapters.containers.docker;

import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

import java.util.LinkedList;

import org.fiteagle.adapters.containers.docker.internal.DockerClient;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class DockerContainer {
	private DockerClient client;

	private Resource instanceResource;
	private String instanceIdentifier;
	LinkedList<Property> instanceProperties;

	private String image = null;
	private String command = null;

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

	public void update(Resource resource) {

	}

	public void delete() {

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

        for (Property prop: instanceProperties) {
            String localName = prop.getLocalName();

            if (localName.equals("image")) {
        		resource.addLiteral(prop, image);
            } else if (localName.equals("command")) {
        		resource.addLiteral(prop, command);
            }
        }

		return resource.getModel();
	}
}
