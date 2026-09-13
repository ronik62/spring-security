Day 1 — Spring Security Fundamentals

Goal

Understand the fundamentals of Spring Security and how Spring Security protects an incoming HTTP request before it reaches the controller.

By the end of Day 1, I should understand:

What Spring Security is

Authentication

Authorization

SecurityFilterChain

SecurityContext

SecurityContextHolder

Authentication

Principal

Credentials

UserDetails

UserDetailsService

PasswordEncoder

BCryptPasswordEncoder

AuthenticationManager

AuthenticationProvider

Roles

Authorities

HTTP Basic Authentication

401 vs 403

Basic authentication flow

What is Spring Security?

Spring Security is a framework used to secure Spring applications.

It provides features such as:

Authentication

Authorization

Password security

Security filters

Roles and authorities

Session security

Protection against common web security vulnerabilities

Basic flow:

Client → Spring Security → Controller → Response

Spring Security processes the incoming request before it reaches the controller.

Authentication

Authentication answers:

Who are you?

Authentication is the process of verifying the identity of a user.

Example:

Username: Ronik
Password: ronik123

Spring Security verifies the provided credentials.

If they are valid, the user becomes authenticated.

Authorization

Authorization answers:

What are you allowed to do?

After authentication, Spring Security checks whether the authenticated user has permission to access a particular resource.

Example:

Ronik → USER
Admin → ADMIN

If /admin requires the ADMIN role:

Ronik → Authenticated → USER → 403 Forbidden

Admin → Authenticated → ADMIN → 200 OK

Authentication vs Authorization

Authentication → Who are you?

Authorization → What can you do?

Authentication happens before authorization.

SecurityFilterChain

SecurityFilterChain defines how Spring Security processes incoming HTTP requests.

It contains a chain of security filters that process requests before they reach the application.

Conceptually:

HTTP Request
↓
SecurityFilterChain
↓
Security Filters
↓
Authentication
↓
Authorization
↓
Controller
↓
Response

Example:

@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http)
        throws Exception {

    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/public").permitAll()
            .requestMatchers("/hello").authenticated()
            .requestMatchers("/admin").hasRole("ADMIN")
        )
        .httpBasic(t -> {});

    return http.build();
}

Our endpoint rules are:

/public → Anyone can access

/hello → Authentication required

/admin → ADMIN role required

Security Filters

Security filters intercept incoming HTTP requests before they reach the controller.

They can perform different security-related tasks such as:

Authentication

Authorization

Security context handling

CSRF protection

Request security checks

Conceptually:

HTTP Request
↓
Security Filters
↓
Security Decision
↓
Controller

If a security check fails, the request may not reach the controller.

SecurityContext

SecurityContext contains security information for the current security context.

The most important information inside it is the current Authentication.

Conceptually:

SecurityContext
↓
Authentication
↓
Principal
Authorities
Credentials
Authentication status

Example:

SecurityContext
↓
Authentication
↓
Principal → Ronik
Authorities → ROLE_USER

The way the SecurityContext is maintained across requests depends on the application's security architecture.

For example:

Session-based authentication
→ Security context can be associated with the session

JWT/stateless authentication
→ Authentication is generally rebuilt for each request

SecurityContextHolder

SecurityContextHolder provides access to the current SecurityContext.

Conceptually:

SecurityContextHolder
↓
SecurityContext
↓
Authentication

It allows Spring Security and application code to access the current authenticated identity.

Authentication Object

Authentication represents authentication information or the result of authentication.

It can contain:

Principal

Credentials

Authorities

Authentication status

Example:

Authentication
├── Principal → Ronik
├── Credentials → password/token information
├── Authorities → ROLE_USER
└── Authenticated → true

Do not confuse the general concept of authentication with the Spring Security Authentication object.

Authentication as a security concept → Verifying who you are

Authentication as a Spring Security object → Represents authentication information/result

Principal

The principal represents the currently authenticated identity.

Example:

Principal → Ronik

A principal does not necessarily have to be a human user. It represents the identity that has been authenticated.

