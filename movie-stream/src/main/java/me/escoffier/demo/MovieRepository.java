package me.escoffier.demo;

import io.quarkus.logging.Log;
import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.reactive.messaging.MutinyEmitter;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class MovieRepository {

    // In-memory repository for demo purpose
    final Map<String, Movie> movies = new ConcurrentHashMap<>();

    @Channel("updates")
    MutinyEmitter<Movie> updates;

    @RestClient TheMovieDatabase service;
    @ConfigProperty(name = "the-movie-database.image-root") String root;
    @ConfigProperty(name = "the-movie-database.api-key") String key;

    @Incoming("movies")
    @RunOnVirtualThread
    void consume(Movie movie) {
        Log.infof("Received movie %s", movie.title());

        // Enrich, Store (title -> enriched) and send to updates if there is requests
        Movie enriched = enrich(movie);
        movies.put(movie.title(), enriched);

        if(updates.hasRequests()) {
            updates.sendAndForget(enriched);
        }
    }

    private Movie enrich(Movie movie) {
        var resp = service.search(key, movie.title());
        for (var result : resp.results) {
            if (result.title.equalsIgnoreCase(movie.title())) {
                return new Movie(result.title, result.release.getYear(), movie.rating(),
                        root + result.poster);
            }
        }
        return movie;
    }

    public List<Movie> getAll() {
        return movies.values().stream().toList();
    }

}
