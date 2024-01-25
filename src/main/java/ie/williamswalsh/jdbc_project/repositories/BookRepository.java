package ie.williamswalsh.jdbc_project.repositories;

import ie.williamswalsh.jdbc_project.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, Long> {
}
