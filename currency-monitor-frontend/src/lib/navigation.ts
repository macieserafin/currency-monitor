import type { IconName } from "../components/Icon";

export type Page = "overview" | "history" | "favorites";
export const navigation: { id: Page; label: string; icon: IconName }[] = [
  { id: "overview", label: "Przegląd rynku", icon: "grid" },
  { id: "history", label: "Historia kursów", icon: "chart" },
  { id: "favorites", label: "Obserwowane", icon: "star" },
];
export const headings: Record<Page, [string, string]> = {
  overview: [
    "Waluty pod kontrolą.",
    "Spokojne spojrzenie na kursy. Wszystkie dane w jednym miejscu.",
  ],
  history: [
    "Kursy z perspektywy czasu.",
    "Zobacz, jak zmieniał się kurs i zajrzyj do zapisanych notowań.",
  ],
  favorites: [
    "To, co dla Ciebie ważne.",
    "Twoje waluty, bez szukania. Lista zapisana w tej przeglądarce.",
  ],
};
