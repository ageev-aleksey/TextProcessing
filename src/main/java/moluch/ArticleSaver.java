package moluch;

public interface ArticleSaver {
    boolean setTitle(String title);
    boolean addAuthor(Author author);
    boolean setText(String text);
    boolean setAnnotation(String annotation);
    boolean addTag(String tag);
    boolean addAutoTag(String tag);
    boolean setCategory(String category);
    boolean addReference(String ref);
}
