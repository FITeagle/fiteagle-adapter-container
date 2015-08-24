package org.fiteagle.adapters.containers.docker;

import java.util.HashSet;
import java.util.Set;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;
import org.fiteagle.abstractAdapter.AbstractAdapter;

import com.google.gson.JsonObject;

@ApplicationPath("/docker")
public class DockerApplication extends Application {
	public static class AdapterConfigWrapper {
		private String id, hostname;
		private int port;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getHostname() {
			return hostname;
		}

		public void setHostname(String hostname) {
			this.hostname = hostname;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int p) {
			this.port = p;
		}
	}

	@Path("/")
	public static class Handler {
		@EJB
		DockerAdapterControl adapterControl;

		@GET
		@Path("/adapters")
		public Response listAdapters() {
			JsonArray resultArray = new JsonArray();

			for (AbstractAdapter aa: adapterControl.getAdapterInstances()) {
				JsonObject adapterObject = new JsonObject();
				adapterObject.addProperty("id", aa.getId());
				adapterObject.addProperty("uri", aa.getAdapterABox().getURI());

				resultArray.add(adapterObject);
			}

			return Response.ok(resultArray.toString()).status(200).build();
		}

//		@POST
//		@Path("/adapters")
//		@Consumes(MediaType.APPLICATION_JSON)
//		public Response createAdapter(AdapterConfigWrapper data) {
//			JsonObject resultJSON = new JsonObject();
//
//			try {
//				AbstractAdapter aa = adapterControl.createAdapterInstance(
//					data.getId(), data.getHostname(), data.getPort()
//				);
//
//				return Response.ok(aa.getId()).status(200).build();
//			} catch (DockerControlException e) {
//				return Response.ok(e.getMessage()).status(500).build();
//			}
//		}

//		@POST
//		@Path("/adapters/{id}/delete")
//		public Response deleteAdapter(@PathParam("id") String id) {
//			return Response.ok().build();
//		}

		@GET
		@Path("/adapters/{id}/instances")
		public Response findAdapterInstances(@PathParam("id") String id) {
			try {
				for (AbstractAdapter aa : adapterControl.getAdapterInstances()) {
					if (!aa.getId().equals(id))
						continue;

					JsonArray returnArray = new JsonArray();
					ResIterator iter = aa.getAllInstances().listSubjectsWithProperty(Omn_lifecycle.hasState);

					while (iter.hasNext()) {
						returnArray.add(new JsonPrimitive(iter.next().getURI()));
					}

					return Response.ok(returnArray.toString()).build();
				}
			} catch (AbstractAdapter.ProcessingException e) {
				e.printStackTrace();
			} catch (AbstractAdapter.InstanceNotFoundException e) {
				e.printStackTrace();
			}

			return Response.status(404).build();
		}
	}

	@Override
	public Set<Class<?>> getClasses() {
		HashSet<Class<?>> directingClasses = new HashSet<Class<?>>();

		directingClasses.add(Handler.class);

		return directingClasses;
	}
}
