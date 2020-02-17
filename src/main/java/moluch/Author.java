package moluch;
import org.jsoup.*;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.ByteArrayInputStream;


public class Author {
    public int moluchId = -1;
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
            //если будет приставка "оглы" то добавлем ее в отчество
            if(fullname.length >= 3) {
                a.patronymic = fullname[2];
                for(int i = 3; i < fullname.length; i++) {
                    a.patronymic =  a.patronymic +  ' ' + fullname[i];
                }
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
              /*  case 2: {
                    a.academy = academic_and_status[0];
                    a.status = academic_and_status[1];
                    break;
                }*/
                default: {
                    a.academy = academic_and_status[0];
                    StringBuilder sb = new StringBuilder();
                    for(int i = 1; i < academic_and_status.length; i++) {
                        sb.append(academic_and_status[i]);
                        sb.append("\r\n");
                    }
                    a.status = sb.toString();
                }
            }
        } else {
            throw new Exception("invalid format status and acdemic of Author. ");
        }
        return  a;
    }
}
