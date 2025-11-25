# JWT (JSON Web Token)

JWT is a compact, URL-safe token format used for securely transmitting information between parties.

https://www.jwt.io

## Structure

A JWT consists of three parts separated by dots:

```
header.payload.signature
```

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30
```

### URL-Safe Encoding

JWTs use **Base64URL** encoding, which makes them safe to use in URLs, HTTP headers, and HTML forms:

**Standard Base64** uses characters that conflict with URLs:
- `+` (plus sign) - reserved character in URLs (means space)
- `/` (forward slash) - path separator in URLs
- `=` (equals sign) - query string separator

**Base64URL** replaces these:
- `+` becomes `-` (minus/hyphen)
- `/` becomes `_` (underscore)
- `=` padding is removed

This allows JWTs to be safely passed as:
- URL query parameters: `?token=eyJhbGc...`
- HTTP headers: `Authorization: Bearer eyJhbGc...`
- Form data without needing additional encoding

### Header
- Contains token type (JWT) and signing algorithm (e.g., HS256, RS256)
- Base64URL encoded JSON
- https://datatracker.ietf.org/doc/html/rfc7519#section-5

#### Example of JOSE (JSON Object Signing and Encryption)
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

### Payload
- Contains claims (statements about the user/entity and additional data) 
- There are three types of claims: registered, public, and private claims.
- Registered claims: `iss` (issuer), `exp` (expiration), `sub` (subject), `aud` (audience) and others https://datatracker.ietf.org/doc/html/rfc7519#section-4.1
- Public claims: a value that contains a Collision-Resistant Name.
- Private claims: any data you want to include, are subject to collision and should be used with caution.
- Base64URL encoded JSON

#### Example
```json
{
    "sub": "1234567890",
    "name": "John Doe",
    "admin": true
}
```

### Signature
- Created by taking encoded header + encoded payload
- Signing with a secret key (HMAC) or private key (RSA/ECDSA) specified on the header
- Ensures token hasn't been tampered with

#### Example
```
HMACSHA256(
      base64UrlEncode(header) + "." +
      base64UrlEncode(payload),
      secret)
```

## Authentication Flow

1. **User logs in** with credentials
2. **Server validates** credentials
3. **Server creates JWT** with user info in payload, signs it
4. **[aws](../../cloud/aws)Client stores JWT** (usually in localStorage or cookie)
5. **Client sends JWT** in subsequent requests (typically in `Authorization: Bearer <token>` header)
6. **Server verifies signature** and extracts user info from payload
7. **No database lookup needed** - all info is in the token

## Key Characteristics

- **Stateless**: Server doesn't need to store session data
- **Self-contained**: All necessary info is in the token
- **Tamper-proof**: Signature verification detects modifications
- **Not encrypted**: Payload is only encoded (Base64), not encrypted - don't store sensitive data
- **Expiration**: Tokens have limited lifetime via `exp` claim

