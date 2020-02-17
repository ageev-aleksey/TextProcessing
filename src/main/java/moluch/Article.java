package moluch;


import java.lang.ref.Reference;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;
import textworker.FDict;


/**
 * Класс описывющий сущность статьи журнала Молодой ученый.
 * @see <a href="https://moluch.ru/">https://moluch.ru/</a>
 * @version 1.0
 */
public class Article {

    /**
     * * Метод для получения списка авторо данной статьи
     * @return ArrayList<Author> - список авторов данной статьи
     * @see Author
     */
    public List<Author> getAuthors() {
        return authors;
    }

    /**
     * * Дбавления автора к списку авторов статьи
     * @param a - Автор, для добавления
     * @see Author
     */
    public void addAuthor(Author a) {
        authors.add(a);
    }

    /**
     * Метод возвращает основной текст статьи
     * @return String - текст статьи
     */
    public String getText() {
        return text;
    }

    /**
     * Метод для установки основного текста статьи
     * @param _text - основной текст статьи
     */
    public void setText(String _text) {
        text = _text;
    }

    public void setTitle(String _title) {
        title = _title;
    }

    /**
     * Сайт молодой ученый генерирует автоматические теги по тексту статьи. Метод возвращает список автоматических тегов
     * @return ArrayList<String> - список автоматических тегов
     */
    public List<String> getAutoTags() {
        return auto_tags;
    }
    /**
     * Сайт молодой ученый генерирует автоматические теги по тексту статьи. Добавить тег к списку автоматических тегов
     * @param  tag - тег, который будет добавлен в список автоматических тегов статьи
     */
    public void addAutoTag(String tag) {
        auto_tags.add(tag);
    }

    /**
     * Метод возвращает список ключевых слов статьи (теги устанавливаемы авторами статьи)
     * @return ArrayList<String> - список ключевых слов (теги авторов)
     */
    public List<String> getUsersTags() {
        return users_tags;
    }

    /**
     * Добавление ключевых слов (тегов устанавливаемые авторами статьи) к статье
     * @param tag - тег, который будет добавлен к ключевым словам статьи.
     */
    public void addUsersTag(String tag) {
        users_tags.add(tag);
    }

    /**
     * Метод возвращающий категорию статьи
     * @return String - категория, к которой относиться статьия
     */
    public String getCategory() {
        return category;
    }

    /**
     * Установка категории статьи
     * @param _category - категория, к которой относиться статьия
     */
    public void setCategory(String _category) {
        category = _category;
    }

    /**
     * Метод возращает аннотацию статьи
     * @return String - аннотация статьи
     */
    public String getAnnotation() {
        return annotation;
    }

    /**
     * Установка аннотации к статье
     * @param ann - аннотаия статьи
     */
    public void setAnnotation(String ann) {
        annotation = ann;
    }

    public void setNumber(int num) {
        this.num = num;
    }

    public void setYearNum(int num) {
        this.yearNum = num;
    }

    public void addReference(String ref) {
        references.add(ref);
    }

    /**
     * Установка объекта, выполняющий операцию сохранения статьи в определнный формат
     * @param saver - объетк,сохранающий статью в определенном им формате
     * @return false - если пееданный указатель null, иначе true
     */
    public boolean setSaver(ArticleSaver saver) {
        if(saver == null) {
            return false;
        }
        this.saver = saver;
        return true;
    }

    public boolean save() {
        if(!saver.setTitle(title)) return false;
        if(!saver.setText(text)) return false;
        if(!saver.setAnnotation(annotation)) return false;
        if(!saver.setCategory(category)) return false;
        if(!saver.setNumber(num)) return false;
        if(!saver.setYearNumber(yearNum)) return false;
        for(Author author : authors) {
            if(!saver.addAuthor(author)) return false;
        }
        for(String ref : references) {
            if(!saver.addReference(ref)) return false;
        }
        for(String tag : users_tags) {
            if(!saver.addTag(tag)) return false;
        }
        for(String tag : auto_tags) {
            if(!saver.addTag(tag)) return false;
        }
        try{
            FDict fd = FDict.from_text(text);
            fd.setSaver(saver);
            return fd.save();
        } catch (Exception exp) {
            return false;
        }

}

