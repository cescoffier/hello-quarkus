package me.escoffier.demo;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

@QuarkusTest
class MovieControllerTest {

    static final TypeRef<List<Movie>> LIST_OF_MOVIES = new TypeRef<List<Movie>>() {
        // Kept empty on purpose
    };

    @Test
    void test() {
        Movie m1 = new Movie();
        m1.title = "The Matrix";
        m1.rating = 5;

        Movie m2 = new Movie();
        m2.title = "The Matrix Reloaded";
        m2.rating = 4;

        Assertions.assertTrue(RestAssured
                .given()
                .header("Content-Type", "application/json")
                .get("/movies")
                .then()
                .statusCode(200)
                .extract().body().as(LIST_OF_MOVIES).isEmpty()
        );

        Assertions.assertEquals("The Matrix", RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(m1)
                .post("/movies")
                .then()
                .statusCode(200)
                .extract().body().as(Movie.class).title);

        Movie movie = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(m2)
                .post("/movies")
                .then()
                .statusCode(200)
                .extract().body().as(Movie.class);
        Assertions.assertEquals("The Matrix Reloaded", movie.title);

        Assertions.assertEquals(2, RestAssured
                .given()
                .header("Content-Type", "application/json")
                .get("/movies")
                .then()
                .statusCode(200)
                .extract().body().as(LIST_OF_MOVIES).size());

        RestAssured.given().header("Content-Type", "application/json")
                .delete("/movies/" + movie.id).then().statusCode(204);

        Assertions.assertEquals(1, RestAssured
                .given()
                .header("Content-Type", "application/json")
                .get("/movies")
                .then()
                .statusCode(200)
                .extract().body().as(LIST_OF_MOVIES).size());

    }


}