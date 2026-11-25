# Calculus Studio 3D — Maan Softwares

**Owner:** Abdul Rehman  
**Brand:** Maan Softwares  
**Copyright:** © 2026 Abdul Rehman. All rights reserved.

## Features
- Derivative and integration modes selected from the top ☰ menu.
- Polynomial differentiation and integration.
- Standard functions: `sin(x)`, `cos(x)`, `tan(x)`, `cot(x)`, `cosec(x)`, `sec(x)`, `sec²(x)`, `eˣ`, `1/x`, and `1/√x`.
- Definite integration for supported standard functions and polynomial inputs.
- 3D-style perspective graph visual, elevated cards, gradient branding, and launch welcome panel.
- Copyable calculation output.

## Build on GitHub
1. Create a new GitHub repository.
2. Extract this ZIP and upload the **contents** of the `IntegrationCalculatorByMaan` folder to the repository root.
3. Push to the `main` branch (or open **Actions → Build Integration Calculator APK → Run workflow**).
4. Open the completed workflow run and download the `IntegrationCalculator-debug-apk` artifact.

The included GitHub Actions workflow uses JDK 17 and Gradle 8.11.1.

## Notes
- Trigonometric input and definite-integral limits use radians.
- Standard-function antiderivatives are symbolic; definite values are evaluated numerically from the antiderivative.
- Derivative mode currently displays standard symbolic derivative rules.
- Review third-party dependency licenses before publishing. See `COPYRIGHT.txt`.
