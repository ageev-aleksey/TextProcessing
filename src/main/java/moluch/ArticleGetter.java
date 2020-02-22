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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArticleGetter implements Iterable<Article> {

    /**
     * Объект предоставлющий доступ к статьям
     * @param journalYears - список лет, за который выдавать доступ к номерам журналов.
     *                     Если список пуст, то выдавать номера за все года.
     * @param categories - категории статей, к которым выдавать доступ. Если список пуст,
     *                   то выдавать стать всех категория
     */
    ArticleGetter(PageStream pageStream, List<Integer> journalYears, List<String> categories) {

    }

    @NotNull
    @Override
    public java.util.Iterator<Article> iterator(){
        return this.new Iterator();
    }

    @Override
    public java.util.Spliterator<Article> spliterator() {
        return null;
    }

    class Iterator implements java.util.Iterator<Article> {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Article next() {
            return null;
        }
        private java.util.Iterator<ArticleURI> aItr = null;
        private java.util.Iterator<JournalNumberURI> jItr = null;
    }




}

