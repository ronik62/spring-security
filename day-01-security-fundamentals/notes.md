🔐 Day 1 — Spring Security Fundamentals

Goal: Understand how Spring Security protects a Spring Boot application, how a user gets authenticated, and how Spring Security decides whether that user is allowed to access an endpoint.

🧠 The Big Picture

Whenever a client sends a request to our application, we don't want every request to directly reach the controller.

Instead:

Client
  │
  │ HTTP Request
  ▼
Spring Security
  │
  ├── 🔎 Authentication
  │      "Who are you?"
  │
  ├── 🛂 Authorization
  │      "Are you allowed to do this?"
  │
  ▼
Controller
  │
  ▼
Response

This is the foundation of Spring Security.

1. 🔐 What is Spring Security?

Spring Security is a framework used to secure Spring applications.

It helps us implement:

Authentication

Authorization

Password security

Roles and permissions

Security filters

Session security

Protection against common web security vulnerabilities

The simplest way to think about it:

Spring Security acts as a security layer between the client and our application.

2. 👤 Authentication — "Who are you?"

Authentication means verifying the identity of a user.

For example, a user sends:

Username: Ronik
Password: ronik123

Spring Security checks whether those credentials are valid.

If they are valid:

User
  ↓
Credentials verified
  ↓
Authenticated

Real-world example

Think about entering your username and password when logging into an application.

The application first needs to answer:

"Is this really Ronik?"

That is Authentication.

Remember

Authentication = Who are you?

3. 🛂 Authorization — "What can you do?"

Once we know who the user is, we need to decide what that user is allowed to access.

For example:

Ronik → USER
Admin → ADMIN

Suppose /admin is only available to administrators.

Ronik
  ↓
Authenticated
  ↓
USER
  ↓
/admin requires ADMIN
  ↓
❌ Access denied

Admin:

Admin
  ↓
Authenticated
  ↓
ADMIN
  ↓
/admin requires ADMIN
  ↓
✅ Access granted

Remember

Authorization = What can you do?

4. 🔥 Authentication vs Authorization

This is one of the most important concepts.

Authentication

Authorization

Who are you?

What can you do?

Verifies identity

Checks permissions

Happens first

Happens after authentication

Example: username/password

Example: USER/ADMIN access

Easy example

Imagine entering an office.

Step 1:
Security guard checks your ID.
        ↓
Authentication

Step 2:
Guard checks which rooms you are allowed to enter.
        ↓
Authorization

5. 🧱 SecurityFilterChain

SecurityFilterChain is one of the most important parts of Spring Security.

It defines how incoming HTTP requests should be secured.

A request passes through a chain of security filters before reaching our controller.

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

Example from this project:

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

Our security rules are:

/public
→ Anyone can access

/hello
→ Authentication required

/admin
→ ADMIN role required

Remember

SecurityFilterChain is where we configure how Spring Security protects HTTP requests.

6. 🔎 Security Filters

A filter can inspect and process an incoming request before the request reaches the controller.

Spring Security uses many filters for different security responsibilities.

Conceptually:

Request
   ↓
Filter 1
   ↓
Filter 2
   ↓
Filter 3
   ↓
...
   ↓
Security Decision
   ↓
Controller

Depending on the request and configuration, filters can handle things such as:

Authentication

Authorization

Security context

CSRF protection

Other security checks

Important

If a security check rejects the request, the controller may never be called.

7. 🪪 Principal

A Principal represents the identity of the currently authenticated user.

Example:

Principal → Ronik

Think of it as:

"Who is currently authenticated?"

8. 🎫 Credentials

Credentials are proof of identity.

Examples:

Password

JWT

API key

Certificate

For our Day 1 project:

Username → Ronik
Password → ronik123

The password is a credential.

9. 🧾 Authentication Object

Spring Security represents authentication information using an Authentication object.

It can contain information such as:

Principal

Credentials

Authorities

Authentication status

Conceptually:

Authentication
├── Principal → Ronik
├── Credentials → password/token information
├── Authorities → ROLE_USER
└── Authenticated → true

Important distinction

Authentication as a concept:

"Who are you?"

Authentication as a Spring Security object:

An object containing information about the authentication.

10. 🗃️ SecurityContext

SecurityContext contains security information for the current security context.

Most importantly, it contains the current Authentication.

SecurityContext
      ↓
Authentication
      ↓
Principal
Authorities
Credentials

For example:

SecurityContext
      ↓
Authentication
      ↓
Principal → Ronik
Authorities → ROLE_USER

Think of the SecurityContext as the place where Spring Security keeps the current authentication information available to the application.

Important

How the SecurityContext is maintained across requests depends on the security architecture.

For example:

Session-based authentication
→ Security context can be associated with the session

JWT/stateless authentication
→ Authentication is generally rebuilt for each request

11. 📍 SecurityContextHolder

SecurityContextHolder provides access to the current SecurityContext.

