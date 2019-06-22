# landschaften: visual explorer for paintings and their concepts
 
> "Seht nur das Wölkchen, wie es spielt ..."
>
> - Goethe, Landschaft 

Search for and explore paintings from different time periods, genres and artists -- 
or even by the concepts contained within a painting itself.

Then save your search results and compare different groups of paintings. 
How similar are the works of Michelangelo and Manet?  

### How

`landschaften` classifies paintings from [Web Gallery of Art's database](https://www.wga.hu/frames-e.html?/html/c/cornelis/index.html)  using [Clarifai's General model](https://www.clarifai.com/models/general-image-recognition-model-aaa03c23b3724a16a56b629203edc62c). 

Web Gallery of Art provides data about the school (Italian, French, etc.), genre (landscape, still life, etc.), time period (1451-1500, 1501-1550 etc.), and artist. 

Clarifai's General model identifies concepts within each painting: e.g. "people", "portrait", "adult", "ocean", "commerce", "saint" etc.    
(Note: we're only interested in concepts the model is pretty certain about.)


### License

Copyright © 2018 FIXME
