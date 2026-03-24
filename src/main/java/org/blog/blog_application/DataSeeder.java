// src/main/java/org/blog/blog_application/DataSeeder.java
package org.blog.blog_application;

import org.blog.blog_application.models.*;
import org.blog.blog_application.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Component
@Profile("dev")   // only runs when spring.profiles.active=dev
public class DataSeeder implements CommandLineRunner {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(PostRepository postRepository,
                      UserRepository userRepository,
                      TagRepository tagRepository,
                      PostTagRepository postTagRepository,
                      PasswordEncoder passwordEncoder) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.postTagRepository = postTagRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ── Seed data ─────────────────────────────────────────────────────────────

    private static final String[][] POST_TEMPLATES = {
            // { title, excerpt, content, tags (comma-separated) }
            {
                    "CAP Theorem Explained",
                    "Understanding the trade-offs between Consistency, Availability, and Partition Tolerance.",
                    """
            The CAP theorem states that a distributed system can only guarantee two of the
            following three properties simultaneously: Consistency, Availability, and
            Partition Tolerance.

            **Consistency** means every read receives the most recent write or an error.
            **Availability** means every request receives a response (not necessarily the latest data).
            **Partition Tolerance** means the system continues to operate despite network partitions.

            In practice, partition tolerance is non-negotiable in distributed systems since
            network failures are inevitable. This forces architects to choose between CP
            (consistent + partition-tolerant) systems like HBase and Zookeeper, or AP
            (available + partition-tolerant) systems like Cassandra and DynamoDB.

            Real-world systems often blur this line by offering tunable consistency levels,
            allowing developers to make the trade-off per operation.
            """,
                    "system-design,distributed-systems,databases"
            },
            {
                    "Designing a URL Shortener",
                    "Step-by-step breakdown of building a scalable URL shortening service like bit.ly.",
                    """
            A URL shortener maps a long URL to a short alias and redirects users when the
            alias is accessed. Let's design one that handles 100M URLs and 10B redirects/month.

            **Requirements**
            - Functional: shorten URL, redirect, custom aliases, expiry
            - Non-functional: low latency reads, high availability, eventual consistency ok

            **Capacity Estimation**
            - 100M new URLs/month → ~40 writes/sec
            - 10B redirects/month → ~4000 reads/sec
            - Read:write ratio = 100:1

            **Core Design**
            1. Generate a 6-character Base62 key (62^6 = 56 billion unique keys)
            2. Use a counter-based or hash-based approach
            3. Store in a key-value store (Redis for hot URLs, Cassandra for persistence)
            4. CDN cache redirects to reduce DB load

            **Database Schema**
            - url_mappings(short_key PK, long_url, user_id, created_at, expires_at, click_count)

            **Redirect flow**: Client → Load Balancer → App Server → Redis cache →
            (miss) → Cassandra → 301/302 redirect
            """,
                    "system-design,scalability,databases,caching"
            },
            {
                    "Load Balancing Strategies",
                    "Round Robin, Least Connections, IP Hash — when to use which strategy.",
                    """
            A load balancer distributes incoming traffic across multiple servers to ensure
            no single server becomes a bottleneck.

            **Round Robin**: Requests are distributed sequentially. Simple and works well
            when all servers have equal capacity and request processing time is similar.

            **Weighted Round Robin**: Servers with higher capacity get more requests.
            Useful in heterogeneous server fleets.

            **Least Connections**: Route to the server with fewest active connections.
            Best for long-lived connections like WebSockets or file uploads.

            **IP Hash**: Same client IP always routes to the same server. Useful for
            session persistence without sticky sessions in the app layer.

            **Layer 4 vs Layer 7**
            - L4 load balancers work at the transport layer (TCP/UDP) — fast but dumb
            - L7 load balancers inspect HTTP headers, cookies, URLs — slower but smart

            Tools: NGINX, HAProxy, AWS ALB (L7), AWS NLB (L4).
            """,
                    "system-design,networking,infrastructure"
            },
            {
                    "Database Sharding Deep Dive",
                    "Horizontal partitioning strategies and the trade-offs that come with them.",
                    """
            Sharding splits a large database into smaller, faster, more manageable pieces
            called shards. Each shard holds a subset of the data.

            **Sharding Strategies**

            *Range-based*: Shard by a range of values (e.g. user IDs 1-1M on shard 1).
            Simple to implement but can cause hotspots if certain ranges are busier.

            *Hash-based*: Apply a hash function to the shard key. Distributes evenly but
            makes range queries expensive.

            *Directory-based*: A lookup service maps keys to shards. Flexible but the
            lookup service becomes a single point of failure.

            **Problems with Sharding**
            - Joins across shards are expensive or impossible
            - Resharding is painful — consistent hashing helps here
            - Cross-shard transactions require distributed transaction protocols (2PC)
            - Hot shards under uneven load

            **When to shard**: Only after you've exhausted vertical scaling, read replicas,
            and caching. Sharding adds enormous operational complexity.
            """,
                    "system-design,databases,scalability"
            },
            {
                    "Redis as a Cache: Patterns and Pitfalls",
                    "Cache-aside, write-through, write-behind — choosing the right caching strategy.",
                    """
            Redis is an in-memory data store used as a cache, message broker, and
            session store. Here are the main caching patterns:

            **Cache-Aside (Lazy Loading)**
            App checks cache first. On miss, loads from DB and populates cache.
            Pros: Only requested data is cached. Cons: Cache miss penalty, stale data.

            **Write-Through**
            Every write goes to cache and DB simultaneously.
            Pros: Cache is always fresh. Cons: Write latency, cache churn for rarely-read data.

            **Write-Behind (Write-Back)**
            Write to cache immediately, async write to DB later.
            Pros: Low write latency. Cons: Risk of data loss on cache failure.

            **Read-Through**
            Cache sits in front of DB; on miss, cache loads from DB automatically.
            Used by libraries like Ehcache and NCache.

            **Common Pitfalls**
            - Cache stampede: many requests hit DB simultaneously on a cold start
            - Cache penetration: queries for non-existent keys bypass cache every time
            - Cache avalanche: many keys expire at the same time

            Solutions: lock/mutex on miss, bloom filters for penetration, jittered TTLs for avalanche.
            """,
                    "system-design,caching,redis,databases"
            },
            {
                    "Designing a Notification System",
                    "Push, pull, WebSockets, and fan-out — building notifications at scale.",
                    """
            A notification system needs to deliver messages to millions of users across
            email, SMS, push notifications, and in-app alerts.

            **High-Level Architecture**
            1. Event producers (payment service, social service) emit events
            2. Message queue (Kafka) buffers events
            3. Notification service consumes events and routes to channels
            4. Third-party providers: APNs (iOS), FCM (Android), Twilio (SMS), SendGrid (email)

            **Fan-out Strategies**
            - Fan-out on write: precompute and push to all followers' feeds on write.
              Fast reads, expensive writes. Good for users with few followers.
            - Fan-out on read: compute feed on read. Slow reads, cheap writes.
              Good for celebrity users with millions of followers.
            - Hybrid: fan-out on write for regular users, fan-out on read for celebrities.

            **Delivery Guarantees**
            - At-least-once: Kafka default. Handle duplicates in consumers.
            - Exactly-once: expensive, use idempotency keys.

            **Rate Limiting**: Throttle per user and per channel to prevent spam.
            """,
                    "system-design,messaging,kafka,scalability"
            },
            {
                    "Consistent Hashing Explained",
                    "How consistent hashing solves the resharding problem in distributed systems.",
                    """
            Traditional hashing assigns keys using `hash(key) % N` where N is the number
            of servers. The problem: when N changes (server added or removed), almost all
            keys get remapped — causing a massive cache invalidation storm.

            **Consistent Hashing**
            Map both servers and keys onto a circular hash ring (0 to 2^32).
            Each key is assigned to the first server clockwise from its position.
            When a server is added or removed, only K/N keys need to be remapped
            (where K = total keys, N = number of servers).

            **Virtual Nodes**
            A single server is represented by multiple points on the ring (virtual nodes).
            This ensures more even distribution and reduces hotspots when nodes fail.
            Each physical server might have 100-200 virtual nodes.

            **Used in**: Amazon DynamoDB, Apache Cassandra, Memcached (ketama).

            **Example**: Cassandra uses consistent hashing with virtual nodes to determine
            which node stores each row. The partition key is hashed to find the token,
            and the token maps to a node on the ring.
            """,
                    "system-design,distributed-systems,algorithms"
            },
            {
                    "API Gateway Pattern",
                    "Why a single entry point for your microservices is worth the complexity.",
                    """
            An API Gateway is a server that acts as the single entry point for all client
            requests in a microservices architecture.

            **What it does**
            - Request routing to appropriate microservices
            - Authentication and authorization (JWT validation)
            - Rate limiting and throttling
            - SSL termination
            - Request/response transformation
            - Load balancing
            - Caching
            - Logging and monitoring

            **Without API Gateway**
            Clients need to know the address of every microservice. Adding auth to each
            service duplicates logic. CORS, rate limiting, logging — all duplicated.

            **With API Gateway**
            Single entry point simplifies client. Cross-cutting concerns handled once.
            Trade-off: potential single point of failure (mitigated by running multiple
            gateway instances behind a load balancer).

            **Popular options**: Kong, AWS API Gateway, NGINX, Traefik, Spring Cloud Gateway.

            **Backend for Frontend (BFF)**: A variant where each client type (mobile, web)
            gets its own gateway tailored to its needs.
            """,
                    "system-design,microservices,api,architecture"
            },
            {
                    "Event Sourcing and CQRS",
                    "Storing state as a sequence of events instead of current state.",
                    """
            **Event Sourcing** stores the state of a system as an ordered sequence of events
            rather than the current state. To get current state, replay all events.

            Example: Instead of storing `account.balance = 500`, store:
            - AccountCreated { balance: 1000 }
            - MoneyWithdrawn { amount: 200 }
            - MoneyWithdrawn { amount: 300 }

            **Benefits**
            - Complete audit log built-in
            - Replay events to reconstruct any past state
            - Easy to build new projections from existing events
            - Natural fit for event-driven architectures

            **CQRS (Command Query Responsibility Segregation)**
            Separate the write model (Commands) from the read model (Queries).
            Writes go through the command side which emits events.
            Events update one or more read-optimized projections (query side).

            **Together**: Event Sourcing + CQRS gives you highly scalable, auditable systems
            but adds significant complexity — event schema evolution, eventual consistency,
            and the learning curve for developers unfamiliar with the pattern.

            **Used in**: Banking systems, e-commerce order management, collaborative editing.
            """,
                    "system-design,architecture,patterns,databases"
            },
            {
                    "Designing a Rate Limiter",
                    "Token bucket, leaky bucket, sliding window — implementing rate limiting at scale.",
                    """
            A rate limiter controls the rate of requests a client can send to an API,
            protecting backend services from abuse and ensuring fair usage.

            **Algorithms**

            *Token Bucket*: A bucket holds tokens. Each request consumes one token.
            Tokens refill at a fixed rate. Allows bursting up to bucket capacity.
            Used by AWS and Stripe.

            *Leaky Bucket*: Requests enter a queue (the bucket). Processed at a fixed rate.
            Excess requests are dropped. Smooths out bursts.

            *Fixed Window Counter*: Count requests per fixed time window (e.g. 100 req/min).
            Problem: boundary spike — 100 requests at 00:59 and 100 at 01:01 = 200 in 2 seconds.

            *Sliding Window Log*: Keep a log of request timestamps. Count entries in the
            rolling window. Accurate but memory-intensive.

            *Sliding Window Counter*: Hybrid approach combining fixed window counters with
            a weighted overlap calculation. Memory efficient and accurate.

            **Distributed Rate Limiting**
            Use Redis with atomic operations (INCR + EXPIRE) or Lua scripts for
            consistency across multiple app servers.

            **Where to enforce**: API Gateway (coarse-grained) + individual services (fine-grained).
            """,
                    "system-design,algorithms,api,redis"
            },
    };

