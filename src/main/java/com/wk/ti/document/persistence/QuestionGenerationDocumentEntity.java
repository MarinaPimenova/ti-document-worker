package com.wk.ti.document.persistence;

import com.wk.ti.document.model.DocumentExtension;
import com.wk.ti.document.model.DocumentStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@SuppressWarnings("SpringDataJdbcAssociatedDbElementsInspection")
@Getter
@Setter
@Table("question_generation_document")
public class QuestionGenerationDocumentEntity {
    @Id
    private Long id;

    private String filename;

    @Column("file_extension")
    private DocumentExtension fileExtension;

    @Column("file_size")
    private Long fileSize;

    private DocumentStatus status;

    @Column("created_at")
    private OffsetDateTime createdAt;

    @Column("updated_at")
    private OffsetDateTime updatedAt;
}
