import { useEffect, useState } from "react";
import { getHistory } from "../lib/api";
import {
  change,
  date,
  downloadHistory,
  filterHistory,
  flags,
  number,
  type Quote,
  type Rate,
} from "../lib/market";
import { Icon } from "./Icon";
import { RateChart } from "./RateChart";

export function HistoryPanel({
  code,
  quotes,
  onSelect,
  revision,
  expanded = false,
}: {
  code: string;
  quotes: Quote[];
  onSelect: (code: string) => void;
  revision: number;
  expanded?: boolean;
}) {
  const [days, setDays] = useState(30);
  const [limit, setLimit] = useState(10);
  const [retry, setRetry] = useState(0);
  const key = `${code}:${revision}:${retry}`;
  const [result, setResult] = useState<{
    key: string;
    rates: Rate[];
    error?: string;
  }>({ key: "", rates: [] });
  useEffect(() => {
    const controller = new AbortController();
    getHistory(code, controller.signal)
      .then((rates) => {
        if (!controller.signal.aborted) setResult({ key, rates });
      })
      .catch((failure) => {
        if (!controller.signal.aborted)
          setResult({
            key,
            rates: [],
            error:
              failure instanceof Error
                ? failure.message
                : "Nie udało się pobrać historii.",
          });
      });
    return () => controller.abort();
  }, [code, key]);
  const loading = result.key !== key;
  const rates = loading ? [] : filterHistory(result.rates, days);
  const delta = change(rates);
  const current = rates[rates.length - 1];
  const quote = quotes.find((row) => row.currency.code === code);
  const descending = [...rates].reverse();
  return (
    <section
      className={`panel history-panel ${expanded ? "expanded" : ""}`}
      aria-label="Historia kursu"
    >
      <div className="panel-heading">
        <div>
          <span className="eyebrow">PERSPEKTYWA MA ZNACZENIE</span>
          <h2>Historia kursu</h2>
        </div>
        <label className="currency-select">
          <span aria-hidden="true">{flags[code] || "◉"}</span>
          <select
            aria-label="Waluta wykresu"
            value={code}
            onChange={(event) => {
              onSelect(event.target.value);
              setLimit(10);
            }}
          >
            {quotes.map((row) => (
              <option key={row.currency.code} value={row.currency.code}>
                {row.currency.code}
              </option>
            ))}
          </select>
        </label>
      </div>
      <div className="chart-heading">
        <div>
          <p className="muted currency-name">
            {quote?.currency.name} <span> / PLN</span>
          </p>
          <div className="chart-value">
            {loading ? "—" : current ? number(current.rate) : "—"}{" "}
            <span>PLN</span>
          </div>
          <span
            className={`change ${delta !== null && delta < 0 ? "negative" : ""}`}
          >
            {delta === null
              ? "Za mało danych do obliczenia zmiany"
              : `${delta > 0 ? "+" : ""}${number(delta, 2)}% w wybranym okresie`}
          </span>
        </div>
        <div className="segments" aria-label="Zakres historii">
          {[
            [7, "7 dni"],
            [30, "30 dni"],
            [90, "90 dni"],
            [0, "Wszystko"],
          ].map(([value, label]) => (
            <button
              key={value}
              aria-pressed={days === value}
              className={days === value ? "active" : ""}
              onClick={() => {
                setDays(Number(value));
                setLimit(10);
              }}
            >
              {label}
            </button>
          ))}
        </div>
      </div>
      {loading ? (
        <div className="chart-loading" role="status">
          <div className="skeleton" />
          <span>Pobieranie historii {code}…</span>
        </div>
      ) : result.error ? (
        <div className="empty chart-empty" role="alert">
          <Icon name="info" />
          <p>{result.error}</p>
          <button
            className="button subtle"
            onClick={() => setRetry((value) => value + 1)}
          >
            Spróbuj ponownie
          </button>
        </div>
      ) : (
        <RateChart
          key={`${code}:${days}:${revision}`}
          rates={rates}
          code={code}
        />
      )}
      <div className="chart-footer">
        <span>
          <i className="legend-dot" /> Kurs średni NBP · 1 {code} w PLN
        </span>
        <button
          className="text-button"
          onClick={() => downloadHistory(code, rates)}
          disabled={!rates.length}
        >
          <Icon name="download" size={15} /> Eksport CSV
        </button>
      </div>
      {expanded && !loading && !result.error && (
        <div className="history-records">
          <div className="section-title">
            <h3>Notowania w okresie</h3>
            <span className="count">{rates.length}</span>
          </div>
          <div className="table-scroll">
            <table>
              <thead>
                <tr>
                  <th>Data publikacji</th>
                  <th>Kurs średni</th>
                  <th>Zmiana do poprzedniego</th>
                </tr>
              </thead>
              <tbody>
                {descending.slice(0, limit).map((row, index) => {
                  const previous = descending[index + 1];
                  const difference = previous
                    ? (row.rate / previous.rate - 1) * 100
                    : null;
                  return (
                    <tr key={row.date}>
                      <td>{date(row.date)}</td>
                      <td className="numeric">{number(row.rate)} PLN</td>
                      <td
                        className={
                          difference !== null && difference < 0
                            ? "negative"
                            : "positive"
                        }
                      >
                        {difference === null
                          ? "—"
                          : `${difference > 0 ? "+" : ""}${number(difference, 2)}%`}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
          {limit < rates.length && (
            <button
              className="button subtle more"
              onClick={() => setLimit((value) => value + 20)}
            >
              Pokaż kolejne notowania ({rates.length - limit})
            </button>
          )}
        </div>
      )}
    </section>
  );
}
