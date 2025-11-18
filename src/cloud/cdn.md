# CDN (Content Delivery Network)

A network of distributed servers that cache and deliver content to users from the nearest location.

How it works:
1. User requests content (website, image, video)
2. CDN serves it from the nearest edge server
3. If not cached, CDN fetches from origin server and caches it

Benefits:
- Lower latency (faster load times)
- Reduced bandwidth costs
- Better availability (if origin fails, cached content still serves)
- DDoS protection (traffic distributed across many servers)

What gets cached:
- Static content: images, CSS, JavaScript, videos
- Dynamic content: can be cached with shorter TTL

# Cloudflare

A company providing CDN services plus additional features:

Core Services:
- CDN: 300+ data centers globally
- DNS: Fast DNS resolution (1.1.1.1)
- DDoS Protection: Automatic mitigation
- SSL/TLS: Free certificates
- Web Application Firewall (WAF): Security rules
- Workers: Serverless compute at edge
- R2: Object storage (S3 alternative)

How it works:
1. Point your domain DNS to Cloudflare
2. Traffic routes through Cloudflare network
3. Cloudflare caches content and applies security rules
4. Clean traffic forwarded to your origin server

Pricing:
- Free tier: Basic CDN, DDoS protection, SSL
- Pro/Business/Enterprise: Advanced features

Competitors:
- AWS CloudFront
- Akamai
- Fastly
- Cloudinary (media-focused)
