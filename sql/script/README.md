# Event Planner SQL Scripts

## How To Use

### First Time

#### Install Postgres (OSX)

1. Install PostgreSQL:

`brew install postgresql`

2. Restart your machine.

3. Run the following command to open the postgres command line:

`psql -w -d postgres`

4. Run the following command in the postgres command line:

`create user postgres with login superuser password 'password';`

5. Open /usr/local/var/postgres/pg_hba.conf and add the following line:

`local all postgres md5`

6. Exit out of the postgres command line by entering `\q`, and then run the
following commands:

```
cd ~
touch .pgpass
chmod 0600 .pgpass
```

7. Open ~/.pgpass and add the following line:

`localhost:5432:*:postgres:password`

#### Install Postgres (Unix)

1. Install PostgreSQL:

`sudo apt-get install postgresql`

2. Open /etc/postgres/9.5/main/pg_hba.conf and change the following line:

`local all postgres ...`

to this:

`local all postgres ...`

3. Run the following command to open the postgres command line:

`psql -U postgres`

4. Run the following command in the postgres command line:

`alter user postgres password 'password';`

5. Exit out of the postgres command line by entering `\q`, and then run the
following commands:

```
cd ~
touch .pgpass
chmod 0600 .pgpass
```

6. Open ~/.pgpass and add the following line:

`localhost:5432:*:postgres:password`

#### Setup Postgres for Development

If this is your first time running this script, perform the following steps:

1. If on OSX, install dependencies:

```
brew install coreutils
pip install psycopg2
```

2. In the postgres command line, run the following commands:

`create role event_planner with superuser login password 'password';`

3. Add the following line to ~/.pgpass:

`localhost:5432:*:event_planner:password`

### Build/Rebuild Database

1. Run the following command in this directory:

`./rebuild_db.sh`

If you are using OSX, run this instead:

`./rebuild_db_osx.sh`

This will automatically create a fresh, up-to-date copy of the database with no
data at all. This also calls `patch_db.py`, so read the next section as well
to make sure you know what's going on.

### Patch Database

1. Run the following command in this directory:

`./patch_db.py -d event_planner -U event_planner`

If you are using OSX, run this instead:

`./patch_db_osx.py -d event_planner -U event_planner`

You will be prompted every so often to enter "y" or "n" while the database is
being built. Usually you'll want to enter "y" unless otherwise specified.
