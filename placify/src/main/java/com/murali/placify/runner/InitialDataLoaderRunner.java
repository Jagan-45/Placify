package com.murali.placify.runner;

import com.murali.placify.entity.Language;
import com.murali.placify.service.LanguageService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InitialDataLoaderRunner implements ApplicationRunner {

    private final LanguageService languageService;
    private final WebClient webClient;

    private final String[] allowedLanguages = new String[]{"java", "c++", "python"};

    public InitialDataLoaderRunner(LanguageService languageService, WebClient webClient) {
        this.languageService = languageService;
        this.webClient = webClient;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        List<Language> existingLanguages = languageService.getAllLanguages();
        boolean shouldLoad = false;
        if (existingLanguages.isEmpty()) {
            shouldLoad = true;
        } else {
            shouldLoad = existingLanguages.stream().anyMatch(Language::isArchived);
        }

        if (!shouldLoad) {
            return;
        }

        LanguageSummary[] summaries = webClient.get()
                .uri("http://localhost:2358/languages")
                .retrieve()
                .bodyToMono(LanguageSummary[].class)
                .block();

        if (summaries == null) {
            return;
        }

        List<Integer> allowedIds = Arrays.stream(summaries)
                .filter(summary -> {
                    String lowerName = summary.getName().toLowerCase();
                    for (String allowed : allowedLanguages) {
                        if (lowerName.contains(allowed.toLowerCase())) {
                            return true;
                        }
                    }
                    return false;
                })
                .map(LanguageSummary::getId)
                .collect(Collectors.toList());

        List<Language> languagesToSave = new ArrayList<>();
        for (Integer id : allowedIds) {
            LanguageDetail detail = webClient.get()
                    .uri("http://localhost:2358/languages/{id}", id)
                    .retrieve()
                    .bodyToMono(LanguageDetail.class)
                    .block();

            if (detail != null && !detail.isArchived()) {
                Language language = new Language();
                language.setName(detail.getName());
                language.setJudgeOID(detail.getId());
                language.setArchived(detail.isArchived());
                languagesToSave.add(language);
            }
        }

        if (!languagesToSave.isEmpty()) {
            languageService.saveAll(languagesToSave);
        }
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class LanguageSummary {
        private int id;
        private String name;
    }


    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    private static class LanguageDetail {
        private int id;
        private String name;
        private boolean is_archived;
        private String source_file;
        private String compile_cmd;
        private String run_cmd;

        public boolean isArchived() {
            return is_archived;
        }
    }
}

