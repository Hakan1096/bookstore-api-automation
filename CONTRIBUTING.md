# Contributing to Bookstore API Automation Framework

Thank you for your interest in contributing to the Bookstore API Automation Framework! This document provides guidelines and information for contributors.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Guidelines](#development-guidelines)
- [Testing Guidelines](#testing-guidelines)
- [Pull Request Process](#pull-request-process)
- [Issue Reporting](#issue-reporting)

## Code of Conduct

We are committed to providing a welcoming and inclusive environment for all contributors. Please be respectful and constructive in all interactions.

## Getting Started

1. **Fork the repository** and clone your fork locally
2. **Set up your development environment** following the README instructions
3. **Create a feature branch** from the main branch
4. **Make your changes** following our development guidelines
5. **Test your changes** thoroughly
6. **Submit a pull request** with a clear description

## Development Guidelines

### Code Style

- Follow **Google Java Style Guide**
- Use **meaningful variable and method names**
- Add **JavaDoc comments** for public methods and classes
- Keep methods **focused and small** (prefer methods under 20 lines)
- Use **builder pattern** for complex object creation

### Project Structure

- **Models**: Place in `com.bookstore.models` package
- **API Clients**: Place in `com.bookstore.clients` package
- **Test Classes**: Organize by feature in `com.bookstore.tests` package
- **Utilities**: Place in `com.bookstore.utils` package

### Naming Conventions

- **Test Classes**: End with `Tests` (e.g., `BookApiHappyPathTests`)
- **Test Methods**: Start with `test` and describe the scenario (e.g., `testCreateBookWithValidData`)
- **API Clients**: End with `ApiClient` (e.g., `BookApiClient`)
- **Model Classes**: Use clear, descriptive names (e.g., `Book`, `Author`)

## Testing Guidelines

### Test Categories

1. **Happy Path Tests**: Test normal, expected behavior
2. **Edge Case Tests**: Test boundary conditions and special scenarios
3. **Negative Tests**: Test error handling and validation
4. **Integration Tests**: Test interactions between components

### Test Requirements

- **Each test should be independent** and able to run in isolation
- **Use descriptive test names** that explain what is being tested
- **Add proper Allure annotations** for reporting
- **Include appropriate assertions** with clear failure messages
- **Clean up test data** when necessary

### Test Data

- **Use TestDataGenerator** for creating test objects
- **Avoid hardcoded values** where possible
- **Use realistic data** that represents actual use cases
- **Consider data variations** (special characters, edge cases, etc.)

## Pull Request Process

1. **Ensure all tests pass** locally before submitting
2. **Update documentation** if your changes affect it
3. **Add tests** for new functionality
4. **Write clear commit messages** describing your changes
5. **Fill out the PR template** completely
6. **Request review** from at least one maintainer

### PR Title Format

Use conventional commit format:
- `feat: add new author search functionality`
- `fix: resolve timeout issue in book creation`
- `docs: update API client documentation`
- `test: add edge cases for book deletion`

### PR Description

Include:
- **What changes were made**
- **Why the changes were necessary**
- **How to test the changes**
- **Any breaking changes**
- **Screenshots** (if applicable)

## Issue Reporting

### Bug Reports

Include:
- **Steps to reproduce** the issue
- **Expected behavior**
- **Actual behavior**
- **Environment details** (OS, Java version, etc.)
- **Logs or error messages**

### Feature Requests

Include:
- **Description** of the proposed feature
- **Use case** and justification
- **Acceptance criteria**
- **Potential implementation** approach (optional)

## Development Setup

### Prerequisites

- Java 11+
- Maven 3.6+
- Git
- IDE (IntelliJ IDEA recommended)

### Setup Steps

```bash
# Clone your fork
git clone https://github.com/YOUR_USERNAME/bookstore-api-automation.git
cd bookstore-api-automation

# Install dependencies
mvn clean install

# Run tests to verify setup
mvn test
```

### Environment Variables

Set up the following for local development:
```bash
export API_BASE_URL=https://fakerestapi.azurewebsites.net
export PARALLEL_THREADS=3
export LOG_LEVEL=DEBUG
```

## Code Review Guidelines

### For Reviewers

- **Be constructive** and helpful in feedback
- **Check for code quality** and adherence to guidelines
- **Verify test coverage** for new functionality
- **Test the changes** when possible
- **Approve promptly** when criteria are met

### For Contributors

- **Address all feedback** or explain why you disagree
- **Update tests** based on review comments
- **Ask questions** if feedback is unclear
- **Be responsive** to review requests

## Release Process

1. **Version numbering** follows semantic versioning (MAJOR.MINOR.PATCH)
2. **Release notes** are generated automatically from PRs
3. **Docker images** are built and pushed for each release
4. **Documentation** is updated for significant changes

## Getting Help

- **Check existing issues** before creating new ones
- **Join our discussions** for questions and ideas
- **Review documentation** for common solutions
- **Contact maintainers** for urgent issues

## Recognition

Contributors will be recognized in:
- **Release notes** for significant contributions
- **README.md** contributors section
- **GitHub contributors** page

Thank you for contributing to making this framework better!