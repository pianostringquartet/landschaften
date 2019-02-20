(ns landschaften.sample)

;; in prod should be retrieved dynamically from backend

(def sample-paintings
  #{{:date "c. 1469",
      :school "Italian",
      :type "portrait",
      :title "Portrait of a Young Man",
      :author "BOTTICELLI, Sandro",
      :concepts #{{:name "gown (clothing)", :value 0.90708596}
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
      :id 5623,
      :timeframe "1451-1500",
      :form "painting",
      :jpg "https://www.wga.hu/art/b/botticel/7portrai/01youngm.jpg"}
    {:date "c. 1483",
     :school "Italian",
     :type "portrait",
     :title "Portrait of a Young Man",
     :author "BOTTICELLI, Sandro",
     :concepts #{{:name "one", :value 0.99197435}
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
     :id 5632,
     :timeframe "1451-1500",
     :form "painting",
     :jpg "https://www.wga.hu/art/b/botticel/7portrai/10youngm.jpg"}})

;; should satisfy ::group spec...
(def sample-group
  {:group-name :default-group
   :paintings sample-paintings
   :types #{}
   :schools #{}
   :timeframes #{}
   :concepts #{}
   :artists #{}})

(def sample-group-2
  {:group-name :spanish-religious
   :paintings sample-paintings
   :types #{"religious"}
   :schools #{"Spanish"}
   :concepts #{"religion"}})


(def sample-concepts
  #{"castle" "decoration" "door" "style" "church" "old" "architecture" "no person" "ancient" "antique" "ornate" "gold" "museum" "design" "vintage" "outdoors" "mountain" "scenic" "tree" "recreation" "mammal" "landscape" "many" "travel" "daylight" "nature" "seashore" "water" "rock" "snow" "cavalry" "print" "herder" "side view" "cat" "dog" "child" "interaction" "suckling" "one" "war" "military" "light" "calamity" "vehicle" "dawn" "evening" "home" "sunset" "battle" "seat" "chandelier" "bedrock" "interior design" "furniture" "indoors" "room" "inside" "window" "communion table" "building" "bench" "column" "cathedral" "chapel" "ceiling" "cross" "position" "Apostle" "Mary" "religious" "holy" "god" "veil" "monument" "statue" "interior" "culture" "wood" "doorway" "facade" "Gothic" "house" "entrance" "wall" "winter" "traditional" "garden" "Zen" "leaf" "bird" "wine" "drink" "celebration" "Christmas" "food" "bottle" "glass" "container" "fruit" "still life" "candle" "glass items" "color" "table" "weapon" "sword" "wear" "armor" "beautiful" "feather" "rope" "animal" "wildlife" "flower" "fall" "basket" "invertebrate" "grow" "vegetable" "pumpkin" "bouquet" "rose" "flower arrangement" "flora" "festival" "luxury" "gift" "desktop" "apple" "vase" "jewelry" "wedding" "retro" "love" "lid" "mustache" "cape" "cowboy hat" "facial hair" "cowboy" "elderly" "portrait" "cap" "spirituality" "looking" "breed" "cute" "pet" "canine" "hound" "domestic" "grass" "puppy" "fur" "sit" "shield" "book" "fresco" "image" "symbol" "ocean" "beach" "sea" "flame" "park" "girl" "environment" "silhouette" "arch" "virgin" "book series" "aura" "son" "artistic" "brunette" "costume" "fashionable" "skirt" "dress" "model" "fashion" "sky" "city" "turning point" "tourism" "temple" "town" "tower" "gown (clothing)" "Madonna" "opera" "facial expression" "outerwear" "jacket" "headscarf" "music" "royalty" "ceremony" "soldier" "street" "market" "river" "summer" "crowd" "tourist" "evergreen" "pine" "fog" "conifer" "displayed" "crustacean" "shellfish" "lobster" "seafood" "fish" "exotic" "marine" "crab" "cave" "coat" "outfit" "gown" "writer" "musician" "priest" "leader" "administration" "pensive" "poet" "confidence" "profile" "ballet dancer" "ballerina" "shirtless" "intimacy" "bed" "affection" "warship" "ship" "watercraft" "sailboat" "wind" "transportation system" "sail" "boat" "navy" "mansion" "warehouse" "empty" "abandoned" "sacred" "relief" "marble" "lion" "kneeling" "family" "prayer" "fortification" "miracle" "peace" "religious belief" "worship" "pattern" "belief" "monastery" "Easter" "Resurrection" "crucifixion" "vault" "hallway" "cloister" "arcade" "courtyard" "historic" "monkey" "ape" "primate" "three" "group together" "five" "several" "four" "politician" "monk" "pope" "bald" "monarch" "person" "necklace" "famous" "fame" "fantasy" "crown" "pop" "wreath" "entertainment" "soup" "dish" "pot" "poultry" "season" "isolate" "Halloween" "romance" "romantic" "anniversary" "floral" "petal" "abstract" "texture" "visuals" "insubstantial" "party" "coloring" "bright" "lily" "singer" "queen" "theater" "performance" "seated" "lithograph" "cupola" "dome" "mine" "uniform" "helmet" "cattle" "livestock" "storm" "smoke" "action" "motion" "card" "wooden" "picture frame" "shrimp" "abbey" "goth like" "army" "road" "cherub" "hairdo" "weather" "island" "cloud" "ocean cruise" "mast" "dark" "construction" "steel" "landmark" "strong" "naked" "figure" "barefoot" "subway system" "chair" "engraving" "danger" "dirty" "kitchenware" "dairy product" "cooking" "knife" "meal" "tableware" "bowl" "sweet" "skirmish" "sheep" "toy" "offspring" "boy" "goddess" "book bindings" "face" "volcano" "geology" "abstraction" "stone" "drum" "percussion instrument" "instrument" "hotel" "lamp" "pain" "commerce" "rug" "stock" "library" "step" "nun" "sexy" "wild" "stream" "waterfall" "scripture" "education" "literature" "text" "school" "offense" "exhibition" "closeup" "H2O" "dancing" "cranberry" "rustic" "hot" "delicious" "plate" "refreshment" "tasty" "restaurant" "epicure" "beef cattle" "pastoral" "agriculture" "cow" "farm" "bull" "desert" "lust" "brawny" "parchment" "paper" "rough" "dancer" "enjoyment" "tattoo" "bridge" "canal" "dusk" "cityscape" "urban" "clock" "accident" "pollution" "hill" "waste" "blur" "insect" "underwater" "Byzantine" "mosaic" "exploration" "knight" "carriage" "cowman" "dust" "camel" "mustang" "horse" "mare" "countryside" "equine" "grassland" "calf" "herd" "horn" "goat" "square" "Buddha" "tropical" "biology" "shell" "reef" "aquarium" "pear" "athlete" "strength" "vine" "Thanksgiving" "grape" "scientist" "theatre" "bronze" "metalwork" "fishing boat" "boatman" "rowboat" "fisherman" "figurine" "leisure" "body" "throne" "robe" "canyon" "lake" "valley" "mist" "violin" "stage" "glamour" "sitting" "abundance" "farming" "hair" "predator" "zoo" "growth" "backlit" "canoe" "stringed instrument" "piano" "eagle" "birth" "blood" "eerie" "moon" "mystery" "palm" "vacation" "ice" "frost" "cold" "hot spring" "eruption" "gloves" "barn" "hut" "margin" "veranda" "album" "spiny lobster" "force" "fox" "reproduction" "aggressive disposition" "crystal" "cup" "alcohol" "jug" "cookware" "pigeon" "graphic" "chicken" "dame" "hen" "cockerel" "pastel" "princess" "imperial" "lioness" "safari" "mane" "Panthera" "big" "knitwear" "movie" "science" "human" "thorax" "anatomy" "fountain" "actress" "backstage" "collection" "neckwear" "artisan" "wallpaper" "sight" "horizontal plane" "skull" "blooming" "berry" "confection" "peach" "grave" "incident " "skin" "lady" "curly" "reptile" "newborn" "bedroom" "captivation" "frame" "idyllic" "reflection" "sand" "multi" "coral" "anemone" "optical illusion" "pants" "Lama" "mug" "handle" "kettle" "teacup" "tea" "coffee" "teapot" "pottery" "capitol" "walking stick" "employee" "embrace" "togetherness" "toddler" "branch" "bush" "Crater" "arrangement" "wet" "laundry" "bathtub" "hygiene" "wash" "harbor" "yacht" "gem" "quartz" "treasure" "shining" "health" "round out" "precious" "disjunct" "wealth" "foot" "merchant" "background" "shape" "horror" "fear" "broken" "ruin" "tunnel" "industry" "rusty" "shadow" "tree log" "iron" "rust" "shipwreck" "seascape" "handmade" "bumpkin" "diet" "healthy" "fair weather" "fireplace" "audience" "perfume" "cluster" "rural" "geometry" "kaleidoscope" "geometric" "fractal" "little" "happiness" "fun" "ebony" "contemporary" "classic" "rain" "energy" "competition" "explosion" "trunk" "root" "bark" "adventure" "kind" "nutmeg" "assortment" "pepper" "cardamom" "chili" "altar" "gondola" "gondolier" "Venetian" "assembly" "pier" "fortress" "lingerie" "erotic" "concert" "rebellion" "swimming" "wig" "bride" "illness" "sacrifice" "injury" "meeting" "beard" "collage" "illegal" "alternative" "tool" "craft" "nut" "event" "public show" "scary" "easel" "grinder" "urn" "oil" "ballet" "flag" "tent" "time" "popularity" "inner surface" "candlestick" "brass" "medicine" "cutlery" "flatware" "breakfast" "tray" "business" "money" "sun" "footwear" "place" "perspective" "tail" "head" "condensation" "juicy" "strawberry" "nutrition" "burnt" "arson" "blacksmith" "torch" "foundry" "furnace" "coal" "melt" "heat" "Earth surface" "chaos" "gun" "friendship" "vest" "tie" "fitness" "relaxation" "back" "sleep" "placid" "oak" "deep" "revolution" "cemetery" "warrior" "topless" "arouse" "bodybuilding" "biceps" "breast" "magic" "goatee" "sweater" "scarf" "elder" "talent" "actor" "red carpet" "spacecraft" "production" "train" "duck" "raptor" "ball-shaped" "atlas" "geography" "map" "Continent" "shore" "field" "crest" "alive" "avian" "beak" "farmyard" "motley" "melon" "cropland" "sandstone" "page" "document" "language" "manuscript" "snap" "sheet" "writing" "banking" "newspaper" "courage" "wasteland" "action energy" "hurry" "equestrian" "guitarist" "guitar" "rider" "teacher" "track" "racehorse" "jockey" "cart" "hayfield" "gallop" "fast" "sadness" "wound" "bonnet" "spherical" "space" "atmosphere" "planet" "sphere" "round" "astronomy" "funeral" "law" "copper" "fly" "deer" "guidance" "bakery" "bread" "hero" "habit" "vertical" "barrel" "fungus" "mushroom" "edible" "concentration" "battlefield" "sofa" "pillow" "oval" "pasture" "dairy" "milk" "limestone" "torso" "hood" "raincoat" "rap" "authority" "judge" "justice" "mirror" "easy chair" "trading floor" "frosty" "glacier" "rainforest" "winery" "meat" "gear" "machine" "metallic" "lantern" "ornithology" "sparrow" "songbird" "majestic" "swimming pool" "stallion" "saddle" "bridle" "water sports" "mural" "flood" "universe" "hemisphere" "curtain" "snake" "pictorial" "etching" "canvas" "sooty" "soil" "brush" "watercolor" "thunderstorm" "wave" "surf" "dramatic" "mountain peak" "hike" "tallest" "alone" "solitude" "ecology" "fine" "stable" "Gospel" "blond" "fin" "aquatic" "octopus" "mythology" "serious" "cherry" "prehistoric" "antelope" "fence" "painter" "Impressionism" "acrylic" "creativity" "watercolor painting" "elephant" "underwear" "corset" "hosiery" "pony" "mask" "silhouetted" "wrestling" "race" "muscle" "election" "currency" "cravat" "sash" "blank" "carpentry" "board" "police" "boxer" "terrier" "spaniel" "whelp" "funny" "roof" "flat" "finance" "graphic design" "designing" "office" "graph" "facts" "diagram" "stripe" "technology" "statistics" "data" "trash" "Ottoman" "note" "sign" "stump" "eye" "ghost" "raw material" "wagon" "lizard" "monster" "hippopotamus" "lush" "graffiti" "scene" "tractor" "heavy" "truck" "nautical" "oar" "shrub" "palette" "water-color" "hyacinth" "herb" "herbal" "menswear" "dinner jacket" "ma" "sibling" "surreal" "pastry" "pork" "slice" "sausage" "ham" "medal" "wing" "smooth" "stub" "cigar" "tobacco" "adorable" "healthcare" "handwriting" "mathematics" "class" "chalkboard" "display" "chalk" "classroom" "fuel" "smog" "power" "air pollution" "rainbow" "fedora" "arid" "dry" "dalmatian" "apartment" "fabric" "jewelry band" "bar" "champagne" "banquet" "elegant" "democracy" "magician" "graduation" "accomplishment" "thoroughbred" "cow pen" "hay" "dig" "eyeglasses" "waterfowl" "purebred" "vineyard" "grapevine" "philosopher" "rifle" "antenna" "pest" "butterfly" "entomology" "beetle" "bee" "zoology" "Scorpio" "larva" "geological formation" "dinner" "bay" "military uniform" "studio" "reverence" "ailment" "clay" "jar" "souvenir" "ground" "chocolate" "skittish" "toxicant" "physiology" "cranium" "bone" "jaw" "locomotive" "railway" "pretty" "crib" "car" "guy" "Messiah" "victorian" "egg" "preparation" "cuisine" "nostalgia" "beer" "icee" "stalactite" "gate" "lock" "gateway" "box" "windmill" "turbine" "electricity" "goldfish" "diving" "tank" "satin" "textile" "handicraft" "coat of arms" "hijab" "chalk out" "guard" "mouse" "rabbit" "archaeology" "moss" "boulder" "tomato" "freshness" "plum" "root vegetable" "band" "acoustic guitar" "play" "acoustic" "farmhouse" "bungalow" "frozen" "foam" "tide" "splash" "hard" "expression" "houseplant" "salmon fish" "freshwater" "catch" "silk" "seamless" "proportion" "mud" "craftsmanship" "skill" "spectator" "niche" "garbage" "demolition" "junk" "wireless" "modern" "screen" "equipment" "isolated" "electronics" "portable" "computer" "cabinet" "storage" "rack" "demon" "grief" "owl" "grotto" "chariot" "destination" "security" "horizontal" "glazed" "porcelain" "poetry" "gilt" "pod" "spice" "pistol" "aisle" "shopping" "close" "exterior" "jungle" "hunter" "camouflage" "bonfire" "wildfire" "ash" "intensiveness" "campfire" "Jupiter" "turtle" "amphibian" "frog" "remote" "bison" "promotion" "pavement" "kiss" "rotunda" "candy" "sugar" "stain" "fur coat" "drop" "bubble" "creepy" "circus" "groom" "sports fan" "moisture" "shower" "burn" "pub" "dining room" "minaret" "kelp" "meditation" "casual" "paleontology" "dinosaur" "balcony" "lobby" "spirit" "hanging" "blanket" "illuminated" "crop" "peacock" "regatta" "schooner" "navigation" "frigate" "mourn" "pool" "glisten" "surface" "tapestry" "exert" "anchor" "woodcut" "sepia pigment" "snowstorm" "snow-white" "riverbank" "parrot" "wicker" "submarine" "sponge" "greeting" "wisdom" "knowledge" "neck" "loyalty" "sheepdog" "runner" "astrology" "emperor" "basement" "dressage" "zombie" "bloody" "stained glass" "chestnut" "foal" "decay" "tunic" "heritage" "novel" "poodle" "neoclassical" "capital" "rosary" "meteorology" "antler" "moose" "buck" "reindeer" "stag" "exercise" "gymnastics" "agility" "balance" "bill" "sailing" "lifestyle" "homemade" "spiderweb" "spider" "arachnid" "carve" "firewood" "poppy" "vibrant" "doll" "rescue" "battleship" "cargo ship" "steam" "youth" "blouse" "beads"})
