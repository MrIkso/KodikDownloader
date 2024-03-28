package com.mrikso.kodikdownloader.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlUtils {
    
    public static String getImdbIdFromUrl(String url) {
        // IMDb URL pattern
        Pattern pattern = Pattern.compile("(?:http|https)://(?:www\\.)?imdb\\.com/title/(\\w+)/?");
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            // Extract ID from the match
            String id = matcher.group(1);

            // Remove any additional characters after the ID (e.g. query parameters)
            if (id.contains("?")) {
                id = id.substring(0, id.indexOf("?"));
            }

            return id;
        }

        // No match found
        return null;
    }

    public static String getKinopoiskIdFromUrl(String url) {
        // Kinopoisk URL pattern
        Pattern pattern =
                Pattern.compile("(?:http|https)://(?:www\\.)?kinopoisk\\.ru/film/(\\d+)/?");
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            // Extract ID from the match
            String id = matcher.group(1);

            // Remove any additional characters after the ID (e.g. query parameters)
            if (id.contains("?")) {
                id = id.substring(0, id.indexOf("?"));
            }

            return id;
        }

        // No match found
        return null;
    }

    public static String getShikimoriIdFromUrl(String url) {
        // Shikimori URL pattern
        Pattern pattern =
                Pattern.compile(
                        "(?:http|https)://(?:www\\.)?shikimori\\.(?:one|pw|rocks|moe|red)/anime/(\\d+)-[\\w-]+/?");
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            // Extract ID from the match
            String id = matcher.group(1);

            // Remove any additional characters after the ID (e.g. query parameters)
            if (id.contains("?")) {
                id = id.substring(0, id.indexOf("?"));
            }

            return id;
        }

        // No match found
        return null;
    }
}
