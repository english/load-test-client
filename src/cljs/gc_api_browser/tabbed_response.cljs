(ns gc-api-browser.tabbed-response
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:import [goog.format JsonPrettyPrinter]))

(defn render-header [[header-name header-value]]
  (dom/div #js {:className "flex-container headers__header u-direction-row"}
           (dom/div #js {:className "flex-item headers__header__name"}
                    (dom/input #js {:className "input"
                                    :disabled  true
                                    :value     header-name}))
           (dom/div #js {:className "flex-item headers__header__value"}
                    (dom/input #js {:className "input"
                                    :disabled  true
                                    :value     header-value}))))

(defn render-headers [headers]
  (apply dom/div #js {:className "tabbed-response__headers headers"}
         (map render-header headers)))

(defn render-body [body]
  (let [json-string (.format (JsonPrettyPrinter.) (clj->js body))]
    (dom/pre #js {:className "tabbed-response__body"}
             (dom/code nil json-string))))

(defn component [cursor owner]
  (reify
    om/IRender
    (render [_]
      (let [showing-headers? (om/get-state owner :showing-headers?)
            {:keys [headers body]} cursor]
        (dom/div #js {:className "tabbed-response"}
                 (dom/h2 #js {:className "tabbed-response__header"} "Response")
                 (dom/div #js {:className "tabbed-response__inner"}
                          (dom/div #js {:className "tabs"}
                                   (dom/button #js {:className (str "tab-item" (when-not showing-headers? " tab-item--active"))
                                                    :onClick   #(om/set-state! owner :showing-headers? false)}
                                               "Body")
                                   (dom/button #js {:className (str "tab-item" (when showing-headers? " tab-item--active"))
                                                    :onClick   #(om/set-state! owner :showing-headers? true)}
                                               "Headers"))
                          (if showing-headers?
                            (render-headers headers)
                            (render-body body))))))))
