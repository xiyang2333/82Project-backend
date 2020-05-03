package com.RapidFeedback;

/**
 * Entity of Final Result
 *
 * Created by xiyang on 2020/5/2
 */
public class FinalRemark {
    private int criterionId;    // one of the primary key, user cannot change it here
    private double finalScore;       // score for a criterion

    public FinalRemark(int criterionId, double finalScore) {
        this.criterionId = criterionId;
        this.finalScore = finalScore;
    }

    public FinalRemark() {

    }

    public int getCriterionId() {
        return criterionId;
    }

    public void setCriterionId(int criterionId) {
        this.criterionId = criterionId;
    }

    public double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(double finalScore) {
        this.finalScore = finalScore;
    }
}
