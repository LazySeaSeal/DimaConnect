# Application de Recrutement - Spring Boot

Ce projet implémente une application de recrutement basée sur Spring Boot 3.4.4 et PostgreSQL, conformément au diagramme entité-relation (ERD) fourni.

## Structure du Projet

Le projet est organisé selon la structure standard de Spring Boot :

```
src/main/java/com/recrutement/app/
├── RecrutementApplication.java        # Point d'entrée de l'application
├── config/
│   └── DataInitializer.java           # Initialisation des données de test
├── model/
│   ├── Candidat.java                  # Entités principales
│   ├── Entreprise.java
│   ├── OffreEmploi.java
│   ├── Candidature.java
│   ├── ...                            # Autres entités
│   └── enums/                         # Énumérations
│       ├── TypeContrat.java
│       ├── StatutCandidature.java
│       └── ...
└── repository/                        # Repositories Spring Data JPA
    ├── CandidatRepository.java
    ├── EntrepriseRepository.java
    └── ...

src/main/resources/
└── application.properties             # Configuration de l'application
```

## Prérequis

- Java 17 ou supérieur
- Maven 3.6 ou supérieur
- PostgreSQL 12 ou supérieur

## Configuration de la Base de Données

La configuration de la base de données se trouve dans le fichier `src/main/resources/application.properties`. Par défaut, l'application se connecte à une base de données PostgreSQL locale avec les paramètres suivants :

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/recrutement
spring.datasource.username=postgres
spring.datasource.password=postgres
```

Assurez-vous que la base de données existe ou modifiez ces paramètres selon votre environnement.

## Exécution de l'Application

1. Clonez ce dépôt
2. Créez la base de données PostgreSQL
3. Exécutez l'application avec Maven :

```bash
mvn spring-boot:run
```

L'application démarrera sur le port 8080 par défaut.

## Données de Test

L'application inclut un chargeur de données de test qui s'exécute automatiquement en mode développement. Pour activer ce mode, utilisez le profil "dev" :

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Les données de test incluent :
- Des candidats avec leurs compétences
- Des entreprises et leurs employés
- Des offres d'emploi avec les compétences requises
- Des candidatures

## Modèle de Données

L'application implémente le modèle de données suivant :

- **Candidat** : Utilisateur cherchant un emploi
- **Entreprise** : Organisation proposant des offres d'emploi
- **Compétence** : Aptitude technique ou professionnelle
- **OffreEmploi** : Poste à pourvoir proposé par une entreprise
- **Candidature** : Demande d'emploi d'un candidat pour une offre
- **Entretien** : Rencontre entre un candidat et une entreprise
- **Employe** : Membre du personnel d'une entreprise
- **PackPremium** : Forfait premium pour les entreprises
- **AbonnementPremium** : Souscription d'une entreprise à un pack premium

Chaque entité est validée avec des annotations Jakarta Validation pour garantir l'intégrité des données.

## Développement

Pour étendre cette application, vous pouvez :

1. Ajouter des contrôleurs REST pour exposer les fonctionnalités via une API
2. Implémenter une interface utilisateur avec Thymeleaf ou un framework frontend
3. Ajouter des services métier pour encapsuler la logique applicative
4. Mettre en place des tests unitaires et d'intégration

## Licence

Ce projet est distribué sous licence MIT.
