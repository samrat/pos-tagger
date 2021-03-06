(ns pos-tagger.brown-counts
  (:require [clojure.string :as str]))

(defn line->vec
  "Splits a line into words."
  [l]
  (-> l
      str/trim
      str/lower-case
      (str/split #"\s")))

(defn separate-word-tag
  "Separate the word from its tag"
  [word-tag]
  (let [rev (str/reverse word-tag)
        rev-split (str/split rev #"/" 2)]
    (when (= (count rev-split) 2)
      (let [[w t] rev-split]
        (str (str/reverse t) " " (str/reverse w))))))

(defn format-sentence
  "One word/tag pair on each line"
  [sentence]
  (apply str
         (conj (interleave sentence (repeat "\n"))
               "\n")))

(defn process-brown
  [file out]
  (with-open [rdr (clojure.java.io/reader file)
              out (clojure.java.io/writer out :append true)]
    (doseq [l (line-seq rdr)]
      (when-not (empty? l)
        (.write out (format-sentence
                     (map separate-word-tag (line->vec l))))))))

(defn process-dir
  "dir is the directory containing the Brown corpus"
  [dir out]
  (doseq [f (filter #(re-find #"c[a-z]\d{2}" (.getPath %))
                    (file-seq (clojure.java.io/file "brown")))]
    (process-brown f out)))

(comment (process-dir "brown" "brown.train"))

(defn read-train-file
  [train-file]
  (->> (str/split (slurp train-file) #"\n\n")
       (map #(str/split % #"\n"))
       (map (fn [sentence]
              (map #(str/split % #"\s") sentence)))))

(defn word-tags
  [train-file]
  (frequencies (apply concat (read-train-file train-file))))

(defn write-word-tags
  [train-file out]
  (with-open [wri (clojure.java.io/writer out)]
    (doseq [word (word-tags train-file)]
      (let [[[w t] c] word]
        (when-not (some empty? [w t])
          (.write wri (str c " WORDTAG " t " " w "\n")))))))

(defn ngrams
  [n train-file]
  (->> (read-train-file train-file)
       (map (fn [sentence]
              (partition n 1 (remove nil?
                                     (concat ["START" "START"]
                                             (map second sentence))))))
       (apply concat)
       (frequencies)))

(defn write-ngrams
  "Appends ngram counts to file. To be called after write-word-tags,
  which does not append."
  [train-file out]
  (with-open [wri (clojure.java.io/writer out :append true)]
    (doseq [n [1 2 3]
            [ngram c] (ngrams n train-file)]
      (when (every? #(not (empty? %)) ngram)
        (.write wri (str c " " n "-GRAM "
                         (apply str (interleave ngram (repeat " ")))
                         "\n"))))))

(comment (write-word-tags "brown.train" "brown.counts")
         (write-ngrams "brown.train" "brown.counts"))

(defn make-counts-file
  [brown-dir counts-file]
  (do (process-dir brown-dir "brown.train")
      (write-word-tags "brown.train" counts-file)
      (write-ngrams "brown.train" counts-file)))
