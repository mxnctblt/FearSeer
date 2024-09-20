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
        model.addAttribute("movieTitle", movieTitle); // Pass the movie title to the form
        return "quiz-form"; // This will map to src/main/resources/templates/quiz-form.html
    }

    @PostMapping("/{movieTitle}")
    public String submitQuiz(
            @PathVariable String movieTitle,
            @RequestParam Map<String, String> answers, // Collect all form fields
            RedirectAttributes redirectAttributes,
            Principal principal
    ) {
        // Get correct answers from the JSON
        Map<String, Object> correctAnswers = quizService.getAnswersForMovie(movieTitle);

        // Convert user's answers (from Map<String, String> to Map<String, Object>)
        Map<String, Object> userAnswers = Map.of(
                "deathCount", Integer.parseInt(answers.get("deathCount")),
                "firstDeath", answers.get("firstDeath"),
                "finalSurvivor", answers.get("finalSurvivor"),
                "jumpScares", Integer.parseInt(answers.get("jumpScares")),
                "weapon", answers.get("weapon"),
                "catchphrase", answers.get("catchphrase"),
                "romance", answers.get("romance")
        );

        // Calculate user score
        int score = quizService.calculateScore(userAnswers, correctAnswers);

        // Save the quiz result to DB
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Quiz quiz = new Quiz();
        quiz.setMovieTitle(movieTitle);
        quiz.setDeathCountPrediction(Integer.parseInt(answers.get("deathCount")));
        quiz.setFirstDeathPrediction(answers.get("firstDeath"));
        quiz.setFinalSurvivorPrediction(answers.get("finalSurvivor"));
        quiz.setJumpScarePrediction(Integer.parseInt(answers.get("jumpScares")));
        quiz.setWeaponPrediction(answers.get("weapon"));
        quiz.setCatchphrasePrediction(answers.get("catchphrase"));
        quiz.setRomancePrediction(answers.get("romance"));
        quiz.setScore(score);
        quiz.setUser(user);

        // Save to database (create a repository for `Quiz` model)
        quizRepository.save(quiz);

        redirectAttributes.addFlashAttribute("score", score);
        return "redirect:/quiz-result"; // Direct them to result page
    }
}
