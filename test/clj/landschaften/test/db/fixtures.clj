(ns landschaften.test.db.fixtures)

;; i.e. (with-model-concepts (first (retrieve-wga-concepts)))
(def a-wga-concept-row-with-general-concepts
  {:date "1861-62",
   :school "Italian",
   :type "landscape",
   :title "Cloister",
   :author "ABBATI, Giuseppe",
   :created_on [org.joda.time.DateTime
                1626028696
                "2018-07-22T22:14:59.000Z"],
   :concepts [{:name "no person", :value 0.9888144}
              {:name "people", :value 0.93722683}
              {:name "building", :value 0.91260886}
              {:name "painting", :value 0.8736326}
              {:name "home", :value 0.86515564}
              {:name "one", :value 0.8387195}
              {:name "adult", :value 0.8206382}
              {:name "action", :value 0.8097817}
              {:name "road", :value 0.8061619}
              {:name "military", :value 0.8045062}
              {:name "industry", :value 0.8003503}
              {:name "vehicle", :value 0.7901014}
              {:name "two", :value 0.7838442}
              {:name "architecture", :value 0.77139056}
              {:name "wear", :value 0.75956225}
              {:name "battle", :value 0.74638677}
              {:name "mine", :value 0.7370186}
              {:name "war", :value 0.73574686}
              {:name "offense", :value 0.7235819}
              {:name "mammal", :value 0.7179329}],
   :id 6,
   :timeframe "1851-1900",
   :form "painting",
   :jpg "https://www.wga.hu/art/a/abbati/abbati3.jpg"})

