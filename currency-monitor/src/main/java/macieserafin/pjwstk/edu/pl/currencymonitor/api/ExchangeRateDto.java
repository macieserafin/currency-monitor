package macieserafin.pjwstk.edu.pl.currencymonitor.api;

import java.math.BigDecimal;
import java.time.LocalDate;


public record ExchangeRateDto(LocalDate date, BigDecimal rate) {}
