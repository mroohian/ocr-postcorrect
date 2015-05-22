package de.iisys.ocr.ocr;

import de.iisys.ocr.archive.ArchiveReader;
import de.iisys.ocr.pos.IPOSTagger;
import de.iisys.ocr.pos.ISentence;
import de.iisys.ocr.pos.OCRAnnotation;
import de.iisys.ocr.pos.StanfordPOSTagger;
import de.iisys.ocr.textalignment.LevenshteinTextAligner;
import de.iisys.ocr.textalignment.LevenshteinTextAlignerResult;
import de.iisys.ocr.textalignment.LevenshteinTextAlignerResultSet;
import de.iisys.ocrcorpa.TextRenderer;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
* Created by reza on 09.12.14.
*/
public class OCRCorpusFileGeneratorRunnable implements Runnable {
    private String filePath;
    private String outputFilePath;

    private final IOCR ocr;
    private final IPOSTagger posTagger;
    private final TextRenderer textRenderer;
    private final String imageFile;

    OCRCorpusFileGeneratorRunnable(String outputFilePath, String filePath,
                                   IOCR ocr, IPOSTagger posTagger, TextRenderer textRenderer) throws IOException {
        this.filePath = filePath;
        this.outputFilePath = outputFilePath;

        this.ocr = ocr;
        this.posTagger = posTagger;
        this.textRenderer = textRenderer;

        File temp = File.createTempFile(outputFilePath, ".png");
        this.imageFile = temp.getAbsolutePath();
        temp.delete();
    }

    @Override
    public void run() {
        System.out.println("FILE: " + outputFilePath);
        String content;
        try {
            content = ArchiveReader.readFile(filePath);
        } catch (IOException e) {
            // TODO: write error log
            return;
        }

        // Check content
        if (content.length() < 1) {
            System.out.println("EMPTY: " + outputFilePath);
            // TODO: write error log
            return;
        }

        // Auto generate text images
        generateTextImage(content);

        // Extract sentences
        int nTokens = 0;
        List<ISentence> sentences = posTagger.extractSentences(content);
        StringBuilder alignerContent = new StringBuilder();
        for (ISentence sentence : sentences) {
            for (HasWord token : (StanfordPOSTagger.StanfordSentence) sentence) {
                nTokens++;
                alignerContent.append(normalize(token.word()) + " ");
            }
        }

        // OCR and align text
        LevenshteinTextAlignerResultSet result = ocrAndAlign(alignerContent.toString());
        if (nTokens != result.size()) {
            System.out.println("ERROR: " + outputFilePath);
            // TODO: write error log
            return;
        }

        // Remove the temp file
        new File(imageFile).delete();

        // Add OCR annotation tags
        int n = 0;
        for (ISentence sentence : sentences) {
            posTagger.tagSentence(sentence);

            for (CoreLabel token : (StanfordPOSTagger.StanfordSentence) sentence) {
                LevenshteinTextAlignerResult<String, String> nextResult = result.get(n++);

                // TODO: remove for release {
                //assert nextResult.output.equals(token.word());
                //System.out.println( token.word() + " / " + nextResult.input + " / " + token.tag());
                // TODO: }

                token.set(OCRAnnotation.class, normalize(nextResult.input));
            }
        }

        // Create the output path
        ensurePath();

        // Write output file
        File outputFile = new File(outputFilePath);
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document document = docBuilder.newDocument();
            Element rootElement = document.createElement("sentences");
            document.appendChild(rootElement);

            // Fill the document with elements
            for (ISentence sentence : sentences) {
                Element sentenceEl = document.createElement("sentence");
                rootElement.appendChild(sentenceEl);

                for (CoreLabel token : (StanfordPOSTagger.StanfordSentence) sentence) {
                    String value = normalize(token.value());
                    Element wordEl = document.createElement("word");
                    sentenceEl.appendChild(wordEl);

                    // Word value
                    wordEl.appendChild(document.createTextNode(value));

                    // POS Attribute
                    Attr attr = document.createAttribute("POS");
                    attr.setValue(token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
                    wordEl.setAttributeNode(attr);

                    // OCR Text attribute
                    attr = document.createAttribute("ocr");
                    attr.setValue(token.get(OCRAnnotation.class));
                    wordEl.setAttributeNode(attr);

                    // OCR Text attribute
                    attr = document.createAttribute("correct");
                    attr.setValue(String.valueOf(value.equals(token.get(OCRAnnotation.class))));
                    wordEl.setAttributeNode(attr);
                }
            }

            writeXmlDocument(outputFile, document);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private String normalize(String word) {
        if (word.equals("``")) {
            return "“";
        }
        if (word.equals("''")) {
            return "\"";
        }
        if (word.equals("-LRB-")) {
            return "(";
        }
        if (word.equals("-RRB-")) {
            return ")";
        }

        if (word.equals("--")) {
            return "—";
        }

        if (word.equals("'s")) {
            return "’s";
        }

        return word;
    }

    private void ensurePath() {
        File parent = new File(new File(outputFilePath).getParent());

        if (!parent.exists()) {
            parent.mkdirs();
        }
    }

    public void generateTextImage(String content) {
        textRenderer.renderTextToPNGFile(imageFile, content.replace("\n", " "));
    }

    public LevenshteinTextAlignerResultSet ocrAndAlign(String alignerContent) {
        try {
            String ocrContent = ocr.runOCR(imageFile);
            LevenshteinTextAlignerResultSet result = LevenshteinTextAligner.alignTexts(ocrContent, alignerContent);

            return result;
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void writeXmlDocument(File file, Document document) {
        try {
            StreamResult result = new StreamResult(file);
            DOMSource source = new DOMSource(document);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}
