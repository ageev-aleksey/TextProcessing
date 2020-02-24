package moluch;

import org.jetbrains.annotations.NotNull;
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
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SiteMoluchStream implements PageStream {

    public SiteMoluchStream(List<Integer> journalYears, List<String> categories, Logger log) throws JournalPageException {
        this.logger = log;
        this.years = journalYears;
        this.categories = categories;
        //this.journals = new LinkedList<>();

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        try {
            journalsUri = get_references_of_journals(MOLUCH_ARCHIVE_URL, client, journalYears);
            //for(JournalNumberURI uri : journalsUri) {
                //add_article_uri(uri, client);
            //}
            jItr = journalsUri.iterator();
            currentJournalURI = jItr.next();
            articlesURI = get_references_of_articles(currentJournalURI.uri, client);
            aItr = articlesURI.iterator();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean hasNext() {
        if(!aItr.hasNext()) {
            return jItr.hasNext();
        }
        return true;
    }

    @Override
    public ArticlePage next()  {
        if(!aItr.hasNext()) {
            currentJournalURI = jItr.next();
            try {
                articlesURI = get_references_of_articles(currentJournalURI.uri, client);
            } catch (URISyntaxException e) {
                logger.warning("page (" + currentJournalURI.uri + ") containing invalid uris of articles page");
                return null;
            } catch (Exception e) {
                logger.warning(e.getMessage());
                return null;
            }
            aItr = articlesURI.iterator();
        }
        //TODO не установленно категоря
        ArticleURI uri = aItr.next();
        ArticlePage artPage = new ArticlePage();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(MOLUCH_URL + uri.uri))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200) {
                artPage.articleHtml = response.body();
                List<String> authrs = get_authors_page(artPage.articleHtml, client);
                for(String html : authrs) {
                    if(!"".equals(html)) {
                        artPage.authorsHtml.add(html);
                    } else {
                        logger.warning("Author page skip");
                    }
                    artPage.articleTitle = uri.title;
                    artPage.articleCategory = uri.category;
                    artPage.journalNumber = currentJournalURI.number;
                    artPage.journalYearNumber = currentJournalURI.numberOfYear;
                    artPage.journalMonth = currentJournalURI.month;
                }

            } else {
                logger.warning("Page of article ("+ uri.uri +") [" + uri.title +"] not available." +
                        "Status code: " + String.valueOf(response.statusCode()));
            }
        } catch (InterruptedException e) {
            logger.warning("connect with " + uri.uri + " was be interrupted. " + e.getMessage());
            return new ArticlePage();
        } catch (IOException e) {
            logger.warning(e.getMessage());
            return new ArticlePage();
        } catch (URISyntaxException e) {
            logger.warning("Invalid url: " + e.getMessage());
            return new ArticlePage();
        } catch (Exception e) {
            logger.warning("Error: " + e.getMessage());
            return new ArticlePage();
        }

        return artPage;

    }

