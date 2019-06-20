(def sample-paintings
  #{{:date      "c. 1469",
     :school    "Italian",
     :type      "portrait",
     :title     "Portrait of a Young Man",
     :author    "BOTTICELLI, Sandro",
     :concepts  #{{:name "gown (clothing)", :value 0.90708596}
                  {:name "one", :value 0.986664}
                  {:name "cape", :value 0.87464726}
                  {:name "adult", :value 0.98579407}
                  {:name "side view", :value 0.8062773}
                  {:name "religion", :value 0.93637943}
                  {:name "sculpture", :value 0.86673677}
                  {:name "lid", :value 0.9411217}
                  {:name "people", :value 0.9946501}
                  {:name "painting", :value 0.9754119}
                  {:name "wear", :value 0.95125747}
                  {:name "portrait", :value 0.9801239}
                  {:name "facial expression", :value 0.8723508}
                  {:name "man", :value 0.9584564}
                  {:name "veil", :value 0.96336377}
                  {:name "facial hair", :value 0.8060329}
                  {:name "woman", :value 0.874543}
                  {:name "illustration", :value 0.8150852}
                  {:name "art", :value 0.96110016}
                  {:name "leader", :value 0.8733945}},
     :id        5623,
     :timeframe "1451-1500",
     :form      "painting",
     :jpg       "https://www.wga.hu/art/b/botticel/7portrai/01youngm.jpg"}
    {:date      "c. 1483",
     :school    "Italian",
     :type      "portrait",
     :title     "Portrait of a Young Man",
     :author    "BOTTICELLI, Sandro",
     :concepts  #{{:name "one", :value 0.99197435}
                  {:name "adult", :value 0.98972064}
                  {:name "side view", :value 0.86024535}
                  {:name "religion", :value 0.7984845}
                  {:name "jewelry", :value 0.8666209}
                  {:name "print", :value 0.8635602}
                  {:name "lid", :value 0.94501436}
                  {:name "outerwear", :value 0.9058312}
                  {:name "necklace", :value 0.92569244}
                  {:name "people", :value 0.9990963}
                  {:name "painting", :value 0.94927025}
                  {:name "wear", :value 0.9762198}
                  {:name "portrait", :value 0.9976093}
                  {:name "man", :value 0.94694376}
                  {:name "veil", :value 0.96435404}
                  {:name "profile", :value 0.8191407}
                  {:name "facial hair", :value 0.88426876}
                  {:name "jacket", :value 0.86635554}
                  {:name "art", :value 0.9661212}
                  {:name "leader", :value 0.96634877}},
     :id        5632,
     :timeframe "1451-1500",
     :form      "painting",
     :jpg       "https://www.wga.hu/art/b/botticel/7portrai/10youngm.jpg"}})


(def sample-group
  {:group-name          "spanish-religious"
   :paintings           sample-paintings
   :type-constraints    #{"religious"}
   :school-constraints  #{"Spanish"}
   :concept-constraints #{"religion"}})