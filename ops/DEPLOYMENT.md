# Wdrożenie Currency Monitor

Ogólna instrukcja dla macOS oraz Windows. Wszystkie adresy i nazwy kont poniżej
są przykładami: zastąp `203.0.113.10` adresem swojego VPS-a, `deploy-user` użytkownikiem
SSH, `currency.example.com` domeną aplikacji, a `example-owner` właścicielem repozytorium.
Nie zapisuj prywatnych kluczy, haseł ani własnych danych serwera w tej instrukcji.

## Komputer lokalny: macOS i Windows

Na macOS używaj Terminala. Na Windows zainstaluj Git i używaj **Git Bash** — bloki
`bash` w tej instrukcji wykonujesz właśnie w nim. PowerShell ma inną składnię.
Do lokalnego uruchomienia aplikacji potrzebujesz Docker Desktop (na Windows z WSL 2).
Git służy do pobierania i wysyłania kodu; Docker uruchamia lokalnie bazę i aplikacje.

```bash
git clone https://github.com/example-owner/currency-monitor.git
cd currency-monitor
cp .env.example .env
docker compose up --build -d
```

Otwórz `http://localhost:3000`. Lokalny `.env` jest ignorowany przez Git. Dla pracy
bez kontenerów użyj Java 17 i Node.js 22 oraz kroków deweloperskich z README.
Lokalne uruchomienie nie wymaga dostępu do produkcyjnego VPS-a ani sekretów GitHuba.

## Drugi komputer: np. przejście z Maca na PC

1. Wypushuj kod z pierwszego komputera, a na drugim sklonuj repozytorium jak powyżej.
2. Utwórz lokalny `.env` z `.env.example`. Git nie przenosi lokalnych konfiguracji,
   prywatnych instrukcji ani danych lokalnej bazy.
3. Dla samego programowania i pushowania zmian nie potrzebujesz klucza wdrożeniowego.
   Skonfiguruj logowanie do GitHuba dla Git (HTTPS lub osobny klucz do GitHuba).
4. Sekrety Actions i zmienna `PUBLIC_URL` są ustawione w GitHubie i obowiązują niezależnie
   od komputera, z którego wykonujesz push. Nie konfiguruj ponownie VPS-a ani workflow.
5. Jeśli chcesz także ręcznie logować się do VPS-a z drugiego komputera, utwórz na nim
   osobny klucz, np. `ssh-keygen -t ed25519 -f ~/.ssh/currency-monitor-pc`.
   Dopisz jego plik `.pub` do `~/.ssh/authorized_keys` na VPS-ie przez istniejącą
   zaufaną sesję. Dotychczasowy klucz GitHub Actions pozostaje bez zmian.

Prywatne notatki możesz zapisywać jako `ops/DEPLOYMENT.local.md`; pliki
`ops/*.local.md` oraz `ops/nginx/*.local.conf` są ignorowane przez Git. Przenoś je
między własnymi komputerami osobnym, prywatnym kanałem, jeżeli są potrzebne.


## Jak działa proces

Pull request uruchamia testy skryptu wdrożenia i walidację Compose, testy i kontrolę
pokrycia backendu oraz lint i build frontendu.
Push do `main` lub ręczne uruchomienie workflow na `main` dodatkowo publikuje dwa obrazy
w GHCR z tagiem SHA commita. Zadanie `smoke` uruchamia te obrazy z nową bazą na runnerze
GitHuba, sprawdza migrację i API, a potem usuwa testowe kontenery i dane. Dopiero po
sukcesie workflow wysyła konfigurację wydania przez SSH, pobiera obrazy i uruchamia
je przez Compose na VPS-ie.

Compose czeka na zdrową bazę, API i frontend. Skrypt sprawdza także stronę i API na
`127.0.0.1:3000`. Po sukcesie zapisuje symlink `current`. Jeśli uruchomienie aplikacji
nie przejdzie, próbuje przywrócić poprzednie wydanie z lokalnie zachowanych obrazów.
Na pierwszym wdrożeniu nie ma poprzedniej wersji. Workflow sprawdza następnie publiczny
adres HTTPS; błąd DNS, Nginx lub certyfikatu oznacza nieudany workflow, ale nie cofa
zdrowych kontenerów.

Nginx na VPS-ie obsługuje domenę i HTTPS. Frontend jest dostępny wyłącznie na
`127.0.0.1:3000`; backend i PostgreSQL nie publikują portów. Każda witryna na serwerze powinna mieć własną konfigurację Nginx.

## 1. Przygotuj VPS

