package ie.williamswalsh.jdbc_project.dao;

import ie.williamswalsh.jdbc_project.domain.Author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class AuthorJdbcTemplateImpl implements AuthorDao {

    public static final String SELECT_FROM_AUTHOR_WHERE_ID = "SELECT * FROM author where id=?";

//    Always brings back author - conditionally brings back books if present.
//    *** NB *** - Mapper + Extractor convert the records back to objects.
//    Extractor calls Mapper.mapRow() method - mapRow() calls mapBooks() method.
    public static final String SELECT_OUTER_JOIN_BOOK = "select author.id as id, first_name, last_name, book.id as book_id, book.isbn, book.publisher, book.title from author\n" +
            "left outer join book on author.id = book.author_id where author.id = ?";
    private static final String SELECT_FROM_AUTHOR_WHERE_NAME = "SELECT * FROM author where first_name=? AND last_name=?";
    public static final String INSERT_INTO_AUTHOR_FIRST_NAME_LAST_NAME_VALUES = "INSERT INTO author (first_name, last_name) VALUES (?, ?)";
    private static final String UPDATE_AUTHOR_WHERE_ID = "UPDATE author SET first_name=?, last_name=? WHERE id=?";
    private static final String DELETE_AUTHOR_WHERE_ID = "DELETE FROM author WHERE id=?";

    //   Auto-configured with default datasource.
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AuthorJdbcTemplateImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Author getById(Long id) {
        return jdbcTemplate.query(SELECT_OUTER_JOIN_BOOK, new AuthorExtractor(), id);
    }

    @Override
    public Author findAuthorByName(String firstName, String lastName) {
        return jdbcTemplate.queryForObject(SELECT_FROM_AUTHOR_WHERE_NAME, getRowMapper(), firstName, lastName);
    }

    @Override
    public Author saveAuthor(Author author) {
        jdbcTemplate.update(INSERT_INTO_AUTHOR_FIRST_NAME_LAST_NAME_VALUES, author.getFirstName(), author.getLastName());

//        Get last insert ID
        Long lastInsertId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);

//        Using get method to return created Author object
        return this.getById(lastInsertId);
    }

    @Override
    public Author updateAuthor(Author author) {
        jdbcTemplate.update(UPDATE_AUTHOR_WHERE_ID, author.getFirstName(), author.getLastName(), author.getId());

        return this.getById(author.getId());
    }

    @Override
    public void deleteAuthorById(Long id) {
        jdbcTemplate.update(DELETE_AUTHOR_WHERE_ID, id);
    }

//    We get a new instance each time we need a row mapper. - relates to Thread safety and perf.
    public RowMapper<Author> getRowMapper() {
        return new AuthorMapper();
    }
}
