import { useEffect, useState } from "react";
import "./App.css";

interface Currency {
  code: string;
  name: string;
  rate?: number;
}

function App() {
  const [currencies, setCurrencies] = useState<Currency[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetch("/api/currencies")
      .then((res) => {
        if (!res.ok) throw new Error("Network error fetching currencies");
        return res.json();
      })
      .then(async (json: Currency[]) => {
        setCurrencies(json);
        setLoading(false);

        const currenciesWithRates = await Promise.all(
          json.map(async (currency) => {
            try {
              const res = await fetch(`/api/rates/${currency.code}`);
              if (!res.ok) throw new Error("Network error fetching rates");
              const rateData: { rate: number }[] = await res.json();
              const rate = rateData[0]?.rate;
              return { ...currency, rate };
            } catch {
              return { ...currency, rate: undefined };
            }
          }),
        );

        setCurrencies(currenciesWithRates);
      })
      .catch((err) => {
        setError(err.message);
        setLoading(false);
      });
  }, []);

  if (loading) return <p>Loading…</p>;
  if (error) return <p>Error: {error}</p>;

  return (
    <div className="container">
      {currencies.map((currency) => (
        <div className="card" key={currency.code}>
          <div className="code">{currency.code}</div>
          <div className="name">{currency.name}</div>
          <div className="rate">
            {currency.rate !== undefined && currency.rate !== null
              ? `Rate: ${currency.rate}`
              : "Loading rate…"}
          </div>
        </div>
      ))}
    </div>
  );
}

export default App;
