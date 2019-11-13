(ns landschaften.sample.manet)


(def manet-people-group-name "Manet's people")

(def manet-type-constraints #{})
(def manet-school-constraints #{"French"})
(def manet-timeframe-constraints #{"1801-1850", "1851-1900"})
(def manet-concept-constraints #{"people"})
(def manet-artist-constraints #{"MANET, Edouard"})

(def manet-sample-paintings
  #{{:date       "1862",
     :school     "French",
     :genre      "other",
     :wga_jpg    "https://www.wga.hu/art/m/manet/1/2spanis1.jpg",
     :title      "Spanish Ballet",
     :author     "MANET, Edouard",
     :created_on #inst "2018-12-21T17:35:10.000-00:00",
     :concepts
                 #{{:name "adult", :value 0.9926609}
                   {:name "dancing", :value 0.95042074}
                   {:name "costume", :value 0.8785554}
                   {:name "two", :value 0.8278563}
                   {:name "music", :value 0.9256652}
                   {:name "people", :value 0.99651086}
                   {:name "painting", :value 0.8622995}
                   {:name "performance", :value 0.91299504}
                   {:name "wear", :value 0.9742775}
                   {:name "man", :value 0.9371989}
                   {:name "veil", :value 0.924356}
                   {:name "group", :value 0.988762}
                   {:name "many", :value 0.8306803}
                   {:name "woman", :value 0.9813038}
                   {:name "outfit", :value 0.83809006}
                   {:name "dancer", :value 0.9224825}
                   {:name "theater", :value 0.83426315}
                   {:name "recreation", :value 0.8632289}
                   {:name "art", :value 0.86101604}
                   {:name "child", :value 0.8897608}},
     :id         24162,
     :timeframe  "1851-1900",
     :form       "painting",
     :jpg
                 "https://res.cloudinary.com/dgpqnl8ul/image/upload/v1546472422/vqutvlotsmipx7xkmxun.jpg"}
    {:date       "1862",
     :school     "French",
     :genre      "portrait",
     :wga_jpg    "https://www.wga.hu/art/m/manet/1/2spanis8.jpg",
     :title      "The Old Musician",
     :author     "MANET, Edouard",
     :created_on #inst "2018-12-21T17:25:31.000-00:00",
     :concepts
                 #{{:name "priest", :value 0.8639396}
                   {:name "adult", :value 0.99157315}
                   {:name "religion", :value 0.9748057}
                   {:name "print", :value 0.8651573}
                   {:name "lid", :value 0.9523072}
                   {:name "coat", :value 0.8577764}
                   {:name "outerwear", :value 0.9128848}
                   {:name "son", :value 0.9158189}
                   {:name "people", :value 0.99749196}
                   {:name "painting", :value 0.89659476}
                   {:name "wear", :value 0.9846792}
                   {:name "portrait", :value 0.8763036}
                   {:name "man", :value 0.9784485}
                   {:name "veil", :value 0.96348965}
                   {:name "group", :value 0.99090946}
                   {:name "facial hair", :value 0.88501525}
                   {:name "woman", :value 0.98325956}
                   {:name "art", :value 0.9184065}
                   {:name "three", :value 0.8912737}
                   {:name "child", :value 0.9239347}},
     :id         24169,
     :timeframe  "1851-1900",
     :form       "painting",
     :jpg
                 "https://res.cloudinary.com/dgpqnl8ul/image/upload/v1546472423/m3vwue4ql8zmyw10jw3k.jpg"}
    {:date       "1862",
     :school     "French",
     :genre      "other",
     :wga_jpg    "https://www.wga.hu/art/m/manet/1/3early2.jpg",
     :title      "Music in the Tuileries Gardens (detail)",
     :author     "MANET, Edouard",
     :created_on #inst "2018-12-21T17:35:20.000-00:00",
     :concepts
                 #{{:name "priest", :value 0.8843517}
                   {:name "banquet", :value 0.8813138}
                   {:name "adult", :value 0.94551253}
                   {:name "religion", :value 0.9926128}
                   {:name "crowd", :value 0.9564}
                   {:name "celebration", :value 0.94409996}
                   {:name "people", :value 0.9958187}
                   {:name "wear", :value 0.87971234}
                   {:name "man", :value 0.8914131}
                   {:name "group", :value 0.98950154}
                   {:name "prayer", :value 0.8437264}
                   {:name "many", :value 0.99210143}
                   {:name "woman", :value 0.9641658}
                   {:name "crucifixion", :value 0.83641267}
                   {:name "ceremony", :value 0.98226374}
                   {:name "Easter", :value 0.9773995}
                   {:name "festival", :value 0.858477}
                   {:name "cross", :value 0.830389}
                   {:name "leader", :value 0.89480937}
                   {:name "funeral", :value 0.86351913}},
     :id         24171,
     :timeframe  "1851-1900",
     :form       "painting",
     :jpg
                 "https://res.cloudinary.com/dgpqnl8ul/image/upload/v1546472425/l6e4punrvp3etn74zun8.jpg"}
    {:date       "1865-66",
     :school     "French",
     :genre      "other",
     :wga_jpg    "https://www.wga.hu/art/m/manet/2/2manet01.jpg",
     :title      "Bullfight",
     :author     "MANET, Edouard",
     :created_on #inst "2018-12-21T17:35:10.000-00:00",
     :concepts
                 #{{:name "mammal", :value 0.9926224}
                   {:name "racehorse", :value 0.9552147}
                   {:name "hurry", :value 0.9601132}
                   {:name "adult", :value 0.95615387}
                   {:name "race", :value 0.98657596}
                   {:name "jockey", :value 0.9815377}
                   {:name "crowd", :value 0.9723752}
                   {:name "cavalry", :value 0.9981413}
                   {:name "livestock", :value 0.9761847}
                   {:name "equestrian", :value 0.9525757}
                   {:name "people", :value 0.9984633}
                   {:name "action energy", :value 0.97289234}
                   {:name "man", :value 0.9494112}
                   {:name "horse", :value 0.9891174}
                   {:name "competition", :value 0.9896964}
                   {:name "cattle", :value 0.96983814}
                   {:name "group", :value 0.9905813}
                   {:name "many", :value 0.99030733}
                   {:name "seated", :value 0.99115324}
                   {:name "motion", :value 0.98737}},
     :id         24186,
     :timeframe  "1851-1900",
     :form       "painting",
     :jpg
                 "https://res.cloudinary.com/dgpqnl8ul/image/upload/v1546472429/lbs4bp3rbbrjft5zf6vq.jpg"}
    {:date       "1866",
     :school     "French",
     :genre      "portrait",
     :wga_jpg    "https://www.wga.hu/art/m/manet/2/2manet04.jpg",
     :title      "Young Lady (Woman with a Parrot)",
     :author     "MANET, Edouard",
     :created_on #inst "2018-12-21T17:25:42.000-00:00",
     :concepts
                 #{{:name "one", :value 0.97028434} {:name "model", :value 0.93446124}
                   {:name "adult", :value 0.98388004}
                   {:name "religion", :value 0.83296895}
                   {:name "jewelry", :value 0.8180838}
                   {:name "print", :value 0.75688535}
                   {:name "lid", :value 0.8737594}
                   {:name "outerwear", :value 0.7577708}
                   {:name "people", :value 0.98241544}
                   {:name "fashion", :value 0.9042506}
                   {:name "painting", :value 0.89103764}
                   {:name "performance", :value 0.77718914}
                   {:name "wear", :value 0.97824997}
                   {:name "portrait", :value 0.9477818}
                   {:name "man", :value 0.8805766}
                   {:name "veil", :value 0.91031575}
                   {:name "fashionable", :value 0.8561712}
                   {:name "woman", :value 0.9581425}
                   {:name "dress", :value 0.89266264}
                   {:name "art", :value 0.9495683}},
     :id         24189,
     :timeframe  "1851-1900",
     :form       "painting",
     :jpg
                 "https://res.cloudinary.com/dgpqnl8ul/image/upload/v1546472430/bas4fxrp6hh37j9iszak.jpg"}
    {:date       "1867",
     :school     "French",
     :genre      "genre",
     :wga_jpg    "https://www.wga.hu/art/m/manet/2/2manet11.jpg",
     :title      "The Guitar Player",
     :author     "MANET, Edouard",
     :created_on #inst "2018-12-21T17:23:29.000-00:00",
     :concepts
                 #{{:name "one", :value 0.99543536} {:name "adult", :value 0.99741757}
                   {:name "furniture", :value 0.93576956}
                   {:name "girl", :value 0.9304198}
                   {:name "seat", :value 0.9126075}
                   {:name "singer", :value 0.9263523}
                   {:name "two", :value 0.9186835}
                   {:name "music", :value 0.96700156}
                   {:name "people", :value 0.99657536}
                   {:name "painting", :value 0.97315586}
                   {:name "reclining", :value 0.9257157}
                   {:name "wear", :value 0.9945359}
                   {:name "portrait", :value 0.9816007}
                   {:name "actress", :value 0.933759}
                   {:name "facial expression", :value 0.95856774}
                   {:name "veil", :value 0.96556073}
                   {:name "musician", :value 0.93621033}
                   {:name "woman", :value 0.985641}
                   {:name "recreation", :value 0.9139211}
                   {:name "art", :value 0.95078254}},
     :id         24196,
     :timeframe  "1851-1900",
     :form       "painting",
     :jpg
                 "https://res.cloudinary.com/dgpqnl8ul/image/upload/v1546472432/ixevnvrrhfsvl2btqule.jpg"}
    {:date       "1868",
     :school     "French",
     :genre      "genre",
     :wga_jpg    "https://www.wga.hu/art/m/manet/2/2manet13.jpg",
     :title      "The Luncheon in the Studio",
     :author     "MANET, Edouard",
     :created_on #inst "2018-12-21T17:23:07.000-00:00",
     :concepts
                 #{{:name "one", :value 0.928209} {:name "adult", :value 0.9921365}
                   {:name "furniture", :value 0.9286638}
                   {:name "lid", :value 0.97724456}
                   {:name "two", :value 0.9651395}
                   {:name "royalty", :value 0.8792964}
                   {:name "commerce", :value 0.8866627}
                   {:name "several", :value 0.8716241}
                   {:name "people", :value 0.9982027}
                   {:name "painting", :value 0.89995265}
                   {:name "wear", :value 0.9909334}
                   {:name "four", :value 0.88462174}
                   {:name "portrait", :value 0.93528205}
                   {:name "man", :value 0.979193}
                   {:name "veil", :value 0.9725639}
                   {:name "group", :value 0.98707205}
                   {:name "woman", :value 0.9860933}
                   {:name "outfit", :value 0.9133043}
                   {:name "recreation", :value 0.89808434}
                   {:name "three", :value 0.93893814}},
     :id         24198,
     :timeframe  "1851-1900",
     :form       "painting",
     :jpg
                 "https://res.cloudinary.com/dgpqnl8ul/image/upload/v1546472433/rwywykjuns6crcgtrx1z.jpg"}
    {:date       "1868",
     :school     "French",
     :genre      "genre",
     :wga_jpg    "https://www.wga.hu/art/m/manet/2/2manet15.jpg",
     :title      "The Reading",
     :author     "MANET, Edouard",
     :created_on #inst "2018-12-21T17:23:29.000-00:00",
     :concepts
                 #{{:name "one", :value 0.9187814} {:name "adult", :value 0.9846325}
                   {:name "furniture", :value 0.8632232}
                   {:name "girl", :value 0.8502531}
                   {:name "seat", :value 0.71896744}
                   {:name "princess", :value 0.76299554}
                   {:name "two", :value 0.9359244}
                   {:name "people", :value 0.9865805}
                   {:name "painting", :value 0.7880821}
                   {:name "wear", :value 0.9162672}
                   {:name "beautiful", :value 0.84891796}
                   {:name "portrait", :value 0.86976075}
                   {:name "man", :value 0.89266723}
                   {:name "healthcare", :value 0.7076663}
                   {:name "room", :value 0.8991654}
                   {:name "indoors", :value 0.80724007}
                   {:name "woman", :value 0.98091006}
                   {:name "dress", :value 0.85546994}
                   {:name "art", :value 0.8825964}
                   {:name "sit", :value 0.7793608}},
     :id         24200,
     :timeframe  "1851-1900",
     :form       "painting",
     :jpg
                 "https://res.cloudinary.com/dgpqnl8ul/image/upload/v1546472435/oj8aefh3d7pfmffpusht.jpg"}
    {:date       "1874",
     :school     "French",
     :genre      "landscape",
     :wga_jpg    "https://www.wga.hu/art/m/manet/3/3manet13.jpg",
     :title      "Claude Monet Painting on His Boat-Studio in Argenteuil",
     :author     "MANET, Edouard",
     :created_on #inst "2018-12-21T17:26:40.000-00:00",
     :concepts
                 #{{:name "watercraft", :value 0.99094677}
                   {:name "one", :value 0.9455198}
                   {:name "rowboat", :value 0.92349803}
                   {:name "adult", :value 0.987556}
                   {:name "print", :value 0.92447}
                   {:name "two", :value 0.9623909}
                   {:name "sea", :value 0.908358}
                   {:name "people", :value 0.9969213}
                   {:name "painting", :value 0.98337245}
                   {:name "man", :value 0.94620585}
                   {:name "group", :value 0.9605703}
                   {:name "transportation system", :value 0.96333385}
                   {:name "water", :value 0.9794651}
                   {:name "vehicle", :value 0.9800147}
                   {:name "woman", :value 0.97150505}
                   {:name "illustration", :value 0.94317913}
                   {:name "travel", :value 0.9278274}
                   {:name "recreation", :value 0.94930357}
                   {:name "ship", :value 0.94353926}
                   {:name "art", :value 0.9695486}},
     :id         24218,
     :timeframe  "1851-1900",
     :form       "painting",
     :jpg
                 "https://res.cloudinary.com/dgpqnl8ul/image/upload/v1546472436/swsvuokxf891evo93p1b.jpg"}
    {:date       "1878-80",
     :school     "French",
     :genre      "genre",
     :wga_jpg    "https://www.wga.hu/art/m/manet/4/4manet07.jpg",
     :title      "Corner of a Café-Concert",
     :author     "MANET, Edouard",
     :created_on #inst "2018-12-21T17:23:29.000-00:00",
     :concepts
                 #{{:name "adult", :value 0.98452723}
                   {:name "religion", :value 0.9813553}
                   {:name "administration", :value 0.80553263}
                   {:name "two", :value 0.841617}
                   {:name "container", :value 0.81185424}
                   {:name "celebration", :value 0.8444092}
                   {:name "commerce", :value 0.8864105}
                   {:name "several", :value 0.86247987}
                   {:name "people", :value 0.99761593}
                   {:name "wear", :value 0.95024574}
                   {:name "elderly", :value 0.8193976}
                   {:name "man", :value 0.97192264}
                   {:name "veil", :value 0.8256639}
                   {:name "group", :value 0.9939004}
                   {:name "many", :value 0.9530274}
                   {:name "woman", :value 0.9748919}
                   {:name "interaction", :value 0.84736484}
                   {:name "ceremony", :value 0.8802029}
                   {:name "recreation", :value 0.80783606}
                   {:name "leader", :value 0.8523675}},
     :id         24233,
     :timeframe  "1851-1900",
     :form       "painting",
     :jpg
                 "https://res.cloudinary.com/dgpqnl8ul/image/upload/v1546472438/vxaqini8v50k8eazpzuq.jpg"}
    {:date       "1878",
     :school     "French",
     :genre      "genre",
     :wga_jpg    "https://www.wga.hu/art/m/manet/4/4manet10.jpg",
     :title      "Two Women Drinking Bocks",
     :author     "MANET, Edouard",
     :created_on #inst "2018-12-21T17:23:37.000-00:00",
     :concepts
                 #{{:name "one", :value 0.9047679} {:name "adult", :value 0.9140061}
                   {:name "food", :value 0.8745171}
                   {:name "lid", :value 0.8042917}
                   {:name "drink", :value 0.83911383}
                   {:name "two", :value 0.8198239}
                   {:name "container", :value 0.725613}
                   {:name "cold", :value 0.9290925}
                   {:name "people", :value 0.9570219}
                   {:name "wear", :value 0.70177203}
                   {:name "portrait", :value 0.72586167}
                   {:name "man", :value 0.896839}
                   {:name "healthcare", :value 0.7551681}
                   {:name "indoors", :value 0.83269846}
                   {:name "glass", :value 0.88382506}
                   {:name "veil", :value 0.7795876}
                   {:name "group", :value 0.87480223}
                   {:name "woman", :value 0.9120748}
                   {:name "beer", :value 0.7319597}
                   {:name "no person", :value 0.79889}},
     :id         24236,
     :timeframe  "1851-1900",
     :form       "painting",
     :jpg
                 "https://res.cloudinary.com/dgpqnl8ul/image/upload/v1546472440/o3ijavwwa4ojkpqaw4sz.jpg"}
    {:date       "1878",
     :school     "French",
     :genre      "portrait",
     :wga_jpg    "https://www.wga.hu/art/m/manet/4/4manet12.jpg",
     :title      "Man in a Round Hat (Alphonse Maureau)",
     :author     "MANET, Edouard",
     :created_on #inst "2018-12-21T17:25:31.000-00:00",
     :concepts
                 #{{:name "one", :value 0.98983526} {:name "adult", :value 0.98518276}
                   {:name "side view", :value 0.7534818}
                   {:name "religion", :value 0.755723}
                   {:name "old", :value 0.8170091}
                   {:name "lid", :value 0.9244362}
                   {:name "people", :value 0.9983702}
                   {:name "painting", :value 0.98108876}
                   {:name "wear", :value 0.89224696}
                   {:name "portrait", :value 0.98770726}
                   {:name "elderly", :value 0.7708284}
                   {:name "man", :value 0.98501396}
                   {:name "beard", :value 0.72548115}
                   {:name "veil", :value 0.85667205}
                   {:name "vintage", :value 0.7308766}
                   {:name "facial hair", :value 0.94412553}
                   {:name "retro", :value 0.816434}
                   {:name "art", :value 0.9833573}
                   {:name "leader", :value 0.7347914}
                   {:name "mustache", :value 0.9813224}},
     :id         24238,
     :timeframe  "1851-1900",
     :form       "painting",
     :jpg
                 "https://res.cloudinary.com/dgpqnl8ul/image/upload/v1546472441/jrom433z3ebwnqerjnjh.jpg"}
    {:date       "1879",
     :school     "French",
     :genre      "genre",
     :wga_jpg    "https://www.wga.hu/art/m/manet/4/4manet16.jpg",
     :title      "In the Winter Garden",
     :author     "MANET, Edouard",
     :created_on #inst "2018-12-21T17:23:17.000-00:00",
     :concepts
                 #{{:name "one", :value 0.9759318} {:name "adult", :value 0.99300295}
                   {:name "furniture", :value 0.9467297}
                   {:name "seat", :value 0.9515189}
                   {:name "lid", :value 0.91574156}
                   {:name "two", :value 0.97907144}
                   {:name "outerwear", :value 0.87922907}
                   {:name "people", :value 0.9964845}
                   {:name "painting", :value 0.8783745}
                   {:name "wear", :value 0.9583739}
                   {:name "portrait", :value 0.9744396}
                   {:name "man", :value 0.98772514}
                   {:name "veil", :value 0.8911963}
                   {:name "military", :value 0.9242512}
                   {:name "woman", :value 0.97469366}
                   {:name "soldier", :value 0.88871837}
                   {:name "art", :value 0.92404234}
                   {:name "sit", :value 0.93323195}
                   {:name "three", :value 0.89724416}
                   {:name "child", :value 0.8757273}},
     :id         24242,
     :timeframe  "1851-1900",
     :form       "painting",
     :jpg
                 "https://res.cloudinary.com/dgpqnl8ul/image/upload/v1546472443/gqwvv3nmczfqaqf3c7l1.jpg"}
    {:date       "1881",
     :school     "French",
     :genre      "historical",
     :wga_jpg    "https://www.wga.hu/art/m/manet/5/5late07.jpg",
     :title      "The Escape of Henri Rochefort",
     :author     "MANET, Edouard",
     :created_on #inst "2018-12-21T17:35:42.000-00:00",
     :concepts
                 #{{:name "watercraft", :value 0.9823499}
                   {:name "one", :value 0.94041026}
                   {:name "rowboat", :value 0.9560243}
                   {:name "adult", :value 0.9147363}
                   {:name "oar", :value 0.8999064}
                   {:name "two", :value 0.9277632}
                   {:name "sea", :value 0.93822336}
                   {:name "canoe", :value 0.9338627}
                   {:name "people", :value 0.98351806}
                   {:name "swimming", :value 0.8851098}
                   {:name "ocean", :value 0.92358166}
                   {:name "fisherman", :value 0.9260603}
                   {:name "leisure", :value 0.8901384}
                   {:name "transportation system", :value 0.897137}
                   {:name "water", :value 0.99450403}
                   {:name "vehicle", :value 0.89671445}
                   {:name "travel", :value 0.9164829}
                   {:name "recreation", :value 0.9788822}
                   {:name "fish", :value 0.949726}
                   {:name "boatman", :value 0.9217631}},
     :id         24252,
     :timeframe  "1851-1900",
     :form       "painting",
     :jpg
                 "https://res.cloudinary.com/dgpqnl8ul/image/upload/v1546472446/x8crdusi0cqdu3rywwv8.jpg"}
    {:date       "1881",
     :school     "French",
     :genre      "portrait",
     :wga_jpg    "https://www.wga.hu/art/m/manet/5/5late08.jpg",
     :title      "Portrait of Henri Rochefort",
     :author     "MANET, Edouard",
     :created_on #inst "2018-12-21T17:25:52.000-00:00",
     :concepts
                 #{{:name "one", :value 0.99891716} {:name "adult", :value 0.99553406}
                   {:name "side view", :value 0.9648169}
                   {:name "administration", :value 0.9108915}
                   {:name "music", :value 0.9220626}
                   {:name "people", :value 0.99919045}
                   {:name "wear", :value 0.95365226}
                   {:name "portrait", :value 0.9991429}
                   {:name "tie", :value 0.92405903}
                   {:name "facial expression", :value 0.85146475}
                   {:name "man", :value 0.9939749}
                   {:name "writer", :value 0.9283779}
                   {:name "politician", :value 0.8993956}
                   {:name "profile", :value 0.9753293}
                   {:name "facial hair", :value 0.93652}
                   {:name "outfit", :value 0.89390266}
                   {:name "menswear", :value 0.93957925}
                   {:name "neckwear", :value 0.95516014}
                   {:name "leader", :value 0.9851012}
                   {:name "mustache", :value 0.9306363}},
     :id         24253,
     :timeframe  "1851-1900",
     :form       "painting",
     :jpg
                 "https://res.cloudinary.com/dgpqnl8ul/image/upload/v1546472448/xxaoqvs6ad2av4t2aduh.jpg"}
    {:date       "1881-82",
     :school     "French",
     :genre      "genre",
     :wga_jpg    "https://www.wga.hu/art/m/manet/5/5late10.jpg",
     :title      "A Bar at the Folies-Bergère (detail)",
     :author     "MANET, Edouard",
     :created_on #inst "2018-12-21T17:23:07.000-00:00",
     :concepts
                 #{{:name "one", :value 0.8000617}
                   {:name "religion", :value 0.8590821}
                   {:name "winter", :value 0.8660009}
                   {:name "traditional", :value 0.85578156}
                   {:name "outdoors", :value 0.8795291}
                   {:name "cold", :value 0.86454546}
                   {:name "people", :value 0.95081115}
                   {:name "painting", :value 0.8788986}
                   {:name "indoors", :value 0.8228017}
                   {:name "group", :value 0.82631165}
                   {:name "water", :value 0.9398377}
                   {:name "woman", :value 0.84158987}
                   {:name "crystal", :value 0.8572757}
                   {:name "wet", :value 0.8016135}
                   {:name "travel", :value 0.92263913}
                   {:name "decoration", :value 0.8386463}
                   {:name "icee", :value 0.8358636}
                   {:name "fish", :value 0.7907878}
                   {:name "art", :value 0.92345643}
                   {:name "no person", :value 0.9639495}},
     :id         24255,
     :timeframe  "1851-1900",
     :form       "painting",
     :jpg
                 "https://res.cloudinary.com/dgpqnl8ul/image/upload/v1546472449/k3jixe3wrjb4qcuuvo3h.jpg"}
    {:date       "1881",
     :school     "French",
     :genre      "portrait",
     :wga_jpg    "https://www.wga.hu/art/m/manet/5/5late11.jpg",
     :title      "Study of a Model",
     :author     "MANET, Edouard",
     :created_on #inst "2018-12-21T17:26:01.000-00:00",
     :concepts
                 #{{:name "one", :value 0.99024737} {:name "model", :value 0.8956412}
                   {:name "adult", :value 0.9915126}
                   {:name "girl", :value 0.90252256}
                   {:name "face", :value 0.9113476}
                   {:name "lid", :value 0.97739756}
                   {:name "people", :value 0.9946698}
                   {:name "fashion", :value 0.9372419}
                   {:name "painting", :value 0.8990917}
                   {:name "wear", :value 0.95215756}
                   {:name "smoke", :value 0.89441824}
                   {:name "portrait", :value 0.9936029}
                   {:name "facial expression", :value 0.88725185}
                   {:name "man", :value 0.9698202}
                   {:name "veil", :value 0.957594}
                   {:name "fashionable", :value 0.8565195}
                   {:name "woman", :value 0.9524783}
                   {:name "retro", :value 0.8354877}
                   {:name "jacket", :value 0.8673773}
                   {:name "art", :value 0.95265865}},
     :id         24256,
     :timeframe  "1851-1900",
     :form       "painting",
     :jpg
                 "https://res.cloudinary.com/dgpqnl8ul/image/upload/v1546472451/b8fiba0shz0wldpk0bbb.jpg"}})

