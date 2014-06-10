package pl.marekstrejczek.jmsconsumer.jbehave;

import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;

/**
 * Created by maro on 2014-06-04.
 */
public final class MyStoryReporterBuilder extends StoryReporterBuilder {
    public MyStoryReporterBuilder() {
        super();
        withFormats(org.jbehave.core.reporters.Format.HTML, org.jbehave.core.reporters.Format.STATS);
        withFailureTrace(true);
    }
}
