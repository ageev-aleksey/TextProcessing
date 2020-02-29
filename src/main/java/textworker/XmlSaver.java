package textworker;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.logging.Logger;


public class XmlSaver implements TableSaver {
    public XmlSaver(String path) throws ParserConfigurationException {
        this(path, Logger.getLogger("TableSaver.XmlSaver"));
    }
    public XmlSaver(String path, Logger log) throws ParserConfigurationException {
        this.logger = log;
        this.path = path;
        doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();
        Element root = doc.createElement("fdict");
        doc.appendChild(root);
        pointer = null;
    }
    @Override
    public boolean addWord(Word word)  throws SaveException {
        try{
            Element root = doc.getDocumentElement();
            pointer = doc.createElement("word");
            pointer.setAttribute("string", word.word);
            pointer.setAttribute("frequency", String.valueOf(word.frequency));
            root.appendChild(pointer);
        }
        catch (Exception exp) {
            logger.warning(exp.getMessage());
            throw new SaveException(exp.getMessage());
        }
        return true;
    }

   /* @Override
    public Word getNextWord() {
        Word word = new Word();
        if(!hasNext()) return word;
        try {
            word.word = pointer.getAttribute("string");
            word.frequency = Double.parseDouble(pointer.getAttribute("frequency"));
            pointer = (Element) pointer.getNextSibling();
        }
        catch(Exception exp) {
            return new Word();
        }
        return word;
    }*/

    //@Override
   /* public boolean hasNext() {
        return pointer == null;
    }*/

    @Override
    public boolean save() throws SaveException {
        try {
            DOMSource source = new DOMSource(doc);
            StreamResult file = new StreamResult(new File(path));
            TransformerFactory.newInstance()
                    .newTransformer()
                    .transform(source, file);
        }
        catch(Exception exp) {
            logger.warning(exp.getMessage());
            throw new SaveException(exp.getMessage());
        }
        return true;
    }

   /* @Override
    public boolean load() {
        try {
            doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().parse(new File(path));
        }
        catch(Exception exp) {
            return false;
        }
        return true;
    }*/

    private String path;
    private Document doc;
    private Element pointer;
    private Logger logger;
}
