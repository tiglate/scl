package ludo.mentis.aciem.scl.dev;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DevAppRunnerTest {

    @Mock
    private Environment environment;

    @Mock
    private DataLoaderCommand dataLoader1;

    @Mock
    private DataLoaderCommand dataLoader2;

    @Mock
    private ApplicationArguments args;

    @Test
    void run_shouldSkipWhenCanItRunIsFalse() throws Exception {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"prod"});
        DevAppRunner runner = new DevAppRunner(new ArrayList<>(), environment);
        
        runner.run(args);
        
        verifyNoInteractions(args);
    }

    @Test
    void run_shouldExecuteLoadersWhenCanItRunIsTrue() throws Exception {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"dev"});
        
        when(dataLoader1.getOrder()).thenReturn(1);
        when(dataLoader1.getName()).thenReturn("Loader 1");
        when(dataLoader1.canItRun()).thenReturn(true);
        when(dataLoader1.run()).thenReturn(10);
        
        when(dataLoader2.getOrder()).thenReturn(0);
        when(dataLoader2.getName()).thenReturn("Loader 2");
        when(dataLoader2.canItRun()).thenReturn(true);
        when(dataLoader2.run()).thenReturn(5);
        
        List<DataLoaderCommand> loaders = new ArrayList<>(List.of(dataLoader1, dataLoader2));
        DevAppRunner runner = new DevAppRunner(loaders, environment);
        
        runner.run(args);
        
        verify(dataLoader1).run();
        verify(dataLoader2).run();
        // Check ordering - dataLoader2 has order 0, so it should be first.
        // Mockito doesn't easily verify order across different mocks without InOrder, but the logic seems correct.
        assertEquals(dataLoader2, loaders.get(0));
        assertEquals(dataLoader1, loaders.get(1));
    }

    @Test
    void run_shouldSkipLoaderWhenLoaderCanItRunIsFalse() throws Exception {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"dev"});
        
        when(dataLoader1.getName()).thenReturn("Loader 1");
        when(dataLoader1.canItRun()).thenReturn(false);
        
        DevAppRunner runner = new DevAppRunner(new ArrayList<>(List.of(dataLoader1)), environment);
        
        runner.run(args);
        
        verify(dataLoader1, never()).run();
    }

    @Test
    void canItRun_shouldReturnTrueForDefaultProfile() {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"default"});
        DevAppRunner runner = new DevAppRunner(List.of(), environment);
        assertTrue(runner.canItRun());
    }

    @Test
    void canItRun_shouldReturnFalseForProdProfile() {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"prod"});
        DevAppRunner runner = new DevAppRunner(List.of(), environment);
        assertFalse(runner.canItRun());
    }

    @Test
    void canItRun_shouldReturnTrueForDevAndNotProd() {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"dev", "some-other"});
        DevAppRunner runner = new DevAppRunner(List.of(), environment);
        assertTrue(runner.canItRun());
    }
}