;; i.e. (with-model-concepts (take 10 (retrieve-wga-concepts)))
(def wga-concept-rows-with-general-concepts
 '({:date "1861-62",
    :school "Italian",
    :type "landscape",
    :title "Cloister",
    :author "ABBATI, Giuseppe",
    :created_on [org.joda.time.DateTime
                 1457651899
                 "2018-07-22T22:14:59.000Z"],
    :concepts [{:name "no person", :value 0.9888144}
               {:name "people", :value 0.93722683}
               {:name "building", :value 0.91260886}
               {:name "painting", :value 0.8736326}
               {:name "home", :value 0.86515564}
               {:name "one", :value 0.8387195}
               {:name "adult", :value 0.8206382}
               {:name "action", :value 0.8097817}
               {:name "road", :value 0.8061619}
               {:name "military", :value 0.8045062}
               {:name "industry", :value 0.8003503}
               {:name "vehicle", :value 0.7901014}
               {:name "two", :value 0.7838442}
               {:name "architecture", :value 0.77139056}
               {:name "wear", :value 0.75956225}
               {:name "battle", :value 0.74638677}
               {:name "mine", :value 0.7370186}
               {:name "war", :value 0.73574686}
               {:name "offense", :value 0.7235819}
               {:name "mammal", :value 0.7179329}],
    :id 6,
    :timeframe "1851-1900",
    :form "painting",
    :jpg "https://www.wga.hu/art/a/abbati/abbati3.jpg"}
   {:date "1881",
    :school "Other",
    :type "landscape",
    :title "Winter at Barbizon",
    :author "ANDREESCU, Ion",
    :created_on [org.joda.time.DateTime
                 504082169
                 "2018-07-22T22:14:59.000Z"],
    :concepts [{:name "winter", :value 0.99890256}
               {:name "snow", :value 0.99847496}
               {:name "cold", :value 0.988321}
               {:name "frost", :value 0.97653663}
               {:name "no person", :value 0.9640409}
               {:name "tree", :value 0.96242154}
               {:name "weather", :value 0.95476747}
               {:name "ice", :value 0.9473209}
               {:name "landscape", :value 0.93789524}
               {:name "storm", :value 0.9375764}
               {:name "outdoors", :value 0.9342598}
               {:name "old", :value 0.93266684}
               {:name "frozen", :value 0.9307661}
               {:name "house", :value 0.9290394}
               {:name "nature", :value 0.9195694}
               {:name "travel", :value 0.9028368}
               {:name "fence", :value 0.8942394}
               {:name "abandoned", :value 0.89370203}
               {:name "snowstorm", :value 0.8804929}
               {:name "architecture", :value 0.8778517}],
    :id 33,
    :timeframe "1851-1900",
    :form "painting",
    :jpg "https://www.wga.hu/art/a/andreesc/winter.jpg"}
   {:date "c. 1648",
    :school "Dutch",
    :type "landscape",
    :title "Italianate Landscape with a River and an Arched Bridge",
    :author "ASSELYN, Jan",
    :created_on [org.joda.time.DateTime
                 132135161
                 "2018-07-22T22:14:59.000Z"],
    :concepts [{:name "people", :value 0.9971066}
               {:name "adult", :value 0.9914385}
               {:name "no person", :value 0.9870113}
               {:name "group", :value 0.98438156}
               {:name "painting", :value 0.983824}
               {:name "art", :value 0.98263466}
               {:name "print", :value 0.9619497}
               {:name "cavalry", :value 0.9590912}
               {:name "mammal", :value 0.95071113}
               {:name "architecture", :value 0.93397367}
               {:name "wear", :value 0.9316756}
               {:name "home", :value 0.9161623}
               {:name "man", :value 0.90831864}
               {:name "religion", :value 0.90467}
               {:name "building", :value 0.89258784}
               {:name "travel", :value 0.88926005}
               {:name "bedrock", :value 0.88288474}
               {:name "vehicle", :value 0.87916434}
               {:name "outdoors", :value 0.87432796}
               {:name "two", :value 0.86072487}],
    :id 65,
    :timeframe "1601-1650",
    :form "painting",
    :jpg "https://www.wga.hu/art/a/asselyn/italiana.jpg"}
   {:date "1666",
    :school "Dutch",
    :type "landscape",
    :title "View of Amsterdam with Ships on the Ij",
    :author "BACKHUYSEN, Ludolf",
    :created_on [org.joda.time.DateTime
                 9517352
                 "2018-07-22T22:14:59.000Z"],
    :concepts [{:name "water", :value 0.9880936}
               {:name "watercraft", :value 0.98042107}
               {:name "vehicle", :value 0.9765166}
               {:name "smoke", :value 0.97584885}
               {:name "no person", :value 0.96929896}
               {:name "calamity", :value 0.96754336}
               {:name "ship", :value 0.96298933}
               {:name "storm", :value 0.9618136}
               {:name "transportation system", :value 0.9605753}
               {:name "dawn", :value 0.94281983}
               {:name "sea", :value 0.94122434}
               {:name "ocean", :value 0.93057346}
               {:name "flame", :value 0.92397594}
               {:name "wind", :value 0.9144376}
               {:name "landscape", :value 0.90612817}
               {:name "sunset", :value 0.90424126}
               {:name "river", :value 0.89562774}
               {:name "travel", :value 0.8682114}
               {:name "beach", :value 0.86083925}
               {:name "weather", :value 0.8607013}],
    :id 103,
    :timeframe "1651-1700",
    :form "painting",
    :jpg "https://www.wga.hu/art/b/backhuys/view_ams.jpg"}
   {:date "1868",
    :school "French",
    :type "landscape",
    :title "View of the Village",
    :author "BAZILLE, Jean-Fr_d_ric",
    :created_on [org.joda.time.DateTime
                 1185945177
                 "2018-07-22T22:15:08.000Z"],
    :concepts [{:name "outdoors", :value 0.97836554}
               {:name "nature", :value 0.97684836}
               {:name "tree", :value 0.9568285}
               {:name "beautiful", :value 0.9484276}
               {:name "people", :value 0.94331884}
               {:name "leisure", :value 0.93579733}
               {:name "summer", :value 0.92987597}
               {:name "park", :value 0.9195421}
               {:name "one", :value 0.9141115}
               {:name "travel", :value 0.9118078}
               {:name "girl", :value 0.9084087}
               {:name "woman", :value 0.9065883}
               {:name "fall", :value 0.88403904}
               {:name "religion", :value 0.87937284}
               {:name "adult", :value 0.8791041}
               {:name "sky", :value 0.8636495}
               {:name "grass", :value 0.8528605}
               {:name "wood", :value 0.84386927}
               {:name "outside", :value 0.84203315}
               {:name "art", :value 0.8403818}],
    :id 127,
    :timeframe "1851-1900",
    :form "painting",
    :jpg "https://www.wga.hu/art/b/bazille/06villag.jpg"}
   {:date "1744",
    :school "Italian",
    :type "landscape",
    :title "View of Gazzada near Varese",
    :author "BELLOTTO, Bernardo",
    :created_on [org.joda.time.DateTime
                 84870160
                 "2018-07-22T22:14:59.000Z"],
    :concepts [{:name "architecture", :value 0.9860444}
               {:name "travel", :value 0.9748821}
               {:name "no person", :value 0.97071385}
               {:name "building", :value 0.9703387}
               {:name "old", :value 0.95257074}
               {:name "city", :value 0.9524727}
               {:name "house", :value 0.9465499}
               {:name "town", :value 0.9435768}
               {:name "church", :value 0.93659997}
               {:name "tree", :value 0.93466496}
               {:name "outdoors", :value 0.93171805}
               {:name "home", :value 0.92714524}
               {:name "ancient", :value 0.9255544}
               {:name "landscape", :value 0.9245631}
               {:name "religion", :value 0.9237393}
               {:name "sky", :value 0.9159529}
               {:name "tourism", :value 0.89982665}
               {:name "castle", :value 0.8928462}
               {:name "tower", :value 0.8786728}
               {:name "hill", :value 0.8773894}],
    :id 157,
    :timeframe "1751-1800",
    :form "painting",
    :jpg "https://www.wga.hu/art/b/bellotto/1/bello107.jpg"}
   {:date "1759-60",
    :school "Italian",
    :type "landscape",
    :title "Vienna, Panorama from Palais Kaunitz (detail)",
    :author "BELLOTTO, Bernardo",
    :created_on [org.joda.time.DateTime
                 61785016
                 "2018-07-22T22:15:08.000Z"],
    :concepts [{:name "people", :value 0.99743855}
               {:name "group", :value 0.9909292}
               {:name "adult", :value 0.97592634}
               {:name "travel", :value 0.948683}
               {:name "many", :value 0.9426423}
               {:name "religion", :value 0.9412153}
               {:name "man", :value 0.9329335}
               {:name "woman", :value 0.93073237}
               {:name "art", :value 0.9245392}
               {:name "wear", :value 0.9002374}
               {:name "home", :value 0.88268155}
               {:name "vehicle", :value 0.8748741}
               {:name "two", :value 0.87200093}
               {:name "military", :value 0.8639743}
               {:name "architecture", :value 0.8581039}
               {:name "three", :value 0.8573965}
               {:name "outdoors", :value 0.8486011}
               {:name "temple", :value 0.822764}
               {:name "one", :value 0.8202104}
               {:name "print", :value 0.8179546}],
    :id 181,
    :timeframe "1751-1800",
    :form "painting",
    :jpg "https://www.wga.hu/art/b/bellotto/3/bello3021.jpg"}
   {:date "c. 1650",
    :school "Dutch",
    :type "landscape",
    :title "Return from the Falcon Hunt",
    :author "BERCHEM, Nicolaes",
    :created_on [org.joda.time.DateTime
                 184541852
                 "2018-07-22T22:15:08.000Z"],
    :concepts [{:name "no person", :value 0.9962325}
               {:name "travel", :value 0.9604747}
               {:name "people", :value 0.9598855}
               {:name "painting", :value 0.95870304}
               {:name "water", :value 0.9496825}
               {:name "rock", :value 0.9480589}
               {:name "group", :value 0.9476403}
               {:name "outdoors", :value 0.9454174}
               {:name "mammal", :value 0.9379796}
               {:name "recreation", :value 0.93731534}
               {:name "cave", :value 0.9345502}
               {:name "adult", :value 0.93366456}
               {:name "landscape", :value 0.9303421}
               {:name "mountain", :value 0.8844762}
               {:name "art", :value 0.85783076}
               {:name "one", :value 0.85662436}
               {:name "two", :value 0.8553585}
               {:name "daylight", :value 0.8549762}
               {:name "seashore", :value 0.8459116}
               {:name "exploration", :value 0.78809774}],
    :id 214,
    :timeframe "1651-1700",
    :form "painting",
    :jpg "https://www.wga.hu/art/b/berchem/returnfa.jpg"}
   {:date "1878",
    :school "Spanish",
    :type "landscape",
    :title "The Banks of the Manzanares River",
    :author "BERUETE, Aureliano",
    :created_on [org.joda.time.DateTime
                 1278951236
                 "2018-07-22T22:14:59.000Z"],
    :concepts [{:name "tree", :value 0.9964956}
               {:name "landscape", :value 0.9924129}
               {:name "no person", :value 0.9729613}
               {:name "outdoors", :value 0.965922}
               {:name "nature", :value 0.9583609}
               {:name "field", :value 0.95059884}
               {:name "daylight", :value 0.947664}
               {:name "grass", :value 0.9453794}
               {:name "environment", :value 0.93601745}
               {:name "wood", :value 0.9350527}
               {:name "agriculture", :value 0.92945844}
               {:name "sky", :value 0.9289676}
               {:name "flora", :value 0.91326576}
               {:name "branch", :value 0.91133654}
               {:name "farm", :value 0.90583813}
               {:name "weather", :value 0.90517145}
               {:name "season", :value 0.9041977}
               {:name "countryside", :value 0.9007134}
               {:name "hayfield", :value 0.89570045}
               {:name "scenic", :value 0.8945366}],
    :id 240,
    :timeframe "1851-1900",
    :form "painting",
    :jpg "https://www.wga.hu/art/b/beruete/manzana1.jpg"}
   {:date "1864",
    :school "American",
    :type "landscape",
    :title "Yosemite Valley at Sunset",
    :author "BIERSTADT, Albert",
    :created_on [org.joda.time.DateTime
                 837389655
                 "2018-07-22T22:15:08.000Z"],
    :concepts [{:name "tree", :value 0.9887403}
               {:name "no person", :value 0.98413384}
               {:name "landscape", :value 0.9838584}
               {:name "fall", :value 0.9761846}
               {:name "fog", :value 0.97488713}
               {:name "wood", :value 0.97098666}
               {:name "dawn", :value 0.96394515}
               {:name "mist", :value 0.96103096}
               {:name "nature", :value 0.9544077}
               {:name "outdoors", :value 0.9363743}
               {:name "water", :value 0.93210244}
               {:name "park", :value 0.92536205}
               {:name "travel", :value 0.90933883}
               {:name "leaf", :value 0.89261305}
               {:name "lake", :value 0.88897973}
               {:name "scenic", :value 0.8704938}
               {:name "winter", :value 0.86994493}
               {:name "light", :value 0.86924964}
               {:name "river", :value 0.86176157}
               {:name "mountain", :value 0.8599278}],
    :id 256,
    :timeframe "1851-1900",
    :form "painting",
    :jpg "https://www.wga.hu/art/b/bierstad/yosemite.jpg"}))
