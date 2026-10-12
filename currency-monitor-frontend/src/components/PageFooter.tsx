

export function PageFooter({ updatedAt }: { updatedAt?: Date }) {
  return (
    <footer className="page-footer">
      <span>
        <span className="footer-mark">c.</span> Currency Monitor{" "}
        <span className="separator">·</span> Dane: NBP
      </span>
      <span>
        {updatedAt
          ? `Odczyt API: ${updatedAt.toLocaleTimeString("pl-PL", { hour: "2-digit", minute: "2-digit" })}`
          : "Oczekiwanie na dane"}
      </span>
    </footer>
  );
}
