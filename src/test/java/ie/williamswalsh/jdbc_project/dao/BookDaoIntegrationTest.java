package ie.williamswalsh.jdbc_project.dao;

import ie.williamswalsh.jdbc_project.domain.Author;
import ie.williamswalsh.jdbc_project.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ActiveProfiles("local")
@DataJpaTest
@ComponentScan(basePackages = {"ie.williamswalsh.jdbc_project.dao"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // don't use H2 DB.
class BookDaoIntegrationTest {

    @Autowired
    BookDao bookDao;

    @Test
    void testGetById() {
        Book book = bookDao.getById(1L);
        assertThat(book).isNotNull();
    }

    @Test
    void findBookByTitleTest() {
        Book book = bookDao.findBookByTitle("Spring in Action, 6th Edition");
        assertThat(book).isNotNull();
    }

    @Test
    void testSaveBook() {
        Book book = new Book();
        book.setIsbn("11111");
        book.setPublisher("Walsh");
        book.setTitle("Software Engineering");
        book.setAuthorId(3L);
        Book saved = bookDao.saveBook(book);

        assertThat(saved).isNotNull();
    }

//    We save then update to ensure that the input data in the test is not dirty.
//    If we relied on data in the db already, the data could be modified - breaking the test.
    @Test
    void testUpdateBook() {
        String isbn = "566787656";
        String publisher = "Walsh";

        String incorrectTitle = "T";
        String correctTitle = "Typescript for Beginners";

        Book book = new Book();
        book.setIsbn(isbn);
        book.setPublisher(publisher);

        book.setAuthorId(3L);
        book.setTitle(incorrectTitle);

        Book saved = bookDao.saveBook(book);

        book.setId(saved.getId());
        book.setTitle(correctTitle);
        Book updatedBook = bookDao.updateBook(book);

        assertThat(updatedBook).isNotNull();
        assertThat(updatedBook.getIsbn()).isEqualTo(isbn);
        assertThat(updatedBook.getTitle()).isEqualTo(correctTitle);
    }

    @Test
    void testDeleteBookById() {
        Book book = new Book();
        book.setTitle("Should be deleted");
        book.setIsbn("7686976");
        book.setPublisher("Walsh");
        Book saved = bookDao.saveBook(book);
        bookDao.deleteBookById(saved.getId());

//        EmptyResultDataAccessException: Incorrect result size: expected 1, actual 0
//        Because there is no matching object with the specified ID.
        assertThrows(EmptyResultDataAccessException.class, () -> {
            bookDao.getById(saved.getId());
        });
    }
}