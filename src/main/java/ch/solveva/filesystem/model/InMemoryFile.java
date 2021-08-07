package ch.solveva.filesystem.model;

import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Table
@Entity
public class InMemoryFile {

    @Id
    private String path;

    // if text is null -> folder
    @Column(length = 1024)
    private String text;

    public boolean isFolder() {
        return text == null;
    }

}
