# Praca lokalna / Local development

[Polski](#polski) | [English — jump to the English version](#english)

## Polski

Pełny start z Dockerem opisuje [README](../README.md#szybki-start). Do uruchomienia
Java i Vite poza kontenerami potrzebujesz Java 17, Node.js 22.12+ oraz Docker Compose.
Przykłady Bash działają w Git Bash na Windows; PowerShell ma inną składnię środowiska.

### Baza, Java i Vite

Utwórz główny `.env` z `.env.example`, jeśli jeszcze nie istnieje, i uruchom
deweloperską bazę danych:

```bash
docker compose -f docker-compose.db.dev.yml up -d --wait
```

Baza jest dostępna na localhost, domyślnie na porcie 5433, i korzysta z osobnego
wolumenu `postgres_dev_data`. Zatrzymaj pełny zestaw Compose przed przejściem
na ten wariant.

Z katalogu głównego repozytorium wyeksportuj konfigurację, zbuduj i uruchom Java:

```bash
set -a
. ./.env
set +a
cd currency-monitor-backend
bash ./mvnw clean package
java -jar currency-monitor-app/target/currency-monitor-app-0.0.1-SNAPSHOT.jar
```

Compose automatycznie odczytuje `.env`; proces Java tego nie robi. W PowerShell
ustaw samodzielnie `$env:POSTGRES_DB`, `$env:POSTGRES_USER`, `$env:POSTGRES_PASSWORD`
i ewentualne porty, a w katalogu backendu użyj `.\mvnw.cmd clean package`.
Zmienne `SPRING_DATASOURCE_*` mogą nadpisać ustawienia połączenia.

W drugim terminalu:

```bash
cd currency-monitor-frontend
npm ci
npm run dev
```

Vite działa pod http://localhost:5173 i przekazuje `/api` na port `BACKEND_PORT`
z głównego pliku środowiska. Po zmianie pliku zrestartuj Vite. `FRONTEND_PORT`
dotyczy tylko frontendu w Dockerze.

### Sprawdzenia

W katalogu backendu uruchom `bash ./mvnw -B verify` lub `.\mvnw.cmd -B verify`.
W katalogu frontendu uruchom `npm run lint`, `npm run build` i `npm audit`.
Playwright jest skonfigurowany, ale testy przeglądarkowe nie są zapisane w repo;
`npm test` nie jest obecnie udokumentowanym, przechodzącym sprawdzeniem.

Z katalogu głównego zatrzymaj bazę poleceniem
`docker compose -f docker-compose.db.dev.yml down`. Dodanie `-v` usuwa dane
tego wariantu. Edycja danych logowania w `.env` nie zmienia hasła już zainicjalizowanej bazy.

[Przejdź do wersji angielskiej ↓](#english)

---

## English

[Wróć do wersji polskiej ↑](#polski)

The full Docker quick start is in [README](../README.md#quick-start). For Java and
Vite outside containers, use Java 17, Node.js 22.12+ and Docker Compose. Bash
examples work in Git Bash on Windows; PowerShell uses different environment syntax.

### Database, Java and Vite

Create the root `.env` from `.env.example` if it does not already exist, then start
the development database:

```bash
docker compose -f docker-compose.db.dev.yml up -d --wait
```

The database binds to localhost port 5433 by default and uses a separate
`postgres_dev_data` volume. Stop the full Compose stack before switching to this
variant.

From the repository root, export your local environment and build/run Java:

```bash
set -a
. ./.env
set +a
cd currency-monitor-backend
bash ./mvnw clean package
java -jar currency-monitor-app/target/currency-monitor-app-0.0.1-SNAPSHOT.jar
```

Compose reads `.env` automatically; a Java process does not. In PowerShell, set
`$env:POSTGRES_DB`, `$env:POSTGRES_USER`, `$env:POSTGRES_PASSWORD` and any port
overrides yourself and use `.\mvnw.cmd clean package` in the backend directory.
Standard `SPRING_DATASOURCE_*` environment variables can override the connection.

In a second terminal:

```bash
cd currency-monitor-frontend
npm ci
npm run dev
```

Vite runs at http://localhost:5173 and proxies `/api` using `BACKEND_PORT` from the
root environment file. Restart Vite after changing that file. `FRONTEND_PORT`
controls the Docker frontend only.

### Checks

Run `bash ./mvnw -B verify` (or `.\mvnw.cmd -B verify`) from the backend directory.
Run `npm run lint`, `npm run build` and `npm audit` from the frontend directory.
Playwright tooling is configured, but browser tests are not committed; `npm test`
is not currently a documented passing check.

Stop the database with `docker compose -f docker-compose.db.dev.yml down` from
the repository root. Adding `-v` deletes that variant's data. Editing credentials
in `.env` does not change the password of an already initialized database.

[Back to the top ↑](#polski)
