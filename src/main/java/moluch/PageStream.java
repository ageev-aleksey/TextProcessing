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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface PageStream extends Iterator<PageStream.ArticlePage> {

     class ArticlePage {
         public String articleTitle = "";
         public String articleHtml = "";
         public String articleCategory = "";
         public List<String> authorsHtml = new LinkedList<>();
         public int journalNumber = 0;
         public int journalYearNumber = 0;
         public String journalMonth = "";
    }


}
