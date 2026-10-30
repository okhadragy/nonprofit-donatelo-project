export class DonationSubject {
  constructor() {
    this.observers = new Set();
  }

  addObserver(observer) {
    this.observers.add(observer);
    return () => this.removeObserver(observer);
  }

  removeObserver(observer) {
    this.observers.delete(observer);
  }

  notifyObservers(donation) {
    this.observers.forEach((observer) => observer(donation));
  }
}
