# Car Sharing App

Car Sharing Service Management System is a modern web-based solution designed to automate the car rental process. Instead of outdated methods like cash payments or manual booking tracking, the system offers a convenient and secure digital experience for both users and administrators.

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

### Model Diagram

<img width="1278" height="820" alt="image" src="https://github.com/user-attachments/assets/2766876c-25d8-4b60-b204-15310bc7bc95" />

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

- **Java** 17 – Programming language
- **Spring Boot** 3.2.0 - Backend framework for building the core application
- **Spring Security** 6.2.x - For managing authentication and authorization
- **Spring Data JPA** 3.2.x - For interacting with the database
- **PostgreSQL** 15 - Database to store book and user information
- **Docker** - Containerization for easy deployment
- **Swagger** 2.5.0 - API documentation and testing
- **MapStruct** 1.5.5 Final - Object mapping
- **Liquibase** 4.x - Database change management
- **JUnit & Mockito** 5 / 5.18.0 - For unit testing
- **Testcontainers** 1.20.1 - For integration testing with real databases
- **Stripe API** 24.18.0 - Manages secure payment processing for rentals.
- **Telegram API** 6.9.7.1 - Sends real-time notifications via a Telegram bot.

## Requirements

  * **Java** Development Kit (JDK) version 17 or higher
  * **Maven** for dependency management
  * **Docker Desktop** for containerized environment setup

## How to Set Up and Run Locally

Follow these steps to set up and run the application on your local machine. The application is containerized using Docker, making it easy to deploy and manage.

1.  **Clone the Repository**:

       ```bash
    git clone git@github.com:keep-gooing/car-sharing-app.git
    cd car-sharing-app
    ```

3.  **Create `.env` file**: Create a `.env` file in the root directory of the project (same location as `docker-compose.yml`) by filling in the necessary environment variables.

     ```
      POSTGRES_USER=<your_postgres_user>
      POSTGRES_PASSWORD=<your_postgres_password>
      POSTGRES_DATABASE=<your_database_name>
      POSTGRES_LOCAL_PORT=<your_local_port>
      POSTGRES_DOCKER_PORT=<your_docker_port>
     
      SPRING_LOCAL_PORT=<your_spring_local_port>
      SPRING_DOCKER_PORT=<your_spring_docker_port>
      DEBUG_PORT=<your_debug_port>

      JWT_EXPIRATION=<your_jwt_expiration_in_milliseconds>
      JWT_SECRET=<your_jwt_secret_base64_encoded>

      TELEGRAM_BOT_TOKEN=<your_telegram_bot_token>

      STRIPE_PUBLIC_KEY=<your_stripe_public_key>
      STRIPE_SECRET_KEY=<your_stripe_secret_key>

      PAYMENT_SUCCESS_URL=<your_payment_success_url>
      PAYMENT_CANCEL_URL=<your_payment_cancel_url>
    ```

4.  **Build the Application**: Use Maven to build the application. This will compile the code and package it into a JAR file.

    ```bash
    mvn clean package
    ```

5.  **Start Docker Containers**: Ensure Docker Desktop is running on your system, then execute the following command in the project's root directory to build images and start the application along with its dependencies (PostgreSQL database).

    ```bash
    docker-compose up --build
    ```

6.  **Access the Application**: The application will be accessible at `http://localhost:8082/api`.

7.  **Stop the Application**: To stop the application and its containers, use:

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
    `http://localhost:8082/api/swagger-ui/index.html#/`

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
