package ie.williamswalsh.jdbc_project.dao;


import ie.williamswalsh.jdbc_project.domain.Author;
import ie.williamswalsh.jdbc_project.domain.Book;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AuthorMapper implements RowMapper<Author> {

    @Override
    public Author mapRow(ResultSet rs, int rowNum) throws SQLException {

        Author author = new Author();
        author.setId(rs.getLong("id"));
        author.setFirstName(rs.getString("first_name"));
        author.setLastName(rs.getString("last_name"));

        try {
            if (rs.getString("isbn") != null) {
                author.setBooks(new ArrayList<>());
                author.getBooks().add(mapBooks(rs));
            }

            while (rs.next()) {
                author.getBooks().add(mapBooks(rs));
            }
        } catch(SQLException e) {
//            Do nothing
//            e.printStackTrace();
        }
        return author;
    }

    private Book mapBooks(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getLong(4));
        book.setIsbn(rs.getString(5));
        book.setPublisher(rs.getString(6));
        book.setTitle(rs.getString(7));
        book.setAuthorId(rs.getLong(1));
        return book;
    }
}
