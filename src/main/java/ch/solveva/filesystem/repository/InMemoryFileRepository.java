package ch.solveva.filesystem.repository;

import ch.solveva.filesystem.model.InMemoryFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InMemoryFileRepository extends JpaRepository<InMemoryFile, String> {
}
