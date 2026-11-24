# PDE Visualizer

An interactive tool that solves the 1D heat and wave equations numerically
(finite differences) and compares the results to the exact Fourier series
solution. Built in Java with JavaFX to deepen my understanding of PDEs and
numerical methods.

**Status:** in development. Done when a user can change parameters with
sliders and see the numerical solution, exact solution, and error update live.

## Running

Requires JDK 25.

    .\gradlew run

## Roadmap

- [ ] v0.1: explicit finite-difference heat equation solver with a plot
- [ ] v0.2: Fourier series solution and error plot
- [ ] Stretch: wave equation, sliders, multiple initial conditions