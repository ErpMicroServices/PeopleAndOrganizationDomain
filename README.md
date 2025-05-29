# **The People and Organization Domain**
It provides the core models for any
- CRM 
- HRM 
- VRM
- Help Desk

and any other domain that needs to track information on who they're talking to, what you're talking about, and how to contact them so you can talk to them.

# Architecture
## Domain Driven Design
Domain-Driven Design (DDD) is a software development approach that emphasizes understanding and modeling the specific business domain the software is intended to support. It's a strategy for creating software that more accurately addresses the needs of the business, improves communication between developers and domain experts, and leads to more maintainable and flexible systems.
### Key Principles of DDD:
#### Domain-Driven Design:
Focusing on the business domain and its complexities, rather than solely on technology or technical details.
#### Ubiquitous Language:
Using a shared vocabulary between developers and business stakeholders to ensure everyone is on the same page.
#### Bounded Contexts:
Dividing a complex system into smaller, more manageable parts with their own specific models and vocabulary.
#### Domain Model:
Creating a conceptual model of the domain, which is then reflected in the software code.
#### Entities:
Objects that have unique identifiers and a persistent state within the domain.
#### Value Objects:
Objects that represent attributes or characteristics of the domain, without a unique identity.
#### Aggregates:
Clusters of related entities treated as a single unit to maintain consistency.
### Benefits of DDD:
#### Better Alignment with Business Needs:
DDD ensures that the software is closely aligned with the business requirements and goals, leading to more effective solutions.
#### Improved Communication:
The use of a shared ubiquitous language reduces misunderstandings and promotes better collaboration between developers and domain experts.
#### Modularity and Flexibility:
DDD encourages the creation of modular systems that are easier to understand, maintain, and adapt to changes in business requirements.
#### Reduced Complexity:
By breaking down complex systems into bounded contexts, DDD helps manage complexity and makes it easier to develop and maintain the software.
#### Improved Code Quality:
DDD promotes a more thoughtful and well-structured design, leading to higher quality software.
#### When to Use DDD:
DDD is particularly well-suited for complex domains with significant business logic and the need for close collaboration with domain experts. It can be a valuable approach for projects involving:
- Large applications with significant business complexity.
- Systems where domain knowledge and expert input are crucial.
- Projects that require a deep understanding of the business domain.
- Situations where flexibility and scalability are important.

In summary, DDD is a software development philosophy that emphasizes understanding and modeling the business domain to create more effective, maintainable, and flexible software solutions. By focusing on the domain and using a shared ubiquitous language, DDD helps to bridge the gap between technical development and business requirements, leading to better software outcomes.

