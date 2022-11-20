# Talent Catalog #

## Overview ##

This is the repository for the Talent Catalog (TC), which manages data
for refugees looking for skilled migration pathways into safe countries and employment.

This repository is a "mono-repo", meaning it contains multiple sub-modules all of which
make up the Talent Catalog system. In particular it contains:

- **server**: the backend module of the system providing secure API (REST) access to the
data, stored in an SQL Database. This module is written in Java / Spring Boot.
- **candidate-portal**: the frontend module through which candidates (refugees seeking skilled
migration) are able to register and manage their details. This is written in Angular and connects
to the REST API endpoints under `/api/portal` provided by the server.
- **admin-portal**: the frontend module through which TBB staff are able to view, manage and annotate
candidate details. This is written in Angular and connects to the REST API endpoints under
`/api/admin` provided by the server.
- **public-portal**: a module through which anyone can access publicly available data.
This is written in Angular and connects to the REST API endpoints under
`/api/public` provided by the server.

## Contributing ##

Contributions are very welcome. Please see
[our contribution guidelines](CONTRIBUTING.md).
They should be submitted as pull request.

## How do I get set up? ##

### Install the tools ###

>IMPORTANT NOTE:
>
>These instructions are tailored for Mac users, as this is what we use for development.
>
>On a Mac, installing with Homebrew usually works well. eg "brew install xxx".
However, Flyway and Postgres don't install with Homebrew, and the book
"Angular Up & Running" notes that installing Node.js using Homebrew
can also have problems. Googling you can still see lots of people having
problems installing Node using brew.
>
>It is also probably easier to install Java directly (or from your
development IDE - see below) rather than using brew.

Download and install the latest of the following tools.

