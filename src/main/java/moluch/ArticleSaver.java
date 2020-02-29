package moluch;
import textworker.SaveException;
import textworker.TableSaver;

public interface ArticleSaver extends TableSaver {
    boolean setTitle(String title) throws SaveException;
    boolean addAuthor(Author author)  throws SaveException;
    boolean setText(String text)  throws SaveException;
    boolean setAnnotation(String annotation)  throws SaveException;
    boolean addTag(String tag)  throws SaveException;
    boolean addAutoTag(String tag)  throws SaveException;
    boolean setCategory(String category)  throws SaveException;
    boolean addReference(String ref)  throws SaveException;
    boolean setNumber(int num)  throws SaveException;
    boolean setYearNumber(int num)  throws SaveException;
    boolean save() throws SaveException;
}
