package de.iisys.ocr.trainingset;

import de.iisys.levdistcorpa.corpa.Corpa;
import de.iisys.levdistcorpa.types.EndNode;
import de.iisys.levdistcorpa.types.StartNode;
import de.iisys.ocr.archive.ArchiveReader;
import de.iisys.ocr.pos.IPOSTagger;
import de.iisys.ocr.pos.ISentence;
import de.iisys.ocr.pos.StanfordPOSTagger;
import de.iisys.levdistcorpa.alphabet.POSAlphabet;
import de.iisys.levdistcorpa.alphabet.WordAlphabet;
import de.iisys.ocr.types.MixedTrigramInfo;
import de.iisys.ocr.types.POSTrigramInfo;
import de.iisys.ocr.types.WordPOSInfo;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

import java.io.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * TrainingSetGenerator
 * Created by reza on 11.01.15.
 */
public class TrainingSetGenerator {
    private static final int SENTENCE_PER_FILE = 500;

    private final TestGeneratorPoolExecutor poolExecutor;
    private final IPOSTagger posTagger;

    // temporary data structure for generation step
    private final Map<Integer, WordPOSInfo> wordPOSMapping;
    private final Map<Long, POSTrigramInfo> posTrigrams;
    private final Map<Long, MixedTrigramInfo> mixedTrigrams;
    private final POSAlphabet posAlphabet;
    private final WordAlphabet wordAlphabet;
    private final ArrayList<String> taggedSentences;

    // results
    private long totalWords;
    private long totalPOSTrigrams;
    private long totalMixedTrigrams;
    private ArrayList<WordPOSInfo> sortedWordPOSMappings;
    private ArrayList<POSTrigramInfo> sortedPOSTrigrams;
    private ArrayList<MixedTrigramInfo> sortedMixedTrigrams;

    public TrainingSetGenerator(IPOSTagger posTagger) {
        posAlphabet = new POSAlphabet();
        wordAlphabet = new WordAlphabet();

        wordPOSMapping = new HashMap<Integer, WordPOSInfo>();
        posTrigrams = new HashMap<Long, POSTrigramInfo>();
        mixedTrigrams = new HashMap<Long, MixedTrigramInfo>();

        this.posTagger = posTagger;
        poolExecutor = new TestGeneratorPoolExecutor();
        taggedSentences = new ArrayList<String>();

        totalWords = 0;
        totalPOSTrigrams = 0;
        totalMixedTrigrams = 0;
    }

    public TrainingSetGenerator(String file) throws IOException, ClassNotFoundException {
        posAlphabet = new POSAlphabet();
        wordAlphabet = new WordAlphabet();

        wordPOSMapping = new HashMap<Integer, WordPOSInfo>();
        posTrigrams = new HashMap<Long, POSTrigramInfo>();
        mixedTrigrams = new HashMap<Long, MixedTrigramInfo>();

        posTagger = null;
        poolExecutor = null;
        taggedSentences = null;

        read(file);
    }

    // <editor-fold desc="Generator functions">
    public void executeFile(File file){
        TestFileParserRunnable runnable = new TestFileParserRunnable(file);
        poolExecutor.execute(runnable);
    }

    private ArrayList<WordPOSInfo> getWordPOSMapping() {
        return sortedWordPOSMappings;
    }

    private ArrayList<POSTrigramInfo> getPOSTrigrams() {
        return sortedPOSTrigrams;
    }

    private ArrayList<MixedTrigramInfo> getMixedTrigrams() {
        return sortedMixedTrigrams;
    }

    public void awaitTermination() throws InterruptedException, IOException {
        poolExecutor.shutdown();

        while (!poolExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
            System.out.println("Waiting...");
        }

        sortedWordPOSMappings = new ArrayList<WordPOSInfo>(wordPOSMapping.values());
        Collections.sort(sortedWordPOSMappings);

        // Generate rankings
        int rank = 0;
        long lastFreq = Long.MAX_VALUE;
        for (WordPOSInfo wordPOSInfo : sortedWordPOSMappings) {
            long freq = wordPOSInfo.getFreq();
            if (freq < lastFreq) {
                rank++;
                lastFreq = freq;
            }

            wordPOSInfo.setRank(rank);
        }

        generateTrigrams();
    }

