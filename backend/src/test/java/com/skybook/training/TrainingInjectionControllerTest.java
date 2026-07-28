package com.skybook.training;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Regression coverage for CWE-78 (OS command injection) on the training command
 * endpoints. These cases assert on the rejection path only, so no subprocess is
 * started and the tests stay hermetic.
 */
class TrainingInjectionControllerTest {

    private static final String REJECTION_PREFIX = "Rejected host (allowlist only):";

    private final TrainingInjectionController controller = new TrainingInjectionController();

    @Test
    void commandEndpointRejectsChainedCommandPayload() throws Exception {
        // Reported exploit shape: an allowlisted host followed by a command separator.
        String output = controller.insecureCommand("127.0.0.1; id");

        assertThat(output).startsWith(REJECTION_PREFIX);
        assertThat(output).doesNotContain("uid=");
    }

    @Test
    void commandEndpointRejectsOtherShellMetacharacters() throws Exception {
        assertThat(controller.insecureCommand("127.0.0.1 && id")).startsWith(REJECTION_PREFIX);
        assertThat(controller.insecureCommand("127.0.0.1 | id")).startsWith(REJECTION_PREFIX);
        assertThat(controller.insecureCommand("$(id)")).startsWith(REJECTION_PREFIX);
    }

    @Test
    void commandEndpointRejectsArbitraryHost() throws Exception {
        assertThat(controller.insecureCommand("evil.example.com")).startsWith(REJECTION_PREFIX);
    }

    @Test
    void commandEndpointRejectsMissingHost() throws Exception {
        assertThat(controller.insecureCommand(null)).startsWith(REJECTION_PREFIX);
    }

    @Test
    void secureCommandEndpointRejectsTheSamePayload() throws Exception {
        assertThat(controller.secureCommand("127.0.0.1; id")).startsWith(REJECTION_PREFIX);
    }
}