Credentials

Credentials are proof of identity.

Examples:

Password

JWT

API key

Certificate

For username/password authentication:

Username → Ronik
Password → ronik123

The password is a credential.

UserDetailsService

UserDetailsService is responsible for loading user information required by Spring Security.

Its main method is:

loadUserByUsername()

Conceptually:

Username
↓
UserDetailsService
↓
UserDetails
↓
Username
Password
Authorities
Account status

Important:

UserDetailsService does not primarily verify the password.

Its job is to load the user's security information.

UserDetails

UserDetails represents the user information required by Spring Security.

It can contain:

Username

Password

Authorities

Account status

Account expiration

Credentials expiration

Account locked status

Enabled status

Conceptually:

UserDetails
├── username
├── password
├── authorities
├── accountNonExpired
├── accountNonLocked
├── credentialsNonExpired
└── enabled

PasswordEncoder

PasswordEncoder is used to:

Encode passwords

Verify submitted passwords against stored encoded passwords

Passwords should never be stored as plain text.

Bad:

ronik123

Better:

Raw password
↓
PasswordEncoder
↓
Encoded password
↓
Database / Storage

Example:

@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

BCryptPasswordEncoder

BCryptPasswordEncoder is a password encoder implementation commonly used for password hashing.

Example:

.password(passwordEncoder().encode("ronik123"))

The raw password is encoded before being stored.

During authentication:

Submitted password
↓
PasswordEncoder
↓
Verify against stored encoded password
↓
Match / No match

We do not decode the BCrypt password back into the original password.

AuthenticationManager

AuthenticationManager is responsible for processing authentication requests.

It coordinates authentication by delegating the request to an appropriate AuthenticationProvider.

Conceptually:

Authentication Request
↓
AuthenticationManager
↓
AuthenticationProvider
↓
Authentication Result

Think of it as:

AuthenticationManager → Coordinates the authentication process

AuthenticationProvider

AuthenticationProvider performs a specific type of authentication.

For username/password authentication, it can:

Receive username and password

Load the user using UserDetailsService

Verify the password using PasswordEncoder

Check account status

Return an authenticated Authentication

Conceptually:

Username + Password
↓
AuthenticationManager
↓
AuthenticationProvider
↓
UserDetailsService
↓
UserDetails
↓
PasswordEncoder
↓
Authentication Result

AuthenticationManager vs AuthenticationProvider

AuthenticationManager coordinates authentication.

AuthenticationProvider performs a particular authentication mechanism.

Conceptually:

AuthenticationManager
↓
AuthenticationProvider
↓
Authentication Logic

Roles

A role represents a broad category of access.

Examples:

USER

ADMIN

MANAGER

SUPPORT

When creating a user:

.roles("USER")

Spring Security represents this role internally as:

ROLE_USER

Similarly:

.roles("ADMIN")

becomes:

ROLE_ADMIN

Authorities

Authorities represent permissions or access granted to an authenticated user.

Examples:

TICKET_READ

TICKET_CREATE

TICKET_UPDATE

TICKET_DELETE

A user can have multiple authorities.

Example:

Ronik
├── ROLE_USER
├── TICKET_READ
└── TICKET_CREATE

Roles are also represented as authorities internally.

For example:

ROLE_ADMIN is an authority representing the ADMIN role.

Role vs Authority

Role → Broad access category

Authority → Specific permission

Example:

ADMIN
↓
ROLE_ADMIN
↓
Broad access

TICKET_DELETE
↓
Specific permission

In real applications, both roles and fine-grained permissions can be used.

hasRole()

Example:

.requestMatchers("/admin").hasRole("ADMIN")

This checks for:

ROLE_ADMIN

Normally we write:

hasRole("ADMIN")

instead of:

hasRole("ROLE_ADMIN")

because role-based checks handle the ROLE_ prefix.

permitAll()

Example:

.requestMatchers("/public").permitAll()

This means authentication is not required for that endpoint.

Flow:

GET /public
↓
permitAll()
↓
Controller
↓
200 OK

