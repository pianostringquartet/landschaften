(ns landschaften.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [landschaften.core-test]))

(doo-tests 'landschaften.core-test)

