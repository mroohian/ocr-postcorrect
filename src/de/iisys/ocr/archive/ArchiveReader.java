package de.iisys.ocr.archive;

import de.iisys.ocr.ocr.OCRCorpusSentence;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * ArchiveReader
 * de.iisys.ocr.archive
 * Created by reza on 19.08.14.
 */
public class ArchiveReader {
    public interface IArchiveReaderCallback {
        public void processFileContent(ArchiveReader archiveReader, File file);
        public void processDirChanged(File dir);
    }

    private final IArchiveReaderCallback archiveReaderCallback;
    private boolean forceStop = false;

    public ArchiveReader(IArchiveReaderCallback archiveReaderCallback) {
        this.archiveReaderCallback = archiveReaderCallback;
    }

    public void cancelOperation() {
        forceStop = true;
    }

    // Threads will call the method for multi-threaded support
    private static Document readFile(File file)  throws IOException {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            return doc;
        } catch (ParserConfigurationException e) {
            throw new IOException(e);
        } catch (SAXException e) {
            throw new IOException(e);
        }
    }

    public static OCRCorpusSentence[] readOCRCorpusFile(File file) throws IOException {
        Document doc = readFile(file);

        NodeList nodeSentenceList = doc.getElementsByTagName("sentence");
        OCRCorpusSentence[] result = new OCRCorpusSentence[nodeSentenceList.getLength()];
        for (int i = 0; i < nodeSentenceList.getLength(); i++) {
            Node nodeSentence = nodeSentenceList.item(i);

            NodeList sentenceWordNodeList = nodeSentence.getChildNodes();
            String[] words = new String[sentenceWordNodeList.getLength()];
            String[] ocrWords = new String[sentenceWordNodeList.getLength()];
            String[] posTags = new String[sentenceWordNodeList.getLength()];
            boolean[] correct = new boolean[sentenceWordNodeList.getLength()];
            for (int j = 0; j < sentenceWordNodeList.getLength(); j++) {
                Node nodeWord = sentenceWordNodeList.item(j);
                NamedNodeMap attributes = nodeWord.getAttributes();

                words[j] = nodeWord.getTextContent();
                posTags[j] = attributes.getNamedItem("POS").getTextContent();
                ocrWords[j] = attributes.getNamedItem("ocr").getTextContent();
                correct[j] = Boolean.parseBoolean(attributes.getNamedItem("correct").getTextContent());
            }

            result[i] = new OCRCorpusSentence(words, ocrWords, posTags, correct);
        }

        return result;
    }

    public static String readRawFile(File file) throws IOException {
        Document doc = readFile(file);

        NodeList nodeList = doc.getElementsByTagName("ExtractedText");

        StringBuilder builder = new StringBuilder();
        for (int temp = 0; temp < nodeList.getLength(); temp++) {
            Node node = nodeList.item(temp);

            String input = node.getTextContent();
            builder.append(input);
        }

        return builder.toString();
    }

    public static String readFile(String filePath) throws IOException {
        return readRawFile(new File(filePath));
    }

    // TODO: exception handling and processing user interrupts (Ctrl+C)
    public void readDir(File dir)  {
        if (forceStop) return;

        if (!dir.exists()) {
            throw new IllegalArgumentException("archive path does not exists: " + dir.getAbsolutePath());
        }
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            archiveReaderCallback.processDirChanged(dir);
            for (File child : directoryListing) {
                if (forceStop) break;
                readDir(child);
            }
        } else {
            archiveReaderCallback.processFileContent(this, dir);
        }
    }
}