# Software Development Principles
## DRY - Don’t repeat yourself
This is probably the most fundamental programming tenet: avoid repetition. Many programming constructs exist solely for that purpose (e.g., loops, functions, classes, and more). As soon as you start repeating yourself (e.g., a long expression, a series of statements, the same concept), create a new abstraction. [http://en.wikipedia.org/wiki/Don%27t_repeat_yourself](http://en.wikipedia.org/wiki/Don't_repeat_yourself)

## Abstraction Principle 
 Related to DRY is the abstraction principle: “Each significant piece of functionality in a program should be implemented in just one place in the source code.” [http://en.wikipedia.org/wiki/Abstraction_principle_(programming](http://en.wikipedia.org/wiki/Abstraction_principle_(programming))

##  KISS (Keep it simple, stupid!) 
 Simplicity (and avoiding complexity) should always be a key goal. Simple code takes less time to write, has fewer bugs, and is easier to modify. [http://en.wikipedia.org/wiki/KISS_principle](http://en.wikipedia.org/wiki/KISS_principle)

## Avoid Creating a YAGNI (You aren’t going to need it) 
 You should try not to add functionality until you need it. [http://en.wikipedia.org/wiki/YAGNI](http://en.wikipedia.org/wiki/YAGNI)

## Do the simplest thing that could work 
 A good question to ask oneself when programming is “What is the simplest thing that could work?” This helps keep us on the path towards simplicity in the design. [http://c2.com/xp/DoTheSimplestThingThatCouldPossiblyWork.html](http://c2.com/xp/DoTheSimplestThingThatCouldPossiblyWork.html)

## "Don't Make Me Think"
The title of a book by Steve Krug on web usability, which is also relevant in software development and programming. The point is that code should be easily read and understood with minimal effort. If code requires too much thinking from an observer to understand, then it can probably stand to be simplified [http://www.sensible.com/dmmt.html](http://www.sensible.com/dmmt.html)

## Open/Closed Principle 
 Software entities (classes, modules, functions, etc.) should be open for extension, but closed for modification. In other words, don't write classes that people can modify; write classes that people can extend. [http://en.wikipedia.org/wiki/Open_Closed_Principle](http://en.wikipedia.org/wiki/Open_Closed_Principle)

## Write Code for the Maintainer 
 Almost any code worth writing is worth maintaining in the future, either by you or someone else. The future you who has to maintain code often remembers as much of the code as a stranger, so you should always write for someone else. A memorable way to remember this is “Always code as if the person who maintains your code is a violent psychopath who knows where you live.” [http://c2.com/cgi/wiki?CodeForTheMaintainer](http://c2.com/cgi/wiki?CodeForTheMaintainer)

## Principle of least astonishment 
 The principle of least astonishment is usually referenced regarding the user interface, but the same principle applies to written code. Code should surprise the reader as little as possible. Following standard conventions, the code should do what the comments and names suggest, and potentially surprising side effects should be avoided as much as possible. [http://en.wikipedia.org/wiki/Principle_of_least_astonishment](http://en.wikipedia.org/wiki/Principle_of_least_astonishment)

## Single Responsibility Principle 
 A component of code (e.g., class or function) should perform a single well-defined task. [http://en.wikipedia.org/wiki/Single_responsibility_principle](http://en.wikipedia.org/wiki/Single_responsibility_principle)

## Minimize Coupling 
 Any section of code (code block, function, class, etc) should minimize the dependencies on other areas of code. This is achieved by using as few shared variables as possible. “Low coupling is often a sign of a well-structured computer system and a good design, and when combined with high cohesion, supports the general goals of high readability and maintainability” [http://en.wikipedia.org/wiki/Coupling_(computer_programming](http://en.wikipedia.org/wiki/Coupling_(computer_programming))

## Maximize Cohesion 
 Code with similar functionality should be found within the same component. [http://en.wikipedia.org/wiki/Cohesion_(computer_science](http://en.wikipedia.org/wiki/Cohesion_(computer_science))

## Hide Implementation Details 
 Hiding implementation details enables changes to a code component's implementation while minimizing the impact on other modules that utilize that component. [http://en.wikipedia.org/wiki/Information_Hiding](http://en.wikipedia.org/wiki/Information_Hiding)

## Law of Demeter 
 Code components should only communicate with their direct relations (e.g., classes that they inherit from, objects that they contain, objects passed by argument, etc.) [http://en.wikipedia.org/wiki/Law_of_Demeter](http://en.wikipedia.org/wiki/Law_of_Demeter).

## Avoid Premature Optimization 
 Don’t even think about optimization unless your code is working, but slower than you want. Only then should you begin thinking about optimization, and only then, with the aid of empirical data. "We should forget about small efficiencies, say about 97% of the time: premature optimization is the root of all evil" - Donald Knuth. [http://en.wikipedia.org/wiki/Program_optimization](http://en.wikipedia.org/wiki/Program_optimization)

## Code Reuse is Good 
 Not very pithy, but as good a principle as any other. Reusing code improves code reliability and decreases development time. [http://en.wikipedia.org/wiki/Code_reuse](http://en.wikipedia.org/wiki/Code_reuse)

## Separation of Concerns 
 Different areas of functionality should be managed by distinct and minimally overlapping modules of code. [http://en.wikipedia.org/wiki/Separation_of_concerns](http://en.wikipedia.org/wiki/Separation_of_concerns)

## Embrace Change 
 This is the subtitle of a book by Kent Beck and is also considered a tenet of Extreme Programming and the Agile methodology in general. Many other principles are based on the concept that you should expect and welcome change. Ancient software engineering principles, such as minimizing coupling, are directly related to the requirement of making code easier to change. Whether or not you are an extreme programming practitioner, this approach to writing code makes sense. [http://www.amazon.com/gp/product/0321278658](http://www.amazon.com/gp/product/0321278658)

## Liskov Substitution Principle 
 It states that a subclass can be used in place of its parent class. [Wikipedia Article](https://en.wikipedia.org/wiki/Liskov_substitution_principle)

## Interface Segregation 
 In the field of [software engineering](https://en.wikipedia.org/wiki/Software_engineering "Software engineering"), the **interface segregation principle** (**ISP**) states that no code should be forced to depend on [methods](https://en.wikipedia.org/wiki/Method_\(computer_programming\) "Method (computer programming)") it does not use.[[1]](https://en.wikipedia.org/wiki/Interface_segregation_principle#cite_note-ASD-1) ISP splits [interfaces](https://en.wikipedia.org/wiki/Interface_\(computing\) "Interface (computing)") that are very large into smaller and more specific ones so that clients will only have to know about the methods that are of interest to them. Such shrunken interfaces are also called _role interface_s.[[2]](https://en.wikipedia.org/wiki/Interface_segregation_principle#cite_note-RoleInterface-2).  [Wikipedia Artile](https://en.wikipedia.org/wiki/Interface_segregation_principle)

 ## Dependency Inversion 
 In [object-oriented design](https://en.wikipedia.org/wiki/Object-oriented_design "Object-oriented design"), the **dependency inversion principle** is a specific methodology for [loosely coupled](https://en.wikipedia.org/wiki/Coupling_\(computer_programming\) "Coupling (computer programming)") software [modules](https://en.wikipedia.org/wiki/Modular_programming "Modular programming"). When following this principle, the conventional [dependency](https://en.wikipedia.org/wiki/Dependency_\(computer_science\) "Dependency (computer science)") relationships established from high-level, policy-setting modules to low-level, dependency modules are reversed, thus rendering high-level modules independent of the low-level module implementation details. The principle states:[[1]](https://en.wikipedia.org/wiki/Dependency_inversion_principle#cite_note-Martin2003-1)
1. High-level modules should not import anything from low-level modules. Both should depend on abstractions (e.g., interfaces).
2. Abstractions should not depend on details. Details (concrete implementations) should depend on abstractions.
   [Wikipedia Article](https://en.wikipedia.org/wiki/Dependency_inversion_principle)
# Policies
## Versioning
This project follows [Semantic Versioning](https://semver.org).  Non-production versions will end in -SNAPSHOT.
## Code Quality
### Static code analysis
Every commit shall be statically analyzed for 
- code complexity 
- duplicate code 
- missing code 
- missing documentation 
- unused code
- memory leaks
- run time errors
- data type errors 
As part of a a CI/CD pipeline:
- Code complexity will be examined for the top 3 most complex methods, will have a defect per method written.  
- All other failures of the static analysis will result in a defect ticket being written for the failure.
### OWASP top 10 errors
The [OWASP top 10 errors](https://owasp.org/www-project-top-ten/) that are functional shall be part of the features in the features directory.  If one of them is removed from the list, they shall not be removed from the features.  The errors that can be statically analyzed will become part of the static analysis, and again shall not be removed.
As part of a a CI/CD pipeline OWASP failures shall have defect tickets written for the failures.
### Unit tests
Unit tests coverage will be 90% of all code, except getters/setters that are one line of code.  Writing unit tests will follow the same principles as regular code, as provided by the static code analysis quality policy.
Only SQL code that is not a stored procedure is exempt.
Unit test code is not deployable, and should never be included in a build artifact.
As part of a CI/CD pipeline Unit test that fail shall have a defect written. 
### Behavior Tests
These tests start with features in the features directory, and are executed by Cucumber.  Test coverage is a minimum of 80%.
As part of a CI/CD pipeline any test failures will result in a ticket to be written.
## Release Standards
- All defects must be resolved.
- No -SNAPSHOTS, not even a transitive dependency.  
# Repository Organization
## features directory
There is a directory in which all features are described, and scenarios provided using BDD.
## database directory
This directory contains the flyway migration files.  The files are in yyyymmddhhmm-feature name format.  They should include a rollback script as well that flyway can use.
## ui-components directory
This directory contains the React components that one could use to build a web based react UI from.
## api directory
This directory contains the Spring Boot GraphQL api that the UI components use.
# Development Process
1. Describe the feature you want, and provide examples using Behavior Driven Development language.  
2. Every new feature must start development in it's own branch
3. Start with the data, 
    1. and data integrity behavior ( not null, foreign keys etc).  These scenarios should be annotated with @data. 
    2. Implement the data test in cucumber in the database, make sure they run and fail. 
    3. Add the data to a flyway migration file. 
    4. Run flyway, and create a docker instance 
    5. Run the cucumber tests for the database.  Make sure they pass. 
    6. commit the changes.
4. Then work on the API
   1. create the cucumber tests.  Make sure they run and fail.
   2. Create the functionality
   3. Make sure that all one to many relationships are searchable, sortable and pageable.  If necessary add these to the examples, and have their tests.
   4. Run the tests, make sure they pass
   5. commit the changes.
5. Then work on the UI Components
   1. Create the cucumber tests.  Make sure they run and fail
   2. Create the components
   3. Make sure the test run and pass
   4. Commit the changes.
6. Wait for the CI/CD pipeline to finish
7. Check github for any defect tickets
   1. Resolve each defect one at a time.
   2. Commit the code.
   3. wait for the CI/CD pipeline to finish after you fix the last defect ticket
8. Begin the release process.
   