Polecenia w tym rozdziale wykonuj jako użytkownik SSH na VPS-ie z Linuxem.
Wymagany jest działający Docker Engine i Docker Compose. Jeśli Docker jest instalowany
przez Snap, sprawdź dodatkowo połączenie interfejsu `home`. Pliki aplikacji umieszczamy w katalogu domowym, zgodnie z ograniczeniami
dostępu Snap. Workflow wymaga działania `sudo` bez hasła; sprawdź to:

```bash
sudo -n docker ps
command -v curl
command -v flock
```

Dla instalacji Snap sprawdź `sudo snap connections docker`. Jeśli `home` nie jest połączony, wykonaj `sudo snap connect docker:home`.
Jeśli brakuje `curl` lub `flock`, zainstaluj `sudo apt install curl util-linux`.
Jeżeli `sudo -n` wymaga hasła, trzeba skonfigurować uprawnienia do wdrożeń przed
uruchomieniem workflow. Dostęp do Dockera daje szerokie uprawnienia na serwerze;
docelowo warto zastąpić konto administracyjne osobnym kontem wdrożeniowym.

Utwórz plik środowiska z losowym hasłem bazy. Te polecenia wymagają `openssl`
i odmawiają nadpisania istniejącego pliku:

```bash
mkdir -p ~/currency-monitor/releases
chmod 700 ~/currency-monitor
(
  umask 077
  set -eC
  db_password=$(openssl rand -hex 32)
  printf 'POSTGRES_DB=currency_monitor\nPOSTGRES_USER=currency_monitor\nPOSTGRES_PASSWORD=%s\n' \
    "$db_password" > ~/currency-monitor/.env
)
```

Plik `.env` pozostaje tylko na VPS-ie. Hasło zapisane przy inicjalizacji PostgreSQL
nie zmienia się od samej edycji `.env`; późniejsza rotacja wymaga zmiany hasła również
w bazie.

## 2. Skonfiguruj Nginx i HTTPS

Na swoim komputerze, z katalogu repozytorium:

```bash
scp \
  ops/nginx/currency-monitor.conf.example \
  deploy-user@203.0.113.10:~/currency-monitor/currency-monitor.conf
```

Następnie na VPS-ie edytuj `~/currency-monitor/currency-monitor.conf`: ustaw
`server_name` na swoją domenę zamiast `currency.example.com`. Potem:

```bash
sudo install -m 644 ~/currency-monitor/currency-monitor.conf \
  /etc/nginx/sites-available/currency.example.com
sudo ln -s /etc/nginx/sites-available/currency.example.com \
  /etc/nginx/sites-enabled/currency.example.com
sudo nginx -t && sudo systemctl reload nginx
```

Dodaj rekord DNS `A currency → adres Twojego VPS-a`.
Sprawdź `getent ahostsv4 currency.example.com`. Dla tej subdomeny rekord `AAAA`
powinien istnieć tylko wtedy, gdy IPv6 rzeczywiście prowadzi do tego VPS-a.

Do HTTPS potrzebujesz Certbota z pluginem Nginx. Jeśli jest już zainstalowany, sprawdź:

```bash
sudo certbot --version
sudo certbot plugins
```

Jeżeli dostępny jest plugin `nginx`, uzyskaj certyfikat dla nowej witryny:

```bash
sudo certbot --nginx -d currency.example.com
sudo certbot renew --dry-run
```

Do pierwszego wdrożenia aplikacji Nginx może zwracać 502; Certbot obsługuje wyzwanie
HTTP osobno. Nie instaluj drugiej wersji Certbota, jeśli polecenie już działa.

## 3. Klucz wdrożeniowy, sekrety i zmienna GitHub

Jeśli klucz wdrożeniowy jest już dodany i działa, zachowaj go. Dla nowego
wdrożenia na swoim komputerze utwórz osobny klucz do Actions:

```bash
ssh-keygen -t ed25519 -C "github-actions-deploy" -f ~/.ssh/currency-monitor-deploy
cat ~/.ssh/currency-monitor-deploy.pub | ssh deploy-user@203.0.113.10 \
  'umask 077; mkdir -p ~/.ssh; cat >> ~/.ssh/authorized_keys; chmod 700 ~/.ssh; chmod 600 ~/.ssh/authorized_keys'
ssh -i ~/.ssh/currency-monitor-deploy -o IdentitiesOnly=yes \
  -o PasswordAuthentication=no deploy-user@203.0.113.10 'whoami'
```

Zostaw pustą frazę hasłową dla tego klucza automatyzacji. Polecenia z przykładowym
IP i użytkownikiem wykonaj dopiero po zastąpieniu ich własnymi wartościami.

