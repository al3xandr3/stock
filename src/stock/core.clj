(ns stock.core
  (:import [java.net URLEncoder])
  (:require [clojure.data.json :as json]
            [postal.core])
  (:gen-class))

;;;;; YQL QUERY

; http://query.yahooapis.com/v1/public/yql?q=SELECT%20*%20FROM%20yahoo.finance.oquote%20WHERE%20symbol%3D'MSFT'&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=cbfunc
(defn uri-encode [uri]
  (URLEncoder/encode uri "utf-8"))

; YQL
; http://query.yahooapis.com/v1/public/yql?q=SELECT%20*%20FROM%20yahoo.finance.oquote%20WHERE%20symbol%3D'MSFT'&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=cbfunc
(defn get-yql [yql]
  (let [api-url (str "http://query.yahooapis.com/v1/public/yql?q="
             (uri-encode yql)
             "&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys")
        fetch (slurp api-url)
        dta (json/read-str fetch)
        ]
  dta))
; (get-yql "SELECT * FROM yahoo.finance.oquote WHERE symbol='MSFT'")


;;;;; STOCK
(defn get-price [stock]
  (let [dta (get-yql (str "SELECT * FROM yahoo.finance.oquote WHERE symbol='" stock "'"))
        value (-> dta
                  (get "query")
                  (get "results")
                  (get "option")
                  (get "price")
                  )]
    (read-string value)))
;(get-price "MSFT")


;;;; MAIL
(defn send-mail [server mail]
  (postal.core/send-message (with-meta mail server)))

(defn -main []
  (let [config (read-string (slurp "stockConfig.txt"))
        symb   (-> config :stock :symbol)
        price  (get-price symb)
        low    (read-string (-> config :stock :low))
        high   (read-string (-> config :stock :high))
        server (-> config :mail-server)
        mail   (-> config :mail-send)
        content (clojure.string/replace (mail :body) #"\{price\}" (str price))
        subject (clojure.string/replace (mail :subject) #"\{price\}" (str price))
        body   [{:type "text/html" :content content}]
        amail  (assoc mail :body body :subject subject)
        ]

    ; when out of predefined tresholds, send email
    (if (or
       (< price low)
       (> price high))
    (send-mail server amail))

    ; log current value
    (spit "currentValue.txt" price)
    ))
;;(-main)


;; Example of creating the config file
;; Spit out this to regenerate a new config
;; (def config
;;   {:mail-server
;;    {:host "smtp.domain.com"
;;     :port 587
;;     :user "user@domain.com"
;;     :pass "*******"
;;     :tls :yes}

;;    :mail-send
;;     {:from "user@domain.com"
;;      :to ["user@domain.com"]
;;      :subject "stock alert"
;;      :body "time to buy/sell?"}

;;    :stock
;;    {:symbol "MSFT"
;;     :low "31"
;;     :high "35"
;;     }
;;    })
;;(spit "stockConfig.txt" config)
