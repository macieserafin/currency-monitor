import { useState } from "react";

function initialFavorites(): string[] {
  try {
    const value: unknown = JSON.parse(
      localStorage.getItem("currency-monitor:favorites") || "[]",
    );
    return Array.isArray(value)
      ? value.filter(
        (code): code is string =>
          typeof code === "string" && /^[A-Z]{3}$/.test(code),
      )
      : [];
  } catch {
    return [];
  }
}
export function useFavorites() {
  const [favorites, setFavorites] = useState(initialFavorites);
  const [storageError, setStorageError] = useState("");
  function toggleFavorite(code: string) {
    const next = favorites.includes(code)
      ? favorites.filter((value) => value !== code)
      : [...favorites, code];
    setFavorites(next);
    try {
      localStorage.setItem("currency-monitor:favorites", JSON.stringify(next));
      setStorageError("");
    } catch {
      setStorageError(
        "Przeglądarka blokuje zapis. Obserwowane waluty zostaną zachowane tylko do zamknięcia strony.",
      );
    }
  }
  return { favorites, storageError, toggleFavorite, clearStorageError: () => setStorageError("") };
}
