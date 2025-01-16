package com.asusoftware.Gym_Management_BE.expense.controller;


import com.asusoftware.Gym_Management_BE.expense.model.dto.CreateExpenseDto;
import com.asusoftware.Gym_Management_BE.expense.model.dto.ExpenseResponseDto;
import com.asusoftware.Gym_Management_BE.expense.model.dto.UpdateExpenseDto;
import com.asusoftware.Gym_Management_BE.expense.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    /**
     * Creează o cheltuială pentru o sală.
     */
    @PostMapping
    public ResponseEntity<ExpenseResponseDto> createExpense(@Valid @RequestBody CreateExpenseDto createExpenseDto) {
        ExpenseResponseDto expense = expenseService.createExpense(createExpenseDto);
        return ResponseEntity.ok(expense);
    }

    /**
     * Obține detaliile unei cheltuieli după ID.
     */
    @GetMapping("/{expenseId}")
    public ResponseEntity<ExpenseResponseDto> getExpenseById(@PathVariable UUID expenseId) {
        ExpenseResponseDto expense = expenseService.getExpenseById(expenseId);
        return ResponseEntity.ok(expense);
    }

    /**
     * Obține toate cheltuielile pentru o sală.
     */
    @GetMapping("/gym/{gymId}")
    public ResponseEntity<List<ExpenseResponseDto>> getExpensesByGym(@PathVariable UUID gymId) {
        List<ExpenseResponseDto> expenses = expenseService.getExpensesByGym(gymId);
        return ResponseEntity.ok(expenses);
    }

    /**
     * Actualizează o cheltuială.
     */
    @PutMapping("/{expenseId}")
    public ResponseEntity<ExpenseResponseDto> updateExpense(@PathVariable UUID expenseId, @Valid @RequestBody UpdateExpenseDto updateExpenseDto) {
        ExpenseResponseDto updatedExpense = expenseService.updateExpense(expenseId, updateExpenseDto);
        return ResponseEntity.ok(updatedExpense);
    }

    /**
     * Șterge o cheltuială după ID.
     */
    @DeleteMapping("/{expenseId}")
    public ResponseEntity<String> deleteExpense(@PathVariable UUID expenseId) {
        expenseService.deleteExpense(expenseId);
        return ResponseEntity.ok("Expense deleted successfully.");
    }
}
