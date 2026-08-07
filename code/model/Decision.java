package model;


import java.time.LocalDateTime;

// UML: Decision類別(抽象類別)
public abstract class Decision {
    protected int decisionId;
    protected String decisionType;
    protected String result;
    protected LocalDateTime decisionTime;


    public String getDecisionType() { return decisionType; }
    public String getResult() { return result; }
    public LocalDateTime getDecisionTime() { return decisionTime; }


    // UML: makeDecision() : String
    public abstract String makeDecision();
}



