import java.util.*;
import java.util.concurrent.*;

public class Solution {
    // Dimension 1: Page Views
    private Map<String, Integer> pageViews = new ConcurrentHashMap<>();
    // Dimension 2: Unique Visitors (URL -> Set of User IDs)
    private Map<String, Set<String>> uniqueVisitors = new ConcurrentHashMap<>();
    // Dimension 3: Traffic Sources
    private Map<String, Integer> sourceCounts = new ConcurrentHashMap<>();

    /**
     * Processes a single page view event in O(1) average time.
     */
    public void processEvent(String url, String userId, String source) {
        // Increment page views
        pageViews.put(url, pageViews.getOrDefault(url, 0) + 1);

        // Track unique visitors
        uniqueVisitors.computeIfAbsent(url, k -> ConcurrentHashMap.newKeySet()).add(userId);

        // Track traffic sources
        sourceCounts.put(source, sourceCounts.getOrDefault(source, 0) + 1);
    }

    /**
     * Extracts the Top N pages using a PriorityQueue in O(P log K)
     * where P is total pages and K is top N.
     */
    public List<Map.Entry<String, Integer>> getTopPages(int k) {
        PriorityQueue<Map.Entry<String, Integer>> minHeap =
                new PriorityQueue<>(Comparator.comparingInt(Map.Entry::getValue));

        for (Map.Entry<String, Integer> entry : pageViews.entrySet()) {
            minHeap.offer(entry);
            if (minHeap.size() > k) {
                minHeap.poll(); // Remove the smallest
            }
        }

        List<Map.Entry<String, Integer>> result = new ArrayList<>(minHeap);
        Collections.reverse(result);
        return result;
    }

    public void getDashboard() {
        System.out.println("--- REAL-TIME DASHBOARD (Last 5s) ---");
        System.out.println("Top Pages:");
        List<Map.Entry<String, Integer>> top = getTopPages(3);
        for (Map.Entry<String, Integer> e : top) {
            int unique = uniqueVisitors.get(e.getKey()).size();
            System.out.println(e.getKey() + " - " + e.getValue() + " views (" + unique + " unique)");
        }

        System.out.println("\nTraffic Sources: " + sourceCounts);
        System.out.println("-------------------------------------");
    }

    public static void main(String[] args) {
        Solution analytics = new Solution();

        // Simulate traffic
        analytics.processEvent("/news/breaking", "u1", "google");
        analytics.processEvent("/news/breaking", "u2", "facebook");
        analytics.processEvent("/news/breaking", "u1", "google"); // u1 is duplicate
        analytics.processEvent("/sports/live", "u3", "direct");

        analytics.getDashboard();
    }
}