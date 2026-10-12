import { Icon } from "./Icon";
import { headings, type Page } from "../lib/navigation";

interface Props {
  page: Page;
  loading: boolean;
  refresh: () => Promise<void>;
}
export function PageHeading({ page, loading, refresh }: Props) {
  return (
    <div className="page-heading">
      <div>
        <span className="eyebrow">
          CURRENCY MONITOR /{" "}
          {page === "overview"
            ? "PRZEGLĄD"
            : page === "history"
              ? "HISTORIA"
              : page === "favorites"
                ? "OBSERWOWANE"
                : "ŹRÓDŁO DANYCH"}
        </span>
        <h1>{headings[page][0]}</h1>
        <p>{headings[page][1]}</p>
      </div>
      <button
        className="button refresh-button"
        disabled={loading}
        onClick={() => void refresh()}
      >
        <Icon
          name="refresh"
          size={17}
          className={loading ? "spinning" : ""}
        />
        {loading ? "Odświeżanie…" : "Odśwież dane"}
      </button>
    </div>
  );
}
