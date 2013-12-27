(ns pos-tagger.hmm
  (:require [clojure.string :as str]
            [clojure.core.match :refer [match]]))

(defn read-brown-counts
  [counts-file]
  (with-open [rdr (clojure.java.io/reader counts-file)]
    (reduce (fn [[ngram-counts word-tag-counts] line]
              (let [split-line (str/split line #"\s")]
                (match split-line
                       [c "3-GRAM" t u v] [(assoc ngram-counts [t u v] (Integer/parseInt c))
                                           word-tag-counts]
                       [c "2-GRAM" t u]   [(assoc ngram-counts [t u] (Integer/parseInt c))
                                           word-tag-counts]
                       [c "1-GRAM" t]     [(assoc ngram-counts [t] (Integer/parseInt c))
                                           word-tag-counts]
                       [c "WORDTAG" t word] [ngram-counts
                                             (assoc-in word-tag-counts [t word] (Integer/parseInt c))])))
            [{} {}]
            (line-seq rdr))))

(let [counts (read-brown-counts "brown.counts")]
  (def ngram-count (first counts))
  (def word-tag-count (second counts)))

(defn transition-probability
  [ngram]
  (/ (ngram-count ngram)
     (ngram-count (butlast ngram))))

(defn emission-probability
  "Probability of tag emitting word."
  [tag word]
  (/ (get-in word-tag-count [tag word])
     (apply + (vals (word-tag-count tag)))))
