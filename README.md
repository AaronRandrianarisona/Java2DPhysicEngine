# Pixel Sandbox Physics Engine

A small Java starter project for a 2D pixel sandbox physics engine with emergent material behavior.

## Goals

- simple grid-based world simulation
- materials with distinct rules
- testable physics behaviors
- extensible sandbox architecture

## Run

Desktop launch:

```powershell
run-sandbox.bat
```

Terminal fallback:

```bash
mvn test
java -classpath target/classes com.physicengine.Main ascii
```

## Current prototype

The project includes:

- a `World` grid
- `Material` enum for common materials
- basic sand and water gravity behaviors
- a small test suite covering movement rules

## Next ideas

- add more materials: wood, fire, steam, lava
- implement temperature and reaction rules
- add rendering and input for a sandbox UI
- improve deterministic update order and rule weighting
