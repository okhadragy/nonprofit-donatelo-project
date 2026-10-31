export class DonationSmsNotifier {
  constructor(onNotification) {
    this.onNotification = onNotification;
  }

  update(donation) {
    const donor = donation.donorName ? ` from ${donation.donorName}` : "";
    this.onNotification({
      channel: "SMS notification",
      message: `SMS: Donation of $${Number(donation.amount).toLocaleString()} received${donor}.`,
    });
  }
}
