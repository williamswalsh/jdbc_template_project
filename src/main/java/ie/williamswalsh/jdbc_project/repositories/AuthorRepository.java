package ie.williamswalsh.jdbc_project.repositories;


import ie.williamswalsh.jdbc_project.domain.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
