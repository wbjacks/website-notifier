# Website Notifier
Provides API where users can send email address and url to be notified at that adress
when the website at the given URL changes.

## To Run
- A valid gmail address and password must be provided in the email.properties file.
- Run `mvn clean install` to test, compile, and package code.
- Run `mvn exec:java` to launch packaged JAR on a local Jetty server.

## API
Example curl commands given for ease of use

| Endpoint | Data | `curl` Example | Comment |
| --- | --- | --- | --- |
| `GET /hello`       | n/a                             | `curl localhost:4567/hello` | Say hello! |
| `POST /observeUrl` | `url`: String, `email`: String` | `curl -H "Content-Type: application/json" -X POST -d '{"email":"foo@gmail.com","url":"http://www.bar.com/"}' localhost:4567/observeUrl` | Observe URL and send notifications to given email. Note that url must be fully qualified.

## Design Description
The application was created foremost with portability in mind- the user must only add a
few bits of sensitive information to the configurations and they are able to use it with
two Maven commands. To achieve this, I used an in-memory H2 database. While this prevents
the user from having to do any database configuration, saving a lot of hassle, it means
that all persisted data will only last during the scope of the application runtime.
Similarly, these constraints required that Quartz, the library I'm using for job
scheduling, maintain job data in-memory.

The application is basically split into two packages, the request service and the task
service- this was done to allow maintainers to easily migrate the application to a
distributed environment, as there is only a single point of communication between
services. The request service takes user requests, creates the application models, and
persists them, merging data with existing models as required. It then sends the site URL
to the task service to issue a job to watch it. The task service has a job scheduler,
which uses Quartz, to start a job at a user-specified interval (the default is 10s, but
feel free to change it in the scheduler.configurations file). The job grabs the website
model from the database (along with it's many-to-many linked observers), checks if the
website is changed, and if it is, launches email tasks. The emails are actually sent in
threaded tasks in an executor in order to save processor time.

A website is considered changed if the body of the HTML recieved is different from the
last time it was checked. This is calculated by getting the MD5 hash of the body, an
algorithm efficient enough to be considered standard for such a task.

## A Note on Scaling
As mentioned, the services are intentionally decoupled, which would allow easy migration
to a microservice or distributed architecture. To achieve any sort of real production-
readiness, much less scale, the user would have to switch from using an in-memory
database to an actual persistent database. Hibernate should allow for this to be done by
changing configuration files exclusively- it's worth noting that the code currently
assumes that the database might be populated on startup, despite the fact that with an
in-memory database, it never will be.

The first method for scaling the application would come from moving the email exector
from a single-thread to a configurable thread-pool. This is actually pretty simple, as
a thread-pool executor is simply a different implementation of the Executor interface,
and configurations are already supported in the application. The maintainers should test
that this is, in fact, a bottleneck before making this change.

The second method relies on optimizing Quartz, the job scheduling library. Quartz is
widely used for large applications and is configurable to run in a distributed,
persistent environment, which would allow the application to scale up to hundreds of
thousands of users. In fact, the FAQ states that even without these configurations, there
are applications capable of handling hundreds of thousands of jobs (IE, websites)
simultaneously. The limit of the current setup, therefore, is fairly dependent on how
 much memory the user has available on their system.

The maintainers could also add code to stagger the Quartz job triggers, requiring a
minimum interval to prevent DDOS, but otherwise evenly distributing the jobs to prevent
rushing the processor.
