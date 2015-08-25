package org.fiteagle.adapters.containers;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDF;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.OntologyModelUtil;

import java.util.LinkedList;
import java.util.List;

public class Playground {
	private static void extractList(List<String> output, Resource head) {
		if (head.hasProperty(RDF.first))
			output.add(head.getProperty(RDF.first).getObject().asLiteral().getString());

		if (head.hasProperty(RDF.rest))
			extractList(output, head.getProperty(RDF.rest).getObject().asResource());
	}

	public static void main(String[] args) {
		Model adapterModel = OntologyModelUtil.loadModel(
				"ontologies/docker.ttl",
				IMessageBus.SERIALIZATION_TURTLE
		);

		Property propCommand = adapterModel.getProperty(adapterModel.getNsPrefixURI("docker"), "command");

		Model m = OntologyModelUtil.loadModel("test.ttl", IMessageBus.SERIALIZATION_TURTLE);

		ResIterator rs = m.listResourcesWithProperty(propCommand);
		while (rs.hasNext()) {
			Resource r = rs.next();
			Statement c = r.getProperty(propCommand);

			Resource listHead = c.getObject().asResource();
			LinkedList<String> output = new LinkedList<String>();
			extractList(output, listHead);

			for (String elem: output) {
				System.out.println("elem: " + elem);
			}
		}
	}
}
