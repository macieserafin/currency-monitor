export interface Currency {
  code: string;
  name: string;
}
export interface Rate {
  date: string;
  rate: number;
}
export interface Quote {
  currency: Currency;
  latest?: Rate;
  error?: string;
}

export const number = (value: number, digits?: number) =>
  new Intl.NumberFormat("pl-PL", {
    minimumFractionDigits: digits ?? 4,
    maximumFractionDigits: digits ?? 8,
  }).format(value);
export const date = (value?: string, short = false) =>
  value
    ? new Intl.DateTimeFormat("pl-PL", {
        day: "2-digit",
        month: short ? "short" : "long",
        ...(!short && { year: "numeric" }),
      }).format(new Date(`${value}T12:00:00`))
    : "Brak notowania";
export function filterHistory(rates: Rate[], days: number) {
  const sorted = [...rates].sort((a, b) => a.date.localeCompare(b.date));
  if (!days || !sorted.length) return sorted;
  // Windows end at the latest available NBP publication, not the browser clock.
  const end = Date.parse(sorted[sorted.length - 1].date);
  return sorted.filter((row) => end - Date.parse(row.date) < days * 86_400_000);
}
export function change(rates: Rate[]) {
  return rates.length > 1
    ? (rates[rates.length - 1].rate / rates[0].rate - 1) * 100
    : null;
}
export function convert(
  amount: string,
  from: number | undefined,
  to: number | undefined,
) {
  const normalized = amount.trim().replace(",", ".");
  if (!/^\d+(\.\d{0,8})?$/.test(normalized) || !from || !to) return null;
  const result = (Number(normalized) * from) / to;
  return Number.isFinite(result) ? result : null;
}
export function historyCsv(code: string, rates: Rate[]) {
  return (
    "\uFEFFWaluta;Data;Kurs PLN\r\n" +
    rates
      .map(
        (row) => `${code};${row.date};${number(row.rate).replace(/\s/g, "")}`,
      )
      .join("\r\n")
  );
}
export function downloadHistory(code: string, rates: Rate[]) {
  const url = URL.createObjectURL(
    new Blob([historyCsv(code, rates)], { type: "text/csv;charset=utf-8;" }),
  );
  const link = document.createElement("a");
  link.href = url;
  link.download = `currency-monitor-${code}.csv`;
  link.click();
  setTimeout(() => URL.revokeObjectURL(url), 1000);
}

export const symbols: Record<string, string> = {
  EUR: "€",
  USD: "$",
  GBP: "£",
  CHF: "Fr",
  JPY: "¥",
  CNY: "¥",
  PLN: "zł",
  SEK: "kr",
  NOK: "kr",
  DKK: "kr",
  CZK: "Kč",
  HUF: "Ft",
  UAH: "₴",
};
export const flags: Record<string, string> = {
  EUR: "🇪🇺",
  USD: "🇺🇸",
  GBP: "🇬🇧",
  CHF: "🇨🇭",
  JPY: "🇯🇵",
  CNY: "🇨🇳",
  PLN: "🇵🇱",
  AUD: "🇦🇺",
  CAD: "🇨🇦",
  SEK: "🇸🇪",
  NOK: "🇳🇴",
  DKK: "🇩🇰",
  CZK: "🇨🇿",
  HUF: "🇭🇺",
  UAH: "🇺🇦",
  TRY: "🇹🇷",
  NZD: "🇳🇿",
  RON: "🇷🇴",
  ZAR: "🇿🇦",
  BRL: "🇧🇷",
  INR: "🇮🇳",
  KRW: "🇰🇷",
  SGD: "🇸🇬",
  HKD: "🇭🇰",
  MXN: "🇲🇽",
  ILS: "🇮🇱",
  THB: "🇹🇭",
  ISK: "🇮🇸",
  PHP: "🇵🇭",
  MYR: "🇲🇾",
  IDR: "🇮🇩",
  CLP: "🇨🇱",
};
