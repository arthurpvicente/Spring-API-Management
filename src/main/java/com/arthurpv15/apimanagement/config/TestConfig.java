package com.arthurpv15.apimanagement.config;

import java.time.Instant;
import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.arthurpv15.apimanagement.entity.Category;
import com.arthurpv15.apimanagement.entity.Income;
import com.arthurpv15.apimanagement.entity.Outgoing;
import com.arthurpv15.apimanagement.entity.User;
import com.arthurpv15.apimanagement.enums.IncomeStatus;
import com.arthurpv15.apimanagement.enums.OutgoingStatus;
import com.arthurpv15.apimanagement.repository.CategoryRepository;
import com.arthurpv15.apimanagement.repository.IncomeRepository;
import com.arthurpv15.apimanagement.repository.OutgoingRepository;
import com.arthurpv15.apimanagement.repository.UserRepository;

@Configuration
@Profile("teste")
public class TestConfig implements CommandLineRunner {

    private final UserRepository userRepository;
    private final IncomeRepository incomeRepository;
    private final OutgoingRepository outgoingRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    public TestConfig(UserRepository userRepository, IncomeRepository incomeRepository,
                      OutgoingRepository outgoingRepository, CategoryRepository categoryRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.incomeRepository = incomeRepository;
        this.outgoingRepository = outgoingRepository;
        this.categoryRepository = categoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        User user1 = new User("Arthur", "arthur@example.com", passwordEncoder.encode("123456"));
        User user2 = new User("Matheus", "matheus@example.com", passwordEncoder.encode("123456"));

        userRepository.saveAll(Arrays.asList(user1, user2));

        Category category1 = new Category("Shopping");
        Category category2 = new Category("Food");
        Category category3 = new Category("Job");
        Category category4 = new Category("Monthly Expenses");

        categoryRepository.saveAll(Arrays.asList(category1, category2, category3, category4));

        Income income1 = new Income("Salary", 2900.00, Instant.now(), IncomeStatus.RECEIVED, user1, category3);
        Income income2 = new Income("Salary", 4900.00, Instant.now(), IncomeStatus.RECEIVED, user2, category3);
        Income income3 = new Income("Bonus", 900.00, Instant.now(), IncomeStatus.SCHEDULED, user1, category3);

        incomeRepository.saveAll(Arrays.asList(income1, income2, income3));

        Outgoing outgoing1 = new Outgoing("Clothes", 100.00, Instant.now(), OutgoingStatus.PAID, user1, category1);
        Outgoing outgoing2 = new Outgoing("Rent", 150.00, Instant.now(), OutgoingStatus.LATE, user1, category4);
        Outgoing outgoing3 = new Outgoing("Lunch", 25.00, Instant.now(), OutgoingStatus.PAID, user1, category2);

        outgoingRepository.saveAll(Arrays.asList(outgoing1, outgoing2, outgoing3));
    }
}
