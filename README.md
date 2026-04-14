# 🌌 Semantic Space Analyzer & Visualizer

A robust, multi-layered, enterprise-grade Java application designed to analyze, query, and visualize high-dimensional semantic spaces (such as Word2Vec embeddings).

This tool provides an interactive environment to navigate latent text spaces, perform complex mathematical NLP queries, and visualize the relationships between vectors in both 2D and dynamic 3D environments.

---

## 🏗️ Architecture & SOLID Principles

This project features a highly decoupled architecture built with strict adherence to **Object-Oriented Design** and **SOLID principles**, ensuring a scalable and maintainable codebase:

* **Single Responsibility Principle (SRP):** Complete decoupling of the mathematical engine from the UI. Mathematical calculations live in `SpaceFunction` implementations, while UI rendering and state management are handled by `AppAction` classes.
* **Open/Closed Principle (OCP):** The system is open for extension but closed for modification. New distance metrics or search algorithms can be added simply by implementing their respective interfaces, without altering a single line of the core engine.
* **Liskov Substitution Principle (LSP):** Heavy reliance on abstract classes and interfaces (`SpaceFunction`, `AppAction`) ensures that any derived class (e.g., `RadiusFunction` or `KnnFunction`) can replace its parent seamlessly without breaking system behavior.
* **Interface Segregation Principle (ISP):** Interfaces are kept lean and highly specific to the clients that use them (e.g., separating `SpaceComponent` for data access from UI event listeners).
* **Dependency Inversion Principle (DIP):** High-level modules (like `AppUIManager`) depend entirely on abstractions (`DistanceStrategy`, `SpaceVisualizer`), never on low-level concrete implementations.

---

## 🛠️ Design Patterns Utilized

To manage the complexity of the application, several industry-standard design patterns were masterfully implemented:

1. **Template Method (`AbstractSpaceVisualizer`):** A cornerstone of the rendering architecture. The abstract base class implements all the common, shared rendering logic (e.g., managing highlights, clearing the screen), while leaving the specific rendering hooks to be implemented by its subclasses (`Space2DVisualizer` and `Space3DVisualizer`). This perfectly aligns with the DRY (Don't Repeat Yourself) principle.
2. **Factory Method (`SpaceCommand`):** Used seamlessly to bridge UI inputs with execution logic. The `SpaceCommand` interface (and its concrete UI implementations like `RadiusUI`) acts as a factory. It encapsulates user input and dynamically generates the correct `AppAction` object via the `generateAction()` method.
3. **Command Pattern (`AppAction`):** Every user query is encapsulated into a standalone command object (produced by the Factory). This isolates execution logic and enables a flawless, **O(1)** `Undo/Redo` History mechanism that prevents duplicate states.
4. **Composite Pattern (`CompositeSpace`):** Manages a hierarchical tree of vector spaces. The system queries the `CompositeSpace` through a uniform interface, remaining completely agnostic to whether it is querying a single space or a complex tree of multiple dimensions (FULL vs. PCA).
5. **Strategy Pattern (`DistanceStrategy`):** Encapsulates the mathematical algorithms used to measure vector distance (`CosineStrategy`, `EuclideanStrategy`), allowing them to be swapped dynamically at runtime.

---

## 🚀 Key Features

* **Advanced NLP Queries:** Supports K-Nearest Neighbors (KNN), Radius/Donut Search (Min & Max distances), Vector Arithmetic (Analogies), and Semantic Axis Projections.
* **Interactive Visualizations:** Custom JavaFX rendering engine supporting 3D SubScene camera navigation, zooming, and panning, alongside a responsive 2D canvas.
* **Algorithmic Efficiency:** Achieves **O(1)** time complexity for vector retrieval using internal HashMaps, and **O(1)** for State History adjustments using optimized Dual-Stacks.

---

## 📐 System Architecture Flow (UML)

The following diagram illustrates the high-level flow of data and dependencies across the core system packages.

![System Architecture](architecture.png)