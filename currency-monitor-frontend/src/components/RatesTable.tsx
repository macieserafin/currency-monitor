import { useState } from "react";
import { Icon } from "./Icon";
import { date, flags, number, type Quote } from "../lib/market";
import type { Page } from "../lib/navigation";

interface Props {
  quotes: Quote[];
  sort: string;
  setSort: (sort: string) => void;
  page: Page;
  favorites: string[];
  currentCode: string;
  toggleFavorite: (code: string) => void;
  showHistory: (code: string) => void;
  navigate: (page: Page) => void;
}
export function RatesTable({ quotes, page, favorites, currentCode, toggleFavorite, showHistory, navigate, sort, setSort }: Props) {
  const [search, setSearch] = useState("");
  const [limit, setLimit] = useState(10);
  const query = search.toLocaleLowerCase("pl").trim();
  const filtered = quotes
    .filter(
      (row) =>
        (page !== "favorites" || favorites.includes(row.currency.code)) &&
        `${row.currency.code} ${row.currency.name}`
          .toLocaleLowerCase("pl")
          .includes(query),
    )
    .sort((a, b) =>
      sort === "name"
        ? a.currency.name.localeCompare(b.currency.name, "pl")
        : sort === "rate"
          ? (b.latest?.rate ?? -1) - (a.latest?.rate ?? -1)
          : a.currency.code.localeCompare(b.currency.code),
    );

  return (
    <section className="panel rates-panel" aria-label="Lista walut">
      <div className="rates-heading">
        <div className="section-title">
          <h2>
            {page === "favorites"
              ? "Obserwowane waluty"
              : "Wszystkie waluty"}
          </h2>
          <span className="count">{filtered.length}</span>
        </div>
        <div className="table-controls">
          <div className="search-field">
            <Icon name="search" size={17} />
            <input
              aria-label="Szukaj waluty"
              placeholder="Szukaj kodu lub nazwy…"
              value={search}
              onChange={(event) => {
                setSearch(event.target.value);
                setLimit(10);
              }}
            />
            {search && (
              <button
                className="icon-button"
                aria-label="Wyczyść wyszukiwanie"
                onClick={() => setSearch("")}
              >
                <Icon name="close" size={15} />
              </button>
            )}
          </div>
          <select
            className="sort-select"
            aria-label="Sortowanie walut"
            value={sort}
            onChange={(event) => setSort(event.target.value)}
          >
            <option value="code">Kod A–Z</option>
            <option value="name">Nazwa A–Z</option>
            <option value="rate">Kurs malejąco</option>
          </select>
        </div>
      </div>
      {filtered.length ? (
        <>
          <div className="table-scroll">
            <table className="rates-table">
              <thead>
                <tr>
                  <th className="favorite-cell">
                    <span className="sr-only">Obserwuj</span>
                    <Icon name="star" size={14} />
                  </th>
                  <th>Waluta</th>
                  <th>
                    Kurs średni <span className="muted">/ PLN</span>
                  </th>
                  <th>Data publikacji</th>
                  <th>
                    <span className="sr-only">Historia</span>
                  </th>
                </tr>
              </thead>
              <tbody>
                {filtered
                  .slice(0, limit)
                  .map(
                    ({ currency, latest, error: quoteError }) => (
                      <tr
                        key={currency.code}
                        className={
                          currentCode === currency.code
                            ? "selected-row"
                            : ""
                        }
                      >
                        <td className="favorite-cell">
                          <button
                            className={`icon-button favorite-button ${favorites.includes(currency.code) ? "is-favorite" : ""}`}
                            aria-label={`${favorites.includes(currency.code) ? "Usuń z obserwowanych" : "Obserwuj"} ${currency.code}`}
                            aria-pressed={favorites.includes(
                              currency.code,
                            )}
                            onClick={() =>
                              toggleFavorite(currency.code)
                            }
                          >
                            <Icon name="star" size={18} />
                          </button>
                        </td>
                        <td>
                          <button
                            className="currency-cell"
                            onClick={() =>
                              showHistory(currency.code)
                            }
                          >
                            <span
                              className="flag"
                              aria-hidden="true"
                            >
                              {flags[currency.code] || (
                                <Icon name="globe" />
                              )}
                            </span>
                            <span>
                              <strong>{currency.code}</strong>
                              <small>{currency.name}</small>
                            </span>
                          </button>
                        </td>
                        <td className="numeric" title={quoteError}>
                          {latest ? (
                            number(latest.rate)
                          ) : (
                            <span className="muted">
                              Niedostępny
                            </span>
                          )}
                        </td>
                        <td className="date-cell">
                          {date(latest?.date, true)}
                        </td>
                        <td>
                          <button
                            className="history-link"
                            aria-label={`Historia ${currency.code}`}
                            onClick={() =>
                              showHistory(currency.code)
                            }
                          >
                            <span>Historia</span>
                            <Icon name="arrow" size={16} />
                          </button>
                        </td>
                      </tr>
                    ),
                  )}
              </tbody>
            </table>
          </div>
          <div className="table-footer">
            <span>
              Wyświetlasz {Math.min(limit, filtered.length)} z{" "}
              {filtered.length} walut
            </span>
            {limit < filtered.length ? (
              <button
                className="text-button"
                onClick={() => setLimit((value) => value + 20)}
              >
                Pokaż więcej <Icon name="chevron" size={14} />
              </button>
            ) : (
              <span>Kurs za 1 jednostkę waluty</span>
            )}
          </div>
        </>
      ) : (
        <div className="empty">
          <Icon
            name={
              page === "favorites" && !search ? "star" : "search"
            }
            size={32}
          />
          <h3>
            {page === "favorites" && !search
              ? "Twoje ulubione, zawsze pod ręką"
              : "Nie znaleziono walut"}
          </h3>
          <p>
            {page === "favorites" && !search
              ? "Dodaj walutę gwiazdką na liście kursów."
              : "Spróbuj innego kodu lub nazwy waluty."}
          </p>
          <button
            className="button subtle"
            onClick={() =>
              search ? setSearch("") : navigate("overview")
            }
          >
            {search ? "Wyczyść wyszukiwanie" : "Przeglądaj waluty"}
          </button>
        </div>
      )}
    </section>
  );
}