    private void generateTrigrams() {
        // Generated sorted list
        sortedPOSTrigrams = new ArrayList<POSTrigramInfo>(posTrigrams.values());
        Collections.sort(sortedPOSTrigrams);

        sortedMixedTrigrams = new ArrayList<MixedTrigramInfo>(mixedTrigrams.values());
        Collections.sort(sortedMixedTrigrams);
    }

    private synchronized boolean addPOSForWord(String word, short posId) {
        totalWords++;
        int id = wordAlphabet.lookup(word, true);

        if (wordPOSMapping.containsKey(id)) {
            WordPOSInfo wordPOSInfo = wordPOSMapping.get(id);
            wordPOSInfo.increaseFreq();
            return wordPOSInfo.addPosTags(posId);
        } else {
            WordPOSInfo wordPOSInfo = new WordPOSInfo(id);
            wordPOSInfo.addPosTags(posId);
            wordPOSMapping.put(id, wordPOSInfo);
            return true;
        }
    }

    private synchronized void addMixedTrigram(String[] mixedTrigram) {
        totalMixedTrigrams++;
        short posID1 = posAlphabet.lookup(mixedTrigram[0]);
        int wordID = wordAlphabet.lookup(mixedTrigram[1]);
        short posID2 = posAlphabet.lookup(mixedTrigram[2]);
        long id = MixedTrigramInfo.generateID(posID1, wordID, posID2);

        if (mixedTrigrams.containsKey(id)){
            MixedTrigramInfo trigram = mixedTrigrams.get(id);
            trigram.increaseFreq();
        } else {
            MixedTrigramInfo trigram = new MixedTrigramInfo(posID1, wordID, posID2);
            mixedTrigrams.put(id, trigram);
        }
    }

    private synchronized void addPOSTrigram(String[] posTrigram) {
        totalPOSTrigrams++;

        short[] posIDs = new short[] {
                posAlphabet.lookup(posTrigram[0]),
                posAlphabet.lookup(posTrigram[1]),
                posAlphabet.lookup(posTrigram[2])
        };

        long id = POSTrigramInfo.generateID(posIDs);

        if (posTrigrams.containsKey(id)){
            POSTrigramInfo trigram = posTrigrams.get(id);
            trigram.increaseFreq();
        } else {
            POSTrigramInfo trigram = new POSTrigramInfo(posIDs);
            posTrigrams.put(id, trigram);
        }
    }

    private synchronized void addTaggedSentence(StanfordPOSTagger.StanfordSentence stanfordSentence) {
        StringBuilder builder = new StringBuilder();

        for (CoreLabel label : stanfordSentence) {
            builder.append(label.word());
            builder.append("/");
            builder.append(label.get(CoreAnnotations.PartOfSpeechAnnotation.class));
            builder.append(" ");
        }

        taggedSentences.add(builder.toString());
    }

    public long getTotalWords() {
        return totalWords;
    }

    public long getTotalPOSTrigrams() {
        return totalPOSTrigrams;
    }

    public long getTotalMixedTrigrams() {
        return totalMixedTrigrams;
    }

