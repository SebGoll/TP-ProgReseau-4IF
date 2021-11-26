# TP-ProgReseau-4IF

Le projet est séparé en deux parties : Un chat et un serveur web.

## Chat

Le chat comporte des conversations publiques, qui ont toutes un id propre.

Après s'être log in l'utilisateur peut choisir une conversation sur laquelle il se connecte.

Il peut maintenant communiquer avec les autres personnes connectées ou encore lancées des commandes. 

### Commandes supportés. 

**\help :** donne les commandes disponibles. 

**\infos :** donne l'id de conversation et les utilisateurs connectés.

**\changeto <idConv> :** redirige l'utilisateur sur la conversation indiquée. 
  
**\tableFlip :** peut être inséré dans un message pour être remplacé par : "(╯‵□′)╯︵┻━┻".
  
**@<NomPersonne> :** peut être inséré dans un message pour mentionner une personne. La personne mentionnée verra ce message en surbrillance. 

## Web Server 
  
Le serveur Web implemente les requêtes **GET,POST,DELETE** et **HEAD**.
  
Il supporte les ressources de type **html,png,mp3,mp4** et **gif**.
  
Les codes retours HTTP **200,201,400,404,405** et **500** sont implémentés. 
  
  
## Description de l'arborescence
  
***PersistenceData*** comporte les fichiers de persistance des conversations. Il y a un fichier par conversation. Si le fichier n'existe pas, la conversation n'est pas encore créée.

***doc*** comporte les fichiers de la JavaDoc.
  
***resources*** comporte les differentes ressources du serveurs, tel que les fichiers html et les médias affichés.
    
***src/http/server/WebServer.java*** est le serveur Web.  

***src/Data*** comporte les fichiers qui gèrent le partage de données entre les chats. 

***src/stream*** comporte les fichiers des chats. 


  
  
  
