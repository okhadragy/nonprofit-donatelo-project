package com.example.nonprofit.event_decorator;

public class FollowUpDecorator extends EventDecorator {

    private final String feedbackSurveyUrl;
    private final String thankYouNote;

    public FollowUpDecorator(EventComponent decoratedEvent) {
        this(
            decoratedEvent,
            "https://nonprofit.org/feedback/events",
            "Thank you for attending and being part of our non-profit community! Attendance certificates and presentation slides are now available."
        );
    }

    public FollowUpDecorator(
            EventComponent decoratedEvent,
            String feedbackSurveyUrl,
            String thankYouNote) {
        super(decoratedEvent);
        this.feedbackSurveyUrl = (feedbackSurveyUrl != null && !feedbackSurveyUrl.isBlank())
                ? feedbackSurveyUrl
                : "https://nonprofit.org/feedback/events";
        this.thankYouNote = (thankYouNote != null && !thankYouNote.isBlank())
                ? thankYouNote
                : "Thank you for attending and being part of our non-profit community! Attendance certificates and presentation slides are now available.";
    }

    @Override
    public String getDescription() {
        return super.getDescription()
                + "\n\n--- [POST-EVENT FOLLOW-UP & SURVEY] ---"
                + "\nThank You Note: " + thankYouNote
                + "\nAttendee Feedback Survey: " + feedbackSurveyUrl;
    }

    public String getFeedbackSurveyUrl() {
        return feedbackSurveyUrl;
    }

    public String getThankYouNote() {
        return thankYouNote;
    }
}

