package wolox.training.services;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import wolox.training.services.dtos.OpenLibraryBookDTO;

@Service
public class OpenLibrary {

    private RestTemplate restTemplate;

    @Autowired
    private Environment env;

    public OpenLibrary(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    private String doIsbnRequest(String isbn) throws Exception {
        String baseUrl = env.getProperty("services.openLibraryBooksBaseUrl");
        String url = baseUrl + "/books?bibkeys=ISBN:" + isbn + "&format=json&jscmd=data";
        URL urlPath = new URL(url);
        HttpURLConnection con =(HttpURLConnection) urlPath.openConnection();
        con.setRequestMethod("GET");
        if (con.getResponseCode() != HttpURLConnection.HTTP_OK)
            return null;
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);
        in.close();

        return response.toString();
    }

    public static OpenLibraryBookDTO buildDTOFromJsonString(String isbn, String jsonString){
        JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
        JsonObject jsonBook = json.get(String.format("ISBN:%s", isbn)).getAsJsonObject();

        List<String> authors = new ArrayList<>();
        jsonBook.get("authors").getAsJsonArray().forEach(i -> {
            authors.add(i.getAsJsonObject().get("name").getAsString());
        });

        List<String> publishers = new ArrayList<>();
        jsonBook.get("publishers").getAsJsonArray().forEach(i -> {
            publishers.add(i.getAsJsonObject().get("name").getAsString());
        });
        JsonElement notes = jsonBook.get("notes");
        String subtitle = "subtitle";
        if (notes != null)
            subtitle = notes.getAsString();

        JsonElement titleJson = jsonBook.get("title");
        String title = "";
        if (titleJson != null)
            title = titleJson.getAsString();

        JsonElement publishDate = jsonBook.get("publish_date");
        String published = "";
        if (publishDate != null)
            published = publishDate.getAsString();

        JsonElement pagesJson = jsonBook.get("number_of_pages");
        int pages = 0;
        if (pagesJson != null)
            pages = pagesJson.getAsInt();

        return new OpenLibraryBookDTO(
            isbn,
            title,
            subtitle,
            publishers,
            authors,
            published,
            pages
        );
    }

    public OpenLibraryBookDTO tryGetBookByIsbn(String isbn) {
        try {
            String jsonString = doIsbnRequest(isbn);
            OpenLibraryBookDTO dto = buildDTOFromJsonString(isbn, jsonString);
            return dto;
        } catch (Exception e) {
            return null;
        }
    }
}