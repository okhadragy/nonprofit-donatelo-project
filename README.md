# Nonprofit Donatelo – Management System

A full-stack web application designed for charitable organizations to manage donations, community events, volunteer staffing, and beneficiary resource distribution.

Built with a layered 3-tier architecture using a **Spring Boot 3** REST API backend and a **React 18** frontend.

---

## Key Features

- **Role-Based Security**: JWT authentication with distinct roles for Admin, Donor, Volunteer, and Beneficiary.
- **Donation Processing**: Multi-method donation intake (Cash, Card, Check) with receipt and tax document generation.
- **Volunteer Management**: Volunteer directory with skill/qualification filtering, event shift staffing, and hours tracking.
- **Events & Ticketing**: Event scheduling, ticket tier management, attendee registration, and dynamic feature decoration.
- **Resource Distribution**: Inventory cataloging and tracking of aid distributions to beneficiaries.

---

## Design Patterns

| Pattern | Location | Description |
| :--- | :--- | :--- |
| **Strategy** | `donation_strategy/` | `DonationStrategyContext` delegates payment handling to `CashDonation`, `CardDonation`, or `CheckDonation`. |
| **Factory Method + Singleton** | `document_factory/` | Abstract `DocumentFactory` with singleton concrete factories (`ReceiptDocumentFactory`, `TaxDocumentFactory`) creating receipt and tax documents. |
| **Observer (Donations)** | `donation_observer/` | `DonationSubject` notifies subscribers (`EmailNotifier`, `SmsNotifier`, `InSiteNotifier`) on new donations. |
| **Observer (Events)** | `event_observer/` | `EventSubject` pushes schedule and detail updates to `EventObserver` subscribers. |
| **Decorator** | `event_decorator/` | `EventDecorator` dynamically adds features (`FundraisingDecorator`, `ReminderDecorator`, `FollowUpDecorator`) to an `EventComponent`. |

---

## Tech Stack

- **Backend**: Java 17, Spring Boot 3.3.5, Spring Data JPA, Hibernate, Spring Security, JJWT
- **Database**: H2 In-Memory Database (`jdbc:h2:mem:nonprofitdb`)
- **Frontend**: React 18, Vite, CSS
- **UML**: PlantUML (`nonprofit-class-diagram.puml`), Mermaid (`nonprofit-class-diagram-mermaid.md`)

---

## Getting Started

### Prerequisites
- Java 17+
- Node.js 18+ and npm

### 1. Run the Backend
```bash
cd backend
# Windows
.\mvnw.cmd spring-boot:run

# macOS / Linux
./mvnw spring-boot:run
```
- API Base URL: `http://localhost:8080`
- Health check: `http://localhost:8080/api/status`
- H2 Console: `http://localhost:8080/h2-console` (User: `sa`, Password: empty)

### 2. Run the Frontend
```bash
cd frontend
npm install
npm run dev
```
- Web Application: `http://localhost:5173`

---

## API Endpoints

| Area | Endpoints |
| :--- | :--- |
| **Auth** | `POST /api/auth/register`, `POST /api/auth/login` |
| **Donations** | `/api/donations`, `/api/donation-types`, `/api/donors` |
| **Events** | `/api/events`, `/api/event-categories`, `/api/tickets`, `/api/event-registrations` |
| **Volunteers** | `/api/volunteers`, `/api/volunteers/roles`, `/api/volunteers/qualifications`, `/api/volunteer-assignments` |
| **Beneficiaries & Resources** | `/api/beneficiaries`, `/api/beneficiary-categories`, `/api/resources`, `/api/resource-categories` |
| **System** | `GET /api/status` |

---

## Documentation & Diagrams

- **PlantUML**: [`nonprofit-class-diagram.puml`](file:///e:/ASU/Fall%2026/Desing%20Patterns/Project/nonprofit-donatelo-project/nonprofit-class-diagram.puml)

