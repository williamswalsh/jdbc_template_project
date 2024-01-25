package ie.williamswalsh.jdbc_project.dao;

import ie.williamswalsh.jdbc_project.domain.Book;

public interface BookDao {

    Book getById(Long id);

    Book findBookByTitle(String title);

    Book saveBook(Book book);

    Book updateBook(Book book);

    void deleteBookById(Long id);
}
