import java.util.*;

public class Solution {
    // Primary storage for taken usernames (Username -> UserID)
    private Map<String, Integer> userRegistry;
    // Counter for tracking how many times a name is searched
    private Map<String, Integer> popularityMap;
    // Counter for generating unique IDs for new registrations
    private int nextId;

    public Solution() {
        this.userRegistry = new HashMap<>();
        this.popularityMap = new HashMap<>();
        this.nextId = 1;
    }

    /**
     * Checks availability in O(1) time and tracks popularity.
     */
    public boolean checkAvailability(String username) {
        // Increment search frequency
        popularityMap.put(username, popularityMap.getOrDefault(username, 0) + 1);

        // HashMap containsKey is O(1) average case
        return !userRegistry.containsKey(username.toLowerCase());
    }

    /**
     * Generates suggestions by modifying the original string.
     */
    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();
        int count = 1;

        // Strategy 1: Append numbers
        while (suggestions.size() < 2) {
            String candidate = username + count;
            if (!userRegistry.containsKey(candidate)) {
                suggestions.add(candidate);
            }
            count++;
        }

        // Strategy 2: Add a separator (dot) if not already present
        if (!username.contains(".")) {
            String candidate = username.replace("_", ".") + "1";
            if (!userRegistry.containsKey(candidate)) {
                suggestions.add(candidate);
            }
        }

        return suggestions;
    }

    /**
     * Returns the username that has been checked the most.
     */
    public String getMostAttempted() {
        return popularityMap.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No attempts yet");
    }

    // Helper method to simulate a registration
    public void registerUser(String username) {
        userRegistry.put(username.toLowerCase(), nextId++);
    }

    public static void main(String[] args) {
        Solution system = new Solution();

        // Simulate existing users
        system.registerUser("john_doe");
        system.registerUser("admin");

        // Test Availability
        System.out.println("john_doe available: " + system.checkAvailability("john_doe")); // false
        System.out.println("jane_smith available: " + system.checkAvailability("jane_smith")); // true

        // Test Suggestions
        System.out.println("Suggestions for john_doe: " + system.suggestAlternatives("john_doe"));

        // Test Popularity (multiple checks for 'admin')
        system.checkAvailability("admin");
        system.checkAvailability("admin");
        System.out.println("Most attempted: " + system.getMostAttempted());
    }
}