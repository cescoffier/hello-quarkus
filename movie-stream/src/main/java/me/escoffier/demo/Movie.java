package me.escoffier.demo;


/**
 * Represents an enriched movie
 *
 * @param title  the title of the movie
 * @param year   the publication year of the movie
 * @param rating the user rating of the movie
 * @param cover  the cover image of the movie
 */
public record Movie(String title, int year, int rating, String cover) {

}
