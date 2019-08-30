(ns cubesat-clj.handler
  "Main ring app handler that routes all requests. Generates API
  docs with Swagger UI, available at root url."
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [cubesat-clj.telemetry.telemetry-protocol :as rockblock]
            [cubesat-clj.telemetry.telemetry-handler :as telemetry]
            [schema.core :as s]))

(def app
  (api
    {:swagger
     {:ui "/"
      :spec "/swagger.json"
      :data {:info {:title "Cubesat Ground"
                    :description "Alpha Cubesat Ground System"}
             :tags [{:name "API", :description "Satellite control/data API"}
                    {:name "Telemetry", :description "Rockblock web services telemetry endpoint"}]}}}

    (context "/telemetry" []
      :tags ["Telemetry"]

      (GET "/ping" []
        :return s/Str
        :summary "Test the API"
        (ok "pong"))

      (POST "/rockblock" []
        :return nil
        :summary "Receive data from rockblock web services"
        :body [report rockblock/RockblockReport]
        (telemetry/handle-report! report)))

    (context "/api" []
      :tags ["API"]

      (POST "/control" [] ;;TODO
        :return s/Str
        :summary "Process a command to be sent to cubesat. TODO implement"
        :body [input {:value s/Str}]
        (ok (:value input))))))
