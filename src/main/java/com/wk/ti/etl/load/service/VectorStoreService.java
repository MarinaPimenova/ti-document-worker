package com.wk.ti.etl.load.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VectorStoreService {

    private final VectorStore vectorStore;

    public void storeToVectorStore(String info, String imageUrl) {
        // Convert to document and embed
        Document doc = new Document(info);
        doc.getMetadata().put("source", imageUrl);

        TokenTextSplitter splitter =
                TokenTextSplitter.builder()
                        .build();
        vectorStore.accept(splitter.apply(List.of(doc)));
    }

    public void store(
            Long documentId,
            String filename,
            List<Document> pages) {

        log.info("Starting vector store ingestion: documentId={}, filename={}, pages={}",
                documentId,
                filename,
                pages == null ? 0 : pages.size());

        if (pages == null || pages.isEmpty()) {
            throw new IllegalArgumentException("No PDF pages to store");
        }

        TokenTextSplitter textSplitter =
                TokenTextSplitter.builder()
                        .withChunkSize(800)
                        .withMinChunkSizeChars(350)
                        .withMinChunkLengthToEmbed(10)
                        .withMaxNumChunks(10_000)
                        .withKeepSeparator(true)
                        .build();

        List<Document> chunks = textSplitter.apply(pages);

        log.info("TokenTextSplitter produced {} chunks for documentId={}",
                chunks.size(),
                documentId);

        if (chunks.isEmpty()) {
            log.error("TokenTextSplitter produced no chunks");
            throw new IllegalStateException("TokenTextSplitter produced no chunks");
        }
        for(int index = 0; index < chunks.size(); index++) {
            Document chunk = chunks.get(index);
            chunk.getMetadata().put("chunk_index", index);
            chunk.getMetadata().put("total_chunks", chunks.size());
        }

        log.info("Adding {} chunks to VectorStore. documentId={}, filename={}",
                chunks.size(),
                documentId,
                filename);

        try {
            vectorStore.add(chunks);
            log.info("Successfully added {} chunks to VectorStore. documentId={}", chunks.size(), documentId);
        }
        catch (Exception e) {
            log.error("Failed to add chunks to VectorStore. documentId={}, filename={}", documentId, filename, e);
            throw e;
        }
    }

}
