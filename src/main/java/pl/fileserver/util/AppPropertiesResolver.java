package pl.fileserver.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class AppPropertiesResolver {

    @Autowired
    private Environment env;

    @Bean
    public Integer tcpPort() {
        return Integer.valueOf(env.getProperty(AppProperties.TCP_PORT.propName, AppProperties.TCP_PORT.defaultValue));
    }

    @Bean
    public String fileName() {
        return env.getProperty(AppProperties.FILE_NAME.propName);
    }

    private static enum AppProperties {
        TCP_PORT("tcp.port", "3000"),
        FILE_NAME("fileName");

        private String propName;
        private String defaultValue;

        AppProperties(String propName, String defaultValue) {
            this.propName = propName;
            this.defaultValue = defaultValue;
        }

        AppProperties(String propName) {
            this.propName = propName;
        }
    }
}
