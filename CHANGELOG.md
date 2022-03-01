# Changelog

All notable changes to this project will be documented in this file.

| Title            | Description                 |
|------------------|-----------------------------|
| Breaking Changes | big change for application  |
| Features         | new features                |
| Improvements     | improve existing features   |
| Bugfixes         | fixed for any bug fixes     |
| Deprecations     | soon-to-be removed features |
| Dependencies     | required to run the program |

## [Unreleased]

## [2022.0.1] - 2022-03-02
### Features
- add function that patch category

### Improvements
- improved to bring category children to the desired depth
- change category post method (status code 200 -> 201)
- validation fails if there are more than 20 children when creating a category 

### Bugfixes
- changed to occur an exception when validation fails

## [2022.0.0] - 2022-02-28
### Features
- add function that list, get, create, update, delete category