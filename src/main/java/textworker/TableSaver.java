package textworker;

import javax.xml.transform.TransformerConfigurationException;

public interface TableSaver {
    class Word {
        Word() {
            word = "";
            frequency = 0;
        }
        public String word;
        public double frequency;
    }
    boolean addWord(Word word);
    //Word getNextWord();
    //boolean hasNext();
    boolean save();
    //boolean load();
}
