# udp-recv

Listens for UDP packets that contain a sequence number only. Currently the sequence number is actually sent as ASCII text because I am lazy and stupid.

## Installation

1. Install [leiningen](https://github.com/technomancy/leiningen)
2. That's it.

## Usage

Run `lein trampoline run <port#>`. Press CTRL-C to terminate. Output is written to 'data.csv'.

FIXME
