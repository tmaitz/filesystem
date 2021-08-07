package ch.solveva.filesystem.service;

import ch.solveva.filesystem.dto.FileDto;
import ch.solveva.filesystem.model.InMemoryFile;
import ch.solveva.filesystem.repository.InMemoryFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FileService {

    private static final String SYSTEM_SEPARATOR = File.separator;

    private final InMemoryFileRepository fileRepository;

    public List<InMemoryFile> findFileByQuery(String fileQuery) {
        final var probe = InMemoryFile.builder()
                .path(fileQuery)
                .build();
        final var exampleMatcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase();
        return fileRepository.findAll(Example.of(probe, exampleMatcher));
    }

    public Optional<InMemoryFile> findById(String path) {
        return fileRepository.findById(path);
    }

    public void createFile(FileDto fileDto) {
        save(InMemoryFile.builder()
                .path(fileDto.getPath())
                .text(fileDto.getText())
                .build());
    }

    private InMemoryFile save(InMemoryFile file) {
        final var parentFoldersNames = Arrays.stream(file.getPath().split("[" + SYSTEM_SEPARATOR + "]"))
                .filter(s -> !s.isEmpty())
                .toList();
        if (parentFoldersNames.size() < 2) {
            throw new UnsupportedOperationException("No parent folder");
        }
        String parentFolderCandidate = parentFoldersNames.get(0);
        for (int i = 1; i < parentFoldersNames.size() - 1; i++) {
            if (!fileRepository.existsById(parentFolderCandidate)) {
                throw new UnsupportedOperationException(String.format("Parent folder [%s] not exists", parentFolderCandidate));
            }
            parentFolderCandidate += SYSTEM_SEPARATOR + parentFoldersNames.get(i);
        }
        return fileRepository.save(file);
    }

    public void remove(String path) {
        final var removeCandidates = fileRepository.findById(path)
                .map(file -> {
                    // if folder -> remove subfolders and files
                    if(file.isFolder()) {
                        final var probe = InMemoryFile.builder()
                                .path(path)
                                .build();
                        final var exampleMatcher = ExampleMatcher.matching()
                                .withStringMatcher(ExampleMatcher.StringMatcher.STARTING)
                                .withIgnoreCase();
                        return fileRepository.findAll(Example.of(probe, exampleMatcher));
                    }
                    return Collections.singletonList(file);
                })
                .orElseGet(Collections::emptyList);
        fileRepository.deleteAll(removeCandidates);
    }
}
