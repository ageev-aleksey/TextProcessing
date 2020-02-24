
import moluch.PageStream;
import moluch.SiteMoluchStream;
import textworker.FDict;
import moluch.DbSaver;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;


public class MainApp {
    public static void main(String[] args) throws Exception {

        List<Integer> years = new ArrayList<>();
        years.add(2019);
        PageStream pg = new SiteMoluchStream(years, Collections.emptyList(), Logger.getLogger("Stream"));
        int i = 1;
        Path root_path = Paths.get("articles_html");
        if(!Files.exists(root_path)) {
            Files.createDirectory(root_path);
        }
        for (PageStream it = pg; it.hasNext(); ) {
            PageStream.ArticlePage page = it.next();
            Path category_path = Paths.get(root_path.resolve( page.articleCategory).toString());
            if (!Files.exists(category_path)) {
                Files.createDirectory(category_path);
            }
            Path article_path = Paths.get(String.valueOf(category_path.resolve(String.valueOf(i))));
            Files.createDirectory(article_path);
            Path file = Files.createFile(article_path.resolve("article.html"));
            PrintWriter fwriter= new PrintWriter(new FileOutputStream(String.valueOf(file)));
            fwriter.print(page.articleHtml);
            fwriter.close();
            int author_num = 1;
            for (String author_html : page.authorsHtml) {

                file = Files.createFile(article_path.resolve("author" + String.valueOf(author_num) + ".html"));
                fwriter = new PrintWriter(new FileOutputStream(String.valueOf(file)));
                fwriter.print(author_html);
                fwriter.close();
                author_num++;
            }
            i++;
            System.out.println(i);
        }


       /* String db = "jdbc:sqlite:./sql/test2.sqlite3";
        Connection conn = DriverManager.getConnection(db);
        DbSaver db_saver  = new DbSaver(conn);
        Moluch m = new Moluch(Logger.getLogger("test"), db_saver);
        m.access("https://moluch.ru/archive/");*/

       /*String db_uri = "jdbc:sqlite:./sql/db.sqlite3";
        Connection conn = DriverManager.getConnection(db_uri);
        try{

            conn.setAutoCommit(false);
            PreparedStatement st = conn.prepareStatement("INSERT INTO article (article, annotation, bibliography) " +
                    "VALUES ('article', 'annotation', 'bibliography')", PreparedStatement.RETURN_GENERATED_KEYS);
            ResultSet rs = st.getGeneratedKeys();
            System.out.println((rs.getObject(1).getClass().getName()));
            conn.commit();
        } catch(Exception exp) {
            conn.rollback();
        }*/

      //  DbSaver.checkDbScheme(conn, "");


      /*  Moluch m = new Moluch(Logger.getLogger("test"));
        m.access("https://moluch.ru/archive/");*/

        //String test_text = "Учитывая ключевые сценарии поведения, убеждённость некоторых оппонентов не оставляет шанса для поставленных обществом задач. Господа, сплочённость команды профессионалов позволяет выполнить важные задания по разработке новых принципов формирования материально-технической и кадровой базы. Идейные соображения высшего порядка, а также реализация намеченных плановых заданий предполагает независимые способы реализации поэтапного и последовательного развития общества. В частности, высококачественный прототип будущего проекта выявляет срочную потребность модели развития. В частности, сплочённость команды профессионалов в значительной степени обусловливает важность системы массового участия. Равным образом, внедрение современных методик является качественно новой ступенью анализа существующих паттернов поведения. Не следует, однако, забывать, что повышение уровня гражданского сознания обеспечивает широкому кругу (специалистов) участие в формировании системы обучения кадров, соответствующей насущным потребностям. Разнообразный и богатый опыт говорит нам, что сплочённость команды профессионалов в значительной степени обусловливает важность вывода текущих активов. В рамках спецификации современных стандартов, действия представителей оппозиции формируют глобальную экономическую сеть и при этом - обнародованы. В целом, конечно, понимание сути ресурсосберегающих технологий создаёт необходимость включения в производственный план целого ряда внеочередных мероприятий с учётом комплекса укрепления моральных ценностей. Ясность нашей позиции очевидна: укрепление и развитие внутренней структуры обеспечивает актуальность укрепления моральных ценностей. Прежде всего, базовый вектор развития напрямую зависит от существующих финансовых и административных условий. Также как внедрение современных методик, в своём классическом представлении, допускает внедрение дальнейших направлений развития! Но независимые государства призывают нас к новым свершениям, которые, в свою очередь, должны быть объявлены нарушающими общечеловеческие нормы этики и морали. Как принято считать, некоторые особенности внутренней политики призывают нас к новым свершениям, которые, в свою очередь, должны быть указаны как претенденты на роль ключевых факторов.";
       // FDict fd = FDict.from_text(test_text);
       // fd.setSaver(new XmlSaver("test_fdict.xml"));
      //  System.out.println(fd);
        //System.out.println(fd);
       /* RussianAnalyzer morph = new RussianAnalyzer();
        RussianLuceneMorphology russ = new RussianLuceneMorphology();
        List<String> str = russ.getMorphInfo("красивое");
        String[] m = str.get(0).split("\\|");
        System.out.println(m[0]);*/
    }


}
