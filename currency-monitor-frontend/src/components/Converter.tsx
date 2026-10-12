import { useState } from "react";
import { convert, date, flags, number, type Quote } from "../lib/market";
import { Icon } from "./Icon";

export function Converter({ quotes }: { quotes: Quote[] }) {
  const available = quotes.filter((row) => row.latest);
  const [amount, setAmount] = useState("1000");
  const [from, setFrom] = useState("EUR");
  const [to, setTo] = useState("PLN");
  const options = [
    { code: "PLN", name: "polski złoty" },
    ...quotes.map((row) => row.currency).filter((row) => row.code !== "PLN"),
  ];
  const source = quotes.find((row) => row.currency.code === from)?.latest;
  const target = quotes.find((row) => row.currency.code === to)?.latest;
  const fromRate = from === "PLN" ? 1 : source?.rate;
  const toRate = to === "PLN" ? 1 : target?.rate;
  const result = convert(amount, fromRate, toRate);
  const validAmount = convert(amount, 1, 1) !== null;
  const dates = [
    ...new Set(
      [source?.date, target?.date].filter((value): value is string => !!value),
    ),
  ];
  return (
    <section className="panel converter" aria-label="Przelicznik walut">
      <div className="panel-heading">
        <div>
          <span className="eyebrow">POLICZ W CHWILĘ</span>
          <h2>Przelicznik walut</h2>
        </div>
        <span className="soft-icon">
          <Icon name="swap" />
        </span>
      </div>
      <label className="field-label" htmlFor="amount">
        Kwota
      </label>
      <div
        className={`amount-field ${!validAmount && amount ? "invalid" : ""}`}
      >
        <input
          id="amount"
          inputMode="decimal"
          autoComplete="off"
          value={amount}
          onChange={(event) => setAmount(event.target.value)}
          aria-invalid={!validAmount}
          aria-describedby={!validAmount ? "amount-error" : undefined}
        />
        <span>{from}</span>
      </div>
      {!validAmount && (
        <small id="amount-error" className="negative">
          Wpisz nieujemną kwotę, np. 1000,50.
        </small>
      )}
      <div className="converter-pair">
        <label>
          <span className="field-label">Z waluty</span>
          <span className="select-field">
            <span aria-hidden="true">{flags[from] || "◉"}</span>
            <select
              aria-label="Z waluty"
              value={from}
              onChange={(event) => setFrom(event.target.value)}
            >
              {!options.some((row) => row.code === from) && (
                <option value={from}>{from}</option>
              )}
              {options.map((row) => (
                <option key={row.code} value={row.code}>
                  {row.code}
                </option>
              ))}
            </select>
          </span>
        </label>
        <button
          className="swap-button"
          aria-label="Zamień waluty"
          onClick={() => {
            setFrom(to);
            setTo(from);
          }}
        >
          <Icon name="swap" size={18} />
        </button>
        <label>
          <span className="field-label">Na walutę</span>
          <span className="select-field">
            <span aria-hidden="true">{flags[to] || "◉"}</span>
            <select
              aria-label="Na walutę"
              value={to}
              onChange={(event) => setTo(event.target.value)}
            >
              {!options.some((row) => row.code === to) && (
                <option value={to}>{to}</option>
              )}
              {options.map((row) => (
                <option key={row.code} value={row.code}>
                  {row.code}
                </option>
              ))}
            </select>
          </span>
        </label>
      </div>
      <div className="conversion-result" aria-live="polite">
        <span>Otrzymasz w przeliczeniu</span>
        <div>
          {result === null ? "—" : number(result, 2)} <small>{to}</small>
        </div>
        <p>
          {fromRate && toRate
            ? `1 ${from} = ${number(fromRate / toRate)} ${to}`
            : "Kurs tej waluty jest niedostępny."}
        </p>
      </div>
      <p className="converter-note">
        <Icon name="info" size={15} />
        <span>
          Kursy średnie, bez spreadu i prowizji.
          {dates.length > 0 && (
            <>
              {" "}
              Notowanie: {dates.map((value) => date(value, true)).join(" / ")}.
            </>
          )}
          {dates.length > 1 && " Waluty mają różne daty notowań."}
          {!available.length && " Oczekiwanie na kursy z API."}
        </span>
      </p>
    </section>
  );
}