- IntelliJ IDEA (or the IDE of your choice) - [Intellij website](https://www.jetbrains.com/idea/download/)

- Java 11
  - At least Java 11 is required because we use the Locale object to provide translations of
  countries and languages and that support is not complete in Java 8, for example.
  - If you are using a recent version of Intellij the version of Java that comes with it works
 fine except that it does not have library source code - so probably best to download a new SDK
     (which you can from inside Intellij).

- Gradle [https://gradle.org/install/](https://gradle.org/install/)
  ```shell
  brew install gradle
  ```


- NodeJS: Install as described here [https://nodejs.org/en/](https://nodejs.org/en/)
  - Note that you should use the LTS version of node - which is not normally the latest.

    "Production applications should only use Active LTS or Maintenance LTS releases." -
  <https://nodejs.org/en/about/releases/>

- Angular CLI [https://angular.io/cli](https://angular.io/cli)

  ```shell
  npm install -g @angular/cli
  ```

  - To upgrade Angular versions, see <https://update.angular.io/>

- cURL (for database migrations, can also use Postman)
  > brew install curl
  >
  > or...
  >
  > brew install --cask postman

- Docker
  - Install Docker Desktop for Mac -
      see [docker website](https://hub.docker.com/editions/community/docker-ce-desktop-mac/)

- Elasticsearch (for text search)
  - Install Docker image.
      See [Elastic search website](https://www.elastic.co/guide/en/elasticsearch/reference/current/docker.html)
      Just pull the image to install. See later for how to run.

- Kibana (for monitoring Elasticsearch)
  - Install Docker image.
      See [Elastic search website](https://www.elastic.co/guide/en/kibana/current/docker.html)
      Just pull the image to install. See later for how to run.

- Git - [see Git website](https://git-scm.com/downloads)
- PostgreSQL - [Postgres website](https://www.postgresql.org/download/)

### Setup your local database ###

```shell
docker run -d --name tbb_postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 library/postgres:14
```

Use the [create_database.sh](scripts/database/create_database.sh) to create the tbbtalent user, and database or use the PostreSQL pgAdmin tool to...
To make the scripts execute without prompting for a password create a pgpass file with the following contents.

```text
#hostname:port:database:username:password
localhost:5432:postgres:postgres:postgres
localhost:5432:tbbtalent:postgres:postgres
localhost:5432:tbbtalent:tbbtalent:tbbtalent
```

On Linux save this file as `~/.pgpass`.
On Windows save this file as `%APPDATA%\postgresql\pgpass.conf`.
See [pgpass file documentation](https://www.postgresql.org/docs/current/libpq-pgpass.html)


- Create a new login role (ie user) called tbbtalent, password tbbtalent with
full privileges

- Create a new database called tbbtalent and set tbbtalent as the owner

The database details are defined in bundle/all/resources/application.yml

The database is populated/updated using Flyway at start up - see TbbTalentApplication

**The migration endpoint is now hidden, and seems to require a mysql DB**
- Run data migration script to add additional data - using tool like postman or curl
  - call login <http://localhost:8080/api/admin/auth/login> and save token

```shell
curl -X POST -H 'Content-Type: application/json' -d "{\"username\":\"${USERNAME}\",\"password\":\"${PASSWORD}\"}" http://localhost:8080/api/admin/auth/login
```

  - call API <http://localhost:8080/api/admin/system/migrate> with token
```shell
curl -H 'Accept: application/json' -H "Authorization: Bearer ${TOKEN}" http://localhost:8080/api/admin/system/migrate
```

### Download and edit the code ###

- Clone [the repository](https://github.com/talentbeyondboundaries/tbbtalentv2.git) to your local system
- Open the root folder in IntelliJ IDEA (it should auto detect gradle and self-configure)

### Run Elasticsearch ###

Can run from Docker desktop for Mac, or (replacing appropriate version number)...

To create and start the container
```shell
docker run --name tbb_elasticsearch -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:7.12.0
```
To start a stopped container
```shell
docker start tbb_elasticsearch
```

Elasticsearch will run listening on port 9200.
You can verify this by going to [localhost:9200](http://localhost:9200) in your browser

### Run Kibana (optional) ###

Can run from Docker desktop for Mac, or (replacing appropriate version number)...

To create and start the container

```shell
docker run --name tbb_kibana --link tbb_elasticsearch -p 5601:5601 docker.elastic.co/kibana/kibana:7.12.0
```

To start a stopped container
```shell
docker start tbb_kibana
```

Kibana runs listening on port 5601.
You can verify this by going to [localhost:5601](http://localhost:5601) in your browser

### Run the server ###

- Some secret information such as passwords and private keys are set in
  environment variables - including programmatic access to TBB's Amazon AWS,
  Google and Salesforce accounts. Some of these secrets must exist for the application
to start see [CreateFakeSecrets](#Create-Fake-Secrets) for how to set up some
fake secrets just to get the application up and running without access to these integrations.
Contact TBB if you need access to these
  "secrets". They are stored in a tbb_secrets.txt file which you can hook into
  your start up to set the relevant environment variables.
  For example add "source ~/tbb_secrets.txt" to .bash_profile or .zshenv
  depending on whether you are running bash or zsh.
- To simulate the AWS S3 environment which is used for storing files, such as translations,
use [localstack](https://localstack.cloud/). 
  - Install as per instructions.
  - Start using `localstack start`
  - Use the [provided script](./scripts/localstack_s3/setup_s3.sh) to make english translations available to the candidate portal.
  - Set an environment variable in IntelliJ's Run configuration `AWS_S3_ENDPOINT=http://127.0.0.1:4566` or at system level.
  
    *Note:It's important to use the ip address rather than localhost so that [PathStyleAccess is enabled](https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/s3/AmazonS3Builder.html#enablePathStyleAccess--)* 
    
- Create a new Run Profile for `org.tbbtalent.server.TbbTalentApplication`.
  In the Environment Variables section of Intellij, check the
  "Include system environment variables" checkbox.
- By default, gradle will build the server, and the angular apps. 
For Java development, to just build the server, create a gradle.properties 
file in your 'gradle user home folder' `${HOME}/.gradle` / `%HOMEDRIVE%%HOMEPATH%\.gradle` or in the project root with the following property:

    ```text
    systemProp.env=dev
    ```
- Run the new profile, you should see something similar to this in the logs:

```text
Started TbbTalentApplication in 2.217 seconds (JVM running for 2.99)
```

- your server will be running on port 8080 (default for Spring Boot)
(can be overridden by setting server.port in application.yml, or Intellij Run
  Configuration, and updating environment.ts in portals)
- To test it open a browser to [http://localhost:8080/test](http://localhost:8080/test)

#### Create Fake Secrets ####

You will need some secrets to get the application running. If you do not need the integrations running you can create some Fake secrets.
[A shell script is provided to generate Fake Secrets](scripts/secrets/generate_fake_secrets.sh).

### Run the Candidate Portal ###

The "Candidate Portal" is an Angular Module and can be found in the directory `tbbtalentv2\ui\candidate-portal`.

Before running, make sure all the libraries have been downloaded locally by running `npm install` from the root
directory of the module (i.e. `tbbtalentv2\ui\candidate-portal`):

```shell
cd tbbtalentv2/ui/candidate-portal

npm install
```

> It is also a good idea to install fsevents for MacOS which will greatly
> reduce your CPU usage
>
>```shell
>npm install fsevents
>
>npm rebuild fsevents
>```
>

Then from within the same directory run:

```shell
ng serve
```

You will see log similar to:

```text
chunk {main} main.js, main.js.map (main) 11.9 kB [initial] [rendered]
chunk {polyfills} polyfills.js, polyfills.js.map (polyfills) 236 kB [initial] [rendered]
chunk {runtime} runtime.js, runtime.js.map (runtime) 6.08 kB [entry] [rendered]
chunk {styles} styles.js, styles.js.map (styles) 16.6 kB [initial] [rendered]
chunk {vendor} vendor.js, vendor.js.map (vendor) 3.55 MB [initial] [rendered]
i ｢wdm｣: Compiled successfully.
```

The Candidate Portal is now running locally and you can open a browser (chrome preferred) to:

[http://localhost:4200](http://localhost:4200)

**Note:** _this is for development mode only. In production, the Candidate Portal module will be bundled
into the server and serve through Apache Tomcat._

### Run the Public Portal ###

The "Public Portal" is an Angular Module and can be found in the directory `tbbtalentv2\ui\public-portal`.

As for the "Candidate Portal", make sure all libraries are installed locally.

Then from within the same directory run:

```shell
ng serve
````

You will see log similar to:

```text
chunk {main} main.js, main.js.map (main) 11.9 kB [initial] [rendered]
chunk {polyfills} polyfills.js, polyfills.js.map (polyfills) 236 kB [initial] [rendered]
chunk {runtime} runtime.js, runtime.js.map (runtime) 6.08 kB [entry] [rendered]
chunk {styles} styles.js, styles.js.map (styles) 16.6 kB [initial] [rendered]
chunk {vendor} vendor.js, vendor.js.map (vendor) 3.55 MB [initial] [rendered]
i ｢wdm｣: Compiled successfully.
```

The Public Portal is now running locally and you can open a browser (chrome preferred) to:

[http://localhost:4202](http://localhost:4202)

**Note:** _this is for development mode only. In production, the Public Portal module will be bundled
into the server and serve through Apache Tomcat._

### Run the Admin Portal ###

The "Admin Portal" is an Angular Module and can be found in the directory `tbbtalentv2\ui\admin-portal`.

As for the "Candidate Portal", make sure all libraries are installed locally.

Then from within the same directory run:

```shell
ng serve
```

You will see log similar to:

```text
chunk {main} main.js, main.js.map (main) 11.9 kB [initial] [rendered]
chunk {polyfills} polyfills.js, polyfills.js.map (polyfills) 236 kB [initial] [rendered]
chunk {runtime} runtime.js, runtime.js.map (runtime) 6.08 kB [entry] [rendered]
chunk {styles} styles.js, styles.js.map (styles) 16.6 kB [initial] [rendered]
chunk {vendor} vendor.js, vendor.js.map (vendor) 3.55 MB [initial] [rendered]
i ｢wdm｣: Compiled successfully.
```

The Admin Portal is now running locally and you can open a browser (chrome preferred) to:

[http://localhost:4201](http://localhost:4201)

**Note:** _this is for development mode only. In production, the Admin Portal module will be bundled
into the server and serve through Apache Tomcat._

### Log In To The Admin Portal ###

- On startup, the server automatically creates a default user with username `SystemAdmin`
and password `password` that can be used to log in to the admin portal in development.
- Details about this user can be found in `org/tbbtalent/server/configuration/SystemAdminConfiguration.java`

## Upgrades ##

### Angular ###

See <https://update.angular.io>

Note that you have to separately upgrade each of the Angular directories:

- ui/admin-portal
- ui/candidate-portal
- ui/public-portal

Assuming that the package.json in each of the above directories has the right
versions already in there you just need run the following commands in each
directory.

```shell
npm install
```

Note and fix any errors. "npm outdated" is good for identifying outdated libraries
"npm update --save" will update versions to the latest version within the allowed versions
specified by the package.json.

Once all versions are updated for the current version of Angular, you can run the Angular
update as follows.

```shell
ng update
```

This will prompt you to update the Angular core and cli. For example:

```shell
ng update @angular/core@13 @angular/cli@13
```

This will update package.json with the appropriate Angular versions which will drive updates of
other dependent libraries.

You may find that you need to manually upgrade versions of some tools in package.json so that they
work with the new version of Angular.
For example, you might need to upgrade the version of ng-bootstrap to a version that works with the
later version of Angular.
Look at the doc of the library in question to select the correct version

You may also need to make changes to your Angular code because of changes in Angular, or because of
changed APIs in the dependent libraries.

## Version Control ##

We use GitHub. Our repository is called tbbtalentv2 -
[https://github.com/talentbeyondboundaries/tbbtalentv2](https://github.com/talentbeyondboundaries/tbbtalentv2)

See the [GitHub wiki](https://github.com/talentbeyondboundaries/tbbtalentv2/wiki)
for additional documentation.

### Master branch ###

The main branch is "master". We only merge and push into "master" when we are
ready to deploy to production (rebuild and upload of build artefacts to the
production environment is automatic, triggered by any push to "master".
See Deployment section below).

Master should only be accessed directly when staging
is merged into it, triggering deployment to production. You should not
do normal development in Master.

### Staging branch ###

The "staging" branch is used for code which is potentially ready to go into
production. Code is pushed into production by merging staging into master and
then pushing master. See Deployment section below.

Staging is a shared resource so you should only push changes there when
you have finished changes which you are confident will build without error
and should not break other parts of the code.

As a shared resource, staging is the best way to share your code with other
team members to allow them to merge your code into their own branches and
also to allow them to review your code and help with testing.

### Personal branches ###

New development should be done in branches.

Typically, you should branch from the staging branch, and merge regularly
(eg daily) from staging so that your code does not get too far away from
what everyone else is doing.

When you are ready to share your code for others to take a look at and for
final joint testing and eventual deployment, merge your branch into staging.

On your branch you should commit often - doing separate commits for specific
functionality, rather than lumping different kinds of functionality into
a single big commit. That makes commits simpler to review and understand.
It also makes it easier to revert specific functionality when you have got
something wrong and decide to start again, doing it differently.

You should feel comfortable pushing regularly - often doing Commit and Push
at the same time. Pushing is effectively saving your work into the "cloud"
rather having changes just saved on your computer.

## Deployment and Monitoring ##

See the Deployment and Monitoring pages on the
[GitHub wiki](https://github.com/talentbeyondboundaries/tbbtalentv2/wiki).

## License ##

[GNU AGPLv3](https://choosealicense.com/licenses/agpl-3.0/)


## Setup static Data ##

To use the candidate portal you need some static data in your database. You can use the [provided script](scripts/database/populate_static_data.sh) to create this data.