Conceptually:

SecurityContextHolder
        ↓
SecurityContext
        ↓
Authentication
        ↓
Current user

This allows Spring Security and application code to access the current authenticated identity.

12. 👤 UserDetails

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

Think of UserDetails as:

The security-related information Spring Security needs about a user.

13. 🔎 UserDetailsService

UserDetailsService is responsible for loading a user's security information.

Its important method is:

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

For example:

"Ronik"
   ↓
UserDetailsService
   ↓
Ronik's UserDetails

Important

UserDetailsService primarily loads user information.

It does not itself perform the password comparison.

14. 🔑 PasswordEncoder

PasswordEncoder is used to:

Encode passwords

Verify submitted passwords against stored encoded passwords

We should never store passwords as plain text.

Bad:

ronik123

Instead:

Raw Password
     ↓
PasswordEncoder
     ↓
Encoded Password
     ↓
Storage

Example:

@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

15. 🛡️ BCryptPasswordEncoder

BCryptPasswordEncoder is a password encoder implementation commonly used for password hashing.

When creating a user:

.password(passwordEncoder().encode("ronik123"))

The raw password is encoded before being stored.

During authentication:

User enters password
       ↓
PasswordEncoder
       ↓
Verify against stored encoded password
       ↓
Match / No match

We do not decode a BCrypt password back into the original password.

Important

Passwords should be stored as secure password hashes, not plain text.

16. 🎯 AuthenticationManager

AuthenticationManager coordinates the authentication process.

Conceptually:

Authentication Request
        ↓
AuthenticationManager
        ↓
AuthenticationProvider
        ↓
Authentication Result

Think of it as the component that says:

"I need to authenticate this request. Which provider should handle it?"

17. ⚙️ AuthenticationProvider

AuthenticationProvider performs a particular type of authentication.

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

18. 🔄 AuthenticationManager vs AuthenticationProvider

This distinction is important.

AuthenticationManager

Coordinates authentication.

"Who should handle this authentication request?"

AuthenticationProvider

Performs a particular authentication mechanism.

"I know how to authenticate this type of request."

Simple mental model:

AuthenticationManager
        ↓
AuthenticationProvider
        ↓
Authentication Logic

19. 👥 Roles

A role represents a broad category of access.

Examples:

USER
ADMIN
MANAGER
SUPPORT

When creating a user:

.roles("USER")

Spring Security represents the role as:

ROLE_USER

Similarly:

.roles("ADMIN")

becomes:

ROLE_ADMIN

20. 🔐 Authorities

Authorities represent permissions or access granted to a user.

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

Important:

Roles are also represented as authorities internally.

For example:

ROLE_ADMIN

is an authority representing the ADMIN role.

21. 🎭 Role vs Authority

Think of it this way:

Role
→ Broad access category

Authority
→ Specific permission

Example:

ADMIN
  ↓
ROLE_ADMIN
  ↓
Broad access

TICKET_DELETE
  ↓
Specific permission

A real application may use roles, authorities, or both.

22. ✅ permitAll()

permitAll() means authentication is not required for the specified endpoint.

Example:

.requestMatchers("/public").permitAll()

Flow:

GET /public
     ↓
permitAll()
     ↓
Controller
     ↓
200 OK

Anyone can access /public.

23. 🔒 authenticated()

authenticated() means the user must be authenticated.

Example:

.requestMatchers("/hello").authenticated()

Both USER and ADMIN can access /hello because both are authenticated.

Ronik → USER → Authenticated → ✅

Admin → ADMIN → Authenticated → ✅

The specific role does not matter for this rule.

24. 👑 hasRole()

hasRole() checks whether the authenticated user has a particular role.

Example:

.requestMatchers("/admin").hasRole("ADMIN")

This checks for:

ROLE_ADMIN

Normally:

hasRole("ADMIN")

not:

hasRole("ROLE_ADMIN")

because the role check handles the ROLE_ prefix.

25. 🚫 401 Unauthorized

A 401 Unauthorized response generally means authentication is required or the provided authentication is not valid.

Example:

Unauthenticated user
        ↓
GET /hello
        ↓
401 Unauthorized

Remember:

401 → Authentication problem

Easy way to remember:

401 = "I don't know who you are."

26. ⛔ 403 Forbidden

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

403 = "I know who you are, but you cannot do this."

27. 🔑 HTTP Basic Authentication

Our Day 1 project uses HTTP Basic Authentication.

The client sends credentials with the HTTP request.

Conceptually:

Browser / Postman
       ↓
Username + Password
       ↓
Spring Security
       ↓
Authentication

HTTP Basic is different from traditional session-based login.

A browser may cache Basic credentials.

Because of this, switching between users in the same browser can sometimes be confusing.

For testing different users, use:

Incognito/private window

Postman

Another HTTP client

28. 🔥 Complete Authentication Flow

This is one of the most important flows to remember.

Client
  ↓
