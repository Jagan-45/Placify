package com.murali.placify.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ContestCache {

    private ConcurrentHashMap<UUID, HashMap<UUID, Set<UUID>>> contestUserAcProblemsCache = new ConcurrentHashMap<>();

}