authenticated()

Example:

.requestMatchers("/hello").authenticated()

This means the request must be authenticated.

The user's specific role does not matter.

Example:

Ronik → USER → Authenticated → Allowed

Admin → ADMIN → Authenticated → Allowed

401 Unauthorized

A 401 Unauthorized response generally means authentication is required or the provided authentication is not valid.

Example:

Unauthenticated user
↓
GET /hello
↓
401 Unauthorized

Remember:

401 → Authentication problem

403 Forbidden

A 403 Forbidden response generally means the user is authenticated but does not have sufficient permission.

Example:

Ronik
↓
Authenticated
↓
ROLE_USER
↓
GET /admin
↓
ROLE_ADMIN required
↓
403 Forbidden

Remember:

403 → Authenticated but not allowed

Easy way to remember:

401 → Who are you?

403 → I know who you are, but you cannot do this.

HTTP Basic Authentication

Our Day 1 project uses HTTP Basic authentication.

The client sends credentials with the HTTP request.

Conceptually:

Browser / Postman
↓
Username + Password
↓
Spring Security
↓
Authentication

HTTP Basic authentication is different from traditional session-based login.

A browser may cache Basic credentials.

Because of this, switching between users in the same browser can sometimes be confusing.

For testing different users, use:

Incognito/private window

Postman

Another HTTP client

Complete Authentication Flow

For username/password authentication, understand this conceptual flow:

Client
↓
SecurityFilterChain
↓
AuthenticationManager
↓
AuthenticationProvider
↓
UserDetailsService
↓
UserDetails
↓
PasswordEncoder
↓
Authentication
↓
SecurityContext
↓
Authorization
↓
Controller

More detailed:

Username + Password
↓
Security Filter
↓
AuthenticationManager
↓
AuthenticationProvider
↓
UserDetailsService
↓
Load UserDetails
↓
PasswordEncoder
↓
Verify Password
↓
Create authenticated Authentication
↓
SecurityContext
↓
Authorization
↓
Controller

Example: Ronik Accessing /admin

Ronik
↓
GET /admin
↓
SecurityFilterChain
↓
Authentication
↓
AuthenticationManager
↓
AuthenticationProvider
↓
UserDetailsService
↓
Loads Ronik's UserDetails
↓
PasswordEncoder verifies password
↓
Authentication successful
↓
SecurityContext contains Authentication
↓
Authorization check
↓
/admin requires ROLE_ADMIN
↓
Ronik has ROLE_USER
↓
Access denied
↓
403 Forbidden

Important:

Authentication succeeds.

Authorization fails.

Therefore:

403 Forbidden

Example: Admin Accessing /admin

Admin
↓
GET /admin
↓
SecurityFilterChain
↓
Authentication
↓
AuthenticationManager
↓
AuthenticationProvider
↓
UserDetailsService
↓
Loads admin UserDetails
↓
PasswordEncoder verifies password
↓
Authentication successful
↓
SecurityContext contains Authentication
↓
Authorization check
↓
/admin requires ROLE_ADMIN
↓
Admin has ROLE_ADMIN
↓
Access granted
↓
Controller
↓
200 OK

Day 1 Project — Secure Hello

The project contains:

/public

/hello

/admin

Users:

Ronik
Username: Ronik
Role: USER

Admin
Username: admin
Role: ADMIN

Security rules:

/public → permitAll()

/hello → authenticated()

/admin → hasRole("ADMIN")

Expected Endpoint Behavior

User

/public

/hello

/admin

Unauthenticated

200

401

401

Ronik (USER)

200

200

403

admin (ADMIN)

200

200

200

Day 1 Practical Work

Created Spring Boot project

Added Spring Security

Created /public endpoint

Created /hello endpoint

Created /admin endpoint

Configured SecurityFilterChain

Configured HTTP Basic authentication

Created in-memory users

Added USER role

Added ADMIN role

Added BCryptPasswordEncoder

Tested public endpoint

Tested authenticated endpoint

Tested ADMIN endpoint

