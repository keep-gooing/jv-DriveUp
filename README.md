# Car Sharing App

## Project Overview

Welcome to the **Car Sharing Service Management System**. This comprehensive application modernizes the traditional car rental process by providing a feature-rich, user-friendly digital solution. It addresses common challenges such as untracked rentals, cash-only payments, and limited visibility into car availability, significantly enhancing the experience for both customers and administrators.

The system is designed to streamline the process of finding, renting, and managing cars online. Users can effortlessly browse available vehicles, initiate rental transactions, manage their current and past rentals, and securely process payments. Administrators, on the other hand, gain robust tools for managing car inventory, overseeing all rentals and payments, and receiving real-time notifications.

## Goals

  * **Streamline** car, rental, and payment tracking processes.
  * **Enable** users to check car availability and book rentals online.
  * **Support** digital payments through Stripe, eliminating cash-only dependency.
  * **Notify** customers of overdue rentals and other key updates.
  * **Provide** detailed records for rentals, users, cars, and payments for better management.

## Architecture

The system is built on a robust, scalable architecture designed for high performance and a smooth user experience. It leverages a layered structure with a dedicated backend API, a relational database, and integrations with third-party services for payments and notifications.

## Domain Models

  * **User**: Represents the core entity for both customers and managers. Stores user profile data like email, name, and authentication details. Facilitates secure access to the platform and interactions with rentals, payments, and notifications.
  * **Role**: Defines the user's authorization level within the system, providing clear separation of permissions for Managers and Customers.
  * **Car**: The central asset of the car-sharing service, representing the vehicles available for rent. Provides detailed information about available cars, their types, pricing, and inventory status.
  * **Rental**: Represents a booking made by a user to rent a car for a specified duration. Tracks the start and end dates of rentals, as well as whether the car was returned on time.
  * **Payment**: Handles financial transactions for rentals and overdue fines. Integrates with the Stripe API for secure payment processing.

### UML ER Diagram

```
+----------------+       +-----------------+       +------------------+
|      Car       |<----+ |    Rental     |       |     Payment      |
+----------------+       +-----------------+       +------------------+
| id             |<---+ | id              |       | id               |
| model          |       | rentalDate      |       | status           |
| brand          |       | returnDate      |       | type             |
| type           |       | actualReturnDate|       | sessionUrl       |
| inventory      |       | car_id          | ----> | sessionId        |
| dailyFee       |       | user_id         | ----> | amount           |
| isDeleted      |       | payment_id      | <---- | rental_id        |
+----------------+       +-----------------+       +------------------+
            ^                        ^                         |
            |                        |                         |
            +------------------------+-------------------------+
            |
            |
       +----------------+
       |      User      |
       +----------------+
       | id             |
       | email          |
       | firstName      |
       | lastName       |
       | password       |
       | tgChatId |
       | isDeleted      |
       +----------------+
            |
            v
       +---------------+
       |     Role      |
       +---------------+
       | id            |
       | role          |
       +---------------+
```

## Core Functionalities

### For Users

  * **Register and Login**: New users can register, log in, and begin renting cars.
  * **Browse Cars**: Users can view available cars and their details.
  * **Rent a Car**: Users can initiate rental transactions.
  * **View Rentals**: Users can check active and past rentals.
  * **Return Cars**: Users return rented cars, updating rental status.
  * **Payments**: Secure payment processing for rentals and fines.

### For Administrators

  * **Manage Inventory**: Managers can add, update, or delete cars.
  * **View Rentals and Payments**: Access to all rental and payment history.
  * **Telegram Notifications**: Managers receive real-time updates about new rentals, overdue rentals, and successful payments.

## Technology Stack

