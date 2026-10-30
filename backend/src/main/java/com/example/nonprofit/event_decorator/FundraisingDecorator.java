package com.example.nonprofit.event_decorator;

public class FundraisingDecorator extends EventDecorator {

    private final String fundraisingGoal;
    private final String donationAppeal;
    private final String tierRewards;

    public FundraisingDecorator(EventComponent decoratedEvent) {
        this(
            decoratedEvent,
            "$50,000",
            "Your contributions directly fund life-saving medical packages and community food distribution.",
            "Bronze ($50): Donor Pin | Silver ($250): VIP Seating | Gold ($1,000+): Recognition Plaque & Gala Access"
        );
    }

    public FundraisingDecorator(
            EventComponent decoratedEvent,
            String fundraisingGoal,
            String donationAppeal,
            String tierRewards) {
        super(decoratedEvent);
        this.fundraisingGoal = (fundraisingGoal != null && !fundraisingGoal.isBlank()) ? fundraisingGoal : "$50,000";
        this.donationAppeal = (donationAppeal != null && !donationAppeal.isBlank())
                ? donationAppeal
                : "Your contributions directly fund life-saving medical packages and community food distribution.";
        this.tierRewards = (tierRewards != null && !tierRewards.isBlank())
                ? tierRewards
                : "Bronze ($50): Donor Pin | Silver ($250): VIP Seating | Gold ($1,000+): Recognition Plaque & Gala Access";
    }

    @Override
    public String getDescription() {
        return super.getDescription()
                + "\n\n--- [FUNDRAISING GOALS & REWARDS] ---"
                + "\nTarget Goal: " + fundraisingGoal
                + "\nDonation Appeal: " + donationAppeal
                + "\nDonor Tiers: " + tierRewards;
    }

    public String getFundraisingGoal() {
        return fundraisingGoal;
    }

    public String getDonationAppeal() {
        return donationAppeal;
    }

    public String getTierRewards() {
        return tierRewards;
    }
}

