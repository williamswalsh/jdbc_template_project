package ie.williamswalsh.jdbc_project.dao;

import ie.williamswalsh.jdbc_project.domain.Author;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ActiveProfiles("local")
@DataJpaTest
@ComponentScan(basePackages = {"ie.williamswalsh.jdbc_project.dao"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // don't use H2 DB.
class AuthorDaoIntegrationTest {

    @Autowired
    AuthorDao authorDao;

    @Test
    void testGetById() {
//        20L has no books
        Author author = authorDao.getById(20L);

        assertThat(author).isNotNull();
    }

    @Test
    void findAuthorByNameTest() {
//        If multiple records match the criteria you get an error
//        org.springframework.dao.IncorrectResultSizeDataAccessException: Incorrect result size: expected 1, actual 20
//        Author author = authorDao.findAuthorByName("John", "Thompson");

        Author author = authorDao.findAuthorByName("Robert", "Martin");
        assertThat(author).isNotNull();
    }

    @Test
    void testSaveAuthor() {
        String firstName = "This user";
        String lastName = "will be rolled back.";

        Author author = new Author();
        author.setFirstName(firstName);
        author.setLastName(lastName);
        Author saved = authorDao.saveAuthor(author);
        System.out.println("Saved ID: " + saved.getId()); // Id's are not sequential - due to rollbacks - such as in this test.

        assertThat(saved).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo(firstName);
        assertThat(saved.getLastName()).isEqualTo(lastName);
    }

//    We save then update to ensure that the input data in the test is not dirty.
//    If we relied on data in the db already, the data could be modified - breaking the test.
    @Test
    void testUpdateAuthor() {
        String firstName = "John";
        String incorrectLastName = "T";
        String correctLastName = "Thompson";

        Author author = new Author();
        author.setFirstName(firstName);
        author.setLastName(incorrectLastName);
        Author saved = authorDao.saveAuthor(author);

        author.setId(saved.getId());
        author.setLastName(correctLastName);
        Author updatedAuthor = authorDao.updateAuthor(author);

        assertThat(updatedAuthor).isNotNull();
        assertThat(updatedAuthor.getFirstName()).isEqualTo(firstName);
        assertThat(updatedAuthor.getLastName()).isEqualTo(correctLastName);
    }

    @Test
    void testDeleteAuthorById() {
        Author author = new Author();
        author.setFirstName("Arthur Conan ");
        author.setLastName("Doyle");
        Author saved = authorDao.saveAuthor(author);

        authorDao.deleteAuthorById(saved.getId());
//        EmptyResultDataAccessException: Incorrect result size: expected 1, actual 0
//        Because there is no matching object with the specified ID.
        assertThrows(TransientDataAccessResourceException.class, () -> {
            authorDao.getById(saved.getId());
        });
    }
}
