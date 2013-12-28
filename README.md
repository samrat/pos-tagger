# pos-tagger

A parts-of-speech tagger using Hidden Markov Models and trained using the
[Brown Corpus](http://nltk.googlecode.com/svn/trunk/nltk_data/packages/corpora/brown.zip)
(.zip file). The tagset is described [here](http://www.comp.leeds.ac.uk/amalgam/tagsets/brown.html).

## Usage

First start a REPL inside the project dir:

    lein repl

Then,

    (require '[pos-tagger.hmm :refer :all])

    (tag-sequence ["the" "man" "saw" "a" "dog" "."])
    ;; ("at" "nn" "vbd" "at" "nn" ".")

## License

Copyright © 2013 [Samrat Man Singh](http://samrat.me)

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
