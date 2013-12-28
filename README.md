# pos-tagger

A parts-of-speech tagger using Hidden Markov Models and trained using the
[Brown Corpus](http://nltk.googlecode.com/svn/trunk/nltk_data/packages/corpora/brown.zip)
(.zip file).

This repository also contains the files containing processed data in
the `brown.train` and `brown.counts` files. `brown.train` contains the
whole of the Brown corpus concattenated into a single file(with some
cleaning up). `brown.counts` contains n-gram and word-tag counts
obtained from `brown.train`.

The brown.counts file is formatted as follows:

    2 WORDTAG jj inward
    150 3-GRAM cs dt nn

The first line means that the word "inward" is paired with the "jj"
tag 2 times in the corpus.

The second line means that the trigram(3-GRAM) ["cs" "dt" "nn"]
appears 150 times in the corpus. There are also lines with counts for
1- and 2-grams.

The code used to produce `brown.counts` and `brown.train` files are in
the `pos-tagger.brown-counts` namespace.

The Brown corpus tagset is described
[here](http://www.comp.leeds.ac.uk/amalgam/tagsets/brown.html).

## Usage

First start a REPL inside the project dir:

    lein repl

Then,

    (require '[pos-tagger.hmm :refer :all])

    (tag-sequence ["the" "man" "saw" "a" "dog" "."])
    ;; ("at" "nn" "vbd" "at" "nn" ".")

## TODO
* Tokenizer
* Account for rare words, proper nouns, typos.(smoothing)
* Try a trigram language model.
* Evaluate performance.

## License

Copyright © 2013 [Samrat Man Singh](http://samrat.me)

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
