package textworker;
import tech.tablesaw.api.*;
import opennlp.tools.tokenize.SimpleTokenizer;
import org.apache.lucene.morphology.russian.*;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class FDict {
    public FDict() {
        table = Table.create();
        saver = new DefaultSaverTable();
    }
    public FDict(TableSaver saver) {
        this.table = Table.create();
        this.saver = saver;
    }

    public void setSaver(TableSaver saver) {
        if(saver == null) {
            this.saver = new DefaultSaverTable();
            return;
        }
        this.saver = saver;
    }
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

    public boolean save() {
        for(int i = 0; i < table.rowCount(); i++) {
            Row row = table.row(i);
            TableSaver.Word w = new TableSaver.Word();
            w.word = row.getString("word");
            w.frequency = row.getDouble("frequency");
            if(!saver.addWord(w)) {
                return false;
            }
        }
        return saver.save();
    }

    public boolean load() {
        if(saver.load()) {
            while(saver.hasNext()) {
                TableSaver.Word w = saver.getNextWord();
                Row row = table.appendRow();
                row.setString("word", w.word);
                row.setDouble("frequency", w.frequency);
            }
            return true;
        }
        return false;
    }
   /* public Row get(int index) {
       // return table.
    }*/

    //PRIVATE
   // private bool isNotWord()

    private Table table = null;
    private TableSaver saver = null;
}

class DefaultSaverTable implements TableSaver{

    @Override
    public boolean addWord(Word word) {
        return false;
    }

    @Override
    public Word getNextWord() {
        return new Word();
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public boolean save() {
        return false;
    }

    @Override
    public boolean load() {
        return false;
    }
}