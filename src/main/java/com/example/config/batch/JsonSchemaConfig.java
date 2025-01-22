package com.example.config.batch;

import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Configuration
public class JsonSchemaConfig {
    public static final String SCHEMA_VALIDATION_FILE = "src/main/resources/SubscriptionUploadDtoValidationSchema.json";

    @Bean
    public JsonSchema subscriptionUploadDtoJsonSchema() throws FileNotFoundException {
        return JsonSchemaFactory
                .getInstance(SpecVersion.VersionFlag.V4)
                .getSchema(new FileInputStream(SCHEMA_VALIDATION_FILE));
    }

}
