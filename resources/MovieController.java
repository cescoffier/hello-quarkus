package me.escoffier.demo;

import io.quarkus.logging.Log;
import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.reactive.messaging.MutinyEmitter;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RunOnVirtualThread
public class MovieController {

    @Channel("movies")
    MutinyEmitter<Movie> emitter;

    @GetMapping("/movies")
    public List<Movie> getAll() {
        return Movie.listAll();
    }

    @PostMapping("/movies")
    @Transactional
    public Movie addMovie(Movie movie) {
        Log.infof("Adding a movie: %s (rating: %d)", movie.title, movie.rating);
        movie.persist();
        emitter.sendAndAwait(movie);
        return movie;
    }

    @DeleteMapping("/movies/{id}")
    @Transactional
    public Response deleteMovie(Long id) {
        Log.infof("Deleting a movie with id: %d", id);
        Movie movie = Movie.findById(id);
        if (movie != null) {
            movie.delete();
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}
