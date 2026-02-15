package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.domain.FxSettlementView;

import java.time.LocalDate;
import java.util.List;


public interface FxSettlementService {

    List<FxSettlementView> findAllBySearchCriteria(LocalDate startDate, LocalDate endDate);

    LocalDate getLastTradeDate();
}
