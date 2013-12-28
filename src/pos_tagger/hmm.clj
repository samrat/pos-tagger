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
  (def word-tag-count (second counts))
  (def tag-space (conj (keys word-tag-count) "*")))

(defn transition-probability
  [ngram]
  (try (/ (ngram-count ngram)
          (ngram-count (butlast ngram)))
       (catch Exception _ 0)))

(defn emission-probability
  "Probability of tag emitting word."
  [[tag word]]
  (try (/ (get-in word-tag-count [tag word])
          (apply + (vals (word-tag-count tag))))
       (catch Exception _ 0)))

(def viterbi
  "Returns the probability of the most likely tag sequence upto posn."
  (memoize
   (fn [sentence tag posn]
     (if (= posn 1)
       (* (emission-probability [tag (first sentence)])
          (transition-probability ["*" tag]))
       (* (emission-probability [tag (nth sentence (dec posn))])
          (apply max (map (fn [prev-tag]
                            (* (transition-probability [prev-tag tag])
                               (viterbi sentence prev-tag (dec posn))))
                          tag-space)))))))

(defn viterbi-tag
  "Return the most likely tag for word at posn."
  [sentence posn]
  (apply max-key (fn [tag] (viterbi sentence tag posn)) tag-space))

(defn tag-sequence
  [sentence]
  (map (partial viterbi-tag sentence)
       (range 1 (inc (count sentence)))))
