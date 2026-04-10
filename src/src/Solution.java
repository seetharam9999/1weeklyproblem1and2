import java.util.*;

public class Solution {
    // Inverted Index: N-gram Hash -> Set of Document IDs that contain it
    private Map<Long, Set<String>> ngramIndex = new HashMap<>();
    // Store total n-gram count per document to calculate similarity %
    private Map<String, Integer> docTotalNgrams = new HashMap<>();

    private final int N = 5; // Using 5-grams for balance between speed and accuracy

    /**
     * Indexes a document by breaking it into N-grams and hashing them.
     */
    public void indexDocument(String docId, String content) {
        String[] words = content.toLowerCase().split("\\s+");
        int count = 0;

        for (int i = 0; i <= words.length - N; i++) {
            long hash = generateNgramHash(words, i, i + N);
            ngramIndex.computeIfAbsent(hash, k -> new HashSet<>()).add(docId);
            count++;
        }
        docTotalNgrams.put(docId, count);
    }

    /**
     * Checks a new document against the database.
     */
    public void analyzeDocument(String newDocId, String content) {
        String[] words = content.toLowerCase().split("\\s+");
        Map<String, Integer> matchCounts = new HashMap<>();
        int totalNgrams = 0;

        for (int i = 0; i <= words.length - N; i++) {
            long hash = generateNgramHash(words, i, i + N);
            totalNgrams++;

            if (ngramIndex.containsKey(hash)) {
                for (String matchingDocId : ngramIndex.get(hash)) {
                    matchCounts.put(matchingDocId, matchCounts.getOrDefault(matchingDocId, 0) + 1);
                }
            }
        }

        System.out.println("Analysis for: " + newDocId);
        for (Map.Entry<String, Integer> entry : matchCounts.entrySet()) {
            String otherDocId = entry.getKey();
            double similarity = (entry.getValue() * 100.0) / totalNgrams;

            System.out.printf("-> Found %d matches with %s. Similarity: %.1f%%%s\n",
                    entry.getValue(), otherDocId, similarity,
                    (similarity > 50 ? " [PLAGIARISM DETECTED]" : similarity > 15 ? " [SUSPICIOUS]" : ""));
        }
    }

    // A simple polynomial rolling hash for the n-gram
    private long generateNgramHash(String[] words, int start, int end) {
        long h = 0;
        for (int i = start; i < end; i++) {
            h = 31 * h + words[i].hashCode();
        }
        return h;
    }

    public static void main(String[] args) {
        Solution detector = new Solution();

        // Database
        detector.indexDocument("essay_089.txt", "The quick brown fox jumps over the lazy dog");
        detector.indexDocument("essay_092.txt", "Data structures are essential for efficient software development and design");

        // New Submission
        String submission = "Data structures are vital for efficient software systems and design";
        detector.analyzeDocument("student_submission.txt", submission);
    }
}