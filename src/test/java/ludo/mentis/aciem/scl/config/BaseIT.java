package ludo.mentis.aciem.scl.config;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import ludo.mentis.aciem.scl.SclApplication;
import ludo.mentis.aciem.scl.repos.CounterpartyRepository;
import ludo.mentis.aciem.scl.repos.CurrencyRepository;
import ludo.mentis.aciem.scl.repos.DocumentRepository;
import ludo.mentis.aciem.scl.repos.DocumentTypeRepository;
import ludo.mentis.aciem.scl.repos.FileContentRepository;
import ludo.mentis.aciem.scl.repos.FxSettlementRepository;
import ludo.mentis.aciem.scl.repos.FxSettlementStepRepository;
import ludo.mentis.aciem.scl.repos.FxStepEvidenceRepository;
import ludo.mentis.aciem.scl.repos.FxTradeRepository;
import ludo.mentis.aciem.scl.repos.RoleRepository;
import ludo.mentis.aciem.scl.repos.UserRepository;
import ludo.mentis.aciem.scl.service.FileDataServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StreamUtils;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.containers.wait.strategy.Wait;


/**
 * Abstract base class to be extended by every IT test. Starts the Spring Boot context with a
 * Datasource connected to the Testcontainers Docker instance. The instance is reused for all tests,
 * with all data wiped out before each test.
 */
@SpringBootTest(
        classes = SclApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("it")
@Sql({"/data/clearAll.sql", "/data/roleData.sql", "/data/userData.sql"})
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
public abstract class BaseIT {

    @ServiceConnection
    private static final MSSQLServerContainer mSSQLServerContainer = new MSSQLServerContainer("mcr.microsoft.com/mssql/server:2022-latest");
    private static final GenericContainer<?> mailpitContainer = new GenericContainer<>("axllent/mailpit:v1.26");
    public static String smtpHost;
    public static Integer smtpPort;
    public static String messagesUrl;
    public static final String ADMIN = "admin@invalid.bootify.io";
    public static final String SETTLEMENT_READ = "settlementRead@x.x";
    public static final String SETTLEMENT_WRITE = "settlementWrite@x.x";
    public static final String COUNTERPARTY_READ = "counterpartyRead@x.x";
    public static final String COUNTERPARTY_WRITE = "counterpartyWrite@x.x";
    public static final String TRADE_READ = "tradeRead@invalid.bootify.io";
    public static final String TRADE_WRITE = "tradeWrite@invalid.bootify.io";
    public static final String PASSWORD = "Bootify!";
    private static final HashMap<String, String> formsSecurityConfigSessions = new HashMap<>();

    static {
        mSSQLServerContainer.acceptLicense()
                .withReuse(true)
                .start();
        mailpitContainer.withExposedPorts(1025, 8025)
                .waitingFor(Wait.forLogMessage(".*accessible via.*", 1))
                .withReuse(true)
                .start();
        smtpHost = mailpitContainer.getHost();
        smtpPort = mailpitContainer.getMappedPort(1025);
        messagesUrl = "http://" + smtpHost + ":" + mailpitContainer.getMappedPort(8025) + "/api/v1/messages";
    }

    @LocalServerPort
    public int serverPort;

    @Autowired
    public CurrencyRepository currencyRepository;

    @Autowired
    public FxSettlementStepRepository fxSettlementStepRepository;

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public RoleRepository roleRepository;

    @Autowired
    public FxSettlementRepository fxSettlementRepository;

    @Autowired
    public FxTradeRepository fxTradeRepository;

    @Autowired
    public CounterpartyRepository counterpartyRepository;

    @Autowired
    public FxStepEvidenceRepository fxStepEvidenceRepository;

    @Autowired
    public DocumentTypeRepository documentTypeRepository;

    @Autowired
    public DocumentRepository documentRepository;

    @Autowired
    public FileContentRepository fileContentRepository;

    @Value("classpath:testFile.txt")
    public Resource testFile;

    @PostConstruct
    public void initRestAssured() {
        RestAssured.port = serverPort;
        RestAssured.urlEncodingEnabled = false;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @DynamicPropertySource
    public static void setDynamicProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.mail.host", () -> smtpHost);
        registry.add("spring.mail.port", () -> smtpPort);
        registry.add("spring.mail.properties.mail.smtp.auth", () -> false);
        registry.add("spring.mail.properties.mail.smtp.starttls.enable", () -> false);
        registry.add("spring.mail.properties.mail.smtp.starttls.required", () -> false);
    }

    @BeforeEach
    public void beforeEach() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .delete(messagesUrl);
    }

    public String readResource(final String resourceName) {
        try {
            return StreamUtils.copyToString(getClass().getResourceAsStream(resourceName), StandardCharsets.UTF_8);
        } catch (final IOException io) {
            throw new UncheckedIOException(io);
        }
    }

    public void prepareUpload(final String uid, final String fileName) {
        try {
            final File uploadFile = new File(FileDataServiceImpl.UPLOAD_DIRECTORY + "/" + uid + "/" + fileName);
            uploadFile.getParentFile().mkdirs();
            uploadFile.createNewFile();
            FileCopyUtils.copy(testFile.getContentAsByteArray(), uploadFile);
        } catch (final IOException io) {
            throw new UncheckedIOException(io);
        }
    }

    public void waitForMessages(final int total) {
        int loop = 0;
        while (loop++ < 25) {
            final Response messagesResponse = RestAssured
                    .given()
                        .accept(ContentType.JSON)
                    .when()
                        .get(messagesUrl);
            if (messagesResponse.jsonPath().getInt("total") == total) {
                return;
            }
            try {
                Thread.sleep(250);
            } catch (final InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
        throw new RuntimeException("Could not find " + total + " messages in time.");
    }

    public String formsSecurityConfigSession(final String username) {
        String formsSecurityConfigSession = formsSecurityConfigSessions.get(username);
        if (formsSecurityConfigSession == null) {
            // init session
            formsSecurityConfigSession = RestAssured
                    .given()
                        .accept(ContentType.HTML)
                    .when()
                        .get("/login")
                    .sessionId();

            // perform login
            formsSecurityConfigSession = RestAssured
                    .given()
                        .sessionId(formsSecurityConfigSession)
                        .csrf("/login")
                        .accept(ContentType.HTML)
                        .contentType(ContentType.URLENC)
                        .formParam("username", username)
                        .formParam("password", PASSWORD)
                    .when()
                        .post("/login")
                    .sessionId();
            formsSecurityConfigSessions.put(username, formsSecurityConfigSession);
        }
        return formsSecurityConfigSession;
    }

}
