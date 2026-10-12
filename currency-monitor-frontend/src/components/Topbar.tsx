import { Icon } from "./Icon";
import { navigation, type Page } from "../lib/navigation";

interface Props {
  page: Page;
  error: string;
  loading: boolean;
  failedCount: number;
}
export function Topbar({ page, error, loading, failedCount }: Props) {
  return (
    <header className="topbar">
      <div className="breadcrumb">
        Twoje centrum walut <Icon name="chevron" size={13} />
        <strong>
          {navigation.find((item) => item.id === page)?.label}
        </strong>
      </div>
      <div
        className={`connection ${error ? "offline" : failedCount ? "partial" : ""}`}
      >
        <span />
        {error
          ? "Problem z połączeniem"
          : loading
            ? "Pobieranie danych"
            : failedCount
              ? "Część kursów niedostępna"
              : "Połączono z API"}
      </div>
    </header>
  );
}
