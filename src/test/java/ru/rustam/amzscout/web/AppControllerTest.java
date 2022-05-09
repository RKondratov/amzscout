package ru.rustam.amzscout.web;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.rustam.amzscout.web.AppController.REST_URL;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:test.yaml")
class AppControllerTest {
    private static final String X_FORWARDED_FOR = "X-FORWARDED-FOR";
    private static final String FIRST_CLIENT_IP = "0.0.0.1";
    private static final String SECOND_CLIENT_IP = "0.0.0.2";
    public static final int N_THREADS = 1000;

    @Value("${period}")
    private long PERIOD;
    @Value("${reqNumber}")
    private int REQ_NUMBER;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void get() throws Exception {
        for (int i = 0; i < REQ_NUMBER; i++) {
            perform(MockMvcRequestBuilders.get(REST_URL), FIRST_CLIENT_IP).andExpect(status().isOk());
        }
        Thread.sleep(PERIOD);
        for (int i = 0; i < REQ_NUMBER; i++) {
            perform(MockMvcRequestBuilders.get(REST_URL), FIRST_CLIENT_IP).andExpect(status().isOk());
        }
    }

    @Test
    void getServerError() throws Exception {
        for (int i = 0; i < REQ_NUMBER; i++) {
            perform(MockMvcRequestBuilders.get(REST_URL), SECOND_CLIENT_IP).andExpect(status().isOk());
        }
        perform(MockMvcRequestBuilders.get(REST_URL), SECOND_CLIENT_IP).andExpect(status().is5xxServerError());
    }

    @Test
    void getMultiple() throws InterruptedException, ExecutionException {
        final ExecutorService service = Executors.newFixedThreadPool(N_THREADS);
        final Set<Callable<Boolean>> set = new HashSet<>();
        for (int i = 0; i < N_THREADS; i++) {
            set.add(new TestThread(MockMvcRequestBuilders.get(REST_URL), generateIp(i)));
        }
        final List<Future<Boolean>> futures = service.invokeAll(set);
        for (Future<Boolean> future : futures) {
            Assertions.assertTrue(future.get());
        }
    }

    protected ResultActions perform(MockHttpServletRequestBuilder builder, String clientIp) throws Exception {
        return mockMvc.perform(builder.header(X_FORWARDED_FOR, clientIp));
    }

    private static String generateIp(int i) {
        return String.format("%s.%s.%s.%s", i, i, i, i);
    }

    private class TestThread implements Callable<Boolean> {
        private final MockHttpServletRequestBuilder mockHttpServletRequestBuilder;
        private final String clientIp;

        private TestThread(MockHttpServletRequestBuilder mockHttpServletRequestBuilder, String clientIp) {
            this.mockHttpServletRequestBuilder = mockHttpServletRequestBuilder;
            this.clientIp = clientIp;
        }

        @Override
        public Boolean call() {
            for (int i = 0; i <= REQ_NUMBER; i++) {
                try {
                    if (i != REQ_NUMBER) {
                        perform(mockHttpServletRequestBuilder, clientIp).andExpect(status().isOk());
                    } else {
                        perform(mockHttpServletRequestBuilder, clientIp).andExpect(status().is5xxServerError());
                    }
                } catch (Exception e) {
                    return false;
                }
            }
            return true;
        }
    }
}