package com.example.Question_Service.service;

import com.example.Question_Service.dao.QuestionDao;
import com.example.Question_Service.model.Question;
import com.example.Question_Service.model.QuestionWrapper;
import com.example.Question_Service.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    QuestionDao questionDao;

    public ResponseEntity<List<Question>> getAllQuestions() {
        try{
            return new ResponseEntity<>(questionDao.findAll(), HttpStatus.OK);
        }catch(Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<List<Question>> getQuestionsByCategory(String category) {
        try{
            return new ResponseEntity<>(questionDao.findByCategory(category), HttpStatus.OK);
        }catch(Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<String> addQuestion(Question question) {
        questionDao.save(question);
        return new ResponseEntity<>("Question added successfully",  HttpStatus.CREATED);
    }

    public ResponseEntity<String> deleteQuestion(int id) {
        try{
            questionDao.deleteById(id);
            return new ResponseEntity<>("Question deleted successfully",  HttpStatus.OK);
        }catch(Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>("Failed to delete question",  HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<String> updateQuestion(int id, Question question) {
        try{
            Question existingQuestion = questionDao.findById(id).orElse(null);
            if(existingQuestion == null){
                return new ResponseEntity<>("Question not found",  HttpStatus.NOT_FOUND);
            }

            existingQuestion.setCategory(question.getCategory());
            existingQuestion.setQuestionTitle(question.getQuestionTitle());
            existingQuestion.setOption1(question.getOption1());
            existingQuestion.setOption2(question.getOption2());
            existingQuestion.setOption3(question.getOption3());
            existingQuestion.setOption4(question.getOption4());
            existingQuestion.setRightAnswer(question.getRightAnswer());
            existingQuestion.setDifficultylevel(question.getDifficultylevel());

            questionDao.save(existingQuestion);

            return new ResponseEntity<>("Question updated successfully",  HttpStatus.OK);

        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>("Failed to update question",  HttpStatus.BAD_REQUEST);

    }

    public ResponseEntity<List<Integer>> getQuestionsForQuiz(String categoryName, Integer numQuestions) {

        List<Integer> questions = questionDao.findRandomQuestionsByCategory(categoryName, numQuestions);
        return new ResponseEntity<>(questions, HttpStatus.OK);

    }

    public ResponseEntity<List<QuestionWrapper>> getQuestionsFromId(List<Integer> questionIds) {
        List<QuestionWrapper> wrappers = new ArrayList<>();
        //We create a list to store QuestionWrapper objects.
        List<Question> questions = new ArrayList<>();
        //This list will store the actual Question entities fetched from the database.

        for(Integer id : questionIds){
            questions.add(questionDao.findById(id).get());
        }
        //Loop through each ID.
        //Each question is added to the questions list.

        //Now we loop through each question object.
        for(Question question : questions){
            QuestionWrapper wrapper = new QuestionWrapper();
            wrapper.setId(question.getId());
            wrapper.setQuestionTitle(question.getQuestionTitle());
            wrapper.setOption1(question.getOption1());
            wrapper.setOption2(question.getOption2());
            wrapper.setOption3(question.getOption3());
            wrapper.setOption4(question.getOption4());
            wrappers.add(wrapper);
        }

        return new ResponseEntity<>(wrappers, HttpStatus.OK);
    }

    public ResponseEntity<Integer> getScore(List<Response> responses) {

        int right = 0;

        for (Response response : responses) {
            Question question = questionDao.findById(response.getId()).get();
            if(response.getResponse().equals(question.getRightAnswer())) {
                right++;
            }
        }
        return new ResponseEntity<>(right, HttpStatus.OK);
    }
}
