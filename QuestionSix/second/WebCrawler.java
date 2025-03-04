package QuestionSix.second;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler {

    // Thread-safe queue for managing URLs to be crawled
    private final Queue<String> urlQueue = new ConcurrentLinkedQueue<>();
    // Track visited URLs to prevent duplicate processing
    private final Set<String> visitedUrls = new HashSet<>();
    // Thread pool for concurrent crawling
    private final ExecutorService executorService;
    // Configuration parameters
    private final int numThreads;
    private final Pattern urlPattern = Pattern.compile("href=[\"']([^\"']+)[\"']");
    private final int maxPages; // Maximum number of pages to crawl
    private final String domainFilter; // Restrict crawling to specific domain
    // Flag to coordinate graceful shutdown across threads
    private volatile boolean isDone = false;

    public WebCrawler(int numThreads, int maxPages, String domainFilter) {
        this.executorService = Executors.newFixedThreadPool(numThreads);
        this.numThreads = numThreads;
        this.maxPages = maxPages;
        this.domainFilter = domainFilter;
    }

    /**
     * Starts the crawling process from the initial URL
     */
    public void startCrawling(String startUrl) {
        urlQueue.add(startUrl);
        // Start worker threads
        for (int i = 0; i < numThreads; i++) {
            executorService.submit(this::crawlPages);
        }
        executorService.shutdown();
        try {
            // Allow threads up to 30 seconds to finish after shutdown signal
            executorService.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("Crawling interrupted: " + e.getMessage());
        }
        System.out.println("Crawling completed. Visited " + visitedUrls.size() + " pages.");
    }

    /**
     * Worker thread method for processing URLs from the queue Implements empty
     * queue detection with polling and timeout
     */
    private void crawlPages() {
        int emptyPolls = 0;
        final int MAX_EMPTY_POLLS = 5; // Consecutive empty polls before considering work done

        while (!isDone) {
            String url = urlQueue.poll();
            if (url == null) {
                emptyPolls++;
                // Stop if queue remains empty beyond threshold
                if (emptyPolls >= MAX_EMPTY_POLLS) {
                    isDone = true; // Signal global completion
                    break;
                }
                // Brief pause to prevent CPU spin on empty queue
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                continue;
            } else {
                emptyPolls = 0; // Reset counter on successful poll
            }

            // Synchronize access to shared visited URLs set
            synchronized (visitedUrls) {
                if (visitedUrls.contains(url)) {
                    continue;
                }
                visitedUrls.add(url);
                // Check if reached page limit
                if (visitedUrls.size() >= maxPages) {
                    isDone = true;
                    break;
                }
            }

            try {
                System.out.println(Thread.currentThread().getName() + " crawling: " + url);
                String content = fetchPage(url);
                processContent(url, content);
                // Extract and queue new URLs
                Set<String> newUrls = extractUrls(content);
                for (String newUrl : newUrls) {
                    if (shouldCrawl(newUrl)) {
                        urlQueue.add(newUrl);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error crawling " + url + ": " + e.getMessage());
            }
        }
    }

    private boolean shouldCrawl(String url) {
        // Skip domain check if no filter specified
        return domainFilter == null || url.contains(domainFilter);
    }

    /**
     * Fetches page content from a URL
     */
    private String fetchPage(String urlString) throws IOException {
        URL url = new URL(urlString);
        StringBuilder content = new StringBuilder();

        // Read content line-by-line from URL stream
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }

        return content.toString();
    }

    /**
     * Processes page content to extract metadata
     */
    private void processContent(String url, String content) {
        // Simple title extraction demonstration
        Pattern titlePattern = Pattern.compile("<title>(.*?)</title>");
        Matcher titleMatcher = titlePattern.matcher(content);
        if (titleMatcher.find()) {
            System.out.println("Title: " + titleMatcher.group(1));
        }
    }

    /**
     * Extracts URLs from HTML content using regex pattern Note: Simple
     * implementation - real-world use would require more robust parsing
     *
     */
    private Set<String> extractUrls(String content) {
        Set<String> foundUrls = new HashSet<>();
        Matcher matcher = urlPattern.matcher(content);

        // Find all href attribute values
        while (matcher.find()) {
            String url = matcher.group(1);
            // Basic handling of relative URLs (implementation simplified)
            if (url.startsWith("http")) {
                foundUrls.add(url);
            }
        }

        return foundUrls;
    }

    /**
     * Test method to crawl multiple sites concurrently Demonstrates parallel
     * crawling of different domains
     */
    private static void testMultipleSites() {
        String[][] sites = {
            {"https://twbcreates.com", "twbcreates.com"},
            {"https://example.com", "example.com"},
            {"https://wikipedia.org", "wikipedia.org"}
        };

        // Create separate thread pool for site crawling tasks
        ExecutorService siteExecutor = Executors.newFixedThreadPool(sites.length);
        for (String[] site : sites) {
            siteExecutor.submit(() -> testSite(site[0], site[1]));
        }
        siteExecutor.shutdown();
        try {
            siteExecutor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper method for testing individual site crawling
     *
     */
    private static void testSite(String url, String domain) {
        System.out.println("\n===== CRAWLING " + domain + " =====");
        int numThreads = 5;
        int maxPages = 50;
        WebCrawler crawler = new WebCrawler(numThreads, maxPages, domain);
        crawler.startCrawling(url);
        System.out.println("===== FINISHED CRAWLING " + domain + " =====\n");
    }

    public static void main(String[] args) {
        testMultipleSites();
    }
}
