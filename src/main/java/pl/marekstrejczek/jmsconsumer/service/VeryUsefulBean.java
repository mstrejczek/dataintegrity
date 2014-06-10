package pl.marekstrejczek.jmsconsumer.service;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

/**
 * Created by maro on 2014-06-03.
 */
public class VeryUsefulBean {
    private static final Logger log = Logger.getLogger(VeryUsefulBean.class);

    public String doHeavyProcessing(final Exchange exchange) {
        log.info("Doing heavy processing with "+exchange.getIn().getBody());
        return "output_"+exchange.getExchangeId();
    }
}
