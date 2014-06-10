package pl.marekstrejczek.jmsconsumer;

import org.jbehave.core.InjectableEmbedder;
import org.jbehave.core.annotations.Configure;
import org.jbehave.core.annotations.UsingEmbedder;
import org.jbehave.core.annotations.spring.UsingSpring;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.failures.FailingUponPendingStep;
import org.jbehave.core.failures.RethrowingFailure;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.spring.SpringAnnotatedEmbedderRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.marekstrejczek.jmsconsumer.jbehave.MyStoryReporterBuilder;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by maro on 2014-06-02.
 */
@RunWith(SpringAnnotatedEmbedderRunner.class)
@Configure( pendingStepStrategy = FailingUponPendingStep.class, failureStrategy = RethrowingFailure.class, storyReporterBuilder = MyStoryReporterBuilder.class)
@UsingEmbedder(embedder = Embedder.class, verboseFailures = true, generateViewAfterStories = true, ignoreFailureInStories = false, ignoreFailureInView = true)
@UsingSpring(resources = { "spring-test-context.xml" }, ignoreContextFailure = false)
public class IntegrityTest extends InjectableEmbedder {

    @Test
    public void run() throws URISyntaxException {
        injectedEmbedder().runStoriesAsPaths(storyPaths());
    }

    protected List<String> storyPaths() throws URISyntaxException {
        URL storyLocation = this.getClass().getClassLoader().getResource("stories/integrity.story");
        File storyFile = new File(storyLocation.toURI());
        String storyDirectory = storyFile.getParentFile().getParent();
        return new StoryFinder().findPaths(storyDirectory, asList("**/*.story"), null);
    }
}