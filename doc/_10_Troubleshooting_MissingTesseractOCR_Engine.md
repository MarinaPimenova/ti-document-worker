Looking at the PDF file you provided, `MCP-server-ScannedPdfDocument.pdf` contains a multi-page article titled *"Exploring Model Context Protocol (MCP) With Spring AI"* with code snippets, architecture diagrams, and multiple headings.

However, your `TikaDocumentReader` extracted almost nothing:

```text
text = "\nView Comments\n\nOldest\n\n\n"

```

It only extracted the footer text from the very bottom of the page.

---

### Why Did This Happen?

1. **Scanned PDF / Image-Based Text:** The PDF appears to be a web printout where the text was rendered as images rather than selectable, native PDF text vectors. Pure Apache Tika without OCR (Tesseract) sees image-based PDFs as empty pages and only grabs the tiny bit of real text it can find (the web browser footer).
2. **Missing Tesseract / OCR Engine:** `spring-ai-tika-document-reader` relies on underlying Apache Tika. If Tika encounters images, it will not extract text unless `tesseract` is installed on your OS and configured in Tika.

---

### How to Fix It

**Option 1: Enable OCR in Tika (Recommended for Scanned PDFs)**
Install Tesseract OCR on your server/machine:

* **macOS:** `brew install tesseract`
* **Ubuntu/Debian:** `sudo apt-get install tesseract-ocr`

Spring AI's `TikaDocumentReader` will automatically detect Tesseract on your system PATH and run OCR on the images inside the PDF.

**Option 2: Fall back to PDFBox Page Reader (If it's standard PDF text)**
If you want to test if Spring AI's native PDF reader can parse it without Tika overhead, use `PagePdfDocumentReader`:

```java
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;

// Uses PDFBox under the hood
PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(
    fileResource,
    PdfDocumentReaderConfig.builder().build()
);

List<Document> documents = pdfReader.read();

```

If `PagePdfDocumentReader` also returns empty text, the PDF is strictly image-based, and you **must** use an OCR-enabled parser (Tesseract with Tika, or an external OCR API like AWS Textract / Azure Form Recognizer).