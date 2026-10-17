# Parkour Companion

Mod client Fabric pour 1.21.11, à utiliser avec le plugin
[Parkour](https://github.com/SalksCore/parkour). Sans le mod tout marche quand même,
avec le mod tu as en plus :

- **Le fantôme de ton record** : une version transparente de toi qui refait ton meilleur
  temps pendant que tu cours. Il est enregistré chez toi, par serveur et par parcours.
- **Les temps intermédiaires** : à chaque checkpoint, l'écart avec ton record s'affiche en
  vert si tu es en avance, en rouge si tu es en retard.
- **Un HUD sur une ligne** en haut de l'écran : chrono, checkpoint en cours, direction et
  distance du prochain point, record perso.
- **Ton record sur place** : au départ et sur le tableau des temps, avec ton rang.
- **Un petit rayon de particules** au-dessus du prochain point pour le repérer de loin.

Quand le serveur voit le mod, il coupe son action bar et sa bossbar pour ne pas faire
doublon.

## Touches

| Touche | Action |
|---|---|
| `G` | Afficher ou cacher le fantôme |
| `H` | Afficher ou cacher le HUD |

Elles se changent dans Options > Commandes.

## Installation

1. Installer [Fabric Loader](https://fabricmc.net/use/) pour 1.21.11
2. Mettre [Fabric API](https://modrinth.com/mod/fabric-api) et ce mod dans `mods/`

Côté serveur il faut le plugin Parkour 1.1.0 ou plus récent.

## Fichiers

- `config/parkour-companion.properties` : fantôme, HUD et marqueurs activés ou non
- `config/parkour-companion/ghosts/<serveur>/<parcours>.ghost` : tes records enregistrés

## Compiler

Gradle demande Java 25 pour tourner, le mod reste compilé pour Java 21.

```
./gradlew build
```

Le jar sort dans `build/libs/`.

## Licence

MIT
