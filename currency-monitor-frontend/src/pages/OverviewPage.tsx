import { Converter } from "../components/Converter";
import { HistoryPanel } from "../components/HistoryPanel";
import { QuoteCards } from "../components/QuoteCards";
import type { Quote } from "../lib/market";

interface Props {
  currentCode: string;
  quotes: Quote[];
  setSelected: (code: string) => void;
  revision: number;
}
export function OverviewPage({ currentCode, quotes, setSelected, revision }: Props) {
  return (
    <>
      <QuoteCards quotes={quotes} currentCode={currentCode} setSelected={setSelected} />
      <div className="analysis-grid">
        <HistoryPanel
          code={currentCode}
          quotes={quotes}
          onSelect={setSelected}
          revision={revision}
        />
        <Converter quotes={quotes} />
      </div>
    </>
  );
}
