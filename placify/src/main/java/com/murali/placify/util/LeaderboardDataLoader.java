package com.murali.placify.util;

import com.murali.placify.entity.Department;
import com.murali.placify.entity.Leaderboard;
import com.murali.placify.entity.User;
import com.murali.placify.enums.Level;
import com.murali.placify.enums.Role;
import com.murali.placify.repository.DepartmentRepository;
import com.murali.placify.repository.LeaderboardRepo;
import com.murali.placify.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/*
* THIS CLASS IS USED FOR GENERATING TEST DATA FOR DB
* */
@Component
public class LeaderboardDataLoader {

    @Autowired
    private LeaderboardRepo leaderboardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepo;

    private final Random random = new Random();

    @PostConstruct
    public void loadData() {
        // Insert departments
        if (departmentRepo.count() == 0) {
            insertDepartments();
        }

        // Insert users with departments
        if (userRepository.count() == 0) {
            insertUsers();
        }

        // Insert leaderboard data with users
        if (leaderboardRepository.count() == 0) {
            insertLeaderboardData();
        }
    }

    private void insertDepartments() {
        // Sample department names
        String[] departmentNames = {"Computer Science", "Electrical Engineering", "Mechanical Engineering", "Civil Engineering"};

        for (int i = 0; i < departmentNames.length; i++) {
            Department department = new Department();
            department.setDeptID((byte) (i + 1)); // Set a unique department ID (1, 2, 3...)
            department.setDeptName(departmentNames[i]);
            departmentRepo.save(department);
        }
    }

    private void insertUsers() {
        // Load all departments from the database
        List<Department> departments = departmentRepo.findAll();
        if (departments.isEmpty()) {
            throw new IllegalStateException("No departments found in the database.");
        }

        List<User> createdUsers = new ArrayList<>();

        // Insert 25 sample users and associate them with departments
        for (int i = 0; i < 25; i++) {
            User user = new User();
            user.setUsername("user" + (i + 1));
            user.setMailID("user" + (i + 1) + "@example.com");
            user.setRole(random.nextBoolean() ? Role.ROLE_STUDENT : Role.ROLE_STAFF);
            user.setYear(random.nextInt(10) + 1); // Random batch between 1 and 10
            user.setDepartment(departments.get(random.nextInt(departments.size()))); // Randomly assign a department
            userRepository.save(user);
            createdUsers.add(user); // Store the user in the list
        }

        // Now the created users are available in the createdUsers list
        // You can access these users to insert the leaderboard data
    }

    private void insertLeaderboardData() {
        // Load all users from the database (now we have the created users in memory)
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new IllegalStateException("No users found in the database.");
        }

        List<User> createdUsers = new ArrayList<>(users); // Get the created users list

        // Insert leaderboard data with a one-to-one mapping
        for (int i = 0; i < 25; i++) {
            User user = createdUsers.get(i); // Iterate over the created users list and get each user

            int overAllRating = random.nextInt(1000); // Random overall rating between 0 and 999
            int contestRating = random.nextInt(500); // Random contest rating between 0 and 499
            int taskStreak = random.nextInt(50); // Random task streak between 0 and 49
            Level randomLevel = Level.values()[random.nextInt(Level.values().length)]; // Random level

            Leaderboard leaderboard = new Leaderboard();
            leaderboard.setOverAllRating(overAllRating);
            leaderboard.setContestRating(contestRating);
            leaderboard.setTaskStreak(taskStreak);
            leaderboard.setLevel(randomLevel);
            leaderboard.setUser(user); // Set the user to the leaderboard entry (one-to-one mapping)
            leaderboardRepository.save(leaderboard);
        }
    }
}
