package com.murali.placify.service;

import com.murali.placify.entity.Batch;
import com.murali.placify.entity.User;
import com.murali.placify.model.BatchResDto;
import com.murali.placify.repository.BatchRepository;
import com.murali.placify.util.ExcelService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
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

    public List<BatchResDto> createBatch(MultipartFile file, String batchName) {
        Batch batch = new Batch();
        batch.setBatchName(batchName);

        batchRepository.save(batch);

        List<String> mailIds = excelService.extractMailIds(file);
        System.out.println(mailIds);
        List<User> users = new ArrayList<>();
        List<BatchResDto> result = new ArrayList<>();

        mailIds.forEach(mailId -> {
            UUID userID = userService.getUserIdByEmail(mailId);
            User user = userService.getUserById(userID);
            user.setBatch(batch);
            users.add(user);

            BatchResDto dto = new BatchResDto();
            dto.setUsername(user.getUsername());
            dto.setRating(user.getLeaderboard().getOverAllRating());
            dto.setMailId(user.getMailID());

            result.add(dto);
        });

        userService.saveUsers(users);

        return result;
    }
}