    /**
     * Функция для построения объеката Article из html страницы со статьей с сайта журнала "Молодой Ученый"
     * @param page - html страница со статьей
     * @return Article - объект, описывющий статью журнала "Молодой ученый"
     * @see <a href="https://moluch.ru/">https://moluch.ru/</a>
     */
    public static Article parse_html_moluch(String page) throws Exception {
        Document doc = Jsoup.parse(page);
        Elements elements =  doc.getElementsByAttributeValue("itemprop", "articleBody");
        Article moluch = new Article();
        moluch.category = doc.getElementsByAttributeValue("itemprop", "articleSection").text().trim().toLowerCase();
        moluch.title = doc.getElementsByAttributeValue("itemprop", "headline").text().trim().toLowerCase();
        String number_in_the_text = doc.getElementsByAttributeValue("itemprop", "issueNumber").text();
        Matcher s = Pattern.compile("\\d+").matcher(number_in_the_text);
        if (s.find()) {
            moluch.num = Integer.parseInt(number_in_the_text.substring(s.start(), s.end()));
            if (s.find()) {
                moluch.yearNum = Integer.parseInt(number_in_the_text.substring(s.start(), s.end()));
            } else {
                throw new Exception("page don't containing number of journal");
            }
        }
        else {
            throw new Exception("page don't containing number of journal");
        }
        if (elements != null) {
            Node node = (Node) elements.get(0);
            //-==ПАРСИНГ СПИСКА ЛИТЕРАТУРЫ==-
            boolean list_found = false;
            List<Node> child_nodes = node.childNodes();
            int i = child_nodes.size()-1;
            for(int  j = 0; i >= 0 && j < 5; i--, j++) {
                if (child_nodes.get(i).nodeName().equals("ol")) {
                    list_found = true;
                    break;
                }
            }
            Node literature_list_paragraph = null;
            int literature_list_index = i;
            if (list_found) {
                for(int j = 0; i >= 0 && j < 5; i--, j++) {
                    Node node_element = child_nodes.get(i);
                    if (node_element.nodeName().equals("p") &&
                            ((Element)node_element).text().trim().toLowerCase().equals("литература:")) {
                        literature_list_paragraph = node_element;
                        break;
                    }
                }
            }
            if (literature_list_paragraph != null) {
                Node literature_node = child_nodes.get(literature_list_index );
                DomConsistentRunner literature_runner = new DomConsistentRunner(literature_node);
                int lindex = literature_runner.add_method(new LiteratureParser());
                HashMap<String, String[]> literature_container = literature_runner.run();
                String literature_key = String.valueOf(lindex) + "_literature";
                if(literature_container.containsKey(literature_key)) {
                    for (String ref : literature_container.get(literature_key)) {
                        moluch.addReference(ref);
                    }
                }
                literature_node.remove();
                literature_list_paragraph.remove();
            }
            //-======-

            //System.out.println(literature_list.toString());
            DomConsistentRunner text_runner = new DomConsistentRunner(node);
            int ru_annotation_id = text_runner.add_method(new AnnotationParser("ключевые слова"));
            int eng_annotation_id = text_runner.add_method(new AnnotationParser("keywords"));
            int text_id = text_runner.add_method(new TextParser());
            HashMap<String, String[]> container = text_runner.run();

            Elements auto_tags = doc.getElementsByClass("art_base_keywords");
            if ((auto_tags != null) && (auto_tags.size() > 0)) {
                Element t_element = auto_tags.get(0);
                if (t_element != null) {
                    String auto_tags_str = t_element.ownText();
                    for(String auto_tag : auto_tags_str.split("[,:]")) {
                        String tmp = auto_tag.trim().toLowerCase();
                        if (!tmp.equals("")) {
                            moluch.addAutoTag(tmp);
                        }

                    }
                }
            }

            String annotation = String.valueOf(ru_annotation_id) + "_annotation";
            if (container.containsKey(annotation)) {
                moluch.setAnnotation(container.get(annotation)[0]);
                for (String tag : container.get(String.valueOf(ru_annotation_id ) + "_user_key_words")) {
                    moluch.addUsersTag(tag);
                }
            }
            moluch.setText(container.get(String.valueOf(text_id) + "_text")[0]);
            return moluch;

        }
        throw new Exception("invalid html pages structure");
    }

    private String title;
    private List<Author> authors = new ArrayList<Author>();
    private String text;
    private List<String> auto_tags = new ArrayList<String>();
    private List<String> users_tags = new ArrayList<String>();
    private String category;
    private String annotation;
    private List<String> references = new ArrayList<String>();
    private ArticleSaver saver = new DefaultArticleSaver();
    private int num; // номер журнала за все время существования издания
    private int yearNum;/// номер журнала в текущем году
}

/**
 * Статья состоит из блоков (Аннотация, Основной текст, Список литературы). Этот интерфейс описывает объект, который
 * должен разбирать один из блоков
 */
interface ArticlePartParser {
    public void parse(Node dom, Article art);
}

class DefaultArticleSaver implements ArticleSaver {

    @Override
    public boolean setTitle(String title) {
        return false;
    }

    @Override
    public boolean addAuthor(Author author) {
        return false;
    }

    @Override
    public boolean setText(String text) {
        return false;
    }

    @Override
    public boolean setAnnotation(String annotation) {
        return false;
    }

    @Override
    public boolean addTag(String tag) {
        return false;
    }

    @Override
    public boolean addAutoTag(String tag) {
        return false;
    }

    @Override
    public boolean setCategory(String category) {
        return false;
    }

    @Override
    public boolean addReference(String ref) {
        return false;
    }

