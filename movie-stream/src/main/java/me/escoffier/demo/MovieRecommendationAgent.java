package me.escoffier.demo;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

import java.util.List;

@RegisterAiService
public interface MovieRecommendationAgent {


    @SystemMessage("""
            Your goal is to provide a recommendation for a movie.
            You will be given a list of movies and the user's rating for each movie.
            You will then provide a recommendation based on the user's preferences.
            Only provide the movie title.
            Do not recommend a movie that the user has already rated.
            """)
    @UserMessage("""
            Here is the list of movies and the user's ratings:
            
            {#for rating in ratings}
            - {rating.title}: {rating.rating}
            {/for}
            
            """)
    String recommend(List<Movie> ratings);
}