    private static final String[] EXTRA_TOPICS = {
            "Message Queues", "Service Mesh", "Circuit Breaker Pattern", "Database Indexing",
            "SQL vs NoSQL", "Microservices vs Monolith", "Kubernetes Architecture",
            "Docker Networking", "GraphQL vs REST", "gRPC Deep Dive",
            "OAuth 2.0 Flow", "JWT Authentication", "WebSocket vs Server-Sent Events",
            "CDN Architecture", "DNS Resolution", "TCP vs UDP", "HTTP/2 vs HTTP/3",
            "Search Engine Design", "Time Series Databases", "Column-Oriented Databases",
            "Bloom Filters", "Merkle Trees", "Raft Consensus Algorithm",
            "Paxos Algorithm", "Two-Phase Commit", "Saga Pattern",
            "Designing Instagram", "Designing Twitter", "Designing Netflix",
            "Designing Uber", "Designing WhatsApp", "Designing Google Drive",
            "Designing YouTube", "Designing a Search Engine", "Designing TikTok",
            "Designing Airbnb", "Designing a Payment System", "Designing a Chat System",
            "Designing a Leaderboard", "Designing a Recommendation Engine",
            "Designing a Newsfeed", "Designing an Autocomplete System",
            "Designing a Typeahead", "Designing a Distributed Cache",
            "Designing a Distributed Lock", "Designing a Job Scheduler",
            "Designing a Log Aggregation System", "Designing a Monitoring System",
            "Designing an API Rate Limiter", "Designing a File Storage System",
    };

