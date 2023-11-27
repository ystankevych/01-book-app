# BOOK-APP - Spring Boot Rest Application


### Table of contents
* [ Project Overview <a name="project-overview"></a>](#project-overview-a-nameproject-overviewa)
* [ Technologies used](#technologies-used)
* [ Models and relations](#models-and-relations)
* [ Project structure](#project-structure)
* [ Getting Started ](#getting-started)


### Project Overview <a name=project-overview></a>
Book-app - is a REST application that supports JWT-based authentication,
registration and provides platform for both administrator and customers to
interact with books, shopping-carts, orders. Project was developed according to SOLID principles
follows a Three-Tier Architecture:
1. Presentation layer (Controllers): Accepts requests from clients and sends results back to them.
2. Business logic layer (Services): Provides logic to operate on the data sent to and from the DAO and the client.
3. Data access layer (DAO): Represents a bridge between the database and the application.

### Technologies used
* Java  ```v.17```
* Spring Boot ```v.3.1.4``` including:
  + Spring Boot Data Jpa
  + Spring Boot Security
  + Spring Boot Web
* Hibernate ```v.6.2.9.Final```
* MySql ```v.8.0.33```
* Lombok
* Mapstruct
* Liquibase
* Swagger
* Docker

### Models and relations
<div align="center"><a href="https://i.ibb.co/N6ntxJR/image.png"><img src="https://i.ibb.co/7JNjvZh/image.png" alt="demo-app-screen-readme-git" border="0" /></a></div>

### Project structure:
* **config**: classes providing application configuration
* **controller** - rest controllers with following API endpoints
  * **AuthenticationController**:
    * **POST** ```/api/auth/registration``` - new user registration, access none
    * **POST** ```api/auth/login``` - get JWT-token for authentication, access ADMIN / USER
  * **BookController**:
    * **POST** ```/api/books``` - add new book to the DB, access ADMIN
    * **PUT** ```/api/books/{id}``` - update book with the new value, access ADMIN
    * **GET** ```/api/books``` get all books from the DB, access ADMIN/USER
    * **GET** ```/api/books/{id}``` get book by id, access ADMIN/USER
    * **DELETE** ```/api/books/{id}``` delete book by its id, access ADMIN
    * **GET** ```/api/books/search``` search books, access ADMIN/USER
  * **CategoryController**:
    * **POST** ```/api/categories``` add new book category to the DB, access ADMIN
      * **PUT** ```/api/categories``` update category, access ADMIN
      * **GET** ```/api/categories``` get list of all categories, access ADMIN/USER
      * **GET** ```/api/categories/{id}``` get category by its id, access ADMIN/USER
      * **DELETE** ```/api/categories/{id}``` delete category by its id, access ADMIN
      * **GET** ```/api/categories/{id}/books``` get list of books by category id, access ADMIN/USER
  * **OrderController**:
    * **POST** ```/api/orders``` create new order, access USER
    * **GET** ```/api/orders``` get order history of a logged-in user, access USER
    * **PATCH** ```/api/orders/{id}``` update order status by its id, access ADMIN
    * **GET** ```/api/orders/{orderId}/items``` get list of order items of a logged-in user, access USER
    * **GET** ```/api/orders/{orderId}/items/{itemId}``` get a specific order item from the order of a logged-in user, access USER
  * **ShoppingCartController**:
    * **GET** ```/api/cart``` get the shopping cart of a logged-in user, access USER
    * **POST** ```/api/cart``` add a new book/update the quantity of books in the cart of a logged-in user, access USER
    * **PUT** ```/api/cart/cart-items/{cartItemId}``` update the number of books in the cart of a logged-in user, access USER
    * **DELETE** ```/api/cart/cart-items/{cartItemId}``` delete book from the cart of a logged-in user, access USER
* **dto**: Data transfer objects
* **exception**: custom exceptions
* **mapper**: classes that generates mapping code based on annotations, reducing the need for manual, error-prone mapping code
* **model**: all data models
* **repository**: specific implementations of Jpa repositories
* **security**: security services providing user authentication and JWT token generation and validation
* **service**: services that provide all the business logic of the application
* **validation** custom annotations for validation

### Getting started
* Clone the [**repository**](https://github.com/ystankevych/book-app)
* Create a `.env` file with the necessary environment variables. (See `.env-sample` for a sample.)
* Run the following command to build and start the Docker containers:
  `docker-compose up --build`.
* The application should now be running at `http://localhost:8088`.