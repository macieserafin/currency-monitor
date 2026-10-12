import { Sidebar } from "./components/Sidebar";
import { Topbar } from "./components/Topbar";
import { PageHeading } from "./components/PageHeading";
import { PageFooter } from "./components/PageFooter";
import { RatesTable } from "./components/RatesTable";
import { OverviewPage } from "./pages/OverviewPage";
import { HistoryPage } from "./pages/HistoryPage";
import type { Page } from "./lib/navigation";
import { useFavorites } from "./lib/useFavorites";
import { useState } from "react";
import { Icon } from "./components/Icon";
import { date } from "./lib/market";
import { useMarket } from "./lib/useMarket";
import "./App.css";

export default function App() {
  const { quotes, loading, error, updatedAt, revision, refresh } = useMarket();
  const [page, setPage] = useState<Page>("overview");
  const [selected, setSelected] = useState("EUR");
  const [sort, setSort] = useState("code");
  const { favorites, storageError, toggleFavorite, clearStorageError } = useFavorites();
  const currentCode = quotes.some((row) => row.currency.code === selected)
    ? selected
    : quotes[0]?.currency.code || "EUR";
  const validQuotes = quotes.filter((row) => row.latest);
  const latestDate = validQuotes
    .map((row) => row.latest!.date)
    .sort()
    .at(-1);
  const failedCount = quotes.length - validQuotes.length;
  function navigate(value: Page) {
    setPage(value);
    window.scrollTo({ top: 0, behavior: "smooth" });
  }
  function showHistory(code: string) {
    setSelected(code);
    navigate("history");
  }
  return (
    <div className="app-shell">
      <a className="skip-link" href="#main">
        Przejdź do treści
      </a>
      <Sidebar page={page} favorites={favorites} navigate={navigate} />
      <div className="workspace">
        <Topbar page={page} error={error} loading={loading} failedCount={failedCount} />
        <main id="main">
          <PageHeading page={page} loading={loading} refresh={refresh} />
          {error && (
            <div className="notice error-notice" role="alert">
              <Icon name="info" />
              <div>
                <strong>Nie udało się odświeżyć danych.</strong>
                <p>
                  {error}
                  {quotes.length > 0 &&
                    " Poniżej widzisz dane z poprzedniego odczytu."}
                </p>
              </div>
              <button
                className="text-button"
                onClick={() => void refresh()}
                disabled={loading}
              >
                Ponów
              </button>
            </div>
          )}
          {storageError && (
            <div className="notice" role="status">
              <Icon name="info" />
              <p>{storageError}</p>
              <button
                className="icon-button"
                aria-label="Zamknij komunikat"
                onClick={() => clearStorageError()}
              >
                <Icon name="close" />
              </button>
            </div>
          )}
          {failedCount > 0 && !error && (
            <div className="notice" role="status">
              <Icon name="info" />
              <p>
                Nie udało się pobrać {failedCount} z {quotes.length} kursów.
                Dostępne notowania pozostają widoczne.
              </p>
              <button
                className="text-button"
                onClick={() => void refresh()}
                disabled={loading}
              >
                Ponów
              </button>
            </div>
          )}
          <div className="market-meta">
            <span>
              <Icon name="clock" size={15} /> Ostatnia publikacja:{" "}
              <strong>{date(latestDate)}</strong>
            </span>
            <span className="meta-right">
              {quotes.length} walut <span className="separator">/</span> Waluta
              bazowa <strong>PLN</strong>
            </span>
          </div>
          {loading && !quotes.length ? (
            <div
              className="dashboard-skeleton"
              role="status"
              aria-label="Ładowanie notowań"
            >
              <div className="skeleton-cards">
                {[0, 1, 2, 3].map((value) => (
                  <div key={value} className="skeleton" />
                ))}
              </div>
              <div className="skeleton skeleton-main" />
              <p>Pobieramy najnowsze notowania…</p>
            </div>
          ) : !quotes.length ? (
            <section className="panel empty initial-empty">
              <span className="soft-icon">
                <Icon name="globe" size={32} />
              </span>
              <h2>
                {error
                  ? "Czekamy na połączenie"
                  : "Tu zaczyna się Twój przegląd walut"}
              </h2>
              <p>
                {error
                  ? "Uruchom backend i spróbuj ponownie."
                  : "Baza jest jeszcze pusta. Backend cyklicznie pobiera kursy NBP — dane pojawią się po pierwszym udanym pobraniu."}
              </p>
              <button
                className="button primary"
                disabled={loading}
                onClick={() => void refresh()}
              >
                <Icon name="refresh" size={16} />
                Sprawdź ponownie
              </button>
            </section>
          ) : (
            <>
              {page === "overview" && (
                <OverviewPage currentCode={currentCode} quotes={quotes} setSelected={setSelected} revision={revision} />
              )}
              {page === "history" && (
                <HistoryPage currentCode={currentCode} quotes={quotes} setSelected={setSelected} revision={revision} />
              )}
              {(page === "overview" || page === "favorites") && (
                <RatesTable sort={sort} setSort={setSort} key={page} quotes={quotes} page={page} favorites={favorites} currentCode={currentCode} toggleFavorite={toggleFavorite} showHistory={showHistory} navigate={navigate} />
              )}

            </>
          )}
          <PageFooter updatedAt={updatedAt} />
        </main>
      </div>
    </div>
  );
}
