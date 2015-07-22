package org.fiteagle.adapters.containers.docker;

import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

import java.util.logging.Logger;

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
	private final Logger logger = Logger.getLogger(getClass().getName());

	private DockerAdapter adapter;

	private Resource instanceResource;
	private String instanceIdentifier;

	public DockerContainer(
		DockerAdapter parent,
		String uri,
		Resource resource
	) {
		adapter = parent;

		instanceIdentifier = uri;
		instanceResource = resource;
	}

	public boolean update(Model resource) {
		StmtIterator stmtIter = resource.listStatements();
		while (stmtIter.hasNext()) {
			Statement stmt = stmtIter.next();

			if (!stmt.getSubject().getURI().equals(instanceIdentifier))
				continue;

			Property prop = stmt.getPredicate();
			if (prop.getURI().equals("http://docker.com/schema/docker#containerConfig")) {
				StmtIterator propIter = stmt.getResource().listProperties();
				while (propIter.hasNext()) {
					logger.info(propIter.next().getString());
				}
			}
		}

		return true;
	}

	public void delete() {
//		logger.info("[" + instanceIdentifier + "] Delete");
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

		return resource.getModel();
	}
}
