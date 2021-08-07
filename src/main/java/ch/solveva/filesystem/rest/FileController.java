package ch.solveva.filesystem.rest;

import ch.solveva.filesystem.dto.FileDto;
import ch.solveva.filesystem.model.InMemoryFile;
import ch.solveva.filesystem.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;

    @GetMapping
    public List<String> findFiles(@RequestParam("q") String fileQuery) {
        return fileService.findFileByQuery(fileQuery).stream()
                .map(InMemoryFile::getPath)
                .collect(Collectors.toList());
    }

    @GetMapping("/{path}")
    public String getFileContent(@PathVariable String path) {
        return fileService.findById(path)
                .map(InMemoryFile::getText)
                .orElse(null);
    }

    @PostMapping
    public void createFile(FileDto fileDto) {
        fileService.createFile(fileDto);
    }

    @DeleteMapping
    public void removeFile(String path) {
        fileService.remove(path);
    }

}
