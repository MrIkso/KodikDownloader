package com.mrikso.kodikdownloader.service;

import android.util.Base64;
import android.util.Log;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KodikVideosService {

    private final String URL_PATTERN = "\"([0-9]+)p?\":\\[\\{\"src\":\"([^\"]+)";

    public KodikVideosService() {}

    private static KodikVideosService kodikVideosService;

    public static KodikVideosService getInstance() {
        if (kodikVideosService == null) {
            kodikVideosService = new KodikVideosService();
        }

        return kodikVideosService;
    }

    public String getVideos(String baseUrl) throws IOException, IOException, IOException {
        // thanks by immisterio
        // https://github.com/immisterio/Lampac/blob/51c10020f6c96de96d1501c7904ed40d3a99c697/Online/Controllers/Kodik.cs#L132

        String ifRame = loadPage("https:" + baseUrl);
        String domain = getMatcherResult("domain = \"([^\"]+)\"", ifRame, 1);
        String d_sign = getMatcherResult("d_sign = \"([^\"]+)\"", ifRame, 1);
        String pd = getMatcherResult("pd = \"([^\"]+)\"", ifRame, 1);
        String pd_sign = getMatcherResult("pd_sign = \"([^\"]+)\"", ifRame, 1);
        String ref = getMatcherResult("ref = \"([^\"]+)\"", ifRame, 1);
        String ref_sign = getMatcherResult("ref_sign = \"([^\"]+)\"", ifRame, 1);
        String type = getMatcherResult("videoInfo.type = '([^']+)'", ifRame, 1);
        String hash = getMatcherResult("videoInfo.hash = '([^']+)'", ifRame, 1);
        String id = getMatcherResult("videoInfo.id = '([^']+)'", ifRame, 1);

        String url = String.format("https://%s/gvi", domain);

        RequestBody formBody =
                new FormBody.Builder()
                        .add("d", domain)
                        .add("d_sign", d_sign)
                        .add("pd", pd)
                        .add("pd_sign", pd_sign)
                        .add("ref", "")
                        .add("bad_user", "false")
                        .add("type", type)
                        .add("hash", hash)
                        .add("id", id)
                        .build();

        // Log.i("tag", url);
        return loadPage(url, formBody);
    }

    public Map<String, String> getVideosMap(String url) {
        Map<String, String> videoLinksMap = new HashMap<>();
        try {
            String json = getVideos(url);
            Log.i("tag", json);
            Pattern pat = Pattern.compile(URL_PATTERN);
            Matcher mat = pat.matcher(json);
            while (mat.find()) {
                Log.i("tag", mat.group(1) + " =>" + decodeUrl(mat.group(2)));
                videoLinksMap.put(mat.group(1), decodeUrl(mat.group(2)));
            }

            return videoLinksMap;
        } catch (IOException io) {
            io.printStackTrace();
        }

        return videoLinksMap;
    }

    private String loadPage(String url, RequestBody formBody) throws IOException {
        OkHttpClient client = getClient();

        Request request =
                new Request.Builder()
                        .addHeader(
                                "User-Agent",
                                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36")
                        .addHeader("X-Requested-With", "XMLHttpRequest")
                        .url(url)
                        .post(formBody)
                        .build();

        Call call = client.newCall(request);
        Response response = call.execute();

        return response.body().string();
    }

    private String loadPage(String url) throws IOException {
        OkHttpClient client = getClient();

        Request request =
                new Request.Builder()
                        .addHeader(
                                "User-Agent",
                                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36")
                        .url(url)
                        .build();

        Call call = client.newCall(request);
        Response response = call.execute();

        return response.body().string();
    }

    private OkHttpClient getClient() {
        OkHttpClient client =
                new OkHttpClient.Builder()
                        .followRedirects(true)
                        .callTimeout(20L, TimeUnit.SECONDS)
                        .build();
        return client;
    }

    private String getMatcherResult(String regex, String content, int group) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        boolean found = matcher.find();
        if (found) return matcher.group(group);
        else return null;
    }

    /*
    // decoder from js script 
       	var src = "Yl9woT91MP5eo2Ecnl1mqT9lLJqyYzAioF91p2IlqKOfo2Sxpl83MQL1MGWuBF01ZQyvYGEwMwRgBTD0Al1wMwL5AmtmZmZ3AmpiMGH1ZmR4BTVkBJSxLmVlZJZ0MGV1ATEyAQHkMGSzBQL6ZwNlZmNmZwxkBP8mAwNhoKN0BzufpmcgLJ5cMzImqP5gZ3H4";

       var t;
       src = (t = src, atob(t.replace(/[a-zA-Z]/g, function(e) {
           return String.fromCharCode((e <= "Z" ? 90 : 122) >= (e = e.charCodeAt(0) + 13) ? e : e - 26)
       })));

       console.log(src);
       	*/

    private String decodeUrl(String encoded) {
        Pattern pattern = Pattern.compile("[A-Za-z]");
        StringBuffer encodedBase64 = new StringBuffer();
        Matcher matcher = pattern.matcher(encoded);
        while (matcher.find()) {
            char m = matcher.group().charAt(0);
            int temp = (int) m + 13;
            String rep =
                    Character.toString(
                            ((Character.compare(m, 'Z') <= 0 ? 90 : 122) >= temp)
                                    ? (char) temp
                                    : (char) (temp - 26));
            matcher.appendReplacement(encodedBase64, rep);
        }
        matcher.appendTail(encodedBase64);

        String url = new String(Base64.decode(encodedBase64.toString(), Base64.NO_WRAP));

        if (url.startsWith("//")) {
            url = url.replaceFirst("//", "https://");
        }
        url = url.replace(":hls:manifest.m3u8", "").replace(":hls:hls.m3u8", "");
        return url;
    }
}
