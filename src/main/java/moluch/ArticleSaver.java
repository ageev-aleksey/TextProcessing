package moluch;
import textworker.TableSaver;

public interface ArticleSaver extends TableSaver {
    boolean setTitle(String title);
    boolean addAuthor(Author author);
    boolean setText(String text);
    boolean setAnnotation(String annotation);
    boolean addTag(String tag);
    boolean addAutoTag(String tag);
    boolean setCategory(String category);
    boolean addReference(String ref);
    boolean setNumber(int num);
    boolean setYearNumber(int num);
    boolean save();
}
