#!/usr/bin/env bash
set -Eeuo pipefail
umask 077

release_dir="$(cd "$(dirname "$0")/.." && pwd -P)"
app_root="$(cd "$release_dir/../.." && pwd -P)"
export IMAGE_NAMESPACE="$(cat "$release_dir/image-namespace")"
export IMAGE_TAG="$(basename "$release_dir")"
mkdir -p "$app_root/backups"
backup_file="$app_root/backups/currency-monitor-$(date -u +%Y%m%dT%H%M%SZ).dump"
temporary_file="$(mktemp "$app_root/backups/backup.XXXXXX")"
trap 'rm -f -- "$temporary_file"' EXIT
sudo -n --preserve-env=IMAGE_TAG,IMAGE_NAMESPACE docker compose \
    --project-name currency-monitor --env-file "$app_root/.env" \
    -f "$release_dir/compose.prod.yml" exec -T postgres \
    sh -c 'exec pg_dump -U "$POSTGRES_USER" -d "$POSTGRES_DB" -Fc' > "$temporary_file"
test -s "$temporary_file"
mv "$temporary_file" "$backup_file"
echo "$backup_file"
