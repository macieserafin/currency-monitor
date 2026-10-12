import { Converter } from "../components/Converter";
import { HistoryPanel } from "../components/HistoryPanel";
import { Icon } from "../components/Icon";
import type { Quote } from "../lib/market";

interface Props {
  currentCode: string;
  quotes: Quote[];
  setSelected: (code: string) => void;
  revision: number;
}
export function HistoryPage({ currentCode, quotes, setSelected, revision }: Props) {
  return (
    <div className="analysis-grid">
      <HistoryPanel
        code={currentCode}
        quotes={quotes}
        onSelect={setSelected}
        revision={revision}
        expanded
      />
      <div className="history-aside">
        <Converter quotes={quotes} />
        <div className="note-card">
          <Icon name="info" />
          <h3>Historia rośnie z czasem</h3>
          <p>
            Wykres pokazuje notowania zapisane w Twojej bazie.
            Zakresy liczone są od ostatniej dostępnej publikacji.
            Dni bez publikacji NBP nie tworzą nowych punktów.
          </p>
        </div>
      </div>
    </div>
  );
}
