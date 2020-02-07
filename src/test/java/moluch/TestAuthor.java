package moluch;
import java.io.*;

public class TestAuthor {

    static public void main(String[] args) throws Exception {
        System.out.println(System.getProperty("user.dir"));
        File file = new File(".\\src\\test\\java\\resource\\author_page2.html");
        StringBuilder text = new StringBuilder();
        String tmp = null;
        //TODO что такое FileReader and BufferedReader;
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while((tmp = reader.readLine()) != null) {
            text.append(tmp);
        }
        Author author = Author.parse_html_page(text.toString());

    }
}
