package moluch;

import org.jetbrains.annotations.NotNull;
import textworker.TableSaver;

import javax.sql.RowSet;
import java.sql.*;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;

public class DbSaver implements ArticleSaver {
    public DbSaver(Connection dbConnection) throws Exception {
        if (!dbConnection.isValid(10)) {
            throw new Exception("Db connection must be opened");
        }
        db = dbConnection;
       // db.setAutoCommit(false);
    }
    //TODO доделать метод провреки на существование соответсвующих таблиц в базе данных.
    public static boolean checkDbScheme(Connection dbConnection, String dbScheme) throws Exception{
        try {
            if (!dbConnection.isValid(10)) {
                throw new Exception("Db connection must be opened");
            }
            PreparedStatement st = dbConnection.prepareStatement("SELECT * FROM status LIMIT 1");
           // st.setString(1, dbScheme);
            if(st.execute()) {
                ResultSet rs = st.getResultSet();
                ResultSetMetaData md = rs.getMetaData();
                if(((md.getColumnCount() == 1)  )) {
                    if(!md.getColumnName(1).equals("status"))
                        return false;
                }
            }
            return true;
        } catch(SQLException exp ) {
            return false;
        }

    }


    @Override
    public boolean addWord(Word word) {
        if(word == null) {
            return false;
        }
        String res = (String) insert_to_data_base("word", "word", word.word);
        if(res == ERROR_INSERTING_IN_DB) {
            return false;
        }
        words.add(word);
        return true;
    }

    @Override
    public boolean save() {
        if(title == null || article == null || annotation == null || authors_id.size() == 0 ||
        reference.length() == 0 || num == VALUE_NOT_SET || num_year == VALUE_NOT_SET) {
            return false;
        }
        try{
           PreparedStatement st = db.prepareStatement("INSERT INTO article (article, annotation, bibliography)" +
                    "VALUES ( ?, ?, ? )", PreparedStatement.RETURN_GENERATED_KEYS);
            st.setString(1, article);
            st.setString(2, annotation);
            st.setString(3, reference.toString());
            if(st.executeUpdate() != 1) {
                //db.rollback();
                return false;
            }
            int art_id = st.getGeneratedKeys().getInt(1);
            st = db.prepareStatement(
                    "INSERT INTO info (id, title, category, num_year, num)" +
                            "VALUES ( ?, ?, ?, ?, ? )",
                    PreparedStatement.RETURN_GENERATED_KEYS);
            st.setInt(1, art_id);
            st.setString(2, title);
            st.setString(3, category_id);
            st.setInt(4, num_year);
            st.setInt(5, num);
            if(st.executeUpdate() != 1) {
                // db.rollback();
                return false;
            }
            for(Integer author : authors_id) {
                st = db.prepareStatement("INSERT INTO author_articel (id_author, id_article)" +
                        "VALUES ( ?, ? ) ");
                st.setInt(1, author);
                st.setInt(2, art_id);
                if(st.executeUpdate() != 1) {
                    return false;
                }
            }
            for(String kw : key_words_id) {
                st = db.prepareStatement("INSERT INTO key_word_article (word, id_article)" +
                        "VALUES (?, ?) ");
                st.setString(1, kw);
                st.setInt(2, art_id);
                if(st.executeUpdate() != 1) {
                    return false;
                }
            }

            for(String kw : auto_key_words_id) {
                st = db.prepareStatement("INSERT INTO key_word_auto_article (word, id_article)" +
                        "VALUES (?, ?) ");
                st.setString(1, kw);
                st.setInt(2, art_id);
                if(st.executeUpdate() != 1) {
                    return false;
                }
            }

            for(Word w : words) {
                st = db.prepareStatement("INSERT INTO fdict (word, id_article, frequency) " +
                        "VALUES ( ?, ?, ? )");
                st.setString(1, w.word);
                st.setInt(2, art_id);
                st.setDouble(3, w.frequency);
                if(st.executeUpdate() != 1) {
                    return false;
                }
            }

            //db.commit();
        } catch (Exception exp) {
            return false;
        }
        return true;
    }

    @Override
    public boolean setTitle(String title) {
        if (title == null) {
            return false;
        }
        this.title = title;
        return true;
    }

