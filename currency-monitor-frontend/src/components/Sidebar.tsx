import { Icon } from "./Icon";
import { navigation, type Page } from "../lib/navigation";

interface Props {
  page: Page;
  favorites: string[];
  navigate: (page: Page) => void;
}
export function Sidebar({ page, favorites, navigate }: Props) {
  return (
    <aside className="sidebar">
      <a
        href="#"
        className="brand"
        onClick={(event) => {
          event.preventDefault();
          navigate("overview");
        }}
        aria-label="Currency Monitor — strona główna"
      >
        <span className="brand-mark">
          <span />
          <span />
          <span />
        </span>
        <span>
          currency<span className="brand-sub">MONITOR</span>
        </span>
      </a>
      <span className="nav-label">TWOJE CENTRUM WALUT</span>
      <nav aria-label="Nawigacja główna">
        {navigation.map((item) => (
          <button
            key={item.id}
            className={`nav-item ${page === item.id ? "selected" : ""}`}
            aria-label={item.label}
            aria-current={page === item.id ? "page" : undefined}
            onClick={() => navigate(item.id)}
          >
            <Icon name={item.icon} />
            <span>{item.label}</span>
            {item.id === "favorites" && favorites.length > 0 && (
              <span className="nav-count">{favorites.length}</span>
            )}
          </button>
        ))}
      </nav>
      <div className="sidebar-bottom">
        <div className="source-illustration" aria-hidden="true">
          <Icon name="globe" size={62} />
          <span>PLN</span>
        </div>
        <h3>
          Świat walut.
          <br />
          Polska perspektywa.
        </h3>
        <p>
          Oficjalne kursy średnie
          <br />
          Narodowego Banku Polskiego.
        </p>
        <span className="source-tag">
          <span /> NBP · Tabela A
        </span>
      </div>
      <div className="sidebar-foot">Mniej szumu. Więcej informacji.</div>
    </aside>
  );
}
