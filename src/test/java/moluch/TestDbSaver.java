package moluch;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestDbSaver {

    @Test
    public void test_to_write_db()  {
        Article art = new Article();
        art.setTitle("title");
        art.setText("This is text");
        art.setAnnotation("This is Annotation");
        art.setCategory("Test");
        art.addReference("Literature 1");
        art.addReference("Literature 2");
        art.addReference("Literature 3");
        art.addAutoTag("auto tag 1");
        art.addAutoTag("auto tag 2");
        art.addUsersTag("tag 1");
        art.addUsersTag("tag 2");
        art.setNumber(10);
        art.setYearNum(115);
        Author a1 = new Author();
        a1.status = "student";
        a1.academy = "";
        a1.lname = "Name";
        a1.fname = "LastName";
        a1.patronymic = "Patronymic";
        a1.moluchId = 195;
        a1.organiztation = "University";
        Author a2 = new Author();
        a2.status = "lecturer";
        a2.academy = "Professor";
        a2.lname = "Name2";
        a2.fname = "LastName2";
        a2.patronymic = "Patronymic2";
        a2.moluchId = 12;
        a2.organiztation = "University";
        art.addAuthor(a1);
        art.addAuthor(a2);
        try{
            Connection connection = DriverManager.getConnection("jdbc:sqlite:./sql/testDb.sqlite3");
            DbSaver saver = new DbSaver(connection);
            art.setSaver(saver);
            Assertions.assertTrue(art.save());
        } catch(Exception exp) {
            Assertions.fail();
        }

    }
}
