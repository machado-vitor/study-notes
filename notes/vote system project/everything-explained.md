vote system


1. Client Web APP (PWA - Progressive Web Application)
   It starts on PWA, that's a progressive web application, this is a web application that runs on a browser, and can behave like a native app with notifications and everything. It works on all platforms that run a browser.

2. Route 53 (AWS DNS Service)

It is the Amazon DNS service, it receives the address and returns the ip address to the browser. It provides health checks, failover routing, geolocation routing, fast resolution.

3. CDN (Content Delivery Network)

It caches things, like html pages, js bundles, css stylesheets, images, fonts.
This is usefull for speed (content is served from nearest location), for scalability (can hangle millitons of concurrent users)
cost, as it reduces bandwidth on origin server.
It is reliable; if one edge location fails, it routes the request to the next nearest, providing multiple copies.
And security against DDoS, and has SSL/TLS.

4 - S3 (Simple Storage Service.)
is the origin and source of truth for static files
The CDN fetches files from here when cache misses occur.

5 - Auth0 (Third-Party Authentication Service)
Handles all authentication and authorization concerns
Eliminates need to build custom authentication

Flow :
a - User clicks Login in PWA.
b - PWA redirects to Auth0 login page
c - User enters credentials
d - Auth0 validates credentials against its database
e - If valid, Auth0 generates access token (jwt), id token(contains user profile), refresh token (for getting new access tokens).
f - auth0 redirects back to PWA with tokens
g - PWA exchanges code for tokens
h - PWA stores tokens securely (memory/session storage)
i - Future API requests include access token in headers.