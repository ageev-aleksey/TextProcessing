package moluch;
import org.jsoup.*;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.ByteArrayInputStream;


public class Author {
    public String fname = "";
    public String lname = "";
    public String patronymic = "";
    public String organiztation = "";
    public String status = "";
    public String academy = "";

    public String toString() {
        return "{fname: " + fname + "; lname: " + lname + "; patronymic: " + patronymic + "; organiztation: "
                  + organiztation + "; status: " + status + "; academy: "  + academy + "}";
    }

    public static Author parse_html_page(String page) throws Exception {
        Author a = new Author();
        // XPathExpression author_exp = xpath.compile("//div[class='page_header']/h1/text()");
        Document doc =  Jsoup.parse(page);
        String[] fullname = doc.getElementsByClass("page_header").get(0).text().split(" ");
        if (fullname.length >= 2) {
            a.fname = fullname[0];
            a.lname = fullname[1];
            if (fullname.length == 3) {
                a.patronymic = fullname[2];
            }
        } else {
            throw new Exception("invalid format full name of author");
        }

        a.organiztation = doc.getElementsByClass("text text_block").get(0).getElementsByTag("a").text();
        String[] academic_and_status  = doc.getElementsByClass("text text_block").get(0).getElementsByTag("b").text().split(",");
        if (academic_and_status.length >= 1) {
            switch(academic_and_status.length) {
                case 1: {
                    a.status = academic_and_status[0];
                    break;
                }
                case 2: {
                    a.academy = academic_and_status[0];
                    a.status = academic_and_status[1];
                    break;
                }
                default: {
                    throw new Exception("invalid format status and acdemic of Author. STATUS (acdemic and status) must be describe 1 field or 2 field");
                }
            }
        } else {
            throw new Exception("invalid format status and acdemic of Author. ");
        }
        return  a;
    }
}
