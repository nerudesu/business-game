business-game (Zentriumph)
==========================

A Massively Multiplayer Online Business Simulation Game for Android.

# Game Design
We try to simulate real-world conditions in some cases where people have to propose a proposal to a bank to cover their expenses when creating new industries (from equipment, employees, storage, to raw materials [for several turns]). They’ve to return the money they borrowed from the bank at a specific time.

## Industries
The industries are in a linked chain (circulated in circle ways).

## Market
People can interact with each other on the market; they can specify their price on the product. People can choose where they want to live (there’s a region system, people can buy/sell their product from/to other regions by import/export).

## Product Quality
Every product including equipment and employee has its quality rated from 1 star to 5 stars, it’ll affect the output product (using a matrix).

## Product Production
Every industry can run no matter number of equipment that you run in a factory, say it you must have about 2200 pumps to run an oil well, but with 200 pumps your industry can still run (ratio).

## Advertisement
There’s another feature such as advertisement. We’re trying to implement all marketing mix aspect on this research.

# Technology in use
- Client: Android (minimum 2.3)
- Game Framework: libGDX
- Server: Spring
- Database: MySql
