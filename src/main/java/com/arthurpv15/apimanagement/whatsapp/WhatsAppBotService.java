package com.arthurpv15.apimanagement.whatsapp;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.arthurpv15.apimanagement.dto.IncomeRequest;
import com.arthurpv15.apimanagement.dto.OutgoingRequest;
import com.arthurpv15.apimanagement.entity.Category;
import com.arthurpv15.apimanagement.entity.Income;
import com.arthurpv15.apimanagement.entity.Outgoing;
import com.arthurpv15.apimanagement.entity.User;
import com.arthurpv15.apimanagement.enums.IncomeStatus;
import com.arthurpv15.apimanagement.enums.OutgoingStatus;
import com.arthurpv15.apimanagement.services.CategoryService;
import com.arthurpv15.apimanagement.services.IncomeService;
import com.arthurpv15.apimanagement.services.OutgoingService;
import com.arthurpv15.apimanagement.repository.UserRepository;

@Service
public class WhatsAppBotService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final IncomeService incomeService;
    private final OutgoingService outgoingService;
    private final CategoryService categoryService;
    private final EmailService emailService;
    private final ConcurrentHashMap<String, PendingEntry> pendingEntries = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, PendingVerification> pendingVerifications = new ConcurrentHashMap<>();

    public WhatsAppBotService(UserRepository userRepository, IncomeService incomeService,
                              OutgoingService outgoingService, CategoryService categoryService,
                              EmailService emailService) {
        this.userRepository = userRepository;
        this.incomeService = incomeService;
        this.outgoingService = outgoingService;
        this.categoryService = categoryService;
        this.emailService = emailService;
    }

    public String processMessage(String from, String body) {
        if (body == null || body.isBlank()) {
            return helpMessage();
        }

        String text = body.trim();

        if (text.equalsIgnoreCase("/help")) {
            return helpMessage();
        }

        if (text.toLowerCase().startsWith("/register")) {
            return handleRegister(from, text);
        }

        if (text.toLowerCase().startsWith("/verify")) {
            return handleVerify(from, text);
        }

        User user = userRepository.findByPhoneNumber(from).orElse(null);
        if (user == null) {
            return "Welcome! Link your account first:\n/register your@email.com";
        }

        if (text.equalsIgnoreCase("cancel")) {
            if (pendingEntries.remove(from) != null) {
                return "Entry canceled.";
            }
            return "Nothing to cancel.";
        }

        PendingEntry pending = pendingEntries.get(from);
        if (pending != null) {
            return handleCategorySelection(from, text, pending, user);
        }

        return handleNewEntry(from, text);
    }

    private String handleRegister(String from, String text) {
        String[] parts = text.split("\\s+");
        if (parts.length < 2) {
            return "Usage: /register your@email.com";
        }

        String email = parts[1];

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return "If that account exists, a verification code has been generated.\nUse /verify <code> to complete linking.";
        }

        String code = String.format("%06d", RANDOM.nextInt(1_000_000));
        pendingVerifications.put(from, new PendingVerification(email, code, Instant.now()));

        emailService.sendVerificationCode(email, code);

        return "If that account exists, a verification code has been generated.\nUse /verify <code> to complete linking.";
    }

    private String handleVerify(String from, String text) {
        String[] parts = text.split("\\s+");
        if (parts.length < 2) {
            return "Usage: /verify <code>";
        }

        PendingVerification pending = pendingVerifications.get(from);
        if (pending == null || pending.getCreatedAt().plus(Duration.ofMinutes(5)).isBefore(Instant.now())) {
            pendingVerifications.remove(from);
            return "No pending verification or code expired. Start with /register your@email.com";
        }

        if (!pending.getCode().equals(parts[1].trim())) {
            return "Invalid code. Try again or start over with /register.";
        }

        pendingVerifications.remove(from);

        User user = userRepository.findByEmail(pending.getEmail()).orElse(null);
        if (user == null) {
            return "Account no longer exists.";
        }

        user.setPhoneNumber(from);
        userRepository.save(user);
        return "Phone linked to " + pending.getEmail() + "!\nSend an expense like \"groceries 50\" or income like \"+salary 3000\".";
    }

    private String handleNewEntry(String from, String text) {
        boolean isIncome = text.startsWith("+");
        String cleaned = isIncome ? text.substring(1).trim() : text;

        String[] parts = cleaned.split("\\s+");
        if (parts.length < 2) {
            return "I didn't understand that.\nSend an expense like \"groceries 50\" or income like \"+salary 3000\".";
        }

        String amountStr = parts[parts.length - 1];
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            return "I didn't understand that.\nSend an expense like \"groceries 50\" or income like \"+salary 3000\".";
        }

        if (amount <= 0) {
            return "Amount must be positive.";
        }

        StringBuilder titleBuilder = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            if (i > 0) titleBuilder.append(" ");
            titleBuilder.append(parts[i]);
        }
        String title = titleBuilder.toString();

        if (title.isBlank()) {
            return "Please include a title. Example: \"groceries 50\"";
        }

        PendingEntry entry = new PendingEntry(title, amount, isIncome);
        pendingEntries.put(from, entry);

        String type = isIncome ? "income" : "expense";
        StringBuilder reply = new StringBuilder();
        reply.append(String.format("\"%s\" R$%.2f as %s.\nPick a category:\n", title, amount, type));

        List<Category> categories = categoryService.searchAll();
        if (categories.isEmpty()) {
            pendingEntries.remove(from);
            return "No categories found. Please create categories first.";
        }

        for (int i = 0; i < categories.size(); i++) {
            reply.append(String.format("%d. %s\n", i + 1, categories.get(i).getTitle()));
        }
        reply.append("\nReply with the number.");

        return reply.toString();
    }

    private String handleCategorySelection(String from, String text, PendingEntry pending, User user) {
        int choice;
        try {
            choice = Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return "Please reply with a number, or type \"cancel\" to cancel.";
        }

        List<Category> categories = categoryService.searchAll();
        if (choice < 1 || choice > categories.size()) {
            return String.format("Invalid choice. Pick a number between 1 and %d, or type \"cancel\".", categories.size());
        }

        Category category = categories.get(choice - 1);
        pendingEntries.remove(from);

        if (pending.isIncome()) {
            IncomeRequest request = new IncomeRequest(
                    pending.getTitle(),
                    pending.getValue(),
                    IncomeStatus.RECEIVED.getCode(),
                    user.getId(),
                    category.getId()
            );
            Income income = incomeService.insert(request, user.getEmail());
            return String.format("Income saved! \"%s\" R$%.2f under %s. (ID: %d)",
                    income.getTitle(), income.getValue(), category.getTitle(), income.getId());
        } else {
            OutgoingRequest request = new OutgoingRequest(
                    pending.getTitle(),
                    pending.getValue(),
                    OutgoingStatus.PAID.getCode(),
                    user.getId(),
                    category.getId()
            );
            Outgoing outgoing = outgoingService.insert(request, user.getEmail());
            return String.format("Expense saved! \"%s\" R$%.2f under %s. (ID: %d)",
                    outgoing.getTitle(), outgoing.getValue(), category.getTitle(), outgoing.getId());
        }
    }

    private String helpMessage() {
        return """
                Finance Bot Commands:
                - "groceries 50" -> record a R$50 expense
                - "+salary 3000" -> record a R$3000 income
                - "cancel" -> cancel pending entry
                - "/register email" -> link your account
                - "/verify code" -> confirm verification code
                - "/help" -> show this message""";
    }

    @Scheduled(fixedRate = 600000)
    public void cleanupExpiredEntries() {
        Instant cutoff = Instant.now().minus(Duration.ofMinutes(10));
        pendingEntries.entrySet().removeIf(e -> e.getValue().getCreatedAt().isBefore(cutoff));
        Instant verifyCutoff = Instant.now().minus(Duration.ofMinutes(5));
        pendingVerifications.entrySet().removeIf(e -> e.getValue().getCreatedAt().isBefore(verifyCutoff));
    }
}
