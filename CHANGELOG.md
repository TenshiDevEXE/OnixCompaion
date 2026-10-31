# Changelog

## [2.0.1] - 2026-10-31

### Cambios principales
- **Migración a NeoForge 1.21.0** desde Forge 1.20.1
- **Java 21** (requerido para Minecraft 1.21)
- **Gradle 9.0** actualizado desde 8.8
- **ModDevGradle 2.0.148** (actualización de desarrollo)

### Correcciones técnicas
- **Networking**: Reemplazado `SimpleChannel` por `CustomPacketPayload + StreamCodec + RegisterPayloadHandlersEvent`
- **Configuración**: `ForgeConfigSpec` reemplazado por `ModConfigSpec`
- **Eventos**: Reemplazado `@EventBusSubscriber` por registro manual de listeners vía `IEventBus.addListener()`
- **Entity API**: Actualizadas firmas para Minecraft 1.21:
  - `defineSynchedData()` ahora toma `SynchedEntityData.Builder`
  - `FoodProperties.nutrition()` en lugar de `getNutrition()`
  - `ItemStack.parse()` en lugar de `ItemStack.of(CompoundTag)`
- **Modelo**: `renderToBuffer()` actualizado a `(PoseStack, VertexConsumer, int, int, int)`
- **Pantalla**: `renderBackground()` actualizado a `(GuiGraphics, int, int, float)`

### Mejoras
- **Persistencia**: Carga de inventario y equipamiento via NBT usando `ItemStack.parse(RegistryAccess, CompoundTag)`
- **Compatibilidad**: Mejor soporte para versiones 1.21 de Minecraft

### Archivos modificados
- `build.gradle` - Configuración de NeoForge y versiones
- `gradle/wrapper/gradle-wrapper.properties` - Gradle 9.0
- `settings.gradle` - Repositorios NeoForge
- `neoforge.mods.toml` - Reemplaza `mods.toml`
- Múltiples clases Java actualizadas para API 1.21

---

## [1.0.6] y anteriores (2026-10-25)

### Funcionalidades principales
- **Compañero NPC**: Entidad persistente con sistema de órdenes
- **Órdenes**: Seguir, Quedarse, Vadar, Patrullar, Guardia, Recoger, Minar, Construir
- **Modos combate**: Pasivo, Defensivo, Agresivo
- **Huevo de spawn**: Textura y modelo personalizados
- **Sistema de habilidades**: Combate, Minería, Supervivencia, Recolección (8 habilidades x 4 categorías, niveles 1-5)
- **Machine Learning**: Sistema adaptativo con pesos por acción y exploración epsilon-greedy
- **Memoria de zonas**: Caché LRU persistente
- **Inventario**: 27 slots + equipamiento
- **Teleport seguridad**: Auto-teletransporte si se aleja o falla el pathfinding
- **Sistema de lealtad**: Afecta obediencia del compañero
- **Muerte/respawn**: Drop de inventario + respawn en spawn del mundo

### Archivos
- Localización: `en_us.json`, `es_es.json`
- Modelos: `companion_spawn_egg.json`, `companion.json`
- Texturas: `companion_spawn_egg.png`, `companion.png`
- Sonidos: Integrados desde recursos de Minecraft