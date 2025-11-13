package io.github.jpmorganchase.fusion;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;

public class Java21UpgradeTest {

    @Test
    public void testJava21BytecodeVersion() throws IOException {
        Class<?> testClass = Fusion.class;
        String className = testClass.getName().replace('.', '/') + ".class";

        try (InputStream classStream = testClass.getClassLoader().getResourceAsStream(className)) {
            assertNotNull(classStream, "Could not load class file for " + testClass.getName());

            byte[] classBytes = new byte[8];
            int bytesRead = classStream.read(classBytes);
            assertEquals(8, bytesRead, "Could not read class file header");

            int majorVersion = ((classBytes[6] & 0xFF) << 8) | (classBytes[7] & 0xFF);

            assertEquals(65, majorVersion, "Expected Java 21 bytecode (major version 65), but got " + majorVersion);
        }
    }

    @Test
    public void testAllDependenciesLoadCorrectly() {
        assertDoesNotThrow(
                () -> {
                    Class.forName("com.google.gson.Gson");
                },
                "Gson dependency should load correctly");

        assertDoesNotThrow(
                () -> {
                    Class.forName("org.slf4j.Logger");
                },
                "SLF4J dependency should load correctly");

        assertDoesNotThrow(
                () -> {
                    Class.forName("ch.qos.logback.classic.Logger");
                },
                "Logback dependency should load correctly");

        assertDoesNotThrow(
                () -> {
                    Class.forName("org.junit.jupiter.api.Test");
                },
                "JUnit Jupiter dependency should load correctly");

        assertDoesNotThrow(
                () -> {
                    Class.forName("com.github.tomakehurst.wiremock.WireMockServer");
                },
                "WireMock dependency should load correctly");

        assertDoesNotThrow(
                () -> {
                    Class.forName("org.mockito.Mockito");
                },
                "Mockito dependency should load correctly");
    }

    @Test
    public void testFusionSDKInstantiation() {
        assertDoesNotThrow(
                () -> {
                    Fusion.builder().bearerToken("test-token").build();
                },
                "Fusion SDK should instantiate without errors on Java 21");
    }

    @Test
    public void testJavaVersionAtRuntime() {
        String javaVersion = System.getProperty("java.version");
        assertNotNull(javaVersion, "Java version should be available");

        String[] versionParts = javaVersion.split("\\.");
        int majorVersion = Integer.parseInt(versionParts[0]);

        assertTrue(majorVersion >= 21, "Expected Java 21 or higher at runtime, but got Java " + majorVersion);
    }
}
