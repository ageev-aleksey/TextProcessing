package moluch;
import org.checkerframework.checker.units.qual.A;
import org.jsoup.nodes.TextNode;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Assertions;

import java.awt.*;
import java.io.*;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;




public class TestMoluchParser {

    @Test
    public void testSerachAndFillRussianAnnotation() throws IOException {
        File f = new File("src/test/java/resource/articles/Периодические решения разностного уравнения третьего порядка _ Статья в журнале «Молодой ученый».html");
        BufferedReader fr = new BufferedReader(new FileReader(f));
        StringBuilder buff = new StringBuilder();
        String tmp = null;
        while((tmp = fr.readLine()) != null) {
            buff.append(tmp);
        }
        Document doc = Jsoup.parse(buff.toString());
        Node node = (Node) doc.getElementsByAttributeValue("itemprop", "articleBody").get(0);
        AnnotationParser ann_parser = new AnnotationParser("ключевые слова");
        Article art = new Article();
        //ann_parser.parse(node, art);

    }

    @Test
    public void testDomConsitentRunner() {
        String html = "<div>1<p>2<em>3</em>4<b>5<em>6</em>7</b>8</div>";
        Document doc = Jsoup.parse(html);
        DomConsistentRunner runner = new DomConsistentRunner(doc.childNodes().get(0));
        runner.add_method(new test_html_parser1());
        runner.add_method(new test_html_parser2());
        HashMap<String, String[]> res = runner.run();
        Assertions.assertTrue(res.containsKey("0_first"));
        Assertions.assertArrayEquals(new String[] {"1", "2", "3", "4"}, res.get("0_first"));
        Assertions.assertTrue(res.containsKey("1_second"));
        Assertions.assertArrayEquals(new String[] {"5", "6", "7", "8"}, res.get("1_second"));
    }

    @Test
    public void testDomConsitentRunner_when_method_return_failor_restore_stack_state() {
        String html = "<div>1<p>2<em>3</em><b>5<em>6</em>7</b>8</div>";
        Document doc = Jsoup.parse(html);
        DomConsistentRunner runner = new DomConsistentRunner(doc.childNodes().get(0));
        runner.add_method(new test_html_parser1());
        runner.add_method(new test_html_parser2());
        HashMap<String, String[]> res = runner.run();
        Assertions.assertTrue(res.containsKey("1_second"));
        Assertions.assertArrayEquals(new String[] {"1", "2", "3", "5", "6", "7", "8"}, res.get("1_second"));
    }

    @Test
    public void testParseAnnotationAndKeyWords1_keywords_in_block_with_wordKEYWORD() throws IOException {
        File f = new File("src/test/java/resource/articles/Зарубежный опыт определения размера алиментов на несовершеннолетних детей_ возможности имплементации в российское законодательство _ Статья в журнале «Молодой ученый».html");
        BufferedReader fr = new BufferedReader( new FileReader(f));
        String tmp = null;
        StringBuilder data = new StringBuilder();
        while((tmp = fr.readLine()) != null) {
            data.append(tmp);
        }
        Document doc = Jsoup.parse(data.toString());
        Node node = (Node) doc.getElementsByAttributeValue("itemprop", "articleBody").get(0);
        DomConsistentRunner runner = new DomConsistentRunner(node);
        int id = runner.add_method(new AnnotationParser("ключевые слова"));
        HashMap<String, String[]> container = runner.run();
        Assertions.assertTrue(container.containsKey(String.valueOf(id) + "_annotation"));
        Assertions.assertTrue(container.containsKey(String.valueOf(id) +  "_user_key_words"));
    }

    @Test
    public void testParseAnnotationAndKeyWords2_keywords_in_other_block() throws IOException {
        File f = new File("src/test/java/resource/articles/Периодические решения разностного уравнения третьего порядка _ Статья в журнале «Молодой ученый».html");
        BufferedReader fr = new BufferedReader( new FileReader(f));
        String tmp = null;
        StringBuilder data = new StringBuilder();
        while((tmp = fr.readLine()) != null) {
            data.append(tmp);
        }
        Document doc = Jsoup.parse(data.toString());
        Node node = (Node) doc.getElementsByAttributeValue("itemprop", "articleBody").get(0);
        DomConsistentRunner runner = new DomConsistentRunner(node);
        int id = runner.add_method(new AnnotationParser("ключевые слова"));
        HashMap<String, String[]> container = runner.run();
        Assertions.assertTrue(container.containsKey(String.valueOf(id) + "_annotation"));
        Assertions.assertTrue(container.containsKey(String.valueOf(id) + "_user_key_words"));
    }

    /*@Test
    public void testParseAnnotationAndKeyWords3_keywords_notfound() throws IOException {
        File f = new File("src/test/java/resource/articles/Применение самонесущих изолированных проводов в системе электроснабжения г. Южно-Сахалинска _ Статья в журнале «Молодой ученый».html");
        BufferedReader fr = new BufferedReader( new FileReader(f));
        String tmp = null;
        StringBuilder data = new StringBuilder();
        while((tmp = fr.readLine()) != null) {
            data.append(tmp);
        }
        Document doc = Jsoup.parse(data.toString());
        Node node = (Node) doc.getElementsByAttributeValue("itemprop", "articleBody").get(0);
        DomConsistentRunner runner = new DomConsistentRunner(node);
        TextHtmlProcessor proc = new AnnotationParser("ключевые слова");
        HashMap<String, String[]> container = new HashMap<String, String[]>();
        TextHtmlProcessor.Result r = proc.execute(new runner.Container(0, container), new runner.DataGetter(runner));
        Assertions.assertTrue(r.equals(TextHtmlProcessor.Result.FAILURE));
    }*/

