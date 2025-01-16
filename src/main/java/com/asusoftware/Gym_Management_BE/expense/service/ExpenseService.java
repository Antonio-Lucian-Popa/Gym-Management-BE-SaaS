package com.asusoftware.Gym_Management_BE.expense.service;


import com.asusoftware.Gym_Management_BE.expense.model.dto.CreateExpenseDto;
import com.asusoftware.Gym_Management_BE.expense.model.dto.ExpenseResponseDto;
import com.asusoftware.Gym_Management_BE.expense.model.dto.UpdateExpenseDto;

import java.util.List;
import java.util.UUID;

public interface ExpenseService {

    ExpenseResponseDto createExpense(CreateExpenseDto createExpenseDto);

    ExpenseResponseDto getExpenseById(UUID expenseId);

    List<ExpenseResponseDto> getExpensesByGym(UUID gymId);

    ExpenseResponseDto updateExpense(UUID expenseId, UpdateExpenseDto updateExpenseDto);

    void deleteExpense(UUID expenseId);
}