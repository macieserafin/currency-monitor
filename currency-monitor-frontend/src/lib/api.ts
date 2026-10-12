import type { Currency, Rate } from "./market";

export class ApiError extends Error {
  status: number;
  constructor(message: string, status: number) {
    super(message);
    this.status = status;
  }
}
export async function request(
  path: string,
  signal?: AbortSignal,
): Promise<unknown> {
  const timeout = AbortSignal.timeout(15_000);
  let response: Response;
  try {
    response = await fetch(path, {
      signal: signal ? AbortSignal.any([signal, timeout]) : timeout,
      headers: { Accept: "application/json" },
    });
  } catch {
    if (signal?.aborted) throw new DOMException("Przerwano", "AbortError");
    throw new ApiError(
      "Nie udało się połączyć z API. Sprawdź, czy backend jest uruchomiony, i spróbuj ponownie.",
      0,
    );
  }
  if (!response.ok) {
    throw new ApiError(
      response.status === 404
        ? "Brak notowań tej waluty w bazie."
        : `API jest chwilowo niedostępne (HTTP ${response.status}). Spróbuj ponownie.`,
      response.status,
    );
  }
  try {
    return await response.json();
  } catch {
    throw new ApiError(
      "API zwróciło nieprawidłową odpowiedź.",
      response.status,
    );
  }
}
const isRecord = (value: unknown): value is Record<string, unknown> =>
  typeof value === "object" && value !== null;
function isRate(value: unknown): value is Rate {
  return (
    isRecord(value) &&
    typeof value.date === "string" &&
    /^\d{4}-\d{2}-\d{2}$/.test(value.date) &&
    Number.isFinite(Date.parse(value.date)) &&
    typeof value.rate === "number" &&
    Number.isFinite(value.rate) &&
    value.rate > 0
  );
}
export async function getCurrencies(signal?: AbortSignal): Promise<Currency[]> {
  const value = await request("/api/currencies", signal);
  if (
    !Array.isArray(value) ||
    !value.every(
      (row) =>
        isRecord(row) &&
        typeof row.code === "string" &&
        /^[A-Z]{3}$/.test(row.code) &&
        typeof row.name === "string",
    )
  )
    throw new ApiError("Nieprawidłowa lista walut w odpowiedzi API.", 0);
  return value as Currency[];
}
export async function getLatest(
  code: string,
  signal?: AbortSignal,
): Promise<Rate> {
  const value = await request(
    `/api/rates/${encodeURIComponent(code)}/latest`,
    signal,
  );
  if (!isRate(value))
    throw new ApiError("Nieprawidłowy kurs w odpowiedzi API.", 0);
  return value;
}
export async function getHistory(
  code: string,
  signal?: AbortSignal,
): Promise<Rate[]> {
  const value = await request(`/api/rates/${encodeURIComponent(code)}`, signal);
  if (!Array.isArray(value) || !value.every(isRate))
    throw new ApiError("Nieprawidłowa historia w odpowiedzi API.", 0);
  return value.sort((a, b) => a.date.localeCompare(b.date));
}
