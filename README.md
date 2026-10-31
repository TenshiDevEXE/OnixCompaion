# OnixCompanion

Mod para Minecraft con un compañero NPC persistente, controlable por el jugador, con órdenes de combate, seguimiento, inventario, habilidades y sistema de aprendizaje adaptativo.

## Características

- **Compañero NPC** — Entidad persistente que te sigue y guarda su estado (posición, inventario, XP, lealtad) entre sesiones.
- **Órdenes** — Seguir, quedarse, vagar dentro de una zona hogar, teletransporte de seguridad y modos de combate (Pasivo / Defensivo / Agresivo).
- **Huevo de spawn** — Textura y modelo personalizados para invocar al compañero.
- **Sonidos y partículas** — Retroalimentación audiovisual al cambiar órdenes.
- **GUI de órdenes** — Abre una pantalla con botones para cada orden y modo de combate.
- **Sistema de lealtad** — Sube o baja según cómo lo trates; afecta su obediencia.
- **Comportamiento día/noche** — Más lento y menos activo durante la noche.
- **Inventario y equipamiento** — 27 slots de inventario y slots de armadura + arma en mano.
- **Curación** — Dale comida para restaurar sus puntos de vida.
- **Sistema de habilidades (RPG ligero)** — 4 categorías: Combate, Minería, Supervivencia, Recolección. Cada una tiene 8 habilidades con niveles (1-5) que desbloquean mejoras (daño, velocidad de minería, armadura, respiración bajo agua, rango de recogida, etc.).
- **Machine Learning (comportamiento adaptativo)** — El compañero aprende de sus acciones con un sistema de pesos por acción, recompensa/penalización (EMA) y exploración epsilon-greedy. Mejora con el tiempo según lo que hagas.
- **Memoria de zonas** — Recuerda zonas visitadas, recursos encontrados y rutas exitosas (caché LRU persistente en NBT).
- **Muerte y respawn** — Al morir, droppea inventario y equipamiento; respawn en el spawn point del mundo tras 30 segundos.
- **Configuración (NeoForge Config)** — `companionmod-common.toml`: distancia de seguimiento, vida, daño, velocidad, teleport, cap de nivel, sonidos, etc.
- **Localización** — Idiomas: inglés (`en_us.json`) y español (`es_es.json`).

## Instalación

1. Instala [NeoForge 1.21.0 (21.0.167)](https://neoforged.net/).
2. Descarga el JAR más reciente desde la pestaña **Releases** de este repositorio.
3. Coloca el archivo `onixcompanion-2.0.1.jar` en la carpeta `mods` de tu instancia de Minecraft.
4. Inicia Minecraft con el perfil de NeoForge.

## Uso

1. Crea un **Huevo de Spawn de Compañero** en el modo creativo o consígelo mediante comandos si lo tienes instalado.
2. Usa el huevo para invocar al compañero.
3. **Clic derecho vacío** sobre el compañero: alterna entre seguirte y quedarse quieto.
4. Abre la **GUI de órdenes** (si está habilitada) para cambiar órdenes y modos de combate.
5. Usa la configuración en `config/companionmod-common.toml` para ajustar distancia de seguimiento, vida, daño, velocidad, teleport, cap de nivel y más.

## Construcción desde el código fuente

Requisitos:
- JDK 21
- Gradle 9.0 (incluido como wrapper)

```bash
.\gradlew.bat build
```

El JAR resultante se genera en `build\libs\onixcompanion-2.0.1.jar`.

## Estructura del proyecto

```
onixcompanion/
├── build.gradle
├── settings.gradle
├── gradlew / gradlew.bat
├── gradle/wrapper/
├── LICENSE
├── README.md
└── src/
    └── main/
        ├── java/com/kilo/companionmod/
        │   ├── CompanionMod.java
        │   ├── ModEntities.java
        │   ├── ModItems.java
        │   ├── ModNetwork.java
        │   ├── ModSounds.java
        │   ├── config/ModConfig.java
        │   ├── entity/
        │   │   ├── CompanionEntity.java
        │   │   ├── CompanionOrder.java
        │   │   ├── CombatMode.java
        │   │   ├── ai/
        │   │   │   └── CompanionOrderGoal.java
        │   ├── ai/CompanionBrain.java
        │   ├── skills/CompanionSkills.java
        │   ├── event/CompanionEvents.java
        │   ├── gui/CompanionScreen.java
        │   └── sound/ModSounds.java
        └── resources/
            ├── META-INF/neoforge.mods.toml
            └── assets/companionmod/
                ├── lang/en_us.json
                ├── lang/es_es.json
                ├── models/item/companion_spawn_egg.json
                ├── models/entity/companion.json
                ├── sounds.json
                ├── sounds/*.ogg
                └── textures/item/companion_spawn_egg.png
                └── textures/entity/companion.png
```

## Licencia

Este proyecto está bajo la licencia **MIT**. Ver el archivo `LICENSE` para más detalles.

## Historial de versiones

### 2.0.1 (2026-10-31)
- **Migración a NeoForge 1.21.0**
- Actualizado a Java 21, Gradle 9.0, ModDevGradle 2.0.148
- Networking: SimpleChannel → CustomPacketPayload + StreamCodec
- Configuración: ForgeConfigSpec → ModConfigSpec
- Entidades actualizadas a API de MC 1.21
- Inventario persistente con carga NBT desde ItemStack.parse()

### 1.0.0 (2026-10-25)
- Lanzamiento inicial
- Compañero NPC con órdenes de seguir/quedarse/vander/caminar
- Modos de combate: Pasivo, Defensivo, Agresivo
- Huevo de spawn con textura y modelo personalizados
- Sistema de habilidades (Combate, Minería, Supervivencia, Recolección)
- Machine Learning adaptativo con memoria de zonas
- Inventario, equipamiento y curación
- Muerte con drop y respawn en spawn point
- Config Forge y localización EN/ES