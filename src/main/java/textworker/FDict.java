package textworker;
import tech.tablesaw.api.*;
import opennlp.tools.tokenize.SimpleTokenizer;
import org.apache.lucene.morphology.russian.*;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class FDict {


    public void append_row(String word, double frequency) {
        Row new_row = table.appendRow();
        new_row.setString("word", word);
        new_row.setDouble("frequency", frequency);
    }

    public static FDict from_text(final String text) throws IOException {
        String[] words = SimpleTokenizer.INSTANCE.tokenize(text);
        RussianLuceneMorphology morph = new RussianLuceneMorphology();
        for (int i = 0; i < words.length; i++) {
            if (words[i].matches("[\\W\\d!#$%&\\\\'\\(\\)\\*\\+,-\\./:;<=>?@[\\\\]^_`{\\|}~]")) {
                words[i] = "";
            } else {
                words[i] = morph.getMorphInfo(words[i].toLowerCase()).get(0).split("\\|")[0];
            }
        }
        StringColumn word_col = StringColumn.create("word", words);
        Table tmp = word_col.countByCategory();
        tmp.column("Category").setName("word");
        tmp.column("Count").setName("frequency");
        tmp = tmp.dropWhere(((StringColumn)tmp.column("word")).isEqualTo(""));
        double num_words = ((IntColumn)tmp.column("frequency")).sum();
        DoubleColumn tmp_col = ((IntColumn)tmp.column("frequency")).divide(num_words).setName("frequency");
        tmp.removeColumns(tmp.column("frequency"));
        tmp.addColumns(tmp_col);
        FDict fdict = new FDict();
        fdict.table = tmp;
        return fdict;
    }

    public String toString() {
        table = table.sortOn("-frequency");
        return table.print();
    }

   /* public Row get(int index) {
       // return table.
    }*/

    //PRIVATE
   // private bool isNotWord()

    private Table table = null;
}