    @Test
    public void testParseAnnotationAndKeyWords4_keywords_on_two_language_russin_english() throws IOException {
        File f = new File("src/test/java/resource/articles/Социальная защита прав детей в Москве и Калужской области Российской Федерации _ Статья в журнале «Молодой ученый».html");
        BufferedReader fr = new BufferedReader( new FileReader(f));
        String tmp = null;
        StringBuilder data = new StringBuilder();
        while((tmp = fr.readLine()) != null) {
            data.append(tmp);
        }
        Document doc = Jsoup.parse(data.toString());
        Node node = (Node) doc.getElementsByAttributeValue("itemprop", "articleBody").get(0);
        DomConsistentRunner runner = new DomConsistentRunner(node);
        runner.add_method(new AnnotationParser("ключевые слова"));
        runner.add_method(new AnnotationParser("key words"));
        HashMap<String, String[]> container = runner.run();

    }

    @Test
    public void testParseFullText_from_html_without_annotation_and_literature() throws Exception {
        HashMap<String, StringBuilder> data = load_article("src/test/java/resource/articles/annotation and literature");

        Article moluch = Article.parse_html_moluch(data.get("article.html").toString());

        compare_article(moluch, data);
    }

    @Test
    public void testParseFullText_math_formulas() throws Exception {
        HashMap<String, StringBuilder> data = load_article("src/test/java/resource/articles/math_formulas");

        Article moluch = Article.parse_html_moluch(data.get("article.html").toString());

        compare_article(moluch, data);
    }



    public static void compare_article(Article moluch,HashMap<String, StringBuilder> data ) {
        Assertions.assertEquals(moluch.getText().trim(), data.get("text.txt").toString());
        //Assertions.assertEquals(moluch.getAnnotation().trim(), data.get("annotation.txt").toString().trim());
        // Assertions.assertEquals(moluch.getAnnotation(), data.get("annotation.txt").toString());
        for(String keyword : data.get("keywords.txt").toString().split(";")) {
            if (!in(keyword.trim().toLowerCase(), moluch.getUsersTags())) {
                Assertions.assertTrue(false);
            } else {
                continue;
            }
        }
    }
    public static boolean in(String a, Iterable<String> b) {
        for(String el : b) {
            if (el.equals(a)) {
                return true;
            }
        }
        return false;
    }

    public static HashMap<String, StringBuilder> load_article(String p) throws Exception {
        File folder = new File(p);
        if(folder.isDirectory()) {
            String[] files_name = folder.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".txt") || name.endsWith(".html");
                }
            });
            //Поиск нужных файлов
            LinkedList<String> fm = new LinkedList<String>(Arrays.asList("annotation.txt", "keywords.txt",
                    "literature.txt", "text.txt", "article.html"));
            int num_overlaps = 0;
            final int MUST_BE_OVERLAPS = fm.size();

            for(String name : files_name) {
                Iterator<String> itr = fm.iterator();
                while(itr.hasNext()) {
                    String el = itr.next();
                    if (name.equals(el)) {
                        num_overlaps++;
                        itr.remove();
                        break;
                    }
                }
            }
            if (num_overlaps == MUST_BE_OVERLAPS) {
                //Загружаем каждый файл в хеш таблицу
                fm = new LinkedList<String>(Arrays.asList("annotation.txt", "keywords.txt",
                        "literature.txt", "text.txt", "article.html"));
                HashMap<String, StringBuilder> result = new HashMap<String, StringBuilder>();
                for (String file_name : fm) {
                    String file_path = p + '/' + file_name;
                    BufferedReader f = new BufferedReader(new FileReader(file_path));
                    StringBuilder buff = new StringBuilder();
                    String tmp = null;
                    while((tmp = f.readLine()) != null) {
                        buff.append(tmp);
                    }
                    result.put(file_name, buff);
                }
            return result;
            } else {
                throw  new Exception("folder containing incorrect structure of files: " + p);
            }
        } else {
            throw new Exception("path not point on directory");
        }

    }


}


class test_html_parser1 implements TextHtmlProcessor {

    @Override
    public Result execute(DomConsistentRunner.Container container, DomConsistentRunner.DataGetter txt_getter) {
        TextNode txt = new TextNode("");
        ArrayList<String> data = new ArrayList<String>();
        while(txt != null)
        {
            txt = txt_getter.next();
            if (txt != null) {
                String tmp = txt.toString().trim();
                data.add(tmp);
                if (tmp.equals("4")) {
                    container.put("first", data.toArray(new String[data.size()]));
                    return Result.SUCCESSFUL;
                }
            }
        }

        return Result.FAILURE;
    }
}

class test_html_parser2 implements TextHtmlProcessor {

    @Override
    public Result execute(DomConsistentRunner.Container container, DomConsistentRunner.DataGetter txt_getter) {
        TextNode txt = txt_getter.next();
        ArrayList<String> data = new ArrayList<String>();
        while (txt != null) {
            data.add(txt.toString().trim());
            txt = txt_getter.next();
        }
        container.put("second", data.toArray(new String[data.size()]));
        return Result.SUCCESSFUL;
    }
}