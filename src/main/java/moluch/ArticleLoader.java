package moluch;

public interface ArticleLoader {
    String getTitle(String title);
    String getText(String text);
    String getAnnotation(String annotation);
    String nextAuthor(Author author);
    String getCategory(String category);
    String getReference(String ref);
    String nextTag(String tag);
    String nextAutoTag(String tag);
}
