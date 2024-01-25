package ie.williamswalsh.jdbc_project.dao;

import ie.williamswalsh.jdbc_project.domain.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class BookJdbcTemplateImpl implements BookDao {

    public static final String SELECT_FROM_BOOK_WHERE_ID = "SELECT * FROM book where id=?";
    private static final String SELECT_FROM_BOOK_WHERE_TITLE = "SELECT * FROM book where title=?";
    public static final String INSERT_INTO_BOOK_VALUES = "INSERT INTO book (title, isbn, publisher) VALUES (?, ?, ?)";
    private static final String UPDATE_BOOK_WHERE_ID = "UPDATE book SET title=?, isbn=?, publisher=? WHERE id=?";
    private static final String DELETE_BOOK_WHERE_ID = "DELETE FROM book WHERE id=?";
    public static final String SELECT_LAST_INSERT_ID = "SELECT LAST_INSERT_ID()";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BookJdbcTemplateImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Book getById(Long id) {
        return jdbcTemplate.queryForObject(SELECT_FROM_BOOK_WHERE_ID, getRowMapper(), id);
    }

    @Override
    public Book findBookByTitle(String title) {
        return jdbcTemplate.queryForObject(SELECT_FROM_BOOK_WHERE_TITLE, getRowMapper(), title);
    }

    @Override
    public Book saveBook(Book book) {
        jdbcTemplate.update(INSERT_INTO_BOOK_VALUES, book.getTitle(), book.getIsbn(), book.getPublisher());

//        Get last insert ID
        Long lastInsertId = jdbcTemplate.queryForObject(SELECT_LAST_INSERT_ID, Long.class);

//        Using get method to return created Book object
        return this.getById(lastInsertId);
    }

    @Override
    public Book updateBook(Book book) {
        jdbcTemplate.update(UPDATE_BOOK_WHERE_ID, book.getTitle(), book.getIsbn(), book.getPublisher(), book.getId());

        return this.getById(book.getId());
    }

    @Override
    public void deleteBookById(Long id) {
        jdbcTemplate.update(DELETE_BOOK_WHERE_ID, id);
    }

//    We get a new instance each time we need a row mapper. - relates to Thread safety and perf.
    public RowMapper<Book> getRowMapper() {
        return new BookMapper();
    }
}