This project uses modern technologies to ensure high performance and a smooth user experience:

  * **Spring Boot**: Backend framework for building the core application. 
  * **Spring Security**: For managing authentication and authorization.
  * **Spring Data JPA**: For interacting with the PostgreSQL database.
  * **PostgreSQL**: Relational database for efficient data storage.
  * **Docker**: For containerization and simplified deployment.
  * **Swagger**: API documentation and interactive testing.
  * **MapStruct**: Automated object mapping.
  * **Liquibase**: Database change management and version control.
  * **JUnit & Mockito**: For robust unit and integration testing.
  * **Maven**: Builds and manages project dependencies.
  * **Stripe API**: Manages secure payment processing for rentals.
  * **Telegram API**: Sends real-time notifications via a Telegram bot.

## Requirements

  * **Java** Development Kit (JDK) version 17 or higher
  * **Maven** for dependency management
  * **Docker Desktop** for containerized environment setup

## How to Set Up and Run Locally

Follow these steps to set up and run the application on your local machine. The application is containerized using Docker, making it easy to deploy and manage.

1.  **Clone the Repository**:

    ```bash
    git clone https://github.com/your-username/car-sharing-app.git
    cd car-sharing-app
    ```

2.  **Create `.env` file**: Create a `.env` file in the root directory of the project (same location as `docker-compose.yml`) by filling in the necessary environment variables.

    ```
    STRIPE_SECRET_KEY=
    TELEGRAM_BOT_TOKEN=
    TELEGRAM_BOT_NAME=
    JWT_SECRET=
    JWT_EXPIRATION=
    POSTGRES_DATABASE=carsharing
    POSTGRES_USER=postgres
    POSTGRES_PASSWORD=12345678pLzola.
    POSTGRES_LOCAL_PORT=5436
    POSTGRES_DOCKER_PORT=5432
    SPRING_LOCAL_PORT=8082
    SPRING_DOCKER_PORT=8080
    DEBUG_PORT=5007
    DOMAIN=
    ```

3.  **Build the Application**: Use Maven to build the application. This will compile the code and package it into a JAR file.

    ```bash
    mvn clean package
    ```

4.  **Start Docker Containers**: Ensure Docker Desktop is running on your system, then execute the following command in the project's root directory to build images and start the application along with its dependencies (PostgreSQL database).

    ```bash
    docker-compose up --build
    ```

5.  **Access the Application**: The application will be accessible at `http://localhost:8082/api`.

6.  **Stop the Application**: To stop the application and its containers, use:

    ```bash
    docker-compose down
    ```

### Additional Notes

  * **Database Configuration**: The application uses Liquibase for database version control. Ensure the database container is started and healthy as defined in `docker-compose.yml`.
  * **Environment Variables**: All environment-specific configurations (like database credentials, API keys, port numbers) are managed through the `.env` file.

## Testing

1.  **Run Tests**: You can run automated tests to check if everything is working as expected by executing:
    ```bash
    mvn clean test
    ```
2.  **API Documentation (Swagger)**: For detailed API documentation and to test endpoints directly, visit:
    `http://localhost:8082/api/swagger-ui/index.html`

## Postman Collection

A Postman collection with pre-configured API requests is provided to help you easily test the application's functionalities.

### How to use it

1.  **Locate the collection**: The Postman collection is located in the `postman` folder of the project.
      * File path: `./postman/Car sharing.postman_collection.json`
2.  **Import the collection**: In Postman, use **`File -> Import`** or press **`Ctrl + O`** to import the the JSON file.
3.  **Set Base URL**: Ensure to set `http://localhost:8082/api` as the base URL for all requests within the collection.

-----

## Challenges Faced

During the development of this project, specific challenges were addressed:

1.  **Proper Integration Testing**: Several SQL scripts were created to emulate a realistic database state for integration testing. This ensures tests remain independent and unaffected by execution order.
2.  **Database Change Management**: Liquibase was utilized to simplify database schema changes and migrations, ensuring reliability and easy management of all changesets.

This car-sharing project provided a wealth of experience, fundamentally changing the understanding of building applications. It involved familiarity with a wide range of libraries and frameworks, gaining a clear understanding of their purposes, and applying them effectively. Additionally, it included integrating and utilizing third-party APIs to enhance the application's functionality.
