import { Icon } from "./Icon";
import { date, number, symbols, type Quote } from "../lib/market";

interface Props {
  quotes: Quote[];
  currentCode: string;
  setSelected: (code: string) => void;
}
export function QuoteCards({ quotes, currentCode, setSelected }: Props) {
  const featured = ["EUR", "USD", "GBP", "CHF"]
    .map((code) => quotes.find((row) => row.currency.code === code))
    .filter((row) => !!row);
  const cards = [
    ...featured,
    ...quotes.filter((row) => !featured.includes(row)),
  ].slice(0, 4);

  return (
    <div className="quote-cards">
      {cards.map(({ currency, latest }) => (
        <button
          key={currency.code}
          className={`quote-card ${currentCode === currency.code ? "chosen" : ""}`}
          onClick={() => setSelected(currency.code)}
          aria-pressed={currentCode === currency.code}
        >
          <div className="quote-top">
            <span
              className={`currency-symbol symbol-${currency.code.toLowerCase()}`}
            >
              {symbols[currency.code] ||
                currency.code.slice(0, 2)}
            </span>
            <span className="quote-identity">
              <strong>
                {currency.code}
                <span>/ PLN</span>
              </strong>
              <small>{currency.name}</small>
            </span>
            <Icon name="arrow" size={17} />
          </div>
          <div className="quote-number">
            {latest ? number(latest.rate) : "—"}
            <span>PLN</span>
          </div>
          <div className="quote-bottom">
            <span>
              {latest
                ? date(latest.date, true)
                : "Kurs niedostępny"}
            </span>
            <span>
              Kurs średni <span className="tiny-dot" />
            </span>
          </div>
        </button>
      ))}
    </div>
  );
}
