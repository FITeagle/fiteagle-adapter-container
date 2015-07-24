package org.fiteagle.adapters.containers.docker;

import java.util.HashSet;
import java.util.Set;

import javax.ejb.EJB;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import com.google.gson.JsonObject;

@ApplicationPath("/docker")
public class DockerApplication extends Application {
	@Path("/")
	public static class Handler {
		@EJB
		DockerAdapterControl adapterControl;

		@GET
		@Path("/instantiate-adapter")
		public Response test() {
			JsonObject resultJSON = new JsonObject();

//			adapterControl.createAdapterInstance(mod, res)

			return
				Response.ok(resultJSON.toString()).type("application/json").build();
		}
	}

	@Override
	public Set<Class<?>> getClasses() {
		HashSet<Class<?>> directingClasses = new HashSet<Class<?>>();

		directingClasses.add(Handler.class);

		return directingClasses;
	}
}
