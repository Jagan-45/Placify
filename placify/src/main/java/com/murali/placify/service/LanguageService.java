package com.murali.placify.service;

import com.murali.placify.entity.Language;
import com.murali.placify.repository.LanguageRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LanguageService {

    private final LanguageRepository languageRepository;


    public LanguageService(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    public List<Language> getAllLanguages() {
        return languageRepository.findAll();
    }

    public void saveAll(List<Language> languages) {
        languageRepository.saveAll(languages);
    }

    public List<Map<String, Object>> getAllowedLangs() {
        List<Language> languages = getAllLanguages();
        List<Map<String, Object>> result = new ArrayList<>();

        languages.forEach(language -> {
            Map<String, Object> map = new HashMap<>();

            if (!language.isArchived() && !language.getName().toLowerCase().contains("javascript")) {
                map.put("lanuage", language.getName());
                map.put("id", language.getJudgeOID());
            }

            result.add(map);
        });

        return result;
    }
}
