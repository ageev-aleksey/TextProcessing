package moluch;


import java.util.*;

import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;


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
    public ArrayList<Author> getAuthors() {
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

    /**
     * Сайт молодой ученый генерирует автоматические теги по тексту статьи. Метод возвращает список автоматических тегов
     * @return ArrayList<String> - список автоматических тегов
     */
    public ArrayList<String> getAutoTags() {
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
    public ArrayList<String> getUsersTags() {
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

    public void addReference(String ref) {
        references.add(ref);
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
            if (auto_tags != null) {
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

    private ArrayList<Author> authors = new ArrayList<Author>();
    private String text;
    private ArrayList<String> auto_tags = new ArrayList<String>();
    private ArrayList<String> users_tags = new ArrayList<String>();
    private String category;
    private String annotation;
    private ArrayList<String> references = new ArrayList<String>();
}

/**
 * Статья состоит из блоков (Аннотация, Основной текст, Список литературы). Этот интерфейс описывает объект, который
 * должен разбирать один из блоков
 */
interface ArticlePartParser {
    public void parse(Node dom, Article art);
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
                text.append(text_node.toString().trim() + " ");
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


interface TextHtmlProcessor {
    enum Result {
        SUCCESSFUL, FAILURE
    }

    /**
     *
     * @param container Хештаблица, которая заполняется пользовательскими полями
     * @param txt_getter Объект у которого выполятеся получение очередной порции текста (TextNode) из DOM
     *                   дерева посредством вызова метода next()
     * @return Статус выполненого метода. SUCCESSFUL - удачно, текущее позиция в DOM дереве документа сохраняется.
     *            FAILURE - неудача, позиция в DOM дереве возвращается к положению до вызыва метода.
     *            После выполнения метода запускаетсы выполнение следующего метода, вне зависимости от того,
     *            выполнился ли данный метод удачно или нет.
     */
    Result execute( DomConsistentRunner.Container container, DomConsistentRunner.DataGetter txt_getter);
}

/**
 * Класс оббертка для бибилотеки JSOUP, который предоставляет возможность вытаскивания текста, отделенного тегами.
 * Также предоставлает возможность обхода DOM дерева, с возвратом в исходную точку. Например, если метод выполяняте
 * движение по DOM дереву, и при этом метод завершился неудачей, возможно вернуться в ту же позицию DOM дерева, которое
 * было до выполнения метода. Или же остаться в той же позиции.
 * <p><b>Детали реализации:</b></p>
 * <p>Объект может, использовать <span color="red">много памяти</span> если обрабатываемый html документ имеет большой размер, так как выполняется
 * обход в глубину через стек (не рекурсия). Возврат к исходному состоянию реализуются путем сохранения стека ссылок
 * на непосещенных потомков.</p>
 */
class DomConsistentRunner {
    /**
     * Создание обработчкиа, который хранит в себе состояние (текущее не обработанный узел)
     * @param dom - DOM дерево html документа из которого начать вытаскивать текст.
     */
    DomConsistentRunner(Node dom) {
        methods = new ArrayList<Pair>();
        stack = new LinkedList<Node>();
        for (int i = dom.childNodes().size() - 1; i >= 0; i--) {
            stack.push(dom.childNode(i));
        }
    }
    /**
     *  Метод возращает следующий текст отделенный html тегом
     *  <pre>
     *{@code
     * Node dom = "<p>1<em>2<b>3</b></em>4</p>"
     * TextGetter tg = new TextGetter(dom);
     * //Напечатать весь текст, который содержит html страница
     * TextNode txt = new TextNode("");
     * while (txt != null) {
     *     System.out.println(txt);
     *     txt = tg.next();
     * }
     *  //OUTPUT:
     *  //1
     *  //2
     *  //3
     *  //4
     * }
     * </pre>
    */
    TextNode next() {
        while(!stack.isEmpty()) {
            String result = "";
            Node el = stack.pop();
            for (int i = el.childNodes().size() - 1; i >= 0; i--) {
                stack.push(el.childNode(i));
            }
            if (el instanceof TextNode) {
                return (TextNode)el;
            }
        }
        return null;
    }

    /**
     * регистрация объекта, который будет обрабатывать поток текста из Dom дерева html документа
     * @param method объект выполняющий обработку текста
     * @return иденификатор метода. Данный идентификатор будет добавляться к началу названия поля создаваемого объектом
     *          в хеш таблице при обработке текста. Это необходимо, поскольку разные методы могут создвать поля с
     *          одинаковым названием. Название будет формироваться по следующему шаблону:
     *          <br><b><идентификатор>_<название поля></b>
     */
    int add_method(TextHtmlProcessor method){
        int new_id = last_id++;
        methods.add(new Pair(method, new_id));
        return new_id;
    }

    /**
     * Метод запускает последовательное выполнение всех зарегистрированных обработчиков посредством метода add_method
     * @return Хештаблица создеращая поля, которые являются результатом работы запущенных методов.
     */
    HashMap<String, String[]> run() {
        HashMap<String, String[]> container = new HashMap<String, String[]>();
        DataGetter getter = new DataGetter(this);
        TextHtmlProcessor.Result res = TextHtmlProcessor.Result.SUCCESSFUL;
        for (Pair m : methods) {
            if (res == TextHtmlProcessor.Result.SUCCESSFUL) {
                stack_state = new LinkedList<Node>(stack);
            } else {
                stack = new LinkedList<Node>(stack_state);
            }
            res = m.method.execute(new Container(m.id, container), getter);
        }
        return container;
    }

    class DataGetter {
        DataGetter(DomConsistentRunner _main) {
            main = _main;
        }

        TextNode next() {
            return main.next();
        }

        private DomConsistentRunner main;
    }

    class Container {
        Container(int _id, HashMap<String, String[]> _map) {
            id = _id;
            map = _map;
        }
        public void put(String key, String[] value) {
            map.put(String.valueOf(id) + "_" + key, value);
        }
        private int id;
        private HashMap<String, String[]> map;
    }

    private class Pair {
        Pair(TextHtmlProcessor _method, int _id) {
            method = _method;
            id = _id;
        }
        public TextHtmlProcessor method;
        public int id;
    }
    private ArrayList<Pair> methods;
    private Deque<Node> stack;
    private Deque<Node> stack_state;
    private int last_id = 0;
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