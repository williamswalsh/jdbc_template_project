package ie.williamswalsh.jdbc_project.dao;

import ie.williamswalsh.jdbc_project.domain.Author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;


// Using JDBC objects directly to interface with DB.
// Defining the implementation here.
@Component
public class AuthorDaoImplJdbcDirect { // implements AuthorDao {

    public static final String INSERT_AUTHOR_PREPARED_STATEMENT = "INSERT INTO author (first_name, last_name) VALUES (?, ?)";
    private static final String UPDATE_AUTHOR_PREPARED_STATEMENT = "UPDATE author SET first_name=?, last_name=? WHERE id=?";
    private static final String DELETE_AUTHOR_PREPARED_STATEMENT = "DELETE FROM author WHERE id=?";

    private final DataSource source;

    @Autowired
    public AuthorDaoImplJdbcDirect(DataSource source) {
        this.source = source;
    }

//    @Override
    public Author getById(Long id) {

        try (Connection connection = source.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * from author WHERE id=" + id)) {
//            Bad practice - as not using bind parameters in SQL. SQL injection?               ^^^
//            Should use prepared statement,
//            also not cleaning up the resources Connection, Statement & ResultSet.

            if (resultSet.next()) {
                Author author = new Author();
                author.setId(id);
                author.setFirstName(resultSet.getString("first_name"));
                author.setLastName(resultSet.getString("last_name"));

                return author;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

//    @Override
    public Author findAuthorByName(String firstName, String lastName) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "SELECT * from author WHERE first_name = ? AND last_name = ?";

        try {
            conn = source.getConnection();
            ps = conn.prepareStatement(statement);
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            rs = ps.executeQuery();

            if (rs.next()) {
                return getAuthorFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeAll(conn, ps, rs);
        }
        return null;
    }


    public Author getByIdPrepared(Long id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = source.getConnection();

//            Prepared statements more performant than execute query.
//            SQL will only validate the query once for prepared statements.
//            SQL will validate the statement query each time its executed.
            ps = conn.prepareStatement("SELECT * from author WHERE id = ?"); // use of ? in prepared statement to locate param

            // setting the bind param to bind to the question mark symbol in the prepared statement
            // the bind parameters are typed - more secure ***NB***
            ps.setLong(1, id);


            rs = ps.executeQuery();

            if (rs.next()) {
                return getAuthorFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeAll(conn, ps, rs);
        }
        return null;
    }

    private Author getAuthorFromResultSet(ResultSet rs) throws SQLException {
        Author author = new Author();
        author.setId(rs.getLong("id"));
        author.setFirstName(rs.getString("first_name"));
        author.setLastName(rs.getString("last_name"));

        return author;
    }

    private void closeAll(Connection conn, PreparedStatement ps, ResultSet rs) {
        try {
            if(rs != null) {
                rs.close();
            }

            if(ps != null) {
                ps.close();
            }

            if(conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    @Override
    public Author saveAuthor(Author author) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            conn = source.getConnection();
            ps = conn.prepareStatement(INSERT_AUTHOR_PREPARED_STATEMENT);

            ps.setString(1, author.getFirstName());
            ps.setString(2, author.getLastName());
            ps.execute();

            Statement statement = conn.createStatement();
            resultSet = statement.executeQuery("SELECT LAST_INSERT_ID()");


//            MySQL/H2 only feature - SELECT LAST_INSERT_ID
            if(resultSet.next()) {
                long lastInsertIndex = resultSet.getLong(1);
//                System.out.println("LAST INSERT INDEX: " + lastInsertIndex);
                return this.getById(lastInsertIndex);
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeAll(conn, ps, resultSet);
        }
        return null;
    }

//    @Override
    public Author updateAuthor(Author author) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            conn = source.getConnection();
            ps = conn.prepareStatement(UPDATE_AUTHOR_PREPARED_STATEMENT);
            ps.setString(1, author.getFirstName());
            ps.setString(2, author.getLastName());
            ps.setLong(3, author.getId());
            ps.execute();

            Statement statement = conn.createStatement();
            resultSet = statement.executeQuery("SELECT LAST_INSERT_ID()");

//            MySQL/H2 only feature - SELECT LAST_INSERT_ID
            if(resultSet.next()) {
                long lastInsertIndex = resultSet.getLong(1);
                return this.getById(lastInsertIndex);
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeAll(conn, ps, resultSet);
        }
        return null;
    }

//    @Override
    public void deleteAuthorById(Long id) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = source.getConnection();
            ps = conn.prepareStatement(DELETE_AUTHOR_PREPARED_STATEMENT);
            ps.setLong(1, id);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeAll(conn, ps, null);
        }
    }
}