    private static final String[] TAG_POOL = {
            "system-design", "distributed-systems", "scalability", "databases",
            "caching", "redis", "kafka", "microservices", "api", "architecture",
            "algorithms", "networking", "infrastructure", "patterns", "messaging",
            "security", "performance", "cloud", "kubernetes", "docker"
    };

    private static final String[] EXCERPTS = {
            "A deep dive into %s with real-world examples and trade-offs.",
            "Understanding %s from first principles.",
            "How top tech companies implement %s at scale.",
            "The complete guide to %s for senior engineers.",
            "Everything you need to know about %s for system design interviews.",
            "Practical %s: lessons learned from production systems.",
            "%s explained with diagrams, code, and examples.",
            "When and how to use %s in your architecture.",
    };

    // ── Runner ────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Skip if data already exists
        if (postRepository.count() > 10) {
            System.out.println("DataSeeder: posts already exist, skipping.");
            return;
        }

        System.out.println("DataSeeder: seeding 1000 posts...");

        List<User> authors = seedAuthors();
        List<Tag> tags = seedTags();
        seedPosts(authors, tags);

        System.out.println("DataSeeder: done.");
    }

    // ── Seed authors ──────────────────────────────────────────────────────────

    private List<User> seedAuthors() {
        String[][] authorData = {
                {"Alice Chen",    "alice",   "alice@blog.com"},
                {"Bob Martin",    "bob",     "bob@blog.com"},
                {"Carol White",   "carol",   "carol@blog.com"},
                {"David Kumar",   "david",   "david@blog.com"},
                {"Eva Rodriguez", "eva",     "eva@blog.com"},
        };

        List<User> authors = new ArrayList<>();
        for (String[] data : authorData) {
            // Skip if user already exists
            userRepository.findByName(data[0]).ifPresentOrElse(
                    authors::add,
                    () -> {
                        User user = new User();
                        user.setName(data[0]);
                        user.setUsername(data[1]);
                        user.setEmail(data[2]);
                        user.setPassword(passwordEncoder.encode("password123"));
                        user.setRole(Role.ROLE_AUTHOR);
                        authors.add(userRepository.save(user));
                    }
            );
        }
        return authors;
    }

    // ── Seed tags ─────────────────────────────────────────────────────────────

    private List<Tag> seedTags() {
        List<Tag> tags = new ArrayList<>();
        for (String tagName : TAG_POOL) {
            tagRepository.findByNameIgnoreCase(tagName).ifPresentOrElse(
                    tags::add,
                    () -> {
                        Tag tag = new Tag();
                        tag.setName(tagName);
                        tags.add(tagRepository.save(tag));
                    }
            );
        }
        return tags;
    }

    // ── Seed posts ────────────────────────────────────────────────────────────

    private void seedPosts(List<User> authors, List<Tag> allTags) {
        Random random = new Random(42); // fixed seed = reproducible data
        int postCount = 0;

        // First: insert the 10 hand-crafted detailed posts
        for (String[] template : POST_TEMPLATES) {
            createPost(
                    template[0],           // title
                    template[1],           // excerpt
                    template[2].strip(),   // content
                    template[3],           // tags (comma-separated)
                    authors.get(postCount % authors.size()),
                    LocalDateTime.now().minusDays(random.nextInt(365)),
                    allTags,
                    random
            );
            postCount++;
        }

        // Then: generate the remaining 990 posts from EXTRA_TOPICS
        int topicCount = EXTRA_TOPICS.length;
        while (postCount < 1000) {
            String topic = EXTRA_TOPICS[postCount % topicCount];

            // Add a number suffix to avoid duplicate titles
            String title = postCount < topicCount
                    ? topic
                    : topic + " — Part " + (postCount / topicCount + 1);

            String excerptTemplate = EXCERPTS[random.nextInt(EXCERPTS.length)];
            String excerpt = String.format(excerptTemplate, topic);

            String content = generateContent(topic, random);

            // Pick 2-4 random tags
            String tagsCsv = pickRandomTags(allTags, 2 + random.nextInt(3), random);

            User author = authors.get(random.nextInt(authors.size()));
            // Spread posts over the past 2 years
            LocalDateTime publishedAt = LocalDateTime.now()
                    .minusDays(random.nextInt(730))
                    .minusHours(random.nextInt(24));

            createPost(title, excerpt, content, tagsCsv, author, publishedAt, allTags, random);
            postCount++;
        }
    }

    // ── Create a single post with tags ────────────────────────────────────────

    private void createPost(String title, String excerpt, String content,
                            String tagsCsv, User author,
                            LocalDateTime publishedAt,
                            List<Tag> allTags, Random random) {
        Post post = new Post();
        post.setTitle(title);
        post.setExcerpt(excerpt);
        post.setContent(content);
        post.setAuthor(author);
        post.setPublishedAt(publishedAt);

        Post saved = postRepository.save(post);

        // Attach tags
        for (String tagName : tagsCsv.split(",")) {
            tagName = tagName.trim();
            if (tagName.isEmpty()) continue;

            String finalTagName = tagName;
            Tag tag = allTags.stream()
                    .filter(t -> t.getName().equalsIgnoreCase(finalTagName))
                    .findFirst()
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setName(finalTagName);
                        return tagRepository.save(newTag);
                    });

            PostTag postTag = new PostTag();
            postTag.setPost(saved);
            postTag.setTag(tag);
            postTagRepository.save(postTag);
            saved.getPostTags().add(postTag);
        }
    }

    // ── Generate realistic content for a topic ────────────────────────────────

    private String generateContent(String topic, Random random) {
        String[] intros = {
                "When building large-scale systems, %s is one of the most critical concepts to understand.",
                "Every senior engineer is expected to have a solid grasp of %s.",
                "%s comes up in almost every system design interview and production architecture discussion.",
                "Let's break down %s from the ground up, covering both theory and practical implementation.",
        };
        String[] middles = {
                """
            **Core Concepts**
            The fundamental idea behind this approach is to separate concerns at the right
            abstraction level. By doing so, teams can scale individual components independently
            and reduce coupling between services.

            **When to use it**
            - You need horizontal scalability
            - Traffic patterns are unpredictable
            - Different components have different scaling requirements
            - You want to isolate failures
            """,
                """
            **Architecture Overview**
            The system consists of three main layers: the ingestion layer, the processing layer,
            and the storage layer. Each layer can be scaled independently based on load.

            **Key Design Decisions**
            1. Use asynchronous communication where possible to decouple producers and consumers
            2. Design for failure — assume any component can fail at any time
            3. Make operations idempotent to handle retries safely
            4. Use circuit breakers to prevent cascade failures
            """,
                """
            **Trade-offs to Consider**
            No architecture is perfect. Here are the main trade-offs:

            - Consistency vs Availability: in a distributed system you often have to choose
            - Latency vs Throughput: optimising for one often hurts the other
            - Cost vs Performance: more replicas = more availability but more cost
            - Simplicity vs Flexibility: abstractions add overhead but enable evolution
            """,
        };
        String[] conclusions = {
                "\n**Summary**\nUnderstanding %s deeply will help you make better architectural decisions. Always consider your specific requirements before applying any pattern.",
                "\n**Further Reading**\nTo go deeper on %s, study how Google, Amazon, and Meta have implemented similar systems at scale. Their engineering blogs are invaluable resources.",
                "\n**Key Takeaway**\n%s is a powerful tool in the distributed systems toolbox, but like all tools, it comes with trade-offs. Use it when the benefits clearly outweigh the added complexity.",
        };

        String intro = String.format(intros[random.nextInt(intros.length)], topic);
        String middle = middles[random.nextInt(middles.length)];
        String conclusion = String.format(conclusions[random.nextInt(conclusions.length)], topic);

        return intro + "\n\n" + middle + conclusion;
    }

    private String pickRandomTags(List<Tag> allTags, int count, Random random) {
        List<Tag> shuffled = new ArrayList<>(allTags);
        Collections.shuffle(shuffled, random);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(count, shuffled.size()); i++) {
            if (i > 0) sb.append(",");
            sb.append(shuffled.get(i).getName());
        }
        return sb.toString();
    }
}