    @Override
    public boolean addAuthor(Author author) {
        if(author == null) {
            return false;
        }

        try {
            PreparedStatement st = db.prepareStatement("SELECT id FROM author WHERE id = ?");
            st.setInt(1, author.moluchId);
            ResultSet rs = st.executeQuery();
            if(!rs.isClosed()) {
                authors_id.add(rs.getInt(1));
                return true;
            }
        } catch (Exception exp) {
            return false;
        }

        Object res = insert_to_data_base("organization", "title", author.organiztation);
        if(res == ERROR_INSERTING_IN_DB) {
            return false;
        }
        res = (String) insert_to_data_base("status", "status", author.status);
        if (res == ERROR_INSERTING_IN_DB) {
            return false;
        }
        res = (String) insert_to_data_base("academic_rank", "academic_rank", author.academy);
        if (res == ERROR_INSERTING_IN_DB) {
            return false;
        }
        try {
            PreparedStatement st = db.prepareStatement(
                    "INSERT INTO author (id, fname, lname, patronymic, status, academic_rank, organization)" +
                            "VALUES ( ?, ?, ?, ?, ?, ?, ? )");
            st.setInt(1, author.moluchId);
            st.setString(2, author.fname);
            st.setString(3, author.lname);
            st.setString(4, author.patronymic);
            st.setString(5, author.status);
            st.setString(6, author.academy);
            st.setString(7, author.organiztation);
            if(st.executeUpdate() != 1) {
                return false;
            }
        }catch(Exception exp) {
            return false;
        }
        authors_id.add(author.moluchId);
        return true;
    }

    @Override
    public boolean setText(String text) {
        if(text == null) {
            return false;
        }
        article = text;
        return true;
    }

    @Override
    public boolean setAnnotation(String annotation) {
        if (annotation == null) {
            return false;
        }
        this.annotation = annotation;
        return true;
    }

    @Override
    public boolean addTag(String tag) {
        String res =  (String) insert_to_data_base("key_word", "word", tag);
        if (res == ERROR_INSERTING_IN_DB) {
            return false;
        }
        key_words_id.add(res);
        return true;
    }

    @Override
    public boolean addAutoTag(String tag) {
        String res = (String)  insert_to_data_base("key_word", "word", tag);
        if (res == ERROR_INSERTING_IN_DB) {
            return false;
        }
        auto_key_words_id.add(res);
        return true;
    }

    @Override
    public boolean setCategory(String category) {
        Object res = insert_to_data_base("category", "title", category);
        if (res == ERROR_INSERTING_IN_DB) {
            return false;
        }
        category_id = category;
        return true;
    }

    @Override
    public boolean addReference(String ref) {
        if(ref ==null) {
            return false;
        }
        reference.append(ref + "/n/r");
        return true;
    }

    @Override
    public boolean setNumber(int num) {
        if(num <= 0) {
            return false;
        }
        this.num = num;
        return true;
    }

    @Override
    public boolean setYearNumber(int num) {
        if(num <= 0) {
            return false;
        }
        this.num_year = num;
        return true;
    }

    private Object insert_to_data_base(@NotNull String table,@NotNull String field, String value) {
        Object id = ERROR_INSERTING_IN_DB;

        if (value == null ) {
            return ERROR_INSERTING_IN_DB;
        }

        try {
            Formatter sql = new Formatter(new StringBuilder());
            sql.format("SELECT COUNT(%s) as rowcount FROM %s WHERE %s = ?", field, table, field);
            PreparedStatement st = db.prepareStatement(sql.toString());
            st.setString(1, value);
            ResultSet rs = st.executeQuery();
            if((rs != null) && (rs.getInt("rowcount") == 1)) {
                    sql = new Formatter(new StringBuilder());
                    sql.format("SELECT %s FROM %s WHERE %s = ?", field, table, field);
                    st = db.prepareStatement(sql.toString());
                    st.setString(1, value);
                    rs = st.executeQuery();
                    id = rs.getObject(field);
            } else {
                sql = new Formatter(new StringBuilder());
                sql.format("INSERT INTO %s ( %s ) VALUES ( ? )", table, field);
                st = db.prepareStatement(sql.toString());
                st.setString(1, value);
                if (st.executeUpdate() == 1) {
                    sql = new Formatter(new StringBuilder());
                    sql.format("SELECT %s FROM %s WHERE %s = ?", field, table, field);
                    st = db.prepareStatement(sql.toString());
                    st.setString(1, value);
                    rs = st.executeQuery();
                    id = rs.getObject(field);
                }
            }
        } catch(Exception exp) {
            return ERROR_INSERTING_IN_DB;
        }
        return id;
    }

    private Connection db;
    private String title = null;
    private String article= null;
    private String category_id = null;
    private String annotation = null;
    private StringBuilder reference = new StringBuilder();
    private int num_year = VALUE_NOT_SET;
    private int num = VALUE_NOT_SET;
    List<String> key_words_id = new LinkedList<>();
    List<String> auto_key_words_id = new LinkedList<>();
    List<Integer> authors_id = new LinkedList<>();
    List<Word> words = new LinkedList<>();
    private static final Object ERROR_INSERTING_IN_DB = null;
    private static final int VALUE_NOT_SET = -1;
}