(def manet-sample-concept-frequencies
  [["people" 0.9868421052631579] ["adult" 0.881578947368421] ["art" 0.8157894736842105] ["man" 0.8026315789473685] ["woman" 0.75] ["painting" 0.6973684210526315] ["one" 0.6578947368421053] ["wear" 0.6578947368421053] ["portrait" 0.6052631578947368] ["group" 0.40789473684210525] ["veil" 0.39473684210526316] ["two" 0.3815789473684211] ["lid" 0.34210526315789475] ["religion" 0.25] ["illustration" 0.2236842105263158] ["costume" 0.21052631578947367] ["girl" 0.18421052631578946] ["print" 0.18421052631578946] ["dress" 0.17105263157894737] ["many" 0.14473684210526316] ["three" 0.14473684210526316] ["fashion" 0.13157894736842105] ["model" 0.13157894736842105] ["music" 0.13157894736842105] ["recreation" 0.13157894736842105] ["retro" 0.13157894736842105] ["water" 0.13157894736842105] ["child" 0.11842105263157894] ["no person" 0.11842105263157894] ["mammal" 0.10526315789473684] ["nude" 0.10526315789473684] ["reclining" 0.10526315789473684] ["military" 0.09210526315789473] ["outfit" 0.09210526315789473] ["travel" 0.09210526315789473] ["artistic" 0.07894736842105263] ["cavalry" 0.07894736842105263] ["sea" 0.07894736842105263] ["theater" 0.07894736842105263] ["vehicle" 0.07894736842105263] ["vintage" 0.07894736842105263] ["weapon" 0.07894736842105263] ["Renaissance" 0.06578947368421052] ["ceremony" 0.06578947368421052] ["crowd" 0.06578947368421052] ["furniture" 0.06578947368421052] ["musician" 0.06578947368421052] ["ocean" 0.06578947368421052] ["old" 0.06578947368421052] ["smoke" 0.06578947368421052]])

(def manet-sample-painting-ids (map :id manet-sample-paintings))

(def manet-example-group
  {:group-name            manet-people-group-name
   :paintings             manet-sample-paintings
   :painting-ids          manet-sample-painting-ids
   :concept-frequencies   manet-sample-concept-frequencies
   :genre-constraints     manet-type-constraints
   :school-constraints    manet-school-constraints
   :timeframe-constraints manet-timeframe-constraints
   :concept-constraints   manet-concept-constraints
   :artist-constraints    manet-artist-constraints})

