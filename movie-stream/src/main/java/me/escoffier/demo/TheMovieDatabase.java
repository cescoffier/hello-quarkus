package me.escoffier.demo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RegisterRestClient(baseUri = "https://api.themoviedb.org/3/")
public interface TheMovieDatabase {

    @GET
    @Path("/search/movie")
    @Retry(maxRetries = 4, delay = 1, delayUnit = ChronoUnit.SECONDS)
    @Timeout(1000)
    TmdbResponse search(@QueryParam("api_key") String key, @QueryParam("query") String title);


    class TmdbResponse {
        public List<MovieResponse> results;
    }

    class MovieResponse {
        public String title;
        @JsonProperty("poster_path")
        public String poster;
        @JsonProperty("release_date")
        @JsonFormat(pattern = "yyyy-MM-dd")
        public LocalDate release;
    }
}