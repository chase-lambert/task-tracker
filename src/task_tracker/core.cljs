(ns task-tracker.core
    (:require
      [reagent.core :as r]
      [reagent.dom :as d]
      ["react-icons/fa6" :refer [FaXmark]]))

(defn on-change [v]
  (fn [e]
    (reset! v (-> e .-target .-value))))
    
(defonce my-tasks
  (r/atom 
    {1 {:id 1 
        :text "Doctor's Appointment"
        :day "Feb 5th at 2:30pm"
        :reminder true}
     2 {:id 2 
        :text "Meeting at School"
        :day "Feb 6th at 1:30pm"
        :reminder true}
     3 {:id 3 
        :text "Grocery shopping"
        :day "Feb 6th at 4:30pm"
        :reminder false}}))

(defn delete-task [id]
  (swap! my-tasks dissoc id))

(defn toggle-reminder [id]
  (swap! my-tasks update-in [id :reminder] not))

(defn add-task [{:keys [id text day reminder]}]
  (swap! my-tasks assoc id {:id id 
                            :text text
                            :day day
                            :reminder reminder}))

(defn button [{:keys [color text on-click] :or {color "steelblue"}}]
  [:button.btn {:style {:background-color color}
                :on-click on-click}
    text])

(defn add-task-form []
  (fn []
    (let [text (r/atom "")
          day (r/atom "")
          reminder (r/atom false)]
      [:form.add.form
       [:div.form-control
        [:label "Task"]
        [:input {:type "text"
                 :placeholder "Add Task"
                 :value @text
                 :on-change (fn [e] 
                              (reset! text (-> e .-target .-value)))}]]
       [:div.form-control
        [:label "Day & Time"]
        [:input {:type "text"
                 :placeholder "Add Day & Time"
                 :value @day
                 :on-change #(on-change day)}]]
       [:div.form-control.form-control-check
        [:label "Set Reminder?"]
        [:input {:type "checkbox"
                 :value @reminder
                 :on-change (swap! reminder not)}]]
       [:input.btn.btn-block {:type "submit"
                              :value "Save Task"}]])))

(defn header [{:keys [title] :or {title "Task Tracker"}}]
  [:header.header
   [:h1 title]
   [button {:color "green"
            :text "Add"
            :on-click #(js/console.log "Clicked")}]])

(defn task [{:keys [day id text reminder]}]
  [:div.task {:class (when reminder "reminder")
              :on-double-click #(toggle-reminder id)}
   [:h3 text [:> FaXmark {:style {:color "red"
                                  :cursor "pointer"}
                          :on-click #(delete-task id)}]]
   [:p day]])

(defn tasks []
  [:<>
   (for [[_ v] @my-tasks]
     ^{:key (:id v)}
     [task v])])
   


(defn app []
  [:div.container
   [header]
   [add-task-form]
   (if (seq @my-tasks)
    [tasks]
    [:div "No tasks to complete"])])

(defn mount-root []
  (d/render [app] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
