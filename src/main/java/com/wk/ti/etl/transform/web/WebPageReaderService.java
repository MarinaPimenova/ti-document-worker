package com.wk.ti.etl.transform.web;

import com.wk.ti.etl.transform.image.ImageReaderService;
import com.wk.ti.etl.load.service.VectorStoreService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@SuppressWarnings("unused")
@Service
public class WebPageReaderService {

    private final ImageReaderService imageReaderService;
    private final VectorStoreService vectorStoreService;
    private final HtmlTableExtractorService htmlTableExtractorService;
    private final ChatClient chatTableClient;
    private final RetrievalAugmentationAdvisor retrievalAugmentationAdvisor;
    private final SimpleLoggerAdvisor simpleLoggerAdvisor;

    public WebPageReaderService(
            VectorStore vectorStore,
            ImageReaderService imageReaderService,
            VectorStoreService vectorStoreService,
            HtmlTableExtractorService htmlTableExtractorService,
            @Qualifier("openAiChatClient")
            ChatClient chatTableClient) {

        this.imageReaderService = imageReaderService;
        this.vectorStoreService = vectorStoreService;
        this.htmlTableExtractorService = htmlTableExtractorService;
        this.chatTableClient = chatTableClient;

        // Configure the document retriever explicitly using the VectorStore
        VectorStoreDocumentRetriever retriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .build();

        // Construct the modern RetrievalAugmentationAdvisor with the document retriever
        this.retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(retriever)
                .build();

        this.simpleLoggerAdvisor = new SimpleLoggerAdvisor();
    }

    public void addWebPageContent(String url) {
        try {
            addPlainWebPageContent(url);
            extractDiagrams(url);
            //addTablesDescriptionOfWebPageContent(url);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read from URL: " + url + " Caused by: " + e.getMessage(), e);
        }
    }

    protected void addPlainWebPageContent(String url) throws IOException {
        Document htmlDoc = Jsoup.connect(url).get();
        String textContent = htmlDoc.select("article, main, body").text();
        if (textContent.isBlank()) {
            throw new RuntimeException("No readable content found at URL: " + url);
        }
        vectorStoreService.storeToVectorStore(textContent, url);
    }

    protected List<String> extractImageUrlsFromPage(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        return doc.select("img")
                .stream()
                .map(img -> img.absUrl("src"))
                .filter(src -> src.endsWith(".png") || src.endsWith(".jpg"))
                .toList();
    }

    protected void extractDiagrams(String webpageUrl) throws IOException {
        List<String> imageUrls = extractImageUrlsFromPage(webpageUrl);
        for (String imageUrl : imageUrls) {
            imageReaderService.addResource(imageUrl);
        }
    }

    protected void addTablesDescriptionOfWebPageContent(String url) throws IOException {
        String tableHtml = htmlTableExtractorService.extractTablesAsHtml(url);

        String prompt = """
                Please analyze and summarize the following HTML tables.
                Convert data into meaningful descriptions that can be embedded for search.
                HTML:
                """ + tableHtml;

        String content = chatTableClient
                .prompt(prompt)
                .advisors(
                        retrievalAugmentationAdvisor,
                        simpleLoggerAdvisor)
                .call()
                .content();

        vectorStoreService.storeToVectorStore(content, url);
    }
}