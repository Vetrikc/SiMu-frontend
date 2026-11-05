# SiMu client

Server-rendered web client for the SiMu backend (Spring Boot + Thymeleaf + Tailwind CSS).

## Running

The backend has to be up (gateway on :8081). Then:

```bash
./mvnw spring-boot:run
# or
./mvnw clean package && java -jar target/client-1.0.0.jar
```

Open http://localhost:8090.

| Variable | Default | Meaning |
| --- | --- | --- |
| `SERVER_PORT` | 8090 | port of this application |
| `BACKEND_URL` | http://localhost:8081 | backend gateway |
| `BACKEND_CONNECT_TIMEOUT_MS` | 5000 | connect timeout for backend calls |
| `BACKEND_READ_TIMEOUT_MS` | 20000 | read timeout for backend calls |

## What it does

The browser talks only to this application. Tokens are kept in the server-side HTTP session and are
never exposed to JavaScript; every backend call goes through `BackendClient`, which attaches the
access token, renews it with the refresh token when it is about to expire (or when the backend
answers 401) and translates backend error codes into readable messages.

Pages:

| Route | Backend endpoints |
| --- | --- |
| `/signup` | `POST /api/v1/auth/signup` |
| `/signin` | `POST /api/v1/auth/signin`, `GET /api/v1/auth/oauth2/google` |
| `/oauth2/callback` | `POST /api/v1/auth/oauth2/exchange`, `GET /api/v1/auth/me` |
| `/forgot-password` | `POST /api/v1/auth/forgot-password` |
| `/reset-password` | `POST /api/v1/auth/reset-password` |
| `/verify-email` | `POST /api/v1/auth/verify-email` |
| `/logout` | `POST /api/v1/auth/logout` |
| `/home` | `GET /api/v1/music`, `GET /api/v1/users/playlists` |
| `/music/{id}` | `GET /api/v1/music/{id}`, `POST /api/v1/playlists/{id}/tracks/{musicId}` |
| `/upload` | `POST /api/v1/music` (multipart) |
| `/playlists` | `GET/POST /api/v1/users/playlists` |
| `/playlists/{id}` | `GET /api/v1/playlists/{id}`, `POST .../tracks/{musicId}`, `DELETE /api/v1/playlists/{id}` |
| `/profile` | `GET /api/v1/auth/me`, `GET /api/v1/users`, `DELETE /api/v1/users/me` |

`DELETE` requests are sent from HTML forms through the application, which calls the backend with
the proper HTTP method.

## Notes

- Google login needs `GOOGLE_CLIENT_ID/SECRET` on auth-service and
  `OAUTH_FRONTEND_REDIRECT_URI=http://localhost:8090/oauth2/callback` so the callback comes back
  to this client.
- For the links inside verification and reset e-mails, set `APP_PUBLIC_URL=http://localhost:8090`
  on auth-service.