W repozytorium `example-owner/currency-monitor` otwórz **Settings → Environments**
i utwórz środowisko `production`. Jeśli ustawienia planu pozwalają, ogranicz jego
wdrożenia do `main`. Nie ustawiaj wymaganej ręcznej akceptacji, jeśli każde poprawne
wdrożenie ma przebiegać automatycznie.

Sekrety możesz dodać do środowiska `production` albo jako sekrety repozytorium
w **Settings → Secrets and variables → Actions → New repository secret**:

| Nazwa | Wartość |
| --- | --- |
| `VPS_HOST` | Adres Twojego VPS-a |
| `VPS_USER` | Twój użytkownik SSH |
| `VPS_SSH_KEY` | Cała zawartość prywatnego `~/.ssh/currency-monitor-deploy` |
| `VPS_KNOWN_HOSTS` | Zweryfikowany wpis klucza hosta SSH serwera |

Dodaj też **zmienną** `PUBLIC_URL` w **Settings → Secrets and variables → Actions
→ Variables → New repository variable**. Wartość to pełny adres swojej strony,
np. `https://currency.example.com`. Workflow używa jej do sprawdzenia HTTPS.
Adres nie jest zapisany w plikach repozytorium; zmienna Actions nie jest sekretem.
Jeśli chcesz także ukrywać adres w logach Actions, użyj sekretu `PUBLIC_URL` i zmień
w workflow `vars.PUBLIC_URL` na `secrets.PUBLIC_URL`.

Klucz prywatny wklej bezpośrednio do pola sekretu GitHuba, razem z liniami BEGIN/END
i wszystkimi nowymi liniami. Nie zapisuj go w repozytorium ani czacie.

Skopiowanie klucza do schowka:

- macOS: `pbcopy < ~/.ssh/currency-monitor-deploy`
- Windows / Git Bash: `clip.exe < ~/.ssh/currency-monitor-deploy`
- Windows / PowerShell: `Get-Content -Raw "$env:USERPROFILE\.ssh\currency-monitor-deploy" | Set-Clipboard`


Aby uzyskać wpis `VPS_KNOWN_HOSTS`, na swoim komputerze:

```bash
ssh-keyscan -t ed25519 203.0.113.10 > /tmp/currency-monitor-known-hosts
ssh-keygen -lf /tmp/currency-monitor-known-hosts
```

Porównaj fingerprint z wynikiem uzyskanym w zaufanej sesji SSH na VPS-ie lub konsoli
dostawcy VPS:

```bash
sudo ssh-keygen -lf /etc/ssh/ssh_host_ed25519_key.pub
```

Po potwierdzeniu zgodności wklej do sekretu linię z adresem hosta, `ssh-ed25519`
i kluczem. Linie zaczynające się od `#` są komentarzami. Plik możesz skopiować
poleceniem `pbcopy < /tmp/currency-monitor-known-hosts` (macOS) albo
`clip.exe < /tmp/currency-monitor-known-hosts` (Windows / Git Bash). Workflow wymaga zgodnego klucza hosta i nie
akceptuje nowego klucza automatycznie.

`GITHUB_TOKEN` jest tworzony automatycznie przez GitHub. Workflow ma uprawnienia do
publikowania obrazów, a zadanie wdrożenia ma dostęp do ich odczytu. Krótkotrwały token
trafia na VPS przez stdin. Skrypt zapisuje tymczasowy plik dostępu do GHCR jako
użytkownik wdrożeniowy (uprawnienia 600); Docker odczytuje go podczas pobierania
obrazów. Plik jest usuwany po zakończeniu skryptu. Pozwala to uniknąć zapisywania
danych przez Docker Snap uruchomiony jako root w katalogu innego użytkownika.
Nie potrzebujesz dodatkowego PAT.

Jeśli obrazy o tych nazwach istnieją już w GHCR, sprawdź ich **Manage Actions access**
i przyznaj repozytorium dostęp. Pakiety nowo publikowane przez ten workflow są
powiązane z repozytorium.

## 4. Pierwsze uruchomienie

Po przygotowaniu serwera, domeny, certyfikatu i sekretów wypushuj pliki do `main`.
W zakładce **Actions → CI and deployment** obserwuj zadania `backend`, `frontend`,
`images`, `smoke` i `deploy`. Możesz też wybrać **Run workflow** z gałęzi `main`. GitHub uruchamia cały proces
niezależnie od tego, czy push wykonałeś z Maca, PC czy innego komputera.