Tested USER accessing ADMIN endpoint

Verified 403 Forbidden

Verified authentication-required endpoints

Understood authentication flow

Understood authorization flow

Key Concepts

Spring Security
→ Secures the Spring application

SecurityFilterChain
→ Processes requests through security filters

Authentication
→ Verifies identity

Authorization
→ Checks access

AuthenticationManager
→ Coordinates authentication

AuthenticationProvider
→ Performs a specific authentication mechanism

UserDetailsService
→ Loads user information

UserDetails
→ Represents user security information

PasswordEncoder
→ Encodes and verifies passwords

SecurityContext
→ Holds current security information

SecurityContextHolder
→ Provides access to SecurityContext

Principal
→ Current authenticated identity

Credentials
→ Proof of identity

Role
→ Broad access category

Authority
→ Permission/access

Core Mental Model

HTTP REQUEST
↓
SecurityFilterChain
↓
Authentication
↓
Authorization
↓
Controller

Authentication:

Who are you?

Authorization:

What can you do?

Authentication Architecture

Authentication Request
↓
AuthenticationManager
↓
AuthenticationProvider
↓
UserDetailsService
↓
UserDetails
↓
PasswordEncoder
↓
Authentication
↓
SecurityContext

This flow will become important when learning:

Database authentication

JWT

Custom authentication

Custom AuthenticationProvider

Custom filters

OAuth2

Resource Server

Day 1 Interview Questions

What is Spring Security?

Spring Security is a framework used to secure Spring applications by providing authentication, authorization, password security and other security mechanisms.

What is authentication?

Authentication is the process of verifying the identity of a user or security principal.

What is authorization?

Authorization determines what an authenticated user is allowed to access or perform.

What is SecurityFilterChain?

It is a configured chain of Spring Security filters that processes incoming HTTP requests and applies security rules before requests reach the application.

What is SecurityContext?

It contains security information for the current security context, especially the current Authentication.

What is SecurityContextHolder?

It provides access to the current SecurityContext.

What is Authentication?

It represents authentication information/result and can contain the principal, credentials, authorities and authentication status.

What is Principal?

It represents the currently authenticated identity.

What is UserDetailsService?

It loads user-specific security information using the username.

Does UserDetailsService verify passwords?

No. It loads the user's security information. Password verification is performed during authentication using a PasswordEncoder.

What is UserDetails?

It represents the user information required by Spring Security, such as username, password, authorities and account status.

What is PasswordEncoder?

It encodes passwords and verifies submitted passwords against stored encoded passwords.

What is AuthenticationManager?

It coordinates authentication by delegating authentication requests to an appropriate AuthenticationProvider.

What is AuthenticationProvider?

It performs a specific authentication mechanism and returns an authenticated Authentication when successful.

What is a role?

A role is a broad category of access such as USER or ADMIN.

What is an authority?

An authority represents a permission or access granted to an authenticated user.

What does hasRole("ADMIN") check?

It checks whether the user has the ROLE_ADMIN authority.

What is the difference between 401 and 403?

401 means authentication is required or failed.

403 means the user is authenticated but does not have sufficient permission.

Day 1 Final Revision

If I remember only one flow, it should be:

Request
↓
SecurityFilterChain
↓
Authentication
↓
AuthenticationManager
↓
AuthenticationProvider
↓
UserDetailsService
↓
UserDetails
↓
PasswordEncoder
↓
Authentication
↓
SecurityContext
↓
Authorization
↓
Controller

The three most important questions:

Authentication → Who are you?

Authorization → What can you do?

PasswordEncoder → How do we securely store and verify passwords?

Day 1 Status

Spring Security fundamentals

Authentication

Authorization

SecurityFilterChain

Security Filters

SecurityContext

SecurityContextHolder

Authentication

Principal

Credentials

UserDetailsService

UserDetails

PasswordEncoder

BCryptPasswordEncoder

AuthenticationManager

AuthenticationProvider

Roles

Authorities

HTTP Basic Authentication

401 vs 403

Practical project

Testing

GitHub documentation