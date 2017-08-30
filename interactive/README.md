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

## Running the SEMPRE server for NRC

0. Setup SEMPRE dependencies and compile

         ./pull-dependencies core
         ant interactive

1. Start the server

        ./interactive/run @mode=robot -server -interactive

  things in the core language such as `add red 1 2 3`, `block 2 4 6` should work.

### Interacting with the server

After you run the above, there are 3 ways to interact and try your own commands.

* The visual way is to use the client: [https://the-dawwctor.github.io/shrdlurn-robot/](https://the-dawwctor.github.io/shrdlurn-robot/).
  Code for the client is at `https://github.com/The-Dawwctor/shrdlurn-robot` (see its [README.md](https://github.com/The-Dawwctor/shrdlurn-robot/blob/master/README.md)).
  Try `repeat 5 [add red left]`.

* Hit `Ctrl-D` on the terminal running the server, and type `add red top`, or `block 0 5 0`