# Vote System

## Frontend Layer

### 1. Client Web APP (PWA - Progressive Web Application)

It starts on PWA, that's a progressive web application, this is a web application that runs on a browser, and can behave
like a native app with notifications and everything. It works on all platforms that run a browser.

### 2. Route 53 (AWS DNS Service)

It is the Amazon DNS service, it receives the address and returns the ip address to the browser. It provides health
checks, failover routing, geolocation routing, fast resolution.

### 3. CDN (Content Delivery Network)

It caches things, like html pages, js bundles, css stylesheets, images, fonts.
This is useful for speed (content is served from nearest location), for scalability (can handle millions of concurrent
users)
cost, as it reduces bandwidth on origin server.
It is reliable; if one edge location fails, it routes the request to the next nearest, providing multiple copies.
And security against DDoS, and has SSL/TLS.

### 4. S3 (Simple Storage Service)

Is the origin and source of truth for static files.
The CDN fetches files from here when cache misses occur.

### 5. Auth0 (Third-Party Authentication Service)

Handles all authentication and authorization concerns.
Eliminates need to build custom authentication.

**Auth Flow:**

- a - User clicks Login in PWA
- b - PWA redirects to Auth0 login page
- c - User enters credentials
- d - Auth0 validates credentials against its database
- e - If valid, Auth0 generates access token (jwt), id token (contains user profile), refresh token (for getting new
  access tokens)
- f - Auth0 redirects back to PWA with tokens
- g - PWA exchanges code for tokens
- h - PWA stores tokens securely (memory/session storage)
- i - Future API requests include access token in headers

## Security Layer

### WAF (Web Application Firewall)

It is a layer 7 firewall, sits between internet and API gateway.

It inspects every http/https request using rules like:

1. SQL injection protection
2. XSS protection
3. Rate limiting
4. Geo blocking
5. Known malicious IPs
6. Custom rules

Every blocked request is logged.

### HTTPS Everywhere

It encrypts all communication between client and server.

## API Layer

### 1. API Gateway

Central front door for all backend services.
Routes, validates, transforms, and monitors API requests.
Core functions: JWT validation (critical for security).

**Flow:**

- a - Request arrives with JWT
- b - API gateway extracts token from header
- c - Validates JWT
- d - Extract user context from valid token
- e - Passes user context to backend service in header

**Public Key Caching:**

First request:

```
API Gateway → Auth0: "GET /.well-known/jwks.json"
Auth0 returns public keys
Cache for 24 hours
```

Subsequent requests (within 24 hours):

```
Use cached public key (fast validation)
```

### 2. Request Routing

- Admin only (backoffice routes)
- Public read (results routes)
- Authenticated users (vote routes)

**Load balancing:**

- Traffic is split between instances using ALB

### 3. Authorization (Role-based access control)

Examples:

- Admin creates elections → Allowed!
- Voter tries to create election → Forbidden!

### 4. Request/Response Transformation

**Request Transformation:**

Backend services expect specific format:

```javascript
// API Gateway receives:
{
    "candidate"
:
    "123e4567-e89b-12d3-a456-426614174000"
}

// Transforms and sends to backend:
{
    "candidateId"
:
    "123e4567-e89b-12d3-a456-426614174000",
        "userId"
:
    "auth0|123456",              // Added from JWT
        "timestamp"
:
    "2024-02-16T10:30:00Z",   // Added by gateway
        "sourceIp"
:
    "1.2.3.4",                 // Added by gateway
        "userAgent"
:
    "Mozilla/5.0..."          // Added by gateway
}
```

**Response Transformation:**

Backend service returns internal format:

```javascript
// Backend response:
{
    "vote_id"
:
    "abc-123",
        "status_code"
:
    1,
        "created_ts"
:
    1708095600
}

// API Gateway transforms to client-friendly format:
{
    "voteId"
:
    "abc-123",
        "status"
:
    "ACCEPTED",
        "createdAt"
:
    "2024-02-16T10:30:00Z"
}
```

### 5. Rate Limiting & Throttling

### 6. Caching

Request caching, cache invalidation.

### 7. Monitoring & Logging

### 8. CORS (Cross-Origin Resource Sharing)

### 9. API Versioning

## Summary - How It All Works Together

### Complete Request Flow for Casting a Vote:

1. User opens PWA (loaded from CDN/S3)
   ↓
2. User clicks "Login" → redirected to Auth0
   ↓
3. Auth0 validates credentials → returns JWT
   ↓
4. User browses elections, clicks "Vote for Candidate A"
   ↓
5. PWA sends: `POST /api/elections/123/vote`
    - Authorization: Bearer {JWT}
    - Over HTTPS (encrypted)
      ↓
6. Request hits WAF
    - Checks for SQL injection: ✓ Clean
    - Rate limit: ✓ Within limits
    - IP reputation: ✓ Good
      ↓
7. Request forwarded to API Gateway
    - JWT validation: ✓ Valid, not expired
    - Extract user: auth0|123456, role=VOTER
    - Authorization: ✓ Voter allowed to vote
    - Route matching: → Vote Service
      ↓
8. API Gateway forwards to Vote ALB
    - Added headers: X-User-Id, X-User-Role
      ↓
9. Vote ALB routes to healthy Vote Service instance
   ↓
10. Vote Service:
    - Checks ElastiCache: Has user voted? No
    - Writes vote to RDS
    - Updates ElastiCache
    - Updates candidate vote count
    - Returns: 200 OK, voteId=xyz
      ↓
11. Response flows back:
    - Vote Service → ALB → API Gateway → WAF → CDN → User
      ↓
12. PWA displays: "Vote recorded successfully!"
    ↓
13. API Gateway invalidates cache for election results
    ↓
14. Next user viewing results gets fresh data

### This architecture ensures:

- **Security:** Multiple layers (Auth0, WAF, JWT, HTTPS)
- **Scalability:** CDN, microservices, load balancers, caching
- **Reliability:** Health checks, failover, multiple availability zones
- **Performance:** CDN edge caching, ElastiCache, API Gateway caching
- **Auditability:** Comprehensive logging at every layer