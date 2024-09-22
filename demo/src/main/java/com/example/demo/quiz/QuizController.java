package com.example.demo.quiz;

import com.example.demo.user.User;
import com.example.demo.user.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/quiz")
public class QuizController {

    private final QuizService quizService;
    private final UserService userService;
    private final QuizRepository quizRepository;

    public QuizController(QuizService quizService, UserService userService, QuizRepository quizRepository) {
        this.quizService = quizService;
        this.userService = userService;
        this.quizRepository = quizRepository;
    }

    @GetMapping("/{movieTitle}")
    public String showQuiz(@PathVariable String movieTitle, Model model) {
        model.addAttribute("movieTitle", movieTitle);
        return "quiz-form";
    }

    @PostMapping("/{movieTitle}")
    public String submitQuiz(
            @PathVariable String movieTitle,
            @RequestParam Map<String, String> answers,
            RedirectAttributes redirectAttributes,
            Principal principal
    ) {
        // Get correct answers from the JSON
        Map<String, Object> correctAnswers = quizService.getAnswersForMovie(movieTitle);

        // Convert user's answers
        Map<String, Object> userAnswers = Map.of(
                "deathCount", Integer.parseInt(answers.get("deathCount")),
                "lifeCount", Integer.parseInt(answers.get("lifeCount")),
                "mainCharacter", answers.get("mainCharacter"),
                "jumpScares", Integer.parseInt(answers.get("jumpScares")),
                "romance", answers.get("romance"),
                "blood", answers.get("blood"),
                "finalGirl", answers.get("finalGirl"),
                "creaking", answers.get("creaking"),
                "weapon", answers.get("weapon"),
                "killer", answers.get("killer")
        );

        // Calculate user score
        int score = quizService.calculateScore(userAnswers, correctAnswers);

        // Find user by email (from Principal)
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Create and save a new Quiz entity
        Quiz quiz = new Quiz();
        quiz.setMovieTitle(movieTitle);
        quiz.setDeathCountPrediction(Integer.parseInt(answers.get("deathCount")));
        quiz.setLifeCountPrediction(Integer.parseInt(answers.get("lifeCount")));
        quiz.setMainCharacterPrediction(answers.get("mainCharacter"));
        quiz.setJumpScarePrediction(Integer.parseInt(answers.get("jumpScares")));
        quiz.setRomancePrediction(answers.get("romance"));
        quiz.setBloodPrediction(answers.get("blood"));
        quiz.setFinalGirlPrediction(answers.get("finalGirl"));
        quiz.setCreakingPrediction(answers.get("creaking"));
        quiz.setWeaponPrediction(answers.get("weapon"));
        quiz.setKillerPrediction(answers.get("killer"));
        quiz.setScore(score);
        quiz.setUser(user);

        Quiz savedQuiz = quizRepository.save(quiz);

        return "redirect:/quiz/" + movieTitle + "/quiz-result/" + savedQuiz.getId();
    }

    @GetMapping("/{movieTitle}/quiz-result/{quizId}")
    public String showQuizResult(
            @PathVariable String movieTitle,
            @PathVariable Long quizId,
            Principal principal,
            Model model
    ) {
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Optional<Quiz> quizOptional = quizRepository.findById(quizId);

        if (quizOptional.isEmpty() || !quizOptional.get().getUser().equals(user)) {
            return "redirect:/";
        }

        Quiz quiz = quizOptional.get();

        Map<String, Object> correctAnswers = quizService.getAnswersForMovie(movieTitle);

        model.addAttribute("quiz", quiz);
        model.addAttribute("correctAnswers", correctAnswers);

        return "quiz-result";
    }
}
