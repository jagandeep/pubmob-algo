package com.turleylabs.algo.trader.kata.states;

import com.turleylabs.algo.trader.kata.Averages;
import com.turleylabs.algo.trader.kata.RefactorMeAlgorithm;
import com.turleylabs.algo.trader.kata.RefactorMeLogger;
import com.turleylabs.algo.trader.kata.framework.Holding;
import com.turleylabs.algo.trader.kata.framework.Slice;

import java.util.Map;
import java.util.function.Consumer;

public class BoughtAbove50 extends ProfitState {

    private final RefactorMeAlgorithm refactorMeAlgorithm;
    private final Consumer<String> liquidateFunction;
    private final Map<String, Holding> portfolio;
    private final RefactorMeLogger logger;

    public BoughtAbove50(RefactorMeAlgorithm refactorMeAlgorithm, Consumer<String> liquidateFunction, Map<String, Holding> portfolio, RefactorMeLogger logger) {
        this.refactorMeAlgorithm = refactorMeAlgorithm;
        this.liquidateFunction = liquidateFunction;
        this.portfolio = portfolio;
        this.logger = logger;
    }

    @Override
    public String toString() {
        return "BoughtAbove50{}";
    }

    @Override
    public ProfitState onData(Slice data, String symbol, Averages averages, double lastVixClose) {
        logger.logSellAction(data, averages, symbol, refactorMeAlgorithm.lastVix, portfolio);

        if ((!averages.priceBelow50DayMAByAtLeast(data, symbol, .07) &&
                !refactorMeAlgorithm.hasHighVolatility(refactorMeAlgorithm.lastVix) &&
                !averages.did10DayMACrossBelow21DayMA())
                && (averages.isPriceCloseToPeak(data, symbol))) {
            liquidateFunction.accept(symbol);
            return refactorMeAlgorithm.TOOK_PROFITS;
        }

        if (averages.priceBelow50DayMAByAtLeast(data, symbol, .07) ||
                refactorMeAlgorithm.hasHighVolatility(refactorMeAlgorithm.lastVix) ||
                averages.did10DayMACrossBelow21DayMA() ||
                averages.isPriceCloseToPeak(data, symbol)) {
            liquidateFunction.accept(symbol);
            return refactorMeAlgorithm.READY_TO_BUY;
        }
        return refactorMeAlgorithm.BOUGHT_BELOW_50;
    }

}
