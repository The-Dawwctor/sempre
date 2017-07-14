# README

This `interactive` package is the preliminary code for utilizing Natural Language Processing to control robots.

NLP Robot Control (NRC) is a language interface to operational space robot control.
This server handles commands used to learn from definitions, and other interactive queries.
In this setting, the system begin with the dependency-based action language (`dal.grammar`), and gradually expand the language through interacting with the user.

## Overview of the components

### sempre.interactive

The `edu.stanford.nlp.sempre.interactive` package live in this repo contains code for
* running interactive commands (such as query, accept, reject, definition)
* executor for the dependency-based action (DAL) language
* NRC specific code in `edu.stanford.nlp.sempre.interactive.robot` for actually generating the points and manipulating them.

Utilties and resources such as the grammar and run script are in this directory, and the code in in the regular `sempre/src` directory.

### NRC client

It queries the server and renders the points to a browser.
Code for the client at `https://github.com/The-Dawwctor/shrdlurn-robot`. See its [README.md](https://github.com/The-Dawwctor/shrdlurn-robot/blob/master/README.md) if you want to work with and build the client yourself.


### NRC community server
Located at `interactive/community-server`, the community server
handles other functionalities such as logging client actions, leaderboard, submiting structures, authentication etc. and generally functions not related to parsing. This server is needed for running interactive experiments, but is not required just for trying out NRC.


## Running the SEMPRE server for NRC

0. Setup SEMPRE dependencies and compile

         ./pull-dependencies core
         ant interactive

1. Start the server

        ./interactive/run @mode=robot -server -interactive

  things in the core language such as `add red left`, `repeat 3 [select left]` should work.

2. Feed the server existing definitions, which should take less than 2 minutes.

        ./interactive/run @mode=simulator @server=local @sandbox=none @task=freebuilddef -maxQueries 2496

  try `add dancer` now.

### Interacting with the server

After you run the above, there are 3 ways to interact and try your own commands.

* The visual way is to use the client: [https://the-dawwctor.github.io/shrdlurn-robot/](https://the-dawwctor.github.io/shrdlurn-robot/).
  Code for the client is at `https://github.com/The-Dawwctor/shrdlurn-robot` (see its [README.md](https://github.com/The-Dawwctor/shrdlurn-robot/blob/master/README.md)).
  Try `[add dancer; front 5] 3 times`.

* Hit `Ctrl-D` on the terminal running the server, and type `add red top`, or `add green monster`

* On a browser, type `http://localhost:8410/sempre?q=(:q add green monster)`


## Experiments in ACL2017

1. Start the server

       ./interactive/run @mode=voxelurn -server -interactive

2. Feed the server all the query logs

       ./interactive/run @mode=simulator @server=local @sandbox=none @task=freebuild -maxQueries 103874

  This currently takes just under 30 minutes. Decrease maxQuery for a quicker experiment. This generate `plotInfo.json` in `./state/execs/${lastExec}.exec/` where `lastExec` is `cat ./state/lastExec`.

3. Taking `../state/execs/${lastExec}.exec/plotInfo.json` as input, we can analyze the data and produce some plots using the following ipython notebook

       ipython notebook interactive/analyze_data.ipynb

  which prints out basic statistics and generates the plots used in our paper. The plots are saved at `../state/execs/${lastExec}.exec/`


## Misc.

There are some unit tests

    ./interactive/run @mode=test

To specify a specific test class and verbosity

    ./interactive/run @mode=test @class=DALExecutorTest -verbose 5

Clean up or backup data

    ./interactive/run @mode=backup # save previous data logs
    ./interactive/run @mode=trash # deletes previous data logs

Data, in .gz can be found in queries.

* `./interactive/queries/freebuild.def.json.gz`
has 2495 definitions combining just over 10k utterances.
* `./interactive/queries/freebuild.json.gz` has 103873 queries made during the main experiment.

## Voxelurn community server (optional and in development)

This server helps with client side logging, leaderboard, authentication etc. basically anything that is not directly related to parsing.
This component is only required if you want to run the interactive experiment yourself. It is fairly coupled with the [voxelurn  client](http://github.com/sidaw/shrdlurn), which sends the request to this server.

    cd interactive/community-server
    python install-deps.py
    python server.py --port 8403

    # required keys for authentication
    export SEMPRE_JWT_SECRET=ANY_RANDOM_SEQEUNCE
    export SLACK_OAUTH_SECRET=OAUTH_KEY_FROM_SLACK
