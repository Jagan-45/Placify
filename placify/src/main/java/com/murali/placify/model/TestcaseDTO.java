package com.murali.placify.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestcaseDTO {

        @NotBlank
        @NotNull
        private String tcName;
        @NotBlank
        private String inputFields;
        @NotBlank
        private String outputField;
        private boolean sample;
        private String problemSlug;
        @Size(max = 500, message = "Explanation length exceeded")
        private String explanation;
}

