export class DonationInSiteNotifier {
  constructor(onNotification) {
    this.onNotification = onNotification;
  }

  update(donation) {
    const donor = donation.donorName ? ` from ${donation.donorName}` : "";
    this.onNotification({
      channel: "In-site notification",
      message: `A donation of $${Number(donation.amount).toLocaleString()} was added${donor}.`,
    });
  }
}
