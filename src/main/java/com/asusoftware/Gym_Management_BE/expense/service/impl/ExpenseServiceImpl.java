package com.asusoftware.Gym_Management_BE.expense.service.impl;

import com.asusoftware.Gym_Management_BE.expense.model.Expense;
import com.asusoftware.Gym_Management_BE.expense.model.dto.CreateExpenseDto;
import com.asusoftware.Gym_Management_BE.expense.model.dto.ExpenseResponseDto;
import com.asusoftware.Gym_Management_BE.expense.model.dto.UpdateExpenseDto;
import com.asusoftware.Gym_Management_BE.expense.repository.ExpenseRepository;
import com.asusoftware.Gym_Management_BE.expense.service.ExpenseService;
import com.asusoftware.Gym_Management_BE.gym.model.Gym;
import com.asusoftware.Gym_Management_BE.gym.repository.GymRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private GymRepository gymRepository;


    @Override
    public ExpenseResponseDto createExpense(CreateExpenseDto createExpenseDto) {
        Gym gym = gymRepository.findById(createExpenseDto.getGymId())
                .orElseThrow(() -> new RuntimeException("Gym not found"));

        Expense expense = new Expense();
        expense.setGym(gym);
        expense.setCategory(createExpenseDto.getCategory());
        expense.setAmount(createExpenseDto.getAmount());
        expense.setDescription(createExpenseDto.getDescription());
        expense.setDate(LocalDate.now());

        expense = expenseRepository.save(expense);
        return mapToResponseDto(expense);
    }

    @Override
    public ExpenseResponseDto getExpenseById(UUID expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        return mapToResponseDto(expense);
    }

    @Override
    public List<ExpenseResponseDto> getExpensesByGym(UUID gymId) {
        return expenseRepository.findByGymId(gymId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ExpenseResponseDto updateExpense(UUID expenseId, UpdateExpenseDto updateExpenseDto) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        expense.setCategory(updateExpenseDto.getCategory());
        expense.setAmount(updateExpenseDto.getAmount());
        expense.setDescription(updateExpenseDto.getDescription());

        expense = expenseRepository.save(expense);
        return mapToResponseDto(expense);
    }

    @Override
    public void deleteExpense(UUID expenseId) {
        expenseRepository.deleteById(expenseId);
    }

    private ExpenseResponseDto mapToResponseDto(Expense expense) {
        ExpenseResponseDto dto = new ExpenseResponseDto();
        dto.setId(expense.getId());
        dto.setGymId(expense.getGym().getId());
        dto.setCategory(expense.getCategory());
        dto.setAmount(expense.getAmount());
        dto.setDescription(expense.getDescription());
        dto.setDate(expense.getDate().toString());
        return dto;
    }
}