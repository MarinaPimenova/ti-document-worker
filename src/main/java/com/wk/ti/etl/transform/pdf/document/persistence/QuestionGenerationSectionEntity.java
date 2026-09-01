package com.wk.ti.etl.transform.pdf.document.persistence;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("question_generation_section")
public class QuestionGenerationSectionEntity {

    @Id
    private Long id;

    @Column("document_id")
    private Long documentId;

    @Column("section_number")
    private Integer sectionNumber;

    private String title;

    private String content;

    @Column("start_page_number")
    private Integer startPageNumber;

    @Column("end_page_number")
    private Integer endPageNumber;

    @Column("token_count")
    private Integer tokenCount;

    @Column("created_at")
    private OffsetDateTime createdAt;

    public static QuestionGenerationSectionEntity create(
            Long documentId,
            int sectionNumber,
            String title,
            String content,
            int tokenCount) {
        return new QuestionGenerationSectionEntity(
                null,
                documentId,
                sectionNumber,
                title,
                content,
                null,
                null,
                tokenCount,
                OffsetDateTime.now()
        );
    }
}
