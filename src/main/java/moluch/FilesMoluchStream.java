package moluch;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.CharBuffer;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilesMoluchStream implements PageStream {

    public FilesMoluchStream(Path root) throws InvalidArgument, IOException {
        this(root, Logger.getLogger("PageStream.FilesMoluchStream"));
    }

    public FilesMoluchStream(Path root, Logger logger) throws InvalidArgument, IOException {
        if(!Files.isDirectory(root)) {
            throw new InvalidArgument("path does not point to directory");
        }
        this.logger = logger;
        this.root = root;
        File folder = new File(root.toString());
        String[] folders = folder.list((path, name) -> path.isDirectory());
        if(folders == null) {
            throw new InvalidArgument("path does not containing path with categories of articles");
        }
        category = new LinkedList<>(Arrays.asList(folders));
        categoryItr = category.iterator();
        //articleItr = articlesPath.iterator();
        currentCategory = categoryItr.next();
        update_articles_paths();

    }

    @Override
    public boolean hasNext() {
        if(articleItr.hasNext()) {
            return true;
        }
        return categoryItr.hasNext();
    }

    @Override
    public ArticlePage next() {
        check_and_update_next_articles_paths();
        ArticlePage result = new ArticlePage();
        result.articleCategory = currentCategory;
        String  articleFolder =  articleItr.next();
        File folder = new File(root.toString() + '/'+ currentCategory +'/' +articleFolder);
        String[] allArticleFiles = null;
        boolean folderIsEmpty = false;
        while(!folderIsEmpty) {
            allArticleFiles = folder.list();
            if((allArticleFiles != null) && (allArticleFiles.length > 0)) {
                folderIsEmpty = true;
            } else {
                check_and_update_next_articles_paths();
            }
        }

        Pattern artPattern = Pattern.compile("article.html");
        Pattern authorPattern = Pattern.compile("author\\d+.html");
        for(String fileName : allArticleFiles) {
            Matcher artMatcher = artPattern.matcher(fileName);
            Matcher authorMatcher = authorPattern.matcher(fileName);
            if(authorMatcher.matches()) {
                try {
                    String authorPage = load_file(folder.toString()+ '/'+ fileName);
                    result.authorsHtml.add(authorPage);
                } catch (IOException e) {
                    logger.warning(e.getMessage());
                }
            } else if(artMatcher.matches()) {
                try {
                    result.articleHtml  = load_file(folder.toString()+ '/'+ fileName);

                } catch (IOException e) {
                    logger.warning( e.getMessage());
                }
            }
        }

        return result;
    }

    private void update_articles_paths() {
        File dirCategory = new File(root.toString() +'/'+ currentCategory);
        String[] articlesPaths =dirCategory.list((path, name) -> path.isDirectory());//
        if((articlesPaths != null) && (articlesPaths.length > 0)) {
            articlesPath = Arrays.asList(articlesPaths);
            articleItr = articlesPath.iterator();
        }
    }

    private void check_and_update_next_articles_paths() {
        if(!articleItr.hasNext()) {
            currentCategory = categoryItr.next();
            update_articles_paths();
        }
    }

    private String load_file(String fileName) throws IOException {
        StringBuilder buffer = new StringBuilder();
        BufferedReader bfr = new BufferedReader(new FileReader(fileName));
        String tmp = null;
        while((tmp = bfr.readLine()) != null) {
            buffer.append(tmp);
        }
        bfr.close();
        return String.valueOf(buffer);
    }

    private List<String> category;
    private List<String> articlesPath;
    private Path root;
    private Iterator<String> articleItr;
    private Iterator<String> categoryItr;
    private String currentCategory;
    private Logger logger;
}