    public void printStatistics() {
        int posCount = posAlphabet.size();
        posAlphabet.print();
        System.out.println();
        int wordCount = wordAlphabet.size();
        //WordAlphabet.getInstance().print();
        //System.out.println();

        double total = getTotalWords();
        ArrayList<WordPOSInfo> wordPOSInfos = getWordPOSMapping();
        System.out.println("total words: " + total);
        System.out.println("total distinct words: " + wordPOSInfos.size());

        int n = 0;
        for (WordPOSInfo wordPOSInfo : wordPOSInfos) {
            double percent = wordPOSInfo.getFreq() * 100. / total;
            int wordId = wordPOSInfo.getWordId();
            String test = String.format("%s %.4f%% =", wordAlphabet.lookupValue(wordId), percent);
            List<Short> posIdList = wordPOSInfo.getPosTagIDs();

            for (short posId : posIdList) {
                test += " " + posAlphabet.lookupValue(posId);
            }

            //if (posList.size() > 3 || wordInfo.getFreq() > 100) {
                System.out.println(test);
            //}
            if (n++ > 100) break;
        }

        long totalPOSTrigrams = getTotalPOSTrigrams();
        ArrayList<POSTrigramInfo> posTrigramInfos = getPOSTrigrams();
        System.out.println("\ntotal POS trigrams: " + totalPOSTrigrams);
        System.out.println("total distinct POS trigrams: " + posTrigramInfos.size() +
                " of " + posCount * posCount * posCount );

        n = 0;
        for (POSTrigramInfo posTrigramInfo : posTrigramInfos) {
            double percent = posTrigramInfo.getFreq() * 100. / totalPOSTrigrams;
            short[] posIDs = posTrigramInfo.getPosIDs();
            String test = String.format("%s_%s_%s %.4f%%", posAlphabet.lookupValue(posIDs[0]),
                    posAlphabet.lookupValue(posIDs[1]), posAlphabet.lookupValue(posIDs[2]), percent);

            //if (posTrigramInfo.getFreq() > 100) {
                System.out.println(test);
            //}

            if (n++ > 100) break;
        }

        long totalMixedTrigrams = getTotalMixedTrigrams();
        ArrayList<MixedTrigramInfo> mixedTrigramInfos = getMixedTrigrams();
        System.out.println("\ntotal Mixed trigrams: " + totalMixedTrigrams);
        System.out.println("total distinct Mixed trigrams: " + mixedTrigramInfos.size() +
                " of " + posCount * wordCount * posCount );

        n = 0;
        for (MixedTrigramInfo mixedTrigramInfo : mixedTrigramInfos) {
            double percent = mixedTrigramInfo.getFreq() * 100. / totalPOSTrigrams;
            short posID1 = mixedTrigramInfo.getPosID1();
            int wordId = mixedTrigramInfo.getWordID();
            short posID2 = mixedTrigramInfo.getPosID2();
            String test = String.format("%s_%s_%s %.4f%%", posAlphabet.lookupValue(posID1),
                    wordAlphabet.lookupValue(wordId), posAlphabet.lookupValue(posID2), percent);

            //if (mixedTrigramInfo.getFreq() > 100) {
                System.out.println(test);
            //}

            if (n++ > 100) break;
        }
    }

    public void write(String file) throws IOException {
        ObjectOutputStream oos;

        String wordAlphabetFile = file + ".wab";
        String posAlphabetFile = file + ".pab";
        String wordPOSMappingFile = file + ".wpm";
        String posTrigramsFile = file + ".ptg";
        String mixedTrigramsFile = file + ".mtg";

        // write alphabets
        oos = new ObjectOutputStream(new FileOutputStream(posAlphabetFile));
        posAlphabet.write(oos);
        oos.close();
        oos = new ObjectOutputStream(new FileOutputStream(wordAlphabetFile));
        wordAlphabet.write(oos);
        oos.close();

        // Write word POS mappings
        oos = new ObjectOutputStream(new FileOutputStream(wordPOSMappingFile));
        oos.writeLong(totalWords);
        oos.writeObject(sortedWordPOSMappings);
        oos.close();

        // Write POS trigrams
        oos = new ObjectOutputStream(new FileOutputStream(posTrigramsFile));
        oos.writeLong(totalPOSTrigrams);
        oos.writeObject(posTrigrams);
        oos.close();

        // Write mixed trigrams
        oos = new ObjectOutputStream(new FileOutputStream(mixedTrigramsFile));
        oos.writeLong(totalMixedTrigrams);
        oos.writeObject(mixedTrigrams);
        oos.close();
    }

    public void writeTraining(String file) throws IOException {
        // Write training data
        int part = 1;

        int i = 0;
        int n = taggedSentences.size();
        while (i < n) {
            String partFile = String.format("%s%03d", file, part);
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(partFile)));

            for (int j=0;i<n && j<SENTENCE_PER_FILE;i++, j++) {
                String taggedSentence = taggedSentences.get(i);
                writer.write(taggedSentence);
                writer.write("\n\n");
            }

