package me.escoffier.demo;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.quarkiverse.langchain4j.ToolBox;


@RegisterAiService
public interface MovieRecommendationAgent {

    @SystemMessage("""
            You are a movie recommendation assistant.
            You MUST use the provided tool to retrieve the list of movies rated by the user.
            From that list, pick the movie whose genre and theme best match the requested mood.
            The mood match is the MOST important criterion. The user's rating is secondary.
            Do NOT always pick the highest-rated movie. Prefer a lower-rated movie if it is a better mood match.
            You MUST vary your recommendations - do not always pick the same movie.
            """)
    @UserMessage("""
            The user is in a {mood} mood. Pick the best matching movie from their rated list.
            Think about what genre and themes fit a {mood} mood, then select accordingly.
            """)
    @ToolBox(MovieRepository.class)
    Movie recommend(String mood);
}
