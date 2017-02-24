# Event Planner SQL Scripts

## How To Use

### First Time

If this is your first time running this script, perform the following steps:

1. If on OSX, install coreutils:

`sudo brew install coreutils`

2. In the postgres command line, run the following commands:

```
create role event_planner with superuser login;
alter user event_planner password 'password';
```

3. Open the file ~/.pgpass. If this file does not exist, run the following
commands in the terminal:

```
cd ~
touch .pgpass
chmod 0600 .pgpass
```

4. Add the following line to ~/.pgpass:

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
