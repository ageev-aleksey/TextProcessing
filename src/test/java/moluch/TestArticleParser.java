package moluch;

import org.apache.commons.math3.exception.NoDataException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.util.List;
import java.util.Stack;

public class TestArticleParser {

    static public void main(String[] args) throws Exception {
        /*String html = "<p>1<em>2<b>3</b>4</em>5</p>";
        Document d = Jsoup.parseBodyFragment(html);
        Node n = d.childNode(0);
        TextGetter tg = new TextGetter(n);
        TextNode tmp = tg.next();
        while(tmp != null) {
          //  System.out.println(tmp);
            System.out.println(tmp.toString() + " (" +  tmp.parentNode().nodeName() + ")");
            tmp = tg.next();
        }*/
       /* File f = new File("./src/test/java/resource/articles/Периодические решения разностного уравнения третьего порядка _ Статья в журнале «Молодой ученый».html");
        BufferedReader fb  = new BufferedReader(new FileReader(f));
        StringBuilder buff = new StringBuilder();
        String tmp = null;
        while ((tmp = fb.readLine()) != null) {
            buff.append(tmp);
        }
        Document doc = Jsoup.parse(buff.toString());
        Elements l = doc.getElementsByTag("ol");
        l.remove();
        Elements elms = doc.getElementsByAttributeValue("itemprop", "articleBody");*/
        //============================
        /*for (String str : elms.eachText()) {
            System.out.println(str);
        }*/
        //=====-END-==========
        /*Stack<Element> stack = new Stack<Element>();
        for (Element el : elms) {
            stack.push(el);
        }
        while (!stack.isEmpty()) {
            Element cl = stack.pop();
            for (int i = cl.children().size()-1; i >= 0; i--) {
                stack.push(cl.child(i));
            }
            if (cl.tagName().equals("strong") || cl.tagName().equals("em")) {
                System.out.println(cl.tag() + " - " + cl.ownText() + "(" + cl.parent().tagName() + ")");
            } else {
                System.out.println(cl.tag());
            }

        }
        System.out.println(elms.text());
        File fr = new File("parsed_text.txt");
        fr.createNewFile();
        FileWriter fw = new FileWriter(fr);
        fw.write(elms.text());
        fw.flush();
        fw.close();*/
        


        /* String html = "<p>\n" +
                "<script async=\"\" src=\"https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js\"></script>\n" +
                "<ins class=\"adsbygoogle\" style=\"display: block; text-align: center; height: 200px;\" data-ad-layout=\"in-article\" data-ad-format=\"fluid\" data-ad-client=\"ca-pub-2904388124793132\" data-ad-slot=\"9062867921\" data-adsbygoogle-status=\"done\"><ins id=\"aswift_2_expand\" style=\"display:inline-table;border:none;height:200px;margin:0;padding:0;position:relative;visibility:visible;width:839px;background-color:transparent;\"><ins id=\"aswift_2_anchor\" style=\"display:block;border:none;height:200px;margin:0;padding:0;position:relative;visibility:visible;width:839px;background-color:transparent;\"><iframe width=\"839\" height=\"200\" frameborder=\"0\" marginwidth=\"0\" marginheight=\"0\" vspace=\"0\" hspace=\"0\" allowtransparency=\"true\" scrolling=\"no\" allowfullscreen=\"true\" onload=\"var i=this.id,s=window.google_iframe_oncopy,H=s&amp;&amp;s.handlers,h=H&amp;&amp;H[i],w=this.contentWindow,d;try{d=w.document}catch(e){}if(h&amp;&amp;d&amp;&amp;(!d.body||!d.body.firstChild)){if(h.call){setTimeout(h,0)}else if(h.match){try{h=s.upd(h,i)}catch(e){}w.location.replace(h)}}\" id=\"aswift_2\" name=\"aswift_2\" style=\"left:0;position:absolute;top:0;border:0px;width:839px;height:200px;\"></iframe></ins></ins></ins>\n" +
                "<script>\n" +
                "     (adsbygoogle = window.adsbygoogle || []).push({});\n" +
                "</script> Успешно себя зарекомендовала так называемая «Формула Мелсона» (Melson formula), впервые использованная судьей штата Делавэр Э. Мелсоном в 1989 году. Согласно формуле, необходимо из доходов родителей вычесть минимальный прожиточный минимум самих родителей, определить сумму, необходимую для удовлетворения потребностей ребенка (детей) и распределить бремя несения этих расходов между родителями пропорционально их вкладу в общий чистый доход (чистый доход –совокупный доход с вычетом прожиточного минимума) [4].\n" +
                " </p>";
        Document dom = Jsoup.parse(html);
        Elements p1 = dom.getElementsByTag("p");
        Element p =  p1.get(0);
        System.out.println(p.ownText());*/
    }
}
