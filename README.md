# TP-ProgReseau-4IF

Le projet est séparé en deux parties : Un chat et un serveur web.

## Chat

Le chat comporte des conversations publiques, qui ont toutes un id propre.
Après s'être log in l'utilisateur peut choisir une conversation sur laquelle il se connecte.
Il peut maintenant communiquer avec les autres personnes connectées ou encore lancées des commandes. 

##### Commandes supportés. 

**\help :** donne les commandes disponibles. 
**\infos :** donne l'id de conversation et les utilisateurs connectés.
**\changeto <idConv> :** redirige l'utilisateur sur la conversation indiquée. 
**\tableFlip :** peut être inséré dans un message pour être remplacé par : "(╯‵□′)╯︵┻━┻".
**@<NomPersonne> :** peut être inséré dans un message pour mentionner une personne. La personne mentionnée verra ce message en surbrillance. 

## Web Server 
  
Le serveur Web implemente les requêtes **GET,POST,DELETE** et **HEAD**.
Il supporte les ressources de type **html,png,mp3,mp4** et **gif**.
Les codes retours HTTP **200,201,400,404,405** et **500** sont implémentés. 

  
  
  
