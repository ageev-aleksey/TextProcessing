package moluch;

import textworker.TableSaver;

import java.sql.*;

public class DbSaver implements TableSaver, ArticleSaver{
    public DbSaver(Connection dbConnection) throws Exception {
        if (!dbConnection.isValid(10)) {
            throw new Exception("Db connection must be opened");
        }
        db = dbConnection;
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
        return false;
    }

    @Override
    public Word getNextWord() {
        return null;
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

    Connection db;

    @Override
    public boolean setTitle(String title) {
        return false;
    }

    @Override
    public boolean addAuthor(Author author) {
        return false;
    }

    @Override
    public boolean setText(String text) {
        return false;
    }

    @Override
    public boolean setAnnotation(String annotation) {
        return false;
    }

    @Override
    public boolean addTag(String tag) {
        return false;
    }

    @Override
    public boolean addAutoTag(String tag) {
        return false;
    }

    @Override
    public boolean setCategory(String category) {
        return false;
    }

    @Override
    public boolean addReference(String ref) {
        return false;
    }
}
