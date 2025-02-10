package talesofabyss;

public class Puzzle {
    private String question;
    private String answer;
    private String rewardMessage;
    private String reward;
    private boolean solved;

    public Puzzle(String question, String answer, String rewardMessage, String reward) {
        this.question = question;
        this.answer = answer;
        this.rewardMessage = rewardMessage;
        this.reward = reward;
        this.solved = false;
    }

    public String getQuestion() {
        return question;
    }

    public String getRewardMessage() {
        return rewardMessage;
    }

    public String getReward() {
        return reward;
    }

    public boolean isSolved() {
        return solved;
    }

    public boolean solve(String playerAnswer) {
        if (playerAnswer.equalsIgnoreCase(answer)) {
            solved = true;
            return true;
        }
        return false;
    }
}