            writer.close();
            part++;
        }
    }

    public static Map<List<String>, List<Short>> readTrainingFiles(List<String> files, Corpa corpa) throws IOException {
        Map<List<String>, List<Short>> instances = new HashMap<List<String>, List<Short>>();

        for (String file : files) {
            File trainingFile = new File(file);
            BufferedReader br = new BufferedReader(new FileReader(trainingFile));

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.equals("")) continue;

                String[] tokens = line.split(" ");
                List<String> x = new ArrayList<String>();
                List<Short> y = new ArrayList<Short>();
                y.add(StartNode.SHORT_INDEX);
                y.add(StartNode.SHORT_INDEX);
                for (String token : tokens) {
                    int splitPos = token.lastIndexOf("/");
                    String word = token.substring(0, splitPos);
                    String pos = token.substring(splitPos + 1);

                    x.add(word);
                    try {
                        y.add(corpa.getPOSID(pos));
                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                    }
                }
                y.add(EndNode.SHORT_INDEX);
                y.add(EndNode.SHORT_INDEX);

                instances.put(x, y);
            }
            br.close();

        }

        return instances;
    }

    public void read(String file) throws IOException, ClassNotFoundException {
        ObjectInputStream ois;

        String wordAlphabetFile = file + ".wab";
        String posAlphabetFile = file + ".pab";
        String wordPOSMappingFile = file + ".wpm";
        String posTrigramsFile = file + ".ptg";
        String mixedTrigramsFile = file + ".mtg";

        // read alphabets
        ois = new ObjectInputStream(new FileInputStream(posAlphabetFile));
        posAlphabet.read(ois);
        ois.close();
        ois = new ObjectInputStream(new FileInputStream(wordAlphabetFile));
        wordAlphabet.read(ois);
        ois.close();

        // read word POS mappings
        ois = new ObjectInputStream(new FileInputStream(wordPOSMappingFile));
        totalWords = ois.readLong();
        sortedWordPOSMappings = (ArrayList<WordPOSInfo>) ois.readObject();
        ois.close();

        // read POS trigrams
        ois = new ObjectInputStream(new FileInputStream(posTrigramsFile));
        totalPOSTrigrams = ois.readLong();
        Map<Long, POSTrigramInfo> posTrigramsTmp = (Map<Long, POSTrigramInfo>) ois.readObject();
        ois.close();

        // read mixed trigrams
        ois = new ObjectInputStream(new FileInputStream(mixedTrigramsFile));
        totalMixedTrigrams = ois.readLong();
        Map<Long, MixedTrigramInfo> mixedTrigramsTmp = (Map<Long, MixedTrigramInfo>) ois.readObject();
        ois.close();

        generateTrigrams();
    }
    // </editor-fold>

    private class TestFileParserRunnable implements Runnable {
        private File file;

        public TestFileParserRunnable(File file) {
            this.file = file;
        }

        public void analyzeSentence(ISentence sentence) {
            StanfordPOSTagger.StanfordSentence stanfordSentence = (StanfordPOSTagger.StanfordSentence)sentence;
            posTagger.tagSentence(stanfordSentence);
            addTaggedSentence(stanfordSentence);

            String[] posTrigram = { null, StartNode.ID, StartNode.ID};
            String lastWord = StartNode.ID;
            for(CoreLabel label : stanfordSentence) {
                String pos = label.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                String word = label.word();
                short posId = posAlphabet.lookup(pos, true);
                addPOSForWord(word, posId);
                //System.out.println(word + "/" + pos);

                posTrigram[0] = posTrigram[1];
                posTrigram[1] = posTrigram[2];
                posTrigram[2] = pos;
                addPOSTrigram(posTrigram);

                String[] mixedTrigram = { posTrigram[0], lastWord, posTrigram[2]};
                addMixedTrigram(mixedTrigram);

                lastWord = word;
            }

            // final steps
            for (int n=0; n<2; n++) {
                posTrigram[0] = posTrigram[1];
                posTrigram[1] = posTrigram[2];
                posTrigram[2] = EndNode.ID;
                addPOSTrigram(posTrigram);

                String[] mixedTrigram = { posTrigram[0], lastWord, posTrigram[2]};
                addMixedTrigram(mixedTrigram);

                lastWord = EndNode.ID;
            }
        }

        @Override
        public void run() {
            //System.out.println("reading: " + file.getAbsolutePath());

            String content;
            try {
                content = ArchiveReader.readRawFile(file);

                List<ISentence> sentences = posTagger.extractSentences(content);

                for (ISentence sentence : sentences) {
                    analyzeSentence(sentence);
                }

            } catch (IOException e) {
                System.out.println("cannot read file: " + file.getAbsolutePath());
                return;
            }
        }
    }

    private class TestGeneratorPoolExecutor extends ThreadPoolExecutor {
        private static final int corePoolSize = 32;// TODO: change to 32;
        private static final int maximumPoolSize = 32;// TODO: change to 32;
        private static final long keepAliveTime = 60; // 60 s

        public TestGeneratorPoolExecutor() {
            super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        }
    }
}
