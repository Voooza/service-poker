(ns service-poker.core
  (:require [clj-http.client :as c]
            [clojure.pprint :as pp]
            [cheshire.core :refer :all]
            [clojure.java.io :as io]
            [clojure.data.json :as json])
  (:gen-class))

(defn execute
  [context]
  (pp/pprint context)
  (let [{:keys [url options fun]} context
        response (fun url options)
        body (:body response)]
    {:context context
     :response response
     :response-body body}))

(defn write-out
  [data base]
  (let [date (str "sp_" (.format (java.text.SimpleDateFormat. "yyyy_MM_dd_HH_mm_ss")
                                 (java.util.Date.)))
        mydir (io/file base date)]
    (.mkdir mydir)
    (pp/pprint (:context data) (io/writer (io/file mydir "request.txt")))
    (spit (io/file mydir "response_body.json")
          (with-out-str (json/pprint (json/read-str (:response-body data)))))
    (pp/pprint (:response data) (io/writer (io/file mydir "response.txt")))
    (json/pprint (json/read-str (:response-body data)))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (write-out
   (execute (load-file (first args)))
   (second args)))

