package pl.marekstrejczek.jmsconsumer.aop;

import org.apache.log4j.Logger;

/**
 * Created by maro on 2014-06-03.
 */
public class ThrowExceptionAspectBean {
    private static final Logger log = Logger.getLogger(ThrowExceptionAspectBean.class);

    private boolean exceptionFlag;

    public void throwException() {
        if (exceptionFlag) throw new RuntimeException("Oh crap");
        else log.info("NOT throwing exception");
    }

    public void setExceptionFlag(final boolean exceptionFlag) {
        this.exceptionFlag = exceptionFlag;
    }
}
