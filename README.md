# Projet POO-javafx
Ce projet est un projet scolaire qui permet d'expérimenter avec Maven et javafx.
L'idée est de représenter une baignoire avec des fuites et des robinets qui la remplissent
On définit un nombre de robinets et de fuite < 4 puis on lance la simulation
On peut changer le débit de chaque robinet et supprimer certaines fuites
Lorsque la baignoire est remplie, on arrête la simulation et affiche le temps pris.
Ce projet est censé prendre un temps d'environ : 8h
Ce projet à pris un temps de : 12h20

![Screenshot de l application](ressources/ongletBaignoire.png)
![Screenshot de l application](ressources/ongletParametre.png)
![Screenshot de l application](ressources/ongletGraphique.png)

## Auteurs :
Projet crée par Marcus Richier, suivant les cours de Azim Roussalany.

## Version :
Testé pour java 17.

## Necessite pour l'installation :

- Java 17
- Maven

## Installation :

- Allez a la racine du projet
- Installez avec : "mvn package"

## Utilisation :

### Avec un binary 
Lancez avec "./bindist/bin/baignoire"

### Avec un jar
Lancez le jar dans target avec les librairies javafx

## Conception de l'architecture :
![diagramme de classe avec dépendence](ressources/diagrammeClasse.png)


## Conception graphique :
![Modele de la conception](ressources/Organisation_graphique.png)


## Licence MIT :
