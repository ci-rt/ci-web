#! /bin/bash

PSQL_SCRIPT=$(dirname $0)/fix_sequences.psql

if [ x"$1" = x ]
then
    echo "usage: $0 <RT-Test-Database>"
    exit 1
fi

psql -Atq -f $PSQL_SCRIPT $1 | psql $1
