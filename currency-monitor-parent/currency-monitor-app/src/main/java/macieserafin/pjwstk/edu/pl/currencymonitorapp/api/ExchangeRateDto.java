package macieserafin.pjwstk.edu.pl.currencymonitorapp.api;

import java.math.BigDecimal;
import java.time.LocalDate;


public record ExchangeRateDto(LocalDate date, BigDecimal rate) {}
