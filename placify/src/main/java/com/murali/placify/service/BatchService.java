package com.murali.placify.service;

import com.murali.placify.entity.Batch;
import com.murali.placify.entity.User;
import com.murali.placify.model.BatchResDto;
import com.murali.placify.repository.BatchRepository;
import com.murali.placify.util.ExcelService;
import org.apache.poi.sl.draw.geom.GuideIf;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class BatchService {
    private final BatchRepository batchRepository;
    private final ExcelService excelService;
    private final UserService userService;

    public BatchService(BatchRepository batchRepository, ExcelService excelService, UserService userService) {
        this.batchRepository = batchRepository;
        this.excelService = excelService;
        this.userService = userService;
    }

    public List<Batch> findBatchesByName(List<String> names) {
        return batchRepository.findAllByBatchNameIn(names);
    }

    public List<Batch> saveAll(List<Batch> batches){
        return batchRepository.saveAll(batches);
    }

    private List<User> updateBatchMembership(Batch batch, List<String> newMailIds, List<String> unfoundIds, List<BatchResDto> result) {
        List<User> currentUsers = userService.getUserByBatch(Arrays.asList(batch.getBatchName()));
        Set<String> newMailIdSet = new HashSet<>(newMailIds);

        for (User user : currentUsers) {
            if (!newMailIdSet.contains(user.getMailID())) {
                user.setBatch(null);
            }
        }

        for (String mailId : newMailIds) {
            UUID userID = userService.getUserIdByEmailForBatch(mailId);
            if (userID != null) {
                User user = userService.getUserById(userID);
                if (user.getBatch() == null || !user.getBatch().equals(batch)) {
                    user.setBatch(batch);
                }
                BatchResDto dto = new BatchResDto();
                dto.setUsername(user.getUsername());
                dto.setRating(user.getLeaderboard().getOverAllRating());
                dto.setMailId(user.getMailID());
                result.add(dto);
            } else {
                unfoundIds.add(mailId);
            }
        }

        List<User> allAffectedUsers = new ArrayList<>();
        allAffectedUsers.addAll(currentUsers);
        return allAffectedUsers;
    }

    public Map<String, Object> createBatch(UUID userId, MultipartFile file, String batchName) {
        List<Batch> batches = batchRepository.findAllByBatchNameIn(Arrays.asList(batchName));
        if (!batches.isEmpty()) {
            throw new IllegalArgumentException("batch name already used");
        }

        Batch batch = new Batch();
        batch.setBatchName(batchName);
        batch.setCreatedBy(userService.getUserById(userId));
        batchRepository.save(batch);

        List<String> mailIds = excelService.extractMailIds(file);
        System.out.println(mailIds);
        List<BatchResDto> result = new ArrayList<>();
        List<String> unfoundIds = new ArrayList<>();

        List<User> updatedUsers = updateBatchMembership(batch, mailIds, unfoundIds, result);
        userService.saveUsers(updatedUsers);

        Map<String, Object> res = new HashMap<>();
        res.put("result", result);
        res.put("un-found", unfoundIds);
        return res;
    }

    public Map<String, Object> updateBatch(UUID userId, MultipartFile file, String batchName) {

        List<Batch> batches = batchRepository.findAllByBatchNameIn(Arrays.asList(batchName));
        if (batches.isEmpty()) {
            throw new IllegalArgumentException("No batch present for name: "+ batchName);
        }

        Batch batch = batches.get(0);

        List<String> mailIds = excelService.extractMailIds(file);
        System.out.println("New mailIds: " + mailIds);

        List<BatchResDto> result = new ArrayList<>();
        List<String> unfoundIds = new ArrayList<>();

        List<User> affectedUsers = updateBatchMembership(batch, mailIds, unfoundIds, result);
        userService.saveUsers(affectedUsers);

        Map<String, Object> res = new HashMap<>();
        res.put("result", result);
        res.put("un-found", unfoundIds);
        return res;
    }

    public List<Map<String, Object>> getAllBatchesCreatedBy(UUID userId) {
        List<Batch> batches = batchRepository.findAllByCreatedBy(userService.getUserById(userId));
        List<Map<String, Object>> result = new ArrayList<>();

        batches.forEach(batch -> {
            List<BatchResDto> batchResDtos = new ArrayList<>();

            List<User> sutudents = batch.getStudents();
            sutudents.forEach(s -> {
                BatchResDto dto = new BatchResDto();

                dto.setMailId(s.getMailID());
                dto.setUsername(s.getUsername());
                dto.setRating(s.getLeaderboard().getOverAllRating());

                batchResDtos.add(dto);
            });
            Map<String, Object> map = new HashMap<>();
            map.put("batch id", batch.getBatchID());
            map.put("batch name", batch.getBatchName());
            map.put("students", batchResDtos);
            result.add(map);
        });

        return result;
    }

    public Map<String, Object> deleteBatch(String batchName) {
        List<Batch> batches = batchRepository.findAllByBatchNameIn(Arrays.asList(batchName));
        if (batches.isEmpty()) {
            throw new IllegalArgumentException("No batch present for name" + batchName);
        }

        Batch batch = batches.get(0);

        List<User> usersInBatch = userService.getUserByBatch(Arrays.asList(batchName));
        for (User user : usersInBatch) {
            user.setBatch(null);
        }

        userService.saveUsers(usersInBatch);
        batchRepository.delete(batch);

        Map<String, Object> res = new HashMap<>();
        res.put("message", "Batch deleted and user assignments cleared.");
        return res;
    }

}