HTTP Request
  ↓
SecurityFilterChain
  ↓
Security Filters
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
  ↓
Response

For username/password authentication:

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

29. 🧪 Example: Ronik Accessing /admin

Suppose Ronik provides the correct password.

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
❌ Access denied
  ↓
403 Forbidden

The important thing:

Authentication → SUCCESS

Authorization → FAILED

Therefore:

403 Forbidden

30. 🧪 Example: Admin Accessing /admin

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
Loads Admin's UserDetails
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
✅ Access granted
  ↓
Controller
  ↓
200 OK

31. 🏗️ Day 1 Project — Secure Hello

The project contains three endpoints:

/public
/hello
/admin

Security rules:

/public
    ↓
permitAll()

/hello
    ↓
authenticated()

/admin
    ↓
hasRole("ADMIN")

Users:

Ronik
Username: Ronik
Role: USER

Admin
Username: admin
Role: ADMIN

32. 📊 Expected Endpoint Behavior

User

/public

/hello

/admin

Unauthenticated

✅ 200

❌ 401

❌ 401

Ronik (USER)

✅ 200

✅ 200

❌ 403

Admin (ADMIN)

✅ 200

✅ 200

✅ 200

33. 🧪 What Was Tested

Public endpoint

GET /public

Expected:

200 OK

USER accessing /hello

Username: Ronik
Password: ronik123

Expected:

200 OK

ADMIN accessing /hello

Username: admin
Password: admin123

Expected:

200 OK

ADMIN accessing /admin

Username: admin
Password: admin123

Expected:

200 OK

USER accessing /admin

Username: Ronik
Password: ronik123

Expected:

403 Forbidden

34. 🧩 Important Mental Models

Model 1 — Authentication and Authorization

Authentication
     ↓
Who are you?

Authorization
     ↓
What can you do?

Model 2 — Request Security

Request
  ↓
SecurityFilterChain
  ↓
Authentication
  ↓
Authorization
  ↓
Controller

Model 3 — Username/Password Authentication

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

35. 💡 Most Important Things I Learned

Spring Security protects requests before they reach the controller.

Authentication verifies the identity of a user.

Authorization checks what the authenticated user is allowed to access.

SecurityFilterChain configures how HTTP requests are secured.

AuthenticationManager coordinates authentication.

AuthenticationProvider performs a specific authentication mechanism.

UserDetailsService loads user information.

UserDetails represents the security information of a user.

PasswordEncoder encodes and verifies passwords.

SecurityContext contains the current authentication information.

Roles represent broad categories of access.

Authorities represent permissions/access.

permitAll() allows access without authentication.

authenticated() requires authentication.

hasRole("ADMIN") checks for the ADMIN role.

401 generally means authentication is required or invalid.

403 generally means the user is authenticated but not authorized.

36. 🎯 Day 1 Interview Questions

What is Spring Security?

Spring Security is a framework used to secure Spring applications by providing authentication, authorization, password security and other security mechanisms.

What is authentication?

Authentication is the process of verifying the identity of a user or security principal.

What is authorization?

Authorization determines what an authenticated user is allowed to access or perform.

What is SecurityFilterChain?

It is a configured chain of Spring Security filters that processes incoming HTTP requests and applies security rules before the request reaches the application.

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

37. 🧠 Day 1 Quick Revision

If I have only one minute to revise Day 1:

Spring Security
      ↓
Protects the application
      ↓
SecurityFilterChain
      ↓
Authentication
      ↓
"Who are you?"
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
"What can you do?"
      ↓
Controller

The three things I must remember

🔐 Authentication
Who are you?

🛂 Authorization
What can you do?

🔑 PasswordEncoder
How do we securely store and verify passwords?

Status Codes

401 → Authentication problem

403 → Authenticated but not authorized

Access Rules

permitAll()
→ Everyone

authenticated()
→ Logged-in/authenticated users

hasRole("ADMIN")
→ Users with ADMIN role

38. 🚀 What Comes Next?

Day 1 focused on understanding the architecture and basic security concepts.

The next step is to go deeper into Authentication.

Topics will include:

AuthenticationManager in more detail

AuthenticationProvider in detail

Database-backed authentication

Custom UserDetailsService

Authentication flow internally

Authentication failures

Custom authentication logic

How Spring Security decides which authentication mechanism to use

The goal is not to memorize Spring Security configuration.

The goal is to understand:

What happens to an HTTP request from the moment it enters Spring Security until it reaches the controller?

Day 1 Complete ✅

Fundamentals     ✅
Authentication   ✅
Authorization    ✅
Security Filters  ✅
Roles            ✅
Authorities      ✅
Password Security ✅
HTTP Basic        ✅
401 vs 403        ✅
Practical Project ✅
Testing           ✅

Day 1 takeaway: Security is not just about putting .authenticated() in a configuration. The important skill is understanding the complete authentication → security context → authorization flow.