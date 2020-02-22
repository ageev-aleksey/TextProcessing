package moluch;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*
public class Moluch {
    public Moluch(Logger logger, ArticleSaver saver) {
        this.logger = logger;
        this.art_saver = saver;
    }
    public void access(String moluch_uri_archive)
            throws URISyntaxException, IOException, InterruptedException
    {
        HttpClient client = HttpClient.newBuilder().
                followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
        List<JournalNumberURI> numbers = get_references_of_journals(moluch_uri_archive, client);
        List<ArticleURI> articles = new LinkedList<>();
        for (JournalNumberURI n : numbers) {
            articles.addAll(get_references_of_articles(n.uri));
            for (ArticleURI  art_uri : articles) {
                HttpRequest request = HttpRequest.newBuilder()
                        .GET()
                        .uri(new URI(MOLUCH_JOURNAL_URL + art_uri.uri))
                        .build();
                HttpResponse<String> response = client.send(request,
                        HttpResponse.BodyHandlers.ofString());
                if(response.statusCode() != 200) {
                    logger.warning("request of uri " + art_uri.uri +
                            " returned code: " + String.valueOf(response.statusCode()));
                    continue;
                }
                try {
                    String body = response.body();
                    Article art = Article.parse_html_moluch(body);
                    List<Author> authors = parse_authors_through_page_article(response.body(), client);
                    for(Author auth : authors) {
                        art.addAuthor(auth);
                    }
                    art.setSaver(art_saver);
                   if(!art.save()) {
                       logger.warning("Error save article");
                   }
                } catch(Exception exp) {
                    logger.warning("error of parsing html page of uri: " +
                            art_uri.uri);
                }


            }
            //вытаскивание ссылки на авторов

        }

    }


    private Logger logger;
    private ArticleSaver art_saver;



}


*/