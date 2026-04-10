import java.util.*;

public class Solution {
    // Custom Entry to store metadata
    class DNSEntry {
        String ipAddress;
        long expiryTime; // Store as absolute system time in ms

        DNSEntry(String ipAddress, int ttlSeconds) {
            this.ipAddress = ipAddress;
            this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000L);
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    private final int MAX_CAPACITY = 1000;
    private long hits = 0;
    private long misses = 0;

    // LinkedHashMap with accessOrder=true handles LRU automatically
    private Map<String, DNSEntry> cache = new LinkedHashMap<String, DNSEntry>(MAX_CAPACITY, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
            return size() > MAX_CAPACITY;
        }
    };

    /**
     * Resolves domain to IP with TTL check and hit/miss tracking.
     */
    public String resolve(String domain) {
        DNSEntry entry = cache.get(domain);

        if (entry != null) {
            if (!entry.isExpired()) {
                hits++;
                return entry.ipAddress + " (Cache HIT)";
            } else {
                // Remove expired entry
                cache.remove(domain);
            }
        }

        // Simulate Upstream DNS Query (Cache MISS or EXPIRED)
        misses++;
        String resolvedIp = queryUpstreamDNS(domain);
        int ttl = 300; // 5-minute TTL
        cache.put(domain, new DNSEntry(resolvedIp, ttl));

        return resolvedIp + " (Upstream Query)";
    }

    private String queryUpstreamDNS(String domain) {
        // Mock IP generation
        return "172.217." + (int)(Math.random() * 255) + "." + (int)(Math.random() * 255);
    }

    public void getCacheStats() {
        double total = hits + misses;
        double hitRate = (total == 0) ? 0 : (hits / total) * 100;
        System.out.println(String.format("Cache Stats - Hits: %d, Misses: %d, Hit Rate: %.2f%%", hits, misses, hitRate));
    }

    public static void main(String[] args) throws InterruptedException {
        Solution dns = new Solution();

        // First lookup (MISS)
        System.out.println("google.com -> " + dns.resolve("google.com"));

        // Second lookup (HIT)
        System.out.println("google.com -> " + dns.resolve("google.com"));

        // Simulate TTL Expiry for a short-lived entry
        dns.cache.put("fast-expire.com", dns.new DNSEntry("1.1.1.1", 1)); // 1 second TTL
        System.out.println("fast-expire.com -> " + dns.resolve("fast-expire.com")); // HIT

        Thread.sleep(1100); // Wait for expiration

        System.out.println("fast-expire.com (after 1s) -> " + dns.resolve("fast-expire.com")); // EXPIRED -> MISS

        dns.getCacheStats();
    }
}