import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Solution {
    // Thread-safe map for O(1) stock lookups
    private ConcurrentHashMap<String, AtomicInteger> inventory = new ConcurrentHashMap<>();

    // Maps product IDs to a Queue of User IDs for the waiting list (FIFO)
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<Integer>> waitingLists = new ConcurrentHashMap<>();

    public Solution() {}

    /**
     * Initializes stock for a product.
     */
    public void addProduct(String productId, int initialStock) {
        inventory.put(productId, new AtomicInteger(initialStock));
        waitingLists.put(productId, new ConcurrentLinkedQueue<>());
    }

    /**
     * Checks stock in O(1) time.
     */
    public int checkStock(String productId) {
        AtomicInteger stock = inventory.get(productId);
        return (stock != null) ? stock.get() : 0;
    }

    /**
     * Processes purchase with atomic decrement to prevent overselling.
     */
    public String purchaseItem(String productId, int userId) {
        AtomicInteger stock = inventory.get(productId);

        if (stock == null) return "Product not found";

        // Atomic update: only decrement if value > 0
        // getAndUpdate ensures thread safety during high concurrency
        int currentStock = stock.getAndUpdate(s -> (s > 0) ? s - 1 : 0);

        if (currentStock > 0) {
            return "User " + userId + ": Success, " + (currentStock - 1) + " units remaining";
        } else {
            // Stock is 0, add to waiting list (FIFO)
            ConcurrentLinkedQueue<Integer> list = waitingLists.get(productId);
            list.add(userId);
            return "User " + userId + ": Added to waiting list, position #" + list.size();
        }
    }

    public static void main(String[] args) {
        Solution flashSale = new Solution();
        String productId = "IPHONE15_256GB";

        // 1. Setup inventory with a small stock for testing
        flashSale.addProduct(productId, 3);
        System.out.println("Initial stock: " + flashSale.checkStock(productId));
        System.out.println("-------------------------------------------");

        // 2. Simulate 5 users trying to buy (3 will succeed, 2 will waitlist)
        System.out.println(flashSale.purchaseItem(productId, 101));
        System.out.println(flashSale.purchaseItem(productId, 102));
        System.out.println(flashSale.purchaseItem(productId, 103));

        // These users should go to the waiting list automatically
        System.out.println(flashSale.purchaseItem(productId, 104));
        System.out.println(flashSale.purchaseItem(productId, 105));

        // 3. Final verification
        System.out.println("-------------------------------------------");
        System.out.println("Final stock count: " + flashSale.checkStock(productId));
    }
}