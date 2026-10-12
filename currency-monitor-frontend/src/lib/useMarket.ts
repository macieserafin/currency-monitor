import { useCallback, useEffect, useRef, useState } from "react";
import { getCurrencies, getLatest } from "./api";
import type { Quote } from "./market";

export function useMarket() {
  const [quotes, setQuotes] = useState<Quote[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [updatedAt, setUpdatedAt] = useState<Date>();
  const [revision, setRevision] = useState(0);
  const active = useRef<AbortController | null>(null);
  const refresh = useCallback(async () => {
    active.current?.abort();
    const controller = new AbortController();
    active.current = controller;
    setLoading(true);
    setError("");
    try {
      const currencies = await getCurrencies(controller.signal);
      const result: Quote[] = new Array(currencies.length);
      let next = 0;
      // Bound requests even when the catalogue grows.
      await Promise.all(
        Array.from({ length: Math.min(6, currencies.length) }, async () => {
          while (next < currencies.length && !controller.signal.aborted) {
            const index = next++;
            const currency = currencies[index];
            try {
              result[index] = {
                currency,
                latest: await getLatest(currency.code, controller.signal),
              };
            } catch (failure) {
              result[index] = {
                currency,
                error:
                  failure instanceof Error
                    ? failure.message
                    : "Nie udało się pobrać kursu.",
              };
            }
          }
        }),
      );
      if (!controller.signal.aborted) {
        setQuotes(
          result.sort((a, b) => a.currency.code.localeCompare(b.currency.code)),
        );
        setUpdatedAt(new Date());
        setRevision((value) => value + 1);
      }
    } catch (failure) {
      if (!controller.signal.aborted)
        setError(
          failure instanceof Error ? failure.message : "Błąd połączenia.",
        );
    } finally {
      if (!controller.signal.aborted) setLoading(false);
    }
  }, []);
  useEffect(() => {
    void refresh();
    const interval = window.setInterval(() => {
      if (!document.hidden && !active.current?.signal.aborted) void refresh();
    }, 60_000);
    return () => {
      active.current?.abort();
      window.clearInterval(interval);
    };
  }, [refresh]);
  return { quotes, loading, error, updatedAt, revision, refresh };
}
