# Contributing to Restaurant POS

Thank you for your interest in contributing to the Restaurant POS project! This document provides guidelines and information for contributors.

## Code of Conduct

By participating in this project, you agree to abide by our code of conduct:
- Be respectful and inclusive
- Focus on constructive feedback
- Help maintain a welcoming environment for all contributors
- Report any unacceptable behavior to the project maintainers

## Getting Started

### Prerequisites

Before contributing, ensure you have:
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17 or later
- Git installed and configured
- Basic knowledge of Kotlin and Android development

### Setting Up Development Environment

1. **Fork the repository** on GitHub
2. **Clone your fork** locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/pos.git
   cd pos
   ```
3. **Add upstream remote**:
   ```bash
   git remote add upstream https://github.com/moamenorg3-hue/pos.git
   ```
4. **Open in Android Studio** and wait for Gradle sync

## Development Workflow

### Branch Naming Convention

Use descriptive branch names with prefixes:
- `feature/` - New features (e.g., `feature/payment-integration`)
- `bugfix/` - Bug fixes (e.g., `bugfix/navigation-crash`)
- `hotfix/` - Critical fixes (e.g., `hotfix/security-patch`)
- `docs/` - Documentation updates (e.g., `docs/api-documentation`)
- `refactor/` - Code refactoring (e.g., `refactor/database-layer`)

### Making Changes

1. **Create a feature branch**:
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Keep your branch updated**:
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

3. **Make your changes** following the coding standards below

4. **Test your changes**:
   ```bash
   ./gradlew test
   ./gradlew connectedAndroidTest  # If applicable
   ```

5. **Commit your changes** with clear messages (see commit guidelines below)

6. **Push to your fork**:
   ```bash
   git push origin feature/your-feature-name
   ```

## Commit Guidelines

### Commit Message Format

Use the conventional commit format:
```
<type>(<scope>): <description>

[optional body]

[optional footer]
```

### Types

- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks
- `ci`: CI/CD changes

### Examples

```bash
feat(pos): add product selection functionality
fix(database): resolve Room migration issue
docs(readme): update setup instructions
test(ui): add navigation tests
refactor(di): simplify dependency injection modules
```

### Commit Best Practices

- Keep commits atomic (one logical change per commit)
- Write clear, descriptive commit messages
- Use present tense ("add feature" not "added feature")
- Reference issues when applicable (`fixes #123`)
- Keep the first line under 50 characters
- Add detailed explanation in the body if needed

## Pull Request Process

### Before Submitting

- [ ] Code follows the project's coding standards
- [ ] All tests pass locally
- [ ] Documentation is updated if needed
- [ ] Commit messages follow the guidelines
- [ ] Branch is up to date with main

### PR Title and Description

**Title Format**: Use the same format as commit messages
```
feat(pos): add product selection functionality
```

**Description Template**:
```markdown
## Description
Brief description of changes made.

## Type of Change
- [ ] Bug fix (non-breaking change which fixes an issue)
- [ ] New feature (non-breaking change which adds functionality)
- [ ] Breaking change (fix or feature that would cause existing functionality to not work as expected)
- [ ] Documentation update

## Testing
- [ ] Unit tests pass
- [ ] Integration tests pass (if applicable)
- [ ] Manual testing completed

## Screenshots (if applicable)
Add screenshots or GIFs showing the changes.

## Checklist
- [ ] My code follows the style guidelines of this project
- [ ] I have performed a self-review of my own code
- [ ] I have commented my code, particularly in hard-to-understand areas
- [ ] I have made corresponding changes to the documentation
- [ ] My changes generate no new warnings
- [ ] I have added tests that prove my fix is effective or that my feature works
- [ ] New and existing unit tests pass locally with my changes
```

### Review Process

1. **Automated Checks**: CI/CD pipeline must pass
2. **Code Review**: At least one maintainer review required
3. **Testing**: Manual testing may be requested
4. **Approval**: PR approved by maintainer
5. **Merge**: Squash and merge to main branch

## Coding Standards

### Kotlin Style Guide

Follow the [official Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html):

- Use 4 spaces for indentation
- Use camelCase for functions and variables
- Use PascalCase for classes and interfaces
- Use UPPER_SNAKE_CASE for constants
- Maximum line length: 120 characters

### Android Specific Guidelines

- Follow [Android's code style guidelines](https://source.android.com/docs/setup/contribute/code-style)
- Use meaningful resource names (e.g., `btn_submit`, `tv_title`)
- Organize resources by type and feature
- Use vector drawables when possible

### Architecture Guidelines

- Follow MVVM pattern with Clean Architecture
- Use Hilt for dependency injection
- Keep ViewModels lifecycle-aware
- Separate business logic from UI logic
- Use Repository pattern for data access

### Code Quality

- Write self-documenting code with clear variable names
- Add KDoc comments for public APIs
- Keep functions small and focused (max 20-30 lines)
- Avoid deep nesting (max 3-4 levels)
- Handle edge cases and error states
- Use nullable types appropriately

## Testing Guidelines

### Unit Tests

- Write tests for business logic
- Use descriptive test names
- Follow AAA pattern (Arrange, Act, Assert)
- Mock external dependencies
- Aim for high code coverage

### UI Tests

- Test critical user flows
- Use Compose testing utilities
- Test accessibility features
- Verify proper navigation

### Test Naming Convention

```kotlin
@Test
fun `should return success when valid data is provided`() {
    // Test implementation
}
```

## Documentation

### Code Documentation

- Add KDoc comments for public classes and functions
- Document complex algorithms or business logic
- Include usage examples for utility functions
- Keep documentation up to date with code changes

### README Updates

When adding new features:
- Update feature list
- Add setup instructions if needed
- Include usage examples
- Update screenshots if UI changes

## Localization

### Adding New Strings

1. Add English strings to `res/values/strings.xml`
2. Add Arabic translations to `res/values-ar/strings.xml`
3. Use descriptive string keys
4. Group related strings together
5. Test both languages

### String Guidelines

- Use string resources for all user-facing text
- Avoid hardcoded strings in code
- Use plurals for count-dependent strings
- Consider RTL layout for Arabic text

## Issue Reporting

### Bug Reports

Include:
- Android version and device model
- App version
- Steps to reproduce
- Expected vs actual behavior
- Screenshots or logs if applicable

### Feature Requests

Include:
- Clear description of the feature
- Use case and benefits
- Mockups or examples if applicable
- Implementation suggestions (optional)

## Getting Help

- **Documentation**: Check README and code comments
- **Issues**: Search existing issues before creating new ones
- **Discussions**: Use GitHub Discussions for questions
- **Code Review**: Ask for feedback during PR review

## Recognition

Contributors will be recognized in:
- GitHub contributors list
- Release notes for significant contributions
- Special mentions for outstanding contributions

Thank you for contributing to Restaurant POS! 🚀