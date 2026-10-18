# Wdrożenie produkcyjne / Production deployment

[Polski](#polski) | [English — jump to the English version](#english)

## Polski

Produkcja działa na jednym VPS z Linuxem za serwerem Nginx obsługującym HTTPS.
Podane nazwy serwerów są przykładami. Prawdziwe adresy, klucze i osobiste instrukcje
przechowuj poza śledzonymi plikami; lokalne archiwum może być w ignorowanym
katalogu `private-docs/`.

### Proces wdrożenia

1. Pull requesty uruchamiają testy backendu i pokrycie, testy skryptów wdrożenia,
   walidację Compose oraz lint i build frontendu.
2. Push do `main` lub ręczne uruchomienie na `main` buduje obrazy backendu
   i frontendu w GHCR, z tagiem SHA commita, dla `linux/amd64`.
3. Zadanie smoke uruchamia `compose.prod.yml` ze świeżą bazą PostgreSQL,
   sprawdza zdrowie kontenerów, migrację, start aplikacji i API, a następnie usuwa testowy zestaw.
4. Zadanie produkcyjne wysyła pliki wydania przez SSH i uruchamia `ops/deploy.sh`.
   Skrypt pobiera obrazy, czeka na healthchecki i po sukcesie aktualizuje symlink `current`.
5. Końcowe sprawdzenie HTTPS weryfikuje publiczną stronę i API.

Skrypt używa `flock`, aby wdrożenia nie wykonywały się jednocześnie. Gdy start
kontenerów lub lokalne sprawdzenia API zawiodą, próbuje przywrócić wcześniejsze
wydanie z zachowanych obrazów. Błąd końcowego sprawdzenia publicznego HTTPS
nie cofa automatycznie kontenerów, które przeszły lokalne sprawdzenia.

### Wymagania

- VPS x86-64 z Linuxem, Docker Engine, Compose, curl i flock.
- Konto SSH mogące wykonywać polecenia Docker skryptu przez sudo bez hasła.
  Dostęp do Dockera daje szerokie uprawnienia; użyj osobnego konta wdrożeniowego.
- Dla Dockera ze Snap połącz interfejs `home`, ponieważ wydania i dane logowania
  do rejestru są zapisywane w katalogu domowym użytkownika.
- DNS wskazujący VPS i poprawny certyfikat HTTPS w Nginx na hoście.

Użyj [szablonu Nginx](nginx/currency-monitor.conf.example), zastąp domenę własną
i skonfiguruj HTTPS oraz odnawianie certyfikatu. `compose.prod.yml` publikuje
tylko frontend na `127.0.0.1:3000`; PostgreSQL i backend nie publikują portów hosta.

### Środowisko serwera

Jako użytkownik wdrożeniowy na VPS zainicjalizuj nową instalację:

```bash
mkdir -p ~/apps/currency-monitor/releases
chmod 700 ~/apps/currency-monitor
(
  umask 077
  set -eC
  db_password=$(openssl rand -hex 32)
  printf 'POSTGRES_DB=currency_monitor\nPOSTGRES_USER=currency_monitor\nPOSTGRES_PASSWORD=%s\n' \
    "$db_password" > ~/apps/currency-monitor/.env
)
```

Polecenie odmawia nadpisania istniejącego pliku środowiska. Wzór konfiguracji
znajduje się w [production.env.example](production.env.example). Prawdziwy plik
zachowaj na serwerze. Późniejsza edycja pliku nie zmienia hasła zainicjalizowanej
bazy PostgreSQL; hasło trzeba zmienić również w bazie.

### Konfiguracja GitHub

Utwórz środowisko `production` i ogranicz wdrożenia do `main`, jeśli plan to
umożliwia. Dodaj sekrety Actions w środowisku lub repozytorium:

| Sekret | Przeznaczenie |
| --- | --- |
| `VPS_HOST` | Nazwa hosta lub IP serwera |
| `VPS_USER` | Użytkownik wdrożeniowy |
| `VPS_SSH_KEY` | Prywatny klucz wdrożeniowy, którego klucz publiczny jest autoryzowany na VPS |
| `VPS_KNOWN_HOSTS` | Zweryfikowany wpis klucza hosta SSH |

Przed zapisaniem klucza hosta porównaj fingerprint z konsolą serwera lub zaufaną
sesją. Workflow używa `StrictHostKeyChecking=yes`; nie zastępuj go automatyczną
akceptacją. Klucz automatyzacji musi działać bez interaktywnego pytania o passphrase.

Dodaj zmienną Actions `PUBLIC_URL`, np. `https://currency.example.com`.
Nie jest sekretem. Workflow korzysta z krótkotrwałego `GITHUB_TOKEN` dla GHCR
i przesyła go na VPS przez stdin. Dane logowania do rejestru są zapisywane
tymczasowo z ograniczonym dostępem i usuwane po zakończeniu skryptu.
Istniejące pakiety GHCR muszą przyznawać repozytorium dostęp Actions.

Po konfiguracji wykonaj push do `main` lub uruchom workflow na tej gałęzi.
Pusta lista walut może być poprawna przed pierwszym udanym importem NBP.

### Baza i utrzymanie

Produkcja włącza Flyway i walidację schematu Hibernate; lokalnie Hibernate
aktualizuje schemat. Migracja V1 oczekuje nowej bazy. Nie edytuj zastosowanych
migracji ani nie przejmuj automatycznie bazy utworzonej przez Hibernate.
Przyszłe migracje muszą być zgodne z poprzednią wersją aplikacji, jeśli rollback
obrazów ma działać. Rollback obrazów nie cofa zmian bazy.

Po wdrożeniu utwórz backup:

```bash
bash ~/apps/currency-monitor/current/ops/backup.sh
```

Skrypt zapisuje dump PostgreSQL w `~/apps/currency-monitor/backups`. Retencja
i kopiowanie poza serwer nie są automatyczne. Zaplanuj backupy, monitoruj dysk
i osobno przetestuj odtwarzanie. Na produkcji nie wykonuj `docker compose down -v`.

Logi na serwerze:

```bash
cd ~/apps/currency-monitor
export IMAGE_TAG=$(basename "$(readlink -f current)")
export IMAGE_NAMESPACE=$(cat current/image-namespace)
sudo --preserve-env=IMAGE_TAG,IMAGE_NAMESPACE docker compose --env-file .env \
  -f current/compose.prod.yml logs --tail=100 backend
```

### Ręczny rollback

Aby przywrócić zachowane wydanie na VPS, ustaw `old_sha` na 40-znakowy SHA commita
z `~/apps/currency-monitor/releases` i wykonaj:

```bash
cd ~/apps/currency-monitor
old_sha=REPLACE_WITH_PREVIOUS_RELEASE_SHA
(
  flock -w 600 9
  export IMAGE_TAG="$old_sha"
  export IMAGE_NAMESPACE=$(cat "releases/$old_sha/image-namespace")
  sudo --preserve-env=IMAGE_TAG,IMAGE_NAMESPACE docker compose --env-file .env \
    -f "releases/$old_sha/compose.prod.yml" \
    up -d --pull never --wait --wait-timeout 240 && \
    ln -sfn "$PWD/releases/$old_sha" current
) 9>deploy.lock
```

Zachowaj obrazy i konfigurację poprzedniego wydania potrzebne do rollbacku.
Compose wymienia kontenery i może powodować krótką przerwę. Jeden VPS nie zapewnia
wdrożeń bez przerwy ani redundancji.

[Przejdź do wersji angielskiej ↓](#english)

---

## English

[Wróć do wersji polskiej ↑](#polski)

The production stack runs on one Linux VPS behind an HTTPS Nginx proxy. All server
names below are examples. Store actual host details, keys and personal setup notes
outside tracked files; local archives may be kept in the ignored `private-docs/`
directory.

### Pipeline

1. Pull requests run backend tests/coverage, deployment-script tests, Compose
   validation and frontend lint/build.
2. A push to `main` or a manual run on `main` builds backend/frontend images in
   GHCR, tagged with the commit SHA, for `linux/amd64`.
3. The smoke job starts `compose.prod.yml` with a fresh PostgreSQL database,
   verifies container health, migration/startup and API availability, then removes
   that test stack.
4. The production job uploads release files over SSH and runs `ops/deploy.sh`.
   It pulls images, waits for health checks and updates the `current` symlink on success.
5. A final HTTPS check verifies the public site and API.

The server-side script uses `flock` to serialize deployments. If container startup
or local API checks fail, it attempts to restore a previous release using locally
retained images. A failure of the final public HTTPS check does not automatically
roll back containers that passed local checks.

### Prerequisites

- An x86-64 Linux VPS with Docker Engine, Compose, curl and flock.
- An SSH deployment account able to run the script's Docker commands through
  passwordless sudo. Docker access grants broad privileges; use a dedicated account.
- For Docker installed by Snap, the `home` interface must be connected because
  releases and registry credentials are stored under the deployment user's home.
- DNS pointing to the VPS and a valid HTTPS certificate on the host Nginx proxy.

Use [the Nginx template](nginx/currency-monitor.conf.example), replace its domain
with your own, and configure HTTPS and certificate renewal on the server.
`compose.prod.yml` publishes only the frontend on `127.0.0.1:3000`; PostgreSQL
and the backend do not publish host ports.

### Server environment

As the deployment user on the VPS, initialize a new installation:

```bash
mkdir -p ~/apps/currency-monitor/releases
chmod 700 ~/apps/currency-monitor
(
  umask 077
  set -eC
  db_password=$(openssl rand -hex 32)
  printf 'POSTGRES_DB=currency_monitor\nPOSTGRES_USER=currency_monitor\nPOSTGRES_PASSWORD=%s\n' \
    "$db_password" > ~/apps/currency-monitor/.env
)
```

The command refuses to overwrite an existing environment file. Use
[production.env.example](production.env.example) as the configuration reference.
Keep the real file on the server. Editing it later does not rotate an initialized
PostgreSQL password; update the database password too.

### GitHub configuration

Create the `production` environment and restrict deployment to `main` where
supported. Add these Actions secrets to that environment or the repository:

| Secret | Purpose |
| --- | --- |
| `VPS_HOST` | Server hostname or IP |
| `VPS_USER` | Deployment username |
| `VPS_SSH_KEY` | Private deployment key whose public key is authorized on the VPS |
| `VPS_KNOWN_HOSTS` | Verified SSH host-key entry |

Compare the host-key fingerprint with the server console or an existing trusted
session before saving it. The workflow uses `StrictHostKeyChecking=yes`; do not
replace it with automatic acceptance. A non-interactive automation key must be
usable without a passphrase prompt.

Add the Actions variable `PUBLIC_URL`, for example `https://currency.example.com`.
It is not a secret. The workflow uses a short-lived `GITHUB_TOKEN` for GHCR and
passes it to the VPS through stdin. Registry credentials are stored temporarily
with restrictive permissions and deleted when the deployment script exits.
Existing GHCR packages must grant this repository Actions access.

After configuration, push to `main` or run the workflow there. An empty currency
list can be valid before the first successful NBP import.

### Database and operations

Production enables Flyway and Hibernate schema validation; local development
uses Hibernate schema updates. Migration V1 expects a new database. Do not edit
applied migrations or automatically reuse an existing Hibernate-created database.
Future migrations must remain compatible with the previous application version
if image rollback is expected to work. Image rollback does not undo database changes.

After deployment, create a backup:

```bash
bash ~/apps/currency-monitor/current/ops/backup.sh
```

The script writes a PostgreSQL dump to `~/apps/currency-monitor/backups`. Retention and
off-server replication are not automated. Schedule backups, monitor disk space
and test restores separately. Do not run `docker compose down -v` in production.

For logs on the server:

```bash
cd ~/apps/currency-monitor
export IMAGE_TAG=$(basename "$(readlink -f current)")
export IMAGE_NAMESPACE=$(cat current/image-namespace)
sudo --preserve-env=IMAGE_TAG,IMAGE_NAMESPACE docker compose --env-file .env \
  -f current/compose.prod.yml logs --tail=100 backend
```

### Manual rollback

To restore a retained release on the VPS, set `old_sha` to its 40-character commit
SHA from `~/apps/currency-monitor/releases`, then run:

```bash
cd ~/apps/currency-monitor
old_sha=REPLACE_WITH_PREVIOUS_RELEASE_SHA
(
  flock -w 600 9
  export IMAGE_TAG="$old_sha"
  export IMAGE_NAMESPACE=$(cat "releases/$old_sha/image-namespace")
  sudo --preserve-env=IMAGE_TAG,IMAGE_NAMESPACE docker compose --env-file .env \
    -f "releases/$old_sha/compose.prod.yml" \
    up -d --pull never --wait --wait-timeout 240 && \
    ln -sfn "$PWD/releases/$old_sha" current
) 9>deploy.lock
```

Retain the previous release images and configuration for rollback. Compose
replaces containers and can briefly interrupt service. This single-VPS setup
does not provide zero-downtime deployments or redundancy.

[Back to the top ↑](#polski)

## Układ katalogów aplikacji na VPS

Produkcja używa `~/apps/currency-monitor/`. Folder `config/` zawiera plik `.env`
i odnośniki do aktywnych konfiguracji Nginx i Compose; `.env` w korzeniu
aplikacji jest odnośnikiem do `config/.env`. Wersje są w `releases/`, a `current`
wskazuje bieżące wydanie. Foldery `backups/`, `images/` i `scripts/` grupują
kopie i polecenia administracyjne. Obrazy z GHCR oraz wolumen PostgreSQL
pozostają w magazynie Dockera. Lista obrazów: `scripts/list-images.sh`.