//TODO каждый раз, как надо получить новую ссылку на жу
    private List<JournalNumberURI> get_references_of_journals(String moluch_archive_url, HttpClient client,
                                                              List<Integer> jurnalYears)
            throws URISyntaxException, IOException, InterruptedException
    {
        CheckYear checker = null;
        if(jurnalYears.size() == 0) {
            checker = year -> true;
        } else {
            checker = jurnalYears::contains;
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(moluch_archive_url))
                .GET()
                .build();
        HttpResponse<String> response =  client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            List<JournalNumberURI> result = new LinkedList<>();
            Document root = Jsoup.parse(response.body());
            for (Element journal_blocks : root.getElementsByClass("journals")) {
                int block_year = Integer.parseInt(journal_blocks.attr("data-block"));
                if(checker.check(block_year)) {
                    Elements elements = journal_blocks.getElementsByClass("j_h");
                    for (Element el : elements) {
                        Elements href = el.children();
                        if (href.size() == 1) {
                            JournalNumberURI jnu = new JournalNumberURI();
                            jnu.uri = moluch_archive_url + href.get(0).attr("href");
                            Matcher matcher = Pattern.compile("\\d+").matcher(href.get(0).text());
                            if (!matcher.find()) {
                                logger.warning("journal for url: " + jnu.uri +
                                        "have a incorrect number: " + href.get(0).text());
                                continue;
                            }
                            jnu.number = Integer.parseInt(href.get(0).text().substring(matcher.start(), matcher.end()));
                            if (!matcher.find()) {
                                logger.warning("journal for url: " + jnu.uri +
                                        "have a incorrect number: " + href.get(0).text());
                                continue;
                            }
                            jnu.numberOfYear = Integer.parseInt(href.get(0).text().substring(matcher.start(), matcher.end()));
                            jnu.uri = moluch_archive_url + String.valueOf(jnu.numberOfYear);
                            String[] elementsOfJournalName = href.get(0).text().split(",");
                            jnu.year = block_year;
                            jnu.month = elementsOfJournalName[elementsOfJournalName.length - 1];
                            result.add(jnu);
                        } else {
                            logger.warning("Invalid struct of html document. html element have a " +
                                    String.valueOf(href.size()) + " children");
                        }

                    }
                }

            }
            return result;

        } else {
            return Collections.emptyList();
        }

    }

    private List<ArticleURI> get_references_of_articles(String uri_number_journal, HttpClient client)
            throws URISyntaxException, IOException, InterruptedException, JournalPageException
    {

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(new URI(uri_number_journal))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            List<ArticleURI> result = new LinkedList<>();
            Document root = Jsoup.parse(response.body());

            Elements category_list = root.getElementsByClass("j_menu");
            HashMap<String, String> map = new HashMap<>();
            if(category_list.size() > 0) {
                category_list = category_list.get(0).getElementsByTag("a");
                if(category_list.size() > 0) {
                    Iterator<Element> cItr = category_list.iterator();
                    Element elm = cItr.next();
                    if(CLASS_HTML_CATEGORY.equals(elm.attr("data-block-code"))) {
                        elm = cItr.next();
                    }
                    while(true) {
                            String number = elm.attr("data-block-code");
                            String value = elm.text();
                            map.put(number, value);
                        if(!cItr.hasNext()) {
                            break;
                        }
                        elm = cItr.next();
                    }
                }
            }


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
                String key = href.get(0).parent().parent().attr("data-block");
                au.category = map.get(key);
                //au.category = TODO Доделать получение категории стать на этапе вытаскивания ссылок
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
        throw new JournalPageException("Error get page with journal number (" + uri_number_journal
                +"). Status Code: " + String.valueOf(response.statusCode()) );
    }



    private List<String> get_authors_page(String article_page, HttpClient client) throws Exception {
        Document doc = Jsoup.parse(article_page);
        Elements authors_list = doc.getElementsByAttributeValue("itemprop", "author");
        List<String> result = new LinkedList<>();
        List<String> urls = new LinkedList<>();
        for(Element author : authors_list) {
            urls.add(author.parent().attr("href"));
        }
        for(String url : urls) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(MOLUCH_URL + url))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200) {
                result.add(response.body());
            } else {
                logger.warning("Author's page (" + url
                        +") not available. Status code: "
                        + String.valueOf(response.statusCode()));
            }
        }

        return result;
    }

    /*private void add_article_uri(JournalNumberURI uri, HttpClient client) throws InterruptedException, IOException, URISyntaxException {
        List<ArticleURI> au = get_references_of_articles(uri.uri, client);
        if(categories.size() == 0) {
            JournalNumber number = new JournalNumber();
            number.articles = new LinkedList<>();
            number.articles.addAll(au);
            number.journalMonth = uri.month;
            number.journalNumber = uri.number;
            number.journalYearNumber = uri.numberOfYear;
            journals.add(number);
        } else {
            for(ArticleURI artUri : au) {
                if(categories.contains(artUri.category)) {
                    JournalNumber number = new JournalNumber();
                    number.articles = new LinkedList<>();
                    number.articles.addAll(au);
                    number.journalMonth = uri.month;
                    number.journalNumber = uri.number;
                    number.journalYearNumber = uri.numberOfYear;
                    journals.add(number);
                }
            }
        }
    }*/

    private Logger logger;
    private List<Integer> years;
    private List<String> categories;
   // private List<JournalNumber> journals;
    private List<JournalNumberURI> journalsUri;
    private Iterator<JournalNumberURI> jItr;
    private JournalNumberURI currentJournalURI;
    private List<ArticleURI>  articlesURI;
    private Iterator<ArticleURI> aItr;
    HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
   // private List<JournalNumberURI> journalsUri;


    private static final String MOLUCH_URL = "https://moluch.ru";
    private static final String MOLUCH_ARCHIVE_URL = "https://moluch.ru/archive/";
    private static final String CLASS_HTML_CATEGORY = "all";

}

class JournalNumberURI {
    public String uri;//Полная ссылка на журнал
    public int numberOfYear;//Номер журнала в этом году
    public int number;//Общий номер журнала
    public int year;
    public String month;
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
    String category;
}

class JournalNumber {
    public List<ArticleURI> articles = null;
    public int journalNumber = 0;
    public int journalYearNumber = 0;
    public String journalMonth = "";
}


interface CheckYear {
    boolean check(int year);
}

