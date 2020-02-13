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

public class Moluch {
    public Moluch(Logger logger) {
        this.logger = logger;
    }
    public List<Article> access(String moluch_uri_archive)
            throws URISyntaxException, IOException, InterruptedException
    {
        List<JournalNumberURI> numbers = get_references_of_journals(moluch_uri_archive);
        List<ArticleURI> articles = new LinkedList<>();
        for (JournalNumberURI n : numbers) {
            articles.addAll(get_references_of_articles(n.uri));
        }
        return Collections.emptyList();
    }
    public  List<JournalNumberURI> get_references_of_journals(String moluch_archive_url)
            throws URISyntaxException, IOException, InterruptedException
    {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(moluch_archive_url))
                .GET()
                .build();
        HttpResponse<String> response =  client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            List<JournalNumberURI> result = new LinkedList<>();
            Document root = Jsoup.parse(response.body());
            Elements elements = root.getElementsByClass("j_h");
            for(Element el : elements) {
                Elements href = el.children();
                if(href.size() == 1) {
                    JournalNumberURI jnu = new JournalNumberURI();
                    jnu.uri =moluch_archive_url + href.get(0).attr("href");
                    Matcher matcher = Pattern.compile("\\d+").matcher(href.get(0).text());
                    if(!matcher.find()) {
                        logger.warning("journal for url: " + jnu.uri +
                                "have a incorrect number: " + href.get(0).text());
                        continue;
                    }
                    jnu.number = Integer.parseInt(href.get(0).text().substring(matcher.start(), matcher.end()));
                    if(!matcher.find()) {
                        logger.warning("journal for url: " + jnu.uri +
                                "have a incorrect number: " + href.get(0).text());
                        continue;
                    }
                    jnu.numberOfYear = Integer.parseInt(href.get(0).text().substring(matcher.start(), matcher.end()));
                    jnu.uri = moluch_archive_url + String.valueOf(jnu.numberOfYear);
                    result.add(jnu);
                } else {
                    logger.warning("Invalid struct of html document. html element have a " +
                            String.valueOf(href.size()) + " children");
                }

            }
            return result;

        } else {
            return Collections.emptyList();
        }

    }

    public List<ArticleURI> get_references_of_articles(String uri_number_journal)
            throws URISyntaxException, IOException, InterruptedException
    {
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(new URI(uri_number_journal))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            List<ArticleURI> result = new LinkedList<>();
            Document root = Jsoup.parse(response.body());
            Elements elements = root.getElementsByClass("j_article_h");
            for(Element el : elements) {
                Elements href = el.children();
                if(href.size() != 1) {
                    logger.warning("article (in jurnal " +
                            uri_number_journal + ") have a incorrect html structure");
                    continue;
                }
                ArticleURI au = new ArticleURI();
                au.uri = href.get(0).attr("href");
                au.title = href.get(0).text();
                if(au.uri.equals("#")) {
                    logger.warning("article: " + au.title + " in journal: " + uri_number_journal
                    + " don't have uri");
                    continue;
                }
                result.add(au);
            }
            return result;
        }
        logger.warning(uri_number_journal +" response: " + String.valueOf(response.statusCode()));
        return Collections.emptyList();
    }
    private Logger logger;
}

class JournalNumberURI {
    public String uri;//Полная ссылка на журнал
    public int numberOfYear;//Номер журнала в этом году
    public int number;//Общий номер журнала
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("uri: ")
                .append(uri)
                .append("\n")
                .append("numberOfYear: ")
                .append(numberOfYear)
                .append("\n")
                .append("number: ")
                .append(number);
        return builder.toString();

    }
}

class ArticleURI {
    String title;
    String uri;
}