Obrazy są budowane dla `linux/amd64`. VPS musi mieć taką architekturę (sprawdź
`uname -m`: oczekiwane `x86_64`). Dla VPS-a ARM zmień `platforms` w workflow.
Komputer deweloperski może być Apple Silicon, ponieważ obrazy produkcyjne buduje GitHub.
Namespace GHCR jest wyznaczany automatycznie z właściciela repozytorium i zapisywany
jako `image-namespace` w katalogu wydania na VPS-ie.
Wdrożenia danej gałęzi nie są przerywane przez następny push. Blokada `flock` na VPS-ie
chroni także przed równoczesnym uruchomieniem dwóch skryptów wdrożeniowych.

Po wdrożeniu sprawdź `https://currency.example.com` i endpoint
`https://currency.example.com/api/currencies`. Pusta lista walut jest poprawną
odpowiedzią API do czasu pierwszego udanego importu danych z NBP.

Na VPS-ie możesz sprawdzić stan i logi:

```bash
cd ~/currency-monitor
export IMAGE_TAG=$(basename "$(readlink -f current)")
export IMAGE_NAMESPACE=$(cat current/image-namespace)
sudo --preserve-env=IMAGE_TAG,IMAGE_NAMESPACE docker compose --env-file .env \
  -f current/compose.prod.yml ps
sudo --preserve-env=IMAGE_TAG,IMAGE_NAMESPACE docker compose --env-file .env \
  -f current/compose.prod.yml logs --tail=100 backend
```

## 5. Baza, backup i rollback

Profil `prod` uruchamia Flyway i sprawdza zgodność encji ze schematem (`validate`).
Migracja V1 jest przeznaczona dla nowej, pustej bazy; nie przejmujemy automatycznie
istniejącej bazy lokalnej utworzonej przez Hibernate. Kolejne zmiany zapisuj jako V2,
V3 itd. Nie edytuj migracji już zastosowanych na produkcji.

Migracje muszą być kompatybilne z poprzednią wersją aplikacji. Rollback obrazów nie
cofa migracji ani danych. Unikaj usuwania kolumn w tym samym wydaniu, które przestaje
ich używać. PostgreSQL przechowuje dane w wolumenie `currency-monitor_postgres_data`;
nie wykonuj `docker compose down -v` na produkcji.

Po udanym wdrożeniu utwórz backup:

```bash
bash ~/currency-monitor/current/ops/backup.sh
```

Skrypt zapisuje dump w formacie `pg_dump -Fc` w `~/currency-monitor/backups`.
Nie ma automatycznej retencji: kontroluj miejsce. Backup na tym samym VPS-ie jest
kopią roboczą; przed przechowywaniem istotnych danych skonfiguruj automatyczne
kopiowanie poza VPS i przetestuj odtwarzanie na osobnej bazie.

Przykładowy wpis w `crontab -e` użytkownika wdrożeniowego (zastąp ścieżkę domową;
codziennie o 03:00 w strefie
czasowej serwera):

```cron
0 3 * * * /bin/bash /home/deploy-user/currency-monitor/current/ops/backup.sh >> /home/deploy-user/currency-monitor/backup.log 2>&1
```

Aby ręcznie wrócić do zachowanego wydania, na VPS-ie ustaw `old_sha` na SHA wybranego
wydania (40 znaków, dostępne w `~/currency-monitor/releases`) i wykonaj:

```bash
cd ~/currency-monitor
old_sha=WPISZ_SHA_POPRZEDNIEGO_WYDANIA
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

Nie usuwaj obrazów i konfiguracji poprzedniego wydania potrzebnego do rollbacku.
Compose wymienia kontenery i może powodować krótką przerwę. Ten układ nie zapewnia
wdrożeń bez przerwy ani odporności na awarię pojedynczego VPS-a.

## Dalsze kroki

Po pierwszym wdrożeniu: backup poza VPS-em, zewnętrzne sprawdzanie dostępności,
alerty o awarii i zapełnieniu dysku, osobne konto wdrożeniowe oraz ochrona gałęzi `main`
wymagająca poprawnych sprawdzeń przed merge. Helpdesk wymaga osobnego projektu Compose,
własnej bazy/wolumenów i innego lokalnego portu (np. `127.0.0.1:3001`).

Dokumentacja:
- [GitHub Container Registry](https://docs.github.com/en/packages/working-with-packages/working-with-the-container-registry)
- [Compose w produkcji](https://docs.docker.com/compose/how-tos/production/)
- [Firewall i porty Docker](https://docs.docker.com/engine/network/packet-filtering-firewalls/)
