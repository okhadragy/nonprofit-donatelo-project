package com.example.nonprofit.dto;

/**
 * Decorator choices saved for an event (request body and part of EventResponse).
 */
public class EventDecoratorSettings {

    private boolean fundraising;
    private boolean reminder;
    private boolean followup;
    private String fundraisingGoal;

    public EventDecoratorSettings() {
    }

    public EventDecoratorSettings(boolean fundraising, boolean reminder,
                                  boolean followup, String fundraisingGoal) {
        this.fundraising = fundraising;
        this.reminder = reminder;
        this.followup = followup;
        this.fundraisingGoal = fundraisingGoal;
    }

    public boolean isFundraising() {
        return fundraising;
    }

    public void setFundraising(boolean fundraising) {
        this.fundraising = fundraising;
    }

    public boolean isReminder() {
        return reminder;
    }

    public void setReminder(boolean reminder) {
        this.reminder = reminder;
    }

    public boolean isFollowup() {
        return followup;
    }

    public void setFollowup(boolean followup) {
        this.followup = followup;
    }

    public String getFundraisingGoal() {
        return fundraisingGoal;
    }

    public void setFundraisingGoal(String fundraisingGoal) {
        this.fundraisingGoal = fundraisingGoal;
    }
}
