# ProcedIt

ProcedIt is a procedural game developed with the LibGDX platform for the Natural Language Processing Game. Beside the whole LibGDX framework, it takes advantage of Box2D engine (inside the framework) and the SaGa API built by La Sapienza for building Games with a Purpose. 
The game itself is a procedural jumper: a small alien has landed on earth and wants to gather knowledge about the planet. Since all it finds lying around are images of what is human knowledge, we want to collect as many images as possible related with the gloss that his mother ship is asking about. 
The SaGa API has been used to login and manage data on the Sapienza servers such as images, glosses and annotations, while LibGDX was used to create the game in its entirety. 
In the game the player wants to collect as many relevant images as possible: this is done in order to create both a fun game mechanic, and collect as much data as possible for the Sapienza NLP lab to conduct their studies. In fact there are three types of images in a typical game: valid, non-valid and to be annotated. The Sapienza servers send the information to the player, which provides the best informations by either collecting or ignoring the images that are to be annotated. In fact this way the NLP lab will know that those images are/aren’t related to the gloss presented on the screen. 
The whole game is generated procedurally and is built so that the player can never find himself in a situation in which it’s impossible to go on. But the game is rather hard: in fact a study on a number of players has been conducted and the results made me realize that the harder the game gets, the more players want to go for ‘one more round’, thus providing the best experience for gamers and the most annotations for the lab. 

A NLPReport.pdf contains the informations about the game, and some statistics on players.
