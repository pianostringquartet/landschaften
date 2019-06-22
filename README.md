# landschaften: visual explorer for paintings and their concepts
 
> "Seht nur das Wölkchen, wie es spielt ..."
>
> - Goethe, Landschaft 

Search for and explore paintings from different time periods, genres and artists -- 
or even by the concepts contained within a painting itself.

Then save your search results and compare different groups of paintings. 
How similar are the works of Michelangelo and Manet?  

### What

Visual encyclopedias like Wikiart are conservative in the information they host: only obvious or traditional classifications are allowed ("art movement", "genre", etc.).

But sometimes we want to go deeper into a painting, search for an idea that cuts across genres and time periods in unpredictable ways, or compare two artists' bodies of work.

`landschaften` is designed to combine the fun, art-first exploration of a visual encyclopedia with a more aggressive insight into the paintings themselves, enabled by machine learning.     


### How

`landschaften` classifies paintings from [Web Gallery of Art's database](https://www.wga.hu/frames-e.html?/html/c/cornelis/index.html)  using [Clarifai's General model](https://www.clarifai.com/models/general-image-recognition-model-aaa03c23b3724a16a56b629203edc62c). 

Web Gallery of Art provides data about the _school_ (Italian, French, etc.), _genre_ (landscape, still life, etc.), _time period_ (1451-1500, 1501-1550 etc.), and _artist_. 

Clarifai's General model identifies concepts within each painting: e.g. "people", "portrait", "adult", "ocean", "commerce", "saint" etc.    

Saved search results are compared statistically by measuring [variance](https://www.wikihow.com/Calculate-Variance) and visually with a [radar chart](https://en.wikipedia.org/wiki/Radar_chart#Alternatives). 


### License

MIT
