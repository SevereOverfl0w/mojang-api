(ns mojang-api.core
  (:require [clojure.data.json :as json]
            [clj-http.client :as client]))

(defn unwrap
  "Takes a clj-http.client request, and decodes the JSON body"
  [request]
  (json/read-str (:body request)
                 :key-fn keyword))

(defn username->uuid
  "
  Takes a username, and returns the id and current username.

  Example usage:
  (username->uuid \"SevereOverfl0w\") => {:id \"f9001cdd2fe748119d1a86b8eb53d293\", :name \"SevereOverfl0w\"}
  "
  [username & {:keys [timestamp]}]
  (unwrap (client/get (str "https://api.mojang.com/users/profiles/minecraft/"
                   username
                   (when timestamp (str "?at=" timestamp))))))

(defn uuid->name-history
  "
  Takes a uuid, and returns the history for it.

  Example usage:
  (uuid->name-history \"9a984ee9527e40d1aba4fbe70b5fb830\") => [{:name \"Quaffee\"} {:name \"Apology\", :changedToAt 1423295345000} {:name \"Gold\", :changedToAt 1439116188000}]
  "
  [uuid]
  (unwrap (client/get (str  "https://api.mojang.com/user/profiles/" uuid "/names"))))

(defn playernames->uuids
  "
  Takes a list of playernames, and returns a list of uuids & extas.

  Example usage:
  (playernames->uuids [:macreddin \"SevereOverfl0w\"]) => [{:id \"9cea25bf8eb449259c3f4c075deb1124\", :name \"macreddin\", :legacy true} {:id \"f9001cdd2fe748119d1a86b8eb53d293\", :name \"SevereOverfl0w\"}]
  "
  [playernames]
  (unwrap (client/post "https://api.mojang.com/profiles/minecraft"
                       {:body (json/write-str playernames)
                        :content-type :json})))

(defn uuid->profile
  "
  Takes a uuid, and returns the player's username plus additional information (such as skins)

  Example usage:
  (uuid->profile \"9cea25bf8eb449259c3f4c075deb1124\") => {:id \"9cea25bf8eb449259c3f4c075deb1124\", :name \"macreddin\", :properties [{:name \"textures\", :value \"eyJ0aW1lc3RhbXAiOjE0NDExNTg5NjkzNjYsInByb2ZpbGVJZCI6IjljZWEyNWJmOGViNDQ5MjU5YzNmNGMwNzVkZWIxMTI0IiwicHJvZmlsZU5hbWUiOiJtYWNyZWRkaW4iLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFmZWY4ZWYyODc5NTY1ZTRkOGFjYzUwOGE4OTIxNTM2ZDE0NzRhODBlNGY5ODdlZWRiNGRjMmY2Yjc3YTU5In0sIkNBUEUiOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS85NWEyZDJkOTQ5NDI5NjZmNzQzYjg0ZTRjMjYyNjMxOTc4MjUzOTc5ZGI2NzNjMmZiY2MyN2RjM2QyZGNjN2E3In19fQ==\"}], :legacy true}
  "
  [uuid]
  (unwrap (client/get (str "https://sessionserver.mojang.com/session/minecraft/profile/" uuid))))
