package me.escoffier.demo;

import io.smallrye.mutiny.Multi;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.resteasy.reactive.RestStreamElementType;

import java.util.List;

@Path("/movies")
public class MovieStream {

    @Inject MovieRepository repository;
    @Channel("updates") Multi<Movie> updates;

    @GET
    public List<Movie> getAll() {
        return repository.getAll();
    }

    @GET
    @Path("/stream")
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<Movie> stream() {
        return updates;
    }

}
