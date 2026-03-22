package com.example.Quiz_Service.model;

import lombok.Data;

@Data
public class QuizDto {
    String categoryName;
    int numQuestions;
    String title;
}