    @Override
    public boolean setNumber(int num) {
        return false;
    }

    @Override
    public boolean setYearNumber(int num) {
        return false;
    }

    @Override
    public boolean addWord(Word word) {
        return false;
    }

    @Override
    public boolean save() {
        return false;
    }
}

/**
 * Класс извлекающий аннатацию из html страницы со статьей. Алгоритм работы основан на предположении того, что после
 * аннотации всегода идут ключевые слова. Таким образом, весь текст, который расположен до ключевых слов является
 * аннотацией статьи.
 *
 * Ключевые слова определяются на основании совпадения со строкой _annotation_word_detect [параметр конструктора]
 * (для русской статьи имеет значение: "ключевые слова:"), которая обернута в html тег <em></em>
 */
class AnnotationParser implements TextHtmlProcessor {
    /**
     *
     * @param _annotation_word_detect - строка определяющая шаблон (не regex!, требуется полное совпадение)
     *                               по определению начала ключевых слов.
     */
    AnnotationParser(String _annotation_word_detect) {
        annotation_word_detect = _annotation_word_detect;
    }

    /**
     * Выполнения извелечение аннотации и ключевых слов из html страницы
     * @see TextHtmlProcessor
     */
    @Override
    public Result execute(DomConsistentRunner.Container container, DomConsistentRunner.DataGetter txt_getter) {
        int count_symbols  = 0;
        StringBuilder annotation = new StringBuilder();
        while(count_symbols <= MAX_LENGTH_ANNOTATION) {
            TextNode txt = txt_getter.next();
            if (txt == null) {
                return Result.FAILURE; //прошли весь html документ и ключевых слов не нашли
            }
            String[] words = txt.toString().trim().split(":", 2);
            if (words[0].trim().toLowerCase().equals(annotation_word_detect)) {
                //Мы нашли начало ключевх слов, а значить и обошли всю аннотацию.
                container.put("annotation", new String[] {annotation.toString().trim()});
                //вытаскиваем ключевые слова
                //Ключевые слова могут находиться как в html блоке с словом "Ключевые слова" так и в следующем html блоке
                if (words[1].length() == 0) { // в этом случае ключевые слова назодятся в следующем блоке
                    TextNode key_words = txt_getter.next();
                    if (key_words != null) {
                        String [] keywords = key_words.toString().toLowerCase().split(",");
                        for(int i = 0; i < keywords.length; i++) {
                            keywords[i] = keywords[i].trim();
                        }
                      container.put("user_key_words", keywords);
                    }
                } else { // в этом случае ключевые слова находятся в этом же блоке.
                   //TODO очистка ключевых слов от небуквенных символов
                    String[] keywords = words[1].toLowerCase().split("[,.]");
                    for(int i = 0; i < keywords.length; i++) {
                        keywords[i] = keywords[i].trim();
                    }
                    container.put("user_key_words", keywords);
                }
                return Result.SUCCESSFUL;
            } else {
                String trimmed = txt.toString().trim();
                if (!trimmed.equals("")) {
                    annotation.append(trimmed);
                    count_symbols += trimmed.length();
                }
            }
        }
        return Result.FAILURE;
    }
    private final static int MAX_LENGTH_ANNOTATION = 1500;
    private String annotation_word_detect;
}

class TextParser implements TextHtmlProcessor {

    @Override
    public Result execute(DomConsistentRunner.Container container, DomConsistentRunner.DataGetter txt_getter) {
        TextNode text_node = txt_getter.next();
        StringBuilder text = new StringBuilder();
        while (text_node  != null) {
            String tmp = text_node.toString().trim();
            if (!tmp.equals("")) {
                //text.append(text_node.toString().trim() + " ");
               /* if(text_node.hasParent() && (text_node.parent().nodeName().equals("p"))) {
                    text.append(text_node.toString().trim() + " ");
                } else {
                    text.append(text_node.toString().trim());
                }*/
                text.append(text_node.toString());
            }
            text_node = txt_getter.next();
        }
        //text.delete()
        container.put("text", new String[] {text.toString()});
        return Result.SUCCESSFUL;
    }
}

class LiteratureParser implements TextHtmlProcessor {

    @Override
    public Result execute(DomConsistentRunner.Container container, DomConsistentRunner.DataGetter txt_getter) {
        TextNode text_node = txt_getter.next();
        ArrayList<String> array = new ArrayList<String>();
        while (text_node != null) {
            String txt = text_node.toString().trim();
            if (!txt.equals("")) {
                array.add(txt);
            }
            text_node = txt_getter.next();
        }
        container.put("literature", array.toArray(new String[array.size()]));
        return Result.SUCCESSFUL;
    }
}

class ParseBody implements ArticlePartParser {


    @Override
    public void parse(Node dom, Article art) {

    }

    class _Node {
        Document curent_tag;
        int index_next_child = CHILDREN_NOT_PROCESSED;
    }
    private final static int  CHILDREN_NOT_PROCESSED = -1;
}