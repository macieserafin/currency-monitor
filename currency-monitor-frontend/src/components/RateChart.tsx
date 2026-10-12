import { useId, useState } from "react";
import { date, number, type Rate } from "../lib/market";

export function RateChart({ rates, code }: { rates: Rate[]; code: string }) {
  const id = useId();
  const [focused, setFocused] = useState<number | null>(null);
  if (!rates.length)
    return (
      <div className="empty chart-empty">Brak notowań w wybranym okresie.</div>
    );
  const values = rates.map((row) => row.rate);
  const low = Math.min(...values),
    high = Math.max(...values);
  const padding = (high - low) * 0.25 || high * 0.008;
  const bottom = low - padding,
    top = high + padding;
  const width = 680,
    height = 210,
    left = 5,
    right = 600;
  const x = (index: number) =>
    rates.length === 1
      ? right / 2
      : left +
        ((Date.parse(rates[index].date) - Date.parse(rates[0].date)) /
          (Date.parse(rates[rates.length - 1].date) -
            Date.parse(rates[0].date))) *
          (right - left);
  const labelDigits = Math.max(
    4,
    Math.min(8, Math.ceil(-Math.log10(high)) + 3),
  );
  const hitStart = (index: number) =>
    index === 0 ? 0 : (x(index - 1) + x(index)) / 2;
  const hitEnd = (index: number) =>
    index === rates.length - 1 ? right : (x(index) + x(index + 1)) / 2;
  const y = (value: number) =>
    15 + ((top - value) / (top - bottom)) * (height - 30);
  const points = rates
    .map((row, index) => `${x(index)},${y(row.rate)}`)
    .join(" ");
  const active =
    focused === null ? rates.length - 1 : Math.min(focused, rates.length - 1);
  return (
    <div className="rate-chart">
      <div className="chart-readout" aria-live="polite">
        <span>{date(rates[active].date, true)}</span>
        <strong>
          {number(rates[active].rate)} <small>PLN</small>
        </strong>
      </div>
      <svg
        viewBox={`0 0 ${width} ${height + 28}`}
        role="img"
        aria-label={`Historia ${code}: ${rates.length} notowań. Minimum ${number(low)}, maksimum ${number(high)} PLN.`}
      >
        <defs>
          <linearGradient id={id} x1="0" x2="0" y1="0" y2="1">
            <stop offset="0%" stopColor="#258c6b" stopOpacity=".16" />
            <stop offset="100%" stopColor="#258c6b" stopOpacity="0" />
          </linearGradient>
        </defs>
        {[0, 1, 2, 3].map((i) => {
          const value = bottom + ((top - bottom) * i) / 3;
          return (
            <g key={i}>
              <line
                x1="0"
                x2={right}
                y1={y(value)}
                y2={y(value)}
                stroke="#e8ece7"
                strokeDasharray="4 5"
              />
              <text x={right + 15} y={y(value) + 4} className="axis-label">
                {number(value, labelDigits)}
              </text>
            </g>
          );
        })}
        {rates.length > 1 && (
          <>
            <polygon
              points={`${left},${height} ${points} ${right},${height}`}
              fill={`url(#${id})`}
            />
            <polyline
              points={points}
              fill="none"
              stroke="#238766"
              strokeWidth="2.5"
              strokeLinejoin="round"
            />
          </>
        )}
        <line
          x1={x(active)}
          x2={x(active)}
          y1="8"
          y2={height}
          stroke="#238766"
          strokeDasharray="3 5"
          opacity=".3"
        />
        {rates.map((row, index) => (
          <circle
            key={row.date}
            cx={x(index)}
            cy={y(row.rate)}
            r={index === active ? 5 : 2.5}
            fill="#238766"
            stroke="white"
            strokeWidth="2"
          />
        ))}
        <text x={left} y={height + 23} className="axis-label">
          {date(rates[0].date, true)}
        </text>
        {rates.length > 1 && (
          <text
            x={right}
            y={height + 23}
            textAnchor="end"
            className="axis-label"
          >
            {date(rates[rates.length - 1].date, true)}
          </text>
        )}
        {rates.map((row, index) => (
          <rect
            key={row.date}
            x={hitStart(index)}
            y="0"
            width={hitEnd(index) - hitStart(index)}
            height={height}
            fill="transparent"
            onMouseEnter={() => setFocused(index)}
          >
            <title>
              {date(row.date)}: {number(row.rate)} PLN
            </title>
          </rect>
        ))}
      </svg>
      {rates.length > 1 && (
        <label className="chart-slider">
          <span>Przeglądaj notowania</span>
          <input
            aria-label="Notowanie na wykresie"
            type="range"
            min="0"
            max={rates.length - 1}
            value={active}
            onChange={(event) => setFocused(Number(event.target.value))}
          />
        </label>
      )}
      {rates.length === 1 && (
        <p className="chart-note">
          Pierwsze notowanie. Wykres rozwinie się, gdy backend zapisze kolejne
          kursy.
        </p>
      )}
    </div>
  );
}
