package ie.williamswalsh.jdbc_project;

import ie.williamswalsh.jdbc_project.domain.Author;
import ie.williamswalsh.jdbc_project.repositories.AuthorRepository;
import ie.williamswalsh.jdbc_project.repositories.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@ActiveProfiles("local") // enable the "local" profile config application-local.properties - mysql db
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MySqlIntegrationTest {

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    BookRepository bookRepository;

    @Test
    void test() {
        Author author = new Author();
        author.setFirstName("Stephen");
        author.setLastName("King");

        Author saved = authorRepository.save(author);
        Author fetched = authorRepository.getReferenceById(saved.getId());
        assertThat(fetched).isNotNull();
    }
}
